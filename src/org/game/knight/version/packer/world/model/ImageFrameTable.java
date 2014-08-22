package org.game.knight.version.packer.world.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

import org.chw.util.FileUtil;
import org.eclipse.swt.graphics.Rectangle;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.world.task.RootTask;

public class ImageFrameTable
{
	private RootTask root;

	private GridImg[] inputList;
	private int nextIndex;
	private int finishedCount;
	private String lastLog = "";

	private HashMap<String, ImageFrame[]> oldTable = new HashMap<String, ImageFrame[]>();
	private HashMap<String, ImageFrame[]> newTable = new HashMap<String, ImageFrame[]>();

	/**
	 * ���캯��
	 * 
	 * @param root
	 */
	public ImageFrameTable(RootTask root)
	{
		this.root = root;
	}

	/**
	 * ��ȡ֡��Ϣ
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
	 * �Ƿ��Ѿ����
	 * 
	 * @return
	 */
	private synchronized boolean isFinished()
	{
		return finishedCount >= inputList.length;
	}

	/**
	 * ��ȡ��һ��֡
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
	 * ���һ��֡
	 * 
	 * @param frames
	 */
	private synchronized void finishFrames(GridImg img, ImageFrame[] frames)
	{
		finishedCount++;
		lastLog = "������С��������(" + finishedCount + "/" + inputList.length + ")��" + img.file.url;
		add(frames);
	}

	/**
	 * ��ʼ
	 */
	public void start()
	{
		openVer();

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
	// �汾��Ϣ
	//
	// -----------------------------------------------------------------------------------------------------------------------

	/**
	 * �Ƿ���
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
	 * ѹ��汾�б�
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

	/**
	 * ��ȡ�汾�ļ�
	 * 
	 * @return
	 */
	private File getVerFile()
	{
		return new File(root.getOutputFolder().getPath() + File.separatorChar + ".ver" + File.separatorChar + "clip");
	}

	/**
	 * �򿪰汾��Ϣ
	 */
	private void openVer()
	{
		this.oldTable = new HashMap<String, ImageFrame[]>();
		this.newTable = new HashMap<String, ImageFrame[]>();

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
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * ����汾��Ϣ
	 */
	public void saveVer()
	{
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

		StringBuilder sb = new StringBuilder();
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
			sb.append(first.file.gid + "_" + first.row + "_" + first.col);
			sb.append(" = ");
			for (int j = 0; j < frames.length; j++)
			{
				ImageFrame frame = frames[j];
				if (j > 0)
				{
					sb.append(",");
				}
				sb.append(frame.index + "_" + frame.frameX + "_" + frame.frameY + "_" + frame.frameW + "_" + frame.frameH + "_" + frame.clipX + "_" + frame.clipY + "_" + frame.clipW + "_" + frame.clipH);
			}

			if (i < keys.length - 1)
			{
				sb.append("\n");
			}
		}

		try
		{
			FileUtil.writeFile(getVerFile(), sb.toString().getBytes("UTF-8"));
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}

	// ------------------------------------------------------------------------------------------------------------
	//
	// ������Ҫ�и��ͼ��
	//
	// ------------------------------------------------------------------------------------------------------------

	/**
	 * ��������ͼ��֡
	 */
	private GridImg[] findAllFrame(RootTask root)
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
	// ͼ���и�ߺ���
	//
	// ------------------------------------------------------------------------------------------------------------

	/**
	 * ����ͼ��֡�б�
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
	 * ����ͼ��֡
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
			throw new Error("���������ó��Ľ����һ�£�");
		}

		return new Rectangle(l, t, r - l + 1, b - t + 1);
	}

	/**
	 * ����ͼ��
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
