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
	 * ���캯��
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
	// �������
	//
	// ----------------------------------------------------------------------------------

	/**
	 * ����Ŀ¼
	 * 
	 * @return
	 */
	public File getInputFolder()
	{
		return inputFolder;
	}

	/**
	 * ���Ŀ¼
	 * 
	 * @return
	 */
	public File getOutputFolder()
	{
		return outputFolder;
	}

	/**
	 * ZIPѹ��
	 * 
	 * @return
	 */
	public boolean hasZIP()
	{
		return zip;
	}

	// ----------------------------------------------------------------------------------
	//
	// �������ݱ�
	//
	// ----------------------------------------------------------------------------------

	/**
	 * ȫ��ѡ���
	 * 
	 * @return
	 */
	public OptionTable getGlobalOptionTable()
	{
		return optionTable;
	}

	/**
	 * �ļ���
	 * 
	 * @return
	 */
	public ProjectFileTable getFileTable()
	{
		return fileTable;
	}

	/**
	 * ATF������
	 * 
	 * @return
	 */
	public AtfParamTable getAtfParamTable()
	{
		return atfParamTable;
	}

	/**
	 * װ���
	 * 
	 * @return
	 */
	public AttireTable getAttireTable()
	{
		return attireTable;
	}

	/**
	 * �����
	 * 
	 * @return
	 */
	public WorldTable getWorldTable()
	{
		return worldTable;
	}

	/**
	 * ��ȡͼ��֡��
	 * 
	 * @return
	 */
	public ImageFrameTable getImageFrameTable()
	{
		return frameTable;
	}

	/**
	 * ��ȡUIװ�������
	 * 
	 * @return
	 */
	public GameUIAttireWriter getUIAttireWriter()
	{
		return gameUIAttireWriter;
	}

	/**
	 * MP3�����
	 * 
	 * @return
	 */
	public Mp3Writer getMp3Writer()
	{
		return mp3Writer;
	}

	// ----------------------------------------------------------------------------------
	//
	// ���ߺ���
	//
	// ----------------------------------------------------------------------------------

	/**
	 * ȡ��
	 */
	public synchronized void cancel()
	{
		cancel = true;
	}

	/**
	 * ����ȡ��
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
	 * �Ƿ��Ѿ�ȡ��
	 * 
	 * @return
	 */
	public synchronized boolean isCancel()
	{
		return cancel;
	}

	/**
	 * ��ʼ��ȡװ��
	 */
	public void start()
	{
		GamePacker.beginTask("����");

		GamePacker.progress("��ȡȫ����Ϣ");

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
				GamePacker.error("��ȡ��");
			}
		}

		GamePacker.endTask();
	}

	// ----------------------------------------------------------------------------------
	//
	// �ļ��������
	//
	// ----------------------------------------------------------------------------------

	/**
	 * ����ļ���׺
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
	 * ����·����CDN·��
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
	 * �������ļ�
	 * 
	 * @param url
	 */
	public synchronized void addOutputFile(String url)
	{
		outputFiles.add("/" + getOutputFolder().getName() + url);
	}

	/**
	 * ��� /db.ver
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
