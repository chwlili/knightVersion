package org.game.knight.version.packer.world.model;

public class WorldCity
{
	/**
	 * ID
	 */
	public final int id;
	
	/**
	 * ����
	 */
	public final String name;
	
	/**
	 * �����б�
	 */
	public final Scene[] scenes;
	
	/**
	 * ���캯��
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
