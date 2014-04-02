package org.game.knight.version.packer.game;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Arrays;
import java.util.Hashtable;

import org.chw.util.FileUtil;
import org.chw.util.MD5Util;
import org.chw.util.XmlUtil;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.base.AbsExporter;


public class GameExporter extends AbsExporter
{
	private File appDir;

	private String clientUrl;
	private String clientVer;
	private String serverHost;
	private String serverPort;
	private String serverID;
	private String testList;
	private String userList;

	private Hashtable<String, File> files;

	/**
	 * ���캯��
	 * 
	 * @param src
	 * @param dst
	 */
	public GameExporter(File src, File dst, File appDir, String clientUrl, String clientVer, String params)
	{
		super("��������", src, dst);

		this.appDir = appDir;

		this.clientUrl = clientUrl;
		this.clientVer = clientVer;

		serverHost = "";
		serverPort = "";
		serverID = "";
		testList = "";
		userList = "";

		if (params != null && params.trim() != "")
		{
			params = params.trim();

			String[] paramList = params.split(" ");
			if (paramList.length > 0)
			{
				serverHost = paramList[0];
			}
			if (paramList.length > 1)
			{
				serverPort = paramList[1];
			}
			if (paramList.length > 2)
			{
				serverID = paramList[2];
			}
			if (paramList.length > 3)
			{
				testList = paramList[3];
			}
			if (paramList.length > 4)
			{
				userList = paramList[4];
			}
		}
	}

	@Override
	protected void exportContent() throws Exception
	{
		files = new Hashtable<String, File>();

		if (isCancel()) { return; }

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

		if (isCancel()) { return; }
		
		//����GameHead��GameBody
		StringBuilder codeSB=new StringBuilder();
		codeSB.append("\t<codes>\n");
		for(String url : urls)
		{
			File file=files.get(url);
			if(file.getName().equals("Index.swf") || file.getName().equals("GameHead.swf") || file.getName().equals("GameBody.swf") || file.getName().equals("Game.swf"))
			{
				codeSB.append(String.format("\t\t<code name=\"%s\">\n",getFileName(file)));
				
				// ��ֲ�����ļ�
				ByteArrayInputStream input = new ByteArrayInputStream(FileUtil.getFileBytes(file));
				
				int partSize = 500 * 1024;
				while (input.available() > 0)
				{
					byte[] part = new byte[Math.min(input.available(), partSize)];
					
					input.read(part);
					
					String md5 = MD5Util.md5Bytes(part);

					exportFile(md5, part, "part");
					
					codeSB.append(String.format("\t\t\t<part path=\"%s\" size=\"%s\"/>\n",getExportedFileUrl(md5),getExportedFileSize(md5)));
				}
				
				codeSB.append("\t\t</code>\n");
			}
		}
		codeSB.append("\t</codes>\n");
		

		// �����ļ�
		/*
		GamePacker.beginLogSet("����ļ�");
		for (String url : urls)
		{
			GamePacker.progress("����ļ�", url);
			if (files.get(url).getParentFile().getPath().equals(getSourceDir().getPath()))
			{
				exportFile(getChecksumTable().getChecksumID(url), files.get(url));
			}

			if (isCancel()) { return; }
		}
		GamePacker.endLogSet();
		*/

		if (isCancel()) { return; }

		// ��������
		GamePacker.beginLogSet("���������Ϣ");
		GamePacker.log("���ɻ�����Ϣ");
		/*StringBuilder txt1 = new StringBuilder();
		StringBuilder txt2 = new StringBuilder();
		txt1.append("\t<configs>\n");
		txt2.append("\t<games>\n");
		for (String url : urls)
		{
			File file = files.get(url);
			if (file.getParentFile().getPath().equals(getSourceDir().getPath()))
			{
				String name = getFileName(file);
				String checksum = getChecksumTable().getChecksumID(url);

				// ����ͳ�Ʊ�
				String ext = getFileExtName(file);
				if (!ext.equals("swf"))
				{
					txt1.append(String.format("\t\t<config name=\"%s\" path=\"%s\" size=\"%s\"/>\n", name, getExportedFileUrl(checksum), getExportedFileSize(checksum)));
				}
				else
				{
					txt2.append(String.format("\t\t<game name=\"%s\" path=\"%s\" size=\"%s\"/>\n", name, getExportedFileUrl(checksum), getExportedFileSize(checksum)));
				}
			}
		}
		txt1.append("\t</configs>\n");
		txt2.append("\t</games>\n");
		GamePacker.log("���������Ϣ");
		String text = "<project>\n"+codeSB.toString() + txt1.toString() + txt2.toString() + "</project>";*/
		String text = "<project>\n"+codeSB.toString() + "</project>";
		FileUtil.writeFile(new File(getDestDir().getPath() + "/db.xml"), text.toString().getBytes("UTF-8"));
		GamePacker.endLogSet();

		//�����ļ��б�
		GamePacker.beginLogSet("����ļ�����");
		GamePacker.log("�����ļ�����");
		StringBuilder filesSB = new StringBuilder();
		String[] urlList=getExportedFileUrls();
		for (String url : urlList)
		{
			filesSB.append(url + "\n");
		}
		GamePacker.log("�����ļ�����");
		FileUtil.writeFile(new File(getDestDir().getPath() + "/db.ver"), filesSB.toString().getBytes("UTF-8"));
		GamePacker.endLogSet();
		
		writeStartupFiles();
	}

	/**
	 * ��������ļ�
	 * 
	 * @throws Exception
	 */
	private void writeStartupFiles() throws Exception
	{
		if (appDir == null || (appDir.exists() && appDir.isFile())) { return; }
		if(appDir.getPath().isEmpty())
		{
			return;
		}
		GamePacker.beginLogSet("��������ļ�");
		
		if(!appDir.exists())
		{
			appDir.mkdirs();
		}
		
		for (String url : files.keySet())
		{
			File from = new File(this.getSourceDir().getPath() + url);
			File dest = new File(this.appDir.getPath() + url);

			if (from.exists() && (from.getParentFile().getPath().equals(getSourceDir().getPath()) == false || (from.getParentFile().getPath().equals(getSourceDir().getPath()) && (from.getName().equals("Index.swf") || from.getName().equals("index.html")))))
			{
				GamePacker.progress("�����ļ�", url);
				FileUtil.copyTo(dest, from);
			}
		}

		rebuildClientXML(new File(appDir.getPath() + "/GameConfigs/client.xml"));
		rebuildServerXML(new File(appDir.getPath() + "/GameConfigs/server.xml"));
		rebuildFlashPlayerTrust();

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

		if (files == null) { return; }

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

				GamePacker.progress("��ȡ�ļ�", innerPath);

				this.files.put(innerPath, file);
			}
		}
	}

	private void rebuildFlashPlayerTrust() throws Exception
	{
		File redmin = new File(appDir.getPath() + File.separatorChar + "TrustGameDir.txt");

		String text = "";
		text += "# 32λϵͳ����Ѵ��ļ����Ƶ�: C:\\WINDOWS\\system32\\Macromed\\Flash\\FlashPlayerTrust Ŀ¼��\r\n";
		text += "# 64λϵͳ����Ѵ��ļ����Ƶ�: C:\\Windows\\SysWOW64\\Macromed\\Flash\\FlashPlayerTrust Ŀ¼��\r\n";
		text += appDir.getPath();

		FileUtil.writeFile(redmin, text.getBytes("UTF-8"));

		GamePacker.warning("������Ҫ����FlashPlayer����Ŀ¼��", "���÷�����:" + redmin.getPath());
	}

	private void rebuildClientXML(File file) throws Exception
	{
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("config");

		Element client = root.addElement("client");
		client.addAttribute("ver", clientVer);
		client.addAttribute("stand", "false");
		client.addAttribute("authURL", "http://192.168.0.127/qzone/auth.php");

		Element url = client.addElement("url");
		url.addAttribute("path", clientUrl);

		String[] tests = testList.split(",");
		for (int i = 0; i < tests.length; i++)
		{
			Element testNode = client.addElement("tester");
			testNode.addAttribute("name", tests[i]);
		}

		String[] users = userList.split(",");
		for (int i = 0; i < users.length; i++)
		{
			Element userNode = client.addElement("developer");
			userNode.addAttribute("name", users[i]);
		}

		FileUtil.writeFile(file, XmlUtil.formatXML(document.asXML()).getBytes("UTF-8"));
	}

	private void rebuildServerXML(File file) throws Exception
	{
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("config");
		Element server = root.addElement("server");

		server.addAttribute("host", serverHost);
		server.addAttribute("port", serverPort);
		server.addAttribute("world", serverID);

		FileUtil.writeFile(file, XmlUtil.formatXML(document.asXML()).getBytes("UTF-8"));
	}

}
