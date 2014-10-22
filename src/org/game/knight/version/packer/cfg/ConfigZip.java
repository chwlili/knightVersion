package org.game.knight.version.packer.cfg;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;

import org.game.knight.version.packer.base.ZipConfig;

public class ConfigZip extends ZipConfig
{

	public ConfigZip(File file)
	{
		super(file);
	}

	public ConfigZip()
	{
		super(null);
	}

	@Override
	protected void save() throws IOException
	{
		saveXmlInfoMap();
		saveConfigFile();
		saveSkillContent();

		super.save();
	}

	// ==================================================================================

	/**
	 * 技能内容汇总文件
	 */
	private static final String SKILL_CONTENT_FILE = "tmp/skill/content.txt";

	private String skillMD5;
	private String skillContent;

	/**
	 * 获取技能内容
	 * 
	 * @param md5
	 * @return
	 */
	public String getSkillContent(String md5)
	{
		openSkillContent();

		if (md5.equals(skillMD5))
		{
			return skillContent;
		}
		return null;
	}

	/**
	 * 设置技能内容
	 * 
	 * @param md5
	 * @param content
	 */
	public void setSkillContent(String md5, String content)
	{
		skillMD5 = md5;
		skillContent = content;
	}

	/**
	 * 打开配置汇总文件
	 * 
	 */
	private void openSkillContent()
	{
		if (skillMD5 != null)
		{
			return;
		}

		String text = getEntryContent(SKILL_CONTENT_FILE);
		if (text != null)
		{
			int index = text.indexOf("\n");
			if (index != -1)
			{
				skillMD5 = text.substring(0, index);
				skillContent = text.substring(index + 1);
			}
		}
	}

	/**
	 * 保存配置汇总文件
	 */
	private void saveSkillContent()
	{
		if (skillMD5 != null && skillContent != null)
		{
			try
			{
				setEntry(SKILL_CONTENT_FILE, (skillMD5 + "\n" + skillContent).getBytes("UTF-8"));
			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
		}
	}

	// ==================================================================================

	// ==================================================================================

	/**
	 * 配置文件目录
	 */
	private static final String CONFIG_DIR = "tmp/config/files/";

	/**
	 * 配置汇总文件
	 */
	private static final String CONFIG_MD5_FILE = "tmp/config/md5.txt";

	/**
	 * 配置汇总文件
	 */
	private static final String CONFIG_NAME_FILE = "tmp/config/name.txt";

	/**
	 * 配置列表
	 */
	private HashMap<String, String> urlMap = null;

	/**
	 * 名称列表
	 */
	private HashMap<String, String[]> xmlInfoMap = null;

	/**
	 * 获取配置数据
	 * 
	 * @param xmlMD5
	 * @param xml2MD5
	 * @param xlsMD5
	 * @return
	 */
	public byte[] getConfig(String xmlMD5, String xml2MD5, String xlsMD5)
	{
		openConfigFile();

		String url = urlMap.get(xmlMD5 + "+" + xml2MD5 + "+" + xlsMD5);
		if (url != null)
		{
			return getEntry(url);
		}
		return null;
	}

	/**
	 * 设置配置数据
	 * 
	 * @param xmlMD5
	 * @param xml2MD5
	 * @param xlsMD5
	 * @param data
	 */
	public void setConfig(String xmlMD5, String xml2MD5, String xlsMD5, byte[] data)
	{
		openConfigFile();

		String key = xmlMD5 + "+" + xml2MD5 + "+" + xlsMD5;
		String url = urlMap.get(key);
		if (url == null)
		{
			int i = 1;
			while (true)
			{
				url = CONFIG_DIR + i;
				if (getEntry(url) == null)
				{
					break;
				}
				i++;
			}
		}

		urlMap.put(key, url);
		setEntry(url, data);
	}

	public String[] getXmlInfo(String lang, String mode, String name)
	{
		openXmlInfoMap();

		return xmlInfoMap.get(lang + "_" + mode + "_" + name);
	}

	public void setXmlInfo(String lang, String mode, String name, String xmlMD5, String xml2MD5, String xlsMD5)
	{
		openXmlInfoMap();

		xmlInfoMap.put(lang + "_" + mode + "_" + name, new String[] { xmlMD5, xml2MD5, xlsMD5 });
	}

	private void openXmlInfoMap()
	{
		if (xmlInfoMap != null)
		{
			return;
		}

		xmlInfoMap = new HashMap<String, String[]>();
		String text = getEntryContent(CONFIG_NAME_FILE);
		if (text != null)
		{
			String[] lines = text.split("\\n");
			for (String line : lines)
			{
				line = line.trim();

				if (line.length() > 0)
				{
					String[] pairs = line.split("=");
					if (pairs.length == 2)
					{
						String key = pairs[0].trim();

						String[] vals = pairs[1].trim().split("\\+");
						if (vals.length == 3)
						{
							String xmlMD5 = vals[0].trim();
							String xml2MD5 = vals[1].trim();
							String xlsMD5 = vals[2].trim();

							xmlInfoMap.put(key, new String[] { xmlMD5, xml2MD5, xlsMD5 });
						}
					}
				}
			}
		}
	}

	private void saveXmlInfoMap()
	{
		StringBuilder sb = new StringBuilder();
		if (xmlInfoMap != null)
		{
			String[] keys = xmlInfoMap.keySet().toArray(new String[] {});
			Arrays.sort(keys);

			for (String key : keys)
			{
				String[] values = xmlInfoMap.get(key);
				sb.append(key + " = " + values[0] + "+" + values[1] + "+" + values[2] + "\n");
			}
		}

		try
		{
			setEntry(CONFIG_NAME_FILE, sb.toString().getBytes("UTF-8"));
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 打开配置汇总文件
	 * 
	 */
	private void openConfigFile()
	{
		if (urlMap != null)
		{
			return;
		}
		urlMap = new HashMap<String, String>();

		String text = getEntryContent(CONFIG_MD5_FILE);
		if (text != null)
		{
			String[] lines = text.split("\\n");
			for (String line : lines)
			{
				line = line.trim();

				if (line.length() > 0)
				{
					String[] pairs = line.split("=");
					if (pairs.length == 2)
					{
						String key = pairs[0].trim();
						String val = pairs[1].trim();

						if (key.length() > 0 && val.length() > 0)
						{
							urlMap.put(key, val);
						}
					}
				}
			}
		}
	}

	/**
	 * 保存配置汇总文件
	 */
	private void saveConfigFile()
	{
		StringBuilder sb = new StringBuilder();
		if (urlMap != null)
		{
			String[] keys = urlMap.keySet().toArray(new String[] {});
			Arrays.sort(keys);

			for (String key : keys)
			{
				sb.append(key + " = " + urlMap.get(key) + "\n");
			}
		}

		try
		{
			setEntry(CONFIG_MD5_FILE, sb.toString().getBytes("UTF-8"));
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}

}
