package org.game.knight.version.packer.world;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.chw.swf.writer.SwfBitmap;
import org.chw.swf.writer.SwfWriter;
import org.chw.util.FileUtil;
import org.chw.util.MD5Util;
import org.chw.util.ZlibUtil;
import org.game.knight.version.packer.world.attire.Attire;
import org.game.knight.version.packer.world.attire.AttireAction;
import org.game.knight.version.packer.world.attire.AttireAnim;
import org.game.knight.version.packer.world.attire.AttireFile;

public class UIAvatarExport
{
	private static final String UI_AVATAR_FRAME_PACK = "knight.ui.avatar";

	private WorldExporter world;

	private String cfgFileKey;

	/**
	 * 构造函数
	 * 
	 * @param world
	 */
	public UIAvatarExport(WorldExporter world)
	{
		openHistoryFile(new File(world.getDestDir().getPath() + "/.ver/uiAvatar"));

		this.world = world;
	}

	/**
	 * 导出UI装扮表
	 * 
	 * @throws IOException
	 */
	public String exportUIAttires(Hashtable<String, AttireFile> attires, WorldAttires attireManager, boolean zip) throws IOException
	{
		ArrayList<Attire> attireList = filterAttires(attires);

		StringBuilder json_role = new StringBuilder();
		StringBuilder json_role_light = new StringBuilder();
		StringBuilder json_equip = new StringBuilder();
		StringBuilder json_partner = new StringBuilder();

		StringBuilder json_attires = new StringBuilder();
		for (Attire attire : attireList)
		{
			StringBuilder json_actions = new StringBuilder();

			for (AttireAction action : attire.getActions())
			{
				if (action.getID() != 0 && action.getID() != 1)
				{
					continue;
				}

				StringBuilder json_anims = new StringBuilder();

				for (AttireAnim anim : action.getAnims())
				{
					String imgSHA = world.getChecksumTable().getChecksumID(anim.getImg().getInnerpath());

					int rowCount = anim.getRow();
					int colCount = anim.getCol();

					ArrayList<Region> regions = new ArrayList<Region>();
					ArrayList<String> regionIDs = new ArrayList<String>();
					ArrayList<Integer> regionTimes = new ArrayList<Integer>();

					int regionCount = rowCount * colCount;
					for (int i = 0; i < regionCount; i++)
					{
						int delay = anim.getTimes()[i];
						if (delay > 0)
						{
							Region region = attireManager.getTextureRegion(anim.getBagID(), imgSHA, anim.getRow(), anim.getCol(), i);
							if (region != null)
							{
								regions.add(region);
								regionIDs.add("anim_" + imgSHA + "_" + rowCount + "_" + colCount + "_" + "frame" + i);
								regionTimes.add(delay);
							}
						}
					}

					String swfFileKey = "anim_" + imgSHA + "_" + rowCount + "_" + colCount;
					String swfFilePath = "";
					long swfFileSize = 0;

					boolean exported = world.hasExportedFile(swfFileKey);

					ArrayList<byte[]> bitmaps = new ArrayList<byte[]>();
					ArrayList<String> bitmapIDs = new ArrayList<String>();

					for (int i = 0; i < regionIDs.size(); i++)
					{
						Region region = regions.get(i);
						String regionID = regionIDs.get(i);

						// 导出PNG
						if (!exported)
						{
							BufferedImage img = ImageIO.read(anim.getImg().getFile());

							BufferedImage texture = new BufferedImage(region.getClipW(), region.getClipH(), BufferedImage.TYPE_INT_ARGB);
							Graphics2D graphics = (Graphics2D) texture.getGraphics();
							graphics.drawImage(img, 0, 0, region.getClipW(), region.getClipH(), region.getX() + region.getClipX(), region.getY() + region.getClipY(), region.getX() + region.getClipX() + region.getClipW(), region.getY() + region.getClipY() + region.getClipH(), null);
							graphics.dispose();

							ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
							ImageIO.write(texture, "png", outputBytes);

							bitmaps.add(outputBytes.toByteArray());
						}

						// 确定类名
						String value = getValue(regionID);
						if (value == null)
						{
							value = "$" + getNextClassID();
							putValue(regionID, value);
						}
						bitmapIDs.add(value);
					}

					if (!exported)
					{
						SwfWriter swf = new SwfWriter();
						for (int i = 0; i < bitmaps.size(); i++)
						{
							swf.addBitmap(new SwfBitmap(bitmaps.get(i), UI_AVATAR_FRAME_PACK, bitmapIDs.get(i), true));
						}

						world.exportFile(swfFileKey, swf.toBytes(true), "swf");
					}

					swfFilePath = world.getExportedFileUrl(swfFileKey);
					swfFileSize = world.getExportedFileSize(swfFileKey);

					StringBuilder json_frames = new StringBuilder();
					for (int i = 0; i < regionIDs.size(); i++)
					{
						Region region = regions.get(i);
						int offSetX = region.getClipX() + region.getClipW() / 2 - region.getW() / 2;
						int offsetY = region.getClipY() + region.getClipH() - region.getH();

						// 帧信息
						if (json_frames.length() > 0)
						{
							json_frames.append(",");
						}
						json_frames.append(String.format("{\"x\":%s,\"y\":%s,\"delay\":%s,\"classID\":\"%s\"}", offSetX, offsetY, regionTimes.get(i), bitmapIDs.get(i)));
					}

					// 动画信息
					if (json_anims.length() > 0)
					{
						json_anims.append(",");
					}
					json_anims.append(String.format("{\"x\":%s,\"y\":%s,\"scaleX\":%s,\"scaleY\":%s,\"flip\":%s,\"groupID\":%s,\"layerID\":%s,\"fileURL\":\"%s\",\"fileSize\":%s,\"frames\":[%s]}", anim.getX(), anim.getY(), anim.getScaleX(), anim.getScaleY(), anim.getFlip(), anim.getGroupID(), anim.getLayerID(), swfFilePath, swfFileSize, json_frames.toString()));
				}

				// 动作信息
				if (json_actions.length() > 0)
				{
					json_actions.append(",");
				}
				json_actions.append(String.format("\"%s\":{\"nameX\":%s,\"nameY\":%s,\"anims\":[%s]}", action.getID(), action.getNameX(), action.getNameY(), json_anims.toString()));
			}

			// 装扮信息
			if (json_attires.length() > 0)
			{
				json_attires.append(",");
			}
			json_attires.append(String.format("\"%s\":{\"width\":%s,\"height\":%s,\"actions\":{%s}}", attire.getRefKey(), attire.getHitRect().getWidth(), attire.getHitRect().getHeight(), json_actions.toString()));

			// 装扮分类
			Integer[] params = attire.getParams();
			if (params.length > 0)
			{
				if (params[0] == 1 && params.length >= 3)
				{
					// 装扮
					if (json_role.length() > 0)
					{
						json_role.append(",");
					}
					json_role.append(String.format("\"%s_%s\":\"%s\"", params[1], params[2], attire.getRefKey()));
				}
				else if (params[0] == 2 && params.length >= 4)
				{
					// 装备
					int fromID = params[1];
					int destID = params[2];
					int sectID = params[3];
					for (int i = fromID; i <= destID; i++)
					{
						if (json_equip.length() > 0)
						{
							json_equip.append(",");
						}
						json_equip.append(String.format("\"%s_%s\":\"%s\"", i, sectID, attire.getRefKey()));
					}
				}
				else if (params[0] == 6 && params.length >= 2)
				{
					// 刀光
					if (json_role_light.length() > 0)
					{
						json_role_light.append(",");
					}
					json_role_light.append(String.format("\"%s\":\"%s\"", params[1], attire.getRefKey()));
				}
				else if (params[0] == 7 && params.length >= 2)
				{
					// 伙伴
					if (json_partner.length() > 0)
					{
						json_partner.append(",");
					}
					json_partner.append(String.format("\"%s\":\"%s\"", params[1], attire.getRefKey()));
				}
			}
		}

		String content = String.format("{\"classPackageName\":\"%s\",\"roleMap\":{%s},\"roleLightMap\":{%s},\"equipMap\":{%s},\"partnerMap\":{%s},\"attires\":{%s}}", UI_AVATAR_FRAME_PACK, json_role.toString(), json_role_light.toString(), json_equip.toString(), json_partner.toString(), json_attires.toString());

		byte[] contentBytes = content.getBytes("UTF-8");
		if (zip)
		{
			contentBytes = ZlibUtil.compress(contentBytes);
		}
		cfgFileKey = (zip ? "z" : "") + MD5Util.md5Bytes(contentBytes);

		world.exportFile(cfgFileKey, contentBytes, "cfg");

		saveHistoryFile();

		return cfgFileKey;
	}

	/**
	 * 过滤需要输出的装扮
	 * 
	 * @param attires
	 */
	private static ArrayList<Attire> filterAttires(Hashtable<String, AttireFile> attires)
	{
		ArrayList<Attire> result = new ArrayList<Attire>();

		for (AttireFile attireFile : attires.values())
		{
			for (Attire attire : attireFile.getAllAttires())
			{
				if (attire.isAnimAttire() || attire.getKey().startsWith("0_"))
				{
					continue;
				}

				Integer[] params = attire.getParams();
				if (params.length == 0)
				{
					continue;
				}

				boolean canOutput = false;

				if (params[0] == 1 && params.length >= 3)
				{
					// 装扮
					canOutput = true;
				}
				else if (params[0] == 2 && params.length >= 4)
				{
					// 装备
					canOutput = true;
				}
				else if (params[0] == 3)
				{
					// 效果
					canOutput = true;
				}
				else if (params[0] == 6 && params.length >= 2)
				{
					// 刀光
					canOutput = true;
				}
				else if (params[0] == 7 && params.length >= 2)
				{
					// 伙伴
					canOutput = true;
				}

				if (!canOutput)
				{
					continue;
				}

				// 只有第一帧第二帧有内容
				if ((attire.getAction(0) != null && attire.getAction(0).getAnims().size() > 0) || (attire.getAction(1) != null && attire.getAction(1).getAnims().size() > 0))
				{
					result.add(attire);
				}
			}
		}

		Collections.sort(result, new Comparator<Attire>()
		{
			@Override
			public int compare(Attire o1, Attire o2)
			{
				return o1.getRefKey().compareTo(o2.getRefKey());
			}
		});

		return result;
	}

	// ---------------------------------------------------------------------------------------------------------
	//
	// 版本库处理
	//
	// ---------------------------------------------------------------------------------------------------------

	private int nextClassID = 0;
	private Hashtable<String, String> old_table;
	private Hashtable<String, String> new_table;

	private File historyFile;

	/**
	 * 获取下一个类型的ID
	 * 
	 * @return
	 */
	private int getNextClassID()
	{
		int result = nextClassID;
		nextClassID++;
		return result;
	}

	/**
	 * 获取值
	 * 
	 * @param key
	 * @return
	 */
	private String getValue(String key)
	{
		if (old_table.containsKey(key))
		{
			new_table.put(key, old_table.get(key));
			old_table.remove(key);
		}
		return new_table.get(key);
	}

	/**
	 * 写入值
	 * 
	 * @param key
	 * @param value
	 */
	private void putValue(String key, String value)
	{
		new_table.put(key, value);
	}

	/**
	 * 打开历史文件
	 */
	private void openHistoryFile(File file)
	{
		historyFile = file;

		old_table = new Hashtable<String, String>();
		new_table = new Hashtable<String, String>();

		if (!historyFile.exists())
		{
			return;
		}

		BufferedReader input = null;
		try
		{
			input = new BufferedReader(new InputStreamReader(new FileInputStream(historyFile), "UTF-8"));
			while (true)
			{
				String line = input.readLine();
				if (line == null)
				{
					break;
				}

				line.trim();

				if (line.isEmpty())
				{
					continue;
				}

				String[] parts = line.split("=");
				String key = parts[0].trim();
				String val = parts[1].trim();

				if (key.isEmpty() || val.isEmpty())
				{
					continue;
				}

				if (key.charAt(0) == '$')
				{
					String propName = key.substring(1);
					String propValue = val;

					if (propName.equals("nextClassID"))
					{
						nextClassID = Integer.parseInt(propValue);
					}
				}
				else
				{
					old_table.put(key, val);
				}
			}
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (input != null)
			{
				try
				{
					input.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 保存历史文件
	 */
	private void saveHistoryFile()
	{
		StringBuilder output = new StringBuilder();

		output.append("$nextClassID = " + nextClassID + "\n");

		String[] keys = new_table.keySet().toArray(new String[new_table.size()]);
		Arrays.sort(keys);

		for (String key : keys)
		{
			output.append(key + " = " + new_table.get(key) + "\n");
		}

		// 保存到文件
		try
		{
			byte[] bytes = output.toString().getBytes("UTF-8");
			FileUtil.writeFile(historyFile, bytes);
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}
}
