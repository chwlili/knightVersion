package org.game.knight.version.packer.icon;

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


public class IconSizeTable
{
	private File file;
	private Hashtable<String, String> fileWidthTable;
	private Hashtable<String, String> fileHeightTable;

	private Hashtable<String, String> revisionWidthTable;
	private Hashtable<String, String> revisionHeightTable;


	/**
	 * 添加
	 * 
	 * @param path
	 * @param checksum
	 */
	public void add(String key, String w,String h)
	{
		revisionWidthTable.put(key, w);
		revisionHeightTable.put(key, h);
	}
	
	/**
	 * 获取宽度
	 * @param checksum
	 * @return
	 */
	public String getWidth(String key)
	{
		if (fileWidthTable.containsKey(key))
		{
			revisionWidthTable.put(key, fileWidthTable.get(key));
			fileWidthTable.remove(key);
		}

		if (fileHeightTable.containsKey(key))
		{
			revisionHeightTable.put(key, fileHeightTable.get(key));
			fileHeightTable.remove(key);
		}
		return revisionWidthTable.get(key);
	}
	
	/**
	 * 获取高度
	 * @param checksum
	 * @return
	 */
	public String getHeight(String key)
	{
		if (fileWidthTable.containsKey(key))
		{
			revisionWidthTable.put(key, fileWidthTable.get(key));
			fileWidthTable.remove(key);
		}

		if (fileHeightTable.containsKey(key))
		{
			revisionHeightTable.put(key, fileHeightTable.get(key));
			fileHeightTable.remove(key);
		}
		return revisionHeightTable.get(key);
	}

	/**
	 * 打开
	 * 
	 * @param file
	 */
	public void open(File file)
	{
		this.file = file;
		this.fileWidthTable = new Hashtable<String, String>();
		this.fileHeightTable = new Hashtable<String, String>();

		this.revisionWidthTable = new Hashtable<String, String>();
		this.revisionHeightTable = new Hashtable<String, String>();

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
				String w = parts[1].trim();
				String h = parts[2].trim();
				
				fileWidthTable.put(key, w);
				fileHeightTable.put(key, h);
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
		if (file == null || revisionWidthTable == null) { return; }

		StringBuilder sb = new StringBuilder();

		String[] keys = new String[revisionWidthTable.size()];
		keys = revisionWidthTable.keySet().toArray(keys);
		Arrays.sort(keys);

		for (int i = 0; i < keys.length; i++)
		{
			String key = keys[i];
			String w = revisionWidthTable.get(key);
			String h = revisionHeightTable.get(key);

			sb.append(key + " = " +  w + " = " + h + "\n");
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
