package org.chw.util;

public class TextUtil
{
	public static String formatIntArray(int[] list)
	{
		if(list==null)
		{
			return "";
		}
		
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<list.length;i++)
		{
			sb.append(list[i]);
			if(i<list.length-1)
			{
				sb.append(",");
			}
		}
		
		return sb.toString();
	}
}
