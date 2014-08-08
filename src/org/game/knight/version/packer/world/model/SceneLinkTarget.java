package org.game.knight.version.packer.world.model;


public class SceneLinkTarget
{
	/**
	 * 唯一ID
	 */
	public final String hash;
	
	/**
	 * 场景ID
	 */
	public final int sceneID;
	
	/**
	 * 场景名称
	 */
	public final String sceneName;
	
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
	 * 构造函数
	 * @param hash
	 * @param sceneID
	 * @param sceneName
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	public SceneLinkTarget(String hash,int sceneID,String sceneName,int x,int y,int width,int height)
	{
		this.hash=hash;
		this.sceneID=sceneID;
		this.sceneName=sceneName;
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
	}
}
