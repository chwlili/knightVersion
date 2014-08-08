package org.game.knight.version.packer.world.model;

import java.util.ArrayList;

public class SceneMonsterTimer
{
	private String name;
	private int x;
	private int y;
	private boolean loop;
	private ArrayList<SceneMonsterBatch> batchList=new ArrayList<SceneMonsterBatch>();
	
	public SceneMonsterTimer(String name,int x,int y,boolean loop)
	{
		this.name=name;
		this.x=x;
		this.y=y;
		this.loop=loop;
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
	
	public boolean isLoop()
	{
		return loop;
	}
	
	public void addSceneMonsterBatch(SceneMonsterBatch batch)
	{
		batchList.add(batch);
	}
	
	public ArrayList<SceneMonsterBatch> getBatchList()
	{
		return batchList;
	}
}
