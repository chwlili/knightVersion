package org.game.knight.version.packer.world.model;

public class SceneHot
{
	/**
	 * ID
	 */
	public final String id;

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
	 * �ɽ���������
	 */
	public final String acceptableQuests;

	/**
	 * �ѽ���������
	 */
	public final String acceptedQuests;

	/**
	 * �ɽ���������
	 */
	public final String submitableQuests;

	/**
	 * �ѽ���������
	 */
	public final String submitedQuests;

	/**
	 * �������б�
	 */
	public final SceneHotLink[] links;

	/**
	 * ���캯��
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param links
	 * @param acceptableQuests
	 * @param acceptedQuests
	 * @param submitableQuests
	 * @param submitedQuests
	 */
	public SceneHot(String id, int x, int y, int width, int height, String acceptableQuests, String acceptedQuests, String submitableQuests, String submitedQuests, SceneHotLink[] links)
	{
		this.id = id;

		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		this.acceptableQuests = acceptableQuests;
		this.acceptedQuests = acceptedQuests;
		this.submitableQuests = submitableQuests;
		this.submitedQuests = submitedQuests;

		this.links = links;
	}
}
