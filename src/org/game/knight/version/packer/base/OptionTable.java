package org.game.knight.version.packer.base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Hashtable;

import org.chw.util.FileUtil;


public class OptionTable
{
	private static String Revision="revision";
	private static String Last_File_ID="lastFileID";
	private static String Last_Checksum_ID="lastChecksumID";
	private static String Last_Class_ID="lastClassID";
	
	private File file;
	private Hashtable<String, String> optionTable;
	
	
	/**
	 * 获取版本号
	 * @return
	 */
	public String getRevision()
	{
		if(optionTable.containsKey(Revision))
		{
			return optionTable.get(Revision);
		}
		return "";
	}
	
	/**
	 * 设置版本号
	 * @param val
	 */
	public void setRevision(String val)
	{
		optionTable.put(Revision, val);
	}
	
	
	/**
	 * 获取下一个文件的ID
	 * @return
	 */
	public long getNextFileID()
	{
		long lastID=0;
		if(optionTable.containsKey(Last_File_ID))
		{
			lastID=Long.parseLong(optionTable.get(Last_File_ID));
		}
		
		lastID++;
		optionTable.put(Last_File_ID, lastID+"");
		
		return lastID;
	}
	
	/**
	 * 获取下一个校验码的ID
	 * @return
	 */
	public long getNextChecksumID()
	{
		long lastID=0;
		if(optionTable.containsKey(Last_Checksum_ID))
		{
			lastID=Long.parseLong(optionTable.get(Last_Checksum_ID));
		}
		
		lastID++;
		optionTable.put(Last_Checksum_ID, lastID+"");
		
		return lastID;
	}
	
	/**
	 * 获取下一个类型的ID
	 * @return
	 */
	public long getNextClassID()
	{
		long lastID=0;
		if(optionTable.containsKey(Last_Class_ID))
		{
			lastID=Long.parseLong(optionTable.get(Last_Class_ID));
		}
		
		lastID++;
		optionTable.put(Last_Class_ID, lastID+"");
		
		return lastID;
	}
	
	
	
	/**
	 * 打开校验码配置
	 * @param file
	 */
	public void open(File file)
	{
		this.file=file;
		
		this.optionTable=new Hashtable<String, String>();
		
		if (file == null || !file.exists() || !file.isFile())
		{
			return;
		}
		
		BufferedReader input = null;
		try
		{
			input = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			while (true)
			{
				String line = input.readLine();
				if (line == null)
				{
					break;
				}
				
				line.trim();
				
				String[] parts=line.split("=");
				String key=parts[0].trim();
				String val=parts[1].trim();
				
				optionTable.put(key, val);
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (input != null)
			{
				try
				{
					input.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 保存校验码配置
	 */
	public void save()
	{
		saveAs(file);
	}

	/**
	 * 另存校验码配置
	 * @param file
	 */
	public void saveAs(File file)
	{
		if(file==null || optionTable==null)
		{
			return ;
		}
		
		StringBuilder sb=new StringBuilder();

		String[] keys=new String[optionTable.size()];
		keys=optionTable.keySet().toArray(keys);
		Arrays.sort(keys);
		
		for(int i=0;i<keys.length;i++)
		{
			String key=keys[i];
			String val=optionTable.get(key);
			
			sb.append(key+" = "+val+"\n");
		}
		
		try
		{
			byte[] bytes=sb.toString().getBytes("UTF-8");
			FileUtil.writeFile(file, bytes);
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}
}
