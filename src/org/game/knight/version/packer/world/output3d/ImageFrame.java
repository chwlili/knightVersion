package org.game.knight.version.packer.world.output3d;

import org.game.knight.version.packer.world.model.ProjectImgFile;


public class ImageFrame
{
	/**
	 * 图像文件
	 */
	public final ProjectImgFile file;
	
	/**
	 * 切割行数
	 */
	public final int row;
	
	/**
	 * 切割列数
	 */
	public final int col;
	
	/**
	 * 帧索引
	 */
	public final int index;
	
	/**
	 * 帧X坐标
	 */
	public final int frameX;
	
	/**
	 * 帧Y坐标
	 */
	public final int frameY;
	
	/**
	 * 帧宽度
	 */
	public final int frameW;
	
	/**
	 * 帧高度
	 */
	public final int frameH;
	
	/**
	 * 裁切X坐标
	 */
	public final int clipX;
	
	/**
	 * 裁切Y坐标
	 */
	public final int clipY;
	
	/**
	 * 裁切宽度
	 */
	public final int clipW;
	
	/**
	 * 裁切高度
	 */
	public final int clipH;
	
	/**
	 * 构造函数
	 * @param file
	 * @param row
	 * @param col
	 * @param index
	 * @param frameX
	 * @param frameY
	 * @param frameW
	 * @param frameH
	 * @param clipX
	 * @param clipY
	 * @param clipW
	 * @param clipH
	 */
	public ImageFrame(ProjectImgFile file,int row,int col,int index,int frameX,int frameY,int frameW,int frameH,int clipX,int clipY,int clipW,int clipH)
	{
		this.file=file;
		this.row=row;
		this.col=col;
		this.index=index;
		this.frameX=frameX;
		this.frameY=frameY;
		this.frameW=frameW;
		this.frameH=frameH;
		this.clipX=clipX;
		this.clipY=clipY;
		this.clipW=clipW;
		this.clipH=clipH;
	}
}
