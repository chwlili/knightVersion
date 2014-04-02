package org.chw.svn;

import org.chw.util.FileUtil;

public class SvnSync
{
	private SvnFile src;
	private SvnFile tmp;
	private SvnFile dst;
	
	public SvnSync(SvnFile source,SvnFile temp,SvnFile target)
	{
		src=source;
		tmp=temp;
		dst=target;
	}
	
	public SvnFile getSource()
	{
		return src;
	}
	
	public SvnFile getTemp()
	{
		return tmp;
	}
	
	public SvnFile getTarget()
	{
		return dst;
	}
	
	public void go()
	{
		int preVer=0;
		
		SvnFile file=new SvnFile(src.getPath()+"/ver.txt");
		if(file.exists())
		{
			byte[] bytes=FileUtil.getFileBytes(file.getNativeFile());
			if(bytes!=null)
			{
				String txt=new String(bytes);
				if(!txt.isEmpty())
				{
					try
					{
						preVer=Integer.parseInt(txt);
					}
					catch(NumberFormatException err)
					{
						
					}
				}
			}
		}
		
		src.svnUpdate();
		
		int newVer=Integer.parseInt(src.svnInfo().revision);
		
		if(newVer>preVer)
		{
			System.out.println(src.svnDiff(preVer, newVer));
		}
	}
}
