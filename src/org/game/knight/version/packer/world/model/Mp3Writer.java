package org.game.knight.version.packer.world.model;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import org.chw.util.FileUtil;
import org.chw.util.MD5Util;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.world.task.RootTask;

public class Mp3Writer
{
	private RootTask root;
	private HashMap<String, String> newTable;
	private HashMap<String, String> oldTable;

	/**
	 * 构造函数
	 * 
	 * @param root
	 */
	public Mp3Writer(RootTask root)
	{
		this.root = root;
	}

	/**
	 * 开始
	 */
	public void start()
	{
		openVer();

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

			try
			{
				FileUtil.copyTo(file, mp3);
				root.addFileSuffix(file);
				add(mp3, url);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				GamePacker.error(e);
				root.cancel();
				return;
			}
		}
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

	/**
	 * 获取版本文件
	 * 
	 * @return
	 */
	private File getVerFile()
	{
		return new File(root.getOutputFolder().getPath() + File.separatorChar + ".ver" + File.separatorChar + "mp3");
	}

	/**
	 * 打开版本信息
	 */
	private void openVer()
	{
		this.oldTable = new HashMap<String, String>();
		this.newTable = new HashMap<String, String>();

		if (!getVerFile().exists())
		{
			return;
		}

		String text = null;

		try
		{
			text = new String(FileUtil.getFileBytes(getVerFile()), "utf8");
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return;
		}

		String[] lines = text.split("\\n");
		for (String line : lines)
		{
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

	/**
	 * 保存版本信息
	 */
	public void saveVer()
	{
		StringBuilder output = new StringBuilder();

		if (newTable == null)
		{
			return;
		}

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

		for (int i = 0; i < keys.length; i++)
		{
			String key = keys[i];
			String url = newTable.get(key);

			output.append(key + " = " + url);

			if (i < keys.length - 1)
			{
				output.append("\n");
			}
		}

		try
		{
			FileUtil.writeFile(getVerFile(), output.toString().getBytes("utf8"));
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			GamePacker.error(e);
			return;
		}

		// 记录输出文件
		for (String url : newTable.values())
		{
			root.addOutputFile(url);
		}
	}
}
