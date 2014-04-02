package org.game.knight.version.packer.world.scene;

import java.util.ArrayList;

public class SceneMonsterBatch
{
	private int delay;
	private boolean obstruct;
	private int loopCount;
	private boolean ignoreFirstDelay;
	private ArrayList<SceneMonster> monsters=new ArrayList<SceneMonster>();
	
	public SceneMonsterBatch(int delay,boolean obstruct,int loopCount,boolean ignoreFirstDelay)
	{
		this.delay=delay;
		this.obstruct=obstruct;
		this.loopCount=loopCount;
		this.ignoreFirstDelay=ignoreFirstDelay;
	}
	
	public int getDelay()
	{
		return delay;
	}
	
	public boolean isObstruct()
	{
		return obstruct;
	}
	
	public int getLoopCount()
	{
		return loopCount;
	}
	
	public boolean isIgnoreFirstDelay()
	{
		return ignoreFirstDelay;
	}
	
	public void addMonster(SceneMonster monster)
	{
		monsters.add(monster);
	}
	
	public ArrayList<SceneMonster> getMonsters()
	{
		return monsters;
	}
}
