package org.game.knight.version.packer.world.model;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;

public class ProjectImgFile extends ProjectFile
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4323116449220773420L;

	/**
	 * 宽度
	 */
	public final int width;

	/**
	 * 高度
	 */
	public final int height;

	/**
	 * 构造函数
	 * 
	 * @param file
	 * @param url
	 */
	public ProjectImgFile(File file, String url, String md5, String gid)
	{
		super(file, url, md5, gid);

		int w = 0;
		int h = 0;

		String ext="";
		int dotIndex=file.getName().lastIndexOf(".");
		if(dotIndex!=-1)
		{
			ext=file.getName().substring(dotIndex+1);
		}
		
		Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(ext);
		if (iter.hasNext())
		{
			ImageReader reader = iter.next();
			try
			{
				reader.setInput(new FileImageInputStream(file));
				w = reader.getWidth(reader.getMinIndex());
				h = reader.getHeight(reader.getMinIndex());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				reader.dispose();
			}
		}

		this.width = w;
		this.height = h;
	}
}
