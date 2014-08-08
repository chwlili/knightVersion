package org.game.knight.version.packer.world.model;

public class SceneTrap
{
	/**
	 * 类型
	 */
	public final int type;
	
	/**
	 * X坐标
	 */
	public final int x;
	
	/**
	 * Y坐标
	 */
	public final int y;
	
	/**
	 * 宽度
	 */
	public final int width;
	
	/**
	 * 高度
	 */
	public final int height;
	
	/**
	 * 任务ID
	 */
	public final int quest;
	
	/**
	 * 内容
	 */
	public final String content;
	
	/**
	 * 构造函数
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
