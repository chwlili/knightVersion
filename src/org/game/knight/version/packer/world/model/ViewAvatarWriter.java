package org.game.knight.version.packer.world.model;

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
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.world.Region;
import org.game.knight.version.packer.world.WorldAttires;
import org.game.knight.version.packer.world.task.RootTask;

public class ViewAvatarWriter
{
	private static final String UI_AVATAR_FRAME_PACK = "knight.ui.avatar";

	private RootTask root;

	private String cfgFileKey;

	/**
	 * 构造函数
	 * 
	 * @param world
	 */
	public ViewAvatarWriter(RootTask root)
	{
		this.root = root;
	}

	/**
	 * 开始
	 */
	public void start()
	{
		openVer();

	}

	/**
	 * 导出UI装扮表
	 * 
	 * @throws IOException
	 */
	public String exportUIAttires(Attire[] attires) throws IOException
	{

		ArrayList<AttireAnim> allAnims = new ArrayList<AttireAnim>();
		ArrayList<AttireAnim> newAnims = new ArrayList<AttireAnim>();

		Hashtable<AttireAnim, ArrayList<Region>> anim_regions = new Hashtable<AttireAnim, ArrayList<Region>>();
		Hashtable<AttireAnim, ArrayList<String>> anim_regionIDs = new Hashtable<AttireAnim, ArrayList<String>>();
		Hashtable<AttireAnim, ArrayList<Integer>> anim_regionTimes = new Hashtable<AttireAnim, ArrayList<Integer>>();
		Hashtable<AttireAnim, ArrayList<String>> anim_regionTypes = new Hashtable<AttireAnim, ArrayList<String>>();
		Hashtable<AttireAnim, ArrayList<byte[]>> anim_regionBytes = new Hashtable<AttireAnim, ArrayList<byte[]>>();

		Hashtable<AttireAnim, String> anim_fileKey = new Hashtable<AttireAnim, String>();
		Hashtable<AttireAnim, String> anim_filePath = new Hashtable<AttireAnim, String>();
		Hashtable<AttireAnim, Integer> anim_fileSize = new Hashtable<AttireAnim, Integer>();

		GamePacker.progress("过滤装扮数据");
		Attire[] attireList = filterAttires(attires);

		GamePacker.progress("分析动画信息");
		for (Attire attire : attireList)
		{
			for (AttireAction action : attire.actions)
			{
				if (action.id != 0 && action.id != 1 && action.id != 51)
				{
					continue;
				}

				for (AttireAnim anim : action.anims)
				{
					int rowCount = anim.row;
					int colCount = anim.col;

					ArrayList<ImageFrame> regions = new ArrayList<ImageFrame>();
					ArrayList<String> regionIDs = new ArrayList<String>();
					ArrayList<Integer> regionTimes = new ArrayList<Integer>();
					ArrayList<String> regionTypes = new ArrayList<String>();

					StringBuilder animFileKey = new StringBuilder();
					String animFilePath = "";
					int animFileSize = 0;

					int regionCount = rowCount * colCount;
					for (int i = 0; i < regionCount; i++)
					{
						int delay = anim.times[i];
						if (delay > 0)
						{
							ImageFrame frame = root.getImageFrameTable().get(anim.img.gid, anim.row, anim.col, i);
							if (frame != null)
							{
								String key = anim.img.gid + "_" + rowCount + "_" + colCount + "_" + i;
								String type = getValue(key);
								if (type == null)
								{
									type = "$" + getNextClassID();
								}

								regions.add(frame);
								regionIDs.add(key);
								regionTimes.add(delay);
								regionTypes.add(type);

								if (animFileKey.length() > 0)
								{
									animFileKey.append(",");
								}
								animFileKey.append(key + "_" + type);

								putValue(key, type);
							}
						}
					}

					String fileID = animFileKey.toString();
					if (world.hasExportedFile(fileID))
					{
						animFilePath = world.getExportedFileUrl(fileID);
						animFileSize = (int) world.getExportedFileSize(fileID);
					}
					else
					{
						newAnims.add(anim);
					}

					allAnims.add(anim);
					anim_regions.put(anim, regions);
					anim_regionIDs.put(anim, regionIDs);
					anim_regionTimes.put(anim, regionTimes);
					anim_regionTypes.put(anim, regionTypes);
					anim_fileKey.put(anim, animFileKey.toString());
					anim_filePath.put(anim, animFilePath);
					anim_fileSize.put(anim, animFileSize);
				}
			}
		}

		GamePacker.progress("输出动画文件");
		for (int i = 0; i < newAnims.size(); i++)
		{
			AttireAnim anim = newAnims.get(i);
			ArrayList<Region> regions = anim_regions.get(anim);
			ArrayList<String> regionTypes = anim_regionTypes.get(anim);
			ArrayList<byte[]> regionBytes = new ArrayList<byte[]>();

			String animFileKey = anim_fileKey.get(anim);
			String animFilePath = anim_filePath.get(anim);
			int animFileSize = anim_fileSize.get(anim);

			for (int j = 0; j < regions.size(); j++)
			{
				GamePacker.progress(String.format("输出动画文件(%s/%s)：裁切图像(%s/%s)", i + 1, newAnims.size(), j + 1, regions.size()));

				// 导出PNG
				Region region = regions.get(j);
				BufferedImage img = ImageIO.read(anim.img.file);

				BufferedImage texture = new BufferedImage(region.getClipW(), region.getClipH(), BufferedImage.TYPE_INT_ARGB);
				Graphics2D graphics = (Graphics2D) texture.getGraphics();
				graphics.drawImage(img, 0, 0, region.getClipW(), region.getClipH(), region.getX() + region.getClipX(), region.getY() + region.getClipY(), region.getX() + region.getClipX() + region.getClipW(), region.getY() + region.getClipY() + region.getClipH(), null);
				graphics.dispose();

				ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
				ImageIO.write(texture, "png", outputBytes);

				regionBytes.add(outputBytes.toByteArray());
			}

			// 输出SWF
			GamePacker.progress(String.format("输出动画文件(%s/%s)：输出swf文件", i + 1, newAnims.size()));
			SwfWriter swf = new SwfWriter();
			for (int j = 0; j < regionTypes.size(); j++)
			{
				swf.addBitmap(new SwfBitmap(regionBytes.get(j), UI_AVATAR_FRAME_PACK, regionTypes.get(j), true));
			}
			world.exportFile(animFileKey, MD5Util.addSuffix(swf.toBytes(true)), "swf");

			// 记录信息
			animFilePath = world.getExportedFileUrl(animFileKey);
			animFileSize = (int) world.getExportedFileSize(animFileKey);

			anim_regionBytes.put(anim, regionBytes);
			anim_filePath.put(anim, animFilePath);
			anim_fileSize.put(anim, animFileSize);
		}

		GamePacker.progress("输出动画配置");
		StringBuilder json_role = new StringBuilder();
		StringBuilder json_role_light = new StringBuilder();
		StringBuilder json_equip = new StringBuilder();
		StringBuilder json_partner = new StringBuilder();
		StringBuilder json_horse = new StringBuilder();

		StringBuilder json_attires = new StringBuilder();
		for (Attire attire : attireList)
		{
			StringBuilder json_actions = new StringBuilder();
			for (AttireAction action : attire.getActions())
			{
				if (action.id != 0 && action.id != 1 && action.id != 51)
				{
					continue;
				}

				StringBuilder json_anims = new StringBuilder();
				for (AttireAnim anim : action.animList)
				{
					ArrayList<Region> regions = anim_regions.get(anim);
					ArrayList<String> regionIDs = anim_regionIDs.get(anim);
					ArrayList<Integer> regionTimes = anim_regionTimes.get(anim);
					ArrayList<String> regionTypes = anim_regionTypes.get(anim);

					String animFilePath = anim_filePath.get(anim);
					int animFileSize = anim_fileSize.get(anim);

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
						json_frames.append(String.format("{\"x\":%s,\"y\":%s,\"delay\":%s,\"classID\":\"%s\"}", offSetX, offsetY, regionTimes.get(i), regionTypes.get(i)));
					}

					// 动画信息
					if (json_anims.length() > 0)
					{
						json_anims.append(",");
					}
					json_anims.append(String.format("{\"x\":%s,\"y\":%s,\"scaleX\":%s,\"scaleY\":%s,\"flip\":%s,\"groupID\":%s,\"layerID\":%s,\"fileURL\":\"%s\",\"fileSize\":%s,\"frames\":[%s]}", anim.x, anim.y, anim.scaleX, anim.scaleY, anim.flip, anim.groupID, anim.layerID, animFilePath, animFileSize, json_frames.toString()));
				}

				// 动作信息
				if (json_actions.length() > 0)
				{
					json_actions.append(",");
				}
				json_actions.append(String.format("\"%s\":{\"nameX\":%s,\"nameY\":%s,\"anims\":[%s]}", action.id, action.nameX, action.nameY, json_anims.toString()));
			}

			// 装扮信息
			if (json_attires.length() > 0)
			{
				json_attires.append(",");
			}
			json_attires.append(String.format("\"%s\":{\"nameX\":%s,\"nameY\":%s,\"width\":%s,\"height\":%s,\"actions\":{%s}}", attire.getRefKey(), attire.getNameX(), attire.getNameY(), attire.getHitRect().width, attire.getHitRect().height, json_actions.toString()));

			// 装扮分类
			String[] params = attire.getParams();
			if (params.length > 0)
			{
				if (params[0].equals("1") && params.length >= 3)
				{
					// 装扮
					if (json_role.length() > 0)
					{
						json_role.append(",");
					}
					json_role.append(String.format("\"%s_%s\":\"%s\"", params[1], params[2], attire.getRefKey()));
				}
				else if (params[0].equals("2") && params.length >= 4)
				{
					// 装备
					int fromID = Integer.parseInt(params[1]);
					int destID = Integer.parseInt(params[2]);
					int sectID = Integer.parseInt(params[3]);
					for (int i = fromID; i <= destID; i++)
					{
						if (json_equip.length() > 0)
						{
							json_equip.append(",");
						}
						json_equip.append(String.format("\"%s_%s\":\"%s\"", i, sectID, attire.getRefKey()));
					}
				}
				else if (params[0].equals("6") && params.length >= 2)
				{
					// 刀光
					if (json_role_light.length() > 0)
					{
						json_role_light.append(",");
					}
					json_role_light.append(String.format("\"%s\":\"%s\"", params[1], attire.getRefKey()));
				}
				else if (params[0].equals("7") && params.length >= 2)
				{
					// 伙伴
					if (json_partner.length() > 0)
					{
						json_partner.append(",");
					}
					json_partner.append(String.format("\"%s\":\"%s\"", params[1], attire.getRefKey()));
				}
				else if (params[0].equals("8") && params.length >= 2)
				{
					// 坐骑
					if (json_horse.length() > 0)
					{
						json_horse.append(",");
					}
					json_horse.append(String.format("\"%s\":\"%s\"", params[1], attire.getRefKey()));
				}
			}
		}

		String content = String.format("{\"classPackageName\":\"%s\",\"roleMap\":{%s},\"roleLightMap\":{%s},\"equipMap\":{%s},\"partnerMap\":{%s},\"horseMap\":{%s},\"attires\":{%s}}", UI_AVATAR_FRAME_PACK, json_role.toString(), json_role_light.toString(), json_equip.toString(), json_partner.toString(), json_horse.toString(), json_attires.toString());

		byte[] contentBytes = content.getBytes("UTF-8");
		if (zip)
		{
			contentBytes = ZlibUtil.compress(contentBytes);
		}
		cfgFileKey = (zip ? "zlib_md5" : "md5") + MD5Util.md5Bytes(contentBytes);

		world.exportFile(cfgFileKey, MD5Util.addSuffix(contentBytes), "cfg");

		saveHistoryFile();

		return cfgFileKey;
	}

	/**
	 * 过滤需要输出的装扮
	 * 
	 * @param attires
	 */
	private static Attire[] filterAttires(Attire[] attires)
	{
		ArrayList<Attire> result = new ArrayList<Attire>();

		for (Attire attire : attires)
		{
			if (attire.isAnimAttire() || attire.nativeName.startsWith("0_"))
			{
				continue;
			}

			String[] params = attire.typeParams;
			if (params.length == 0)
			{
				continue;
			}

			boolean canOutput = false;

			if (params[0].equals("1") && params.length >= 3)
			{
				// 装扮
				canOutput = true;
			}
			else if (params[0].equals("2") && params.length >= 4)
			{
				// 装备
				canOutput = true;
			}
			else if (params[0].equals("3"))
			{
				// 效果
				canOutput = true;
			}
			else if (params[0].equals("6") && params.length >= 2)
			{
				// 刀光
				canOutput = true;
			}
			else if (params[0].equals("7") && params.length >= 2)
			{
				// 伙伴
				canOutput = true;
			}
			else if (params[0].equals("8") && params.length >= 2)
			{
				// 坐骑
				canOutput = true;
			}

			if (!canOutput)
			{
				continue;
			}

			// 只有第一帧第二帧有内容
			if ((attire.getAction(0) != null && attire.getAction(0).anims.length > 0) || (attire.getAction(1) != null && attire.getAction(1).anims.length > 0) || (attire.getAction(51) != null && attire.getAction(51).anims.length > 0))
			{
				result.add(attire);
			}
		}

		Collections.sort(result, new Comparator<Attire>()
		{
			@Override
			public int compare(Attire o1, Attire o2)
			{
				return o1.name.compareTo(o2.name);
			}
		});

		return result.toArray(new Attire[result.size()]);
	}

	// -----------------------------------------------------------------------------------------------------------------------
	//
	// 版本信息
	//
	// -----------------------------------------------------------------------------------------------------------------------

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
		if (new_table.containsKey(key))
		{
			return new_table.get(key);
		}

		return null;
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
	 * 获取版本文件
	 * 
	 * @return
	 */
	private File getVerFile()
	{
		return new File(root.getOutputFolder().getPath() + File.separatorChar + ".ver" + File.separatorChar + "uiAvatar");
	}

	/**
	 * 打开
	 */
	private void openVer()
	{
		old_table = new Hashtable<String, String>();
		new_table = new Hashtable<String, String>();

		if (!getVerFile().exists())
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
	public void saveVer()
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
