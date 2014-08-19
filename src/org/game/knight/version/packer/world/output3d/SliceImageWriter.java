package org.game.knight.version.packer.world.output3d;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
import org.game.knight.version.packer.world.model.AttireAction;
import org.game.knight.version.packer.world.model.AttireAnim;
import org.game.knight.version.packer.world.model.ProjectFile;
import org.game.knight.version.packer.world.model.ProjectImgFile;
import org.game.knight.version.packer.world.model.Scene;
import org.game.knight.version.packer.world.model.SceneAnim;
import org.game.knight.version.packer.world.model.SceneBackLayer;
import org.game.knight.version.packer.world.task.RootTask;

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

	private RootTask root;
	private ProjectImgFile[] inputList;
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
	public SliceImageWriter(RootTask root)
	{
		this.root = root;
	}

	/**
	 * 获取已切分的图像
	 * 
	 * @param img
	 * @return
	 */
	public SliceImage getSliceImage(ProjectImgFile img)
	{
		return newTable.get(createKey(img));
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
						ProjectImgFile file = getNextFile();
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
	private synchronized ProjectImgFile getNextFile()
	{
		ProjectImgFile result = null;
		if (nextIndex < inputList.length)
		{
			result = inputList[nextIndex];
			lastLog = "图像切片(" + nextIndex + "/" + inputList.length + ")：" + result.url;
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
	private synchronized void finishFile(ProjectImgFile img, SliceImage slice)
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
	 * @param file
	 * @return
	 */
	private SliceImage sliceImage(ProjectImgFile file)
	{
		try
		{
			BufferedImage nativeIMG = ImageIO.read(file);
			int row = (int) Math.ceil((double) nativeIMG.getHeight() / SLICE_SIZE);
			int col = (int) Math.ceil((double) nativeIMG.getWidth() / SLICE_SIZE);

			String previewURL = null;
			synchronized (root.getGlobalOptionTable())
			{
				previewURL = root.getGlobalOptionTable().getNextExportFile();
				for (int i = 0; i < row * col; i++)
				{
					root.getGlobalOptionTable().getNextExportFile();
				}
			}

			int previewW = (int) (nativeIMG.getWidth() / PREVIEW_SCALE);
			int previewH = (int) (nativeIMG.getHeight() / PREVIEW_SCALE);
			previewW = TextureHelper.normalizeWH(previewW);
			previewH = TextureHelper.normalizeWH(previewH);

			BufferedImage previewIMG = new BufferedImage(previewW, previewH, BufferedImage.TYPE_INT_ARGB);
			Graphics2D previewGS = (Graphics2D) previewIMG.getGraphics();
			previewGS.drawImage(nativeIMG, 0, 0, previewIMG.getWidth(), previewIMG.getHeight(), 0, 0, nativeIMG.getWidth(), nativeIMG.getHeight(), null);
			previewGS.dispose();

			File previewATF = new File(root.getOutputFolder().getPath() + previewURL + ".atf");
			File previewPNG = new File(root.getOutputFolder().getPath() + previewURL + ".png");
			previewPNG.getParentFile().mkdirs();

			ImageIO.write(previewIMG, "png", previewPNG);
			TextureHelper.png2atf(previewPNG, previewATF);

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
					int subX = j * SLICE_SIZE;
					int subY = i * SLICE_SIZE;
					int subW = TextureHelper.normalizeWH(Math.min(SLICE_SIZE, nativeIMG.getWidth() - subX));
					int subH = TextureHelper.normalizeWH(Math.min(SLICE_SIZE, nativeIMG.getHeight() - subY));
					int drawW = Math.min(SLICE_SIZE, nativeIMG.getWidth() - subX);
					int drawH = Math.min(SLICE_SIZE, nativeIMG.getHeight() - subY);

					BufferedImage subIMG = new BufferedImage(subW, subH, BufferedImage.TYPE_INT_ARGB);
					Graphics2D subIGS = (Graphics2D) subIMG.getGraphics();
					subIGS.drawImage(nativeIMG, 0, 0, drawW, drawH, subX, subY, subX + drawW, subY + drawH, null);
					subIGS.dispose();

					File subATF = new File(root.getOutputFolder().getPath() + previewURL + "_" + index + ".atf");
					File subPNG = new File(root.getOutputFolder().getPath() + previewURL + "_" + index + ".png");
					subPNG.getParentFile().mkdirs();

					ImageIO.write(subIMG, "png", subPNG);

					TextureHelper.png2atf(subPNG, subATF);

					if (subPNG.exists())
					{
						subPNG.delete();
					}

					sliceURLs.add(previewURL + "_" + index + ".atf");
					index++;
				}
			}

			return new SliceImage(file, previewURL + ".atf", row, col, sliceURLs.toArray(new String[sliceURLs.size()]));
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
	private ProjectImgFile[] filterSliceImage()
	{
		HashSet<ProjectImgFile> imgFiles = new HashSet<ProjectImgFile>();

		Scene[] scenes = root.getWorldTable().getAllScene();
		for (Scene scene : scenes)
		{
			for (SceneBackLayer layer : scene.backLayers)
			{
				if (!activate(layer.img.imgFile))
				{
					imgFiles.add(layer.img.imgFile);
				}
			}
			for (SceneAnim role : scene.backAnims)
			{
				if (role.attire == null)
				{
					continue;
				}

				for (AttireAction action : role.attire.actions)
				{
					for (AttireAnim anim : action.anims)
					{
						if (!activate(anim.img))
						{
							imgFiles.add(anim.img);
						}
					}
				}
			}
		}

		return imgFiles.toArray(new ProjectImgFile[imgFiles.size()]);
	}

	// ------------------------------------------------------------------------------------------------------------
	//
	// 版本信息操作
	//
	// ------------------------------------------------------------------------------------------------------------

	/**
	 * 生成版本信息中所用的KEY
	 * 
	 * @param img
	 * @return
	 */
	private String createKey(ProjectImgFile img)
	{
		return img.gid + "_" + PREVIEW_SCALE + "_" + SLICE_SIZE;
	}

	/**
	 * 激活历史版本中的导出项
	 * 
	 * @param img
	 * @return
	 */
	private boolean activate(ProjectImgFile img)
	{
		String key = createKey(img);

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
	private void add(ProjectImgFile img, SliceImage slice)
	{
		String key = createKey(img);

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
					String[] keys = values[0].split("_");
					String[] params = values[1].trim().split(",");

					if (keys.length == 3)
					{
						String gid = keys[0].trim();
						String scale = keys[1].trim();
						String sliceSize = keys[2].trim();

						ProjectFile img = root.getFileTable().getFileByGID(gid);

						if (img != null && img instanceof ProjectImgFile && params.length > 3)
						{
							String previewURL = params[0].trim();
							int sliceRow = Integer.parseInt(params[1].trim());
							int sliceCol = Integer.parseInt(params[2].trim());
							String[] sliceURLs = new String[params.length - 3];
							for (int i = 3; i < params.length; i++)
							{
								sliceURLs[i - 3] = params[i];
							}
							oldTable.put(gid + "_" + scale + "_" + sliceSize, new SliceImage((ProjectImgFile) img, previewURL, sliceRow, sliceCol, sliceURLs));
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
					int val1 = Integer.parseInt(arg0.substring(0, arg0.indexOf("_")));
					int val2 = Integer.parseInt(arg1.substring(0, arg1.indexOf("_")));
					return val1 - val2;
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

		//记录输出文件
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
