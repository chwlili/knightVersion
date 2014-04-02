package org.game.knight.version.packer.world.scene;

import java.util.ArrayList;

public class SceneHot
{
	private int x;
	private int y;
	private int width;
	private int height;
	private ArrayList<SceneLink> links;
	
	private String acceptableQuests;
	private String acceptedQuests;
	private String submitableQuests;
	private String submitedQuests;
	
	private SceneHotLink[] lines;
	
	public SceneHot(int x,int y,int width,int height,ArrayList<SceneLink> links,String acceptableQuests,String acceptedQuests,String submitableQuests,String submitedQuests)
	{
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		this.links=links;

		this.acceptableQuests=acceptableQuests;
		this.acceptedQuests=acceptedQuests;
		this.submitableQuests=submitableQuests;
		this.submitedQuests=submitedQuests;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public SceneHotLink[] getLinks()
	{
		if(lines==null)
		{
			ArrayList<SceneHotLink> tmpList=new ArrayList<SceneHotLink>();
			
			if(links!=null)
			{
				for(SceneLink tmp:links)
				{
					if(tmp.getDest()!=null && tmp.getDest().getScene()!=null)
					{
						Scene scene=tmp.getDest().getScene();
						SceneLinkTarget target=tmp.getDest();
						
						tmpList.add(new SceneHotLink(scene.getSceneID(), scene.getSceneName(), target.getX(), target.getY()));
					}
				}
			}
			
			lines=new SceneHotLink[tmpList.size()];
			lines=tmpList.toArray(lines);
		}
		return lines;
	}
	
	public String getAcceptableQuests()
	{
		return acceptableQuests;
	}
	
	public String getAcceptedQuests()
	{
		return acceptedQuests;
	}
	
	public String getSubmitableQuests()
	{
		return submitableQuests;
	}

	public String getSubmitedQuests()
	{
		return submitedQuests;
	}
}
