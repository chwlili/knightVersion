package org.game.knight.version.packer.world.model;


public class SceneMonster
{
	/**
	 * 怪物ID
	 */
	public final int monsterID;
	
	/**
	 * 装扮
	 */
	public final Attire attire;
	
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
	public final int dir;
	
	/**
	 * 构造函数
	 * @param monsterID
	 * @param attire
	 * @param monsterX
	 * @param monsterY
	 * @param monsterDir
	 */
	public SceneMonster(int monsterID,Attire attire,int monsterX,int monsterY,int monsterDir)
	{
		this.monsterID=monsterID;
		this.attire=attire;
		this.x=monsterX;
		this.y=monsterY;
		this.dir=monsterDir;
	}
}
