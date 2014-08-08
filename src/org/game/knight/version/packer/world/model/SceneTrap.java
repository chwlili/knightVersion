package org.game.knight.version.packer.world.model;

public class SceneTrap
{
	/**
	 * ����
	 */
	public final int type;
	
	/**
	 * X����
	 */
	public final int x;
	
	/**
	 * Y����
	 */
	public final int y;
	
	/**
	 * ���
	 */
	public final int width;
	
	/**
	 * �߶�
	 */
	public final int height;
	
	/**
	 * ����ID
	 */
	public final int quest;
	
	/**
	 * ����
	 */
	public final String content;
	
	/**
	 * ���캯��
	 * @param type
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param quest
	 * @param content
	 */
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
}
