package org.game.knight.version.packer.view;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.chw.util.PathUtil;
import org.chw.util.XmlUtil;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.game.knight.version.packer.GamePacker;

public class ViewEntity
{
	private final ViewEntityWriter manager;

	public final File file;
	private Document domcument;
	private int[] preloadType;
	private String bagName;
	private Element texts;

	public final String dir;
	public final String url;
	public final boolean isCfg;
	public final boolean isSwf;

	public String lang;

	private ArrayList<ViewEntity> xmls = new ArrayList<ViewEntity>();
	private Hashtable<ViewEntity, Attribute> xmlNodes = new Hashtable<ViewEntity, Attribute>();
	private Hashtable<String, ViewEntity> imgs = new Hashtable<String, ViewEntity>();
	private Hashtable<ViewEntity, Attribute> imgNodes = new Hashtable<ViewEntity, Attribute>();
	private Hashtable<String, String> imgRefs = new Hashtable<String, String>();
	private Hashtable<String, ViewEntity> mp3s = new Hashtable<String, ViewEntity>();
	private Hashtable<ViewEntity, Attribute> mp3Nodes = new Hashtable<ViewEntity, Attribute>();
	private Hashtable<String, ViewEntity> swfs = new Hashtable<String, ViewEntity>();
	private Hashtable<ViewEntity, Attribute> swfNodes = new Hashtable<ViewEntity, Attribute>();
	private Hashtable<String, ViewEntity> urls = new Hashtable<String, ViewEntity>();
	private Hashtable<ViewEntity, Attribute> urlNodes = new Hashtable<ViewEntity, Attribute>();

	private HashSet<ViewEntity> mergeredXmlDef = new HashSet<ViewEntity>();
	private Hashtable<String, ViewEntity> mergeredImgDef = new Hashtable<String, ViewEntity>();
	private Hashtable<String, ViewEntity> mergeredMp3Def = new Hashtable<String, ViewEntity>();
	private Hashtable<String, ViewEntity> mergeredSwfDef = new Hashtable<String, ViewEntity>();
	private Hashtable<String, ViewEntity> mergeredUrlDef = new Hashtable<String, ViewEntity>();

	private HashSet<String> crossRefs = new HashSet<String>();
	private String groupName = null;

	private String contentMD5;
	private String typeName;

	private ViewEntityBag outputBag;
	private ViewEntityGroup outputGroup;

	private int fileSize = 0;
	private String[] fileUrls;

	/**
	 * ��
	 * 
	 * @param file
	 */
	public ViewEntity(ViewEntityWriter manager, File file, String dir, String url)
	{
		this.manager = manager;
		this.dir = dir;
		this.url = url;
		this.isCfg = url.toLowerCase().endsWith(".xml");
		this.isSwf = url.toLowerCase().endsWith(".swf");

		this.file = file;
	}

	/**
	 * ��ȡ������
	 * 
	 * @return
	 */
	public String getBagName()
	{
		return bagName;
	}

	/**
	 * Ԥ�ط���
	 * 
	 * @return
	 */
	public int[] getPreloadMethod()
	{
		return preloadType;
	}

	/**
	 * ��ȡ���ֽڵ�
	 * 
	 * @return
	 */
	public Element getTextsNode()
	{
		return texts;
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// ���ļ�,����ʵ������
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * ��
	 * 
	 * @throws Exception
	 */
	public boolean open(String lang) throws Exception
	{
		close();

		this.lang = lang;

		if (isCfg)
		{
			// ����DOM
			domcument = (new SAXReader()).read(file);
			if (domcument == null)
			{
				throw new Exception("��ͼ����ʧ��:" + url);
			}

			// ��ͼ������
			bagName = domcument.getRootElement().attributeValue("name");
			if (bagName == null)
			{
				bagName = "";
			}

			// Ԥ�س���
			preloadType = XmlUtil.parseInts(domcument.getRootElement().attributeValue("preload"), -1);

			// ������Դ����
			findXmls(domcument);
			findImgs(domcument);
			findMp3s(domcument);
			findSwfs(domcument);
			findURLs(domcument);

			// �Ͽ����ֽڵ�
			texts = (Element) domcument.getRootElement().selectSingleNode("texts");
			if (texts != null)
			{
				texts.detach();
			}
		}

		return true;
	}

	/**
	 * �ر�
	 */
	private void close()
	{
		xmls = new ArrayList<ViewEntity>();
		xmlNodes = new Hashtable<ViewEntity, Attribute>();
		imgs = new Hashtable<String, ViewEntity>();
		imgNodes = new Hashtable<ViewEntity, Attribute>();
		imgRefs = new Hashtable<String, String>();
		mp3s = new Hashtable<String, ViewEntity>();
		mp3Nodes = new Hashtable<ViewEntity, Attribute>();
		swfs = new Hashtable<String, ViewEntity>();
		swfNodes = new Hashtable<ViewEntity, Attribute>();
		urls = new Hashtable<String, ViewEntity>();
		urlNodes = new Hashtable<ViewEntity, Attribute>();

		mergeredXmlDef = new HashSet<ViewEntity>();
		mergeredImgDef = new Hashtable<String, ViewEntity>();
		mergeredMp3Def = new Hashtable<String, ViewEntity>();
		mergeredSwfDef = new Hashtable<String, ViewEntity>();
		mergeredUrlDef = new Hashtable<String, ViewEntity>();

		crossRefs = new HashSet<String>();
		groupName = null;

		contentMD5 = null;
		typeName = null;

		outputBag = null;
		outputGroup = null;

		fileSize = 0;
		fileUrls = null;
	}

	/**
	 * ��������
	 * 
	 * @param dom
	 */
	private void findXmls(Document dom)
	{
		@SuppressWarnings("rawtypes")
		List list = dom.getRootElement().selectNodes("depends/depend");

		for (int i = 0; i < list.size(); i++)
		{
			Element node = (Element) list.get(i);

			Attribute att = node.attribute("src");
			String src = att.getStringValue();

			if (src != null)
			{
				ViewEntity file = manager.getView(lang, PathUtil.getAbsPath(dir, src));
				if (file == null)
				{
					GamePacker.warning("��ͼ������Ч��  " + node.asXML(), url);
					continue;
				}

				xmls.add(file);
				xmlNodes.put(file, att);
			}
		}
	}

	/**
	 * ����ͼ��
	 * 
	 * @param dom
	 */
	private void findImgs(Document dom)
	{
		@SuppressWarnings("rawtypes")
		List list = dom.getRootElement().selectNodes("bitmaps/bitmap");

		for (int i = 0; i < list.size(); i++)
		{
			Element node = (Element) list.get(i);

			String id = node.attributeValue("id");
			Attribute att = node.attribute("src");

			if (id != null && att != null)
			{
				String ref = att.getStringValue();

				ViewEntity file = manager.getView(lang, PathUtil.getAbsPath(dir, ref));
				if (file == null)
				{
					GamePacker.warning("IMG��Դ��Ч��  " + node.asXML(), url);
					continue;
				}

				imgs.put(id, file);
				imgNodes.put(file, att);
			}

			Attribute dependID = node.attribute("dependId");
			if (dependID != null)
			{
				String dependIDRef = dependID.getStringValue();

				imgRefs.put(id, dependIDRef);
			}
		}
	}

	/**
	 * ������Ч
	 * 
	 * @param dom
	 */
	private void findMp3s(Document dom)
	{
		@SuppressWarnings("rawtypes")
		List list = dom.getRootElement().selectNodes("sounds/sound");

		for (int i = 0; i < list.size(); i++)
		{
			Element node = (Element) list.get(i);

			String id = node.attributeValue("id");
			Attribute att = node.attribute("src");
			String ref = att.getStringValue();

			if (id != null && ref != null)
			{
				ViewEntity file = manager.getView(lang, PathUtil.getAbsPath(dir, ref));
				if (file == null)
				{
					GamePacker.warning("MP3��Դ��Ч��  " + node.asXML(), url);
					continue;
				}

				mp3s.put(id, file);
				mp3Nodes.put(file, att);
			}
		}
	}

	/**
	 * ����SWF
	 * 
	 * @param dom
	 */
	private void findSwfs(Document dom)
	{
		@SuppressWarnings("rawtypes")
		List list = dom.getRootElement().selectNodes("swfs/swf");

		for (int i = 0; i < list.size(); i++)
		{
			Element node = (Element) list.get(i);

			String id = node.attributeValue("id");
			Attribute att = node.attribute("src");
			String ref = att.getStringValue();

			if (id != null && ref != null)
			{
				ViewEntity file = manager.getView(lang, PathUtil.getAbsPath(dir, ref));
				if (file == null)
				{
					GamePacker.warning("SWF��Դ��Ч��  " + node.asXML(), url);
					continue;
				}

				swfs.put(id, file);
				swfNodes.put(file, att);
			}
		}
	}

	/**
	 * ����URL
	 * 
	 * @param dom
	 */
	private void findURLs(Document dom)
	{
		@SuppressWarnings("rawtypes")
		List list = dom.getRootElement().selectNodes("urls/url");

		for (int i = 0; i < list.size(); i++)
		{
			Element node = (Element) list.get(i);

			String id = node.attributeValue("id");
			Attribute att = node.attribute("src");
			String ref = att.getStringValue();

			if (id != null && ref != null)
			{
				ViewEntity file = manager.getView(lang, PathUtil.getAbsPath(dir, ref));
				if (file == null)
				{
					GamePacker.warning("URL��Դ��Ч��  " + node.asXML(), url);
					continue;
				}

				urls.put(id, file);
				urlNodes.put(file, att);
			}
		}
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// �ϲ��̳е��ⲿ���ã���ǽ�������
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * �ϲ��̳е��ⲿ���ã���ǽ�������
	 * 
	 * @param manager
	 */
	public void mergerInheritRef()
	{
		mergeredXmlDef = new HashSet<ViewEntity>();
		mergeredImgDef = new Hashtable<String, ViewEntity>();
		mergeredMp3Def = new Hashtable<String, ViewEntity>();
		mergeredSwfDef = new Hashtable<String, ViewEntity>();
		mergeredUrlDef = new Hashtable<String, ViewEntity>();

		if (isCfg)
		{
			mergerViewEntity(this, new HashSet<ViewEntity>());
		}
	}

	/**
	 * �ϲ��̳е�����
	 * 
	 * @param file
	 * @param manager
	 * @param findedXml
	 */
	private void mergerViewEntity(ViewEntity file, HashSet<ViewEntity> findedXml)
	{
		findedXml.add(file);

		// ������������
		for (ViewEntity xmlFile : file.xmls)
		{
			mergeredXmlDef.add(xmlFile);
		}

		// ����ͼ������
		Iterator<String> imgKeys = file.imgs.keySet().iterator();
		while (imgKeys.hasNext())
		{
			String key = imgKeys.next();
			if (!mergeredImgDef.containsKey(key))
			{
				mergeredImgDef.put(key, file.imgs.get(key));
			}
		}

		// ������Ч����
		Iterator<String> mp3Keys = file.mp3s.keySet().iterator();
		while (mp3Keys.hasNext())
		{
			String key = mp3Keys.next();
			if (!mergeredMp3Def.containsKey(key))
			{
				mergeredMp3Def.put(key, file.mp3s.get(key));
			}
		}

		// ���涯������
		Iterator<String> swfKeys = file.swfs.keySet().iterator();
		while (swfKeys.hasNext())
		{
			String key = swfKeys.next();
			if (!mergeredSwfDef.containsKey(key))
			{
				mergeredSwfDef.put(key, file.swfs.get(key));
			}
		}

		// �����ַ����
		Iterator<String> urlKeys = file.urls.keySet().iterator();
		while (urlKeys.hasNext())
		{
			String key = urlKeys.next();
			if (!mergeredUrlDef.containsKey(key))
			{
				mergeredUrlDef.put(key, file.urls.get(key));
			}
		}

		// �ݹ�
		for (int i = file.xmls.size() - 1; i >= 0; i--)
		{
			// ���Դ���ĵ���
			ViewEntity importedFile = file.xmls.get(i);

			// �����ظ��Ĵ���
			if (!findedXml.contains(importedFile))
			{
				mergerViewEntity(importedFile, findedXml);
			}
		}
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// ���㽻������
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * ���㽻������
	 */
	public void measureCrossRef()
	{
		// ����
		if (isSwf)
		{
			crossRefs.add(manager.getFileMD5(file));
		}

		if (isCfg)
		{
			crossRefs.add(manager.getFileMD5(file));

			// ����
			if (mergeredXmlDef.size() > 0)
			{
				Iterator<ViewEntity> xmls = mergeredXmlDef.iterator();
				while (xmls.hasNext())
				{
					xmls.next().crossRefs.add(manager.getFileMD5(file));
				}
			}

			// ͼ��
			if (mergeredImgDef.size() > 0)
			{
				Iterator<ViewEntity> imgs = mergeredImgDef.values().iterator();
				while (imgs.hasNext())
				{
					imgs.next().crossRefs.add(manager.getFileMD5(file));
				}
			}

			// ��Ч
			if (mergeredMp3Def.size() > 0)
			{
				Iterator<ViewEntity> mp3s = mergeredMp3Def.values().iterator();
				while (mp3s.hasNext())
				{
					mp3s.next().crossRefs.add(manager.getFileMD5(file));
				}
			}

			// ����
			if (mergeredSwfDef.size() > 0)
			{
				Iterator<ViewEntity> swfs = mergeredSwfDef.values().iterator();
				while (swfs.hasNext())
				{
					swfs.next().crossRefs.add(manager.getFileMD5(file));
				}
			}
		}
	}

	/**
	 * ���ӵ��ļ���
	 * 
	 * @param manager
	 */
	public String getGroupName()
	{
		if (isSwf)
		{
			if (crossRefs.size() > 0)
			{
				groupName = manager.getFileMD5(file);
			}
		}
		else
		{
			if (crossRefs.size() > 0)
			{
				String[] urls = new String[crossRefs.size()];
				urls = crossRefs.toArray(urls);
				Arrays.sort(urls);

				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < urls.length; i++)
				{
					if (sb.length() > 0)
					{
						sb.append("+");
					}
					sb.append(urls[i]);
				}

				groupName = sb.toString();
			}
		}
		return groupName;
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// ȷ������ID
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * ��ȡ����MD5
	 * 
	 * @return
	 */
	public String getContentMD5()
	{
		if (contentMD5 == null)
		{
			HashSet<String> table = new HashSet<String>();
			table.add(manager.getFileMD5(file));
			for (ViewEntity entity : mergeredXmlDef)
			{
				table.add(manager.getFileMD5(entity.file));
			}
			for (ViewEntity entity : mergeredImgDef.values())
			{
				table.add(manager.getFileMD5(entity.file));
			}
			for (ViewEntity entity : mergeredMp3Def.values())
			{
				table.add(manager.getFileMD5(entity.file));
			}
			for (ViewEntity entity : mergeredSwfDef.values())
			{
				table.add(manager.getFileMD5(entity.file));
			}
			for (ViewEntity entity : mergeredUrlDef.values())
			{
				table.add(manager.getFileMD5(entity.file));
			}
			String[] ids = table.toArray(new String[] {});
			Arrays.sort(ids);

			StringBuilder sb = new StringBuilder();
			for (String id : ids)
			{
				if (sb.length() > 0)
				{
					sb.append("+");
				}
				sb.append(id);
			}

			contentMD5 = sb.toString();
		}
		return contentMD5;
	}

	/**
	 * ��ȡ�������
	 * 
	 * @return
	 */
	public String getOutputTypeName()
	{
		if (typeName == null)
		{
			typeName = manager.getFileOutputTypeName(getContentMD5());
		}
		return typeName;
	}

	/**
	 * ��ȡ�����
	 * 
	 * @return
	 */
	public ViewEntityBag getOutputBag()
	{
		return outputBag;
	}

	/**
	 * ���������
	 * 
	 * @param bag
	 */
	public void setOutputBag(ViewEntityBag bag)
	{
		this.outputBag = bag;
	}

	/**
	 * ��ȡ�����
	 * 
	 * @return
	 */
	public ViewEntityGroup getOutputGroup()
	{
		return outputGroup;
	}

	/**
	 * ���������
	 * 
	 * @param group
	 */
	public void setOutputGroup(ViewEntityGroup group)
	{
		this.outputGroup = group;
	}

	/**
	 * ��ȡ�������
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public byte[] getOutputBytes() throws UnsupportedEncodingException
	{
		// �ؽ�����
		Element dependNode = (Element) domcument.getRootElement().selectSingleNode("depends");
		if (dependNode != null)
		{
			dependNode.detach();
		}

		if (mergeredXmlDef.size() > 0)
		{
			dependNode = domcument.getRootElement().addElement("depends");

			ViewEntity[] xmls = mergeredXmlDef.toArray(new ViewEntity[] {});
			Arrays.sort(xmls, new Comparator<ViewEntity>()
			{
				@Override
				public int compare(ViewEntity o1, ViewEntity o2)
				{
					return o1.url.compareTo(o2.url);
				}
			});

			for (ViewEntity entity : xmls)
			{
				Element childNode = dependNode.addElement("depend");
				childNode.addAttribute("src", entity.url);
			}
		}

		// �ؽ�ͼ��
		Element bitmapNode = (Element) domcument.getRootElement().selectSingleNode("bitmaps");
		if (bitmapNode != null)
		{
			bitmapNode.detach();
		}

		if (mergeredImgDef.size() > 0)
		{
			bitmapNode = domcument.getRootElement().addElement("bitmaps");

			String[] refs = mergeredImgDef.keySet().toArray(new String[] {});
			Arrays.sort(refs);

			for (String key : refs)
			{
				ViewEntity val = mergeredImgDef.get(key);

				Element childNode = bitmapNode.addElement("bitmap");
				childNode.addAttribute("id", key);
				childNode.addAttribute("type", val.getOutputTypeName());
			}
		}
		if (imgRefs.size() > 0)
		{
			for (String key : imgRefs.keySet())
			{
				Element childNode = bitmapNode.addElement("bitmap");
				childNode.addAttribute("id", key);
				childNode.addAttribute("dependId", imgRefs.get(key));
			}
		}

		// �ؽ���Ч
		Element soundNode = (Element) domcument.getRootElement().selectSingleNode("sounds");
		if (soundNode != null)
		{
			soundNode.detach();
		}

		if (mergeredMp3Def.size() > 0)
		{
			soundNode = domcument.getRootElement().addElement("sounds");

			String[] refs = mergeredMp3Def.keySet().toArray(new String[] {});
			Arrays.sort(refs);

			for (String key : refs)
			{
				ViewEntity val = mergeredMp3Def.get(key);

				Element childNode = soundNode.addElement("sound");
				childNode.addAttribute("id", key);
				childNode.addAttribute("type", val.getOutputTypeName());
			}
		}

		// �ؽ�����
		Element swfNode = (Element) domcument.getRootElement().selectSingleNode("swfs");
		if (swfNode != null)
		{
			swfNode.detach();
		}

		if (mergeredSwfDef.size() > 0)
		{
			swfNode = domcument.getRootElement().addElement("swfs");

			String[] refs = mergeredSwfDef.keySet().toArray(new String[] {});
			Arrays.sort(refs);

			for (String key : refs)
			{
				ViewEntity val = mergeredSwfDef.get(key);

				Element childNode = swfNode.addElement("swf");
				childNode.addAttribute("id", key);
				childNode.addAttribute("src", "/" + manager.getViewOutputFolder().getName() + val.getOutputBag().getOutputPath());
			}
		}

		// �ؽ���ַ
		Element urlNode = (Element) domcument.getRootElement().selectSingleNode("urls");
		if (urlNode != null)
		{
			urlNode.detach();
		}

		return XmlUtil.formatXML(domcument.asXML()).getBytes("UTF-8");
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// ��ȡ�������б�
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * ��ȡ������ص��ļ��ĵ�ַ�б�
	 * 
	 * @return
	 */
	public String[] getNeedLoadFileUrls()
	{
		measureNeedLoadFiles();
		return fileUrls;
	}

	/**
	 * ��ȡ������ص��ļ��Ĵ�С
	 * 
	 * @return
	 */
	public int getNeedLoadFileSize()
	{
		measureNeedLoadFiles();
		return fileSize;
	}

	/**
	 * ���������ص��ļ�
	 */
	private void measureNeedLoadFiles()
	{
		if (fileUrls != null)
		{
			return;
		}
		ArrayList<ViewEntityGroup> groups = new ArrayList<ViewEntityGroup>();
		groups.add(getOutputGroup());
		for (ViewEntity entity : mergeredXmlDef)
		{
			groups.add(entity.getOutputGroup());
		}
		for (ViewEntity entity : mergeredImgDef.values())
		{
			groups.add(entity.getOutputGroup());
		}
		for (ViewEntity entity : mergeredMp3Def.values())
		{
			groups.add(entity.getOutputGroup());
		}
		for (ViewEntity entity : mergeredSwfDef.values())
		{
			groups.add(entity.getOutputGroup());
		}
		// for(ViewEntity entity:mergeredUrlDef.values())
		// {
		// groups.add(entity.getOutputGroup());
		// }

		int size = 0;
		HashSet<String> urlSet = new HashSet<String>();
		for (ViewEntityGroup group : groups)
		{
			for (ViewEntityBag bag : group.getBags())
			{
				String url = bag.getOutputPath();
				File file = new File(manager.getViewOutputFolder().getPath() + url);

				String rootURL = "/" + manager.getViewOutputFolder().getName() + url;
				if (!urlSet.contains(rootURL))
				{
					size += file.length();
					urlSet.add(rootURL);
				}
			}
		}

		fileSize = size;
		fileUrls = urlSet.toArray(new String[] {});

		Arrays.sort(fileUrls);
	}
}
