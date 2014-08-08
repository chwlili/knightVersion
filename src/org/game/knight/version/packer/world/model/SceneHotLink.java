package org.game.knight.version.packer.world.model;

public class SceneHotLink
{
	/**
	 * Ŀ�곡��ID
	 */
	public final int toSceneID;
	
	/**
	 * Ŀ�곡������
	 */
	public final String toSceneName;
	
	/**
	 * Ŀ�곡��X����
	 */
	public final int toSceneX;
	
	/**
	 * Ŀ�곡��Y����
	 */
	public final int toSceneY;
	
	/**
	 * ���캯��
	 * @param toSceneID
	 * @param toSceneName
	 * @param toSceneX
	 * @param toSceneY
	 */
	public SceneHotLink(int toSceneID,String toSceneName,int toSceneX,int toSceneY)
	{
		this.toSceneID=toSceneID;
		this.toSceneName=toSceneName;
		this.toSceneX=toSceneX;
		this.toSceneY=toSceneY;
	}
}
