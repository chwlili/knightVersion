package org.game.knight.version.packer.world;

import java.io.File;

public class ExportParam
{
	private File file;
	private String id;
	private int w;
	private int h;
	private String param;
	
	public ExportParam(File file,String id,int w,int h,String param)
	{
		this.file=file;
		this.id=id;
		this.w=w;
		this.h=h;
		this.param=param;
	}
	
	public File getFile()
	{
		return file;
	}
	
	public String getID()
	{
		return id;
	}
	
	public int getWidth()
	{
		return w;
	}
	
	public int getHeight()
	{
		return h;
	}
	
	public String getParam()
	{
		return param;
	}
}
