package org.game.knight.version.packer.world;

public class GridImg
{
	private int x;
	private int y;
	private int w;
	private int h;
	
	private int clipX;
	private int clipY;
	private int clipW;
	private int clipH;
	
	public GridImg(int x,int y,int w,int h)
	{
		this.x=x;
		this.y=y;
		this.w=w;
		this.h=h;
	}
	
	public GridImg(int x,int y,int w,int h,int clipX,int clipY,int clipW,int clipH)
	{
		this(x,y,w,h);
		
		setClip(clipX,clipY,clipW,clipH);
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getW()
	{
		return w;
	}
	
	public int getH()
	{
		return h;
	}
	
	public void setClip(int clipX,int clipY,int clipW,int clipH)
	{
		this.clipX=clipX;
		this.clipY=clipY;
		this.clipW=clipW;
		this.clipH=clipH;
	}
	
	public int getClipX()
	{
		return clipX;
	}
	
	public int getClipY()
	{
		return clipY;
	}
	
	public int getClipW()
	{
		return clipW;
	}
	
	public int getClipH()
	{
		return clipH;
	}
}
