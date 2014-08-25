package org.game.knight.version.packer.world.output2d;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import org.chw.util.ZlibUtil;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.world.Region;
import org.game.knight.version.packer.world.model.Attire;
import org.game.knight.version.packer.world.model.AttireAction;
import org.game.knight.version.packer.world.model.AttireAnim;
import org.game.knight.version.packer.world.model.AttireAudio;
import org.game.knight.version.packer.world.model.AttireBitmap;
import org.game.knight.version.packer.world.model.ImageFrame;
import org.game.knight.version.packer.world.model.ProjectImgFile;
import org.game.knight.version.packer.world.task.RootTask;

public class Config2dAttireWriter
{
	private static final String AVATAR2_FRAME_PACK = "knight.avatar2.frames";

	private RootTask root;
	private String outputURL;

	private int nextIndex;
	private int finishedCount;
	private String lastLog;

	private ArrayList<ArrayList<ImageFrame>> newFrameList;
	private ArrayList<ArrayList<ImageFrame>> allFrameList;
	private HashMap<ArrayList<ImageFrame>, String> frameList_url;

	private HashMap<String, String> newTable = new HashMap<String, String>();
	private HashMap<String, String> oldTable = new HashMap<String, String>();

	/**
	 * 构造函数
	 * 
	 * @param root
	 */
	public Config2dAttireWriter(RootTask root)
	{
		this.root = root;
	}

	/**
	 * 获取输出URL
	 * 
	 * @return
	 */
	public String getOutputURL()
	{
		return outputURL;
	}

	/**
	 * 获取指定帧的AS类名
	 * 
	 * @param frame
	 * @return
	 */
	private String getFrameClassID(ImageFrame frame)
	{
		return "$2d_" + frame.file.gid + "_" + 1 + "_" + 1 + "_0";
	}

	/**
	 * 获取下一个组
	 * 
	 * @return
	 */
	private synchronized ArrayList<ImageFrame> getNext()
	{
		ArrayList<ImageFrame> result = null;
		if (nextIndex < newFrameList.size())
		{
			result = newFrameList.get(nextIndex);
			// lastLog
			nextIndex++;
		}
		return result;
	}

	/**
	 * 完成一组
	 * 
	 * @param frames
	 */
	private synchronized void finish(ArrayList<ImageFrame> frames)
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
		return finishedCount >= newFrameList.size();
	}

	/**
	 * 开始
	 */
	private void start()
	{
		GamePacker.progress("输出装扮配置");

		openVer();

		final Hashtable<String, ArrayList<ImageFrame>> bagID_frames = new Hashtable<String, ArrayList<ImageFrame>>();

		GamePacker.progress("分析装扮数据");
		for (AttireBitmap bitmap : root.getAttireTable().getAllBitmaps())
		{
			ImageFrame frame = root.getImageFrameTable().get(bitmap.imgFile.gid, 1, 1, 0);
			if (frame == null)
			{
				continue;
			}

			String bagID = bitmap.atfParam.id;
			if (!bagID_frames.containsKey(bagID))
			{
				bagID_frames.put(bagID, new ArrayList<ImageFrame>());
			}

			ArrayList<ImageFrame> frames = bagID_frames.get(bagID);
			if (!frames.contains(frame))
			{
				frames.add(frame);
			}
		}

		for (Attire attire : root.getAttireTable().getAllAttire())
		{
			for (AttireAction action : attire.actions)
			{
				for (AttireAnim anim : action.anims)
				{
					String bagID = anim.param.id;

					int rowCount = anim.row;
					int colCount = anim.col;
					int regionCount = rowCount * colCount;

					for (int i = 0; i < regionCount; i++)
					{
						if (anim.times[i] <= 0)
						{
							continue;
						}

						ImageFrame frame = root.getImageFrameTable().get(anim.img.gid, anim.row, anim.col, i);
						if (frame == null)
						{
							continue;
						}

						if (!bagID_frames.containsKey(bagID))
						{
							bagID_frames.put(bagID, new ArrayList<ImageFrame>());
						}

						ArrayList<ImageFrame> frames = bagID_frames.get(bagID);
						if (!frames.contains(frame))
						{
							frames.add(frame);
						}
					}
				}
			}
		}

		newFrameList = new ArrayList<ArrayList<ImageFrame>>();
		allFrameList = new ArrayList<ArrayList<ImageFrame>>();
		frameList_url = new HashMap<ArrayList<ImageFrame>, String>();

		for (String groupID : bagID_frames.keySet())
		{
			ArrayList<ImageFrame> frames = bagID_frames.get(groupID);
			Collections.sort(frames, new Comparator<ImageFrame>()
			{
				@Override
				public int compare(ImageFrame o1, ImageFrame o2)
				{
					return getFrameClassID(o1).compareTo(getFrameClassID(o2));
				}
			});

			StringBuilder frameListID = new StringBuilder();
			for (ImageFrame frame : frames)
			{
				if (frameListID.length() > 0)
				{
					frameListID.append("+");
				}
				frameListID.append(frame.file.gid + "_" + frame.row + "_" + frame.col + "_" + frame.index);
			}

			String fileID = frameListID.toString();
			if (!activate(frameListID.toString()))
			{
				add(fileID, root.getGlobalOptionTable().getNextExportFile() + ".swf");

				newFrameList.add(frames);
			}
			
			allFrameList.add(frames);
			
			frameList_url.put(frames, newTable.get(fileID));
		}

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
						ArrayList<ImageFrame> next = getNext();
						if (next == null || root.isCancel())
						{
							break;
						}

						try
						{
							outputSwfs(next);
							finish(next);
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
				}
			});
		}
	}

	private void outputSwfs(ArrayList<ImageFrame> frames) throws IOException
	{
		ArrayList<String> classIDs = new ArrayList<String>();
		ArrayList<byte[]> pngBytes = new ArrayList<byte[]>();

		// 导出PNG
		for (int j = 0; j < frames.size(); j++)
		{
			ImageFrame frame = frames.get(j);
			BufferedImage img = ImageIO.read(frame.file);

			BufferedImage texture = new BufferedImage(frame.clipW, frame.clipH, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = (Graphics2D) texture.getGraphics();
			graphics.drawImage(img, 0, 0, frame.clipW, frame.clipH, frame.frameX + frame.clipX, frame.frameY + frame.clipY, frame.frameX + frame.clipX + frame.clipW, frame.frameY + frame.clipY + frame.clipH, null);
			graphics.dispose();

			ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
			ImageIO.write(texture, "png", outputBytes);

			classIDs.add(getFrameClassID(frame));
			pngBytes.add(outputBytes.toByteArray());
		}

		// 输出SWF
		SwfWriter swf = new SwfWriter();
		for (int j = 0; j < classIDs.size(); j++)
		{
			swf.addBitmap(new SwfBitmap(pngBytes.get(j), AVATAR2_FRAME_PACK, classIDs.get(j), true));
		}
		FileUtil.writeFile(new File(root.getOutputFolder().getPath()+frameList_url.get(frames)), swf.toBytes(true));
	}
/*
	private void writeAttireConfig()
	{
		GamePacker.progress("输出装扮配置");
		StringBuilder attireText = new StringBuilder();
		attireText.append("<attires>\n");
		for (Attire attire : root.getAttireTable().getAllAttire())
		{
			attireText.append(String.format("\t<attire id=\"%s.%s\" name=\"%s\" x=\"%s\" y=\"%s\" width=\"%s\" height=\"%s\">\n", attire.gid, attire.name, attire.hitRect.x, attire.hitRect.y, attire.hitRect.width, attire.hitRect.height));

			for (AttireAction action : attire.actions)
			{
				attireText.append(String.format("\t\t<action id=\"%s\" nameX=\"%s\" nameY=\"%s\" >\n", action.id, action.hitRect.nameX, action.hitRect.nameY));

				for (AttireAnim anim : action.anims)
				{
					attireText.append(String.format("\t\t\t<anim x=\"%s\" y=\"%s\" scaleX=\"%s\" scaleY=\"%s\" flip=\"%s\" groupID=\"%s\" layerID=\"%s\">\n", anim.x, anim.y, anim.scaleX, anim.scaleY, anim.flip, anim.groupID, anim.layerID));

					String bagID = anim.bagID;
					String imgSHA = world.getChecksumTable().getGID(anim.img.url);

					int rowCount = anim.row;
					int colCount = anim.col;
					int regionCount = rowCount * colCount;

					for (int i = 0; i < regionCount; i++)
					{
						int delay = anim.times[i];
						if (delay > 0)
						{
							ImageFrame frame = root.getImageFrameTable().get(anim.img.gid, anim.row, anim.col, i);

							int offSetX = frame.clipX + frame.clipW / 2 - frame.frameW / 2;
							int offsetY = frame.clipY + frame.clipH - frame.frameH;

							// 帧信息
							String frameFileName = world.getExportedFileUrl(boundle_fileID.get(bagID));
							String frameTypeName = frame_className.get(region);

							attireText.append(String.format("\t\t\t\t<frame fileName=\"%s\" classID=\"%s\" x=\"%s\" y=\"%s\" delay=\"%s\"/>\n", frameFileName, frameTypeName, offSetX, offsetY, delay));
						}
					}

					attireText.append(String.format("\t\t\t</anim>\n"));
				}
				for (AttireAudio audio : action.audioList)
				{
					String audioURL = world.exportFile(world.getChecksumTable().getGID(audio.mp3.url), MD5Util.addSuffix(FileUtil.getFileBytes(audio.mp3.file)), "mp3");
					attireText.append(String.format("\t\t\t<audio path=\"%s\" loop=\"%s\" volume=\"%s\"/>\n", audioURL, audio.loop, audio.volume));
				}

				attireText.append(String.format("\t\t</action>\n"));
			}

			attireText.append(String.format("\t</attire>\n"));
		}
		attireText.append("</attires>");

		byte[] contentBytes = attireText.toString().getBytes("UTF-8");
		if (zip)
		{
			contentBytes = ZlibUtil.compress(contentBytes);
		}

		cfgFileKey = (zip ? "zlib_md5" : "md5") + MD5Util.md5Bytes(contentBytes);

		world.exportFile(cfgFileKey, MD5Util.addSuffix(contentBytes), "cfg");

		//
		StringBuilder configs = new StringBuilder();
		configs.append(String.format("\t<configs>\n"));
		configs.append(String.format("\t\t<config name=\"%s\" path=\"%s\" size=\"%s\"/>\n", "attire", world.getExportedFileUrl(cfgFileKey), world.getExportedFileSize(cfgFileKey)));
		configs.append(String.format("\t</configs>\n"));

		versionData.append(configs.toString());

		//
		StringBuilder txt = new StringBuilder();

		txt.append("\t<attires>\n");

		Hashtable<String, Long> actionFileUrls = new Hashtable<String, Long>();
		StringBuilder roles = new StringBuilder();
		StringBuilder equips = new StringBuilder();
		StringBuilder effects = new StringBuilder();
		StringBuilder labels = new StringBuilder();
		StringBuilder horses = new StringBuilder();
		for (AttireFile attireFile : attires.values())
		{
			for (Attire attire : attireFile.getAllAttires())
			{
				if (attire.isAnimAttire() || attire.getKey().startsWith("0_"))
				{
					continue;
				}

				String[] params = attire.getParams();
				if (params.length == 0)
				{
					continue;
				}

				if (params[0].equals("1"))
				{
					// 装扮
					if (params.length >= 3)
					{
						roles.append(String.format("\t\t<role faction=\"%s\" sectLv=\"%s\" name=\"%s\">\n", params[1], params[2], attire.getRefKey()));

						for (AttireAction action : attire.getActions())
						{
							if (action.animList.size() > 0)
							{
								ActionInfo info = getActionID(action);

								roles.append(String.format("\t\t\t<action id=\"%s\" size=\"%s\" files=\"%s\"/>\n", action.id, info.size, info.urls));

								String[] urls = info.urls.split("\\,");
								for (String url : urls)
								{
									if (!actionFileUrls.containsKey(url))
									{
										File actionFile = new File(world.getDestDir().getParentFile().getPath() + url);
										if (actionFile.exists())
										{
											actionFileUrls.put(url, actionFile.length());
										}
									}
								}
							}
						}

						roles.append(String.format("\t\t</role>\n"));
					}
					else
					{
						GamePacker.error("职业关联的装扮命名错误：" + attire.getRefKey() + "   (应该为：1_职业ID_职业等级_名称)");
					}
				}
				else if (params[0].equals("2"))
				{
					// 装备
					if (params.length >= 4)
					{
						equips.append(String.format("\t\t<equip fromID=\"%s\" toID=\"%s\" faction=\"%s\" name=\"%s\">\n", params[1], params[2], params[3], attire.getRefKey()));

						for (AttireAction action : attire.getActions())
						{
							if (action.animList.size() > 0)
							{
								ActionInfo info = getActionID(action);

								equips.append(String.format("\t\t\t<action id=\"%s\" size=\"%s\" files=\"%s\"/>\n", action.id, info.size, info.urls));

								String[] urls = info.urls.split("\\,");
								for (String url : urls)
								{
									if (!actionFileUrls.containsKey(url))
									{
										File actionFile = new File(world.getDestDir().getParentFile().getPath() + url);
										if (actionFile.exists())
										{
											actionFileUrls.put(url, actionFile.length());
										}
									}
								}
							}
						}

						equips.append(String.format("\t\t</equip>\n"));
					}
					else
					{
						GamePacker.error("与装备关联的装扮命名错误：" + attire.getRefKey() + "   (应该为：2_起始ID_结束ID_职业ID_名称)");
					}
				}
				else if (params[0].equals("3"))
				{
					// 效果
					effects.append(String.format("\t\t<effect effectID=\"%s\">\n", attire.getRefKey()));

					for (AttireAction action : attire.getActions())
					{
						if (action.animList.size() > 0)
						{
							ActionInfo info = getActionID(action);

							effects.append(String.format("\t\t\t<action id=\"%s\" size=\"%s\" files=\"%s\"/>\n", action.id, info.size, info.urls));

							String[] urls = info.urls.split("\\,");
							for (String url : urls)
							{
								if (!actionFileUrls.containsKey(url))
								{
									File actionFile = new File(world.getDestDir().getParentFile().getPath() + url);
									if (actionFile.exists())
									{
										actionFileUrls.put(url, actionFile.length());
									}
								}
							}
						}
					}

					effects.append(String.format("\t\t</effect>\n"));
				}
				else if (params[0].equals("4"))
				{
					// 怪物
				}
				else if (params[0].equals("5"))
				{
					// 标签
					// TextureSet
					// textureSet=textureSetTable.getTextureSet(attire.getTextureSetKey());
					// labels.append(String.format("\t\t<label labelID=\"%s\" size=\"%s\" files=\"%s\"/>\n",attire.getRefKey(),getTextureSetSize(textureSet),getTextureSetURLs(textureSet)));
				}
				else if (params[0].equals("6"))
				{
					// 刀光
					if (params.length >= 3)
					{
						roles.append(String.format("\t\t<roleEffect faction=\"%s\" sectLv=\"%s\" name=\"%s\">\n", params[1], params[2], attire.getRefKey()));

						for (AttireAction action : attire.getActions())
						{
							if (action.animList.size() > 0)
							{
								ActionInfo info = getActionID(action);

								roles.append(String.format("\t\t\t<action id=\"%s\" size=\"%s\" files=\"%s\"/>\n", action.id, info.size, info.urls));

								String[] urls = info.urls.split("\\,");
								for (String url : urls)
								{
									if (!actionFileUrls.containsKey(url))
									{
										File actionFile = new File(world.getDestDir().getParentFile().getPath() + url);
										if (actionFile.exists())
										{
											actionFileUrls.put(url, actionFile.length());
										}
									}
								}
							}
						}
						roles.append(String.format("\t\t</roleEffect>\n"));
					}
					else
					{
						GamePacker.error("与刀光关联的装扮命名错误：" + attire.getRefKey() + "   (应该为：6_职业ID_名称)");
					}
				}
				else if (params[0].equals("7"))
				{
				}
				else if (params[0].equals("8"))
				{
					horses.append(String.format("\t\t<horse horseID=\"%s\" name=\"%s\">\n", params[1], attire.getRefKey()));

					if (params.length >= 2)
					{
						for (AttireAction action : attire.getActions())
						{
							if (action.animList.size() > 0)
							{
								ActionInfo info = getActionID(action);

								roles.append(String.format("\t\t\t<action id=\"%s\" size=\"%s\" files=\"%s\"/>\n", action.id, info.size, info.urls));

								String[] urls = info.urls.split("\\,");
								for (String url : urls)
								{
									if (!actionFileUrls.containsKey(url))
									{
										File actionFile = new File(world.getDestDir().getParentFile().getPath() + url);
										if (actionFile.exists())
										{
											actionFileUrls.put(url, actionFile.length());
										}
									}
								}
							}
						}
						horses.append(String.format("\t\t</horse>\n"));
					}
					else
					{
						GamePacker.error("与坐骑关联的装扮命名错误：" + attire.getRefKey() + "   (应该为：0_坐骑ID_名称)");
					}
				}
			}
		}

		txt.append("\t\t<files>\n");
		String[] rowKeys = actionFileUrls.keySet().toArray(new String[actionFileUrls.size()]);
		Arrays.sort(rowKeys);
		for (String rowKey : rowKeys)
		{
			txt.append(String.format("\t\t\t<file url=\"%s\" size=\"%s\" />\n", rowKey, actionFileUrls.get(rowKey)));
		}
		txt.append("\t\t</files>\n");

		txt.append(roles);
		txt.append(equips);
		txt.append(effects);
		txt.append(labels);
		txt.append("\t</attires>\n");

		versionData.append(txt.toString());
	}
*/
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

	/**
	 * 获取版本文件
	 * 
	 * @return
	 */
	private File getVerFile()
	{
		return new File(root.getOutputFolder().getPath() + File.separatorChar + ".ver" + File.separatorChar + "uiAttire");
	}

	/**
	 * 打开版本信息
	 */
	private void openVer()
	{
		this.oldTable = new HashMap<String, String>();
		this.newTable = new HashMap<String, String>();

		if (!getVerFile().exists())
		{
			return;
		}

		String text = null;

		try
		{
			text = new String(FileUtil.getFileBytes(getVerFile()), "utf8");
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return;
		}

		String[] lines = text.split("\\n");
		for (String line : lines)
		{
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

	/**
	 * 保存版本信息
	 */
	public void saveVer()
	{
		StringBuilder output = new StringBuilder();

		if (newTable == null)
		{
			return;
		}

		String[] keys = newTable.keySet().toArray(new String[newTable.size()]);
		Arrays.sort(keys);

		for (int i = 0; i < keys.length; i++)
		{
			String key = keys[i];
			String url = newTable.get(key);

			output.append(key + " = " + url);

			if (i < keys.length - 1)
			{
				output.append("\n");
			}
		}

		try
		{
			FileUtil.writeFile(getVerFile(), output.toString().getBytes("utf8"));
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			GamePacker.error(e);
			return;
		}

		// 记录输出文件
		for (String url : newTable.values())
		{
			root.addOutputFile(url);
		}
	}
}
