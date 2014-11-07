package org.game.knight.version.packer.cfg;

import java.io.File;

import org.chw.util.FileUtil;
import org.chw.util.MD5Util;

class XmlFile
{
	private File outputFolder;
	private String fileName;
	private String fileURL;
	private String fileMD5;

	private File file;
	private byte[] bytes;

	private boolean is2d;
	private String buildName;
	private String buildURL;

	public XmlFile(File outputFolder, String fileName, String fileURL, byte[] bytes)
	{
		this(outputFolder, fileName, fileURL, null, bytes);
	}

	public XmlFile(File outputFolder, String fileName, String fileURL, File file)
	{
		this(outputFolder, fileName, fileURL, file, null);
	}

	private XmlFile(File outputFolder, String fileName, String fileURL, File file, byte[] bytes)
	{
		this.outputFolder = outputFolder;
		this.fileName = fileName;
		this.fileURL = fileURL;
		this.bytes = null;
		this.file = file;
		this.bytes = bytes;

		is2d = fileName.endsWith(".2d.xml");
		buildName = fileName.replaceAll("\\.2d\\.xml", "").replaceAll("\\.xml", "");
		buildURL = fileURL.replaceAll("\\.2d\\.xml", ".xml");
	}

	public boolean is2D()
	{
		return is2d;
	}

	public boolean is3D()
	{
		return !is2d;
	}

	public String getBuildName()
	{
		return buildName;
	}

	public String getBuildPath()
	{
		return buildURL;
	}

	/**
	 * 输出目录
	 * 
	 * @return
	 */
	public File getOutputFolder()
	{
		return outputFolder;
	}

	/**
	 * 文件名称
	 * 
	 * @return
	 */
	public String getFileName()
	{
		return fileName;
	}

	/**
	 * 文件URL
	 * 
	 * @return
	 */
	public String getFileURL()
	{
		return fileURL;
	}

	/**
	 * 文件字节数组
	 * 
	 * @return
	 */
	public byte[] getFileBytes()
	{
		if (bytes != null)
		{
			return bytes;
		}
		if (file != null)
		{
			return FileUtil.getFileBytes(file);
		}
		return null;
	}

	/**
	 * 文件MD5
	 * 
	 * @return
	 */
	public String getFileMD5()
	{
		if (fileMD5 == null)
		{
			fileMD5 = MD5Util.md5Bytes(getFileBytes());
		}
		return fileMD5;
	}
}