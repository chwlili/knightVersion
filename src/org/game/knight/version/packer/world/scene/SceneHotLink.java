package org.game.knight.version.packer.world.scene;

public class SceneHotLink
{
	private int toID;
	private String toName;
	private int toX;
	private int toY;
	
	public SceneHotLink(int toID,String toName,int toX,int toY)
	{
		this.toID=toID;
		this.toName=toName;
		this.toX=toX;
		this.toY=toY;
	}
	
	public int getToID()
	{
		return toID;
	}
	
	public String getToName()
	{
		return toName;
	}
	
	public int getToX()
	{
		return toX;
	}
	
	public int getToY()
	{
		return toY;
	}
}
