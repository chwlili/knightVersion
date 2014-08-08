package org.game.knight.version.packer.world.model;

public class AttireHitRect
{
	/**
	 * X����
	 */
	public final int x;
	
	/**
	 * Y����
	 */
	public final int y;
	
	/**
	 * ���
	 */
	public final int width;
	
	/**
	 * �߶�
	 */
	public final int height;
	
	/**
	 * ����X
	 */
	public final int nameX;
	
	/**
	 * ����Y
	 */
	public final int nameY;
	
	/**
	 * ���캯��
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
