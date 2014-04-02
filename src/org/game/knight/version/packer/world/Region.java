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
	 * ����һ���µ�����
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
	 * ��ȡ������У����
	 * 
	 * @return
	 */
	public String getOwnerChecksum()
	{
		return ownerChecksum;
	}

	/**
	 * ��ȡ�����������
	 * 
	 * @return
	 */
	public int getIndex()
	{
		return index;
	}

	/**
	 * �������ļ�
	 * 
	 * @return
	 */
	public File getFile()
	{
		return file;
	}

	/**
	 * ʱ��
	 * 
	 * @return
	 */
	public int getTime()
	{
		return time;
	}

	// --------------------------------------------------

	/**
	 * ��ȡԭʼX����
	 * 
	 * @return
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * ��ȡԭʼY����
	 * 
	 * @return
	 */
	public int getY()
	{
		return y;
	}

	/**
	 * ��ȡԭʼ���
	 * 
	 * @return
	 */
	public int getW()
	{
		return w;
	}

	/**
	 * ��ȡԭʼ�߶�
	 * 
	 * @return
	 */
	public int getH()
	{
		return h;
	}

	// --------------------------------------------------

	/**
	 * ��ȡ����X����
	 * 
	 * @return
	 */
	public int getClipX()
	{
		return clipX;
	}

	/**
	 * ��ȡ����Y����
	 * 
	 * @return
	 */
	public int getClipY()
	{
		return clipY;
	}

	/**
	 * ��ȡ���п��
	 * 
	 * @return
	 */
	public int getClipW()
	{
		return clipW;
	}

	/**
	 * ��ȡ���и߶�
	 * 
	 * @return
	 */
	public int getClipH()
	{
		return clipH;
	}

	// ------------------------------------------------------

	/**
	 * ��ȡ������ͼ��·��
	 * 
	 * @return
	 */
	public String getTexturePath()
	{
		return texturePath;
	}

	/**
	 * ����������ͼ��·��
	 * 
	 * @return
	 */
	public void setTexturePath(String path)
	{
		texturePath = path;
	}

	/**
	 * ��ȡX����
	 * 
	 * @return
	 */
	public int getTextureX()
	{
		return textureX;
	}

	/**
	 * ����X����
	 * 
	 * @param x
	 */
	public void setTextureX(int x)
	{
		this.textureX = x;
	}

	/**
	 * ��ȡY����
	 * 
	 * @return
	 */
	public int getTextureY()
	{
		return textureY;
	}

	/**
	 * ����Y����
	 * 
	 * @param textureX
	 */
	public void setTextureY(int y)
	{
		this.textureY = y;
	}

	/**
	 * ��ȡ��ת
	 * 
	 * @return
	 */
	public int getTextureR()
	{
		return textureR;
	}

	/**
	 * ������ת
	 * 
	 * @return
	 */
	public void setTextureR(int r)
	{
		this.textureR = r;
	}
}
