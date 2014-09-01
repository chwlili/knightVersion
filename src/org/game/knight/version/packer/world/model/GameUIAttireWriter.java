package org.game.knight.version.packer.world.model;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

import org.chw.swf.writer.SwfBitmap;
import org.chw.swf.writer.SwfWriter;
import org.chw.util.FileUtil;
import org.chw.util.MD5Util;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.world.BaseWriter;
import org.game.knight.version.packer.world.WorldWriter;

public class GameUIAttireWriter extends BaseWriter
{
	private static final String UI_AVATAR_FRAME_PACK = "knight.ui.avatar";

	private HashMap<String, String> newTable = new HashMap<String, String>();
	private HashMap<String, String> oldTable = new HashMap<String, String>();

	private ArrayList<AttireAnim> newAnims;
	private ArrayList<AttireAnim> allAnims;
	private Hashtable<AttireAnim, SWFFile> anim_file;

	private int nextIndex = 0;
	private int finishedCount = 0;
	private String lastLog;

	private String cfgFileURL;

	/**
	 * 构造函数
	 * 
	 * @param world
	 */
	public GameUIAttireWriter(WorldWriter root)
	{
		super(root, "uiAttire");
	}

	/**
	 * 获取配置文件地址
	 * 
	 * @return
	 */
	public String getCfgFileURL()
	{
		return cfgFileURL;
	}

	/**
	 * 获取下一个要输出的动画
	 * 
	 * @return
	 */
	private synchronized AttireAnim getNext()
	{
		AttireAnim result = null;
		if (nextIndex < newAnims.size())
		{
			result = newAnims.get(nextIndex);
			lastLog = "输出UI角色动画(" + nextIndex + "/" + newAnims.size() + ")：图像*" + anim_file.get(result).bitmaps.length;
			nextIndex++;
		}
		return result;
	}

	/**
	 * 完成一个要输出的动画
	 * 
	 * @param anim
	 */
	private synchronized void finish(AttireAnim anim)
	{
		finishedCount++;
	}

	/**
	 * 是否已经完成
	 * 
	 * @return
	 */
	private synchronized boolean hasFinished()
	{
		return finishedCount >= newAnims.size();
	}

	@Override
	protected void startup() throws Exception
	{
		GamePacker.log("开始输出UI Avatar文件");
	}

	@Override
	protected void exec() throws Exception
	{
		allAnims = new ArrayList<AttireAnim>();
		newAnims = new ArrayList<AttireAnim>();
		anim_file = new Hashtable<AttireAnim, SWFFile>();

		GamePacker.progress("过滤装扮数据");
		Attire[] attireList = filterAttires(root.getAttireTable().getAllAttire());

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

					ArrayList<SWFBitmap> bitmaps = new ArrayList<SWFBitmap>();

					int regionCount = rowCount * colCount;
					for (int i = 0; i < regionCount; i++)
					{
						int delay = anim.times[i];
						if (delay > 0)
						{
							ImageFrame frame = root.getImageFrameTable().get(anim.img.gid, anim.row, anim.col, i);
							if (frame != null)
							{
								bitmaps.add(new SWFBitmap("$" + anim.img.gid + "_" + rowCount + "_" + colCount + "_" + i, frame, delay));
							}
						}
					}

					Collections.sort(bitmaps, new Comparator<SWFBitmap>()
					{
						@Override
						public int compare(SWFBitmap o1, SWFBitmap o2)
						{
							String[] list1 = new String[] { o1.frame.file.gid, o1.frame.row + "", o1.frame.col + "", o1.frame.index + "" };
							String[] list2 = new String[] { o2.frame.file.gid, o2.frame.row + "", o2.frame.col + "", o2.frame.index + "" };
							for (int i = 0; i < list1.length; i++)
							{
								String val1 = list1[i];
								String val2 = list2[i];
								try
								{
									int int1 = Integer.parseInt(val1);
									int int2 = Integer.parseInt(val2);
									if (int1 != int2)
									{
										return int1 - int2;
									}
								}
								catch (NumberFormatException e)
								{
									if (!val1.equals(val2))
									{
										return val1.compareTo(val2);
									}
								}
							}
							return 0;
						}
					});

					StringBuilder sb = new StringBuilder();
					for (SWFBitmap bitmap : bitmaps)
					{
						if (sb.length() > 0)
						{
							sb.append("+");
						}
						sb.append(String.format("%s_%s_%s_%s", bitmap.frame.file.gid, bitmap.frame.row, bitmap.frame.col, bitmap.frame.index));
					}

					String fileID = sb.toString();
					if (!activate(fileID))
					{
						add(fileID, root.getGlobalOptionTable().getNextExportFile() + ".swf");
						newAnims.add(anim);
					}

					allAnims.add(anim);
					anim_file.put(anim, new SWFFile(bitmaps.toArray(new SWFBitmap[bitmaps.size()]), newTable.get(fileID)));
				}
			}
		}

		// 开启多线程输出
		ExecutorService exec = Executors.newCachedThreadPool();
		for (int i = 0; i < root.maxThreadCount; i++)
		{
			exec.execute(new Runnable()
			{
				@Override
				public void run()
				{
					while (true)
					{
						AttireAnim anim = getNext();
						if (anim == null || root.isCancel())
						{
							break;
						}

						try
						{
							outputSWF(anim_file.get(anim));
							finish(anim);
						}
						catch (Exception e)
						{
							root.cancel(e);
						}
					}
				}
			});
		}

		// 等待所有线程结束
		while (!root.isCancel() && !hasFinished())
		{
			try
			{
				GamePacker.progress(lastLog);
				Thread.sleep(500);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				break;
			}
		}

		// 终止线程池
		exec.shutdown();

		// 输出配置文件
		if (!root.isCancel())
		{
			writerCfgFile();
		}
	}

	/**
	 * 输出SWF文件
	 * 
	 * @param file
	 * @throws Exception
	 */
	private void outputSWF(SWFFile file) throws Exception
	{
		ArrayList<byte[]> swfBytes = new ArrayList<byte[]>();

		// 输出PNG
		for (SWFBitmap bitmap : file.bitmaps)
		{
			ImageFrame frame = bitmap.frame;
			BufferedImage img = ImageIO.read(bitmap.frame.file);

			BufferedImage texture = new BufferedImage(frame.clipW, frame.clipH, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = (Graphics2D) texture.getGraphics();
			graphics.drawImage(img, 0, 0, frame.clipW, frame.clipH, frame.frameX + frame.clipX, frame.frameY + frame.clipY, frame.frameX + frame.clipX + frame.clipW, frame.frameY + frame.clipY + frame.clipH, null);
			graphics.dispose();

			ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
			ImageIO.write(texture, "png", outputBytes);

			swfBytes.add(outputBytes.toByteArray());
		}

		// 输出SWF
		SwfWriter swf = new SwfWriter();
		for (int i = 0; i < file.bitmaps.length; i++)
		{
			SWFBitmap bitmap = file.bitmaps[i];
			swf.addBitmap(new SwfBitmap(swfBytes.get(i), UI_AVATAR_FRAME_PACK, bitmap.typeID, true));
		}

		File outputFile = new File(root.getOutputFolder().getPath() + file.url);
		FileUtil.writeFile(outputFile, swf.toBytes(true));
		root.addFileSuffix(outputFile);
	}

	/**
	 * 输出XML配置
	 * 
	 * @throws Exception
	 */
	private void writerCfgFile() throws Exception
	{
		GamePacker.progress("输出动画配置");
		StringBuilder json_role = new StringBuilder();
		StringBuilder json_role_light = new StringBuilder();
		StringBuilder json_equip = new StringBuilder();
		StringBuilder json_partner = new StringBuilder();
		StringBuilder json_horse = new StringBuilder();

		Attire[] attireList = filterAttires(root.getAttireTable().getAllAttire());

		StringBuilder json_attires = new StringBuilder();
		for (Attire attire : attireList)
		{
			StringBuilder json_actions = new StringBuilder();
			for (AttireAction action : attire.actions)
			{
				if (action.id != 0 && action.id != 1 && action.id != 51)
				{
					continue;
				}

				StringBuilder json_anims = new StringBuilder();
				for (AttireAnim anim : action.anims)
				{
					SWFFile file = anim_file.get(anim);
					String animFilePath = file.url;
					File savedFile = new File(root.getOutputFolder().getPath() + animFilePath);
					int animFileSize = (int) savedFile.length();

					StringBuilder json_frames = new StringBuilder();
					for (int i = 0; i < file.bitmaps.length; i++)
					{
						SWFBitmap bitmap = file.bitmaps[i];
						ImageFrame frame = bitmap.frame;
						int offSetX = frame.clipX + frame.clipW / 2 - frame.frameW / 2;
						int offsetY = frame.clipY + frame.clipH - frame.frameH;

						// 帧信息
						if (json_frames.length() > 0)
						{
							json_frames.append(",");
						}
						json_frames.append(String.format("{\"x\":%s,\"y\":%s,\"delay\":%s,\"classID\":\"%s\"}", offSetX, offsetY, bitmap.time, bitmap.typeID));
					}

					// 动画信息
					if (json_anims.length() > 0)
					{
						json_anims.append(",");
					}
					json_anims.append(String.format("{\"x\":%s,\"y\":%s,\"scaleX\":%s,\"scaleY\":%s,\"flip\":%s,\"groupID\":%s,\"layerID\":%s,\"fileURL\":\"%s\",\"fileSize\":%s,\"frames\":[%s]}", anim.x, anim.y, anim.scaleX, anim.scaleY, anim.flip, anim.groupID, anim.layerID, root.localToCdnURL(animFilePath), animFileSize, json_frames.toString()));
				}

				// 动作信息
				if (json_actions.length() > 0)
				{
					json_actions.append(",");
				}
				json_actions.append(String.format("\"%s\":{\"nameX\":%s,\"nameY\":%s,\"anims\":[%s]}", action.id, action.hitRect.nameX, action.hitRect.nameY, json_anims.toString()));
			}

			// 装扮信息
			if (json_attires.length() > 0)
			{
				json_attires.append(",");
			}
			json_attires.append(String.format("\"%s\":{\"nameX\":%s,\"nameY\":%s,\"width\":%s,\"height\":%s,\"actions\":{%s}}", attire.name, attire.hitRect.nameX, attire.hitRect.nameY, attire.hitRect.width, attire.hitRect.height, json_actions.toString()));

			// 装扮分类
			String[] params = attire.typeParams;
			if (params.length > 0)
			{
				if (params[0].equals("1") && params.length >= 3)
				{
					// 装扮
					if (json_role.length() > 0)
					{
						json_role.append(",");
					}
					json_role.append(String.format("\"%s_%s\":\"%s\"", params[1], params[2], attire.name));
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
						json_equip.append(String.format("\"%s_%s\":\"%s\"", i, sectID, attire.name));
					}
				}
				else if (params[0].equals("6") && params.length >= 2)
				{
					// 刀光
					if (json_role_light.length() > 0)
					{
						json_role_light.append(",");
					}
					json_role_light.append(String.format("\"%s\":\"%s\"", params[1], attire.name));
				}
				else if (params[0].equals("7") && params.length >= 2)
				{
					// 伙伴
					if (json_partner.length() > 0)
					{
						json_partner.append(",");
					}
					json_partner.append(String.format("\"%s\":\"%s\"", params[1], attire.name));
				}
				else if (params[0].equals("8") && params.length >= 2)
				{
					// 坐骑
					if (json_horse.length() > 0)
					{
						json_horse.append(",");
					}
					json_horse.append(String.format("\"%s\":\"%s\"", params[1], attire.name));
				}
			}
		}

		String text = String.format("{\"classPackageName\":\"%s\",\"roleMap\":{%s},\"roleLightMap\":{%s},\"equipMap\":{%s},\"partnerMap\":{%s},\"horseMap\":{%s},\"attires\":{%s}}", UI_AVATAR_FRAME_PACK, json_role.toString(), json_role_light.toString(), json_equip.toString(), json_partner.toString(), json_horse.toString(), json_attires.toString());
		byte[] bytes = text.getBytes("utf8");
		String md5 = MD5Util.md5Bytes(bytes);

		if (!activate(md5))
		{
			add(md5, root.getGlobalOptionTable().getNextExportFile() + ".cfg");

			File outputFile = new File(root.getOutputFolder().getPath() + newTable.get(md5));

			FileUtil.writeFile(outputFile, bytes);
			root.addFileSuffix(outputFile);
		}

		cfgFileURL = newTable.get(md5);
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

	private static class SWFFile
	{
		public final SWFBitmap[] bitmaps;
		public final String url;

		public SWFFile(SWFBitmap[] bitmaps, String url)
		{
			this.bitmaps = bitmaps;
			this.url = url;
		}
	}

	private static class SWFBitmap
	{
		public final String typeID;
		public final ImageFrame frame;
		public final int time;

		public SWFBitmap(String typeID, ImageFrame frame, int time)
		{
			this.typeID = typeID;
			this.frame = frame;
			this.time = time;
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	//
	// 版本信息
	//
	// -------------------------------------------------------------------------------------------------------------------

	/**
	 * 激活
	 * 
	 * @param file
	 * @return
	 */
	private boolean activate(String fileID)
	{
		if (oldTable.containsKey(fileID))
		{
			newTable.put(fileID, oldTable.get(fileID));
			oldTable.remove(fileID);
		}
		return newTable.containsKey(fileID);
	}

	/**
	 * 添加
	 * 
	 * @param file
	 * @return
	 */
	private void add(String fileID, String url)
	{
		newTable.put(fileID, url);
	}

	@Override
	protected void readHistory(BufferedReader reader) throws Exception
	{
		while (true)
		{
			String line = reader.readLine();
			if (line == null)
			{
				break;
			}

			line = line.trim();
			if (line.isEmpty())
			{
				continue;
			}

			String[] items = line.split("=");
			if (items.length != 2)
			{
				continue;
			}

			String key = items[0].trim();
			if (key.isEmpty())
			{
				continue;
			}

			String url = items[1].trim();
			if (url.isEmpty())
			{
				continue;
			}

			oldTable.put(key, url);
		}
	}

	@Override
	protected void saveHistory(BufferedWriter writer) throws Exception
	{
		// 排序
		String[] keys = newTable.keySet().toArray(new String[newTable.size()]);
		Arrays.sort(keys);

		// 写入
		for (int i = 0; i < keys.length; i++)
		{
			String key = keys[i];
			String url = newTable.get(key);

			writer.write(key + " = " + url);

			if (i < keys.length - 1)
			{
				writer.write("\n");
			}
		}

		// 记录输出文件
		for (String url : newTable.values())
		{
			root.addOutputFile(url);
		}
	}
}
