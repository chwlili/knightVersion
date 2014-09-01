package org.game.knight.version.packer.world;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.chw.util.FileUtil;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.world.model.AtfParamTable;
import org.game.knight.version.packer.world.model.AttireTable;
import org.game.knight.version.packer.world.model.GameUIAttireWriter;
import org.game.knight.version.packer.world.model.OptionTable;
import org.game.knight.version.packer.world.model.ImageFrameTable;
import org.game.knight.version.packer.world.model.Mp3Writer;
import org.game.knight.version.packer.world.model.ProjectFileTable;
import org.game.knight.version.packer.world.model.WorldTable;
import org.game.knight.version.packer.world.output2d.Config2d;
import org.game.knight.version.packer.world.output3d.Config3d;

public class WorldWriter
{
	private boolean cancel;
	private ArrayList<Exception> exceptions;

	private final File inputFolder;
	private final File outputFolder;
	private final boolean zip;
	public final int maxThreadCount;
	private final ArrayList<String> outputFiles;

	private OptionTable optionTable;
	private ProjectFileTable fileTable;
	private AtfParamTable atfParamTable;
	private AttireTable attireTable;
	private WorldTable worldTable;
	private ImageFrameTable frameTable;
	private Mp3Writer mp3Writer;
	private GameUIAttireWriter gameUIAttireWriter;

	private Config3d config3dWriter;
	private Config2d config2dWriter;

	/**
	 * 构造函数
	 * 
	 * @param inputFolder
	 * @param outputFolder
	 */
	public WorldWriter(File inputFolder, File outputFolder, boolean zip, int runCount)
	{
		this.inputFolder = inputFolder;
		this.outputFolder = outputFolder;
		this.zip = zip;
		this.maxThreadCount = runCount;

		this.outputFiles = new ArrayList<String>();
	}

	// ----------------------------------------------------------------------------------
	//
	// 输入参数
	//
	// ----------------------------------------------------------------------------------

	/**
	 * 输入目录
	 * 
	 * @return
	 */
	public File getInputFolder()
	{
		return inputFolder;
	}

	/**
	 * 输出目录
	 * 
	 * @return
	 */
	public File getOutputFolder()
	{
		return outputFolder;
	}

	/**
	 * ZIP压缩
	 * 
	 * @return
	 */
	public boolean hasZIP()
	{
		return zip;
	}

	// ----------------------------------------------------------------------------------
	//
	// 各种数据表
	//
	// ----------------------------------------------------------------------------------

	/**
	 * 全局选项表
	 * 
	 * @return
	 */
	public OptionTable getGlobalOptionTable()
	{
		return optionTable;
	}

	/**
	 * 文件表
	 * 
	 * @return
	 */
	public ProjectFileTable getFileTable()
	{
		return fileTable;
	}

	/**
	 * ATF参数表
	 * 
	 * @return
	 */
	public AtfParamTable getAtfParamTable()
	{
		return atfParamTable;
	}

	/**
	 * 装扮表
	 * 
	 * @return
	 */
	public AttireTable getAttireTable()
	{
		return attireTable;
	}

	/**
	 * 世界表
	 * 
	 * @return
	 */
	public WorldTable getWorldTable()
	{
		return worldTable;
	}

	/**
	 * 获取图像帧表
	 * 
	 * @return
	 */
	public ImageFrameTable getImageFrameTable()
	{
		return frameTable;
	}

	/**
	 * 获取UI装扮输出器
	 * 
	 * @return
	 */
	public GameUIAttireWriter getUIAttireWriter()
	{
		return gameUIAttireWriter;
	}

	/**
	 * MP3输出器
	 * 
	 * @return
	 */
	public Mp3Writer getMp3Writer()
	{
		return mp3Writer;
	}

	// ----------------------------------------------------------------------------------
	//
	// 工具函数
	//
	// ----------------------------------------------------------------------------------

	/**
	 * 取消
	 */
	public synchronized void cancel()
	{
		cancel = true;
	}

	/**
	 * 出错取消
	 * 
	 * @param exception
	 */
	public synchronized void cancel(Exception exception)
	{
		exceptions.add(exception);
		cancel = true;
		System.err.println("--------------------exception--------------------");
		System.err.println(exception);
	}

	/**
	 * 是否已经取消
	 * 
	 * @return
	 */
	public synchronized boolean isCancel()
	{
		return cancel;
	}

	/**
	 * 开始读取装扮
	 */
	public void start()
	{
		GamePacker.beginTask("世界");

		GamePacker.progress("读取全局信息");

		exceptions = new ArrayList<Exception>();

		optionTable = new OptionTable(this);
		fileTable = new ProjectFileTable(this);
		atfParamTable = new AtfParamTable(this);
		attireTable = new AttireTable(this);
		worldTable = new WorldTable(this);
		frameTable = new ImageFrameTable(this);
		mp3Writer = new Mp3Writer(this);
		gameUIAttireWriter = new GameUIAttireWriter(this);
		config3dWriter = new Config3d(this);
		config2dWriter = new Config2d(this);

		ArrayList<BaseWriter> writers = new ArrayList<BaseWriter>();
		writers.add(optionTable);
		writers.add(fileTable);
		writers.add(atfParamTable);
		writers.add(attireTable);
		writers.add(worldTable);
		writers.add(frameTable);
		writers.add(mp3Writer);
		writers.add(gameUIAttireWriter);
		writers.add(config3dWriter);
		writers.add(config2dWriter);

		try
		{
			for (BaseWriter writer : writers)
			{
				if (!isCancel())
				{
					writer.run();
				}
			}

			for (BaseWriter writer : writers)
			{
				if (!isCancel())
				{
					writer.saveVer();
				}
			}

			if (!isCancel())
			{
				writerVer();
			}
		}
		catch (Exception exception)
		{
			cancel(exception);
		}

		if (isCancel())
		{
			if (exceptions.size() > 0)
			{
				for (Exception exception : exceptions)
				{
					GamePacker.error(exception);
				}
			}
			else
			{
				GamePacker.error("已取消");
			}
		}

		GamePacker.endTask();
	}

	// ----------------------------------------------------------------------------------
	//
	// 文件结束标记
	//
	// ----------------------------------------------------------------------------------

	/**
	 * 添加文件后缀
	 * 
	 * @param file
	 */
	public void addFileSuffix(File file)
	{
		if (!file.exists())
		{
			return;
		}

		byte[] suffix = new byte[] { 0, 0, 0, 0, 0x4D, 0x44, 0x35, 0 };

		RandomAccessFile writer = null;
		try
		{
			writer = new RandomAccessFile(file, "rw");
			writer.seek(file.length() - suffix.length);

			boolean added = true;
			for (int j = 0; j < suffix.length; j++)
			{
				if (writer.readByte() != suffix[j])
				{
					added = false;
					break;
				}
			}

			if (!added)
			{
				writer.seek(file.length());
				for (int j = 0; j < suffix.length; j++)
				{
					writer.writeByte(suffix[j]);
				}
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
		finally
		{
			if (writer != null)
			{
				try
				{
					writer.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	// ----------------------------------------------------------------------------------
	//
	// db.ver
	//
	// ----------------------------------------------------------------------------------

	/**
	 * 本地路径到CDN路径
	 * 
	 * @param url
	 * @return
	 */
	public String localToCdnURL(String url)
	{
		if (url != null && !url.isEmpty())
		{
			return "/" + getOutputFolder().getName() + url;
		}
		return "";
	}

	/**
	 * 添加输出文件
	 * 
	 * @param url
	 */
	public synchronized void addOutputFile(String url)
	{
		outputFiles.add("/" + getOutputFolder().getName() + url);
	}

	/**
	 * 输出 /db.ver
	 */
	private void writerVer()
	{
		String[] urls = outputFiles.toArray(new String[outputFiles.size()]);
		Arrays.sort(urls, new Comparator<String>()
		{
			@Override
			public int compare(String o1, String o2)
			{
				o1 = o1.substring(o1.lastIndexOf("/") + 1, o1.lastIndexOf("."));
				o2 = o2.substring(o2.lastIndexOf("/") + 1, o2.lastIndexOf("."));
				String[] list1 = o1.split("[^\\d]+");
				String[] list2 = o2.split("[^\\d]+");

				int length = Math.min(list1.length, list2.length);
				for (int i = 0; i < length; i++)
				{
					try
					{
						int id1 = Integer.parseInt(list1[i]);
						int id2 = Integer.parseInt(list2[i]);
						if (id1 != id2)
						{
							return id1 - id2;
						}
					}
					catch (NumberFormatException e)
					{
						if (!list1[i].equals(list2[i]))
						{
							return list1[i].compareTo(list2[i]);
						}
					}
				}
				return 0;
			}
		});

		StringBuilder sb = new StringBuilder();
		for (String url : urls)
		{
			if (sb.length() > 0)
			{
				sb.append("\n");
			}
			sb.append(url);
		}

		try
		{
			FileUtil.writeFile(new File(getOutputFolder().getPath() + "/db.ver"), sb.toString().getBytes("utf8"));
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			GamePacker.error(e);
			return;
		}
	}
}
