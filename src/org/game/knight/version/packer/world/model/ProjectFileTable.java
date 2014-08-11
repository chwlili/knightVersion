package org.game.knight.version.packer.world.model;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.chw.util.FileUtil;
import org.chw.util.MD5Util;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.world.task.RootTask;

public class ProjectFileTable
{
	private final RootTask root;
	
	private String lastLog;
	
	private HashMap<String, ProjectFile> url_file;
	private HashMap<String, ProjectFile> gid_file;

	private HashMap<String, ProjectImgFile> url_img;
	private HashMap<String, ProjectMp3File> url_mp3;
	private HashMap<String, ProjectFile> url_texture;
	private HashMap<String, ProjectFile> url_attire;
	private HashMap<String, ProjectFile> url_scene;
	private HashMap<String, ProjectFile> url_link;

	/**
	 * 构造函数
	 * 
	 * @param root
	 */
	public ProjectFileTable(RootTask root)
	{
		this.root = root;
	}

	/**
	 * 按GID获取文件
	 * 
	 * @param gid
	 * @return
	 */
	public ProjectFile getFileByGID(String gid)
	{
		return gid_file.get(gid);
	}

	/**
	 * 获取图像文件
	 * 
	 * @param url
	 * @return
	 */
	public ProjectImgFile getImgFile(String url)
	{
		return url_img.get(url);
	}

	/**
	 * 获取MP3文件
	 * 
	 * @param url
	 * @return
	 */
	public ProjectMp3File getMp3File(String url)
	{
		return url_mp3.get(url);
	}

	/**
	 * 获取场景文件
	 * 
	 * @param url
	 * @return
	 */
	public ProjectFile getSceneFile(String url)
	{
		return url_scene.get(url);
	}

	/**
	 * 获取所有ATF参数文件
	 * 
	 * @return
	 */
	public ProjectFile[] getParamFiles()
	{
		return url_texture.values().toArray(new ProjectFile[url_texture.size()]);
	}

	/**
	 * 获取所有装扮文件
	 * 
	 * @return
	 */
	public ProjectFile[] getAttireFiles()
	{
		return url_attire.values().toArray(new ProjectFile[url_attire.size()]);
	}

	/**
	 * 获取所有链接文件
	 * 
	 * @return
	 */
	public ProjectFile[] getLinkFiles()
	{
		return url_link.values().toArray(new ProjectFile[url_link.size()]);
	}

	// -----------------------------------------------------------------------------------------------------------------------
	//
	// MD5计算
	//
	// -----------------------------------------------------------------------------------------------------------------------

	private File[] inputFiles;
	private int index;
	private ArrayList<ProjectFile> finishFiles;

	/**
	 * 执行
	 */
	public void start()
	{
		openVerFile();

		finishFiles = new ArrayList<ProjectFile>();
		inputFiles = FileUtil.listFiles(root.getInputFolder());

		ExecutorService exec = Executors.newCachedThreadPool();
		for (int i = 0; i < 5; i++)
		{
			exec.execute(new Runnable()
			{
				@Override
				public void run()
				{
					while (true)
					{
						if (root.isCancel())
						{
							break;
						}

						File file = getNextFile();
						if (file == null)
						{
							break;
						}
						
						finishFile(file, MD5Util.md5File(file));
						Thread.yield();
					}
				}
			});
		}
		
		while (!root.isCancel() && !isFinished())
		{
			try
			{
				GamePacker.progress(lastLog);
				Thread.yield();
				Thread.sleep(50);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				break;
			}
		}
		
		if (isFinished())
		{
			initFileTable();
		}
		else
		{
			//
		}
	}

	/**
	 * 是否已经完成
	 * 
	 * @return
	 */
	private synchronized boolean isFinished()
	{
		return finishFiles.size() == inputFiles.length;
	}

	/**
	 * 获取下一个文件
	 * 
	 * @return
	 */
	private synchronized File getNextFile()
	{
		File result = null;
		if (index < inputFiles.length)
		{
			result = inputFiles[index];
			index++;
		}
		return result;
	}

	/**
	 * 完成一个文件
	 * 
	 * @param file
	 * @param md5
	 */
	private synchronized void finishFile(File file, String md5)
	{
		String url = file.getPath().substring(root.getInputFolder().getPath().length()).replaceAll("\\\\", "/");

		lastLog="md5(" + finishFiles.size() + "/" + inputFiles.length + "): " + md5+" - "+ url;

		String gid = "";

		MD5 oldItem = oldTable.get(url);
		if (oldItem != null && oldItem.md5.equals(md5))
		{
			gid = oldItem.gid;
		}
		else
		{
			gid = nextID + "";
			nextID++;
		}

		if (!newTable.containsKey(url))
		{
			newTable.put(url, new MD5(url, md5, gid));
		}

		MD5 newItem = newTable.get(url);
		
		url=url.toLowerCase();
		if (url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".png") || url.endsWith(".gif"))
		{
			finishFiles.add(new ProjectImgFile(file, newItem.url, newItem.md5, newItem.gid));
		}
		else if (url.endsWith(".mp3"))
		{
			finishFiles.add(new ProjectMp3File(file, newItem.url, newItem.md5, newItem.gid));
		}
		else
		{
			finishFiles.add(new ProjectFile(file, newItem.url, newItem.md5, newItem.gid));
		}
	}

	/**
	 * 初始化文件表
	 * 
	 * @param finishFiles
	 */
	private void initFileTable()
	{
		this.url_file = new HashMap<String, ProjectFile>();
		this.gid_file = new HashMap<String, ProjectFile>();

		this.url_img = new HashMap<String, ProjectImgFile>();
		this.url_mp3 = new HashMap<String, ProjectMp3File>();
		this.url_texture = new HashMap<String, ProjectFile>();
		this.url_attire = new HashMap<String, ProjectFile>();
		this.url_scene = new HashMap<String, ProjectFile>();
		this.url_link = new HashMap<String, ProjectFile>();

		for (ProjectFile file : finishFiles)
		{
			url_file.put(file.url, file);
			gid_file.put(file.gid, file);

			String url = file.url.toLowerCase();
			if (url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".png") || url.endsWith(".gif"))
			{
				url_img.put(file.url, (ProjectImgFile)file);
			}
			else if (url.endsWith(".mp3"))
			{
				url_mp3.put(file.url, (ProjectMp3File)file);
			}
			else if (url.endsWith(".textures"))
			{
				url_texture.put(file.url, file);
			}
			else if (url.endsWith(".res") || url.endsWith(".attire"))
			{
				url_attire.put(file.url, file);
			}
			else if (url.endsWith(".scene"))
			{
				url_scene.put(file.url, file);
			}
			else if (url.endsWith(".link"))
			{
				url_link.put(file.url, file);
			}
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------
	//
	// 版本信息
	//
	// -----------------------------------------------------------------------------------------------------------------------

	private int nextID;
	private HashMap<String, MD5> oldTable;
	private HashMap<String, MD5> newTable;

	/**
	 * 获取版本文件
	 * 
	 * @return
	 */
	private File getVerFile()
	{
		return new File(root.getOutputFolder().getPath() + File.separatorChar + ".ver" + File.separatorChar + ".md5");
	}

	/**
	 * 打开
	 */
	private void openVerFile()
	{
		this.nextID = 1;
		this.oldTable = new HashMap<String, MD5>();
		this.newTable = new HashMap<String, MD5>();

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
					if (values.length == 3)
					{
						String url = values[0].trim();
						String md5 = values[1].trim();
						String gid = values[2].trim();

						oldTable.put(url, new MD5(url, md5, gid));
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
	 * 保存
	 */
	private void saveVerFile()
	{
		StringBuilder output = new StringBuilder();

		output.append("$nextID = " + nextID + "\n");

		if (newTable != null)
		{
			String[] keys = newTable.keySet().toArray(new String[newTable.size()]);
			Arrays.sort(keys);
			for (String key : keys)
			{
				MD5 item = newTable.get(key);

				output.append(item.url + " = " + item.md5 + " = " + item.gid + "\n");
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

	/**
	 * MD5项
	 * 
	 * @author ds
	 * 
	 */
	private static class MD5
	{
		/**
		 * URL
		 */
		public final String url;

		/**
		 * MD5值
		 */
		public final String md5;

		/**
		 * 全局ID
		 */
		public final String gid;

		/**
		 * 构造函数
		 * 
		 * @param url
		 * @param md5
		 * @param gid
		 */
		public MD5(String url, String md5, String gid)
		{
			this.url = url;
			this.md5 = md5;
			this.gid = gid;
		}
	}

}
