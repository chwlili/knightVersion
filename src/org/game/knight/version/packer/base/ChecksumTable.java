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


public class ChecksumTable
{
	private AbsExporter world;

	private File file;
	private Hashtable<String, String> checksumTable;
	private Hashtable<String, String> checksumIDTable;

	/**
	 * 构造函数
	 * 
	 * @param world
	 */
	public ChecksumTable(AbsExporter world)
	{
		this.world = world;
	}
	
	
	public String[] getKeys()
	{
		return checksumTable.keySet().toArray(new String[checksumTable.size()]);
	}
	
	/**
	 * 获取校验码
	 * @param path
	 * @return
	 */
	public String getChecksum(String path)
	{
		return checksumTable.get(path);
	}
	
	/**
	 * 获取校验码ID
	 * 
	 * @param path
	 * @return
	 */
	public String getChecksumID(String path)
	{
		return checksumIDTable.get(checksumTable.get(path))+"";
	}
	
	/**
	 * 添加一个校验码配置
	 * 
	 * @param path
	 * @param checksum
	 */
	public void add(String path, String checksum)
	{
		if (checksumTable.containsKey(path))
		{
			checksumTable.remove(path);
		}
		checksumTable.put(path, checksum);
		
		if(!checksumIDTable.containsKey(checksum))
		{
			checksumIDTable.put(checksum, world.getOptionTable().getNextChecksumID()+"");
		}
	}
	
	/**
	 * 修改一个校验码配置
	 * 
	 * @param path
	 * @param checksum
	 */
	public void set(String path, String checksum)
	{
		add(path,checksum);
	}

	/**
	 * 删除一个校验码配置
	 * 
	 * @param path
	 */
	public void del(String path)
	{
		if (checksumTable.containsKey(path))
		{
			checksumTable.remove(path);
		}
	}

	/**
	 * 打开校验码配置
	 * 
	 * @param file
	 */
	public void open(File file)
	{
		this.file = file;
		this.checksumTable = new Hashtable<String, String>();
		this.checksumIDTable = new Hashtable<String, String>();

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
				String id = parts[2].trim();

				checksumTable.put(key, val);
				checksumIDTable.put(val, id);
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
	 * 保存校验码配置
	 */
	public void save()
	{
		saveAs(file);
	}

	/**
	 * 另存校验码配置
	 * 
	 * @param file
	 */
	public void saveAs(File file)
	{
		if (file == null || checksumTable == null) { return; }

		StringBuilder sb = new StringBuilder();

		String[] keys = new String[checksumTable.size()];
		keys = checksumTable.keySet().toArray(keys);
		Arrays.sort(keys);

		for (int i = 0; i < keys.length; i++)
		{
			String key = keys[i];
			String val = checksumTable.get(key);
			String id = checksumIDTable.get(val);

			sb.append(key + " = " + val + " = " + id + "\n");
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
