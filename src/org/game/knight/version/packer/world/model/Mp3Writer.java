package org.game.knight.version.packer.world.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import org.chw.util.FileUtil;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.world.BaseWriter;
import org.game.knight.version.packer.world.WorldWriter;

public class Mp3Writer extends BaseWriter
{
	private HashMap<String, String> newTable = new HashMap<String, String>();
	private HashMap<String, String> oldTable = new HashMap<String, String>();

	/**
	 * 构造函数
	 * 
	 * @param root
	 */
	public Mp3Writer(WorldWriter root)
	{
		super(root, "mp3");
	}

	/**
	 * 获取MP3文件的URL
	 * 
	 * @param file
	 * @return
	 */
	public String getMp3URL(ProjectFile file)
	{
		return newTable.get(file.gid);
	}

	@Override
	protected void startup() throws Exception
	{
		GamePacker.log("开始输出MP3文件");
	}

	@Override
	protected void exec() throws Exception
	{
		ArrayList<ProjectFile> newMp3s = new ArrayList<ProjectFile>();

		ProjectFile[] mp3s = root.getFileTable().getAllMp3Files();
		for (ProjectFile mp3 : mp3s)
		{
			if (!activate(mp3))
			{
				newMp3s.add(mp3);
			}
		}

		for (int i = 0; i < newMp3s.size(); i++)
		{
			ProjectFile mp3 = newMp3s.get(i);

			String url = root.getGlobalOptionTable().getNextExportFile() + ".mp3";
			File file = new File(root.getOutputFolder().getPath() + url);

			GamePacker.progress("输出MP3文件(" + (i + 1) + "/" + newMp3s.size() + ")：" + url);

			FileUtil.copyTo(file, mp3);
			root.addFileSuffix(file);
			add(mp3, url);
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	//
	// 版本信息
	//
	// -------------------------------------------------------------------------------------------------------------------

	/**
	 * 激活
	 * 
	 * @param file
	 * @return
	 */
	private boolean activate(ProjectFile file)
	{
		if (oldTable.containsKey(file.gid))
		{
			newTable.put(file.gid, oldTable.get(file.gid));
		}
		return newTable.containsKey(file.gid);
	}

	/**
	 * 添加
	 * 
	 * @param file
	 * @return
	 */
	private void add(ProjectFile file, String url)
	{
		newTable.put(file.gid, url);
	}

	@Override
	protected void readHistory(BufferedReader reader) throws Exception
	{
		while (true)
		{
			String line = reader.readLine();
			if (line == null)
			{
				break;
			}

			line = line.trim();
			if (line.isEmpty())
			{
				continue;
			}

			String[] items = line.split("=");
			if (items.length != 2)
			{
				continue;
			}

			String key = items[0].trim();
			if (key.isEmpty())
			{
				continue;
			}

			String url = items[1].trim();
			if (url.isEmpty())
			{
				continue;
			}

			oldTable.put(key, url);
		}
	}

	@Override
	protected void saveHistory(BufferedWriter writer) throws Exception
	{
		// 排序
		String[] keys = newTable.keySet().toArray(new String[newTable.size()]);
		Arrays.sort(keys, new Comparator<String>()
		{
			@Override
			public int compare(String arg0, String arg1)
			{
				int val1 = Integer.parseInt(arg0);
				int val2 = Integer.parseInt(arg1);
				return val1 - val2;
			}
		});

		// 写入输出流
		for (int i = 0; i < keys.length; i++)
		{
			String key = keys[i];
			String url = newTable.get(key);

			writer.write(key + " = " + url);

			if (i < keys.length - 1)
			{
				writer.write("\n");
			}
		}

		// 记录输出文件
		for (String url : newTable.values())
		{
			root.addOutputFile(url);
		}
	}
}
