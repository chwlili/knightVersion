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
	 * ���캯��
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
	 * ��ȡ�ļ�ID
	 * 
	 * @return
	 */
	public String getFileID()
	{
		return fileID;
	}

	/**
	 * ��ȡ�ڲ�·��
	 * 
	 * @return
	 */
	public String getInnerPath()
	{
		return innerPath;
	}

	/**
	 * ��ȡͼ���ļ�
	 * 
	 * @param url
	 * @return
	 */
	public ImgFile getImg(String key)
	{
		return imgRefs.get(key);
	}

	/**
	 * ��ȡװ��
	 * 
	 * @param key
	 * @return
	 */
	public Attire getAttire(String key)
	{
		return attireRefs.get(key);
	}

	/**
	 * ��ȡͼ����ID
	 * @param file
	 * @return
	 */
	public String getImgGroupID(ImgFile file)
	{
		return img_groupID.get(file);
	}
	
	/**
	 * ��ȡATF����
	 * @return
	 */
	public Hashtable<String,String> getAtfParams()
	{
		return group_param;
	}
	
	/**
	 * ��ȡ����ͼ��
	 * 
	 * @return
	 */
	public Collection<ImgFile> getAllImgs()
	{
		return imgRefs.values();
	}

	/**
	 * ��ȡ����װ��
	 * 
	 * @return
	 */
	public Collection<Attire> getAllAttires()
	{
		return attireRefs.values();
	}

	/**
	 * ��
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
	 * ���ҷ���
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

			// ��ȡ����Ϣ
			String id = XmlUtil.parseString(node.attributeValue("id"), "");
			String param = XmlUtil.parseString(node.attributeValue("param"), "");

			group_param.put(id, param);
		}
	}

	/**
	 * ����ͼ��
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

			// ��ȡͼ����Ϣ
			String key = node.attributeValue("name");
			String imgPath = node.attributeValue("path");
			String groupID = XmlUtil.parseString(node.attributeValue("groupID"), "");

			// ���ͼ������
			String url = PathUtil.getAbsPath(innerDirPath, PathUtil.rebuildPath(imgPath));
			ImgFile img = exporter.getImgFile(url);

			if (img != null)
			{
				imgRefs.put(key, img);
				img_groupID.put(img, groupID);
			}
			else
			{
				GamePacker.warning("��Ч��ͼ�����ã�", "�ļ���" + getInnerPath() + "  ͼ�����ƣ�" + key + " , ͼ��·��:" + imgPath);
			}
		}
	}

	/**
	 * ���Ҷ���
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

			// ��ȡ������Ϣ
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

			// ���ͼ������
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
				GamePacker.warning("��Ч��ͼ�����ã�", "�ļ���" + getInnerPath() + "  �������ƣ�" + attireID + " , ͼ��·��:" + imgPath);
			}
		}
	}

	/**
	 * ����װ��
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

			// ��ȡװ����Ϣ
			String attireID = node.attributeValue("name");
			int attireRectX = XmlUtil.parseInt(node.attributeValue("x"), 0);
			int attireRectY = XmlUtil.parseInt(node.attributeValue("y"), 0);
			int attireRectW = XmlUtil.parseInt(node.attributeValue("width"), 0);
			int attireRectH = XmlUtil.parseInt(node.attributeValue("height"), 0);
			int attireNameX = XmlUtil.parseInt(node.attributeValue("nameX"), 0);
			int attireNameY = XmlUtil.parseInt(node.attributeValue("nameY"), attireRectH);
			HitRect attireHitRect = new HitRect(attireRectX, attireRectY, attireRectW, attireRectH);
			
			// ѹ����
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
					throw new Error("��ID����Ϊ��");
				}

				groupParams.put(groupID, groupParam);
			}

			// ����װ��
			Attire attire = new Attire(fileID, attireID, 0, attireHitRect, attireNameX, attireNameY, groupParams);

			// ���װ��
			attireRefs.put(attireID, attire);

			// �������
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

			// ����װ���ڵ�
			@SuppressWarnings({ "rawtypes" })
			List equipNodes = node.selectNodes("equip");
			for (int j = 0; j < equipNodes.size(); j++)
			{
				Element equipNode = (Element) equipNodes.get(j);

				// ��ȡװ����Ϣ
				String equipName = equipNode.attributeValue("label");

				// ���������ڵ�
				@SuppressWarnings({ "rawtypes" })
				List actionNodes = equipNode.selectNodes("action");
				for (int k = 0; k < actionNodes.size(); k++)
				{
					Element actionNode = (Element) actionNodes.get(k);

					// ����ID
					int actionID = Integer.parseInt(actionNode.attributeValue("id"));

					// ��ȡ������Ϣ
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

					// ������ʱ����
					if (!equipName.equals("temp"))
					{
						if (bagID == null || bagID.isEmpty())
						{
							GamePacker.warning("��IDΪ��! ��ʹ��default����������(" + getInnerPath() + "," + equipName + "," + actionID + ")");
						}
						else if (!groupParams.containsKey(bagID))
						{
							//GamePacker.error(bagID + "��IDûδ�ҵ��� ������Ĭ����ʹ��Ĭ�ϲ��������(" + getInnerPath() + "," + equipName + "," + actionID + ")");
							//bagID = "";
						}

						// ���ͼ������
						String absImgPath = PathUtil.rebuildPath(imgPath);
						ImgFile img = exporter.getImgFile(absImgPath);

						if (img != null)
						{
							// ���������б�
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
							GamePacker.warning("��Ч��ͼ�����ã�", "�ļ���" + getInnerPath() + "  װ������:" + attireID + " , ͼ������:" + equipName + " , ����ID:" + actionID + " , ͼ��·����" + imgPath);
						}
					}

					// ��ȡ��Ч��Ϣ
					String mp3Path = actionNode.attributeValue("soundPath");
					int loop = Integer.parseInt(actionNode.attributeValue("soundPlayCount"));
					float volume = (float) Integer.parseInt(actionNode.attributeValue("soundVolume")) / 1000;

					// �����Ч����
					String absMp3Path = PathUtil.rebuildPath(mp3Path);
					Mp3File mp3 = exporter.getMp3File(absMp3Path);

					if (mp3 != null)
					{
						// ���������б�
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
							GamePacker.warning("��Ч����Ч���ã�", "�ļ���" + getInnerPath() + "  װ������:" + attireID + " , ͼ������:" + equipName + " , ����ID:" + actionID + " , ��Ч·����" + mp3Path);
						}
					}
				}
			}
		}
	}

	/**
	 * ��ȡ�����ID
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
	 * ��ȡ��Ĳ�ID
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
