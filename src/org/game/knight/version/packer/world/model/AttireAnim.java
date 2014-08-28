package org.game.knight.version.packer.world.model;


public class AttireAnim
{
	/**
	 * ID
	 */
	public final int id;
	
	/**
	 * 组ID
	 */
	public final int groupID;
	
	/**
	 * 层ID
	 */
	public final int layerID;
	
	/**
	 * X坐标
	 */
	public final int x;
	
	/**
	 * Y坐标
	 */
	public final int y;
	
	/**
	 * X缩放
	 */
	public final float scaleX;
	
	/**
	 * Y缩放
	 */
	public final float scaleY;
	
	/**
	 * 翻转
	 */
	public final boolean flip;
	
	/**
	 * 图像文件
	 */
	public final ProjectImgFile img;
	
	/**
	 * 行数
	 */
	public final int row;
	
	/**
	 * 列数
	 */
	public final int col;
	
	/**
	 * 帧延时
	 */
	public final String delays;
	
	/**
	 * 帧延时
	 */
	public final int[] times;
	
	/**
	 * ATF参数
	 */
	public final AtfParam param;
	
	/**
	 * 构造函数
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
