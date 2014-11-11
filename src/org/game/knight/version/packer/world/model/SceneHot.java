package org.game.knight.version.packer.world.model;

public class SceneHot
{
	/**
	 * ID
	 */
	public final String id;

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
	 * 可接任务限制
	 */
	public final String acceptableQuests;

	/**
	 * 已接任务限制
	 */
	public final String acceptedQuests;

	/**
	 * 可交任务限制
	 */
	public final String submitableQuests;

	/**
	 * 已交任务限制
	 */
	public final String submitedQuests;

	/**
	 * 连接线列表
	 */
	public final SceneHotLink[] links;

	/**
	 * 构造函数
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
