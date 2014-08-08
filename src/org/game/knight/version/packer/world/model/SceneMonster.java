package org.game.knight.version.packer.world.model;


public class SceneMonster
{
	/**
	 * ����ID
	 */
	public final int monsterID;
	
	/**
	 * װ��
	 */
	public final Attire attire;
	
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
	public final int dir;
	
	/**
	 * ���캯��
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
