package org.game.knight.version.packer.base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Hashtable;

import org.chw.util.FileUtil;
import org.chw.util.MD5Util;


public class FileTable
{
	private AbsExporter world;

	private File file;
	private Hashtable<String, String> filePathTable;

	private Hashtable<String, String> revisionTable;

	/**
	 * 构造函数
	 * 
	 * @param world
	 */
	public FileTable(AbsExporter world)
	{
		this.world = world;
	}
	
	/**
	 * 获取URL数组
	 * @param prev
	 * @return
	 */
	public String[] getURLs()
	{
		String[] list=new String[revisionTable.size()];
		list=revisionTable.values().toArray(list);
		return list;
	}
	
	/**
	 * 获取地址
	 * 
	 * @param xmlPath
	 * @return
	 */
	public String getUrl(String checksum)
	{
		if (filePathTable.containsKey(checksum))
		{
			revisionTable.put(checksum, filePathTable.get(checksum));
			filePathTable.remove(checksum);
		}
		return revisionTable.get(checksum);
	}

	/**
	 * 添加
	 * 
	 * @param path
	 * @param checksum
	 */
	public void add(String checksum, String path)
	{
		if (filePathTable.containsKey(checksum))
		{
			filePathTable.remove(checksum);
		}
		revisionTable.put(checksum, path);
	}

	/**
	 * 打开
	 * 
	 * @param file
	 */
	public void open(File file)
	{
		this.file = file;
		this.filePathTable = new Hashtable<String, String>();

		this.revisionTable = new Hashtable<String, String>();

		if (file == null || !file.exists() || !file.isFile()) { return; }

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
				String key = parts[0].trim();
				String val = parts[1].trim();

				filePathTable.put(key, val);
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
	 * 保存
	 */
	public void save()
	{
		saveAs(file);
	}

	/**
	 * 另存
	 * 
	 * @param file
	 */
	public void saveAs(File file)
	{
		if (file == null || revisionTable == null) { return; }

		StringBuilder sb = new StringBuilder();

		String[] keys = new String[revisionTable.size()];
		keys = revisionTable.keySet().toArray(keys);
		Arrays.sort(keys);

		for (int i = 0; i < keys.length; i++)
		{
			String key = keys[i];
			String val = revisionTable.get(key);

			sb.append(key + " = " + val + "\n");
		}

		for (String key : filePathTable.keySet())
		{
			// 删除旧文件
			File old = new File(world.getDestDir().getPath() +filePathTable.get(key));
			if (old.exists() && old.isFile())
			{
				//old.delete();
			}
		}

		try
		{
			byte[] bytes = sb.toString().getBytes("UTF-8");
			if(file.exists())
			{
				String oldSHA=MD5Util.md5File(file);
				String newSHA=MD5Util.md5Bytes(bytes);
				
				if(!oldSHA.equals(newSHA))
				{
					FileUtil.writeFile(file, bytes);
				}
			}
			else
			{
				FileUtil.writeFile(file, bytes);
			}
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}

}
