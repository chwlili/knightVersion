package org.game.knight.version.packer.world;

import java.io.File;

public class Mp3File
{
	private File file;
	private String innerPath;

	/**
	 * ���캯��
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
	 * ��ȡ�ļ�
	 * @return
	 */
	public File getFile()
	{
		return file;
	}
	
	/**
	 * ��ȡ�ڲ�·��
	 * @return
	 */
	public String getInnerpath()
	{
		return innerPath;
	}
}
