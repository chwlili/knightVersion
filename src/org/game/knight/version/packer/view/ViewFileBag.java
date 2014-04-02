package org.game.knight.version.packer.view;

import java.io.IOException;

import org.chw.swf.writer.SwfBitmap;
import org.chw.swf.writer.SwfMp3;
import org.chw.swf.writer.SwfWriter;
import org.chw.swf.writer.SwfXML;


public class ViewFileBag extends ViewFileSwf
{
	private ViewFile[] files;
	private byte[] content;
	
	/**
	 * ����ļ�
	 * @param file
	 */
	public ViewFileBag(String key,ViewFile[] files)
	{
		super(key, null);
		
		this.files=files;
	}
	
	/**
	 * ��ȡ�ļ���
	 * @return
	 */
	public ViewFile[] getFiles()
	{
		return files;
	}
	
	/**
	 * ����
	 */
	public byte[] build(boolean swfZip)
	{
		SwfWriter writer=new SwfWriter();
		
		for(ViewFile file:files)
		{
			String path=file.getFile().getAbsolutePath();
			
			if(isImg(path))
			{
				try
				{
					writer.addBitmap(new SwfBitmap(file.getFile(), "app.files", "FILE_"+file.getClassID(),swfZip));
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			else if(isMp3(path))
			{
				writer.addMp3(new SwfMp3(file.getContents(), "app.files", "FILE_"+file.getClassID()));
			}
			else if(isXml(path))
			{
				writer.addXml(new SwfXML(file.getContents(), "app.files", "FILE_"+file.getClassID()));
			}
		}
		
		try
		{
			content=writer.toBytes(true);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return content;
	}

	/**
	 * �Ƿ�Ϊͼ��
	 * @param path
	 * @return
	 */
	private boolean isImg(String path)
	{
		String url=path.toLowerCase();
		if(url.endsWith(".jpg"))
		{
			return true;
		}
		else if(url.endsWith(".png"))
		{
			return true;
		}
		else if(url.endsWith(".jpeg"))
		{
			return true;
		}
		return false;
	}
	
	/**
	 * �Ƿ�ΪXML
	 * @param path
	 * @return
	 */
	private boolean isXml(String path)
	{
		String url=path.toLowerCase();
		if(url.endsWith(".xml"))
		{
			return true;
		}
		return false;
	}

	/**
	 * �Ƿ�ΪMP3
	 * @param path
	 * @return
	 */
	private boolean isMp3(String path)
	{
		String url=path.toLowerCase();
		if(url.endsWith(".mp3"))
		{
			return true;
		}
		return false;
	}
}
