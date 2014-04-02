package org.game.knight.version.packer.view;


public class ViewFileOther
{
	private String key;
	private ViewFile file;
	
	public ViewFileOther(String key,ViewFile file)
	{
		this.key=key;
		this.file=file;
	}
	
	public String getKey()
	{
		return key;
	}
	
	public ViewFile getFile()
	{
		return file;
	}

}