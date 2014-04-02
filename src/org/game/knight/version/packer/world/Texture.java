package org.game.knight.version.packer.world;

public class Texture
{
	private int w;
	private int h;

	private Region[] regions;
	private String atfFilePath;
	private String pngFilePath;
	private String xmlFilePath;

	/**
	 * 创建贴图
	 * 
	 * @param w
	 * @param h
	 * @param regions
	 */
	public Texture(int w, int h, Region[] regions)
	{
		this.w = w;
		this.h = h;
		this.regions = regions;
	}

	/**
	 * 从历史信息创建
	 * 
	 * @param pngSavePath
	 * @param xmlSavePath
	 * @param regions
	 */
	public Texture(String atfSavePath, String pngSavePath, String xmlSavePath, Region[] regions)
	{
		this.atfFilePath = atfSavePath;
		this.pngFilePath = pngSavePath;
		this.xmlFilePath = xmlSavePath;
		this.regions = regions;
	}

	/**
	 * 贴图宽度
	 * 
	 * @return
	 */
	public int getWidth()
	{
		return w;
	}

	/**
	 * 贴图高度
	 * 
	 * @return
	 */
	public int getHeight()
	{
		return h;
	}

	/**
	 * 获取png文件路径
	 * 
	 * @return
	 */
	public String getPngFilePath()
	{
		return pngFilePath;
	}

	/**
	 * 设置png文件路径
	 * 
	 * @return
	 */
	public void setPngFilePath(String path)
	{
		pngFilePath = path;
	}

	/**
	 * 获取atf文件路径
	 * 
	 * @param path
	 */
	public String getAtfFilePath()
	{
		return atfFilePath;
	}

	/**
	 * 设置atf文件路径
	 * 
	 * @param path
	 */
	public void setAtfFilePath(String path)
	{
		atfFilePath = path;
	}

	/**
	 * 获取xml文件路径
	 * 
	 * @return
	 */
	public String getXmlFilePath()
	{
		return xmlFilePath;
	}

	/**
	 * 设置xml文件路径
	 * 
	 * @return
	 */
	public void setXmlFilePath(String path)
	{
		xmlFilePath = path;
	}

	/**
	 * 范围列表
	 * 
	 * @return
	 */
	public Region[] getRegions()
	{
		return regions;
	}
}
