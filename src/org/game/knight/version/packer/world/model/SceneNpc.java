package org.game.knight.version.packer.world.model;


public class SceneNpc
{
	/**
	 * 名称
	 */
	public final String name;
	
	/**
	 * ID
	 */
	public final int id;
	
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
	 * 构造函数
	 * @param name
	 * @param id
	 * @param x
	 * @param y
	 * @param direction
	 * @param attire
	 */
	public SceneNpc(String name,int id,int x,int y,int direction,Attire attire)
	{
		this.name=name;
		this.id=id;
		this.x=x;
		this.y=y;
		this.direction=direction;
		this.attire=attire;
	}
}
