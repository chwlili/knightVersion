package org.game.knight.version.packer.base;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.game.knight.version.packer.GamePackerConst;

public class ZipConfig
{
	private File file;

	private HashMap<String, byte[]> map = null;

	/**
	 * ���캯��
	 * 
	 */
	public ZipConfig()
	{
		this.file = null;
	}

	/**
	 * ���캯��
	 * 
	 * @param file
	 */
	public ZipConfig(File file)
	{
		this.file = file;
	}

	/**
	 * ��ȡ�ļ�
	 * 
	 * @return
	 */
	public File getFile()
	{
		return file;
	}

	// ----------------------------------------------------------------------------------

	private String version = null;
	private HashMap<String, String> versionProps = null;
	private ArrayList<String> versionFiles = null;
	private HashMap<String, String> gameFiles = null;
	private HashMap<String, byte[]> cfgFiles = null;

	/**
	 * ��ȡVersion����
	 * 
	 * @return
	 */
	public String getVersion()
	{
		if (version == null)
		{
			version = getEntryContent("core/version.txt");
			if (version == null)
			{
				version = "";
			}
		}
		return version;
	}

	/**
	 * ����Version����
	 * 
	 * @param content
	 */
	public void setVersion(String content)
	{
		version = content != null ? content : "";
	}

	/**
	 * ��ȡVersion���Ա�
	 * 
	 * @return
	 */
	public HashMap<String, String> getVersionProps()
	{
		if (versionProps == null)
		{
			versionProps = readMap("core/versionProps.txt");
		}

		return versionProps;
	}

	/**
	 * ����Version���Ա�
	 * 
	 * @param content
	 */
	public void setVersionProps(HashMap<String, String> map)
	{
		versionProps = map != null ? map : new HashMap<String, String>();
	}

	/**
	 * ��ȡ��һ���ļ���URL
	 * 
	 * @return
	 */
	public String getVersionNextGameFileURL(String ext)
	{
		long fileID = getVersionProps().containsKey("$nextFileID") ? Long.parseLong(getVersionProps().get("$nextFileID")) : 10001;
		long folderID = (fileID - 1) / GamePackerConst.FILE_COUNT_EACH_DIR + 1;

		String filePath = "/" + folderID + "/" + fileID + "." + ext;

		fileID++;

		getVersionProps().put("$nextFileID", fileID + "");

		return filePath;
	}

	/**
	 * ��ȡ��һ��KEY��ID
	 * 
	 * @param key
	 * @return
	 */
	public long getVersionNextTypeID()
	{
		long typeID = getVersionProps().containsKey("$nextTypeID") ? Long.parseLong(getVersionProps().get("$nextTypeID")) : 1;

		getVersionProps().put("$nextTypeID", (typeID + 1) + "");

		return typeID;
	}

	/**
	 * ��ȡURL��
	 * 
	 * @return
	 */
	public ArrayList<String> getVersionFiles()
	{
		if (versionFiles == null)
		{
			versionFiles = new ArrayList<String>();
			String text = getEntryContent("core/versionFiles.txt");
			if (text != null)
			{
				String[] lines = text.split("\n");
				for (String line : lines)
				{
					versionFiles.add(line);
				}
			}
		}
		return versionFiles;
	}

	/**
	 * ��ȡ��Ϸ�ļ���
	 * 
	 * @return
	 */
	public HashMap<String, String> getGameFiles()
	{
		if (gameFiles == null)
		{
			gameFiles = readMap("core/gameFiles.txt");
		}
		return gameFiles;
	}

	/**
	 * ��ȡ�����ļ���
	 * 
	 * @return
	 */
	public HashMap<String, byte[]> getCfgFiles()
	{
		if (cfgFiles == null)
		{
			open();

			cfgFiles = new HashMap<String, byte[]>();
			for (String key : map.keySet())
			{
				if (key.startsWith("cfgs/"))
				{
					cfgFiles.put(key.substring(5), map.get(key));
				}
			}
		}
		return cfgFiles;
	}

	/**
	 * ��ȡMAP
	 * 
	 * @param url
	 * @return
	 */
	private HashMap<String, String> readMap(String url)
	{
		HashMap<String, String> map = new HashMap<String, String>();
		if (url != null)
		{
			String text = getEntryContent(url);
			if (text != null)
			{
				String[] lines = text.split("\n");
				for (String line : lines)
				{
					line = line.trim();
					if (!line.isEmpty())
					{
						String[] pair = line.split("=");
						if (pair.length >= 2)
						{
							String key = pair[0].trim();
							String val = pair[1].trim();

							map.put(key, val);
						}
					}
				}
			}
		}
		return map;
	}

	/**
	 * ����MAP
	 * 
	 * @param url
	 * @param map
	 */
	private void saveMap(String url, HashMap<String, String> map)
	{
		try
		{
			StringBuilder sb = new StringBuilder();

			if (map != null)
			{
				String[] keys = map.keySet().toArray(new String[] {});
				Arrays.sort(keys);
				for (String key : keys)
				{
					sb.append(key + " = " + map.get(key) + "\n");
				}
				setEntry(url, sb.toString().getBytes("UTF-8"));
			}
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * ����֮ǰ
	 */
	protected void prevSave()
	{
		try
		{
			// version
			if (version != null)
			{
				setEntry("core/version.txt", version.getBytes("UTF-8"));
			}

			// versionProps
			if (versionProps != null)
			{
				saveMap("core/versionProps.txt", versionProps);
			}

			// versionFiles
			if (versionFiles != null)
			{
				HashSet<String> lineSet = new HashSet<String>();
				Collections.sort(versionFiles);
				StringBuilder sb = new StringBuilder();
				for (String line : versionFiles)
				{
					if (!lineSet.contains(line))
					{
						sb.append(line + "\n");
						lineSet.add(line);
					}
				}
				setEntry("core/versionFiles.txt", sb.toString().getBytes("UTF-8"));
			}

			// gameFiles
			if (gameFiles != null)
			{
				saveMap("core/gameFiles.txt", gameFiles);
			}

			// cfgFiles
			if (cfgFiles != null)
			{
				String[] names = cfgFiles.keySet().toArray(new String[] {});
				Arrays.sort(names);
				for (String name : names)
				{
					setEntry("cfgs/" + name, cfgFiles.get(name));
				}
			}
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}

	// ----------------------------------------------------------------------------------

	/**
	 * ��ȡ�ļ�
	 * 
	 * @param url
	 * @return
	 */
	protected byte[] getEntry(String url)
	{
		open();
		return map.get(url);
	}

	/**
	 * �����ļ�
	 * 
	 * @param url
	 * @param bytes
	 */
	protected void setEntry(String url, byte[] bytes)
	{
		open();
		map.put(url, bytes);
	}

	/**
	 * ��ȡ�ļ�����
	 * 
	 * @param url
	 * @return
	 */
	protected String getEntryContent(String url)
	{
		byte[] bytes = getEntry(url);
		if (bytes != null)
		{
			try
			{
				return new String(bytes, "UTF-8");
			}
			catch (UnsupportedEncodingException err)
			{
			}
		}
		return null;
	}

	// ----------------------------------------------------------------------------------

	/**
	 * ��
	 * 
	 * @throws IOException
	 * @throws ZipException
	 */
	protected void open()
	{
		if (map != null)
		{
			return;
		}

		map = new HashMap<String, byte[]>();

		if (file == null || !file.exists() || !file.isFile())
		{
			return;
		}

		ZipFile zipFile = null;
		try
		{
			zipFile = new ZipFile(file);

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

				map.put(entry.getName(), bytes);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (zipFile != null)
			{
				try
				{
					zipFile.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * ����
	 * 
	 * @throws IOException
	 */
	protected void save() throws IOException
	{
		if (file == null)
		{
			return;
		}

		// ����Ŀ���ļ����ݵ�MD5��
		String oldContent = null;
		if (file.exists() && file.length() > 0)
		{
			ZipConfig oldZip = new ZipConfig(file);
			oldZip.open();

			String[] oldKeys = oldZip.map.keySet().toArray(new String[] {});
			Arrays.sort(oldKeys);

			StringBuilder text = new StringBuilder();
			for (String key : oldKeys)
			{
				text.append(key + "\n");
				text.append(oldZip.getEntryContent(key) + "\n");
			}
			oldContent = text.toString();
		}

		// ���㵱ǰ���ݵ�MD5��
		String[] keys = map.keySet().toArray(new String[map.size()]);
		Arrays.sort(keys);
		StringBuilder text = new StringBuilder();
		for (String key : keys)
		{
			text.append(key + "\n");
			text.append(getEntryContent(key) + "\n");
		}
		String newContent = text.toString();

		// ��������б仯��������д�롣
		if (oldContent == null || !newContent.equals(oldContent))
		{
			if (!file.getParentFile().exists())
			{
				file.getParentFile().mkdir();
			}

			ZipOutputStream stream = null;
			try
			{
				stream = new ZipOutputStream(new FileOutputStream(file));
				for (String key : keys)
				{
					stream.putNextEntry(new ZipEntry(key));
					stream.write(map.get(key));
					stream.flush();
				}
				stream.finish();
			}
			finally
			{
				stream.close();
			}
		}
	}

	/**
	 * ���浽ָ���ļ�
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void saveTo(File file) throws IOException
	{
		File old = this.file;

		try
		{
			this.file = file;
			prevSave();
			save();
		}
		finally
		{
			this.file = old;
		}
	}
}
