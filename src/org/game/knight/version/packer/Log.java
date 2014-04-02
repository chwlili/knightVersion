package org.game.knight.version.packer;

public class Log
{
	private LogList parent;
	private int type;
	private String text;
	private String path;
	
	/**
	 * ���캯��
	 * @param parent
	 */
	public Log(LogList parent,int type,String text)
	{
		this.parent=parent;
		this.type=type;
		this.text=text;
		this.path="";
	}
	
	public Log(LogList parent,int type,String text,String path)
	{
		this(parent,type,text);
		this.path=path;
	}
	
	/**
	 * ��ȡ����
	 * @return
	 */
	public LogList getParent()
	{
		return parent;
	}
	public void setParent(LogList value)
	{
		parent=value;
	}
	
	/**
	 * ��ȡ����
	 * @return
	 */
	public int getType()
	{
		return type;
	}
	
	/**
	 * ��ȡ����
	 * @return
	 */
	public String getText()
	{
		return text;
	}
	public void setText(String value)
	{
		text=value;
	}
	
	public String getPath()
	{
		return path;
	}
	public void setPath(String value)
	{
		path=value;
	}
}
