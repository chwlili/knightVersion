package org.game.knight.version.packer.world.model;


public class AttireBitmap
{
	/**
	 * ID
	 */
	public final String id;
	
	/**
	 * ͼ���ļ�
	 */
	public final ProjectImgFile imgFile;
	
	/**
	 * ATF����
	 */
	public final AtfParam atfParam;
	
	/**
	 * ���캯��
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
