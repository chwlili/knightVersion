package org.game.knight.version.packer.world.model;


public class AttireAnim
{
	/**
	 * ID
	 */
	public final int id;
	
	/**
	 * ��ID
	 */
	public final int groupID;
	
	/**
	 * ��ID
	 */
	public final int layerID;
	
	/**
	 * X����
	 */
	public final int x;
	
	/**
	 * Y����
	 */
	public final int y;
	
	/**
	 * X����
	 */
	public final float scaleX;
	
	/**
	 * Y����
	 */
	public final float scaleY;
	
	/**
	 * ��ת
	 */
	public final boolean flip;
	
	/**
	 * ͼ���ļ�
	 */
	public final ProjectImgFile img;
	
	/**
	 * ����
	 */
	public final int row;
	
	/**
	 * ����
	 */
	public final int col;
	
	/**
	 * ֡��ʱ
	 */
	public final String delays;
	
	/**
	 * ֡��ʱ
	 */
	public final int[] times;
	
	/**
	 * ATF����
	 */
	public final AtfParam param;
	
	/**
	 * ���캯��
	 * @param id
	 * @param gID
	 * @param lID
	 * @param x
	 * @param y
	 * @param scaleX
	 * @param scaleY
	 * @param flip
	 * @param img
	 * @param row
	 * @param col
	 * @param delays
	 * @param bagID
	 */
	public AttireAnim(int id,int gID,int lID,int x,int y,float scaleX,float scaleY,boolean flip,ProjectImgFile img,int row,int col,String delays,AtfParam param)
	{
		this.id=id;
		this.groupID=gID;
		this.layerID=lID;
		this.x=x;
		this.y=y;
		this.scaleX=scaleX;
		this.scaleY=scaleY;
		this.flip=flip;
		this.img=img;
		this.row=row;
		this.col=col;
		this.delays=delays;
		this.param=param;
		
		String[] parts=delays.split(",");
		times=new int[parts.length];
		for(int i=0;i<parts.length;i++)
		{
			if(parts[i]==null)
			{
				times[i]=0;
			}
			else
			{
				times[i]=Integer.parseInt(parts[i]);
			}
		}
	}
}
