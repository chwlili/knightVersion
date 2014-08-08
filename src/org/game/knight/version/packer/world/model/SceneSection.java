package org.game.knight.version.packer.world.model;

public class SceneSection
{
	/**
	 * X����
	 */
	public final int position;
	
	/**
	 * ����
	 */
	public final int type;
	
	/**
	 * ���캯��
	 * @param position
	 * @param type
	 */
	public SceneSection(int position,int type)
	{
		this.position=position;
		this.type=type;
	}
}
