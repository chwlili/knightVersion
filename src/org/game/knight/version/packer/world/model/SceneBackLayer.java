package org.game.knight.version.packer.world.model;


public class SceneBackLayer
{
	/**
	 * ����
	 */
	public final String name;
	
	/**
	 * Xƫ��
	 */
	public final int x;
	
	/**
	 * Yƫ��
	 */
	public final int y;
	
	/**
	 * �ٶ�
	 */
	public final float speed;
	
	/**
	 * ͼ��
	 */
	public final AttireBitmap img;
	
	/**
	 * ���캯��
	 * @param name
	 * @param x
	 * @param y
	 * @param speed
	 * @param img
	 * @param atfGroup
	 */
	public SceneBackLayer(String name,int x,int y,float speed,AttireBitmap img)
	{
		this.name=name;
		this.x=x;
		this.y=y;
		this.speed=speed;
		this.img=img;
	}
}
