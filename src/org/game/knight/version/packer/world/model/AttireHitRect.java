package org.game.knight.version.packer.world.model;

public class AttireHitRect
{
	/**
	 * X坐标
	 */
	public final int x;
	
	/**
	 * Y坐标
	 */
	public final int y;
	
	/**
	 * 宽度
	 */
	public final int width;
	
	/**
	 * 高度
	 */
	public final int height;
	
	/**
	 * 名称X
	 */
	public final int nameX;
	
	/**
	 * 名称Y
	 */
	public final int nameY;
	
	/**
	 * 构造函数
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public AttireHitRect(int x,int y,int width,int height,int nameX,int nameY)
	{
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		this.nameX=nameX;
		this.nameY=nameY;
	}
}
