package org.game.knight.version.packer.world.model;

public class SceneLink
{
	/**
	 * 起点
	 */
	public final SceneLinkFrom from;
	
	/**
	 * 目标点
	 */
	public final SceneLinkTarget dest;
	
	/**
	 * 成就限制
	 */
	public final String achieve;
	
	/**
	 * 完成任务限制
	 */
	public final String finishQuest;
	
	/**
	 * 接受任务限制
	 */
	public final String acceptQuest;
	
	/**
	 * 等级限制
	 */
	public final int level;
	
	/**
	 * 构造函数
	 * @param from
	 * @param dest
	 * @param achieve
	 * @param finishQuest
	 * @param acceptQuest
	 * @param level
	 */
	public SceneLink(SceneLinkFrom from,SceneLinkTarget dest,String achieve,String finishQuest,String acceptQuest,int level)
	{
		this.from=from;
		this.dest=dest;
		this.achieve=achieve;
		this.finishQuest=finishQuest;
		this.acceptQuest=acceptQuest;
		this.level=level;
	}
}
