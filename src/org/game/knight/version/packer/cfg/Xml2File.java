package org.game.knight.version.packer.cfg;

import java.io.File;

import org.chw.util.FileUtil;
import org.chw.util.MD5Util;
import org.xml2as.builder.ClassTable;

public class Xml2File
{
	private File file;
	private String fileURL;
	private String fileMD5;

	private ClassTable classTable;

	public Xml2File(File file,String fileURL)
	{
		this.file = file;
		this.fileURL=fileURL;
	}

	public void buildClassTable() throws Exception
	{
		classTable = new ClassTable(file);
	}

	public byte[] getFileBytes()
	{
		return FileUtil.getFileBytes(file);
	}

	public String getFileName()
	{
		return file.getName();
	}
	
	public String getFileURL()
	{
		return fileURL;
	}

	public String getFileMD5() throws Exception
	{
		if (fileMD5 == null)
		{
			fileMD5 = MD5Util.md5(file);
		}
		return fileMD5;
	}

	public String getInputURL()
	{
		String inputURL = classTable.getInputFile();
		if (inputURL != null && classTable.getMainClass() != null)
		{
			return inputURL;
		}
		return null;
	}

	public ClassTable getClassTable()
	{
		return classTable;
	}
}
