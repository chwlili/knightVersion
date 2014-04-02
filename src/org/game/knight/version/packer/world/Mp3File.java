package org.game.knight.version.packer.world;

import java.io.File;

public class Mp3File
{
	private File file;
	private String innerPath;

	/**
	 * 构造函数
	 * @param file
	 * @param innerPath
	 * @param innerDirPath
	 */
	public Mp3File(File file,String innerPath,String innerDirPath)
	{
		this.file=file;
		this.innerPath=innerPath;
	}
	
	/**
	 * 获取文件
	 * @return
	 */
	public File getFile()
	{
		return file;
	}
	
	/**
	 * 获取内部路径
	 * @return
	 */
	public String getInnerpath()
	{
		return innerPath;
	}
}
