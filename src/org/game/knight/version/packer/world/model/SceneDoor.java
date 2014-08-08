package org.game.knight.version.packer.world.model;


public class SceneDoor
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
	 * 朝向
	 */
	public final int direction;
	
	/**
	 * 装扮
	 */
	public final Attire attire;
	
	/**
	 * 热区
	 */
	public final SceneHot hot;
	
	/**
	 * 构造函数
	 * @param id
	 * @param x
	 * @param y
	 * @param direction
	 * @param attire
	 * @param hot
	 */
	public SceneDoor(int x,int y,int direction,Attire attire,SceneHot hot)
	{
		this.x=x;
		this.y=y;
		this.direction=direction;
		this.attire=attire;
		this.hot=hot;
	}
}
