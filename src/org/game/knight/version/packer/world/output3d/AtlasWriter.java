package org.game.knight.version.packer.world.output3d;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.world.BaseWriter;
import org.game.knight.version.packer.world.WorldWriter;
import org.game.knight.version.packer.world.model.AtfParam;
import org.game.knight.version.packer.world.model.Attire;
import org.game.knight.version.packer.world.model.AttireAction;
import org.game.knight.version.packer.world.model.AttireAnim;
import org.game.knight.version.packer.world.model.ImageFrame;
import org.game.knight.version.packer.world.output3d.TextureRectPacker.Rect;
import org.game.knight.version.packer.world.output3d.TextureRectPacker.RectSet;

public class AtlasWriter extends BaseWriter
{
	private HashMap<ParamKey, ArrayList<AtlasRect[]>> atf_rectListSet;
	private ArrayList<AtlasRect[]> allRectList;
	private HashMap<AtlasRect[], ParamKey> rectList_atf;
	private HashMap<AtlasRect[], Atlas> rectList_atlas;

	private HashMap<String, AtlasSet> newTable = new HashMap<String, AtlasSet>();
	private HashMap<String, AtlasSet> oldTable = new HashMap<String, AtlasSet>();

	private HashMap<ImageFrame, Atlas> frame_atlas;

	private int nextIndex;
	private int finishedCount;
	private String lastLog;

	/**
	 * 构造函数
	 * 
	 * @param root
	 */
	public AtlasWriter(WorldWriter root)
	{
		super(root, "3dAtlas");
	}

	/**
	 * 查找指定图像帧所分配到的纹理文件
	 * 
	 * @param frame
	 * @return
	 */
	public Atlas findAtlasByImageFrame(ImageFrame frame)
	{
		return frame_atlas.get(frame);
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
			lastLog = "纹理集输出(" + nextIndex + "/" + allRectList.size() + "):" + rectList_atf.get(result).param.id + "(图像*" + result.length + ")";
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

	@Override
	protected void startup() throws Exception
	{
		GamePacker.log("开始输出纹理集(3D)");
	}

	@Override
	protected void exec() throws Exception
	{
		atf_rectListSet = new HashMap<ParamKey, ArrayList<AtlasRect[]>>();
		allRectList = new ArrayList<AtlasRect[]>();
		rectList_atf = new HashMap<AtlasRect[], ParamKey>();
		rectList_atlas = new HashMap<AtlasRect[], Atlas>();

		filterAtfGroup();

		if (root.isCancel())
		{
			return;
		}

		writeAllAtf();

		if (root.isCancel())
		{
			return;
		}

		for (ParamKey key : atf_rectListSet.keySet())
		{
			ArrayList<AtlasRect[]> value = atf_rectListSet.get(key);
			Atlas[] atlasList = new Atlas[value.size()];
			for (int i = 0; i < value.size(); i++)
			{
				atlasList[i] = rectList_atlas.get(value.get(i));
			}

			add(new AtlasSet(key.isAnim, key.param, atlasList));
		}

		if (root.isCancel())
		{
			return;
		}

		frame_atlas = new HashMap<ImageFrame, Atlas>();
		for (AtlasSet set : newTable.values())
		{
			for (Atlas atlas : set.atlasList)
			{
				for (AtlasRect rect : atlas.rects)
				{
					frame_atlas.put(rect.frame, atlas);
				}
			}
		}
	}

	/**
	 * 输出所有ATF
	 * 
	 * @throws Exception
	 */
	private void writeAllAtf() throws Exception
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

						try
						{
							ParamKey key = rectList_atf.get(next);
							Atlas atlas = writeATF(key.param, next);
							if (atlas != null)
							{
								finish(next, atlas);
							}
						}
						catch (Exception e)
						{
							root.cancel(e);
						}
					}
				}
			});
		}

		while (!root.isCancel() && !isFinished())
		{
			GamePacker.progress(lastLog);
			Thread.sleep(50);
		}

		exec.shutdown();
	}

	/**
	 * 输出ATF
	 * 
	 * @param rects
	 * @throws Exception
	 */
	private Atlas writeATF(AtfParam param, AtlasRect[] rects) throws Exception
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
		int maxX = 0;
		int maxY = 0;
		for (AtlasRect rect : rects)
		{
			maxX = Math.max(maxX, rect.x + rect.frame.clipW);
			maxY = Math.max(maxY, rect.y + rect.frame.clipH);
		}
		int SCALE_SIZE = 10;

		// 合并图像
		File subFile = null;
		BufferedImage subImage = null;
		BufferedImage imageA = new BufferedImage(TextureHelper.normalizeWH(maxX), TextureHelper.normalizeWH(maxY), BufferedImage.TYPE_INT_ARGB);
		BufferedImage imageB = new BufferedImage(TextureHelper.normalizeWH(maxX / SCALE_SIZE), TextureHelper.normalizeWH(maxY / SCALE_SIZE), BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphicsA = (Graphics2D) imageA.getGraphics();
		Graphics2D graphicsB = (Graphics2D) imageB.getGraphics();
		for (AtlasRect rect : rects)
		{
			if (rect.frame.file != subFile)
			{
				subFile = rect.frame.file;
				subImage = ImageIO.read(subFile);
			}

			ImageFrame frame = rect.frame;

			int drawL = rect.x;
			int drawT = rect.y;
			int drawR = drawL + frame.clipW;
			int drawB = drawT + frame.clipH;

			int fromX = frame.frameX + frame.clipX;
			int fromY = frame.frameY + frame.clipY;
			int fromR = frame.frameX + frame.clipX + frame.clipW;
			int fromB = frame.frameY + frame.clipY + frame.clipH;

			graphicsA.drawImage(subImage, drawL, drawT, drawR, drawB, fromX, fromY, fromR, fromB, null);

			drawL = (int) Math.floor(drawL / 10);
			drawT = (int) Math.floor(drawT / 10);
			drawR = (int) Math.floor(drawR / 10);
			drawB = (int) Math.floor(drawB / 10);

			graphicsB.drawImage(subImage, drawL, drawT, drawR, drawB, fromX, fromY, fromR, fromB, null);

			if (root.isCancel())
			{
				return null;
			}
		}
		graphicsA.dispose();
		graphicsB.dispose();

		String saveURL = root.optionTable.getNextExportFile();

		String url1 = writerAtfImage(rects, param, imageA, saveURL);
		String url2 = writerAtfPreview(rects, param, imageB, saveURL);

		return new Atlas(rects, param, url1, url2);
	}

	/**
	 * 输出原版ATF
	 * 
	 * @param rects
	 * @param param
	 * @param image
	 * @param saveURL
	 * @return
	 * @throws Exception
	 */
	private String writerAtfImage(AtlasRect[] rects, AtfParam param, BufferedImage image, String saveURL) throws Exception
	{
		// 确定文件输出位置
		String pngURL = saveURL + ".png";
		String atfURL = saveURL + ".atf";
		File pngFile = new File(root.getOutputFolder().getPath() + pngURL);
		File atfFile = new File(root.getOutputFolder().getPath() + atfURL);

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
		pngFile.getParentFile().mkdirs();

		ImageIO.write(image, "png", pngFile);
		TextureHelper.png2atf(pngFile, atfFile, param.other);

		byte[] xmlBytes = atlas.toString().getBytes("utf8");

		RandomAccessFile a = new RandomAccessFile(atfFile, "rw");
		a.seek(atfFile.length());
		a.write(xmlBytes);
		a.write((xmlBytes.length >>> 24) & 0xFF);
		a.write((xmlBytes.length >>> 16) & 0xFF);
		a.write((xmlBytes.length >>> 8) & 0xFF);
		a.write(xmlBytes.length & 0xFF);
		a.close();

		root.addFileSuffix(atfFile);

		if (pngFile.exists())
		{
			pngFile.delete();
		}

		return atfURL;
	}

	/**
	 * 输出小版ATF
	 * 
	 * @param image
	 */
	private String writerAtfPreview(AtlasRect[] rects, AtfParam param, BufferedImage image, String saveURL) throws Exception
	{
		// 确定文件输出位置
		String pngURL = saveURL + "_0.png";
		String atfURL = saveURL + "_0.atf";
		File pngFile = new File(root.getOutputFolder().getPath() + pngURL);
		File atfFile = new File(root.getOutputFolder().getPath() + atfURL);

		// 确定XML配置
		StringBuilder atlas = new StringBuilder();
		atlas.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		atlas.append("<TextureAtlas imagePath=\"" + ("/" + root.getOutputFolder().getName() + atfURL) + "\">\n");
		for (AtlasRect rect : rects)
		{
			ImageFrame frame = rect.frame;

			int drawL = rect.x;
			int drawT = rect.y;
			int drawR = drawL + frame.clipW;
			int drawB = drawT + frame.clipH;

			drawL = (int) Math.floor(drawL / 10);
			drawT = (int) Math.floor(drawT / 10);
			drawR = (int) Math.floor(drawR / 10);
			drawB = (int) Math.floor(drawB / 10);

			int drawW = drawR - drawL;
			int drawH = drawB - drawT;

			atlas.append("\t<SubTexture name=\"" + frame.file.gid + "_" + frame.row + "_" + frame.col + "_" + frame.index + "\" x=\"" + drawL + "\" y=\"" + drawT + "\" width=\"" + drawW + "\" height=\"" + drawH + "\" frameX=\"" + (frame.clipX > 0 ? -frame.clipX : 0) / 10 + "\" frameY=\"" + (frame.clipY > 0 ? -frame.clipY : 0) / 10 + "\" frameWidth=\"" + frame.frameW / 10 + "\" frameHeight=\"" + frame.frameH / 10 + "\"/>\n");
		}
		atlas.append("</TextureAtlas>");

		// 输出ATF,组合XML配置
		pngFile.getParentFile().mkdirs();

		ImageIO.write(image, "png", pngFile);
		TextureHelper.png2atf(pngFile, atfFile, param.other);

		byte[] xmlBytes = atlas.toString().getBytes("utf8");

		RandomAccessFile a = new RandomAccessFile(atfFile, "rw");
		a.seek(atfFile.length());
		a.write(xmlBytes);
		a.write((xmlBytes.length >>> 24) & 0xFF);
		a.write((xmlBytes.length >>> 16) & 0xFF);
		a.write((xmlBytes.length >>> 8) & 0xFF);
		a.write(xmlBytes.length & 0xFF);
		a.close();

		root.addFileSuffix(atfFile);

		if (pngFile.exists())
		{
			pngFile.delete();
		}

		return atfURL;
	}

	/**
	 * 过滤ATF组
	 * 
	 * @return
	 * @throws Exception
	 */
	private void filterAtfGroup() throws Exception
	{
		HashMap<ParamKey, HashSet<ImageFrame>> atf_frameset = new HashMap<ParamKey, HashSet<ImageFrame>>();
		for (Attire attire : root.attireTable.getAllAttire())
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
							ParamKey key = attire.isAnimAttire() ? ParamKey.getAnimKey(atf) : ParamKey.getAttireKey(atf);

							if (!atf_frameset.containsKey(key))
							{
								atf_frameset.put(key, new HashSet<ImageFrame>());
							}

							atf_frameset.get(key).add(root.frameTable.get(anim.img.gid, anim.row, anim.col, i));
						}
					}
				}
			}
		}

		HashMap<ParamKey, ImageFrame[]> atf_frameArray = new HashMap<ParamKey, ImageFrame[]>();
		for (ParamKey key : atf_frameset.keySet())
		{
			HashSet<ImageFrame> value = atf_frameset.get(key);
			ImageFrame[] valueArray = value.toArray(new ImageFrame[value.size()]);
			if (!activate(key, valueArray))
			{
				atf_frameArray.put(key, valueArray);
			}
		}

		for (ParamKey param : atf_frameArray.keySet())
		{
			TextureRectPacker packer = new TextureRectPacker(param.param.width, param.param.height, false);
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
	}

	private static class ParamKey
	{
		public final boolean isAnim;
		public final AtfParam param;

		public ParamKey(boolean isAnim, AtfParam param)
		{
			this.isAnim = isAnim;
			this.param = param;
		}

		private static HashMap<AtfParam, ParamKey> keys1 = new HashMap<AtfParam, ParamKey>();
		private static HashMap<AtfParam, ParamKey> keys2 = new HashMap<AtfParam, ParamKey>();

		public static ParamKey getAnimKey(AtfParam param)
		{
			if (!keys1.containsKey(param))
			{
				keys1.put(param, new ParamKey(true, param));
			}
			return keys1.get(param);
		}

		public static ParamKey getAttireKey(AtfParam param)
		{
			if (!keys2.containsKey(param))
			{
				keys2.put(param, new ParamKey(true, param));
			}
			return keys2.get(param);
		}
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
	private boolean activate(ParamKey param, ImageFrame[] frames)
	{
		String key = AtlasSet.createKey(param.isAnim, param.param, frames);
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
			boolean anim = paramItems[0].equals("1");
			AtfParam param = root.atfParamTable.findAtfParam(Integer.parseInt(paramItems[1].trim()), Integer.parseInt(paramItems[2].trim()), paramItems[3].trim());
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

				String atfURL = atlasValues[0].trim();
				if (atfURL.isEmpty())
				{
					atlas = null;
					break;
				}

				String previewURL = atlasValues[1].trim();
				if (previewURL.isEmpty())
				{
					atlas = null;
					break;
				}

				ArrayList<AtlasRect> rects = new ArrayList<AtlasRect>();
				for (int i = 2; i < atlasValues.length; i++)
				{
					String[] rectValues = atlasValues[i].trim().split("_");
					if (rectValues.length != 6)
					{
						rects = null;
						break;
					}

					ImageFrame frame = root.frameTable.get(rectValues[0].trim(), Integer.parseInt(rectValues[1].trim()), Integer.parseInt(rectValues[2].trim()), Integer.parseInt(rectValues[3].trim()));
					if (frame == null)
					{
						rects = null;
						break;
					}

					int placeX = Integer.parseInt(rectValues[4].trim());
					int placeY = Integer.parseInt(rectValues[5].trim());

					rects.add(new AtlasRect(frame, placeX, placeY));
				}

				if (rects == null)
				{
					atlas = null;
					break;
				}

				atlas.add(new Atlas(rects.toArray(new AtlasRect[rects.size()]), param, atfURL, previewURL));
			}

			if (atlas == null)
			{
				continue;
			}

			AtlasSet atlasSet = new AtlasSet(anim, param, atlas.toArray(new Atlas[atlas.size()]));
			oldTable.put(atlasSet.key, atlasSet);
		}
	}

	@Override
	protected void saveHistory(BufferedWriter writer) throws Exception
	{
		// 排序
		AtlasSet[] atlasSet = newTable.values().toArray(new AtlasSet[newTable.size()]);
		Arrays.sort(atlasSet, new Comparator<AtlasSet>()
		{
			@Override
			public int compare(AtlasSet o1, AtlasSet o2)
			{
				return o1.key.compareTo(o2.key);
			}
		});

		// 写入
		for (int i = 0; i < atlasSet.length; i++)
		{
			AtlasSet set = atlasSet[i];

			writer.write((set.anim ? 1 : 2) + "+" + set.atfParam.width + "+" + set.atfParam.height + "+" + set.atfParam.other);
			writer.write(" = ");

			for (int j = 0; j < set.atlasList.length; j++)
			{
				if (j > 0)
				{
					writer.write("+");
				}

				Atlas atlas = set.atlasList[j];
				writer.write(atlas.atfURL);
				writer.write(",");
				writer.write(atlas.previewURL);
				for (AtlasRect rect : atlas.rects)
				{
					writer.write(",");
					writer.write(rect.frame.file.gid + "_" + rect.frame.row + "_" + rect.frame.col + "_" + rect.frame.index + "_" + rect.x + "_" + rect.y);
				}
			}
			if (i < atlasSet.length - 1)
			{
				writer.write("\n");
			}
		}

		// 记录输出文件
		for (AtlasSet set : newTable.values())
		{
			for (Atlas atlas : set.atlasList)
			{
				root.addOutputFile(atlas.atfURL);
				root.addOutputFile(atlas.previewURL);
			}
		}
	}
}
