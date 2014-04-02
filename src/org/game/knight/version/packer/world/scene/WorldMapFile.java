package org.game.knight.version.packer.world.scene;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.chw.util.PathUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.game.knight.version.packer.world.ImgFile;


public class WorldMapFile
{

	private Hashtable<String, String> resPathTable = new Hashtable<String, String>();

	// ----------------------------------------------------------------------------------------------------------
	//
	// 构建地图
	//
	// ----------------------------------------------------------------------------------------------------------
	
	/**
	 * 生成地图
	 */
	private void buildMap(File file,Element root)
	{
		Document document = null;

		try
		{
			SAXReader reader = new SAXReader();
			document = reader.read(file);
		}
		catch (DocumentException e)
		{
			//world.writeError("地图文件解析失败!\t"+e.getMessage());
		}

		if (document != null)
		{
			buildResBags(document);
			
			long imgSize=0;
			HashSet<ImgFile> imgs=new HashSet<ImgFile>();
			ArrayList<ImgFile> imgArray=new ArrayList<ImgFile>();
	
			Element mapDB = root.addElement("map");
			
			//地图基本属性
			Element mapNode=(Element) document.selectSingleNode("mapData/back");
			mapDB.addAttribute("width", mapNode.attributeValue("width"));
			mapDB.addAttribute("height", mapNode.attributeValue("height"));
			
			//地图背景
			String path=mapNode.attributeValue("path");
			if(hasImgRes(path))
			{
				ImgFile img=getImgRes(path);
				if(!imgs.contains(img))
				{
				//	imgSize+=img.getSize();
					imgs.add(img);
					imgArray.add(img);
				}
				//mapDB.addAttribute("back", world.getAliveFilePath(img));
			}
			else
			{
				//world.writeWarning("地图背图像无效！"+path);
			}
			
			Element citysNode=mapDB.addElement("citys");
			
			//地图城市图标
			@SuppressWarnings({"rawtypes" })
			List list=document.selectNodes("mapData/cityIcons/cityIcon");
			for(int i=0;i<list.size();i++)
			{
				Element node=(Element) list.get(i);
				
				Element cityNode=citysNode.addElement("city");
				cityNode.addAttribute("id", node.attributeValue("cityPath"));
				cityNode.addAttribute("name", node.attributeValue("cityName"));
				cityNode.addAttribute("x", node.attributeValue("cityX"));
				cityNode.addAttribute("y", node.attributeValue("cityY"));
				
				String cityIconPath=node.attributeValue("cityIcon");
				if(hasImgRes(cityIconPath))
				{
					ImgFile cityIcon=getImgRes(cityIconPath);
					if(!imgs.contains(cityIcon))
					{
						//imgSize+=cityIcon.getSize();
						imgs.add(cityIcon);
						imgArray.add(cityIcon);
					}
					//cityNode.addAttribute("icon", world.getAliveFilePath(cityIcon));
				}
			}
			
			//保存图像资源大小
			mapDB.addAttribute("imgSize", imgSize+"");
			
			//图像引用表
			Element imgsNode=mapDB.addElement("imgs");
			for(int i=0;i<imgArray.size();i++)
			{
				ImgFile img=imgArray.get(i);
				
				Element imgNode=imgsNode.addElement("img");
				//imgNode.addAttribute("path", world.getAliveFilePath(img));
				//imgNode.addAttribute("size", img.getSize()+"");
			}
		}
	}
	
	
	/**
	 * 生成场景资源表
	 * 
	 * @param document
	 */
	private void buildResBags(Document document)
	{
		@SuppressWarnings({ "rawtypes" })
		List list = document.selectNodes("mapData/resources/resource");

		for (int i = 0; i < list.size(); i++)
		{
			Element node = (Element) list.get(i);

			String key = node.attributeValue("key");
			String path = PathUtil.rebuildPath(node.attributeValue("path"));

			//if (world.getAttireTable().hasFile(path))
			//{
			//	resPathTable.put(key, path);
			//}
			//else
			//{
				// appendError("无效的资源包引用("+path+")");
			//}
		}
	}

	/**
	 * 检查图像资源
	 * 
	 * @param ref
	 * @return
	 */
	private boolean hasImgRes(String ref)
	{
		String absRef = PathUtil.rebuildPath(ref);
		String[] parts = absRef.split("/");

		String path = parts[0];
		// String type = parts[1];
		String name = parts[2];

		if (resPathTable.containsKey(path))
		{
			path = resPathTable.get(path);
			//if (world.getAttireTable().hasFile(path))
			//{
			//	return world.getAttireTable().hasImg(path, name);
			//}
		}
		return false;
	}

	/**
	 * 获取图像资源
	 * 
	 * @param ref
	 * @return
	 */
	private ImgFile getImgRes(String ref)
	{
		String absRef = PathUtil.rebuildPath(ref);
		String[] parts = absRef.split("/");

		String path = parts[0];
		// String type = parts[1];
		String name = parts[2];

		if (resPathTable.containsKey(path))
		{
			path = resPathTable.get(path);
			//if (world.getAttireTable().hasImg(path, name))
			//{
			//	ImgFile img = world.getAttireTable().getImg(path, name);
				//saveImgFile(img);
			//	return img;
			//}
		}
		return null;
	}
}
