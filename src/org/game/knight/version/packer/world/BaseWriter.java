package org.game.knight.version.packer.world;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public abstract class BaseWriter
{
	protected final WorldWriter root;
	private final String verFileName;

	/**
	 * 构造函数
	 * 
	 * @param root
	 */
	public BaseWriter(WorldWriter root, String verFileName)
	{
		this.root = root;
		this.verFileName = verFileName;
	}

	/**
	 * 获取版本文件
	 * 
	 * @return
	 */
	private File getVerFile()
	{
		if (verFileName != null && !verFileName.isEmpty())
		{
			return new File(root.getOutputFolder().getPath() + File.separatorChar + ".ver" + File.separatorChar + verFileName);
		}
		return null;
	}

	/**
	 * 开始执行
	 * 
	 * @throws Exception
	 */
	public void run() throws Exception
	{
		startup();

		File file = getVerFile();
		if (file != null && file.exists() && file.isFile())
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf8"));

			try
			{
				readHistory(reader);
			}
			finally
			{
				reader.close();
			}
		}

		exec();
	}

	/**
	 * 启动
	 * 
	 * @throws Exception
	 */
	protected void startup() throws Exception
	{

	}

	/**
	 * 执行
	 */
	protected void exec() throws Exception
	{

	}

	/**
	 * 保存版本
	 */
	public void saveVer() throws Exception
	{
		File file = getVerFile();
		if (file != null && file.exists() && file.isFile())
		{
			if (!file.getParentFile().exists())
			{
				file.getParentFile().mkdirs();
			}

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf8"));

			try
			{
				saveHistory(writer);
			}
			finally
			{
				writer.close();
			}
		}
	}

	/**
	 * 读取历史
	 * 
	 * @param stream
	 * @throws Exception
	 */
	protected void readHistory(BufferedReader reader) throws Exception
	{

	}

	/**
	 * 保存历史
	 * 
	 * @param stream
	 * @throws Exception
	 */
	protected void saveHistory(BufferedWriter writer) throws Exception
	{

	}
}
