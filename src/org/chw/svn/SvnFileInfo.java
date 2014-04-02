package org.chw.svn;

public class SvnFileInfo
{
	public String path;
	public String workingCopyRootPath;
	public String url;
	public String repositoryRoot;
	public String repositoryUUID;
	public String revision;
	public String nodeKind;
	public String schedule;
	public String lastChangedAuthor;
	public String lastChangedRevision;
	public String lastChangeDate;
	public String checksum;
	
	@Override
	public String toString()
	{
		return "Path: "+path+"\n"+
		"Working Copy Root Path: "+workingCopyRootPath+"\n"+
		"URL: "+url+"\n"+
		"Repository Root: "+repositoryRoot+"\n"+
		"Repository UUID: "+repositoryUUID+"\n"+
		"Revision: "+revision+"\n"+
		"Node Kind: "+nodeKind+"\n"+
		"Schedule: "+schedule+"\n"+
		"Last Changed Author: "+lastChangedAuthor+"\n"+
		"Last Changed Rev: "+lastChangedRevision+"\n"+
		"Last Changed Date: "+lastChangeDate+"\n"+
		"Checksum: "+checksum;
	}
}
