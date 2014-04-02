package org.chw.swf.writer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import org.chw.util.FileUtil;
import org.chw.util.ZlibUtil;



public class SwfBitmap
{
	private byte[] rgb;
	private byte[] alpha;
	private int width;
	private int height;
	private String packName;
	private String className;

	/**
	 * 构造函数
	 * 
	 * @param file
	 * @param quality
	 */
	public SwfBitmap(byte[] rgb,byte[] alpha,int width,int height,String packName,String className)
	{
		this.rgb=rgb;
		this.alpha=alpha;
		this.width=width;
		this.height=height;
		this.packName=packName;
		this.className=className;
	}

	/**
	 * 从内存中构建的文件创建
	 * @param fileBytes
	 * @param packName
	 * @param className
	 * @param encode
	 * @throws IOException
	 */
	public SwfBitmap(byte[] fileBytes,String packName,String className,Boolean encode) throws IOException
	{
		BufferedImage img = ImageIO.read(new ByteArrayInputStream(fileBytes));
		
		int imageW=img.getData().getBounds().width;
		int imageH=img.getData().getBounds().height;
		
		boolean is32=img.getType()==BufferedImage.TYPE_4BYTE_ABGR; //png
		
		encode=is32;
		
		if(encode)
		{
			int[] rgbs=new int[imageW*imageH];
			rgbs=img.getRGB(0, 0, imageW, imageH, rgbs, 0,imageW);
			
			byte[] alphas=new byte[imageW*imageH];
			for(int j=0;j<rgbs.length;j++)
			{
				byte aAlpha=(byte)((rgbs[j]>>24) & 0xFF);
				alphas[j]=aAlpha;
			}
			this.alpha=ZlibUtil.compress(alphas);
	
			BufferedImage imgB = new BufferedImage(imageW, imageH, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics=(Graphics2D) imgB.getGraphics();
			graphics.drawImage(img, 0, 0, imageW, imageH, 0, 0, imageW, imageH, null);
			graphics.dispose();
			
			Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
			if(writers.hasNext())
			{
				ImageWriter writer = writers.next();
				ImageWriteParam param = writer.getDefaultWriteParam();
				param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				param.setCompressionQuality(.99f);
				ByteArrayOutputStream byteOutput=new ByteArrayOutputStream();
				
				writer.setOutput(new MemoryCacheImageOutputStream(byteOutput));
				writer.write(null, new IIOImage(imgB, null, null), param);
				
				this.rgb=byteOutput.toByteArray();
			}
		}
		else
		{
			this.rgb=fileBytes;
			this.alpha=null;
		}
		
		this.width=imageW;
		this.height=imageH;
		this.packName=packName;
		this.className=className;
	}
	
	/**
	 * 从图像文件构建
	 * @param file
	 * @throws IOException 
	 */
	public SwfBitmap(File file,String packName,String className,Boolean encode) throws IOException
	{
		BufferedImage img = ImageIO.read(new FileInputStream(file));
		
		int imageW=img.getData().getBounds().width;
		int imageH=img.getData().getBounds().height;
		
		boolean is32=img.getType()==BufferedImage.TYPE_4BYTE_ABGR; //png
		
		encode=is32;
		
		if(encode)
		{
			int[] rgbs=new int[imageW*imageH];
			rgbs=img.getRGB(0, 0, imageW, imageH, rgbs, 0,imageW);
			
			byte[] alphas=new byte[imageW*imageH];
			for(int j=0;j<rgbs.length;j++)
			{
				byte aAlpha=(byte)((rgbs[j]>>24) & 0xFF);
				alphas[j]=aAlpha;
			}
			this.alpha=ZlibUtil.compress(alphas);
	
			BufferedImage imgB = new BufferedImage(imageW, imageH, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics=(Graphics2D) imgB.getGraphics();
			graphics.drawImage(img, 0, 0, imageW, imageH, 0, 0, imageW, imageH, null);
			graphics.dispose();
			
			Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
			if(writers.hasNext())
			{
				ImageWriter writer = writers.next();
				ImageWriteParam param = writer.getDefaultWriteParam();
				param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				param.setCompressionQuality(.99f);
				ByteArrayOutputStream byteOutput=new ByteArrayOutputStream();
				
				writer.setOutput(new MemoryCacheImageOutputStream(byteOutput));
				writer.write(null, new IIOImage(imgB, null, null), param);
				
				this.rgb=byteOutput.toByteArray();
			}
		}
		else
		{
			this.rgb=FileUtil.getFileBytes(file);
			this.alpha=null;
		}
		
		this.width=imageW;
		this.height=imageH;
		this.packName=packName;
		this.className=className;
	}

	/**
	 * 获取RGB字节
	 * 
	 * @return
	 */
	public byte[] getRGB()
	{
		return rgb;
	}

	/**
	 * 获取Alpha字节
	 * 
	 * @return
	 */
	public byte[] getAlpha()
	{
		return alpha;
	}
	
	/**
	 * 获取宽度
	 * @return
	 */
	public int getWidth()
	{
		return width;
	}
	
	/**
	 * 获取高度
	 * @return
	 */
	public int getHeight()
	{
		return height;
	}
	
	/**
	 * 获取包名
	 * @return
	 */
	public String getPackName()
	{
		return packName;
	}
	
	/**
	 * 获取类名
	 * @return
	 */
	public String getClassName()
	{
		return className;
	}
}
