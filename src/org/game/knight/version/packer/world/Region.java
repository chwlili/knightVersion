package org.game.knight.version.packer.world;

import java.io.File;

public class Region
{
	private String ownerChecksum;
	private int index;

	private int x;
	private int y;
	private int w;
	private int h;

	private int clipX;
	private int clipY;
	private int clipW;
	private int clipH;

	private String texturePath;
	private int textureX = 0;
	private int textureY = 0;
	private int textureR = 0;

	private File file;
	private int time;

	/**
	 * 建立一个新的区域
	 * 
	 * @param clip
	 * @param file
	 */
	public Region(String ownerChecksum, int index, GridImg clip, File file, int time)
	{
		this.ownerChecksum = ownerChecksum;
		this.index = index;

		this.x = clip.getX();
		this.y = clip.getY();
		this.w = clip.getW();
		this.h = clip.getH();

		this.clipX = clip.getClipX();
		this.clipY = clip.getClipY();
		this.clipW = clip.getClipW();
		this.clipH = clip.getClipH();

		this.file = file;
		this.time = time;
	}

	public Region(String ownerChecksum, int index, int x, int y, int w, int h, int clipX, int clipY, int clipW, int clipH, int textureX, int textureY, int textureR)
	{
		this.ownerChecksum = ownerChecksum;
		this.index = index;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.clipX = clipX;
		this.clipY = clipY;
		this.clipW = Math.min(2048, clipW);
		this.clipH = Math.min(2048, clipH);
		this.textureX = textureX;
		this.textureY = textureY;
		this.textureR = textureR;
	}

	// --------------------------------------------------

	/**
	 * 获取所有者校验码
	 * 
	 * @return
	 */
	public String getOwnerChecksum()
	{
		return ownerChecksum;
	}

	/**
	 * 获取区域的索引号
	 * 
	 * @return
	 */
	public int getIndex()
	{
		return index;
	}

	/**
	 * 所有者文件
	 * 
	 * @return
	 */
	public File getFile()
	{
		return file;
	}

	/**
	 * 时间
	 * 
	 * @return
	 */
	public int getTime()
	{
		return time;
	}

	// --------------------------------------------------

	/**
	 * 获取原始X坐标
	 * 
	 * @return
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * 获取原始Y坐标
	 * 
	 * @return
	 */
	public int getY()
	{
		return y;
	}

	/**
	 * 获取原始宽度
	 * 
	 * @return
	 */
	public int getW()
	{
		return w;
	}

	/**
	 * 获取原始高度
	 * 
	 * @return
	 */
	public int getH()
	{
		return h;
	}

	// --------------------------------------------------

	/**
	 * 获取裁切X坐标
	 * 
	 * @return
	 */
	public int getClipX()
	{
		return clipX;
	}

	/**
	 * 获取裁切Y坐标
	 * 
	 * @return
	 */
	public int getClipY()
	{
		return clipY;
	}

	/**
	 * 获取裁切宽度
	 * 
	 * @return
	 */
	public int getClipW()
	{
		return clipW;
	}

	/**
	 * 获取裁切高度
	 * 
	 * @return
	 */
	public int getClipH()
	{
		return clipH;
	}

	// ------------------------------------------------------

	/**
	 * 获取所属贴图的路径
	 * 
	 * @return
	 */
	public String getTexturePath()
	{
		return texturePath;
	}

	/**
	 * 设置所属贴图的路径
	 * 
	 * @return
	 */
	public void setTexturePath(String path)
	{
		texturePath = path;
	}

	/**
	 * 获取X坐标
	 * 
	 * @return
	 */
	public int getTextureX()
	{
		return textureX;
	}

	/**
	 * 设置X坐标
	 * 
	 * @param x
	 */
	public void setTextureX(int x)
	{
		this.textureX = x;
	}

	/**
	 * 获取Y坐标
	 * 
	 * @return
	 */
	public int getTextureY()
	{
		return textureY;
	}

	/**
	 * 设置Y坐标
	 * 
	 * @param textureX
	 */
	public void setTextureY(int y)
	{
		this.textureY = y;
	}

	/**
	 * 获取旋转
	 * 
	 * @return
	 */
	public int getTextureR()
	{
		return textureR;
	}

	/**
	 * 设置旋转
	 * 
	 * @return
	 */
	public void setTextureR(int r)
	{
		this.textureR = r;
	}
}
