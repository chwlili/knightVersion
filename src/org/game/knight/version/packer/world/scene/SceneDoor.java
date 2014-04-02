package org.game.knight.version.packer.world.scene;

import org.game.knight.version.packer.world.attire.Attire;

public class SceneDoor
{
	private int id;
	private int x;
	private int y;
	private int direction;
	private Attire attire;
	
	private SceneHot hot;
	
	public SceneDoor(int id,int x,int y,int direction,Attire attire,SceneHot hot)
	{
		this.id=id;
		this.x=x;
		this.y=y;
		this.direction=direction;
		this.attire=attire;
		this.hot=hot;
	}
	
	public int getID()
	{
		return id;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getDirection()
	{
		return direction;
	}
	
	public Attire getAttire()
	{
		return attire;
	}
	
	public SceneHot getHot()
	{
		return hot;
	}
	public void setHot(SceneHot hot)
	{
		this.hot=hot;
	}
}
