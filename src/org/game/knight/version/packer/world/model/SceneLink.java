package org.game.knight.version.packer.world.model;

public class SceneLink
{
	/**
	 * ���
	 */
	public final SceneLinkFrom from;
	
	/**
	 * Ŀ���
	 */
	public final SceneLinkTarget dest;
	
	/**
	 * �ɾ�����
	 */
	public final String achieve;
	
	/**
	 * �����������
	 */
	public final String finishQuest;
	
	/**
	 * ������������
	 */
	public final String acceptQuest;
	
	/**
	 * �ȼ�����
	 */
	public final int level;
	
	/**
	 * ���캯��
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
