package org.game.knight.version.packer.world;

import java.io.File;

import org.game.knight.version.packer.world.model.ProjectImgFile;

public class GridImgKey
{
	private String checksum;
	private int rowCount;
	private int colCount;
	private ProjectImgFile img;
	private int[] times;

	public GridImgKey(String checksum, int rowCount, int colCount, int[] times)
	{
		this.checksum = checksum;
		this.rowCount = rowCount;
		this.colCount = colCount;
		this.times = times;
	}

	public GridImgKey(String checksum, int rowCount, int colCount, ProjectImgFile img, int[] times)
	{
		this.checksum = checksum;
		this.rowCount = rowCount;
		this.colCount = colCount;

		this.img = img;
		this.times = times;
	}

	/**
	 * У����
	 * 
	 * @return
	 */
	public String getChecksum()
	{
		return checksum;
	}

	/**
	 * ����
	 * 
	 * @return
	 */
	public int getRowCount()
	{
		return rowCount;
	}

	/**
	 * ����
	 * 
	 * @return
	 */
	public int getColCount()
	{
		return colCount;
	}

	/**
	 * ʱ���б�
	 * 
	 * @return
	 */
	public int[] getTimes()
	{
		return times;
	}

	public void setTime(int[] times)
	{
		this.times = times;
	}

	/**
	 * �ļ�
	 * 
	 * @return
	 */
	public File getFile()
	{
		return img.file;
	}

	/**
	 * �ļ��ڲ�·��
	 * 
	 * @return
	 */
	public String getFileInnerPath()
	{
		return img.url;
	}

	@Override
	public int hashCode()
	{
		return (checksum + "_" + rowCount + "_" + colCount).hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (super.equals(obj))
		{
			return true;
		}
		else
		{
			if (obj instanceof GridImgKey)
			{
				GridImgKey img = (GridImgKey) obj;

				return img.checksum.equals(checksum) && img.rowCount == rowCount && img.colCount == colCount /*&& img.getTimeString().equals(getTimeString())*/;
			}
		}
		return false;
	}

	@Override
	public String toString()
	{
		return checksum + "_" + rowCount + "_" + colCount + "_" + getTimeString();
	}

	/**
	 * ��ȡʱ���ַ���
	 * 
	 * @return
	 */
	private String getTimeString()
	{
		if (times != null)
		{
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < times.length; i++)
			{
				if (i > 0)
				{
					sb.append(".");
				}
				sb.append(times[i]);
			}
			
			return sb.toString();
		}
		
		return "";
	}
}
