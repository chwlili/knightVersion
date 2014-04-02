package org.chw.swf.writer;

public class SwfXML
{
	private byte[] bytes;
	private String packName;
	private String className;

	/**
	 * ¹¹Ôìº¯Êý
	 * 
	 * @param file
	 * @param quality
	 */
	public SwfXML(byte[] bytes,String packName,String className)
	{
		this.bytes=bytes;
		this.packName=packName;
		this.className=className;
	}
	
	public byte[] getBytes()
	{
		return bytes;
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
