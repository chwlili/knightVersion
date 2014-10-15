package org.game.knight.version.packer.icon;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
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
import org.game.knight.version.packer.GamePackerHelper;
import org.game.knight.version.packer.base.AbsExporter;

public class IconExporter extends AbsExporter
{
	private Hashtable<String, File> files;
	private IconSizeTable sizeTable;

	private GamePackerHelper helper;

	private boolean zip;

	/**
	 * ���캯��
	 * 
	 * @param src
	 * @param dst
	 */
	public IconExporter(GamePackerHelper helper, File dst, boolean zip)
	{
		super("����ͼ��", helper.getIconFolder(), dst);

		this.zip = zip;
		this.helper = helper;
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

		// �����ļ�
		GamePacker.beginLogSet("��ȡ�ļ�");
		readDir(getSourceDir());
		GamePacker.endLogSet();

		// �����ļ�
		GamePacker.beginLogSet("�����ļ�");
		String[] urls = new String[files.keySet().size()];
		urls = files.keySet().toArray(urls);
		Arrays.sort(urls);
		GamePacker.endLogSet();

		if (isCancel())
		{
			return;
		}

		// �����ļ�
		GamePacker.beginLogSet("����ļ�");
		for (String url : urls)
		{
			GamePacker.progress("����ļ�", url);
			// exportFile(getChecksumTable().getChecksumID(url),
			// files.get(url));
			exportFile(getChecksumTable().getGID(url), MD5Util.addSuffix(FileUtil.getFileBytes(files.get(url))), getFileExtName(files.get(url)));

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

		// ����ͼ���С��Ϣ
		GamePacker.beginLogSet("��ȡͼ���С");
		for (String url : urls)
		{
			String key = getChecksumTable().getGID(url);
			if (sizeTable.getWidth(key) == null)
			{
				BufferedImage img = ImageIO.read(files.get(url));
				int imgW = img.getWidth();
				int imgH = img.getHeight();
				sizeTable.add(key, imgW + "", imgH + "");

				GamePacker.progress("��ȡͼ���С", url + "(" + imgW + "," + imgH + ")");

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

		// ��������
		GamePacker.beginLogSet("���������Ϣ");
		GamePacker.log("����������Ϣ");
		StringBuilder txt = new StringBuilder();
		txt.append("<iconSet>\n");
		for (String url : urls)
		{
			File file = files.get(url);
			String key = getChecksumTable().getGID(url);

			String type = file.getParentFile().getPath().substring(getSourceDir().getPath().length()).replaceAll("\\\\", "/").replaceFirst("/", "");
			while (type.charAt(type.length() - 1) == '/')
			{
				type = type.substring(0, type.length() - 1);
			}
			String name = getFileName(file);
			String path = getExportedFileUrl(key);
			String iconW = sizeTable.getWidth(key);
			String iconH = sizeTable.getHeight(key);

			txt.append(String.format("\t<icon path=\"%s/%s\" url=\"%s\" w=\"%s\" h=\"%s\"/>\n", type, name, path, iconW, iconH));
		}
		txt.append("</iconSet>");
		GamePacker.log("����������Ϣ");

		byte[] bytes = txt.toString().getBytes("UTF-8");
		byte[] cfgBytes = helper.convertXmlToAs(new ByteArrayInputStream(bytes), "$IconSet.xml2");
		if (cfgBytes != null)
		{
			bytes = cfgBytes;
		}
		else if (zip)
		{
			bytes = ZlibUtil.compress(bytes);
		}

		String checksum = (zip ? "zlib_md5" : "md5") + MD5Util.md5Bytes(bytes);
		exportFile(checksum, MD5Util.addSuffix(bytes), "cfg");
		GamePacker.endLogSet();

		if (isCancel())
		{
			return;
		}

		// ����qpurl.cvs
		File cfgDir = helper.getCfgFolder();
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
					if (getChecksumTable().getMD5(path) != null)
					{
						String key = getChecksumTable().getGID(path);
						if (hasExportedFile(key))
						{
							int itemID = item.id;
							String saveURL = getExportedFileUrl(key);
							if (saveURL.charAt(0) != '/')
							{
								saveURL = "/" + saveURL;
							}
							qpurl.append(itemID + "," + saveURL + "\r\n");
							break;
						}
					}
				}
			}

			FileUtil.writeFile(new File(getDestDir().getPath() + File.separator + "qpurl.cvs"), qpurl.toString().getBytes("UTF-8"));
		}

		// ������Ŀ����
		GamePacker.beginLogSet("���������Ϣ");
		GamePacker.log("���ɻ�����Ϣ");
		StringBuilder sb = new StringBuilder();
		sb.append("<project>\n");
		sb.append("\t<configs>\n");
		sb.append(String.format("\t\t<config name=\"%s\" path=\"%s\" size=\"%s\" />\n", cfgBytes != null ? "$IconSet.xml" : "iconSet", getExportedFileUrl(checksum), getExportedFileSize(checksum)));
		sb.append("\t</configs>\n");
		sb.append("</project>");
		GamePacker.log("���������Ϣ");
		FileUtil.writeFile(new File(getDestDir().getPath() + "/db.xml"), sb.toString().getBytes("UTF-8"));
		GamePacker.endLogSet();

		// �����ļ��б�
		GamePacker.beginLogSet("����ļ�����");
		GamePacker.log("�����ļ�����");
		StringBuilder filesSB = new StringBuilder();
		String[] urlList = getExportedFileUrls();
		for (String url : urlList)
		{
			filesSB.append(url + "\n");
		}
		GamePacker.log("�����ļ�����");
		FileUtil.writeFile(new File(getDestDir().getPath() + "/db.ver"), filesSB.toString().getBytes("UTF-8"));
		GamePacker.endLogSet();
	}

	/**
	 * ��ȡĿ¼
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
					ext = ext.toLowerCase();

					if (ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg"))
					{
						String innerPath = file.getPath().substring(getSourceDir().getPath().length()).replaceAll("\\\\", "/");

						GamePacker.progress("��ȡ�ļ�", innerPath);

						this.files.put(innerPath, file);
					}
				}
			}
		}
	}

	// -------------------------------------------------------------------------------------------------------------------------------

}
