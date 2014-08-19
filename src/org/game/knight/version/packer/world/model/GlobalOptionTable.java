package org.game.knight.version.packer.world.model;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;

import org.chw.util.FileUtil;
import org.game.knight.version.packer.world.task.RootTask;

public class GlobalOptionTable
{
	private static int FILE_COUNT_EACH_DIR = 1000;

	private static final String NEXT_ID = "nextID";

	private RootTask root;

	private int nextID = 1;
	private HashMap<String, String> oldTable = new HashMap<String, String>();
	private HashMap<String, String> newTable = new HashMap<String, String>();

	/**
	 * 构造函数
	 * 
	 * @param root
	 */
	public GlobalOptionTable(RootTask root)
	{
		this.root = root;
	}

	/**
	 * 开始
	 */
	public void start()
	{
		openVer();

		if (oldTable.containsKey(NEXT_ID))
		{
			nextID = Integer.parseInt(oldTable.get(NEXT_ID));
		}
		newTable.put(NEXT_ID, nextID + "");
	}

	/**
	 * 获取下一个导出文件
	 * 
	 * @return
	 */
	public synchronized String getNextExportFile()
	{
		long fileID = nextID;
		long folderID = (fileID - 1) / FILE_COUNT_EACH_DIR + 1;

		nextID++;

		newTable.put(NEXT_ID, nextID + "");

		return "/" + folderID + "/" + fileID;
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
		return new File(root.getOutputFolder().getPath() + File.separatorChar + ".ver" + File.separatorChar + "option");
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
				if (line.isEmpty())
				{
					continue;
				}

				String[] items = line.split("=");
				if (items.length == 2)
				{
					String key = items[0].trim();
					String val = items[1].trim();

					oldTable.put(key, val);
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
	public void saveVer()
	{
		if (newTable == null)
		{
			return;
		}

		String[] keys = newTable.keySet().toArray(new String[newTable.size()]);
		Arrays.sort(keys);

		StringBuilder output = new StringBuilder();
		for (String key : keys)
		{
			output.append(key + " = " + newTable.get(key) + "\n");
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
