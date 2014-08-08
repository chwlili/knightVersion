package org.game.knight.version.packer.world.model;


public class SceneNpc
{
	/**
	 * ����
	 */
	public final String name;
	
	/**
	 * ID
	 */
	public final int id;
	
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
	 * ���캯��
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
