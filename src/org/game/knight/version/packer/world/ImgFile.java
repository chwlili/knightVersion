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
	 * 构造函数
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
	 * 获取文件
	 * @return
	 */
	public File getFile()
	{
		return file;
	}
	
	/**
	 * 获取内部路径
	 * @return
	 */
	public String getInnerpath()
	{
		return innerPath;
	}
	
	/**
	 * 获取宽度
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
	 * 获取高度
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
	 * 读取大小
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
