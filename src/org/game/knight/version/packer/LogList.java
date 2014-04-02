package org.game.knight.version.packer;

import java.util.ArrayList;

public class LogList extends Log
{
	private ArrayList<Log> children;
	
	private Log[] childrenArray;
	private boolean childrenArrayInvalidate;
	
	/**
	 * ���캯��
	 * @param parent
	 */
	public LogList(LogList parent,int type,String text,String path)
	{
		super(parent,type,text,path);
		
		children=new ArrayList<Log>();
	}
	
	/**
	 * ��ȡ�Ӽ�
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
	 * ����Ӽ�
	 * @return
	 */
	public Log addChild(Log log)
	{
		children.add(log);
		childrenArrayInvalidate=true;
		return log;
	}
	
	/**
	 * ɾ���Ӽ�
	 * @param log
	 */
	public void removeChild(Log log)
	{
		children.remove(log);
		childrenArrayInvalidate=true;
	}
}
