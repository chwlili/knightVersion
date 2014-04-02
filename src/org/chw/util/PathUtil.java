package org.chw.util;

public class PathUtil
{
	
	/**
	 * 获取文件名称
	 * @param path
	 * @return
	 */
	public static String getFileName(String path)
	{
		if(path.endsWith("/"))
		{
			path=path.substring(0,path.length()-1);
		}
		
		int index=path.lastIndexOf("/");
		
		if(index!=-1)
		{
			String fileName=path.substring(index+1);
			
			index=fileName.lastIndexOf(".");
			if(index!=-1)
			{
				return fileName.substring(0,index);
			}
			else
			{
				return fileName;
			}
		}
		return "";
	}
	
	/**
	 * 获取文件扩展名
	 * @param path
	 * @return
	 */
	public static String getFileExt(String path)
	{
		if(path!=null && path.isEmpty()==false)
		{
			int index=path.lastIndexOf(".");
			if(index!=-1)
			{
				return path.substring(index+1);
			}
		}
		
		return "";
		/*
		if(path.endsWith("/"))
		{
			return "";
		}
		
		int index=path.lastIndexOf("/");
		
		if(index!=-1)
		{
			String fileName=path.substring(index+1);
			
			index=fileName.lastIndexOf(".");
			if(index!=-1)
			{
				return fileName.substring(index+1);
			}
		}
		return "";
		*/
	}

	/**
	 * 重建路径
	 * @param path
	 * @return
	 */
	public static String rebuildPath(String path)
	{
		if(path!=null)
		{
			return path.replaceAll("\\\\", "/");
		}
		return "";
	}
	
	/**
	 * 获取绝对路径
	 * @param currPath
	 * @param relativePath
	 * @return
	 */
	public static String getAbsPath(String currPath,String relativePath)
	{
		if(relativePath.startsWith("/"))
		{
			return relativePath;
		}
		else
		{
			if(currPath.endsWith("/"))
			{
				currPath=currPath.substring(0, currPath.length()-1);
			}
			if(relativePath.endsWith("/"))
			{
				relativePath=relativePath.substring(0, relativePath.length()-1);
			}
			
			String[] parts=relativePath.split("/");
			for(int i=0;i<parts.length;i++)
			{
				String part=parts[i];
				if(part.equals(".."))
				{
					String parentPath=getParentPath(currPath);
					if(parentPath!=null)
					{
						currPath=parentPath;
					}
					else
					{
						return null;
					}
				}
				else if(part.equals("."))
				{
					//currPath=currPath;
				}
				else if(!part.isEmpty())
				{
					currPath=currPath+"/"+part;
				}
			}
			return currPath;
		}
	}
	
	/**
	 * 获取父路径
	 * @param absPath
	 * @return
	 */
	public static String getParentPath(String absPath)
	{
		if(absPath.endsWith("/"))
		{
			absPath=absPath.substring(0, absPath.length()-1);
		}
		
		if(absPath.length()>0)
		{
			int index=absPath.lastIndexOf("/");
			if(index!=-1)
			{
				return absPath.substring(0,index);
			}
		}
		return null;
	}
}
