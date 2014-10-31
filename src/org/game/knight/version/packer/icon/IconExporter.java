package org.game.knight.version.packer.icon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.xml.parsers.ParserConfigurationException;

import org.chw.util.FileUtil;
import org.chw.util.MD5Util;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.GamePackerHelper;
import org.game.knight.version.packer.base.ZipConfig;
import org.xml.sax.SAXException;

public class IconExporter
{
	private GamePackerHelper helper;
	private File outputFolder;

	private ZipConfig oldZip;
	private ZipConfig newZip;

	/**
	 * 构造函数
	 * 
	 * @param src
	 * @param dst
	 */
	public IconExporter(GamePackerHelper helper, File dst)
	{
		this.helper = helper;
		this.outputFolder = dst;

		this.oldZip = new ZipConfig(new File(dst + File.separator + "ver.zip"));
		this.newZip = new ZipConfig();
	}

	/**
	 * 发布
	 * 
	 * @throws Exception
	 */
	public Boolean pub()
	{
		try
		{
			GamePacker.beginTask("导出图标");

			HashMap<String, String> outputMap = new HashMap<String, String>();

			StringBuilder sb = new StringBuilder();
			sb.append("<iconSet>\n");
			File[] files = helper.listFiles(helper.iconInputFolder, "*");
			for (int i = 0; i < files.length; i++)
			{
				File file = files[i];
				String ext = helper.getFileExtName(file).toLowerCase();
				String url = file.getPath().substring(helper.iconInputFolder.getPath().length()).replaceAll("\\\\", "/");

				GamePacker.progress(String.format("处理图标(%s/%s)：%s", i + 1, files.length, url));

				if (ext.equals("jpg") || ext.equals("png"))
				{
					int width = 0;
					int height = 0;

					Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(ext);
					if (iter.hasNext())
					{
						ImageReader reader = iter.next();
						try
						{
							reader.setInput(new FileImageInputStream(file));
							width = reader.getWidth(reader.getMinIndex());
							height = reader.getHeight(reader.getMinIndex());

							String writeMD5 = MD5Util.md5(file);
							String writeURL = oldZip.getGameFiles().get(writeMD5);
							if (writeURL == null)
							{
								writeURL = oldZip.getVersionNextGameFileURL(ext);

								FileUtil.writeFile(new File(outputFolder.getPath() + writeURL), MD5Util.addSuffix(FileUtil.getFileBytes(file)));
							}

							newZip.getGameFiles().put(writeMD5, writeURL);
							newZip.getVersionFiles().add("/" + outputFolder.getName() + writeURL);

							sb.append(String.format("\t<icon path=\"%s\" url=\"%s\" w=\"%s\" h=\"%s\"/>\n", url.substring(1, url.length() - ext.length() - 1), "/" + outputFolder.getName() + writeURL, width, height));

							outputMap.put(url.substring(0, url.length() - ext.length()) + ext, writeURL);
						}
						finally
						{
							reader.dispose();
						}
					}
				}
				if (GamePacker.isCancel())
				{
					return false;
				}
			}
			sb.append("</iconSet>");

			// 输出qpurl.cvs
			GamePacker.progress(String.format("输出 qpurl.cvs"));
			byte[] qpurl = getIconURLBytes(outputMap);
			if (qpurl != null)
			{
				FileUtil.writeFile(new File(outputFolder.getPath() + "/qpurl.cvs"), qpurl);
				newZip.getVersionFiles().add("/qpurl.cvs");
			}

			//
			newZip.setVersion("");
			newZip.setVersionProps(oldZip.getVersionProps());
			newZip.getVersionFiles().add("/" + helper.iconOutputFolder.getName() + "/" + oldZip.getFile().getName());
			newZip.getCfgFiles().put("$IconSet.xml", sb.toString().getBytes("UTF-8"));
			newZip.saveTo(oldZip.getFile());

			GamePacker.log("完成");

			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			GamePacker.endTask();
		}

		return false;
	}

	/**
	 * 生成qpurl.cvs
	 * 
	 * @param map
	 * @return
	 * @throws FileNotFoundException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private byte[] getIconURLBytes(HashMap<String, String> map) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException
	{
		File cfgDir = helper.cfgInputFolder;
		if (cfgDir != null && cfgDir.exists() && cfgDir.isDirectory())
		{
			ItemConfigHandler itemHandler = new ItemConfigHandler(cfgDir);
			itemHandler.build();

			ArrayList<ItemID> items = itemHandler.getItems();
			Collections.sort(items, new Comparator<ItemID>()
			{
				@Override
				public int compare(ItemID o1, ItemID o2)
				{
					if (o1.id < o2.id)
					{
						return -1;
					}
					else if (o1.id > o2.id)
					{
						return 1;
					}
					return 0;
				}
			});

			StringBuilder qpurl = new StringBuilder();
			Hashtable<Integer, Boolean> idTable = new Hashtable<Integer, Boolean>();
			for (ItemID item : items)
			{
				if (idTable.containsKey(item.id))
				{
					continue;
				}

				idTable.put(item.id, true);

				String[] paths = new String[] { "/bagIcon/" + item.iconID + ".png", "/bagIcon/" + item.iconID + ".jpg", "/bagIcon/" + item.id + ".png", "/bagIcon/" + item.id + ".jpg", "/bagIcon/0.png" };
				for (String path : paths)
				{
					if (map.containsKey(path))
					{
						String saveURL = map.get(path);
						if (saveURL.charAt(0) != '/')
						{
							saveURL = "/" + saveURL;
						}
						qpurl.append(String.format("%s,%s\r\n", item.id, (saveURL.charAt(0) != '/' ? '/' : "") + saveURL));
						break;
					}
				}
			}

			return qpurl.toString().getBytes("UTF-8");
		}
		return null;
	}
}
