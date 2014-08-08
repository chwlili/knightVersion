package org.game.knight.version.packer.world.model;

public class WorldCity
{
	/**
	 * ID
	 */
	public final int id;
	
	/**
	 * 名称
	 */
	public final String name;
	
	/**
	 * 场景列表
	 */
	public final Scene[] scenes;
	
	/**
	 * 构造函数
	 * @param id
	 * @param name
	 * @param scenes
	 */
	public WorldCity(int id,String name,Scene[] scenes)
	{
		this.id=id;
		this.name=name;
		this.scenes=scenes!=null ? scenes:new Scene[]{};
	}
}
