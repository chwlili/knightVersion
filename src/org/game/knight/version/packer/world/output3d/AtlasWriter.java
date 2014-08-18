package org.game.knight.version.packer.world.output3d;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
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

public class AtlasWriter
{
	private RootTask root;

	private HashMap<AtfParam, ArrayList<AtlasRect[]>> atf_rectListSet;
	private ArrayList<AtlasRect[]> allRectList;
	private HashMap<AtlasRect[], AtfParam> rectList_atf;
	private HashMap<AtlasRect[], Atlas> rectList_atlas;

	private HashMap<String, AtlasSet> newTable;
	private HashMap<String, AtlasSet> oldTable;
	
	private HashMap<ImageFrame, Atlas> frame_atlas;

	private int nextIndex;
	private int finishedCount;
	private String lastLog;

	/**
	 * 构造函数
	 * 
	 * @param root
	 */
	public AtlasWriter(RootTask root)
	{
		this.root = root;
	}

	/**
	 * 开始
	 */
	public void start()
	{
		openVer();

		if (root.isCancel())
		{
			return;
		}

		atf_rectListSet = new HashMap<AtfParam, ArrayList<AtlasRect[]>>();
		allRectList = new ArrayList<AtlasRect[]>();
		rectList_atf = new HashMap<AtlasRect[], AtfParam>();
		rectList_atlas = new HashMap<AtlasRect[], Atlas>();

		filterAtfGroup();

		if (root.isCancel())
		{
			return;
		}

		writeAllAtf();

		for (AtfParam key : atf_rectListSet.keySet())
		{
			ArrayList<AtlasRect[]> value = atf_rectListSet.get(key);
			Atlas[] atlasList = new Atlas[value.size()];
			for (int i = 0; i < value.size(); i++)
			{
				atlasList[i] = rectList_atlas.get(value.get(i));
			}

			add(new AtlasSet(key, atlasList));
		}
		
		frame_atlas=new HashMap<ImageFrame, Atlas>();
		for(AtlasSet set:newTable.values())
		{
			for(Atlas atlas:set.atlasList)
			{
				for(AtlasRect rect:atlas.rects)
				{
					frame_atlas.put(rect.frame, atlas);
				}
			}
		}
	}
	
	/**
	 * 查找指定图像帧所分配到的纹理文件
	 * @param frame
	 * @return
	 */
	public Atlas findAtlasByImageFrame(ImageFrame frame)
	{
		return frame_atlas.get(frame);
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
	private void filterAtfGroup()
	{
		HashMap<AtfParam, HashSet<ImageFrame>> atf_frameset = new HashMap<AtfParam, HashSet<ImageFrame>>();
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
							if (!atf_frameset.containsKey(atf))
							{
								atf_frameset.put(atf, new HashSet<ImageFrame>());
							}

							atf_frameset.get(atf).add(root.getImageFrameTable().get(anim.img.gid, anim.row, anim.col, i));
						}
					}
				}
			}
		}

		HashMap<AtfParam, ImageFrame[]> atf_frameArray = new HashMap<AtfParam, ImageFrame[]>();
		for (AtfParam key : atf_frameset.keySet())
		{
			HashSet<ImageFrame> value = atf_frameset.get(key);
			ImageFrame[] valueArray = value.toArray(new ImageFrame[value.size()]);
			if (!activate(key, valueArray))
			{
				atf_frameArray.put(key, valueArray);
			}
		}

		for (AtfParam param : atf_frameArray.keySet())
		{
			try
			{
				MaxRects packer = new MaxRects(param.width, param.height, false);
				for (ImageFrame frame : atf_frameArray.get(param))
				{
					packer.push(frame, frame.clipW, frame.clipH);
				}
				packer.pack();

				ArrayList<AtlasRect[]> textures = new ArrayList<AtlasRect[]>();

				ArrayList<RectSet> rectSets = packer.getRectSets();
				for (int i = 0; i < rectSets.size(); i++)
				{
					RectSet rectSet = rectSets.get(i);

					ArrayList<AtlasRect> list = new ArrayList<AtlasRect>();
					for (Rect rect : rectSet.getRects())
					{
						list.add(new AtlasRect((ImageFrame) rect.data, rect.x, rect.y));
					}

					AtlasRect[] rectList = list.toArray(new AtlasRect[list.size()]);
					textures.add(rectList);
					allRectList.add(rectList);
					rectList_atf.put(rectList, param);
				}
				atf_rectListSet.put(param, textures);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	// -----------------------------------------------------------------------------------------------------
	//
	// 图像输出
	//
	// -----------------------------------------------------------------------------------------------------

	/**
	 * 获取下一个
	 * 
	 * @return
	 */
	private synchronized AtlasRect[] getNext()
	{
		AtlasRect[] result = null;
		if (nextIndex < allRectList.size())
		{
			result = allRectList.get(nextIndex);
			lastLog = "贴图输出(" + nextIndex + "/" + allRectList.size() + "):" + rectList_atf.get(result).id + "(图像X" + result.length + ")";
			nextIndex++;
		}

		return result;
	}

	/**
	 * 完成
	 */
	private synchronized void finish(AtlasRect[] rects, Atlas atlas)
	{
		rectList_atlas.put(atlas.rects, atlas);
		finishedCount++;
	}

	/**
	 * 是否已经完成
	 * 
	 * @return
	 */
	private synchronized boolean isFinished()
	{
		return finishedCount >= allRectList.size();
	}

	/**
	 * 输出所有ATF
	 */
	private void writeAllAtf()
	{
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
						AtlasRect[] next = getNext();
						if (next == null || root.isCancel())
						{
							break;
						}

						finish(next, writeATF(rectList_atf.get(next), next));
					}
				}
			});
		}

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
		exec.shutdown();
	}

	/**
	 * 输出ATF
	 * 
	 * @param rects
	 */
	private Atlas writeATF(AtfParam param, AtlasRect[] rects)
	{
		// 把属于同一个图像文件的帧排到一起，减少绘制图像时切换图像的次数。
		Arrays.sort(rects, new Comparator<AtlasRect>()
		{
			@Override
			public int compare(AtlasRect o1, AtlasRect o2)
			{
				return o1.frame.file.url.compareTo(o2.frame.file.url);
			}
		});

		// 确定横向和纵向上满足2的次方并且长度最小的尺寸
		int outputW = 0;
		int outputH = 0;
		for (AtlasRect rect : rects)
		{
			outputW = Math.max(outputW, rect.x + rect.frame.clipW);
			outputH = Math.max(outputH, rect.y + rect.frame.clipH);
		}
		outputW = TextureHelper.normalizeWH(outputW);
		outputH = TextureHelper.normalizeWH(outputH);

		// 确定文件输出位置
		String saveURL = root.getWriteFileTable().getNextExportFile();
		String pngURL = saveURL + ".png";
		String atfURL = saveURL + ".atf";
		File pngFile = new File(root.getOutputFolder().getPath() + pngURL);
		File atfFile = new File(root.getOutputFolder().getPath() + atfURL);

		// 合并图像
		File subFile = null;
		BufferedImage subImage = null;
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

			graphics.drawImage(subImage, drawX, drawY, drawX + frame.clipW, drawY + frame.clipH, frame.frameX + frame.clipX, frame.frameY + frame.clipY, frame.frameX + frame.clipX + frame.clipW, frame.frameY + frame.clipY + frame.clipH, null);

			if (root.isCancel())
			{
				return null;
			}
		}
		graphics.dispose();

		// 确定XML配置
		StringBuilder atlas = new StringBuilder();
		atlas.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		atlas.append("<TextureAtlas imagePath=\"" + ("/" + root.getOutputFolder().getName() + atfURL) + "\">\n");
		for (AtlasRect rect : rects)
		{
			ImageFrame frame = rect.frame;
			atlas.append("\t<SubTexture name=\"" + frame.file.gid + "_" + frame.row + "_" + frame.col + "_" + frame.index + "\" x=\"" + rect.x + "\" y=\"" + rect.y + "\" width=\"" + frame.clipW + "\" height=\"" + frame.clipH + "\" frameX=\"" + (frame.clipX > 0 ? -frame.clipX : 0) + "\" frameY=\"" + (frame.clipY > 0 ? -frame.clipY : 0) + "\" frameWidth=\"" + frame.frameW + "\" frameHeight=\"" + frame.frameH + "\"/>\n");
		}
		atlas.append("</TextureAtlas>");

		// 输出ATF,组合XML配置
		try
		{
			pngFile.getParentFile().mkdirs();
			
			ImageIO.write(image, "png", pngFile);
			TextureHelper.png2atf(pngFile, atfFile, param.other);

			RandomAccessFile a = new RandomAccessFile(atfFile, "rw");
			a.seek(atfFile.length());
			a.write(atlas.toString().getBytes("utf8"));
			a.close();

			if (pngFile.exists())
			{
				pngFile.delete();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		// 记录
		return new Atlas(param, atfURL, rects);
	}

	// -------------------------------------------------------------------------------------------------------------------
	//
	// 版本信息
	//
	// -------------------------------------------------------------------------------------------------------------------

	/**
	 * 激活旧的
	 * 
	 * @param param
	 * @param frames
	 * @return
	 */
	private boolean activate(AtfParam param, ImageFrame[] frames)
	{
		String key = AtlasSet.createKey(param, frames);
		if (oldTable.containsKey(key))
		{
			newTable.put(key, oldTable.get(key));
			oldTable.remove(key);
		}
		return newTable.containsKey(key);
	}

	/**
	 * 添加
	 * 
	 * @param set
	 * @return
	 */
	private void add(AtlasSet set)
	{
		newTable.put(set.key, set);
	}

	/**
	 * 获取版本文件
	 * 
	 * @return
	 */
	private File getVerFile()
	{
		return new File(root.getOutputFolder().getPath() + File.separatorChar + ".ver" + File.separatorChar + "3dAtlas");
	}

	/**
	 * 打开版本信息
	 */
	private void openVer()
	{
		this.oldTable = new HashMap<String, AtlasSet>();
		this.newTable = new HashMap<String, AtlasSet>();

		if (!getVerFile().exists())
		{
			return;
		}

		String text = "";

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
			line = line.trim();
			if (line.isEmpty())
			{
				continue;
			}

			String[] values = line.split("=");
			if (values.length != 2)
			{
				continue;
			}

			// ATFParam:w\+h\+-c d -r
			String[] paramItems = values[0].trim().split("\\+");
			AtfParam param = root.getAtfParamTable().findAtfParam(Integer.parseInt(paramItems[0].trim()), Integer.parseInt(paramItems[1].trim()), paramItems[2].trim());
			if (param == null)
			{
				continue;
			}

			// RECTS:(url(,gid_row_col_index_x_y)+)(\+url(,gid_row_col_index_x_y)+)*
			ArrayList<Atlas> atlas = new ArrayList<Atlas>();
			String[] atlasItems = values[1].trim().split("\\+");
			for (String atlasItem : atlasItems)
			{
				String[] atlasValues = atlasItem.trim().split(",");

				String url = atlasValues[0].trim();
				if (url.isEmpty())
				{
					atlas = null;
					break;
				}

				ArrayList<AtlasRect> rects = new ArrayList<AtlasRect>();
				for (int i = 1; i < atlasValues.length; i++)
				{
					String[] rectValues = atlasValues[i].trim().split("_");
					if (rectValues.length != 6)
					{
						rects = null;
						break;
					}

					ImageFrame frame = root.getImageFrameTable().get(rectValues[0].trim(), Integer.parseInt(rectValues[1].trim()), Integer.parseInt(rectValues[2].trim()), Integer.parseInt(rectValues[3].trim()));
					if (frame == null)
					{
						rects = null;
						break;
					}

					try
					{
						int placeX = Integer.parseInt(rectValues[4].trim());
						int placeY = Integer.parseInt(rectValues[5].trim());

						rects.add(new AtlasRect(frame, placeX, placeY));
					}
					catch (Error error)
					{
						rects = null;
						break;
					}
				}

				if (rects == null)
				{
					atlas = null;
					break;
				}

				atlas.add(new Atlas(param, url, rects.toArray(new AtlasRect[rects.size()])));
			}

			if (atlas == null)
			{
				continue;
			}

			AtlasSet atlasSet = new AtlasSet(param, atlas.toArray(new Atlas[atlas.size()]));
			oldTable.put(atlasSet.key, atlasSet);
		}
	}

	/**
	 * 保存版本信息
	 */
	public void saveVer()
	{
		StringBuilder output = new StringBuilder();

		if (newTable != null)
		{
			AtlasSet[] atlasSet = newTable.values().toArray(new AtlasSet[newTable.size()]);
			Arrays.sort(atlasSet, new Comparator<AtlasSet>()
			{
				@Override
				public int compare(AtlasSet o1, AtlasSet o2)
				{
					return o1.key.compareTo(o2.key);
				}
			});

			for (int i = 0; i < atlasSet.length; i++)
			{
				AtlasSet set = atlasSet[i];

				output.append(set.atfParam.width + "+" + set.atfParam.height + "+" + set.atfParam.other);
				output.append(" = ");

				for (int j = 0; j < set.atlasList.length; j++)
				{
					if (j > 0)
					{
						output.append("+");
					}

					Atlas atlas = set.atlasList[j];
					output.append(atlas.atfURL);
					for (AtlasRect rect : atlas.rects)
					{
						output.append(",");
						output.append(rect.frame.file.gid + "_" + rect.frame.row + "_" + rect.frame.col + "_" + rect.frame.index + "_" + rect.x + "_" + rect.y);
					}
				}
				if(i<atlasSet.length-1)
				{
					output.append("\n");
				}
			}
		}

		try
		{
			FileUtil.writeFile(getVerFile(), output.toString().getBytes("utf8"));
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}
}
