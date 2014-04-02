package org.chw.util;

import java.io.StringWriter;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class XmlUtil 
{
	/**
	 * ��ʽ��DOM
	 * @param xml
	 * @return
	 */
	static public String formatXML(String xml)
	{
	    try 
	    {
	    	Document dom=DocumentHelper.parseText(xml);
	    	
	    	StringWriter write = new StringWriter();

	    	OutputFormat localOutputFormat = OutputFormat.createPrettyPrint();
			localOutputFormat.setEncoding("UTF-8");
			
		    XMLWriter localXMLWriter = new XMLWriter(write,localOutputFormat);
			localXMLWriter.write(dom);
			localXMLWriter.close();
			
			return write.toString();
	    } 
	    catch (Exception error) 
	    { 
	    	error.printStackTrace(); 
	    } 
	    finally 
	    { 
	    }
	    
	    return xml; 
	}

	/**
	 * ����Float
	 * @param text
	 * @param defaultValue
	 * @return
	 */
	static public float parseFloat(String text,float defaultValue)
	{
		if(text!=null && !text.isEmpty())
		{
			try
			{
				return Float.parseFloat(text);
			}
			catch(Throwable err)
			{
			}
		}
		
		return defaultValue;
	}
	
	/**
	 * ����Int
	 * @param text
	 * @param defaultValue
	 * @return
	 */
	static public int parseInt(String text,int defaultValue)
	{
		if(text!=null && !text.isEmpty())
		{
			try
			{
				return Integer.parseInt(text);
			}
			catch(Throwable err)
			{
			}
		}
		
		return defaultValue;
	}
	
	/**
	 * ����int����
	 * @param text
	 * @param defaultValue
	 * @return
	 */
	static public int[] parseInts(String text,int defaultValue)
	{
		if(text!=null && !text.isEmpty())
		{
			String[] parts=text.split(",");
			
			int[] ints=new int[parts.length];
			for(int i=0;i<parts.length;i++)
			{
				ints[i]=parseInt(parts[i],defaultValue);
			}
			return ints;
		}
		
		return new int[]{};
	}
	
	/**
	 * ����Boolean
	 * @param text
	 * @param defaultValue
	 * @return
	 */
	static public boolean parseBoolean(String text,boolean defaultValue)
	{
		if(text!=null && !text.isEmpty())
		{
			return "true".equals(text);
		}
		return defaultValue;
	}
	
	/**
	 * ����String
	 * @param text
	 * @param defaultValue
	 * @return
	 */
	static public String parseString(String text,String defaultValue)
	{
		if(text!=null && !text.isEmpty())
		{
			return text;
		}
		return defaultValue;
	}
}
