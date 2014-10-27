package org.game.knight.version.packer.game;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import org.chw.util.FileUtil;
import org.chw.util.MD5Util;
import org.chw.util.XmlUtil;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.GamePackerHelper;
import org.game.knight.version.packer.base.ZipConfig;

public class GameExporter
{
	private GamePackerHelper helper;
	private File outputFolder;

	private ZipConfig oldZip;
	private ZipConfig newZip;

	private File appDir;

	private String clientUrl;
	private String clientVer;
	private String serverHost;
	private String serverPort;
	private String serverID;
	private String testList;
	private String userList;

	/**
	 * 构造函数
	 * 
	 * @param src
	 * @param dst
	 */
	public GameExporter(GamePackerHelper helper, File dst, File appDir, String clientUrl, String clientVer, String params)
	{
		this.appDir = appDir;

		this.helper = helper;
		this.outputFolder = dst;

		this.oldZip = new ZipConfig(new File(dst + File.separator + "ver.zip"));
		this.newZip = new ZipConfig();

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

	public boolean pub()
	{
		try
		{
			GamePacker.beginTask("处理代码");

			StringBuilder sb = new StringBuilder();
			sb.append("<project>\n");
			sb.append("\t<codes>\n");
			File[] files = helper.listFiles(helper.codeInputFolder, "swf");
			for (int i = 0; i < files.length; i++)
			{
				File file = files[i];

				String url = file.getPath().substring(helper.codeInputFolder.getPath().length()).replaceAll("\\\\", "/");
				if (file.getName().equals("Index.swf") || file.getName().equals("Game.swf") || file.getName().equals("GameHead.swf") || file.getName().equals("GameBody.swf"))
				{
					GamePacker.progress(String.format("处理代码(%s/%s)：%s", i + 1, files.length, url));

					ByteArrayInputStream input = new ByteArrayInputStream(FileUtil.getFileBytes(file));
					int partSize = 500 * 1024;
					sb.append(String.format("\t\t<code name=\"%s\">\n", helper.getFileName(file)));
					while (input.available() > 0)
					{
						byte[] part = new byte[Math.min(input.available(), partSize)];

						input.read(part);

						part = MD5Util.addSuffix(part);

						String pairMD5 = MD5Util.md5Bytes(part);
						String pairURL = oldZip.getGameFiles().get(pairMD5);
						if (pairURL == null)
						{
							pairURL = oldZip.getVersionNextGameFileURL("part");

							FileUtil.writeFile(new File(outputFolder.getPath() + pairURL), part);
						}

						newZip.getGameFiles().put(pairMD5, pairURL);
						newZip.getVersionFiles().add("/" + outputFolder.getName() + pairURL);

						sb.append(String.format("\t\t\t<part path=\"%s\" size=\"%s\"/>\n", "/" + outputFolder.getName() + pairURL, part.length));
					}

					sb.append("\t\t</code>\n");
				}
			}
			sb.append("\t</codes>\n");
			sb.append("</project>");

			newZip.setVersion(sb.toString());
			newZip.setVersionProps(oldZip.getVersionProps());
			newZip.getCfgFiles().put("$CodeText.xml", writeLangs().getBytes("UTF-8"));
			newZip.getVersionFiles().add("/" + oldZip.getFile().getName());
			newZip.saveTo(oldZip.getFile());

			writeStartupFiles();

			GamePacker.log("完成");

			return true;
		}
		catch (Exception e)
		{
			GamePacker.error(e);
		}
		finally
		{
			GamePacker.endTask();
		}

		return false;
	}

	private String writeLangs() throws FileNotFoundException, UnsupportedEncodingException
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<codeTexts>\n");
		for (File swf : helper.listFiles(helper.codeInputFolder, "swf"))
		{
			if (swf.getName().equals("GameNLS.swf"))
			{
				String[] txts = TextFinder.find(swf);

				for (String txt : txts)
				{
					sb.append("\t<text>\n");
					sb.append("\t\t<key><![CDATA[>" + txt + "]]></key>\n");
					sb.append("\t\t<val><![CDATA[" + txt + "]]></val>\n");
					sb.append("\t</text>\n");
				}
			}
		}
		sb.append("</codeTexts>");
		return sb.toString();
	}

	/**
	 * 输出启动文件
	 * 
	 * @throws Exception
	 */
	private void writeStartupFiles() throws Exception
	{
		if (appDir == null || (appDir.exists() && appDir.isFile()))
		{
			return;
		}
		if (appDir.getPath().isEmpty())
		{
			return;
		}
		GamePacker.beginLogSet("输出启动文件");

		if (!appDir.exists())
		{
			appDir.mkdirs();
		}

		for (File from : helper.listFiles(helper.codeInputFolder, "*"))
		{
			String url = from.getPath().substring(helper.codeInputFolder.getPath().length()).replaceAll("\\\\", "/");
			File dest = new File(this.appDir.getPath() + url);

			if (from.exists() && (from.getParentFile().getPath().equals(helper.codeInputFolder.getPath()) == false || (from.getParentFile().getPath().equals(helper.codeInputFolder.getPath()) && (from.getName().equals("Index.swf") || from.getName().equals("index.html")))))
			{
				GamePacker.progress("复制文件", url);
				FileUtil.copyTo(dest, from);
			}
		}

		rebuildClientXML(new File(appDir.getPath() + "/GameConfigs/client.xml"));
		rebuildServerXML(new File(appDir.getPath() + "/GameConfigs/server.xml"));
		rebuildFlashPlayerTrust();

		GamePacker.endLogSet();
	}

	private void rebuildFlashPlayerTrust() throws Exception
	{
		File redmin = new File(appDir.getPath() + File.separatorChar + "TrustGameDir.txt");

		String text = "";
		text += "# 32位系统，请把此文件复制到: C:\\WINDOWS\\system32\\Macromed\\Flash\\FlashPlayerTrust 目录下\r\n";
		text += "# 64位系统，请把此文件复制到: C:\\Windows\\SysWOW64\\Macromed\\Flash\\FlashPlayerTrust 目录下\r\n";
		text += appDir.getPath();

		FileUtil.writeFile(redmin, text.getBytes("UTF-8"));

		GamePacker.warning("可能需要设置FlashPlayer信任目录！", "设置方法见:" + redmin.getPath());
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
