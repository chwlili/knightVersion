package org.game.knight.version.packer.world.attire;

import java.io.File;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import org.chw.util.PathUtil;
import org.chw.util.XmlUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.world.ImgFile;
import org.game.knight.version.packer.world.Mp3File;
import org.game.knight.version.packer.world.WorldExporter;

public class AttireFile
{
	private String fileID;
	private File file;
	private String innerPath;
	private String innerDirPath;

	private Hashtable<String, String> group_param = new Hashtable<String, String>();
	private Hashtable<ImgFile, String> img_groupID = new Hashtable<ImgFile, String>();
	private Hashtable<String, ImgFile> imgRefs = new Hashtable<String, ImgFile>();
	private Hashtable<String, Attire> attireRefs = new Hashtable<String, Attire>();

	/**
	 * 构造函数
	 * 
	 * @param file
	 * @param innerPath
	 * @param innerDirPath
	 */
	public AttireFile(String fileID, File file, String innerPath, String innerDirPath)
	{
		this.fileID = fileID;
		this.file = file;
		this.innerPath = innerPath;
		this.innerDirPath = innerDirPath;
	}

	/**
	 * 获取文件ID
	 * 
	 * @return
	 */
	public String getFileID()
	{
		return fileID;
	}

	/**
	 * 获取内部路径
	 * 
	 * @return
	 */
	public String getInnerPath()
	{
		return innerPath;
	}

	/**
	 * 获取图像文件
	 * 
	 * @param url
	 * @return
	 */
	public ImgFile getImg(String key)
	{
		return imgRefs.get(key);
	}

	/**
	 * 获取装扮
	 * 
	 * @param key
	 * @return
	 */
	public Attire getAttire(String key)
	{
		return attireRefs.get(key);
	}

	/**
	 * 获取图像组ID
	 * @param file
	 * @return
	 */
	public String getImgGroupID(ImgFile file)
	{
		return img_groupID.get(file);
	}
	
	/**
	 * 获取ATF分组
	 * @return
	 */
	public Hashtable<String,String> getAtfParams()
	{
		return group_param;
	}
	
	/**
	 * 获取所有图像
	 * 
	 * @return
	 */
	public Collection<ImgFile> getAllImgs()
	{
		return imgRefs.values();
	}

	/**
	 * 获取所有装扮
	 * 
	 * @return
	 */
	public Collection<Attire> getAllAttires()
	{
		return attireRefs.values();
	}

	/**
	 * 打开
	 * 
	 * @param exporter
	 */
	public void open(WorldExporter exporter)
	{
		Document document = null;
		SAXReader reader = new SAXReader();
		try
		{
			document = reader.read(file);
		}
		catch (DocumentException e)
		{
			GamePacker.error(e);
		}

		group_param = new Hashtable<String, String>();
		imgRefs = new Hashtable<String, ImgFile>();
		img_groupID = new Hashtable<ImgFile, String>();
		attireRefs = new Hashtable<String, Attire>();

		if (document != null)
		{
			rebuildGroups(document, exporter);
			rebuildImgs(document, exporter);
			rebuildAnims(document, exporter);
			rebuildAttires(document, exporter);
		}
	}

	/**
	 * 查找分组
	 * 
	 * @param dom
	 * @param exporter
	 */
	private void rebuildGroups(Document dom, WorldExporter exporter)
	{
		@SuppressWarnings({ "rawtypes" })
		List groupNodes = dom.selectNodes("resourceData/groups/group");
		for (int i = 0; i < groupNodes.size(); i++)
		{
			Element node = (Element) groupNodes.get(i);

			// 获取组信息
			String id = XmlUtil.parseString(node.attributeValue("id"), "");
			String param = XmlUtil.parseString(node.attributeValue("param"), "");

			group_param.put(id, param);
		}
	}

	/**
	 * 查找图像
	 * 
	 * @param dom
	 */
	private void rebuildImgs(Document dom, WorldExporter exporter)
	{
		@SuppressWarnings({ "rawtypes" })
		List list = dom.selectNodes("resourceData/bitmaps/bitmap");

		for (int i = 0; i < list.size(); i++)
		{
			Element node = (Element) list.get(i);

			// 获取图像信息
			String key = node.attributeValue("name");
			String imgPath = node.attributeValue("path");
			String groupID = XmlUtil.parseString(node.attributeValue("groupID"), "");

			// 检查图像引用
			String url = PathUtil.getAbsPath(innerDirPath, PathUtil.rebuildPath(imgPath));
			ImgFile img = exporter.getImgFile(url);

			if (img != null)
			{
				imgRefs.put(key, img);
				img_groupID.put(img, groupID);
			}
			else
			{
				GamePacker.warning("无效的图像引用！", "文件：" + getInnerPath() + "  图像名称：" + key + " , 图像路径:" + imgPath);
			}
		}
	}

	/**
	 * 查找动画
	 * 
	 * @param document
	 */
	private void rebuildAnims(Document dom, WorldExporter exporter)
	{
		@SuppressWarnings({ "rawtypes" })
		List list = dom.selectNodes("resourceData/filmBitmaps/filmBitmap");

		for (int i = 0; i < list.size(); i++)
		{
			Element node = (Element) list.get(i);

			// 获取动画信息
			String attireID = node.attributeValue("name");
			String delays = node.attributeValue("frameDelays");
			int row = Integer.parseInt(node.attributeValue("rowCount"));
			int col = Integer.parseInt(node.attributeValue("colCount"));
			String imgPath = node.attributeValue("path");
			String groupID = XmlUtil.parseString(node.attributeValue("groupID"), "");

			if (attireID == null || attireID.isEmpty())
			{
				attireID = "blank_" + innerPath + "_" + i;
			}

			// 检查图像引用
			String url = PathUtil.rebuildPath(imgPath);
			ImgFile img = exporter.getImgFile(url);
			if (img != null)
			{
				if (!attireRefs.containsKey(attireID))
				{
					AttireAction action = new AttireAction(1, new HitRect(0, 0, 0, 0), 0, 0);
					action.addAnim(new AttireAnim(1, 3, 1, 0, 0, 1, 1, false, img, row, col, delays, groupID));

					Attire attire = new Attire(fileID, attireID, 1, new HitRect(0, 0, 0, 0), 0, 0, group_param);
					attire.addAction(1, action);

					attireRefs.put(attireID, attire);
				}
			}
			else
			{
				GamePacker.warning("无效的图像引用！", "文件：" + getInnerPath() + "  动画名称：" + attireID + " , 图像路径:" + imgPath);
			}
		}
	}

	/**
	 * 查找装扮
	 * 
	 * @param dom
	 */
	private void rebuildAttires(Document dom, WorldExporter exporter)
	{
		@SuppressWarnings({ "rawtypes" })
		List list = dom.selectNodes("attireData/attires/attire");

		for (int i = 0; i < list.size(); i++)
		{
			Element node = (Element) list.get(i);

			// 获取装扮信息
			String attireID = node.attributeValue("name");
			int attireRectX = XmlUtil.parseInt(node.attributeValue("x"), 0);
			int attireRectY = XmlUtil.parseInt(node.attributeValue("y"), 0);
			int attireRectW = XmlUtil.parseInt(node.attributeValue("width"), 0);
			int attireRectH = XmlUtil.parseInt(node.attributeValue("height"), 0);
			int attireNameX = XmlUtil.parseInt(node.attributeValue("nameX"), 0);
			int attireNameY = XmlUtil.parseInt(node.attributeValue("nameY"), attireRectH);
			HitRect attireHitRect = new HitRect(attireRectX, attireRectY, attireRectW, attireRectH);
			
			// 压缩组
			Hashtable<String, String> groupParams = new Hashtable<String, String>();
			@SuppressWarnings({ "rawtypes" })
			List groupNodes = node.selectNodes("groups/group");
			for (int j = 0; j < groupNodes.size(); j++)
			{
				Element groupNode = (Element) groupNodes.get(j);

				String groupID = groupNode.attributeValue("id");
				String groupParam = groupNode.attributeValue("param");

				if (groupID == null || groupID.isEmpty())
				{
					throw new Error("组ID不能为空");
				}

				groupParams.put(groupID, groupParam);
			}

			// 建立装扮
			Attire attire = new Attire(fileID, attireID, 0, attireHitRect, attireNameX, attireNameY, groupParams);

			// 添加装扮
			attireRefs.put(attireID, attire);

			// 打击矩形
			Hashtable<Integer, Integer> actionRectX = new Hashtable<Integer, Integer>();
			Hashtable<Integer, Integer> actionRectY = new Hashtable<Integer, Integer>();
			Hashtable<Integer, Integer> actionRectW = new Hashtable<Integer, Integer>();
			Hashtable<Integer, Integer> actionRectH = new Hashtable<Integer, Integer>();
			Hashtable<Integer, Integer> actionNameX = new Hashtable<Integer, Integer>();
			Hashtable<Integer, Integer> actionNameY = new Hashtable<Integer, Integer>();
			@SuppressWarnings({ "rawtypes" })
			List sizeNodes = node.selectNodes("sizes/size");
			for (int j = 0; j < sizeNodes.size(); j++)
			{
				Element sizeNode = (Element) sizeNodes.get(j);

				int id = XmlUtil.parseInt(sizeNode.attributeValue("id"), 0);
				int x = XmlUtil.parseInt(sizeNode.attributeValue("x"), 0);
				int y = XmlUtil.parseInt(sizeNode.attributeValue("y"), 0);
				int w = XmlUtil.parseInt(sizeNode.attributeValue("width"), 0);
				int h = XmlUtil.parseInt(sizeNode.attributeValue("height"), 0);
				int nameX = XmlUtil.parseInt(sizeNode.attributeValue("nameX"), 0);
				int nameY = XmlUtil.parseInt(sizeNode.attributeValue("nameY"), h);

				if (x != 0)
				{
					actionRectX.put(id, x);
				}
				if (y != 0)
				{
					actionRectY.put(id, y);
				}
				if (w != 0)
				{
					actionRectW.put(id, w);
				}
				if (h != 0)
				{
					actionRectH.put(id, h);
				}
				if (nameX != 0)
				{
					actionNameX.put(id, nameX);
				}
				if (nameY != 0)
				{
					actionNameY.put(id, nameY);
				}
			}

			// 遍历装备节点
			@SuppressWarnings({ "rawtypes" })
			List equipNodes = node.selectNodes("equip");
			for (int j = 0; j < equipNodes.size(); j++)
			{
				Element equipNode = (Element) equipNodes.get(j);

				// 获取装备信息
				String equipName = equipNode.attributeValue("label");

				// 遍历动作节点
				@SuppressWarnings({ "rawtypes" })
				List actionNodes = equipNode.selectNodes("action");
				for (int k = 0; k < actionNodes.size(); k++)
				{
					Element actionNode = (Element) actionNodes.get(k);

					// 动作ID
					int actionID = Integer.parseInt(actionNode.attributeValue("id"));

					// 获取动作信息
					String imgPath = XmlUtil.parseString(actionNode.attributeValue("bitmapPath"), "");
					int gID = getLayerGroupIDs(equipName, 3);
					int lID = getLayerLayerIDs(equipName, 1);
					int row = Integer.parseInt(actionNode.attributeValue("bitmapRow"));
					int col = Integer.parseInt(actionNode.attributeValue("bitmapCol"));
					String delays = actionNode.attributeValue("frameDelays");
					int x = XmlUtil.parseInt(actionNode.attributeValue("frameX"), 0);
					int y = XmlUtil.parseInt(actionNode.attributeValue("frameY"), 0);
					float scaleX = XmlUtil.parseFloat(actionNode.attributeValue("frameScaleX"), XmlUtil.parseFloat(actionNode.attributeValue("frameScale"), 1));
					float scaleY = XmlUtil.parseFloat(actionNode.attributeValue("frameScaleY"), scaleX);
					boolean flip = XmlUtil.parseBoolean(actionNode.attributeValue("flip"), true);
					String bagID = XmlUtil.parseString(actionNode.attributeValue("groupID"), "");

					// 过滤临时动作
					if (!equipName.equals("temp"))
					{
						if (bagID == null || bagID.isEmpty())
						{
							GamePacker.warning("组ID为空! 将使用default组参数输出！(" + getInnerPath() + "," + equipName + "," + actionID + ")");
						}
						else if (!groupParams.containsKey(bagID))
						{
							//GamePacker.error(bagID + "组ID没未找到！ 将放入默认组使用默认参数输出！(" + getInnerPath() + "," + equipName + "," + actionID + ")");
							//bagID = "";
						}

						// 检查图像引用
						String absImgPath = PathUtil.rebuildPath(imgPath);
						ImgFile img = exporter.getImgFile(absImgPath);

						if (img != null)
						{
							// 建立动画列表
							AttireAction action = attire.getAction(actionID);
							if (action == null)
							{
								int actX = attireRectX;
								int actY = attireRectY;
								int actW = attireRectW;
								int actH = attireRectH;
								int actNameX = attireNameX;
								int actNameY = attireNameY;

								if (actionRectX.containsKey(actionID))
								{
									actX = actionRectX.get(actionID);
								}
								if (actionRectY.containsKey(actionID))
								{
									actY = actionRectY.get(actionID);
								}
								if (actionRectW.containsKey(actionID))
								{
									actW = actionRectW.get(actionID);
								}
								if (actionRectH.containsKey(actionID))
								{
									actH = actionRectH.get(actionID);
								}
								if (actionNameX.containsKey(actionID))
								{
									actNameX = actionNameX.get(actionID);
								}
								if (actionNameY.containsKey(actionID))
								{
									actNameY = actionNameY.get(actionID);
								}

								action = new AttireAction(actionID, new HitRect(actX, actY, actW, actH), actNameX, actNameY);
								attire.addAction(actionID, action);
							}

							action.addAnim(new AttireAnim(actionID, gID, lID, x, y, scaleX, scaleY, flip, img, row, col, delays, bagID));
						}
						else
						{
							GamePacker.warning("无效的图像引用！", "文件：" + getInnerPath() + "  装扮名称:" + attireID + " , 图层名称:" + equipName + " , 动作ID:" + actionID + " , 图像路径：" + imgPath);
						}
					}

					// 获取音效信息
					String mp3Path = actionNode.attributeValue("soundPath");
					int loop = Integer.parseInt(actionNode.attributeValue("soundPlayCount"));
					float volume = (float) Integer.parseInt(actionNode.attributeValue("soundVolume")) / 1000;

					// 检查音效引用
					String absMp3Path = PathUtil.rebuildPath(mp3Path);
					Mp3File mp3 = exporter.getMp3File(absMp3Path);

					if (mp3 != null)
					{
						// 建立动画列表
						AttireAction action = attire.getAction(actionID);
						if (action == null)
						{
							action = new AttireAction(actionID, new HitRect(0, 0, 0, 0), 0, 0);
							attire.addAction(actionID, action);
						}
						action.addAudio(new AttireAudio(actionID, mp3, loop, volume));
					}
					else
					{
						if (mp3Path != null && mp3Path.isEmpty() == false)
						{
							GamePacker.warning("无效的音效引用！", "文件：" + getInnerPath() + "  装扮名称:" + attireID + " , 图层名称:" + equipName + " , 动作ID:" + actionID + " , 音效路径：" + mp3Path);
						}
					}
				}
			}
		}
	}

	/**
	 * 获取层的组ID
	 * 
	 * @param layerName
	 * @param defaultValue
	 * @return
	 */
	private int getLayerGroupIDs(String layerName, int defaultValue)
	{
		if (layerName != null && !layerName.isEmpty())
		{
			int endIndex = 0;
			for (int i = 0; i < layerName.length(); i++)
			{
				char character = layerName.charAt(i);
				if(!Character.isDigit(character))
				{
					endIndex = i;
					break;
				}
				else if(i==layerName.length()-1)
				{
					endIndex=layerName.length();
					break;
				}
			}

			if (endIndex > 0)
			{
				return Integer.parseInt(layerName.substring(0, endIndex));
			}
		}

		return defaultValue;
	}

	/**
	 * 获取层的层ID
	 * 
	 * @param layerName
	 * @param defaultValue
	 * @return
	 */
	private int getLayerLayerIDs(String layerName, int defaultValue)
	{
		if (layerName != null && !layerName.isEmpty())
		{
			int beginIndex = layerName.indexOf("_");
			if (beginIndex != -1)
			{
				return getLayerGroupIDs(layerName.substring(beginIndex + 1),defaultValue);
			}
		}

		return defaultValue;
	}

}
