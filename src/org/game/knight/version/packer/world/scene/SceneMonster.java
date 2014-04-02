package org.game.knight.version.packer.world.scene;

import org.game.knight.version.packer.world.attire.Attire;

public class SceneMonster
{
	private int monsterID;
	private Attire attire;
	private int x;
	private int y;
	private int dir;
	
	public SceneMonster(int monsterID,Attire attire,int monsterX,int monsterY,int monsterDir)
	{
		this.monsterID=monsterID;
		this.attire=attire;
		this.x=monsterX;
		this.y=monsterY;
		this.dir=monsterDir;
	}
	
	public int getMonsterID()
	{
		return monsterID;
	}
	
	public Attire getAttire()
	{
		return attire;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getDir()
	{
		return dir;
	}
}
