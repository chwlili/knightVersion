package org.game.knight.version.packer.world.task;

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
import org.game.knight.version.packer.world.model.GlobalOptionTable;
import org.game.knight.version.packer.world.model.ImageFrameTable;
import org.game.knight.version.packer.world.model.Mp3Writer;
import org.game.knight.version.packer.world.model.ProjectFileTable;
import org.game.knight.version.packer.world.model.GameUIAttireWriter;
import org.game.knight.version.packer.world.model.WorldTable;
import org.game.knight.version.packer.world.output2d.Config2d;
import org.game.knight.version.packer.world.output3d.AtlasWriter;
import org.game.knight.version.packer.world.output3d.Config3d;
import org.game.knight.version.packer.world.output3d.SliceImageWriter;

public class RootTask
{
	public final int maxThreadCount = 3;

	private boolean cancel;

	private final File inputFolder;
	private final File outputFolder;
	private final boolean zip;
	private final ArrayList<String> outputFiles;

	private GlobalOptionTable globalOptionTable;
	private ProjectFileTable fileTable;
	private AtfParamTable paramTable;
	private AttireTable attireTable;
	private WorldTable worldTable;
	private ImageFrameTable imageFrameTable;
	private GameUIAttireWriter gameUIAttireWriter;

	private Mp3Writer mp3Writer;
	private SliceImageWriter sliceImageWriter;
	private AtlasWriter atlasWriter;
	private Config3d config3dWriter;
	private Config2d config2dWriter;

	/**
	 * ���캯��
	 * 
	 * @param inputFolder
	 * @param outputFolder
	 */
	public RootTask(File inputFolder, File outputFolder, boolean zip)
	{
		this.inputFolder = inputFolder;
		this.outputFolder = outputFolder;
		this.zip=zip;
		
		this.outputFiles = new ArrayList<String>();
	}

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
	 * @return
	 */
	public boolean hasZIP()
	{
		return zip;
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
		return paramTable;
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
		return imageFrameTable;
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
	 * ȫ��ѡ���
	 * 
	 * @return
	 */
	public GlobalOptionTable getGlobalOptionTable()
	{
		return globalOptionTable;
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

	/**
	 * ��ȡ��Ƭ�����
	 * 
	 * @return
	 */
	public SliceImageWriter getSliceImageWriter()
	{
		return sliceImageWriter;
	}

	/**
	 * ��ͼ�������
	 * 
	 * @return
	 */
	public AtlasWriter getAtlasTable()
	{
		return atlasWriter;
	}

	/**
	 * ȡ��
	 */
	public synchronized void cancel()
	{
		cancel = true;
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
		globalOptionTable = new GlobalOptionTable(this);
		globalOptionTable.start();
		if (isCancel())
		{
			return;
		}

		GamePacker.progress("��ȡ������Ϣ");
		fileTable = new ProjectFileTable(this);
		fileTable.start();
		fileTable.saveVer();
		if (isCancel())
		{
			return;
		}

		GamePacker.progress("��ȡATF������Ϣ");
		paramTable = new AtfParamTable(this);
		paramTable.start();
		if (isCancel())
		{
			return;
		}

		GamePacker.progress("��ȡװ����Ϣ");
		attireTable = new AttireTable(this);
		attireTable.start();
		if (isCancel())
		{
			return;
		}

		GamePacker.progress("��ȡ������Ϣ");
		worldTable = new WorldTable(this);
		worldTable.start();
		if (isCancel())
		{
			return;
		}

		GamePacker.progress("����ͼ��Ĳ�����Ϣ");
		imageFrameTable = new ImageFrameTable(this);
		imageFrameTable.start();
		imageFrameTable.saveVer();
		if (isCancel())
		{
			return;
		}

		GamePacker.progress("���UIװ������");
		gameUIAttireWriter = new GameUIAttireWriter(this);
		gameUIAttireWriter.start();
		gameUIAttireWriter.saveVer();

		GamePacker.progress("���MP3�ļ�");
		mp3Writer = new Mp3Writer(this);
		mp3Writer.start();
		mp3Writer.saveVer();
		if (isCancel())
		{
			return;
		}

		GamePacker.progress("���ͼ����Ƭ");
		sliceImageWriter = new SliceImageWriter(this);
		sliceImageWriter.start();
		sliceImageWriter.saveVer();
		if (isCancel())
		{
			return;
		}

		GamePacker.progress("�����ͼ��");
		atlasWriter = new AtlasWriter(this);
		atlasWriter.start();
		atlasWriter.saveVer();
		if (isCancel())
		{
			return;
		}

		GamePacker.progress("���3D����");
		config3dWriter = new Config3d(this);
		config3dWriter.start();
		if (isCancel())
		{
			return;
		}

		GamePacker.progress("���2D����");
		config2dWriter = new Config2d(this);
		config2dWriter.start();
		if (isCancel())
		{
			return;
		}

		globalOptionTable.saveVer();

		writerVer();
	}

	private static final byte[] suffix = new byte[] { 0, 0, 0, 0, 0x4D, 0x44, 0x35, 0 };

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
