package org.game.knight.version.packer;

import java.util.ArrayList;

public class LogList extends Log
{
	private ArrayList<Log> children;
	
	private Log[] childrenArray;
	private boolean childrenArrayInvalidate;
	
	/**
	 * 构造函数
	 * @param parent
	 */
	public LogList(LogList parent,int type,String text,String path)
	{
		super(parent,type,text,path);
		
		children=new ArrayList<Log>();
	}
	
	/**
	 * 获取子级
	 * @return
	 */
	public Log[] getChildren()
	{
		if(childrenArray==null || childrenArrayInvalidate)
		{
			childrenArray=new Log[children.size()];
			childrenArray=children.toArray(childrenArray);
			
			childrenArrayInvalidate=false;
		}
		
		return childrenArray;
	}
	
	/**
	 * 添加子级
	 * @return
	 */
	public Log addChild(Log log)
	{
		children.add(log);
		childrenArrayInvalidate=true;
		return log;
	}
	
	/**
	 * 删除子级
	 * @param log
	 */
	public void removeChild(Log log)
	{
		children.remove(log);
		childrenArrayInvalidate=true;
	}
}
