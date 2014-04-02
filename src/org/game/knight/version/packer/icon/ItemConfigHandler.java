package org.game.knight.version.packer.icon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ItemConfigHandler extends DefaultHandler
{
	private File dir;
	private ArrayList<ItemID> items=new ArrayList<ItemID>();

	private ItemHandler itemHandler=new ItemHandler();
	private ItemHandler equipHandler=new ItemHandler();
	private ItemHandler packHandler=new ItemHandler();
	
	/**
	 * 构造函数
	 * @param dir
	 */
	public ItemConfigHandler(File dir)
	{
		this.dir=dir;
	}
	
	/**
	 * 获取列表
	 * @return
	 */
	public ArrayList<ItemID> getItems()
	{
		return items;
	}
	
	/**
	 * 生成
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void build() throws ParserConfigurationException, SAXException, FileNotFoundException, IOException
	{
		File itemFile=new File(dir.getPath()+File.separator+"download"+File.separator+"item.xml");
		File equipFile=new File(dir.getPath()+File.separator+"download"+File.separator+"equipment.xml");
		File packFile=new File(dir.getPath()+File.separator+"download"+File.separator+"package.xml");
		
		if(itemFile.exists() && itemFile.isFile())
		{
			itemHandler.getItems().clear();
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();

			parser.parse(new FileInputStream(itemFile), itemHandler);
		}
		
		if(equipFile.exists() && equipFile.isFile())
		{
			equipHandler.getItems().clear();
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();

			parser.parse(new FileInputStream(equipFile), equipHandler);
		}
		
		if(packFile.exists() && packFile.isFile())
		{
			packHandler.getItems().clear();
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();

			parser.parse(new FileInputStream(packFile), packHandler);
		}
		
		items=new ArrayList<ItemID>();
		items.addAll(itemHandler.getItems());
		items.addAll(equipHandler.getItems());
		items.addAll(packHandler.getItems());
	}
	
	/**
	 * 物品处理器
	 */
	private class ItemHandler extends DefaultHandler
	{
		private boolean dataing=false;
		private boolean iding=false;
		private boolean iconing=false;
		
		private int itemID;
		private int iconID;
		
		private ArrayList<ItemID> list=new ArrayList<ItemID>();
		
		public ArrayList<ItemID> getItems()
		{
			return list;
		}
		
		public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes) throws org.xml.sax.SAXException
		{
			if(dataing)
			{
				if("id".equals(qName))
				{
					iding=true;
				}
				else if("avatar_id".equals(qName))
				{
					iconing=true;
				}
			}
			else if("data".equals(qName))
			{
				dataing=true;
			}
		}
		
		@Override
		public void characters(char[] ch, int start, int length) throws SAXException
		{
			if(iding)
			{
				try
				{
					itemID=Integer.parseInt(new String(ch,start,length));
				}
				catch(Throwable err)
				{
					itemID=0;
				}
				iding=false;
			}
			else if(iconing)
			{
				try
				{
					iconID=Integer.parseInt(new String(ch,start,length));
				}
				catch(Throwable err)
				{
					iconID=0;
				}
				iconing=false;
			}
		}
		
		public void endElement(String uri, String localName, String qName) throws org.xml.sax.SAXException
		{
			if(dataing)
			{
				if("iding".equals(qName))
				{
					iding=false;
				}
				else if("iconing".equals(qName))
				{
					iconing=false;
				}
				else if("data".equals(qName))
				{
					dataing=false;
					
					list.add(new ItemID(itemID,iconID!=0 ? iconID:itemID));
				}
			}
		}
	};
}
