package org.game.knight.version.packer.world.scene;

import java.util.ArrayList;

public class ScenePart
{
	private int left;
	private int right;
	private ArrayList<SceneMonsterTimer> timers=new ArrayList<SceneMonsterTimer>(); 
	
	public ScenePart(int left,int right)
	{
		this.left=left;
		this.right=right;
	}
	
	public int getLeft()
	{
		return left;
	}
	
	public int getRight()
	{
		return right;
	}
	
	public void addTimer(SceneMonsterTimer timer)
	{
		timers.add(timer);
	}

	public ArrayList<SceneMonsterTimer> getTimers()
	{
		return timers;
	}
}
