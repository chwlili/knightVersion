package org.chw.swf.writer;


public class SwfMp3
{
	private byte[] bytes;
	private String packName;
	private String className;

	/**
	 * ���캯��
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
	 * ��ȡ�ֽ�
	 * 
	 * @return
	 */
	public byte[] getBytes()
	{
		return bytes;
	}
	
	/**
	 * ��ȡ����
	 * @return
	 */
	public String getPackName()
	{
		return packName;
	}
	
	/**
	 * ��ȡ����
	 * @return
	 */
	public String getClassName()
	{
		return className;
	}
}
