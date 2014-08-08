package org.game.knight.version.packer.world.model;

public class SceneHotLink
{
	/**
	 * 目标场景ID
	 */
	public final int toSceneID;
	
	/**
	 * 目标场景名称
	 */
	public final String toSceneName;
	
	/**
	 * 目标场景X坐标
	 */
	public final int toSceneX;
	
	/**
	 * 目标场景Y坐标
	 */
	public final int toSceneY;
	
	/**
	 * 构造函数
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
