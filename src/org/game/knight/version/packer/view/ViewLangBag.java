package org.game.knight.version.packer.view;

import java.io.IOException;

import org.chw.swf.writer.SwfWriter;
import org.chw.swf.writer.SwfXML;


public class ViewLangBag extends ViewFileBag
{
	private long typeID;
	private byte[] content;
	
	/**
	 * 添加文件
	 * @param file
	 */
	public ViewLangBag(String key,long typeID,byte[] content)
	{
		super(key, null);
		
		this.typeID=typeID;
		this.content=content;
	}
	
	/**
	 * 生成
	 */
	@Override
	public byte[] build()
	{
		SwfWriter writer=new SwfWriter();
		writer.addXml(new SwfXML(content, "app.files", "FILE_"+typeID));
		
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
}
