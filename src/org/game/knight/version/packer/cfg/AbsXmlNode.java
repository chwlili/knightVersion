package org.game.knight.version.packer.cfg;

import java.util.List;

public abstract class AbsXmlNode extends Object
{
	private String txt;
	
	/**
	 * 构建成字符串
	 * @return
	 */
	protected abstract String buildString();
	
	/**
	 * 格式化List
	 * @param list
	 * @return
	 */
	public static String formatList(List<Integer> list)
	{
		StringBuilder sb=new StringBuilder();
		for(Integer item:list)
		{
			if(sb.length()>0)
			{
				sb.append(",");
			}
			sb.append(item);
		}
		
		return sb.toString();
	}
	
	private String getString()
	{
		if(txt==null)
		{
			txt = buildString();
		}
		return txt;
	}
	
	@Override
	public int hashCode()
	{
		return getString().hashCode(); 
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(super.equals(obj))
		{
			return true;
		}
		else
		{
			return obj.toString().equals(toString());
		}
	}
	
	@Override
	public String toString()
	{
		return getString();
	}
}
