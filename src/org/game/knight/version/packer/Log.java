package org.game.knight.version.packer;

public class Log
{
	private LogList parent;
	private int type;
	private String text;
	private String path;
	
	/**
	 * 构造函数
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
	 * 获取父级
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
	 * 获取类型
	 * @return
	 */
	public int getType()
	{
		return type;
	}
	
	/**
	 * 获取内容
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
