package org.game.knight.version.packer.world.scene;

public class SceneLink
{
	private String achieve;
	private String finishQuest;
	private String acceptQuest;
	private int level;
	private SceneLinkFrom from;
	private SceneLinkTarget dest;
	
	public SceneLink(String achieve,String finishQuest,String acceptQuest,int level)
	{
		this.achieve=achieve;
		this.finishQuest=finishQuest;
		this.acceptQuest=acceptQuest;
		this.level=level;
	}
	
	public String getAchieve()
	{
		return achieve;
	}
	
	public String getFinishQuest()
	{
		return finishQuest;
	}
	
	public String getAcceptQuest()
	{
		return acceptQuest;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public SceneLinkFrom getFrom()
	{
		return from;
	}
	
	public void setLinkFrom(SceneLinkFrom from)
	{
		this.from=from;
	}
	
	public SceneLinkTarget getDest()
	{
		return dest;
	}
	
	public void setLinkDest(SceneLinkTarget dest)
	{
		this.dest=dest;
	}
}
