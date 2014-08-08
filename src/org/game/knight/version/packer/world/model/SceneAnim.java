package org.game.knight.version.packer.world.model;


public class SceneAnim
{
	/**
	 * ����
	 */
	public final String name;
	
	/**
	 * X����
	 */
	public final int x;
	
	/**
	 * Y����
	 */
	public final int y;
	
	/**
	 * Xƫ��
	 */
	public final int offsetX;
	
	/**
	 * Yƫ��
	 */
	public final int offsetY;
	
	/**
	 * ����
	 */
	public final int direction;
	
	/**
	 * װ��
	 */
	public final Attire attire;
	
	/**
	 * ���캯��
	 * @param name
	 * @param x
	 * @param y
	 * @param offsetX
	 * @param offsetY
	 * @param direction
	 * @param attire
	 */
	public SceneAnim(String name,int x,int y,int offsetX,int offsetY,int direction,Attire attire)
	{
		this.name=name;
		this.x=x;
		this.y=y;
		this.offsetX=offsetX;
		this.offsetY=offsetY;
		this.direction=direction;
		this.attire=attire;
	}
}
