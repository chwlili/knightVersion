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

import org.chw.util.FileUtil;
import org.chw.util.MD5Util;
import org.chw.util.PathUtil;
import org.chw.util.XmlUtil;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.game.knight.version.packer.GamePacker;

public class ViewFile
{
	private String innerDir;
	private String innerPath;
	private boolean isCfg;
	private int[] preloadType;
	private String bagName;

	private File file;
	private Document domcument;
	private Element texts;
	private byte[] content;

	private String fileKey;

	/**
	 * 打开
	 * 
	 * @param file
	 */
	public ViewFile(String innerDir, String innerPath, File file)
	{
		this.innerDir = innerDir;
		this.innerPath = innerPath;

		this.isCfg = innerPath.toLowerCase().endsWith(".xml");
		this.file = file;
	}

	public File getFile()
	{
		return this.file;
	}

	/**
	 * 是否为配置文件
	 * 
	 * @return
	 */
	public Boolean isCfg()
	{
		return isCfg;
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
	 * 获取包名称
	 * 
	 * @return
	 */
	public String getBagName()
	{
		return bagName;
	}

	/**
	 * 获取文件KEY
	 * 
	 * @return
	 */
	public String getFileKey()
	{
		return fileKey;
	}

	/**
	 * 预载方法
	 * 
	 * @return
	 */
	public int[] getPreloadMethod()
	{
		return preloadType;
	}

	/**
	 * 获取视图文件内容
	 * 
	 * @return
	 */
	public byte[] getContents()
	{
		if (content == null)
		{
			if (isCfg)
			{
				String txt = XmlUtil.formatXML(domcument.asXML());
				try
				{
					content = txt.getBytes("UTF-8");
				}
				catch (UnsupportedEncodingException e)
				{
					content = new byte[0];
					e.printStackTrace();
				}
			}
			else
			{
				content = FileUtil.getFileBytes(file);
			}
		}

		return content;
	}

	/**
	 * 获取文档
	 * 
	 * @return
	 */
	public Document getDocument()
	{
		return domcument;
	}

	/**
	 * 获取文字节点
	 * 
	 * @return
	 */
	public Element getTextsNode()
	{
		return texts;
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// 打开文件,查找实际链接
	//
	// ---------------------------------------------------------------------------------------------------------------

	private ArrayList<ViewFile> xmls = new ArrayList<ViewFile>();
	private Hashtable<ViewFile, Attribute> xmlNodes = new Hashtable<ViewFile, Attribute>();
	private Hashtable<String, ViewFile> imgs = new Hashtable<String, ViewFile>();
	private Hashtable<String, String> imgRefs = new Hashtable<String, String>();
	private Hashtable<ViewFile, Attribute> imgNodes = new Hashtable<ViewFile, Attribute>();
	private Hashtable<String, ViewFile> mp3s = new Hashtable<String, ViewFile>();
	private Hashtable<ViewFile, Attribute> mp3Nodes = new Hashtable<ViewFile, Attribute>();
	private Hashtable<String, ViewFile> swfs = new Hashtable<String, ViewFile>();
	private Hashtable<ViewFile, Attribute> swfNodes = new Hashtable<ViewFile, Attribute>();
	private Hashtable<String, ViewFile> urls = new Hashtable<String, ViewFile>();
	private Hashtable<ViewFile, Attribute> urlNodes = new Hashtable<ViewFile, Attribute>();

	/**
	 * 打开
	 */
	public void open(ViewExport manager)
	{
		if (!isCfg)
		{
			return;
		}

		try
		{
			domcument = (new SAXReader()).read(file);
		}
		catch (DocumentException e)
		{
		}

		if (domcument == null)
		{
			domcument = DocumentHelper.createDocument();
			domcument.addElement("xml");

			GamePacker.error("视图解析失败", getInnerPath());
		}

		String name = domcument.getRootElement().attributeValue("name");
		if (name != null && name.length() > 0)
		{
			bagName = name;
		}
		else
		{
			bagName = "";
		}

		preloadType = XmlUtil.parseInts(domcument.getRootElement().attributeValue("preload"), -1);

		findXmls(domcument, manager);
		findImgs(domcument, manager);
		findMp3s(domcument, manager);
		findSwfs(domcument, manager);
		findURLs(domcument, manager);

		texts = (Element) domcument.getRootElement().selectSingleNode("texts");
		if (texts != null)
		{
			texts.detach();
		}
	}

	/**
	 * 查找依赖
	 * 
	 * @param dom
	 */
	private void findXmls(Document dom, ViewExport manager)
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
				ViewFile file = manager.getViewBy(PathUtil.getAbsPath(innerDir, src));
				if (file == null)
				{
					GamePacker.warning("视图依赖无效！  " + node.asXML(), getInnerPath());
					continue;
				}

				xmls.add(file);
				xmlNodes.put(file, att);
			}
		}
	}

	/**
	 * 查找图像
	 * 
	 * @param dom
	 */
	private void findImgs(Document dom, ViewExport manager)
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
				
				ViewFile file = manager.getViewBy(PathUtil.getAbsPath(innerDir, ref));
				if (file == null)
				{
					GamePacker.warning("IMG资源无效！  " + node.asXML(), getInnerPath());
					continue;
				}

				imgs.put(id, file);
				imgNodes.put(file, att);
			}
			
			Attribute dependID=node.attribute("dependId");
			if(dependID!=null)
			{
				String dependIDRef=dependID.getStringValue();
				
				imgRefs.put(id, dependIDRef);
			}
		}
	}

	/**
	 * 查找音效
	 * 
	 * @param dom
	 */
	private void findMp3s(Document dom, ViewExport manager)
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
				ViewFile file = manager.getViewBy(PathUtil.getAbsPath(innerDir, ref));
				if (file == null)
				{
					GamePacker.warning("MP3资源无效！  " + node.asXML(), getInnerPath());
					continue;
				}

				mp3s.put(id, file);
				mp3Nodes.put(file, att);
			}
		}
	}

	/**
	 * 查找SWF
	 * 
	 * @param dom
	 */
	private void findSwfs(Document dom, ViewExport manager)
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
				ViewFile file = manager.getViewBy(PathUtil.getAbsPath(innerDir, ref));
				if (file == null)
				{
					GamePacker.warning("SWF资源无效！  " + node.asXML(), getInnerPath());
					continue;
				}

				swfs.put(id, file);
				swfNodes.put(file, att);
			}
		}
	}

	/**
	 * 查找URL
	 * 
	 * @param dom
	 */
	private void findURLs(Document dom, ViewExport manager)
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
				ViewFile file = manager.getViewBy(PathUtil.getAbsPath(innerDir, ref));
				if (file == null)
				{
					GamePacker.warning("URL资源无效！  " + node.asXML(), getInnerPath());
					continue;
				}

				urls.put(id, file);
				urlNodes.put(file, att);
			}
		}
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// 合并继承的外部引用，标记交叉引用
	//
	// ---------------------------------------------------------------------------------------------------------------

	private HashSet<ViewFile> mergeredXmlDef = new HashSet<ViewFile>();
	private Hashtable<String, ViewFile> mergeredImgDef = new Hashtable<String, ViewFile>();
	private Hashtable<String, ViewFile> mergeredMp3Def = new Hashtable<String, ViewFile>();
	private Hashtable<String, ViewFile> mergeredSwfDef = new Hashtable<String, ViewFile>();
	private Hashtable<String, ViewFile> mergeredUrlDef = new Hashtable<String, ViewFile>();

	private HashSet<String> crossRefs = new HashSet<String>();

	/**
	 * 合并继承的外部引用，标记交叉引用
	 * 
	 * @param manager
	 */
	public void mergerInheritRef(ViewExport manager)
	{
		mergeredXmlDef = new HashSet<ViewFile>();
		mergeredImgDef = new Hashtable<String, ViewFile>();
		mergeredMp3Def = new Hashtable<String, ViewFile>();
		mergeredSwfDef = new Hashtable<String, ViewFile>();
		mergeredUrlDef = new Hashtable<String, ViewFile>();

		if (isCfg)
		{
			mergerViewFile(this, manager, new HashSet<ViewFile>());
			// measureCrossRef();
		}
	}

	/**
	 * 合并继承的引用
	 * 
	 * @param file
	 * @param manager
	 * @param findedXml
	 */
	private void mergerViewFile(ViewFile file, ViewExport manager, HashSet<ViewFile> findedXml)
	{
		findedXml.add(file);

		// 保存配置引用
		for (ViewFile xmlFile : file.xmls)
		{
			mergeredXmlDef.add(xmlFile);
		}

		// 保存图像引用
		Iterator<String> imgKeys = file.imgs.keySet().iterator();
		while (imgKeys.hasNext())
		{
			String key = imgKeys.next();
			if (!mergeredImgDef.containsKey(key))
			{
				mergeredImgDef.put(key, file.imgs.get(key));
			}
		}

		// 保存音效引用
		Iterator<String> mp3Keys = file.mp3s.keySet().iterator();
		while (mp3Keys.hasNext())
		{
			String key = mp3Keys.next();
			if (!mergeredMp3Def.containsKey(key))
			{
				mergeredMp3Def.put(key, file.mp3s.get(key));
			}
		}

		// 保存动画引用
		Iterator<String> swfKeys = file.swfs.keySet().iterator();
		while (swfKeys.hasNext())
		{
			String key = swfKeys.next();
			if (!mergeredSwfDef.containsKey(key))
			{
				mergeredSwfDef.put(key, file.swfs.get(key));
			}
		}

		// 保存地址引用
		Iterator<String> urlKeys = file.urls.keySet().iterator();
		while (urlKeys.hasNext())
		{
			String key = urlKeys.next();
			if (!mergeredUrlDef.containsKey(key))
			{
				mergeredUrlDef.put(key, file.urls.get(key));
			}
		}

		// 递归
		for (int i = file.xmls.size() - 1; i >= 0; i--)
		{
			// 忽略错误的导入
			ViewFile importedFile = file.xmls.get(i);

			// 忽略重复的处理
			if (!findedXml.contains(importedFile))
			{
				mergerViewFile(importedFile, manager, findedXml);
			}
		}
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// 计算交叉引用
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * 计算交叉引用
	 */
	public void measureCrossRef()
	{
		if (isCfg)
		{
			// 自身
			crossRefs.add(innerPath);

			// 依赖
			if (mergeredXmlDef.size() > 0)
			{
				Iterator<ViewFile> xmls = mergeredXmlDef.iterator();
				while (xmls.hasNext())
				{
					xmls.next().crossRefs.add(innerPath);
				}
			}

			// 图像
			if (mergeredImgDef.size() > 0)
			{
				Iterator<ViewFile> imgs = mergeredImgDef.values().iterator();
				while (imgs.hasNext())
				{
					imgs.next().crossRefs.add(innerPath);
				}
			}

			// 音效
			if (mergeredMp3Def.size() > 0)
			{
				Iterator<ViewFile> mp3s = mergeredMp3Def.values().iterator();
				while (mp3s.hasNext())
				{
					mp3s.next().crossRefs.add(innerPath);
				}
			}

			// 动画
			// if(mergeredSwfDef.size()>0)
			// {
			// Iterator<ViewFile> swfs=mergeredSwfDef.values().iterator();
			// while(swfs.hasNext())
			// {
			// swfs.next().crossRefs.add(innerPath);
			// }
			// }
		}
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// 确定类型ID
	//
	// ---------------------------------------------------------------------------------------------------------------

	private long classID = 0;
	private ViewFileGroup ownerBag;

	/**
	 * 确定文件包，初始化类型ID
	 * 
	 * @param manager
	 * @throws Exception
	 */
	public void initClassID(ViewExport manager) throws Exception
	{
		if (!isCfg)
		{
			return;
		}

		// 计算MD5
		measureClassID(manager);
	}

	/**
	 * 获取类型ID
	 * 
	 * @return
	 */
	public long getClassID()
	{
		return classID;
	}

	private boolean classInited = false;

	/**
	 * 计算类型ID
	 * 
	 * @param manager
	 * @throws Exception
	 */
	private void measureClassID(ViewExport manager) throws Exception
	{
		if (classInited)
		{
			return;
		}

		classInited = true;

		appendToBag(manager);

		if (!isCfg)
		{
			fileKey = manager.getChecksumTable().getChecksumID(getInnerPath());
			classID = manager.getClassIDTable().getClassID(manager.getChecksumTable().getChecksumID(getInnerPath()) + ".img");
		}
		else
		{
			try
			{
				rebuildDocumentTree(manager);
				byte[] bytes = XmlUtil.formatXML(domcument.asXML()).getBytes("UTF-8");
				String checksum = MD5Util.md5Bytes(bytes);

				classID = manager.getClassIDTable().getClassID(checksum + ".xml");
			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * 附加到文件包
	 * 
	 * @param manager
	 */
	private void appendToBag(ViewExport manager)
	{
		String[] urls = new String[crossRefs.size()];
		urls = crossRefs.toArray(urls);
		Arrays.sort(urls);

		String crossRefKey = "";
		for (int i = 0; i < urls.length; i++)
		{
			crossRefKey = crossRefKey + urls[i];
			if (i < urls.length - 1)
			{
				crossRefKey += "+";
			}
		}

		ownerBag = manager.getBagBy(crossRefKey);
		ownerBag.add(this);
	}

	/**
	 * 视图包排序器
	 */
	private Comparator<ViewFile> viewFileComparator = new Comparator<ViewFile>()
	{
		@Override
		public int compare(ViewFile arg0, ViewFile arg1)
		{
			return arg0.getInnerPath().compareTo(arg1.getInnerPath());
		}
	};

	/**
	 * 重建文档树
	 * 
	 * @throws Exception
	 */
	private void rebuildDocumentTree(ViewExport manager) throws Exception
	{
		HashSet<String> keys = new HashSet<String>();
		keys.add(manager.getChecksumTable().getChecksumID(getInnerPath()));

		// 重建依赖
		Element dependNode = (Element) domcument.getRootElement().selectSingleNode("depends");
		if (dependNode != null)
		{
			dependNode.detach();
		}

		if (mergeredXmlDef.size() > 0)
		{
			dependNode = domcument.getRootElement().addElement("depends");

			ViewFile[] xmls = new ViewFile[mergeredXmlDef.size()];
			xmls = mergeredXmlDef.toArray(xmls);
			Arrays.sort(xmls, viewFileComparator);

			for (ViewFile file : xmls)
			{
				Element childNode = dependNode.addElement("depend");
				childNode.addAttribute("src", file.innerPath);

				keys.add(manager.getChecksumTable().getChecksumID(file.getInnerPath()));
			}
		}

		// 重建图像
		Element bitmapNode = (Element) domcument.getRootElement().selectSingleNode("bitmaps");
		if (bitmapNode != null)
		{
			bitmapNode.detach();
		}

		if (mergeredImgDef.size() > 0)
		{
			bitmapNode = domcument.getRootElement().addElement("bitmaps");

			String[] refs = new String[mergeredImgDef.size()];
			refs = mergeredImgDef.keySet().toArray(refs);
			Arrays.sort(refs);

			for (String key : refs)
			{
				ViewFile val = mergeredImgDef.get(key);

				val.measureClassID(manager);

				Element childNode = bitmapNode.addElement("bitmap");
				childNode.addAttribute("id", key);
				// childNode.addAttribute("src", val.innerPath);
				childNode.addAttribute("type", "FILE_" + val.classID);

				keys.add(manager.getChecksumTable().getChecksumID(val.getInnerPath()));
			}
		}
		if(imgRefs.size()>0)
		{
			for(String key:imgRefs.keySet())
			{
				Element childNode = bitmapNode.addElement("bitmap");
				childNode.addAttribute("id", key);
				childNode.addAttribute("dependId", imgRefs.get(key));
			}
		}

		// 重建音效
		Element soundNode = (Element) domcument.getRootElement().selectSingleNode("sounds");
		if (soundNode != null)
		{
			soundNode.detach();
		}

		if (mergeredMp3Def.size() > 0)
		{
			soundNode = domcument.getRootElement().addElement("sounds");

			String[] refs = new String[mergeredMp3Def.size()];
			refs = mergeredMp3Def.keySet().toArray(refs);
			Arrays.sort(refs);

			for (String key : refs)
			{
				ViewFile val = mergeredMp3Def.get(key);

				val.measureClassID(manager);

				Element childNode = soundNode.addElement("sound");
				childNode.addAttribute("id", key);
				// childNode.addAttribute("src", val.innerPath);
				childNode.addAttribute("type", "FILE_" + val.classID);

				keys.add(manager.getChecksumTable().getChecksumID(val.getInnerPath()));
			}
		}

		// 重建动画
		Element swfNode = (Element) domcument.getRootElement().selectSingleNode("swfs");
		if (swfNode != null)
		{
			swfNode.detach();
		}

		dependSwfs = new ArrayList<ViewFileSwf>();
		if (mergeredSwfDef.size() > 0)
		{
			swfNode = domcument.getRootElement().addElement("swfs");

			String[] refs = new String[mergeredSwfDef.size()];
			refs = mergeredSwfDef.keySet().toArray(refs);
			Arrays.sort(refs);

			for (String key : refs)
			{
				ViewFileSwf swf = new ViewFileSwf(manager.getChecksumTable().getChecksumID(mergeredSwfDef.get(key).getInnerPath()), mergeredSwfDef.get(key));
				manager.addOutputFile(swf.getFile());
				dependSwfs.add(swf);

				Element childNode = swfNode.addElement("swf");
				childNode.addAttribute("id", key);
				childNode.addAttribute("src", manager.getOutputFileURL(swf.getFile()));

				keys.add(manager.getChecksumTable().getChecksumID(swf.getFile().getInnerPath()));
			}
		}

		// 重建地址
		Element urlNode = (Element) domcument.getRootElement().selectSingleNode("urls");
		if (urlNode != null)
		{
			urlNode.detach();
		}

		if (mergeredUrlDef.size() > 0)
		{
			urlNode = domcument.getRootElement().addElement("urls");

			String[] refs = new String[mergeredUrlDef.size()];
			refs = mergeredUrlDef.keySet().toArray(refs);
			Arrays.sort(refs);

			for (String key : refs)
			{
				ViewFileOther other = new ViewFileOther(manager.getChecksumTable().getChecksumID(mergeredUrlDef.get(key).getInnerPath()), mergeredUrlDef.get(key));
				manager.addOutputFile(other.getFile());

				Element childNode = urlNode.addElement("url");
				childNode.addAttribute("id", key);
				childNode.addAttribute("src", manager.getOutputFileURL(other.getFile()));

				keys.add(manager.getChecksumTable().getChecksumID(other.getFile().getInnerPath()));
			}
		}

		String[] stringKeys = new String[keys.size()];
		stringKeys = keys.toArray(stringKeys);
		Arrays.sort(stringKeys, new Comparator<String>()
		{
			@Override
			public int compare(String o1, String o2)
			{
				return o1.compareTo(o2);
			}
		});

		StringBuilder keySB = new StringBuilder();
		for (int i = 0; i < stringKeys.length; i++)
		{
			if (i > 0)
			{
				keySB.append("+");
			}
			keySB.append(stringKeys[i]);
		}

		fileKey = keySB.toString();
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// 获取依赖包列表
	//
	// ---------------------------------------------------------------------------------------------------------------

	private ArrayList<ViewFileGroup> dependBags;
	private ArrayList<ViewFileSwf> dependSwfs;

	/**
	 * 获取依赖SWF列表
	 * 
	 * @return
	 */
	public ArrayList<ViewFileSwf> getDependViewSwfs()
	{
		if (dependSwfs == null)
		{
			dependSwfs = new ArrayList<ViewFileSwf>();
		}
		return dependSwfs;
	}

	/**
	 * 获取依赖包列表
	 */
	public ArrayList<ViewFileGroup> getDependViewFileGroups()
	{
		if (dependBags != null)
		{
			return dependBags;
		}

		HashSet<ViewFileGroup> outBags = new HashSet<ViewFileGroup>();

		// 依赖
		if (mergeredXmlDef.size() > 0)
		{
			Iterator<ViewFile> xmls = mergeredXmlDef.iterator();
			while (xmls.hasNext())
			{
				outBags.add(xmls.next().ownerBag);
			}
		}

		// 图像
		if (mergeredImgDef.size() > 0)
		{
			Iterator<ViewFile> imgs = mergeredImgDef.values().iterator();
			while (imgs.hasNext())
			{
				outBags.add(imgs.next().ownerBag);
			}
		}

		// 音效
		if (mergeredMp3Def.size() > 0)
		{
			Iterator<ViewFile> mp3s = mergeredMp3Def.values().iterator();
			while (mp3s.hasNext())
			{
				outBags.add(mp3s.next().ownerBag);
			}
		}

		// 动画
		// if(mergeredSwfDef.size()>0)
		// {
		// Iterator<ViewFile> swfs=mergeredSwfDef.values().iterator();
		// while(swfs.hasNext())
		// {
		// outBags.add(swfs.next().ownerBag);
		// }
		// }

		// 组合当前包
		dependBags = new ArrayList<ViewFileGroup>();
		dependBags.add(ownerBag);

		// 组合外部引用包
		for (ViewFileGroup bag : outBags)
		{
			dependBags.add(bag);
		}

		return dependBags;
	}
}
