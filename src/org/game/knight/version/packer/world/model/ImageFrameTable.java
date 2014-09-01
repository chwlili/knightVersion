package org.game.knight.version.packer.world.model;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

import org.eclipse.swt.graphics.Rectangle;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.world.BaseWriter;
import org.game.knight.version.packer.world.WorldWriter;

public class ImageFrameTable extends BaseWriter
{
	private WorldWriter root;

	private GridImg[] inputList;
	private int nextIndex;
	private int finishedCount;
	private String lastLog = "";

	private HashMap<String, ImageFrame[]> oldTable = new HashMap<String, ImageFrame[]>();
	private HashMap<String, ImageFrame[]> newTable = new HashMap<String, ImageFrame[]>();

	/**
	 * 构造函数
	 * 
	 * @param root
	 */
	public ImageFrameTable(WorldWriter root)
	{
		super(root, "clip");
	}

	/**
	 * 获取帧信息
	 * 
	 * @param img
	 * @param row
	 * @param col
	 * @param index
	 * @return
	 */
	public ImageFrame get(String fileID, int row, int col, int index)
	{
		String key = fileID + "_" + row + "_" + col;
		ImageFrame[] frames = newTable.get(key);
		if (frames != null && index < frames.length)
		{
			return frames[index];
		}
		return null;
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
	 * 获取下一组帧
	 * 
	 * @return
	 */
	private synchronized GridImg getNextFrames()
	{
		GridImg result = null;
		if (nextIndex < inputList.length)
		{
			result = inputList[nextIndex];
			nextIndex++;
		}
		return result;
	}

	/**
	 * 完成一组帧
	 * 
	 * @param frames
	 */
	private synchronized void finishFrames(GridImg img, ImageFrame[] frames)
	{
		finishedCount++;
		lastLog = "计算最小像素区域(" + finishedCount + "/" + inputList.length + ")：" + img.file.url;
		add(frames);
	}

	@Override
	protected void startup() throws Exception
	{
		GamePacker.log("计算动画帧的裁切矩形");
	}

	@Override
	protected void exec() throws Exception
	{
		inputList = findAllFrame(root);

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
						GridImg frameList = getNextFrames();
						if (frameList == null || root.isCancel())
						{
							break;
						}

						finishFrames(frameList, clipFrameSet(frameList));
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

	// -----------------------------------------------------------------------------------------------------------------------
	//
	// 版本信息
	//
	// -----------------------------------------------------------------------------------------------------------------------

	/**
	 * 是否有
	 * 
	 * @param gid
	 * @param row
	 * @param col
	 * @return
	 */
	private boolean active(String gid, int row, int col)
	{
		String key = gid + "_" + row + "_" + col;
		if (oldTable.containsKey(key))
		{
			newTable.put(key, oldTable.get(key));
			oldTable.remove(key);
		}
		return newTable.containsKey(key);
	}

	/**
	 * 压入版本列表
	 * 
	 * @param frames
	 */
	private void add(ImageFrame[] frames)
	{
		if (frames.length == 0)
		{
			return;
		}

		ImageFrame first = frames[0];
		String key = first.file.gid + "_" + first.row + "_" + first.col;
		newTable.put(key, frames);
	}

	@Override
	protected void readHistory(InputStream stream) throws Exception
	{
		this.oldTable = new HashMap<String, ImageFrame[]>();
		this.newTable = new HashMap<String, ImageFrame[]>();

		BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "utf8"));
		while (true)
		{
			String line = reader.readLine();
			if (line == null)
			{
				break;
			}

			// fileID_row_col =
			// index_frameX_frameY_frameW_frameH_clipX_clipY_clipW_clipH ,
			// ..
			line = line.trim();
			if (line.isEmpty())
			{
				continue;
			}

			String[] parts = line.split("=");
			if (parts.length == 2)
			{
				String[] keys = parts[0].trim().split("_");
				String[] values = parts[1].trim().split(",");

				if (keys.length != 3)
				{
					continue;
				}

				ProjectFile file = root.getFileTable().getFileByGID(keys[0]);
				if (file == null)
				{
					continue;
				}

				int row = 0;
				int col = 0;
				ArrayList<ImageFrame> frames = new ArrayList<ImageFrame>();

				try
				{
					row = Integer.parseInt(keys[1]);
					col = Integer.parseInt(keys[2]);
				}
				catch (Error err)
				{
					continue;
				}

				if (row * col != values.length)
				{
					continue;
				}

				for (String value : values)
				{
					String params[] = value.split("_");
					if (params.length != 9)
					{
						continue;
					}

					try
					{
						int index = Integer.parseInt(params[0]);
						int frameX = Integer.parseInt(params[1]);
						int frameY = Integer.parseInt(params[2]);
						int frameW = Integer.parseInt(params[3]);
						int frameH = Integer.parseInt(params[4]);
						int clipX = Integer.parseInt(params[5]);
						int clipY = Integer.parseInt(params[6]);
						int clipW = Integer.parseInt(params[7]);
						int clipH = Integer.parseInt(params[8]);

						if (file instanceof ProjectImgFile)
						{
							frames.add(new ImageFrame((ProjectImgFile) file, row, col, index, frameX, frameY, frameW, frameH, clipX, clipY, clipW, clipH));
						}
					}
					catch (Error err)
					{
						continue;
					}
				}

				if (row * col == values.length && values.length > 0)
				{
					oldTable.put(file.gid + "_" + row + "_" + col, frames.toArray(new ImageFrame[frames.size()]));
				}
			}
		}
	}

	@Override
	protected void saveHistory(OutputStream stream) throws Exception
	{
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream, "utf8"));

		// 排序
		String[] keys = newTable.keySet().toArray(new String[newTable.size()]);
		Arrays.sort(keys, new Comparator<String>()
		{
			@Override
			public int compare(String arg0, String arg1)
			{
				arg0 = arg0.substring(0, arg0.indexOf("_")).trim();
				arg1 = arg1.substring(0, arg1.indexOf("_")).trim();
				int val1 = Integer.parseInt(arg0);
				int val2 = Integer.parseInt(arg1);
				return val1 - val2;
			}
		});

		// 写入
		for (int i = 0; i < keys.length; i++)
		{
			ImageFrame[] frames = newTable.get(keys[i]);
			if (frames.length <= 0)
			{
				continue;
			}

			Arrays.sort(frames, new Comparator<ImageFrame>()
			{
				@Override
				public int compare(ImageFrame o1, ImageFrame o2)
				{
					return o1.index - o2.index;
				}
			});

			ImageFrame first = frames[0];
			writer.write(first.file.gid + "_" + first.row + "_" + first.col);
			writer.write(" = ");
			for (int j = 0; j < frames.length; j++)
			{
				ImageFrame frame = frames[j];
				if (j > 0)
				{
					writer.write(",");
				}
				writer.write(frame.index + "_" + frame.frameX + "_" + frame.frameY + "_" + frame.frameW + "_" + frame.frameH + "_" + frame.clipX + "_" + frame.clipY + "_" + frame.clipW + "_" + frame.clipH);
			}

			if (i < keys.length - 1)
			{
				writer.write("\n");
			}
		}
	}

	// ------------------------------------------------------------------------------------------------------------
	//
	// 查找需要切割的图像
	//
	// ------------------------------------------------------------------------------------------------------------

	/**
	 * 查找所有图像帧
	 */
	private GridImg[] findAllFrame(WorldWriter root)
	{
		HashMap<String, GridImg> frameMap = new HashMap<String, GridImg>();

		for (Scene scene : root.getWorldTable().getAllScene())
		{
			for (SceneBackLayer layer : scene.backLayers)
			{
				if (!active(layer.img.imgFile.gid, 1, 1))
				{
					frameMap.put(layer.img.imgFile.gid + "_1_1", new GridImg(layer.img.imgFile, 1, 1));
				}
			}
			for (SceneForeLayer layer : scene.foreLayers)
			{
				if (!active(layer.img.imgFile.gid, 1, 1))
				{
					frameMap.put(layer.img.imgFile.gid + "_1_1", new GridImg(layer.img.imgFile, 1, 1));
				}
			}
		}

		for (Attire attire : root.getAttireTable().getAllAttire())
		{
			for (AttireAction action : attire.actions)
			{
				for (AttireAnim anim : action.anims)
				{
					for (int i = 0; i < anim.times.length; i++)
					{
						if (!active(anim.img.gid, anim.row, anim.col))
						{
							frameMap.put(anim.img.gid + "_" + anim.row + "_" + anim.col, new GridImg(anim.img, anim.row, anim.col));
						}
					}
				}
			}
		}

		return frameMap.values().toArray(new GridImg[frameMap.size()]);
	}

	// ------------------------------------------------------------------------------------------------------------
	//
	// 图像切割工具函数
	//
	// ------------------------------------------------------------------------------------------------------------

	/**
	 * 裁切图像帧列表
	 * 
	 * @param file
	 * @return
	 */
	private static ImageFrame[] clipFrameSet(GridImg frame)
	{
		ArrayList<ImageFrame> result = new ArrayList<ImageFrame>();
		try
		{
			BufferedImage image = ImageIO.read(frame.file);

			int frameW = image.getWidth() / frame.col;
			int frameH = image.getHeight() / frame.row;

			int index = 0;
			for (int i = 0; i < frame.row; i++)
			{
				for (int j = 0; j < frame.col; j++)
				{
					int frameX = j * frameW;
					int frameY = i * frameH;
					Rectangle rect = clipFrame(image, frameX, frameY, frameW, frameH);
					result.add(new ImageFrame(frame.file, frame.row, frame.col, index, frameX, frameY, frameW, frameH, rect.x, rect.y, rect.width, rect.height));
					index++;
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return result.toArray(new ImageFrame[result.size()]);
	}

	/**
	 * 裁切图像帧
	 * 
	 * @param image
	 * @param frameX
	 * @param frameY
	 * @param frameW
	 * @param frameH
	 * @return
	 */
	private static Rectangle clipFrame(BufferedImage image, int frameX, int frameY, int frameW, int frameH)
	{
		int[] pixels = new int[frameW * frameH];
		pixels = image.getRGB(frameX, frameY, frameW, frameH, pixels, 0, frameW);

		int l = -1;
		int t = -1;
		int r = -1;
		int b = -1;

		int maxX = frameW;
		for (int i = 0; i < frameH; i++)
		{
			for (int j = 0; j < maxX; j++)
			{
				int index = i * frameW + j;

				byte alpha = (byte) (pixels[index] >> 24 & 0xFF);
				if (alpha != 0)
				{
					if (t == -1)
					{
						t = i;
					}

					l = j;
					maxX = j;
					break;
				}
			}

			if (l == 0)
			{
				break;
			}
		}

		if (l == -1 || t == -1)
		{
			return new Rectangle(0, 0, 1, 1);
		}

		int minX = 0;
		for (int i = frameH - 1; i >= 0; i--)
		{
			for (int j = frameW - 1; j >= minX; j--)
			{
				int index = i * frameW + j;

				byte alpha = (byte) (pixels[index] >> 24 & 0xFF);
				if (alpha != 0)
				{
					if (b == -1)
					{
						b = i;
					}

					r = j;
					minX = j;

					break;
				}
			}

			if (r == 0)
			{
				break;
			}
		}

		if (r == -1 || b == -1)
		{
			throw new Error("反向遍历后得出的结果不一致！");
		}

		return new Rectangle(l, t, r - l + 1, b - t + 1);
	}

	/**
	 * 网格图像
	 * 
	 * @author ds
	 * 
	 */
	private static class GridImg
	{
		public final ProjectImgFile file;
		public final int row;
		public final int col;

		public GridImg(ProjectImgFile file, int row, int col)
		{
			this.file = file;
			this.row = row;
			this.col = col;
		}
	}
}
