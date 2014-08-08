package org.game.knight.version.packer.world.model;


public class SceneDoor
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
	 * ����
	 */
	public final int direction;
	
	/**
	 * װ��
	 */
	public final Attire attire;
	
	/**
	 * ����
	 */
	public final SceneHot hot;
	
	/**
	 * ���캯��
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
