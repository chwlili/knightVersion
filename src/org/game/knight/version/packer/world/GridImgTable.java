package org.game.knight.version.packer.world;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.chw.util.FileUtil;

public class GridImgTable
{
	private File file;
	private Hashtable<GridImgKey, GridImg[]> clipTable;

	private Hashtable<GridImgKey, GridImg[]> revisionTable;

	/**
	 * 按校验码获取裁切信息
	 * 
	 * @param checksum
	 * @return
	 */
	public GridImg[] getClips(GridImgKey checksum)
	{
		return revisionTable.get(checksum);
	}

	/**
	 * 是否有指定的裁切信息
	 * @param key
	 * @return
	 */
	public boolean has(GridImgKey key)
	{
		if (revisionTable.containsKey(key))
		{
			return true;
		}
		if (clipTable.containsKey(key))
		{
			revisionTable.put(key, clipTable.get(key));
			return true;
		}
		return false;
	}
	
	/**
	 * 获取图像裁切信息
	 * 
	 * @param data.file
	 * @param md5
	 * @param rowCount
	 * @param colCount
	 */
	public void add(GridImgKey checksum)
	{
		if (revisionTable.containsKey(checksum))
		{
			return;
		}

		if (clipTable.containsKey(checksum))
		{
			revisionTable.put(checksum, clipTable.get(checksum));
		}
		else
		{
			revisionTable.put(checksum, measureClips(checksum.getFile(), checksum.getRowCount(), checksum.getColCount()));
		}
	}

	/**
	 * 打开图像裁切数据配置
	 * 
	 * @param file
	 */
	public void open(File file)
	{
		this.file = file;

		this.clipTable = new Hashtable<GridImgKey, GridImg[]>();
		this.revisionTable = new Hashtable<GridImgKey, GridImg[]>();

		if (file == null || !file.exists() || !file.isFile())
		{
			return;
		}

		BufferedReader input = null;
		try
		{
			input = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			while (true)
			{
				String line = input.readLine();
				if (line == null)
				{
					break;
				}

				line.trim();

				String[] parts = line.split("=");
				String[] keys = parts[0].trim().split("_");
				String[] times = keys.length > 3 ? keys[3].split("\\.") : new String[] { "0" };
				String[] vals = parts[1].trim().split(";");

				int[] intTimes = new int[times.length];
				for (int i = 0; i < times.length; i++)
				{
					intTimes[i] = Integer.parseInt(times[i]);
					intTimes[i] = intTimes[i] > 0 ? 1 : 0;
				}

				GridImg[] clips = new GridImg[vals.length];
				for (int i = 0; i < vals.length; i++)
				{
					String[] params = vals[i].split(",");

					int x = Integer.parseInt(params[0]);
					int y = Integer.parseInt(params[1]);
					int w = Integer.parseInt(params[2]);
					int h = Integer.parseInt(params[3]);
					int clipX = Integer.parseInt(params[4]);
					int clipY = Integer.parseInt(params[5]);
					int clipW = Integer.parseInt(params[6]);
					int clipH = Integer.parseInt(params[7]);

					clips[i] = new GridImg(x, y, w, h, clipX, clipY, clipW, clipH);
				}

				clipTable.put(new GridImgKey(keys[0], Integer.parseInt(keys[1]), Integer.parseInt(keys[2]), intTimes), clips);
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
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
	 * 保存图像裁切数据配置
	 */
	public void save()
	{
		saveAs(file);
	}

	/**
	 * 另存图像裁切数据配置
	 * 
	 * @param file
	 */
	public void saveAs(File file)
	{
		if (file == null || revisionTable == null)
		{
			return;
		}

		StringBuilder sb = new StringBuilder();

		GridImgKey[] keys = new GridImgKey[revisionTable.size()];
		keys = revisionTable.keySet().toArray(keys);
		Arrays.sort(keys, new Comparator<GridImgKey>()
		{
			@Override
			public int compare(GridImgKey o1, GridImgKey o2)
			{
				return o1.toString().compareTo(o2.toString());
			}
		});

		for (int i = 0; i < keys.length; i++)
		{
			GridImgKey key = keys[i];
			GridImg[] clips = revisionTable.get(key);

			sb.append(key.toString() + " = ");
			for (int j = 0; j < clips.length; j++)
			{
				sb.append(clips[j].getX() + ",");
				sb.append(clips[j].getY() + ",");
				sb.append(clips[j].getW() + ",");
				sb.append(clips[j].getH() + ",");
				sb.append(clips[j].getClipX() + ",");
				sb.append(clips[j].getClipY() + ",");
				sb.append(clips[j].getClipW() + ",");
				sb.append(clips[j].getClipH());

				if (j < clips.length - 1)
				{
					sb.append(";");
				}
			}
			sb.append("\n");
		}

		try
		{
			byte[] bytes = sb.toString().getBytes("UTF-8");
			FileUtil.writeFile(file, bytes);
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 计算图像裁切信息
	 * 
	 * @param file
	 * @return
	 */
	private GridImg[] measureClips(File file, int rowCount, int colCount)
	{
		GridImg[] clips = new GridImg[rowCount * colCount];

		try
		{
			BufferedImage image = ImageIO.read(file);

			int index = 0;

			// int frameX = 0;
			// int frameY = 0;
			int frameW = image.getWidth() / colCount;
			int frameH = image.getHeight() / rowCount;

			for (int i = 0; i < rowCount; i++)
			{
				for (int j = 0; j < colCount; j++)
				{
					clips[index] = measureFrameClip(image, j * frameW, i * frameH, frameW, frameH);

					index++;
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return clips;
	}

	/**
	 * 计算单帧裁切信息
	 * 
	 * @param image
	 * @param frameX
	 * @param frameY
	 * @param frameW
	 * @param frameH
	 * @return
	 */
	private GridImg measureFrameClip(BufferedImage image, int frameX, int frameY, int frameW, int frameH)
	{
		GridImg result = new GridImg(frameX, frameY, frameW, frameH);

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
			result.setClip(0, 0, 1, 1);
			return result;
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
		
		if(r==-1 || b==-1)
		{
			throw new Error("反向遍历后得出的结果不一致！");
		}
		
		result.setClip(l, t, r - l + 1, b - t + 1);
		return result;
	}
}
