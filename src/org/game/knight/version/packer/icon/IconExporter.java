package org.game.knight.version.packer.icon;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.chw.util.FileUtil;
import org.chw.util.MD5Util;
import org.chw.util.ZlibUtil;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.base.AbsExporter;

public class IconExporter extends AbsExporter
{
	private Hashtable<String, File> files;
	private IconSizeTable sizeTable;
	private File cfgDir;
	
	private boolean zip;

	/**
	 * 构造函数
	 * 
	 * @param src
	 * @param dst
	 */
	public IconExporter(File src, File dst, boolean zip,File cfgDir)
	{
		super("导出图标", src, dst);

		this.zip = zip;
		this.cfgDir=cfgDir;
	}

	@Override
	protected void openVers()
	{
		super.openVers();

		sizeTable = new IconSizeTable();
		sizeTable.open(new File(getDestDir().getPath() + "/.ver/iconSizes"));
	}

	@Override
	protected void saveVers()
	{
		super.saveVers();

		sizeTable.save();
	}

	@Override
	protected void exportContent() throws Exception
	{
		files = new Hashtable<String, File>();

		if (isCancel())
		{
			return;
		}

		// 遍历文件
		GamePacker.beginLogSet("读取文件");
		readDir(getSourceDir());
		GamePacker.endLogSet();

		// 排序文件
		GamePacker.beginLogSet("排序文件");
		String[] urls = new String[files.keySet().size()];
		urls = files.keySet().toArray(urls);
		Arrays.sort(urls);
		GamePacker.endLogSet();

		if (isCancel())
		{
			return;
		}

		// 导出文件
		GamePacker.beginLogSet("输出文件");
		for (String url : urls)
		{
			GamePacker.progress("输出文件", url);
			exportFile(getChecksumTable().getChecksumID(url), files.get(url));

			if (isCancel())
			{
				return;
			}
		}
		GamePacker.endLogSet();

		if (isCancel())
		{
			return;
		}

		// 更新图标大小信息
		GamePacker.beginLogSet("读取图标大小");
		for (String url : urls)
		{
			String key = getChecksumTable().getChecksumID(url);
			if (sizeTable.getWidth(key) == null)
			{
				BufferedImage img = ImageIO.read(files.get(url));
				int imgW = img.getWidth();
				int imgH = img.getHeight();
				sizeTable.add(key, imgW + "", imgH + "");

				GamePacker.progress("读取图标大小", url + "(" + imgW + "," + imgH + ")");

				if (isCancel())
				{
					return;
				}
			}
		}
		GamePacker.endLogSet();

		if (isCancel())
		{
			return;
		}

		// 生成配置
		GamePacker.beginLogSet("输出配置信息");
		GamePacker.log("生成配置信息");
		StringBuilder txt = new StringBuilder();
		txt.append("<iconSet>\n");
		for (String url : urls)
		{
			File file = files.get(url);
			String key = getChecksumTable().getChecksumID(url);

			String type = file.getParentFile().getPath().substring(getSourceDir().getPath().length()).replaceAll("\\\\", "/").replaceFirst("/", "");
			while (type.charAt(type.length() - 1) == '/')
			{
				type = type.substring(0, type.length() - 1);
			}
			String name = getFileName(file);
			String path = getExportedFileUrl(key);
			String iconW = sizeTable.getWidth(key);
			String iconH = sizeTable.getHeight(key);

			txt.append(String.format("\t<icon dir=\"%s\" name=\"%s\" url=\"%s\" w=\"%s\" h=\"%s\"/>\n", type, name, path, iconW, iconH));
		}
		txt.append("</iconSet>");
		GamePacker.log("保存配置信息");
		byte[] bytes = txt.toString().getBytes("UTF-8");
		String checksum = (zip ? "z" : "") + MD5Util.md5Bytes(bytes);
		exportFile(checksum, (zip ? ZlibUtil.compress(bytes) : bytes), "cfg");
		GamePacker.endLogSet();

		if (isCancel())
		{
			return;
		}

		// 生成qpurl.cvs
		if(cfgDir!=null && cfgDir.exists() && cfgDir.isDirectory())
		{
			ItemConfigHandler itemHandler = new ItemConfigHandler(cfgDir);
			itemHandler.build();
			
			ArrayList<ItemID> items=itemHandler.getItems();
			Collections.sort(items, new Comparator<ItemID>()
			{
				@Override
				public int compare(ItemID o1, ItemID o2)
				{
					if(o1.id<o2.id)
					{
						return -1;
					}
					else if(o1.id>o2.id)
					{
						return 1;
					}
					return 0;
				}
			});

			StringBuilder qpurl=new StringBuilder();
			for (ItemID item : items)
			{
				String[] paths = new String[] { "/bagIcon/" + item.iconID + ".png", "/bagIcon/" + item.iconID + ".jpg", "/bagIcon/" + item.id + ".png", "/bagIcon/" + item.id + ".jpg", "/bagIcon/0.png" };
				for (String path : paths)
				{
					if(getChecksumTable().getChecksum(path)!=null)
					{
						String key = getChecksumTable().getChecksumID(path);
						if(hasExportedFile(key))
						{
							int itemID=item.id;
							String saveURL = getExportedFileUrl(key);
							if(saveURL.charAt(0)!='/')
							{
								saveURL="/"+saveURL;
							}
							qpurl.append(itemID+","+saveURL+"\r\n");
							break;
						}
					}
				}
			}
			
			FileUtil.writeFile(new File(getDestDir().getPath()+File.separator+"qpurl.cvs"), qpurl.toString().getBytes("UTF-8"));
		}

		// 导出项目配置
		GamePacker.beginLogSet("输出汇总信息");
		GamePacker.log("生成汇总信息");
		StringBuilder sb = new StringBuilder();
		sb.append("<project>\n");
		sb.append("\t<configs>\n");
		sb.append(String.format("\t\t<config name=\"iconSet\" path=\"%s\" size=\"%s\" />\n", getExportedFileUrl(checksum), getExportedFileSize(checksum)));
		sb.append("\t</configs>\n");
		sb.append("</project>");
		GamePacker.log("保存汇总信息");
		FileUtil.writeFile(new File(getDestDir().getPath() + "/db.xml"), sb.toString().getBytes("UTF-8"));
		GamePacker.endLogSet();

		// 生成文件列表
		GamePacker.beginLogSet("输出文件汇总");
		GamePacker.log("生成文件汇总");
		StringBuilder filesSB = new StringBuilder();
		String[] urlList = getExportedFileUrls();
		for (String url : urlList)
		{
			filesSB.append(url + "\n");
		}
		GamePacker.log("保存文件汇总");
		FileUtil.writeFile(new File(getDestDir().getPath() + "/db.ver"), filesSB.toString().getBytes("UTF-8"));
		GamePacker.endLogSet();
	}

	/**
	 * 读取目录
	 * 
	 * @param dir
	 */
	private void readDir(File dir)
	{
		File[] files = dir.listFiles();

		if (files == null)
		{
			return;
		}

		for (int i = 0; i < files.length; i++)
		{
			File file = files[i];

			if (file.isHidden())
			{
				continue;
			}

			if (file.isDirectory())
			{
				readDir(file);
			}
			else
			{
				String ext = getFileExtName(file.getPath());
				if (ext != null && !ext.isEmpty())
				{
					ext=ext.toLowerCase();
					
					if (ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg"))
					{
						String innerPath = file.getPath().substring(getSourceDir().getPath().length()).replaceAll("\\\\", "/");

						GamePacker.progress("读取文件", innerPath);

						this.files.put(innerPath, file);
					}
				}
			}
		}
	}

	// -------------------------------------------------------------------------------------------------------------------------------

}
