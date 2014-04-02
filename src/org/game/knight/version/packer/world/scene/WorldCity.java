package org.game.knight.version.packer.world.scene;

public class WorldCity
{
	private int id;
	private String name;
	private WorldScene[] scenes;
	
	public WorldCity(int id,String name,WorldScene[] scenes)
	{
		this.id=id;
		this.name=name;
		this.scenes=scenes;
	}
	
	public int getID()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public WorldScene[] getScenes()
	{
		return scenes;
	}
}
