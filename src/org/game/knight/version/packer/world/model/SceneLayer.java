package org.game.knight.version.packer.world.model;


public class SceneLayer
{
	/**
	 * ����
	 */
	public final String name;
	
	/**
	 * Xƫ��
	 */
	public final int offsetX;
	
	/**
	 * Yƫ��
	 */
	public final int offsetY;
	
	/**
	 * �ٶ�
	 */
	public final float speed;
	
	/**
	 * ͼ��
	 */
	public final ProjectImgFile image;
	
	/**
	 * ���캯��
	 * @param name
	 * @param x
	 * @param y
	 * @param speed
	 * @param img
	 */
	public SceneLayer(String name,int offsetX,int offsetY,float speed,ProjectImgFile image)
	{
		this.name=name;
		this.offsetX=offsetX;
		this.offsetY=offsetY;
		this.speed=speed;
		this.image=image;
	}
}
