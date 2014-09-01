package org.game.knight.version.packer.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

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
		if (file != null)
		{
			readHistory(new FileInputStream(file));
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
		if (file != null)
		{
			saveHistory(new FileOutputStream(file));
		}
	}

	/**
	 * 读取历史
	 * 
	 * @param stream
	 * @throws Exception
	 */
	protected void readHistory(InputStream stream) throws Exception
	{

	}

	/**
	 * 保存历史
	 * 
	 * @param stream
	 * @throws Exception
	 */
	protected void saveHistory(OutputStream stream) throws Exception
	{

	}
}
