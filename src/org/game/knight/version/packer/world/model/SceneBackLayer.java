package org.game.knight.version.packer.world.model;


public class SceneBackLayer
{
	/**
	 * 名称
	 */
	public final String name;
	
	/**
	 * X偏移
	 */
	public final int x;
	
	/**
	 * Y偏移
	 */
	public final int y;
	
	/**
	 * 速度
	 */
	public final float speed;
	
	/**
	 * 图像
	 */
	public final AttireBitmap img;
	
	/**
	 * 构造函数
	 * @param name
	 * @param x
	 * @param y
	 * @param speed
	 * @param img
	 * @param atfGroup
	 */
	public SceneBackLayer(String name,int x,int y,float speed,AttireBitmap img)
	{
		this.name=name;
		this.x=x;
		this.y=y;
		this.speed=speed;
		this.img=img;
	}
}
