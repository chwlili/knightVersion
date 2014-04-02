package org.game.knight.version.packer.world;

import java.io.File;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class ImgFile
{
	private File file;
	private String innerPath;
	
	private int width=-1;
	private int height=-1;
	
	/**
	 * ���캯��
	 * @param file
	 * @param innerPath
	 * @param innerDirPath
	 */
	public ImgFile(File file,String innerPath,String innerDirPath)
	{
		this.file=file;
		this.innerPath=innerPath;
	}
	
	/**
	 * ��ȡ�ļ�
	 * @return
	 */
	public File getFile()
	{
		return file;
	}
	
	/**
	 * ��ȡ�ڲ�·��
	 * @return
	 */
	public String getInnerpath()
	{
		return innerPath;
	}
	
	/**
	 * ��ȡ���
	 * @return
	 */
	public int getWidth()
	{
		if(width==-1)
		{
			readSize();
		}
		return width;
	}
	
	/**
	 * ��ȡ�߶�
	 * @return
	 */
	public int getHeight()
	{
		if(height==-1)
		{
			readSize();
		}
		return height;
	}
	
	/**
	 * ��ȡ��С
	 */
	private void readSize()
	{
		Image img=new Image(Display.getCurrent(), file.getAbsolutePath());
		if(img!=null)
		{
			if(img.getImageData()!=null)
			{
				width=img.getImageData().width;
				height=img.getImageData().height;
			}
			else
			{
				width=0;
				height=0;
			}
			img.dispose();
		}
	}
}
