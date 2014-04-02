package org.game.knight.version.packer.world.attire;

import org.game.knight.version.packer.world.ImgFile;

public class AttireAnim
{
	private int id;
	private int groupID;
	private int layerID;
	private int x;
	private int y;
	private float scaleX;
	private float scaleY;
	private boolean flip;
	private ImgFile img;
	private int row;
	private int col;
	private String delays;
	private int[] times;
	private String bagID;
	
	public AttireAnim(int id,int gID,int lID,int x,int y,float scaleX,float scaleY,boolean flip,ImgFile img,int row,int col,String delays,String bagID)
	{
		this.id=id;
		this.groupID=gID;
		this.layerID=lID;
		this.x=x;
		this.y=y;
		this.scaleX=scaleX;
		this.scaleY=scaleY;
		this.flip=flip;
		this.img=img;
		this.row=row;
		this.col=col;
		this.delays=delays;
		this.bagID=bagID;
		
		String[] parts=delays.split(",");
		times=new int[parts.length];
		for(int i=0;i<parts.length;i++)
		{
			if(parts[i]==null)
			{
				times[i]=0;
			}
			else
			{
				times[i]=Integer.parseInt(parts[i]);
			}
		}
	}
	
	public int getID()
	{
		return id;
	}
	
	public int getGroupID()
	{
		return groupID;
	}
	
	public int getLayerID()
	{
		return layerID;
	}
	
	public int getX()
	{
		return x;
	}
	public int getY()
	{
		return y;
	}
	
	public float getScaleX()
	{
		return scaleX;
	}
	
	public float getScaleY()
	{
		return scaleY;
	}
	
	public boolean getFlip()
	{
		return flip;
	}
	
	public ImgFile getImg()
	{
		return img;
	}
	
	public int getRow()
	{
		return row;
	}
	
	public int getCol()
	{
		return col;
	}
	
	public String getDelays()
	{
		return delays;
	}
	
	public int[] getTimes()
	{
		return times;
	}
	
	public String getBagID()
	{
		return bagID;
	}
}
