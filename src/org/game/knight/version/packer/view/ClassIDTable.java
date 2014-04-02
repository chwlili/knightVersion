package org.game.knight.version.packer.view;

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


public class ClassIDTable
{
	private ViewExport world;
	
	private File file;
	private Hashtable<String, String> classIDTable;
	private Hashtable<String, String> revisionTable;

	/**
	 * 构造函数
	 * 
	 * @param world
	 */
	public ClassIDTable(ViewExport world)
	{
		this.world=world;
	}

	/**
	 * 获取地址
	 * 
	 * @param path
	 * @return
	 */
	public long getClassID(String checksum)
	{
		if(classIDTable.containsKey(checksum))
		{
			revisionTable.put(checksum, classIDTable.get(checksum));
			classIDTable.remove(checksum);
		}
		
		if(!revisionTable.containsKey(checksum))
		{
			revisionTable.put(checksum, world.getOptionTable().getNextClassID()+"");
		}
		
		return Long.parseLong(revisionTable.get(checksum));
	}

	/**
	 * 打开
	 * 
	 * @param file
	 */
	public void open(File file)
	{
		this.file = file;
		this.classIDTable = new Hashtable<String, String>();
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

				classIDTable.put(key, val);
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

}
