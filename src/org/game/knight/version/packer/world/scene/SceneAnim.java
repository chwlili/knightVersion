package org.game.knight.version.packer.world.scene;

import org.game.knight.version.packer.world.attire.Attire;

public class SceneAnim
{
	private String name;
	private int x;
	private int y;
	private int offsetX;
	private int offsetY;
	private int direction;
	private Attire attire;
	
	public SceneAnim(String name,int x,int y,int offsetX,int offsetY,int direction,Attire attire)
	{
		this.name=name;
		this.x=x;
		this.y=y;
		this.offsetX=offsetX;
		this.offsetY=offsetY;
		this.direction=direction;
		this.attire=attire;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getOffsetX()
	{
		return offsetX;
	}
	
	public int getOffsetY()
	{
		return offsetY;
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
