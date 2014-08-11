package org.game.knight.version.packer.world.model;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;

import org.chw.util.FileUtil;
import org.chw.util.PathUtil;
import org.game.knight.version.packer.GamePackerConst;
import org.game.knight.version.packer.world.task.RootTask;

public class WriteFileTable
{
	private static int FILE_COUNT_EACH_DIR=1000;
	
	private RootTask root;
	
	private int nextID = 0;
	private HashMap<String, String> oldTable = new HashMap<String, String>();
	private HashMap<String, String> newTable = new HashMap<String, String>();

	/**
	 * 构造函数
	 * 
	 * @param root
	 */
	public WriteFileTable(RootTask root)
	{
		this.root = root;
	}

	/**
	 * 开始
	 */
	public void start()
	{
		openVer();
	}
	
	/**
	 * 获取下一个导出ID
	 * @return
	 */
	public synchronized int getNextExportID()
	{
		return nextID++;
	}
	
	/**
	 * 添加已经导出的文件
	 * @param key
	 * @param url
	 */
	public synchronized void addExportedFile(String key,String url)
	{
		newTable.put(key, url);
	}
	
	/**
	 * 获取指定键的导出路径
	 * @param key
	 * @return
	 */
	public synchronized String getExportedURL(String key)
	{
		if(oldTable.containsKey(key))
		{
			newTable.put(key, oldTable.get(key));
		}
		return newTable.get(key);
	}
	
	/**
	 * 获取指定键的导出文件
	 * @param key
	 * @return
	 */
	public synchronized File getExportedFile(String key)
	{
		if(oldTable.containsKey(key))
		{
			newTable.put(key, oldTable.get(key));
		}
		if(newTable.containsKey(key))
		{
			return new File(root.getOutputFolder().getPath()+newTable.get(key));
		}
		return null;
	}

	// ------------------------------------------------------------------------------------------------------------
	//
	// 查找需要切割的图像
	//
	// ------------------------------------------------------------------------------------------------------------
	/**
	 * 获取版本文件
	 * 
	 * @return
	 */
	private File getVerFile()
	{
		return new File(root.getOutputFolder().getPath() + File.separatorChar + ".ver" + File.separatorChar + ".write");
	}

	/**
	 * 打开版本信息
	 */
	private void openVer()
	{
		this.nextID = 1;
		this.oldTable = new HashMap<String, String>();
		this.newTable = new HashMap<String, String>();

		if (!getVerFile().exists())
		{
			return;
		}

		try
		{
			String text = new String(FileUtil.getFileBytes(getVerFile()), "utf8");
			String[] lines = text.split("\\n");
			for (String line : lines)
			{
				line = line.trim();

				if (line.startsWith("\\$"))
				{
					String[] values = line.substring(1).split("=");
					if (values.length == 2)
					{
						String name = values[0].trim();
						String value = values[1].trim();

						if ("nextID".equals(name))
						{
							nextID = Integer.parseInt(value);
						}
					}
				}
				else
				{
					String[] values = line.split("=");
					if (values.length == 2)
					{
						String key = values[0].trim();
						String url = values[1].trim();
						
						oldTable.put(key, url);
					}
				}
			}
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 保存版本信息
	 */
	private void saveVer()
	{
		StringBuilder output = new StringBuilder();

		output.append("$nextID = " + nextID + "\n");

		if (newTable != null)
		{
			String[] keys = newTable.keySet().toArray(new String[newTable.size()]);
			Arrays.sort(keys);

			for (String key : keys)
			{
				output.append(key + " = " + newTable.get(key) + "\n");
			}
		}

		try
		{
			FileUtil.writeFile(getVerFile(), output.toString().getBytes("utf8"));
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}
}
