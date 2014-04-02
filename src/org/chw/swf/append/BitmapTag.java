package org.chw.swf.append;

public class BitmapTag
{
	private int width;
	private int height;
	private String packName;
	private String className;
	
	public BitmapTag(int width,int height,String packName,String className)
	{
		this.width=width;
		this.height=height;
		this.packName=packName;
		this.className=className;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public String getPackName()
	{
		return packName;
	}
	
	public String getClassName()
	{
		return className;
	}
}
