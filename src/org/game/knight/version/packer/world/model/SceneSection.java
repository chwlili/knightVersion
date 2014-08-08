package org.game.knight.version.packer.world.model;

public class SceneSection
{
	/**
	 * X坐标
	 */
	public final int position;
	
	/**
	 * 类型
	 */
	public final int type;
	
	/**
	 * 构造函数
	 * @param position
	 * @param type
	 */
	public SceneSection(int position,int type)
	{
		this.position=position;
		this.type=type;
	}
}
