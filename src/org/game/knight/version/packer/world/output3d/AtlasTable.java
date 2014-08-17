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

	private HashMap<AtfParam, ArrayList<AtlasRect[]>> atf_rectListSet;
	private ArrayList<AtlasRect[]> allRectList;
	private HashMap<AtlasRect[], AtfParam> rectList_atf;
	private HashMap<AtlasRect[], Atlas> rectList_atlas;

	private HashMap<String, Atlas[]> newTable;
	private HashMap<String, Atlas[]> oldTable;

	private int nextIndex;
	private int finishedCount;
	private String lastLog;

	/**
	 * ���캯��
	 * 
	 * @param root
	 */
	public AtlasTable(RootTask root)
	{
		this.root = root;
	}

	/**
	 * ��ʼ
	 */
	public void start()
	{
		atf_rectListSet = new HashMap<AtfParam, ArrayList<AtlasRect[]>>();
		allRectList = new ArrayList<AtlasRect[]>();
		rectList_atf = new HashMap<AtlasRect[], AtfParam>();
		rectList_atlas = new HashMap<AtlasRect[], Atlas>();

		oldTable = new HashMap<String, Atlas[]>();
		newTable = new HashMap<String, Atlas[]>();

		openVer();

		if (root.isCancel())
		{
			return;
		}

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
		}
	}

	// -----------------------------------------------------------------------------------------------------
	//
	// ����ATF��
	//
	// -----------------------------------------------------------------------------------------------------

	/**
	 * ����ATF��
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

							atf_frameset.get(atf).add(root.getImageFrameTable().get(anim.img, anim.row, anim.col, i));
						}
					}
				}
			}
		}

		HashMap<AtfParam, ImageFrame[]> atf_frameArray = new HashMap<AtfParam, ImageFrame[]>();
		for (AtfParam key : atf_frameset.keySet())
		{
			HashSet<ImageFrame> value = atf_frameset.get(key);
			atf_frameArray.put(key, value.toArray(new ImageFrame[value.size()]));
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
	// ͼ�����
	//
	// -----------------------------------------------------------------------------------------------------

	/**
	 * ��ȡ��һ��
	 * 
	 * @return
	 */
	private synchronized AtlasRect[] getNext()
	{
		AtlasRect[] result = null;
		if (nextIndex < allRectList.size())
		{
			result = allRectList.get(nextIndex);
			lastLog = "��ͼ���(" + nextIndex + "/" + allRectList.size() + "):" + rectList_atf.get(result).id + "(ͼ��X" + result.length + ")";
			nextIndex++;
		}

		return result;
	}

	/**
	 * ���
	 */
	private synchronized void finishIncrement()
	{
		finishedCount++;
	}

	/**
	 * �Ƿ��Ѿ����
	 * 
	 * @return
	 */
	private synchronized boolean isFinished()
	{
		return finishedCount >= allRectList.size();
	}

	/**
	 * �������ATF
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
						AtlasRect[] next = getNext();
						if (next == null || root.isCancel())
						{
							break;
						}

						writeATF(rectList_atf.get(next), next);
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
	 * ���ATF
	 * 
	 * @param rects
	 */
	private void writeATF(AtfParam param, AtlasRect[] rects)
	{
		// ������ͬһ��ͼ���ļ���֡�ŵ�һ�𣬼��ٻ���ͼ��ʱ�л�ͼ��Ĵ�����
		Arrays.sort(rects, new Comparator<AtlasRect>()
		{
			@Override
			public int compare(AtlasRect o1, AtlasRect o2)
			{
				return o1.frame.file.url.compareTo(o2.frame.file.url);
			}
		});

		// ȷ�����������������2�Ĵη����ҳ�����С�ĳߴ�
		int outputW = 0;
		int outputH = 0;
		for (AtlasRect rect : rects)
		{
			outputW = Math.max(outputW, rect.x + rect.frame.clipW);
			outputH = Math.max(outputH, rect.y + rect.frame.clipH);
		}
		outputW = TextureHelper.normalizeWH(outputW);
		outputH = TextureHelper.normalizeWH(outputH);

		// ȷ���ļ����λ��
		String saveURL = root.getWriteFileTable().getNextExportFile();
		String pngURL = saveURL + ".png";
		String atfURL = saveURL + ".atf";
		File pngFile = new File(root.getOutputFolder().getPath() + pngURL);
		File atfFile = new File(root.getOutputFolder().getPath() + atfURL);

		// �ϲ�ͼ��
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
				return;
			}
		}
		graphics.dispose();

		// ȷ��XML����
		StringBuilder atlas = new StringBuilder();
		atlas.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		atlas.append("<TextureAtlas imagePath=\"" + ("/" + root.getOutputFolder().getName() + atfURL) + "\">\n");
		for (AtlasRect rect : rects)
		{
			ImageFrame frame = rect.frame;
			atlas.append("\t<SubTexture name=\"" + frame.file.gid + "_" + frame.row + "_" + frame.col + "_" + frame.index + "\" x=\"" + rect.x + "\" y=\"" + rect.y + "\" width=\"" + frame.clipW + "\" height=\"" + frame.clipH + "\" frameX=\"" + (frame.clipX > 0 ? -frame.clipX : 0) + "\" frameY=\"" + (frame.clipY > 0 ? -frame.clipY : 0) + "\" frameWidth=\"" + frame.frameW + "\" frameHeight=\"" + frame.frameH + "\"/>\n");
		}
		atlas.append("</TextureAtlas>");

		// ���ATF,���XML����
		try
		{
			ImageIO.write(image, "png", pngFile);
			TextureHelper.png2atf(pngFile, atfFile);

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

		// ��¼
		rectList_atlas.put(rects, new Atlas(param, atfURL, rects));
	}

	// -------------------------------------------------------------------------------------------------------------------
	//
	// �汾��Ϣ
	//
	// -------------------------------------------------------------------------------------------------------------------

	// private HashMap<String, Group> oldTable = new HashMap<String, Group>();
	// private HashMap<String, Group> newTable = new HashMap<String, Group>();

	/**
	 * ��ȡ�汾�ļ�
	 * 
	 * @return
	 */
	private File getVerFile()
	{
		return new File(root.getOutputFolder().getPath() + File.separatorChar + ".ver" + File.separatorChar + ".atlas");
	}

	/**
	 * �򿪰汾��Ϣ
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
	 * ����汾��Ϣ
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
