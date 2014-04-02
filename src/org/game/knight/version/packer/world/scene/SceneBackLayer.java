package org.game.knight.version.packer.world.scene;

import org.game.knight.version.packer.world.ImgFile;

public class SceneBackLayer
{
	private String name;
	private int x;
	private int y;
	private float speed;
	private ImgFile img;
	private String atfGroup;
	
	public SceneBackLayer(String name,int x,int y,float speed,ImgFile img,String atfGroup)
	{
		this.name=name;
		this.x=x;
		this.y=y;
		this.speed=speed;
		this.img=img;
		this.atfGroup=atfGroup;
	}
	
	public String getName()
	{
		return name;
	}
	public void setName(String value)
	{
		name=value;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public float getSpeed()
	{
		return speed;
	}
	
	public ImgFile getImage()
	{
		return img;
	}
	
	public String getAtfGroup()
	{
		return atfGroup;
	}
}
