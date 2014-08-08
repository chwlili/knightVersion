package org.game.knight.version.packer.world.model;


public class SceneForeLayer
{
	/**
	 * ����
	 */
	public final String name;
	
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
	public final int w;
	
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
	 * @param width
	 * @param speed
	 * @param img
	 * @param atfGroup
	 */
	public SceneForeLayer(String name, int x, int y, int width, float speed, AttireBitmap img)
	{
		this.name = name;
		this.x = x;
		this.y = y;
		this.w = width;
		this.speed = speed;
		this.img = img;
	}
}
