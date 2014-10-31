package org.game.knight.version.packer.view;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.chw.util.MD5Util;
import org.dom4j.Element;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.GamePackerHelper;

public class ViewEntityWriter
{
	private GamePackerHelper helper;
	private ViewZip oldZip;
	private ViewZip newZip;

	private HashMap<File, String> file_md5 = new HashMap<File, String>();
	private HashMap<String, HashMap<String, ViewEntity>> lang_viewList = new HashMap<String, HashMap<String, ViewEntity>>();

	public ViewEntityWriter(GamePackerHelper helper)
	{
		this.helper = helper;
		this.oldZip = new ViewZip(new File(helper.viewOutputFolder.getPath() + File.separator + "ver.zip"));
		this.newZip = new ViewZip();
	}

	/**
	 * 获取视图输出目录
	 * 
	 * @return
	 */
	public File getViewOutputFolder()
	{
		return helper.viewOutputFolder;
	}

	/**
	 * 获取视图文件
	 * 
	 * @param lang
	 * @param url
	 * @return
	 */
	public ViewEntity getView(String lang, String url)
	{
		if (lang_viewList.containsKey(lang) && lang_viewList.get(lang).containsKey(url))
		{
			return lang_viewList.get(lang).get(url);
		}
		lang = "zh";
		if (lang_viewList.containsKey(lang) && lang_viewList.get(lang).containsKey(url))
		{
			return lang_viewList.get(lang).get(url);
		}
		return null;
	}

	/**
	 * 获取视图文件的MD5
	 * 
	 * @param view
	 * @return
	 */
	public String getFileMD5(File file)
	{
		return file_md5.get(file);
	}

	/**
	 * 获取文件的输出类型名称
	 * 
	 * @param key
	 * @return
	 */
	public String getFileOutputTypeName(String key)
	{
		String typeName = oldZip.getTypeName(key);
		if (typeName == null)
		{
			typeName = "FILE_" + oldZip.getVersionNextTypeID();
		}
		newZip.setTypeName(key, typeName);
		return typeName;
	}

	/**
	 * 获取文件输出路径
	 * 
	 * @param key
	 * @param ext
	 * @return
	 */
	public String getFileOutputPath(String key, String ext)
	{
		String filePath = oldZip.getGameFiles().get(key);
		if (filePath == null)
		{
			filePath = oldZip.getVersionNextGameFileURL(ext);
		}
		newZip.getGameFiles().put(key, filePath);
		newZip.getVersionFiles().add("/" + helper.viewOutputFolder.getName() + filePath);
		return filePath;
	}

	/**
	 * 开始
	 * 
	 * @return
	 */
	public boolean go()
	{
		file_md5 = new HashMap<File, String>();
		lang_viewList = new HashMap<String, HashMap<String, ViewEntity>>();

		try
		{
			GamePacker.beginTask("导出视图");

			// 获取文件集
			File[] files = helper.listFiles(helper.viewInputFolder, "*");

			// 计算文件MD5
			for (int i = 0; i < files.length; i++)
			{
				File file = files[i];

				String lang = "zh";
				String url = file.getPath().substring(helper.viewInputFolder.getPath().length()).replaceAll("\\\\", "/");
				String dir = file.getParentFile().getPath().substring(helper.viewInputFolder.getPath().length()).replaceAll("\\\\", "/");

				String name = file.getName();
				int dot1 = name.lastIndexOf(".");
				if (dot1 != -1)
				{
					int dot2 = name.lastIndexOf(".", dot1 - 1);
					if (dot2 != -1)
					{
						lang = name.substring(dot2 + 1, dot1);
						url = dir + "/" + name.substring(0, dot2) + name.substring(dot1);
					}
				}

				GamePacker.progress(String.format("检查文件变化(%s/%s)：%s", i + 1, files.length, url));

				file_md5.put(file, MD5Util.md5(file));
				if (!lang_viewList.containsKey(lang))
				{
					lang_viewList.put(lang, new HashMap<String, ViewEntity>());
				}
				lang_viewList.get(lang).put(url, new ViewEntity(this, file, dir, url));

				if (GamePacker.isCancel())
				{
					return false;
				}
			}

			StringBuilder versionSB = new StringBuilder();
			versionSB.append("<projects>\n");

			String[] langs = lang_viewList.keySet().toArray(new String[] {});
			Arrays.sort(langs);
			for (String lang : langs)
			{
				GamePacker.log(String.format("输出%s版本视图。", lang));

				// 确定语言文件列表
				HashMap<String, ViewEntity> url_view = new HashMap<String, ViewEntity>();
				for (ViewEntity entity : lang_viewList.get("zh").values())
				{
					url_view.put(entity.url, entity);
				}
				for (ViewEntity entity : lang_viewList.get(lang).values())
				{
					url_view.put(entity.url, entity);
				}

				// 排序文件
				ViewEntity[] views = url_view.values().toArray(new ViewEntity[] {});
				Arrays.sort(views, new Comparator<ViewEntity>()
				{
					@Override
					public int compare(ViewEntity o1, ViewEntity o2)
					{
						return o1.url.compareTo(o2.url);
					}
				});

				// 解析XML文件
				for (int i = 0; i < views.length; i++)
				{
					ViewEntity view = views[i];
					GamePacker.progress(String.format("解析文件内容(%s/%s)：%s", i + 1, views.length, view.url));
					view.open(lang);

					if (GamePacker.isCancel())
					{
						return false;
					}
				}

				// 合并继承引用
				for (int i = 0; i < views.length; i++)
				{
					ViewEntity view = views[i];
					GamePacker.progress(String.format("合并继承的资源引用(%s/%s)：%s", i + 1, views.length, view.url));
					view.mergerInheritRef();

					if (GamePacker.isCancel())
					{
						return false;
					}
				}

				// 计算交叉引用
				for (int i = 0; i < views.length; i++)
				{
					ViewEntity view = views[i];
					GamePacker.progress(String.format("计算交叉引用(%s/%s)：%s", i + 1, views.length, view.url));
					view.measureCrossRef();

					if (GamePacker.isCancel())
					{
						return false;
					}
				}

				// 资源分组、资源分包
				HashMap<String, ViewEntityGroup> group_views = new HashMap<String, ViewEntityGroup>();
				for (int i = 0; i < views.length; i++)
				{
					ViewEntity view = views[i];
					GamePacker.progress(String.format("确定资源分组(%s/%s)：%s", i + 1, views.length, view.url));

					String groupName = view.getGroupName();
					if (groupName != null && groupName.isEmpty() == false)
					{
						if (!group_views.containsKey(groupName))
						{
							group_views.put(groupName, new ViewEntityGroup(this));
						}
						group_views.get(groupName).addEntity(view);
					}

					if (GamePacker.isCancel())
					{
						return false;
					}
				}
				ViewEntityGroup[] groups = group_views.values().toArray(new ViewEntityGroup[] {});
				for (int i = 0; i < groups.length; i++)
				{
					ViewEntityGroup group = groups[i];
					GamePacker.progress(String.format("确定资源包：%s/%s", i + 1, groups.length));
					group.measureBag();

					if (GamePacker.isCancel())
					{
						return false;
					}
				}

				// 确定新增的资源
				ArrayList<ViewEntityBag> outputBags = new ArrayList<ViewEntityBag>();
				for (ViewEntityGroup group : groups)
				{
					for (ViewEntityBag bag : group.getBags())
					{
						String url = bag.getOutputPath();
						File outputFile = new File(helper.viewOutputFolder.getPath() + url);
						if (!outputFile.exists())
						{
							outputBags.add(bag);
						}
					}

					if (GamePacker.isCancel())
					{
						return false;
					}
				}

				// 输出新增的资源
				for (int i = 0; i < outputBags.size(); i++)
				{
					ViewEntityBag bag = outputBags.get(i);
					GamePacker.progress(String.format("输出资源包：%s/%s", i + 1, groups.length));
					bag.writeTo(new File(helper.viewOutputFolder.getPath() + bag.getOutputPath()));

					if (GamePacker.isCancel())
					{
						return false;
					}
				}

				//
				versionSB.append(getVersionConfig(views, lang));
				newZip.getCfgFiles().put("$UIText.xml", getLangConfig(views).getBytes("UTF-8"));
				// newZip.getCfgFiles().put("core/version." + lang + ".xml",
				// getVersionConfig(views, lang).getBytes("UTF-8"));

				// if (lang.equals("zh"))
				// {
				// newZip.setVersion(getVersionConfig(views, lang));
				// }
			}
			versionSB.append("</projects>");

			// 保存版本信息
			newZip.setVersion(versionSB.toString());
			newZip.setVersionProps(oldZip.getVersionProps());
			newZip.getVersionFiles().add("/" + helper.viewOutputFolder.getName() + "/" + oldZip.getFile().getName());
			newZip.saveTo(oldZip.getFile());

			GamePacker.log("完成");
		}
		catch (Exception error)
		{
			GamePacker.error(error);
			return false;
		}
		finally
		{
			GamePacker.endTask();
		}

		return true;
	}

	/**
	 * 获取语言配置
	 * 
	 * @param entitys
	 */
	private String getLangConfig(ViewEntity[] entitys)
	{
		StringBuilder langSB = new StringBuilder();
		langSB.append("<uitext>\n");
		for (ViewEntity entity : entitys)
		{
			if (entity.isCfg && entity.getTextsNode() != null)
			{
				@SuppressWarnings("rawtypes")
				List nodes = entity.getTextsNode().selectNodes("text");

				langSB.append("\t<bag url=\"" + entity.url + "\">\n");
				for (int i = 0; i < nodes.size(); i++)
				{
					Element node = (Element) nodes.get(i);
					langSB.append("\t\t<item id=\"" + node.attributeValue("id") + "\"><![CDATA[" + node.getText() + "]]></item>\n");
				}
				langSB.append("\t</bag>\n");
			}
		}
		langSB.append("</uitext>");

		return langSB.toString();
	}

	private String getVersionConfig(ViewEntity[] entitys, String lang)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("\t<project lang=\"" + lang + "\" mode=\"3d\">\n");
		sb.append("\t\t<views>\n");
		for (ViewEntity entity : entitys)
		{
			if (entity.isCfg)
			{
				sb.append(String.format("\t\t\t<view preload=\"%s\" name=\"%s\" url=\"%s\" id=\"%s\" size=\"%s\" files=\"%s\"/>\n", helper.format(entity.getPreloadMethod()), entity.getBagName(), entity.url, entity.getOutputTypeName(), entity.getNeedLoadFileSize(), helper.format(entity.getNeedLoadFileUrls())));
			}
		}
		sb.append("\t\t</views>\n");

		sb.append("\t\t<viewBags>\n");
		HashMap<Integer, HashSet<String>> type_files = new HashMap<Integer, HashSet<String>>();
		for (ViewEntity entity : entitys)
		{
			if (entity.isCfg)
			{
				for (int type : entity.getPreloadMethod())
				{
					if (!type_files.containsKey(type))
					{
						type_files.put(type, new HashSet<String>());
					}
					for (String file : entity.getNeedLoadFileUrls())
					{
						type_files.get(type).add(file);
					}
				}
			}
		}
		Integer[] types = type_files.keySet().toArray(new Integer[] {});
		Arrays.sort(types);
		for (int type : types)
		{
			String[] urlList = type_files.get(type).toArray(new String[] {});
			Arrays.sort(urlList);

			int size = 0;
			for (String url : urlList)
			{
				size += new File(helper.viewOutputFolder.getParentFile().getPath() + url).length();
			}
			sb.append(String.format("\t\t\t<viewBagFiles type=\"%s\" mb=\"%s\" size=\"%s\" files=\"%s\" />\n", type, size / 1024 / 1204, size, helper.format(urlList)));
		}
		sb.append("\t\t</viewBags>\n");
		sb.append("\t</project>\n");

		return sb.toString();
	}
}
