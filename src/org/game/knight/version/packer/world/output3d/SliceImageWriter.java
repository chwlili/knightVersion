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
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.world.WorldWriter;
import org.game.knight.version.packer.world.model.ImageFrame;
import org.game.knight.version.packer.world.model.Scene;
import org.game.knight.version.packer.world.model.SceneBackLayer;

public class SliceImageWriter
{
	/**
	 * 生成缩略图所用的除数
	 */
	private static final int PREVIEW_SCALE = 10;

	/**
	 * 切片所用的规格
	 */
	private static final int SLICE_SIZE = 256;

	private WorldWriter root;
	private ImageFrame[] inputList;
	private int nextIndex;
	private int finishedCount;

	private String lastLog;

	private HashMap<String, SliceImage> oldTable = new HashMap<String, SliceImage>();
	private HashMap<String, SliceImage> newTable = new HashMap<String, SliceImage>();

	/**
	 * 构造函数
	 * 
	 * @param root
	 */
	public SliceImageWriter(WorldWriter root)
	{
		this.root = root;
	}

	/**
	 * 获取已切分的图像
	 * 
	 * @param img
	 * @return
	 */
	public SliceImage getSliceImage(ImageFrame frame)
	{
		return newTable.get(createKey(frame));
	}

	/**
	 * 开始
	 */
	public void start()
	{
		openVer();

		inputList = filterSliceImage();

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
						ImageFrame file = getNextFile();
						if (file == null || root.isCancel())
						{
							break;
						}
						finishFile(file, sliceImage(file));
					}
				}
			});
		}

		while (!root.isCancel() && !isFinished())
		{
			try
			{
				GamePacker.progress(lastLog);
				Thread.sleep(500);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		exec.shutdown();
	}

	/**
	 * 获取下一个需要切片的文件
	 * 
	 * @return
	 */
	private synchronized ImageFrame getNextFile()
	{
		ImageFrame result = null;
		if (nextIndex < inputList.length)
		{
			result = inputList[nextIndex];
			lastLog = "图像切片(" + nextIndex + "/" + inputList.length + ")：" + result.file.url;
			nextIndex++;
		}
		return result;
	}

	/**
	 * 完成
	 * 
	 * @param img
	 * @param slice
	 */
	private synchronized void finishFile(ImageFrame img, SliceImage slice)
	{
		if (img != null && slice != null)
		{
			add(img, slice);
		}

		finishedCount++;
	}

	/**
	 * 是否已经完成
	 * 
	 * @return
	 */
	private synchronized boolean isFinished()
	{
		return finishedCount >= inputList.length;
	}

	/**
	 * 切片图像
	 * 
	 * @param frame
	 * @return
	 */
	private SliceImage sliceImage(ImageFrame frame)
	{
		try
		{
			BufferedImage nativeIMG = ImageIO.read(frame.file);
			int row = (int) Math.ceil((double) frame.clipH / SLICE_SIZE);
			int col = (int) Math.ceil((double) frame.clipW / SLICE_SIZE);

			String previewURL = null;
			synchronized (root.getGlobalOptionTable())
			{
				previewURL = root.getGlobalOptionTable().getNextExportFile();
				for (int i = 0; i < row * col; i++)
				{
					root.getGlobalOptionTable().getNextExportFile();
				}
			}

			int previewW = (int) (frame.clipW / PREVIEW_SCALE);
			int previewH = (int) (frame.clipH / PREVIEW_SCALE);
			previewW = TextureHelper.normalizeWH(previewW);
			previewH = TextureHelper.normalizeWH(previewH);

			BufferedImage previewIMG = new BufferedImage(previewW, previewH, BufferedImage.TYPE_INT_ARGB);
			Graphics2D previewGS = (Graphics2D) previewIMG.getGraphics();
			previewGS.drawImage(nativeIMG, 0, 0, previewIMG.getWidth(), previewIMG.getHeight(), frame.frameX + frame.clipX, frame.frameY + frame.clipY, frame.frameX + frame.clipX + frame.clipW, frame.frameY + frame.clipY + frame.clipH, null);
			previewGS.dispose();

			File previewATF = new File(root.getOutputFolder().getPath() + previewURL + ".atf");
			File previewPNG = new File(root.getOutputFolder().getPath() + previewURL + ".png");
			previewPNG.getParentFile().mkdirs();

			StringBuilder previewAtlas = new StringBuilder();
			previewAtlas.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			previewAtlas.append("<TextureAtlas imagePath=\"" + ("/" + root.getOutputFolder().getName() + previewURL) + ".atf\">\n");
			previewAtlas.append("\t<SubTexture name=\"def\" x=\"0\" y=\"0\" width=\"" + previewW + "\" height=\"" + previewH + "\" frameX=\"0\" frameY=\"0\" frameWidth=\"" + previewW + "\" frameHeight=\"" + previewH + "\"/>\n");
			previewAtlas.append("</TextureAtlas>");

			ImageIO.write(previewIMG, "png", previewPNG);
			TextureHelper.png2atf(previewPNG, previewATF);

			byte[] previewAtlasCfg = previewAtlas.toString().getBytes("utf8");

			RandomAccessFile a = new RandomAccessFile(previewATF, "rw");
			a.seek(previewATF.length());
			a.write(previewAtlasCfg);
			a.write((previewAtlasCfg.length >>> 24) & 0xFF);
			a.write((previewAtlasCfg.length >>> 16) & 0xFF);
			a.write((previewAtlasCfg.length >>> 8) & 0xFF);
			a.write(previewAtlasCfg.length & 0xFF);
			a.close();

			root.addFileSuffix(previewATF);

			if (previewPNG.exists())
			{
				previewPNG.delete();
			}

			int index = 0;
			ArrayList<String> sliceURLs = new ArrayList<String>();
			for (int i = 0; i < row; i++)
			{
				for (int j = 0; j < col; j++)
				{
					int subX = frame.frameX + frame.clipX + j * SLICE_SIZE;
					int subY = frame.frameY + frame.clipY + i * SLICE_SIZE;
					int subW = TextureHelper.normalizeWH(Math.min(SLICE_SIZE, frame.frameX + frame.clipX + frame.clipW - subX));
					int subH = TextureHelper.normalizeWH(Math.min(SLICE_SIZE, frame.frameY + frame.clipY + frame.clipH - subY));
					int drawW = Math.min(SLICE_SIZE, frame.frameX + frame.clipX + frame.clipW - subX);
					int drawH = Math.min(SLICE_SIZE, frame.frameY + frame.clipY + frame.clipH - subY);

					BufferedImage subIMG = new BufferedImage(subW, subH, BufferedImage.TYPE_INT_ARGB);
					Graphics2D subIGS = (Graphics2D) subIMG.getGraphics();
					subIGS.drawImage(nativeIMG, 0, 0, drawW, drawH, subX, subY, subX + drawW, subY + drawH, null);
					subIGS.dispose();

					File subATF = new File(root.getOutputFolder().getPath() + previewURL + "_" + index + ".atf");
					File subPNG = new File(root.getOutputFolder().getPath() + previewURL + "_" + index + ".png");
					subPNG.getParentFile().mkdirs();

					StringBuilder subAtlas = new StringBuilder();
					subAtlas.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
					subAtlas.append("<TextureAtlas imagePath=\"" + ("/" + root.getOutputFolder().getName() + previewURL + "_" + index) + ".atf\">\n");
					subAtlas.append("\t<SubTexture name=\"def\" x=\"0\" y=\"0\" width=\"" + subW + "\" height=\"" + subH + "\" frameX=\"0\" frameY=\"0\" frameWidth=\"" + subW + "\" frameHeight=\"" + subH + "\"/>\n");
					subAtlas.append("</TextureAtlas>");

					ImageIO.write(subIMG, "png", subPNG);
					TextureHelper.png2atf(subPNG, subATF);

					byte[] subAtlasCfg = subAtlas.toString().getBytes("utf8");

					RandomAccessFile subAppender = new RandomAccessFile(subATF, "rw");
					subAppender.seek(subATF.length());
					subAppender.write(subAtlasCfg);
					subAppender.write((subAtlasCfg.length >>> 24) & 0xFF);
					subAppender.write((subAtlasCfg.length >>> 16) & 0xFF);
					subAppender.write((subAtlasCfg.length >>> 8) & 0xFF);
					subAppender.write(subAtlasCfg.length & 0xFF);
					subAppender.close();

					root.addFileSuffix(subATF);

					if (subPNG.exists())
					{
						subPNG.delete();
					}

					sliceURLs.add(previewURL + "_" + index + ".atf");
					index++;
				}
			}

			return new SliceImage(frame, previewURL + ".atf", row, col, sliceURLs.toArray(new String[sliceURLs.size()]));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	// --------------------------------------------------------------------------
	//
	// 过滤所有需要切片的图像
	//
	// --------------------------------------------------------------------------

	/**
	 * 过滤需要切片的图像文件
	 * 
	 * @return
	 */
	private ImageFrame[] filterSliceImage()
	{
		HashSet<ImageFrame> imgFiles = new HashSet<ImageFrame>();

		for (Scene scene : root.getWorldTable().getAllScene())
		{
			for (SceneBackLayer layer : scene.backLayers)
			{
				ImageFrame frame = root.getImageFrameTable().get(layer.img.imgFile.gid, 1, 1, 0);
				if (frame != null)
				{
					if (!activate(frame))
					{
						imgFiles.add(frame);
					}
				}
			}
		}

		return imgFiles.toArray(new ImageFrame[imgFiles.size()]);
	}

	// ------------------------------------------------------------------------------------------------------------
	//
	// 版本信息操作
	//
	// ------------------------------------------------------------------------------------------------------------

	/**
	 * 生成版本信息中所用的KEY
	 * 
	 * @param frame
	 * @return
	 */
	private String createKey(ImageFrame frame)
	{
		return "{scale:" + PREVIEW_SCALE + " , cell:" + SLICE_SIZE + "} + " + frame.file.gid + "_" + frame.row + "_" + frame.col + "_" + frame.index;
	}

	/**
	 * 激活历史版本中的导出项
	 * 
	 * @param img
	 * @return
	 */
	private boolean activate(ImageFrame frame)
	{
		String key = createKey(frame);

		if (oldTable.containsKey(key))
		{
			newTable.put(key, oldTable.get(key));
			oldTable.remove(key);
		}
		return newTable.containsKey(key);
	}

	/**
	 * 添加新的切片图像
	 * 
	 * @param img
	 * @param slice
	 */
	private void add(ImageFrame frame, SliceImage slice)
	{
		String key = createKey(frame);

		newTable.put(key, slice);
		oldTable.remove(key);
	}

	/**
	 * 获取版本文件
	 * 
	 * @return
	 */
	private File getVerFile()
	{
		return new File(root.getOutputFolder().getPath() + File.separatorChar + ".ver" + File.separatorChar + "3dSlice");
	}

	/**
	 * 打开版本信息
	 */
	private void openVer()
	{
		this.oldTable = new HashMap<String, SliceImage>();
		this.newTable = new HashMap<String, SliceImage>();

		if (!getVerFile().exists())
		{
			return;
		}

		try
		{
			String text = new String(FileUtil.getFileBytes(getVerFile()), "utf8");
			String[] lines = text.split("\\n");
			for (String line : lines)
			{
				line = line.trim();
				if (line.isEmpty())
				{
					continue;
				}

				String[] values = line.split("=");
				if (values.length == 2)
				{
					String[] keys = values[0].trim().split("\\+");
					String[] params = values[1].trim().split(",");

					if (keys.length == 2 && params.length > 3)
					{
						keys = keys[1].trim().split("_");
						if (keys.length == 4)
						{
							try
							{
								int gid = Integer.parseInt(keys[0].trim());
								int row = Integer.parseInt(keys[1].trim());
								int col = Integer.parseInt(keys[2].trim());
								int index = Integer.parseInt(keys[3].trim());

								ImageFrame frame = root.getImageFrameTable().get(gid + "", row, col, index);
								if (frame != null)
								{
									String previewURL = params[0].trim();
									int sliceRow = Integer.parseInt(params[1].trim());
									int sliceCol = Integer.parseInt(params[2].trim());
									String[] sliceURLs = new String[params.length - 3];
									for (int i = 3; i < params.length; i++)
									{
										sliceURLs[i - 3] = params[i];
									}

									oldTable.put(values[0].trim(), new SliceImage(frame, previewURL, sliceRow, sliceCol, sliceURLs));
								}
							}
							catch (NumberFormatException e)
							{
								// ..
							}
						}
					}
				}
			}
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
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
			String[] keys = newTable.keySet().toArray(new String[newTable.size()]);
			Arrays.sort(keys, new Comparator<String>()
			{
				@Override
				public int compare(String arg0, String arg1)
				{
					String val1 = arg0.substring(arg0.indexOf("+") + 1).trim();
					String val2 = arg1.substring(arg1.indexOf("+") + 1).trim();

					String[] items1 = val1.split("_");
					String[] items2 = val2.split("_");
					int length = Math.min(items1.length, items2.length);
					for (int i = 0; i < length; i++)
					{
						String item1 = items1[i].trim();
						String item2 = items2[i].trim();
						try
						{
							int int1 = Integer.parseInt(item1);
							int int2 = Integer.parseInt(item2);
							if (int1 != int2)
							{
								return int1 - int2;
							}
						}
						catch (NumberFormatException e)
						{
							if (!item1.endsWith(item2))
							{
								return item1.compareTo(item2);
							}
						}
					}
					return 0;
				}
			});

			for (int i = 0; i < keys.length; i++)
			{
				String key = keys[i];
				SliceImage img = newTable.get(key);

				output.append(key + " = " + img.previewURL + "," + img.sliceRow + "," + img.sliceCol + ",");
				for (int j = 0; j < img.sliceURLs.length; j++)
				{
					if (j > 0)
					{
						output.append(",");
					}
					output.append(img.sliceURLs[j]);
				}
				if (i < keys.length - 1)
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
			GamePacker.error(e);
			return;
		}

		// 记录输出文件
		for (SliceImage img : newTable.values())
		{
			root.addOutputFile(img.previewURL);
			for (String url : img.sliceURLs)
			{
				root.addOutputFile(url);
			}
		}
	}
}
