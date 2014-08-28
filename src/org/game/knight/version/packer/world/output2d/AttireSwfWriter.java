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
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.world.model.Attire;
import org.game.knight.version.packer.world.model.AttireAction;
import org.game.knight.version.packer.world.model.AttireAnim;
import org.game.knight.version.packer.world.model.AttireBitmap;
import org.game.knight.version.packer.world.model.ImageFrame;
import org.game.knight.version.packer.world.model.ProjectImgFile;
import org.game.knight.version.packer.world.task.RootTask;

public class AttireSwfWriter
{
	private static final String AVATAR2_FRAME_PACK = "knight.avatar2.frames";

	private RootTask root;

	private int nextIndex;
	private int finishedCount;
	private String lastLog;

	private ArrayList<OutputEntity> newFrameList;
	private ArrayList<OutputEntity> allFrameList;
	private HashMap<ImageFrame, String> frame2URL;

	private HashMap<String, String> newTable = new HashMap<String, String>();
	private HashMap<String, String> oldTable = new HashMap<String, String>();

	/**
	 * 构造函数
	 * 
	 * @param root
	 */
	public AttireSwfWriter(RootTask root)
	{
		this.root = root;
	}

	/**
	 * 输出实体
	 * 
	 * @author ds
	 * 
	 */
	private static class OutputEntity
	{
		private final String url;
		private final ImageFrame[] frames;

		public OutputEntity(String url, ImageFrame[] frames)
		{
			this.url = url;
			this.frames = frames;
		}
	}

	/**
	 * 获取指定帧所属的文件
	 * 
	 * @param frame
	 * @return
	 */
	public String getFrameFileURL(ImageFrame frame)
	{
		return frame2URL.get(frame);
	}

	/**
	 * 获取指定帧的AS类名
	 * 
	 * @param frame
	 * @return
	 */
	public String getFrameClassID(ImageFrame frame)
	{
		return "$2d_" + frame.file.gid + "_" + 1 + "_" + 1 + "_"+frame.index;
	}

	/**
	 * 获取下一个组
	 * 
	 * @return
	 */
	private synchronized OutputEntity getNext()
	{
		OutputEntity result = null;
		if (nextIndex < newFrameList.size())
		{
			result = newFrameList.get(nextIndex);
			lastLog = "输出2D装扮SWF资源(" + nextIndex + "/" + newFrameList.size() + ")：" + result.url;
			nextIndex++;
		}
		return result;
	}

	/**
	 * 完成一组
	 * 
	 * @param frames
	 */
	private synchronized void finish(OutputEntity entity)
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
	public void start()
	{
		GamePacker.progress("输出装扮配置");

		openVer();

		filterAttireResources();

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
						OutputEntity next = getNext();
						if (next == null || root.isCancel())
						{
							break;
						}

						try
						{
							writeSwfFile(next);
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

		while (!root.isCancel() && !hasFinished())
		{
			GamePacker.progress(lastLog);

			try
			{
				Thread.sleep(500);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				GamePacker.error(e);
				break;
			}
		}

		exec.shutdown();

		if (hasFinished())
		{
			saveVer();
		}
	}

	/**
	 * 过滤装扮资源
	 */
	private void filterAttireResources()
	{
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

		newFrameList = new ArrayList<OutputEntity>();
		allFrameList = new ArrayList<OutputEntity>();
		frame2URL = new HashMap<ImageFrame, String>();

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

			String saveKey = frameListID.toString();

			if (!activate(saveKey))
			{
				String saveURL = root.getGlobalOptionTable().getNextExportFile() + ".swf";

				add(saveKey, saveURL);

				OutputEntity entity = new OutputEntity(saveURL, frames.toArray(new ImageFrame[frames.size()]));

				newFrameList.add(entity);
				allFrameList.add(entity);
			}
			else
			{
				allFrameList.add(new OutputEntity(newTable.get(saveKey), frames.toArray(new ImageFrame[frames.size()])));
			}

			for (ImageFrame frame : frames)
			{
				frame2URL.put(frame, newTable.get(saveKey));
			}
		}
	}

	/**
	 * 输出SWF文件
	 * 
	 * @param entity
	 * @throws IOException
	 */
	private void writeSwfFile(OutputEntity entity) throws IOException
	{
		ArrayList<String> classIDs = new ArrayList<String>();
		ArrayList<byte[]> pngBytes = new ArrayList<byte[]>();

		ImageFrame[] copy = Arrays.copyOf(entity.frames, entity.frames.length);
		Arrays.sort(copy, new Comparator<ImageFrame>()
		{
			@Override
			public int compare(ImageFrame o1, ImageFrame o2)
			{
				return o1.file.url.compareTo(o2.file.url);
			}
		});

		// 导出PNG
		BufferedImage img = null;
		ProjectImgFile file = null;
		for (ImageFrame frame : copy)
		{
			if (frame.file != file)
			{
				img = ImageIO.read(frame.file);
				file = frame.file;
			}

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
		File outputFile = new File(root.getOutputFolder().getPath() + entity.url);
		outputFile.getParentFile().mkdirs();
		FileUtil.writeFile(outputFile, swf.toBytes(true));
		root.addFileSuffix(outputFile);
	}

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
		return new File(root.getOutputFolder().getPath() + File.separatorChar + ".ver" + File.separatorChar + "2dResource");
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
	private void saveVer()
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
