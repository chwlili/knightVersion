package org.game.knight.version.packer.world.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.chw.util.PathUtil;
import org.chw.util.XmlUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.world.task.RootTask;

public class AttireTable
{
	private RootTask root;

	private HashMap<String, HashMap<String, AttireBitmap>> bitmapTable = new HashMap<String, HashMap<String, AttireBitmap>>();
	private HashMap<String, HashMap<String, Attire>> attireTable = new HashMap<String, HashMap<String, Attire>>();
	private ArrayList<Attire> allAttres=new ArrayList<Attire>();

	/**
	 * ���캯��
	 * 
	 * @param files
	 */
	public AttireTable(RootTask root)
	{
		this.root = root;
	}
	
	/**
	 * ��ȡͼ��
	 * @param url
	 * @param id
	 * @return
	 */
	public AttireBitmap getBitmap(String url,String id)
	{
		if(bitmapTable.containsKey(url))
		{
			if(bitmapTable.get(url).containsKey(id))
			{
				return bitmapTable.get(url).get(id);
			}
		}
		return null;
	}
	
	/**
	 * ��ȡ����ͼ��
	 * @return
	 */
	public AttireBitmap[] getAllBitmap()
	{
		return bitmapTable.values().toArray(new AttireBitmap[bitmapTable.size()]);
	}
	
	/**
	 * ��ȡװ��
	 * @param url
	 * @param id
	 * @return
	 */
	public Attire getAttire(String url,String id)
	{
		if(attireTable.containsKey(url))
		{
			if(attireTable.get(url).containsKey(id))
			{
				return attireTable.get(url).get(id);
			}
		}
		return null;
	}
	
	/**
	 * ��ȡ����װ��
	 * @return
	 */
	public Attire[] getAllAttire()
	{
		return allAttres.toArray(new Attire[allAttres.size()]);
	}

	/**
	 * ���ͼ��
	 * 
	 * @param file
	 * @param bitmap
	 */
	private void addBitmap(ProjectFile file, AttireBitmap bitmap)
	{
		String url = file.url;
		if(!bitmapTable.containsKey(url))
		{
			bitmapTable.put(url, new HashMap<String, AttireBitmap>());
		}
		bitmapTable.get(url).put(bitmap.id, bitmap);
	}

	/**
	 * ���װ��
	 * 
	 * @param file
	 * @param attire
	 */
	private void addAttire(ProjectFile file, Attire attire)
	{
		String url=file.url;
		if(!attireTable.containsKey(url))
		{
			attireTable.put(url, new HashMap<String,Attire>());
		}
		attireTable.get(url).put(attire.key, attire);
		allAttres.add(attire);
	}
	
	/**
	 * ����
	 */
	public void start()
	{
		ProjectFile[] files=root.getFileTable().getAttireFiles();
		for(ProjectFile file:files)
		{
			if(root.isCancel())
			{
				return;
			}
			
			open(file);
		}
	}
	
	/**
	 * ��
	 * 
	 * @param exporter
	 */
	private void open(ProjectFile file)
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

		if (document == null)
		{
			return;
		}
		if (document != null)
		{
			rebuildImgs(file, document);
			rebuildAnims(file, document);
			rebuildAttires(file, document);
		}
	}

	/**
	 * ����ͼ��
	 * 
	 * @param dom
	 */
	private void rebuildImgs(ProjectFile file, Document dom)
	{
		@SuppressWarnings({ "rawtypes" })
		List list = dom.selectNodes("resourceData/bitmaps/bitmap");

		for (int i = 0; i < list.size(); i++)
		{
			Element node = (Element) list.get(i);

			// ��ȡͼ����Ϣ
			String key = XmlUtil.parseString(node.attributeValue("name"), "");
			String url = PathUtil.rebuildPath(XmlUtil.parseString(node.attributeValue("path"), ""));
			String paramID = XmlUtil.parseString(node.attributeValue("groupID"), "");

			// ���ͼ������
			ProjectImgFile imgFile = root.getFileTable().getImgFile(url);
			AtfParam param = root.getAtfParamTable().getAtfParam(paramID);
			if (imgFile != null)
			{
				addBitmap(file, new AttireBitmap(key, imgFile, param));
			}
			else
			{
				GamePacker.warning("��Ч��ͼ�����ã�", "�ļ���" + file.url + "  ͼ�����ƣ�" + key + " , ͼ��·��:" + url);
			}
		}
	}

	/**
	 * ���Ҷ���
	 * 
	 * @param document
	 */
	private void rebuildAnims(ProjectFile file, Document dom)
	{
		@SuppressWarnings({ "rawtypes" })
		List list = dom.selectNodes("resourceData/filmBitmaps/filmBitmap");

		for (int i = 0; i < list.size(); i++)
		{
			Element node = (Element) list.get(i);

			// ��ȡ������Ϣ
			String attireID = XmlUtil.parseString(node.attributeValue("name"), "");
			String delays = XmlUtil.parseString(node.attributeValue("frameDelays"), "");
			int row = Integer.parseInt(node.attributeValue("rowCount"));
			int col = Integer.parseInt(node.attributeValue("colCount"));
			String imgURL = PathUtil.rebuildPath(XmlUtil.parseString(node.attributeValue("path"), ""));
			String paramID = XmlUtil.parseString(node.attributeValue("groupID"), "");

			if (attireID == null || attireID.isEmpty())
			{
				attireID = "blank_" + file.gid + "_" + i;
			}

			// ���ͼ������
			ProjectImgFile img = root.getFileTable().getImgFile(imgURL);
			AtfParam param = root.getAtfParamTable().getAtfParam(paramID);
			if (img != null)
			{
				AttireAnim anim = new AttireAnim(1, 3, 1, 0, 0, 1, 1, false, img, row, col, delays, param);
				AttireAction action = new AttireAction(1, new AttireHitRect(0, 0, 0, 0, 0, 0), new AttireAnim[] { anim }, new AttireAudio[] {});
				Attire attire = new Attire(file.gid, attireID, 1, new AttireHitRect(0, 0, 0, 0, 0, 0), new AttireAction[] { action });

				addAttire(file, attire);
			}
			else
			{
				GamePacker.warning("��Ч��ͼ�����ã�", "�ļ���" + file.url + "  �������ƣ�" + attireID + " , ͼ��·��:" + imgURL);
			}
		}
	}

	/**
	 * ����װ��
	 * 
	 * @param dom
	 */
	private void rebuildAttires(ProjectFile file, Document dom)
	{
		@SuppressWarnings({ "rawtypes" })
		List list = dom.selectNodes("attireData/attires/attire");

		for (int i = 0; i < list.size(); i++)
		{
			Element node = (Element) list.get(i);

			// ���ж����Ĵ������
			HashMap<Integer, AttireHitRect> actionID_rect = new HashMap<Integer, AttireHitRect>();
			@SuppressWarnings({ "rawtypes" })
			List sizeNodes = node.selectNodes("sizes/size");
			for (int j = 0; j < sizeNodes.size(); j++)
			{
				Element sizeNode = (Element) sizeNodes.get(j);

				int id = XmlUtil.parseInt(sizeNode.attributeValue("id"), 0);
				int x = XmlUtil.parseInt(sizeNode.attributeValue("x"), 0);
				int y = XmlUtil.parseInt(sizeNode.attributeValue("y"), 0);
				int width = XmlUtil.parseInt(sizeNode.attributeValue("width"), 0);
				int height = XmlUtil.parseInt(sizeNode.attributeValue("height"), 0);
				int nameX = XmlUtil.parseInt(sizeNode.attributeValue("nameX"), 0);
				int nameY = XmlUtil.parseInt(sizeNode.attributeValue("nameY"), height);

				actionID_rect.put(id, new AttireHitRect(x, y, width, height, nameX, nameY));
			}

			// װ��Ĵ������
			String attireID = node.attributeValue("name");
			int attireRectX = XmlUtil.parseInt(node.attributeValue("x"), 0);
			int attireRectY = XmlUtil.parseInt(node.attributeValue("y"), 0);
			int attireRectW = XmlUtil.parseInt(node.attributeValue("width"), 0);
			int attireRectH = XmlUtil.parseInt(node.attributeValue("height"), 0);
			int attireNameX = XmlUtil.parseInt(node.attributeValue("nameX"), 0);
			int attireNameY = XmlUtil.parseInt(node.attributeValue("nameY"), attireRectH);

			AttireHitRect attireHitRect = new AttireHitRect(attireRectX, attireRectY, attireRectW, attireRectH, attireNameX, attireNameY);

			if (attireRectX == 0 && attireRectY == 0 && attireRectW == 0 && attireRectH == 0 && attireNameX == 0 && attireNameY == 0)
			{
				int defRectActionID = actionID_rect.containsKey(1) ? 1 : 0;
				if (!actionID_rect.containsKey(defRectActionID))
				{
					int min = Integer.MAX_VALUE;
					for (Integer aid : actionID_rect.keySet())
					{
						if (aid < min)
						{
							min = aid;
						}
					}
					if (min != Integer.MAX_VALUE)
					{
						defRectActionID = min;
					}
				}

				if (actionID_rect.containsKey(defRectActionID))
				{
					attireHitRect = actionID_rect.get(defRectActionID);
				}
			}

			// ��ȡ��������Ч
			HashMap<Integer, ArrayList<AttireAnim>> actionID_anims = new HashMap<Integer, ArrayList<AttireAnim>>();
			HashMap<Integer, ArrayList<AttireAudio>> actionID_audios = new HashMap<Integer, ArrayList<AttireAudio>>();

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

					// ��ȡ��Ч��Ϣ
					String mp3Path = PathUtil.rebuildPath(XmlUtil.parseString(actionNode.attributeValue("soundPath"), ""));
					int loop = Integer.parseInt(actionNode.attributeValue("soundPlayCount"));
					float volume = (float) Integer.parseInt(actionNode.attributeValue("soundVolume")) / 1000;

					ProjectMp3File mp3 = root.getFileTable().getMp3File(mp3Path);
					if (mp3 != null)
					{
						if (!actionID_audios.containsKey(actionID))
						{
							actionID_audios.put(actionID, new ArrayList<AttireAudio>());
						}
						actionID_audios.get(actionID).add(new AttireAudio(actionID, mp3, loop, volume));
					}
					else
					{
						if (mp3Path != null && mp3Path.isEmpty() == false)
						{
							GamePacker.warning("��Ч����Ч���ã�", "�ļ���" + file.url + "  װ������:" + attireID + " , ͼ������:" + equipName + " , ����ID:" + actionID + " , ��Ч·����" + mp3Path);
						}
					}

					// ��ȡ������Ϣ
					String imgURL = PathUtil.rebuildPath(XmlUtil.parseString(actionNode.attributeValue("bitmapPath"), ""));
					String paramID = XmlUtil.parseString(actionNode.attributeValue("groupID"), "");
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

					// ������ʱ����
					if (!equipName.equals("temp"))
					{
						if (paramID == null || paramID.isEmpty())
						{
							GamePacker.warning("��IDΪ��! ��ʹ��default����������(" + file.url + "," + equipName + "," + actionID + ")");
						}

						// ���ͼ������
						ProjectImgFile img = root.getFileTable().getImgFile(imgURL);
						AtfParam param = root.getAtfParamTable().getAtfParam(paramID);
						if (img != null)
						{
							if (!actionID_anims.containsKey(actionID))
							{
								actionID_anims.put(actionID, new ArrayList<AttireAnim>());
							}
							actionID_anims.get(actionID).add(new AttireAnim(actionID, gID, lID, x, y, scaleX, scaleY, flip, img, row, col, delays, param));
						}
						else
						{
							GamePacker.warning("��Ч��ͼ�����ã�", "�ļ���" + file.url + "  װ������:" + attireID + " , ͼ������:" + equipName + " , ����ID:" + actionID + " , ͼ��·����" + imgURL);
						}
					}
				}
			}

			HashSet<Integer> ids = new HashSet<Integer>();
			for (Integer id : actionID_anims.keySet())
			{
				ids.add(id);
			}
			for (Integer id : actionID_audios.keySet())
			{
				ids.add(id);
			}
			Integer[] idArray = ids.toArray(new Integer[ids.size()]);
			Arrays.sort(idArray);

			ArrayList<AttireAction> actionList = new ArrayList<AttireAction>();
			for (int id : idArray)
			{
				AttireHitRect actionRect = actionID_rect.containsKey(id) ? actionID_rect.get(id) : attireHitRect;
				ArrayList<AttireAnim> actionAnims = actionID_anims.containsKey(id) ? actionID_anims.get(id) : new ArrayList<AttireAnim>();
				ArrayList<AttireAudio> actionAudios = actionID_audios.containsKey(id) ? actionID_audios.get(id) : new ArrayList<AttireAudio>();
				AttireAnim[] actionAnimArray = actionAnims.toArray(new AttireAnim[actionAnims.size()]);
				AttireAudio[] actionAudioArray = actionAudios.toArray(new AttireAudio[actionAudios.size()]);

				actionList.add(new AttireAction(id, actionRect, actionAnimArray, actionAudioArray));
			}

			addAttire(file, new Attire(file.gid, attireID, 0, attireHitRect, actionList.toArray(new AttireAction[actionList.size()])));
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
				if (!Character.isDigit(character))
				{
					endIndex = i;
					break;
				}
				else if (i == layerName.length() - 1)
				{
					endIndex = layerName.length();
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
				return getLayerGroupIDs(layerName.substring(beginIndex + 1), defaultValue);
			}
		}

		return defaultValue;
	}
}
