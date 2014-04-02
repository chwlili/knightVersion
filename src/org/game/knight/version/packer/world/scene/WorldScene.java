package org.game.knight.version.packer.world.scene;

public class WorldScene
{
	private int cityID;
	private String cityName;
	private int sceneID;
	private String sceneName;
	private int sceneType;
	private int timeLimit;
	private int sceneGroup;
	private Scene data;
	
	public WorldScene(int cityID,String cityName,int sceneID,String sceneName,int sceneType,int timeLimit,int sceneGroup,Scene data)
	{
		this.cityID=cityID;
		this.cityName=cityName;
		this.sceneID=sceneID;
		this.sceneName=sceneName;
		this.sceneType=sceneType;
		this.timeLimit=timeLimit;
		this.sceneGroup=sceneGroup;
		this.data=data;
	}
	
	public int getCityID()
	{
		return cityID;
	}
	
	public String getCityName()
	{
		return cityName;
	}
	
	public int getSceneID()
	{
		return sceneID;
	}
	
	public String getSceneName()
	{
		return sceneName;
	}
	
	public int getSceneType()
	{
		return sceneType;
	}
	
	public int getSceneLimit()
	{
		return timeLimit;
	}
	
	public int getSceneGroup()
	{
		return sceneGroup;
	}
	
	public Scene getData()
	{
		return data;
	}
}
