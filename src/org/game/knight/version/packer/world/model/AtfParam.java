package org.game.knight.version.packer.world.model;

import java.io.File;

public class AtfParam
{
	/**
	 * 所属文件
	 */
	public final File file;
	/**
	 * ID
	 */
	public final String id;
	/**
	 * 宽度参数
	 */
	public final int width;
	/**
	 * 高度参数
	 */
	public final int height;
	/**
	 * 其它参数
	 */
	public final String other;

	/**
	 * 构造函数
	 * @param file
	 * @param id
	 * @param w
	 * @param h
	 * @param param
	 */
	public AtfParam(File file,String id,int width,int height,String other)
	{
		this.file=file;
		this.id=id;
		this.width=width;
		this.height=height;
		this.other=other;
	}
}