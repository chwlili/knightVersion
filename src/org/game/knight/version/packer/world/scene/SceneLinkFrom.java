package org.game.knight.version.packer.world.scene;

import java.util.ArrayList;

public class SceneLinkFrom
{
	private String key;
	private Scene scene;
	private int x;
	private int y;
	private int w;
	private int h;
	private ArrayList<SceneLink> links=new ArrayList<SceneLink>();
	private int doorIndex;
	
	public SceneLinkFrom(String key,Scene scene,int x,int y,int w,int h)
	{
		this.key=key;
		this.scene=scene;
		this.x=x;
		this.y=y;
		this.w=w;
		this.h=h;
	}
	
	public String getKey()
	{
		return key;
	}
	
	public Scene getScene()
	{
		return scene;
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
		return w;
	}
	
	public int getHeight()
	{
		return h;
	}
	
	public int getDoorIndex()
	{
		return doorIndex;
	}
	
	public void setDoorIndex(int index)
	{
		doorIndex=index;
	}
	
	public void addLink(SceneLink link)
	{
		links.add(link);
	}
	
	public ArrayList<SceneLink> getLinks()
	{
		return links;
	}
	
}
