package org.game.knight.version.packer.view;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.chw.swf.clear.SwfTagClear;
import org.chw.util.FileUtil;
import org.chw.util.MD5Util;
import org.dom4j.Element;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.base.AbsExporter;

public class ViewExport extends AbsExporter
{
	private boolean swfZip;
	
	/**
	 * ���캯��
	 * 
	 * @param src
	 * @param dst
	 */
	public ViewExport(File src, File dst,boolean swfZip)
	{
		super("������ͼ", src, dst);
		
		this.swfZip=swfZip;
	}

	// -------------------------------------------------------------------------------
	//
	// �汾��Ϣ
	//
	// -------------------------------------------------------------------------------

	private ClassIDTable classIDTable;

	@Override
	protected void openVers()
	{
		super.openVers();

		classIDTable = new ClassIDTable(this);
		classIDTable.open(new File(getDestDir().getPath() + "/.ver/classID"));
	}

	@Override
	protected void saveVers()
	{
		super.saveVers();

		classIDTable.save();
	}

	/**
	 * ��ȡ����ID��
	 * 
	 * @return
	 */
	public ClassIDTable getClassIDTable()
	{
		return classIDTable;
	}

	// -------------------------------------------------------------------------------
	//
	// ����
	//
	// -------------------------------------------------------------------------------

	private Hashtable<String, ViewFile> fileTable;
	private Hashtable<String, ViewFileGroup> fileBags;

	/**
	 * ��·����ȡ��ͼ�ļ�
	 * 
	 * @param path
	 * @return
	 */
	public ViewFile getViewBy(String path)
	{
		return fileTable.get(path);
	}

	/**
	 * ���������û�ȡ�ļ���
	 * 
	 * @param crossRef
	 * @return
	 */
	public ViewFileGroup getBagBy(String crossRef)
	{
		if (!fileBags.containsKey(crossRef))
		{
			fileBags.put(crossRef, new ViewFileGroup());
		}
		return fileBags.get(crossRef);
	}

	/**
	 * �������ļ�
	 * 
	 * @param file
	 * @throws Exception
	 */
	public void addOutputFile(ViewFile file) throws Exception
	{
		String key = getChecksumTable().getChecksumID(file.getInnerPath());

		boolean exists = hasExportedFile(key);

		exportFile(key, file.getFile());

		if (!exists)
		{
			String ext = getFileExtName(file.getFile());
			if (ext.toLowerCase().equals("swf"))
			{
				SwfTagClear.clearSwfFile(getExportedFile(key));
			}
		}

		GamePacker.progress("����ļ�", file.getInnerPath());
	}

	/**
	 * ��ȡ����ļ��ĵ�ַ
	 * 
	 * @param file
	 * @return
	 */
	public String getOutputFileURL(ViewFile file)
	{
		String key = getChecksumTable().getChecksumID(file.getInnerPath());

		return getExportedFileUrl(key);
	}

	/**
	 * ��������
	 * 
	 * @param bag
	 * @throws Exception
	 */
	public void addOutputBag(ViewFileBag bag) throws Exception
	{
		if (!hasExportedFile(bag.getKey()))
		{
			exportFile(bag.getKey(), bag.build(swfZip), "swf");
			//SwfTagClear.clearSwfFile(getExportedFile(bag.getKey()));
		}
		GamePacker.progress("�����ļ�", getExportedFileUrl(bag.getKey()));
	}

	@Override
	protected void exportContent() throws Exception
	{
		fileTable = new Hashtable<String, ViewFile>();
		fileBags = new Hashtable<String, ViewFileGroup>();

		if (isCancel())
		{
			return;
		}

		// ��ȡ�ļ�
		GamePacker.log("��ȡ�ļ�");
		readDir(getSourceDir());

		if (isCancel())
		{
			return;
		}

		// �������ļ�������ʼ������
		GamePacker.beginLogSet("�����ļ�");
		for (ViewFile file : fileTable.values())
		{
			GamePacker.progress("�����ļ�", file.getInnerPath());
			file.open(this);

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

		// �ϲ��ļ��ļ̳�����
		GamePacker.beginLogSet("�ع��ļ�");
		for (ViewFile file : fileTable.values())
		{
			GamePacker.progress("�ع��ļ�", file.getInnerPath());
			file.mergerInheritRef(this);

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

		// ���㽻������
		GamePacker.beginLogSet("��������");
		for (ViewFile file : fileTable.values())
		{
			GamePacker.progress("��������", file.getInnerPath());
			file.measureCrossRef();

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

		// ȷ���ļ��ְ�����ʼ������ID
		GamePacker.beginLogSet("��������");
		for (ViewFile file : fileTable.values())
		{
			GamePacker.progress("��������", file.getInnerPath());
			file.initClassID(this);

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

		// �����ļ���
		GamePacker.log("�����ļ�");
		for (ViewFileGroup group : fileBags.values())
		{
			group.buildBags(this);

			if (isCancel())
			{
				return;
			}
		}

		if (isCancel())
		{
			return;
		}

		// writeDB
		writeDB();
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
				String innerPath = file.getPath().substring(getSourceDir().getPath().length()).replaceAll("\\\\", "/");
				String innerDirPath = file.getParentFile().getPath().substring(getSourceDir().getPath().length()).replaceAll("\\\\", "/");

				GamePacker.progress("��ȡ�ļ�", innerPath);

				fileTable.put(innerPath, new ViewFile(innerDirPath, innerPath, file));
			}
		}
	}

	private void writeDB() throws Exception
	{
		GamePacker.beginLogSet("���������Ϣ");
		GamePacker.log("���ɻ�����Ϣ");

		// ������ͼ�ļ�
		ViewFile[] list = new ViewFile[fileTable.size()];
		list = fileTable.values().toArray(list);
		Arrays.sort(list, new Comparator<ViewFile>()
		{
			@Override
			public int compare(ViewFile o1, ViewFile o2)
			{
				return o1.getInnerPath().compareTo(o2.getInnerPath());
			}
		});

		// �ϲ������ļ�
		mergerLanguages(list);

		// �������԰�
		StringBuilder langFileListXML = new StringBuilder();
		langFileListXML.append("\t<viewLangs>\n");
		StringBuilder viewFileListXML = new StringBuilder();
		File langDir = new File(getSourceDir().getPath() + File.separatorChar + "langs");
		if (langDir == null || !langDir.exists() || langDir.listFiles().length == 0)
		{
			FileUtil.writeFile(new File(langDir.getPath() + File.separatorChar + "cn.txt"), "");
		}

		File[] langFiles = langDir.listFiles();
		for (int i = 0; i < langFiles.length; i++)
		{
			File langFile = langFiles[i];
			String langName = langFile.getName().replaceAll("\\.txt", "");

			StringBuilder langXML = new StringBuilder();
			langXML.append("<xml>\n");
			langXML.append("\t<texts>\n");

			String langFileContent = "";
			byte[] bytes = FileUtil.getFileBytes(langFile);
			if (bytes != null)
			{
				langFileContent = new String(FileUtil.getFileBytes(langFile), "utf8");
				;
			}

			String[] lines = langFileContent.split("\\n\\$");
			for (String line : lines)
			{
				int index = line.indexOf("=");
				if (index != -1)
				{
					String key = line.substring(0, index).trim();
					String val = line.substring(index + 1).trim();

					langXML.append("\t\t<text id=\"" + key + "\"><![CDATA[" + val + "]]></text>\n");
				}
			}
			langXML.append("\t</texts>\n");
			langXML.append("</xml>");

			byte[] bagContent = langXML.toString().getBytes("UTF-8");
			String bagSHA = MD5Util.md5Bytes(bagContent);
			long classID = getClassIDTable().getClassID(bagSHA + ".lang");

			addOutputBag(new ViewLangBag(bagSHA, classID, bagContent));

			String bagURL = getExportedFileUrl(bagSHA);
			long bagSize = getExportedFileSize(bagSHA);

			langFileListXML.append("\t\t<viewLang lang=\"" + langName + "\" url=\"/langs/" + langName + ".xml\" size=\"" + bagSize + "\" file=\"" + bagURL + "\"/>\n");

			viewFileListXML.append("\t\t<view name=\"\" url=\"/langs/" + langName + ".xml\" id=\"FILE_" + classID + "\" size=\"" + bagSize + "\" files=\"" + bagURL + "\"/>\n");
		}
		langFileListXML.append("\t</viewLangs>\n");

		Hashtable<Integer, Integer> method_size = new Hashtable<Integer, Integer>();
		Hashtable<Integer, HashSet<String>> method_files = new Hashtable<Integer, HashSet<String>>();

		// ׼���������
		StringBuilder descXML = new StringBuilder();
		descXML.append("<project>\n");
		descXML.append(langFileListXML.toString());
		descXML.append("\t<views>\n");
		descXML.append(viewFileListXML.toString());
		for (ViewFile file : list)
		{
			if (file.isCfg())
			{
				// ��ȡ�ڲ�·��
				String url = file.getInnerPath();

				// �ܼƴ�С
				long bagSize = 0;

				// ��ȡ���������ļ�����ID
				HashSet<String> swfKeys = new HashSet<String>();
				ArrayList<String> swfUrls = new ArrayList<String>();
				for (ViewFileGroup group : file.getDependViewFileGroups())
				{
					for (ViewFileBag bag : group.getBags())
					{
						if (!swfKeys.contains(bag.getKey()))
						{
							String bagKey = bag.getKey();
							String bagURL = getExportedFileUrl(bagKey);
							long bagLen = getExportedFileSize(bagKey);

							swfKeys.add(bagKey);
							swfUrls.add(bagURL);
							bagSize += bagLen;

							if (file.getPreloadMethod() != null && file.getPreloadMethod().length > 0)
							{
								int size = 0;
								int[] methods = file.getPreloadMethod();
								for (int method : methods)
								{
									if(method==-1)
									{
										continue;
									}
									
									if (method_size.containsKey(method))
									{
										size = method_size.get(method);
									}
									if (!method_files.containsKey(method))
									{
										method_files.put(method, new HashSet<String>());
									}

									if (!method_files.get(method).contains(bagURL))
									{
										method_files.get(method).add(bagURL);
										method_size.put(method, (int) (size + bagLen));
									}
								}
							}
						}
					}
				}
				for (ViewFileSwf swf : file.getDependViewSwfs())
				{
					if (!swfKeys.contains(swf.getKey()))
					{
						String bagKey = swf.getKey();
						String bagURL = getExportedFileUrl(getChecksumTable().getChecksumID(swf.getFile().getInnerPath()));
						long bagLen = getExportedFileSize(getChecksumTable().getChecksumID(swf.getFile().getInnerPath()));

						swfKeys.add(bagKey);
						swfUrls.add(bagURL);
						bagSize += bagLen;

						if (file.getPreloadMethod()!=null && file.getPreloadMethod().length>0)
						{
							int size = 0;
							int[] methods = file.getPreloadMethod();
							for(int method:methods)
							{
								if(method==-1)
								{
									continue;
								}
								
								if (method_size.containsKey(method))
								{
									size = method_size.get(method);
								}
								if (!method_files.containsKey(method))
								{
									method_files.put(method, new HashSet<String>());
								}
	
								if (!method_files.get(method).contains(bagURL))
								{
									method_files.get(method).add(bagURL);
									method_size.put(method, (int) (size + bagLen));
								}
							}
						}
					}
				}

				// �����ļ���
				Collections.sort(swfUrls);

				// ����ļ�·����
				StringBuilder urls = new StringBuilder();
				for (String path : swfUrls)
				{
					urls.append(urls.length() == 0 ? path : "," + path);
				}

				// ��¼��Ϣ
				StringBuilder sb=new StringBuilder();
				for(int typeID:file.getPreloadMethod())
				{
					if(sb.length()>0)
					{
						sb.append(",");
					}
					sb.append(typeID);
				}
				descXML.append("\t\t<view preload=\"" + sb.toString() + "\" name=\"" + file.getBagName() + "\" url=\"" + url + "\" id=\"FILE_" + file.getClassID() + "\" size=\"" + bagSize + "\" files=\"" + urls.toString() + "\"/>\n");
			}
		}
		descXML.append("\t</views>\n");
		descXML.append("\t<viewBags>\n");
		Integer[] methodIDs = method_files.keySet().toArray(new Integer[method_files.keySet().size()]);
		Arrays.sort(methodIDs);
		for (Integer method : methodIDs)
		{
			int size = method_size.get(method);
			HashSet<String> urls = method_files.get(method);
			String[] paths = urls.toArray(new String[urls.size()]);
			Arrays.sort(paths);

			StringBuilder sb = new StringBuilder();
			for (String path : paths)
			{
				if (sb.length() > 0)
				{
					sb.append(",");
				}
				sb.append(path);

				// File viewFrom = new
				// File(getDestDir().getParentFile().getPath() + path);
				// File viewDest = new File(getDestDir().getPath() +
				// "/.ver/preloads/" + method + "/" + viewFrom.getName());
				// FileUtil.copyTo(viewDest, viewFrom);
			}
			descXML.append(String.format("\t\t<viewBagFiles type=\"%s\" size=\"%s\" files=\"%s\" />\n", method, size, sb.toString()));
		}
		descXML.append("\t</viewBags>\n");
		descXML.append("</project>");

		GamePacker.log("���������Ϣ");
		FileUtil.writeFile(new File(getDestDir().getPath() + "/db.xml"), descXML.toString().getBytes("UTF-8"));
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
	 * �ϲ������ļ�
	 * 
	 * @param list
	 * @throws Exception
	 */
	private void mergerLanguages(ViewFile[] list) throws Exception
	{
		// ͳ���µ�������
		Hashtable<String, String> newLangContentTable = new Hashtable<String, String>();
		for (ViewFile file : list)
		{
			if (file.isCfg())
			{
				Element dom = file.getTextsNode();

				if (dom != null)
				{
					@SuppressWarnings("rawtypes")
					List nodes = dom.selectNodes("text");
					for (int i = 0; i < nodes.size(); i++)
					{
						Element node = (Element) nodes.get(i);
						String key = node.attributeValue("id");
						String val = node.getText();
						newLangContentTable.put(key, val);
					}
				}
			}
		}

		String[] newLangKeys = newLangContentTable.keySet().toArray(new String[newLangContentTable.size()]);
		Arrays.sort(newLangKeys);

		// �ϲ��������ļ�
		File langDIR = new File(getSourceDir().getPath() + File.separatorChar + "langs" + File.separatorChar);
		if (!langDIR.exists())
		{
			FileUtil.writeFile(new File(langDIR.getPath() + File.separatorChar + "cn.txt"), "");
		}
		File[] langFiles = langDIR.listFiles();
		for (File langFile : langFiles)
		{
			if (langFile.isFile() && !langFile.isHidden())
			{
				String langContent = new String(FileUtil.getFileBytes(langFile), "UTF-8");

				ArrayList<String> mergeredContentKeys = new ArrayList<String>();
				Hashtable<String, String> mergeredContentTable = new Hashtable<String, String>();

				if (langContent != null && !langContent.isEmpty())
				{
					String[] segments = langContent.split("\\r\\n$");
					for (String segment : segments)
					{
						int index = segment.indexOf("=");
						String txtKey = segment.substring(0, index);
						String txtVal = segment.substring(index + 1);

						if (newLangContentTable.containsKey(txtKey))
						{
							txtKey = txtKey.trim();
							txtVal = txtVal.trim();

							mergeredContentKeys.add(txtKey);
							mergeredContentTable.put(txtKey, txtVal);
						}
					}
				}

				for (String newLangKey : newLangKeys)
				{
					if (!mergeredContentTable.containsKey(newLangKey))
					{
						mergeredContentKeys.add(newLangKey);
						mergeredContentTable.put(newLangKey, newLangContentTable.get(newLangKey));
					}
				}

				StringBuilder mergeredLangContent = new StringBuilder();
				for (String mergeredLangKey : mergeredContentKeys)
				{
					if (mergeredLangContent.length() > 0)
					{
						mergeredLangContent.append("\r\n$");
					}
					mergeredLangContent.append(mergeredLangKey + " = " + mergeredContentTable.get(mergeredLangKey));
				}

				String newLangContent = mergeredLangContent.toString();
				if (!newLangContent.equals(langContent))
				{
					FileUtil.writeFile(langFile, newLangContent.getBytes("UTF-8"));
				}
			}
		}
	}
}
