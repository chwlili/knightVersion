package org.game.knight.version.packer.world.model;

import java.io.File;

public class ProjectFile extends File
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1221136184702972602L;

	/**
	 * URL
	 */
	public final String url;
	
	/**
	 * MD5
	 */
	public final String md5;
	
	/**
	 * 全局ID
	 */
	public final String gid;
	
	/**
	 * 构造函数
	 * @param url
	 * @param file
	 */
	public ProjectFile(File file,String url,String md5,String gid)
	{
		super(file.getPath());
		
		this.url=url;
		this.md5=md5;
		this.gid=gid;
	}
	
	/**
	 * 构造函数
	 * @param file
	 */
	public ProjectFile(ProjectFile file)
	{
		this(file,file.url,file.md5,file.gid);
	}
}
