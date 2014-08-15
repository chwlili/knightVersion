package org.game.knight.version.packer.world.output3d;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

import org.chw.util.FileUtil;
import org.chw.util.MaxRects;
import org.chw.util.MaxRects.Rect;
import org.chw.util.MaxRects.RectSet;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.world.model.AtfParam;
import org.game.knight.version.packer.world.model.Attire;
import org.game.knight.version.packer.world.model.AttireAction;
import org.game.knight.version.packer.world.model.AttireAnim;
import org.game.knight.version.packer.world.model.ImageFrame;
import org.game.knight.version.packer.world.task.RootTask;

public class AtlasTable
{
	private RootTask root;

	private Group[] inputGroups;
	private GroupTexture[] textureInputList;
	private String lastLog;

	public AtlasTable(RootTask root)
	{
		this.root = root;
	}

	/**
	 * 开始
	 */
	public void start()
	{
		openVer();

		inputGroups = filterAtfGroup();

		packAll();

		if (root.isCancel())
		{
			return;
		}

		ArrayList<GroupTexture> allAtlas = new ArrayList<GroupTexture>();
		for (Group group : inputGroups)
		{
			for (GroupTexture texture : group.textures)
			{
				allAtlas.add(texture);
			}
		}
		textureInputList = allAtlas.toArray(new GroupTexture[allAtlas.size()]);

		writeAllAtf();
	}

	// -----------------------------------------------------------------------------------------------------
	//
	// 过滤ATF组
	//
	// -----------------------------------------------------------------------------------------------------

	/**
	 * 过滤ATF组
	 * 
	 * @return
	 */
	private Group[] filterAtfGroup()
	{
		HashMap<AtfParam, HashSet<ImageFrame>> groups = new HashMap<AtfParam, HashSet<ImageFrame>>();
		for (Attire attire : root.getAttireTable().getAllAttire())
		{
			for (AttireAction action : attire.actions)
			{
				for (AttireAnim anim : action.anims)
				{
					for (int i = 0; i < anim.times.length; i++)
					{
						if (anim.times[i] > 0)
						{
							AtfParam atf = anim.param;
							if (!groups.containsKey(atf))
							{
								groups.put(atf, new HashSet<ImageFrame>());
							}

							groups.get(atf).add(root.getImageFrameTable().get(anim.img, anim.row, anim.col, i));
						}
					}
				}
			}
		}

		ArrayList<Group> results = new ArrayList<Group>();
		for (AtfParam atf : groups.keySet())
		{
			HashSet<ImageFrame> frames = groups.get(atf);
			results.add(new Group(atf, frames.toArray(new ImageFrame[frames.size()])));
		}

		return results.toArray(new Group[results.size()]);
	}

	// -----------------------------------------------------------------------------------------------------
	//
	// 矩形打包
	//
	// -----------------------------------------------------------------------------------------------------

	private int nextPackIndex;
	private int finishedPackCount;

	/**
	 * 获取下一个要打包的组
	 * 
	 * @return
	 */
	private synchronized Group getNextPack()
	{
		Group result = null;
		if (nextPackIndex < inputGroups.length)
		{
			result = inputGroups[nextPackIndex];
			lastLog = "贴图打包(" + nextPackIndex + "/" + inputGroups.length + "):" + result.atf.id + "(" + result.frames.length + ")";
			nextPackIndex++;
		}
		return result;
	}

	/**
	 * 完成一组打包
	 * 
	 * @param group
	 * @param rects
	 */
	private synchronized void finishPack(Group group, GroupTexture[] textures)
	{
		finishedPackCount++;

		group.textures = textures;
	}

	/**
	 * 所有组是否已完成打包
	 * 
	 * @return
	 */
	private synchronized boolean isPackFinished()
	{
		return finishedPackCount >= inputGroups.length;
	}

	/**
	 * 打包
	 */
	private void packAll()
	{
		ExecutorService exec = Executors.newCachedThreadPool();
		for (int i = 0; i < 5; i++)
		{
			exec.execute(new Runnable()
			{
				@Override
				public void run()
				{
					while (true)
					{
						Group next = getNextPack();
						if (next == null || root.isCancel())
						{
							break;
						}

						finishPack(next, packGroup(next));
					}
				}
			});
		}
		exec.shutdown();

		while (!root.isCancel() && !isPackFinished())
		{
			try
			{
				GamePacker.progress(lastLog);
				Thread.sleep(50);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				break;
			}
		}
	}

	/**
	 * 打包单组
	 * 
	 * @param group
	 */
	private GroupTexture[] packGroup(Group group)
	{
		MaxRects packer = new MaxRects(group.atf.width, group.atf.height, false);
		for (ImageFrame frame : group.frames)
		{
			packer.push(frame, frame.clipW, frame.clipH);
		}

		try
		{
			packer.pack();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		ArrayList<RectSet> rectSets = packer.getRectSets();
		GroupTexture[] result = new GroupTexture[rectSets.size()];
		for (int i = 0; i < rectSets.size(); i++)
		{
			RectSet rectSet = rectSets.get(i);

			ArrayList<AtlasRect> list = new ArrayList<AtlasRect>();
			for (Rect rect : rectSet.getRects())
			{
				list.add(new AtlasRect(group.atf, (ImageFrame) rect.data, rect.x, rect.y));
			}

			result[i] = new GroupTexture(group, list.toArray(new AtlasRect[list.size()]));
		}
		return result;
	}

	// -----------------------------------------------------------------------------------------------------
	//
	// 图像输出
	//
	// -----------------------------------------------------------------------------------------------------

	private int textureNext;
	private int textureWrited;

	/**
	 * 获取下一个
	 * 
	 * @return
	 */
	private synchronized GroupTexture getNext()
	{
		GroupTexture result = null;
		if (textureNext < textureInputList.length)
		{
			result = textureInputList[textureNext];
			lastLog = "贴图输出(" + textureNext + "/" + textureInputList.length + "):" + result.group.atf.id + "(图像X" + result.rects.length + ")";
			textureNext++;
		}

		return result;
	}

	/**
	 * 完成
	 */
	private synchronized void finishIncrement()
	{
		textureWrited++;
	}

	/**
	 * 是否已经完成
	 * 
	 * @return
	 */
	private synchronized boolean isFinished()
	{
		return textureWrited >= textureInputList.length;
	}

	/**
	 * 输出所有ATF
	 */
	private void writeAllAtf()
	{
		ExecutorService exec = Executors.newCachedThreadPool();
		for (int i = 0; i < 5; i++)
		{
			exec.execute(new Runnable()
			{
				@Override
				public void run()
				{
					while (true)
					{
						GroupTexture next = getNext();
						if (next == null || root.isCancel())
						{
							break;
						}

						writeATF(next);
						finishIncrement();
					}
				}
			});
		}
		exec.shutdown();

		while (!root.isCancel() && !isFinished())
		{
			try
			{
				GamePacker.progress(lastLog);
				Thread.sleep(50);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				break;
			}
		}
	}

	/**
	 * 输出ATF
	 * 
	 * @param rects
	 */
	private void writeATF(GroupTexture texture)
	{
		Group group = texture.group;
		AtlasRect[] rects = Arrays.copyOf(texture.rects, texture.rects.length);

		Arrays.sort(rects, new Comparator<AtlasRect>()
		{
			@Override
			public int compare(AtlasRect o1, AtlasRect o2)
			{
				return o1.frame.file.url.compareTo(o2.frame.file.url);
			}
		});

		int outputW = 0;
		int outputH = 0;
		for (AtlasRect rect : rects)
		{
			outputW = Math.max(outputW, rect.x + rect.frame.clipW);
			outputH = Math.max(outputH, rect.y + rect.frame.clipH);
		}
		outputW = TextureHelper.normalizeWH(outputW);
		outputH = TextureHelper.normalizeWH(outputH);

		File subFile = null;
		BufferedImage subImage = null;

		String saveURL = root.getWriteFileTable().getNextExportFile();
		String pngURL = saveURL + ".png";
		String atfURL = saveURL + ".atf";
		String xmlURL = saveURL + ".xml";
		File pngFile = new File(root.getOutputFolder().getPath() + pngURL);
		File atfFile = new File(root.getOutputFolder().getPath() + atfURL);
		File xmlFile = new File(root.getOutputFolder().getPath() + xmlURL);

		StringBuilder atlas = new StringBuilder();
		BufferedImage image = new BufferedImage(outputW, outputH, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		for (AtlasRect rect : rects)
		{
			int drawX = rect.x;
			int drawY = rect.y;
			ImageFrame frame = rect.frame;

			if (rect.frame.file != subFile)
			{
				try
				{
					subFile = rect.frame.file;
					subImage = ImageIO.read(subFile);
				}
				catch (IOException e)
				{
					e.printStackTrace();
					continue;
				}
			}

			atlas.append("\t<SubTexture name=\"" + frame.file.gid + "_" + frame.row + "_" + frame.col + "_" + frame.index + "\" x=\"" + drawX + "\" y=\"" + drawY + "\" width=\"" + frame.clipW + "\" height=\"" + frame.clipH + "\" frameX=\"" + (frame.clipX > 0 ? -frame.clipX : 0) + "\" frameY=\"" + (frame.clipY > 0 ? -frame.clipY : 0) + "\" frameWidth=\"" + frame.frameW + "\" frameHeight=\"" + frame.frameH + "\"/>\n");

			graphics.drawImage(subImage, drawX, drawY, drawX + frame.clipW, drawY + frame.clipH, frame.frameX + frame.clipX, frame.frameY + frame.clipY, frame.frameX + frame.clipX + frame.clipW, frame.frameY + frame.clipY + frame.clipH, null);

			if (root.isCancel())
			{
				return;
			}
		}
		graphics.dispose();
		
		StringBuilder xmlCfg=new StringBuilder();
		xmlCfg.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		xmlCfg.append("<TextureAtlas imagePath=\"" + ("/" + root.getOutputFolder().getName() + atfURL) + "\">\n");
		xmlCfg.append(atlas.toString());
		xmlCfg.append("</TextureAtlas>");
		
		try
		{
			ImageIO.write(image, "png", pngFile);
			TextureHelper.png2atf(pngFile, atfFile);
			
			RandomAccessFile a=new RandomAccessFile(atfFile, "rw");
			a.seek(atfFile.length());
			a.write(xmlCfg.toString().getBytes("utf8"));
			a.close();
			
			if(pngFile.exists())
			{
				pngFile.delete();
			}
			
			texture.outputURL=atfURL;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	// -----------------------------------------------------------------------------------------------------
	//
	//
	//
	// -----------------------------------------------------------------------------------------------------

	/**
	 * ATF组
	 * 
	 * @author ds
	 * 
	 */
	private static class Group
	{
		public final String key;
		public final AtfParam atf;
		public final ImageFrame[] frames;

		public GroupTexture[] textures;

		public Group(AtfParam atf, ImageFrame[] frames)
		{
			StringBuilder sb = new StringBuilder();
			sb.append(atf.id + "+" + atf.width + "+" + atf.height + "+" + atf.other);
			for (ImageFrame frame : frames)
			{
				sb.append("+" + frame.file.gid + "_" + frame.row + "_" + frame.col + "_" + frame.index);
			}

			this.atf = atf;
			this.frames = frames;
			this.key = sb.toString();
		}
	}

	private static class GroupTexture
	{
		public final Group group;
		public final AtlasRect[] rects;
		public String outputURL;

		public GroupTexture(Group group, AtlasRect[] rects)
		{
			this.group = group;
			this.rects = rects;
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	//
	// 版本信息
	//
	// -------------------------------------------------------------------------------------------------------------------

	private HashMap<String, Group> oldTable = new HashMap<String, Group>();
	private HashMap<String, Group> newTable = new HashMap<String, Group>();

	/**
	 * 获取版本文件
	 * 
	 * @return
	 */
	private File getVerFile()
	{
		return new File(root.getOutputFolder().getPath() + File.separatorChar + ".ver" + File.separatorChar + ".atlas");
	}

	/**
	 * 打开版本信息
	 */
	private void openVer()
	{
		// this.oldTable = new HashMap<String, Group>();
		// this.newTable = new HashMap<String, Group>();
		//
		// if (!getVerFile().exists())
		// {
		// return;
		// }
		//
		// try
		// {
		// String text = new String(FileUtil.getFileBytes(getVerFile()),
		// "utf8");
		// String[] lines = text.split("\\n");
		// for (String line : lines)
		// {
		// line = line.trim();
		// if (line.isEmpty())
		// {
		// continue;
		// }
		//
		// String[] values = line.split("=");
		// if (values.length == 2)
		// {
		// String key = values[0].trim();
		// String output = values[1].trim();
		//
		// oldTable.put(key, output);
		// }
		// }
		// }
		// catch (UnsupportedEncodingException e)
		// {
		// e.printStackTrace();
		// }
	}

	/**
	 * 保存版本信息
	 */
	public void saveVer()
	{
		// StringBuilder output = new StringBuilder();
		//
		// if (newTable != null)
		// {
		// String[] keys = newTable.keySet().toArray(new
		// String[newTable.size()]);
		// Arrays.sort(keys, new Comparator<String>()
		// {
		// @Override
		// public int compare(String arg0, String arg1)
		// {
		// int val1 = Integer.parseInt(arg0.substring(0, arg0.indexOf("_")));
		// int val2 = Integer.parseInt(arg1.substring(0, arg1.indexOf("_")));
		// return val1 - val2;
		// }
		// });
		//
		// for (int i = 0; i < keys.length; i++)
		// {
		// String key = keys[i];
		// SliceImage img = newTable.get(key);
		//
		// output.append(key + " = " + img.previewURL + "," + img.sliceRow + ","
		// + img.sliceCol + ",");
		// for (int j = 0; j < img.sliceURLs.length; j++)
		// {
		// if (j > 0)
		// {
		// output.append(",");
		// }
		// output.append(img.sliceURLs[j]);
		// }
		// if (i < keys.length - 1)
		// {
		// output.append("\n");
		// }
		// }
		// }
		//
		// try
		// {
		// FileUtil.writeFile(getVerFile(), output.toString().getBytes("utf8"));
		// }
		// catch (UnsupportedEncodingException e)
		// {
		// e.printStackTrace();
		// }
	}
}
