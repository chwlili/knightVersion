package org.game.knight.version.packer.world.scene;

public class SceneTrap
{
	private int type;
	private int x;
	private int y;
	private int width;
	private int height;
	private int quest;
	private String content;
	
	public SceneTrap(int type,int x,int y,int width,int height,int quest,String content)
	{
		this.type=type;
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		this.quest=quest;
		this.content=content;
	}
	
	public int getType()
	{
		return type;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public int getQuest()
	{
		return quest;
	}
	
	public String getContent()
	{
		return content;
	}
}
