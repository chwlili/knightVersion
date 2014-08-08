package org.game.knight.version.packer.world.model;


public class SceneLayer
{
	/**
	 * 名称
	 */
	public final String name;
	
	/**
	 * X偏移
	 */
	public final int offsetX;
	
	/**
	 * Y偏移
	 */
	public final int offsetY;
	
	/**
	 * 速度
	 */
	public final float speed;
	
	/**
	 * 图像
	 */
	public final ProjectImgFile image;
	
	/**
	 * 构造函数
	 * @param name
	 * @param x
	 * @param y
	 * @param speed
	 * @param img
	 */
	public SceneLayer(String name,int offsetX,int offsetY,float speed,ProjectImgFile image)
	{
		this.name=name;
		this.offsetX=offsetX;
		this.offsetY=offsetY;
		this.speed=speed;
		this.image=image;
	}
}
