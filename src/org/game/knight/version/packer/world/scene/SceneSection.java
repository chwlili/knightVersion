package org.game.knight.version.packer.world.scene;

public class SceneSection
{
	private int position;
	private int type;
	
	public SceneSection(int position,int type)
	{
		this.position=position;
		this.type=type;
	}
	
	public int getPosition()
	{
		return position;
	}
	
	public int getType()
	{
		return type;
	}
}
