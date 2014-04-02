package org.game.knight.version.packer.view;


public class ViewFileSwf
{
	private String key;
	private ViewFile file;
	
	public ViewFileSwf(String key,ViewFile file)
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