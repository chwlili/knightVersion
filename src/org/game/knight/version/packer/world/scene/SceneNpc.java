package org.game.knight.version.packer.world.scene;

import org.game.knight.version.packer.world.attire.Attire;

public class SceneNpc
{
	private String name;
	private int id;
	private int x;
	private int y;
	private int direction;
	private Attire attire;
	
	public SceneNpc(String name,int id,int x,int y,int direction,Attire attire)
	{
		this.name=name;
		this.id=id;
		this.x=x;
		this.y=y;
		this.direction=direction;
		this.attire=attire;
	}
	
	public String getName()
	{
		return name;
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
}
