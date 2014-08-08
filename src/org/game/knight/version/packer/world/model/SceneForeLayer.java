package org.game.knight.version.packer.world.model;


public class SceneForeLayer
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
	 * 宽度
	 */
	public final int w;
	
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
	 * @param width
	 * @param speed
	 * @param img
	 * @param atfGroup
	 */
	public SceneForeLayer(String name, int x, int y, int width, float speed, AttireBitmap img)
	{
		this.name = name;
		this.x = x;
		this.y = y;
		this.w = width;
		this.speed = speed;
		this.img = img;
	}
}
