package org.game.knight.version.packer.world.model;


public class SceneAnim
{
	/**
	 * 名称
	 */
	public final String name;
	
	/**
	 * X坐标
	 */
	public final int x;
	
	/**
	 * Y坐标
	 */
	public final int y;
	
	/**
	 * X偏移
	 */
	public final int offsetX;
	
	/**
	 * Y偏移
	 */
	public final int offsetY;
	
	/**
	 * 朝向
	 */
	public final int direction;
	
	/**
	 * 装扮
	 */
	public final Attire attire;
	
	/**
	 * 构造函数
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
