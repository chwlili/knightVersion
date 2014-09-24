package org.game.knight.version.packer.world;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.chw.util.FileUtil;
import org.eclipse.core.runtime.CoreException;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.world.model.AtfParamTable;
import org.game.knight.version.packer.world.model.AttireTable;
import org.game.knight.version.packer.world.model.GameUIAttireWriter;
import org.game.knight.version.packer.world.model.ImageFrameTable;
import org.game.knight.version.packer.world.model.Mp3Writer;
import org.game.knight.version.packer.world.model.OptionTable;
import org.game.knight.version.packer.world.model.ProjectFileTable;
import org.game.knight.version.packer.world.model.WorldTable;
import org.game.knight.version.packer.world.output2d.Config2d;
import org.game.knight.version.packer.world.output3d.Config3d;
import org.xml.sax.SAXException;
import org.xml2as.builder.ClassTable;
import org.xml2as.builder.UnitConfigBuilder;

public class WorldWriter
{
	private boolean cancel;
	private ArrayList<Exception> exceptions;

	private final File inputFolder;
	private final File outputFolder;
	private final File xml2Folder;
	public final int maxThreadCount;
	private final boolean zip;

	private HashMap<String, byte[]> oldHistoryMap = new HashMap<String, byte[]>();
	private HashMap<String, byte[]> newHistoryMap = new HashMap<String, byte[]>();

	/**
	 * 选项表
	 */
	public final OptionTable optionTable;
	/**
	 * MD5文件表
	 */
	public final ProjectFileTable fileTable;
	/**
	 * ATF参数表
	 */
	public final AtfParamTable atfParamTable;
	/**
	 * 装扮数据表
	 */
	public final AttireTable attireTable;
	/**
	 * 世界数据表
	 */
	public final WorldTable worldTable;
	/**
	 * 图像裁切表
	 */
	public final ImageFrameTable frameTable;
	/**
	 * MP3输出表
	 */
	public final Mp3Writer mp3Writer;
	/**
	 * UI装扮输出表
	 */
	public final GameUIAttireWriter gameUIAttireWriter;
	/**
	 * 3D资源输出表
	 */
	public final Config3d config3dWriter;
	/**
	 * 2D资源输出表
	 */
	public final Config2d config2dWriter;

	private final ArrayList<String> outputFiles;

	/**
	 * 构造函数
	 * 
	 * @param inputFolder
	 * @param outputFolder
	 */
	public WorldWriter(File inputFolder, File outputFolder, File xml2Folder, int runCount, boolean zip)
	{
		this.inputFolder = inputFolder;
		this.outputFolder = outputFolder;
		this.xml2Folder = xml2Folder;
		this.maxThreadCount = runCount;
		this.zip = zip;

		this.optionTable = new OptionTable(this);
		this.fileTable = new ProjectFileTable(this);
		this.atfParamTable = new AtfParamTable(this);
		this.attireTable = new AttireTable(this);
		this.worldTable = new WorldTable(this);
		this.frameTable = new ImageFrameTable(this);
		this.mp3Writer = new Mp3Writer(this);
		this.gameUIAttireWriter = new GameUIAttireWriter(this);
		this.config3dWriter = new Config3d(this);
		this.config2dWriter = new Config2d(this);

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

	/**
	 * 获取XML2文件
	 * 
	 * @param name
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws CoreException
	 * @throws IOException
	 */
	public byte[] convertXmlToAs(InputStream input, String name) throws IOException, CoreException, SAXException, ParserConfigurationException
	{
		File file = null;
		if (xml2Folder != null)
		{
			ArrayList<File> folders = new ArrayList<File>();
			folders.add(xml2Folder);

			while (folders.size() > 0)
			{
				File folder = folders.remove(0);
				for (File child : folder.listFiles())
				{
					if (child.isHidden())
					{
						continue;
					}

					if (child.isDirectory())
					{
						folders.add(child);
					}
					else if (child.getName().endsWith(name))
					{
						file = child;
						break;
					}
				}
			}
		}
		if (file != null)
		{
			UnitConfigBuilder builder = new UnitConfigBuilder(new ClassTable(file));
			return builder.build(input);
		}
		return null;
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

		exceptions = new ArrayList<Exception>();

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
			openVer();

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
				saveVer();
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

	public byte[] getHistory(String name)
	{
		if (oldHistoryMap.containsKey(name))
		{
			return oldHistoryMap.get(name);
		}
		return null;
	}

	public void setHistoryOutputStream(String name, byte[] bytes)
	{
		newHistoryMap.put(name, bytes);
	}

	private void openVer() throws ZipException, IOException
	{
		HashMap<String, byte[]> inputMap = new HashMap<String, byte[]>();

		File file = new File(outputFolder.getPath() + File.separator + ".ver");
		if (file.exists() && file.isFile())
		{
			ZipFile zipFile = new ZipFile(file);

			Enumeration<? extends ZipEntry> entrys = zipFile.entries();
			while (entrys.hasMoreElements())
			{
				ZipEntry entry = entrys.nextElement();
				InputStream stream = zipFile.getInputStream(entry);

				byte[] bytes = new byte[(int) entry.getSize()];
				int offset = 0;
				while (offset < bytes.length)
				{
					int readCount = stream.read(bytes, offset, bytes.length - offset);
					if (readCount < 0)
					{
						break;
					}
					offset += readCount;
				}

				if (offset < bytes.length)
				{
					throw new IOException("Could not completely read file " + file.getName());
				}

				inputMap.put(entry.getName(), bytes);
			}
		}

		oldHistoryMap = inputMap;
	}

	private void saveVer() throws IOException
	{
		File file = new File(outputFolder.getPath() + File.separator + ".ver");
		if (file.getParentFile().exists())
		{
			file.getParentFile().mkdir();
		}

		String[] keys = newHistoryMap.keySet().toArray(new String[newHistoryMap.size()]);
		Arrays.sort(keys);

		ZipOutputStream stream = null;

		try
		{
			stream = new ZipOutputStream(new FileOutputStream(file));
			for (String key : keys)
			{
				stream.putNextEntry(new ZipEntry(key));
				stream.write(newHistoryMap.get(key));
				stream.flush();
			}
			stream.finish();
		}
		finally
		{
			stream.close();
		}
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
