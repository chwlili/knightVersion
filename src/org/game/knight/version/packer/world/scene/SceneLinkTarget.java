package org.game.knight.version.packer.world.scene;

import java.util.ArrayList;

public class SceneLinkTarget
{
	private Scene scene;
	private String key;
	private int x;
	private int y;
	private int w;
	private int h;
	private ArrayList<SceneLink> links=new ArrayList<SceneLink>();
	
	public SceneLinkTarget(String key,Scene scene,int x,int y,int w,int h)
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
	
	public void addLink(SceneLink link)
	{
		links.add(link);
	}
}
