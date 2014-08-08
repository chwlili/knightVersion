package org.game.knight.version.packer.world.model;

import java.util.ArrayList;

public class ScenePart
{
	/**
	 * 起始位置
	 */
	public final int left;
	
	/**
	 * 结束位置
	 */
	public final int right;
	
	/**
	 * 刷新点列表
	 */
	public final ArrayList<SceneMonsterTimer> timers=new ArrayList<SceneMonsterTimer>(); 
	
	/**
	 * 构造函数
	 * @param left
	 * @param right
	 */
	public ScenePart(int left,int right)
	{
		this.left=left;
		this.right=right;
	}
}
