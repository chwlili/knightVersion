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
	 * ������ͼ
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
	 * ����ʷ��Ϣ����
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
	 * ��ͼ���
	 * 
	 * @return
	 */
	public int getWidth()
	{
		return w;
	}

	/**
	 * ��ͼ�߶�
	 * 
	 * @return
	 */
	public int getHeight()
	{
		return h;
	}

	/**
	 * ��ȡpng�ļ�·��
	 * 
	 * @return
	 */
	public String getPngFilePath()
	{
		return pngFilePath;
	}

	/**
	 * ����png�ļ�·��
	 * 
	 * @return
	 */
	public void setPngFilePath(String path)
	{
		pngFilePath = path;
	}

	/**
	 * ��ȡatf�ļ�·��
	 * 
	 * @param path
	 */
	public String getAtfFilePath()
	{
		return atfFilePath;
	}

	/**
	 * ����atf�ļ�·��
	 * 
	 * @param path
	 */
	public void setAtfFilePath(String path)
	{
		atfFilePath = path;
	}

	/**
	 * ��ȡxml�ļ�·��
	 * 
	 * @return
	 */
	public String getXmlFilePath()
	{
		return xmlFilePath;
	}

	/**
	 * ����xml�ļ�·��
	 * 
	 * @return
	 */
	public void setXmlFilePath(String path)
	{
		xmlFilePath = path;
	}

	/**
	 * ��Χ�б�
	 * 
	 * @return
	 */
	public Region[] getRegions()
	{
		return regions;
	}
}
