package org.chw.svn;

public class SvnFileDelta
{
	public String[] addedFiles;
	public String[] modefiedFiles;
	public String[] deletedFiles;
	
	@Override
	public String toString()
	{
		StringBuilder sb=new StringBuilder();
		
		if(addedFiles!=null)
		{
			for(int i=0;i<addedFiles.length;i++)
			{
				sb.append("A "+addedFiles[i]+"\n");
			}
		}
		
		if(modefiedFiles!=null)
		{
			for(int i=0;i<modefiedFiles.length;i++)
			{
				sb.append("M "+modefiedFiles[i]+"\n");
			}
		}
		
		if(deletedFiles!=null)
		{
			for(int i=0;i<deletedFiles.length;i++)
			{
				sb.append("D "+deletedFiles[i]+"\n");
			}
		}
		
		String txt=sb.toString();
		if(txt.length()>0)
		{
			return txt.substring(0,txt.length()-1);
		}
		return txt;
	}
}
