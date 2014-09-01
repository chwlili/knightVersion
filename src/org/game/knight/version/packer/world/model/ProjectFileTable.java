package org.game.knight.version.packer.world.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.chw.util.FileUtil;
import org.chw.util.MD5Util;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.world.BaseWriter;
import org.game.knight.version.packer.world.WorldWriter;

public class ProjectFileTable extends BaseWriter
{
	private int nextGID = 1;
	private HashMap<String, MD5> oldTable = new HashMap<String, MD5>();
	private HashMap<String, MD5> newTable = new HashMap<String, MD5>();

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
	public ProjectFileTable(WorldWriter root)
	{
		super(root, "md5");
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
	 * 获取所有MP3文件
	 * 
	 * @return
	 */
	public ProjectFile[] getAllMp3Files()
	{
		return url_mp3.values().toArray(new ProjectFile[url_mp3.size()]);
	}

	/**
	 * 获取所有ATF参数文件
	 * 
	 * @return
	 */
	public ProjectFile[] getAllParamFiles()
	{
		return url_texture.values().toArray(new ProjectFile[url_texture.size()]);
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
	 * 获取所有装扮文件
	 * 
	 * @return
	 */
	public ProjectFile[] getAllAttireFiles()
	{
		return url_attire.values().toArray(new ProjectFile[url_attire.size()]);
	}

	/**
	 * 获取所有链接文件
	 * 
	 * @return
	 */
	public ProjectFile[] getAllLinkFiles()
	{
		return url_link.values().toArray(new ProjectFile[url_link.size()]);
	}

	// -----------------------------------------------------------------------------------------------------------------------
	//
	// MD5计算
	//
	// -----------------------------------------------------------------------------------------------------------------------

	private File[] inputList;
	private int nextIndex;
	private int finishedCount;
	private String lastLog;

	/**
	 * 获取下一个文件
	 * 
	 * @return
	 */
	private synchronized File getNextFile()
	{
		File result = null;
		if (nextIndex < inputList.length)
		{
			result = inputList[nextIndex];
			lastLog = "MD5(" + nextIndex + "/" + inputList.length + "): " + result.getPath().substring(root.getInputFolder().getPath().length());
			nextIndex++;
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

		String gid = "";

		MD5 oldItem = oldTable.get(url);
		if (oldItem != null && oldItem.md5.equals(md5))
		{
			gid = oldItem.gid;
		}
		else
		{
			gid = nextGID + "";
			nextGID++;
		}

		if (!newTable.containsKey(url))
		{
			newTable.put(url, new MD5(url, md5, gid));
		}

		MD5 newItem = newTable.get(url);

		ProjectFile myFile = null;

		url = url.toLowerCase();
		if (url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".png") || url.endsWith(".gif"))
		{
			myFile = new ProjectImgFile(file, newItem.url, newItem.md5, newItem.gid);
			url_img.put(newItem.url, (ProjectImgFile) myFile);
		}
		else if (url.endsWith(".mp3"))
		{
			myFile = new ProjectMp3File(file, newItem.url, newItem.md5, newItem.gid);
			url_mp3.put(newItem.url, (ProjectMp3File) myFile);
		}
		else if (url.endsWith(".textures"))
		{
			myFile = new ProjectFile(file, newItem.url, newItem.md5, newItem.gid);
			url_texture.put(newItem.url, myFile);
		}
		else if (url.endsWith(".res") || url.endsWith(".attire"))
		{
			myFile = new ProjectFile(file, newItem.url, newItem.md5, newItem.gid);
			url_attire.put(newItem.url, myFile);
		}
		else if (url.endsWith(".scene"))
		{
			myFile = new ProjectFile(file, newItem.url, newItem.md5, newItem.gid);
			url_scene.put(newItem.url, myFile);
		}
		else if (url.endsWith(".link"))
		{
			myFile = new ProjectFile(file, newItem.url, newItem.md5, newItem.gid);
			url_link.put(newItem.url, myFile);
		}
		else
		{
			myFile = new ProjectFile(file, newItem.url, newItem.md5, newItem.gid);
		}
		url_file.put(newItem.url, myFile);
		gid_file.put(newItem.gid, myFile);

		finishedCount++;
	}

	/**
	 * 是否已经完成
	 * 
	 * @return
	 */
	private synchronized boolean isFinished()
	{
		return finishedCount >= inputList.length;
	}

	@Override
	protected void startup() throws Exception
	{
		GamePacker.log("开始检测文件变化");
	}

	/**
	 * 执行
	 */
	@Override
	public void exec() throws Exception
	{
		inputList = FileUtil.listFiles(root.getInputFolder());

		url_file = new HashMap<String, ProjectFile>();
		gid_file = new HashMap<String, ProjectFile>();

		url_img = new HashMap<String, ProjectImgFile>();
		url_mp3 = new HashMap<String, ProjectMp3File>();
		url_texture = new HashMap<String, ProjectFile>();
		url_attire = new HashMap<String, ProjectFile>();
		url_scene = new HashMap<String, ProjectFile>();
		url_link = new HashMap<String, ProjectFile>();

		ExecutorService exec = Executors.newCachedThreadPool();
		for (int i = 0; i < root.maxThreadCount; i++)
		{
			exec.execute(new Runnable()
			{
				@Override
				public void run()
				{
					while (true)
					{
						File file = getNextFile();
						if (root.isCancel() || file == null)
						{
							break;
						}

						try
						{
							finishFile(file, MD5Util.md5(file));
						}
						catch (Exception e)
						{
							root.cancel(e);
						}
					}
				}
			});
		}

		while (!root.isCancel() && !isFinished())
		{
			GamePacker.progress(lastLog);
			Thread.sleep(100);
		}

		exec.shutdown();
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

	// -----------------------------------------------------------------------------------------------------------------------
	//
	// 版本信息
	//
	// -----------------------------------------------------------------------------------------------------------------------

	/**
	 * 打开
	 */
	@Override
	protected void readHistory(InputStream input) throws Exception
	{
		GamePacker.progress("读取历史信息");

		BufferedReader reader = new BufferedReader(new InputStreamReader(input, "utf8"));
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

			if (line.charAt(0) == '$')
			{
				String[] values = line.substring(1).split("=");
				if (values.length == 2)
				{
					String name = values[0].trim();
					String value = values[1].trim();

					if ("nextID".equals(name))
					{
						nextGID = Integer.parseInt(value);
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

	@Override
	protected void saveHistory(OutputStream stream) throws Exception
	{
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream, "utf8"));

		// 写入NextID字段
		writer.write("$nextID = " + nextGID + "\n");

		// 排序MD5列表
		MD5[] values = newTable.values().toArray(new MD5[newTable.size()]);
		Arrays.sort(values, new Comparator<MD5>()
		{

			@Override
			public int compare(MD5 arg0, MD5 arg1)
			{
				int val1 = Integer.parseInt(arg0.gid);
				int val2 = Integer.parseInt(arg1.gid);
				return val1 - val2;
			}
		});

		// 写入MD5列表
		for (int i = 0; i < values.length; i++)
		{
			MD5 item = values[i];
			writer.write(item.url + " = " + item.md5 + " = " + item.gid);
			if (i < values.length - 1)
			{
				writer.write("\n");
			}
		}
	}
}
