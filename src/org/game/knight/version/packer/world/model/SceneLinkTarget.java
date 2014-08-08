package org.game.knight.version.packer.world.model;


public class SceneLinkTarget
{
	/**
	 * ΨһID
	 */
	public final String hash;
	
	/**
	 * ����ID
	 */
	public final int sceneID;
	
	/**
	 * ��������
	 */
	public final String sceneName;
	
	/**
	 * X����
	 */
	public final int x;
	
	/**
	 * Y����
	 */
	public final int y;
	
	/**
	 * ���
	 */
	public final int width;
	
	/**
	 * �߶�
	 */
	public final int height;
	
	/**
	 * ���캯��
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
