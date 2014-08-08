package org.game.knight.version.packer.world.output3d;

import org.game.knight.version.packer.world.model.ProjectImgFile;


public class ImageFrame
{
	/**
	 * ͼ���ļ�
	 */
	public final ProjectImgFile file;
	
	/**
	 * �и�����
	 */
	public final int row;
	
	/**
	 * �и�����
	 */
	public final int col;
	
	/**
	 * ֡����
	 */
	public final int index;
	
	/**
	 * ֡X����
	 */
	public final int frameX;
	
	/**
	 * ֡Y����
	 */
	public final int frameY;
	
	/**
	 * ֡���
	 */
	public final int frameW;
	
	/**
	 * ֡�߶�
	 */
	public final int frameH;
	
	/**
	 * ����X����
	 */
	public final int clipX;
	
	/**
	 * ����Y����
	 */
	public final int clipY;
	
	/**
	 * ���п��
	 */
	public final int clipW;
	
	/**
	 * ���и߶�
	 */
	public final int clipH;
	
	/**
	 * ���캯��
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
