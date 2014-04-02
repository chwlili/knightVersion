package org.chw.swf.writer;


public class SwfMp3
{
	private byte[] bytes;
	private String packName;
	private String className;

	/**
	 * 构造函数
	 * 
	 * @param file
	 * @param quality
	 */
	public SwfMp3(byte[] bytes,String packName,String className)
	{
		this.bytes=bytes;
		this.packName=packName;
		this.className=className;
	}

	/**
	 * 获取字节
	 * 
	 * @return
	 */
	public byte[] getBytes()
	{
		return bytes;
	}
	
	/**
	 * 获取包名
	 * @return
	 */
	public String getPackName()
	{
		return packName;
	}
	
	/**
	 * 获取类名
	 * @return
	 */
	public String getClassName()
	{
		return className;
	}
}
