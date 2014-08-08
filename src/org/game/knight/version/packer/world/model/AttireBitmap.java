package org.game.knight.version.packer.world.model;


public class AttireBitmap
{
	/**
	 * ID
	 */
	public final String id;
	
	/**
	 * 图像文件
	 */
	public final ProjectImgFile imgFile;
	
	/**
	 * ATF参数
	 */
	public final AtfParam atfParam;
	
	/**
	 * 构造函数
	 * @param imgFile
	 * @param atfParam
	 */
	public AttireBitmap(String id,ProjectImgFile imgFile,AtfParam atfParam)
	{
		this.id=id;
		this.imgFile=imgFile;
		this.atfParam=atfParam;
	}
}
