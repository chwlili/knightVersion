package org.game.knight.version.packer.world.model;

import java.util.ArrayList;

public class ScenePart
{
	/**
	 * ��ʼλ��
	 */
	public final int left;
	
	/**
	 * ����λ��
	 */
	public final int right;
	
	/**
	 * ˢ�µ��б�
	 */
	public final ArrayList<SceneMonsterTimer> timers=new ArrayList<SceneMonsterTimer>(); 
	
	/**
	 * ���캯��
	 * @param left
	 * @param right
	 */
	public ScenePart(int left,int right)
	{
		this.left=left;
		this.right=right;
	}
}
