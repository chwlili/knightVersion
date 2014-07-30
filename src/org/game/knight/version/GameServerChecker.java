package org.game.knight.version;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

public class GameServerChecker
{
	/**
	 * �������б���ַ
	 */
	private static final String listURL = "http://app1101079868.openwebgame.qq.com/SelectServer/qzone?appid=1101079868";

	private ArrayList<GameArea> areas;
	private String text;
	private String message;
	private boolean runing;
	
	private MyThread thread;
	
	/**
	 * ��ʼ
	 */
	public void start()
	{
		runing = true;
		
		areas=null;
		
		thread=new MyThread();
		thread.start();
	}
	
	/**
	 * ֹͣ
	 */
	public void stop()
	{
		runing=false;
	}
	
	/**
	 * �Ƿ���������
	 */
	public boolean isRuning()
	{
		return runing;
	}
	
	public GameArea[] getData()
	{
		if(areas!=null)
		{
			return areas.toArray(new GameArea[areas.size()]);
		}
		return null;
	}
	
	public String getText()
	{
		return text;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	
	
	public static class GameArea
	{
		public String serverID;
		public String serverName;
		public String clientXML;
		public String serverXML;
		public String host;
		public String port;
		public String world;
		public String version;

		public GameArea(String serverID, String serverName, String clientXML, String serverXML)
		{
			this.serverID = serverID;
			this.serverName = serverName;
			this.clientXML = clientXML;
			this.serverXML = serverXML;
		}
	}

	private class MyThread extends Thread
	{
		private boolean isStoped()
		{
			return !runing || thread!=this;
		}
		
		@Override
		public void run()
		{
			ArrayList<GameArea> datas=new ArrayList<GameArea>();
			
			update("��ȡ�������б�...");
			
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e1)
			{
				e1.printStackTrace();
			}
			
			String text = readWebContent(listURL);

			if (isStoped())
			{
				runing=false;
				return;
			}

			if (text == null)
			{
				error("��ȡ�������б�ʧ��", listURL);
				return;
			}
			
			HashSet<String> ids=new HashSet<String>();
			String prev = "javascript:loginNew(1101079868,";
			int fromIndex = 0;
			while (true)
			{
				int from = text.indexOf(prev, fromIndex);
				if (from == -1)
				{
					break;
				}
				from = from + prev.length();

				int dest = text.indexOf(",", from);
				if (dest == -1)
				{
					break;
				}

				String sid = text.substring(from, dest);

				from = text.indexOf("<em", dest);
				if (from == -1)
				{
					break;
				}
				from += 3;

				from = text.indexOf(">", from);
				if (from == -1)
				{
					break;
				}
				from += 1;

				dest = text.indexOf("<", from);
				if (dest == -1)
				{
					break;
				}

				String serverID = sid;
				String serverName = text.substring(from, dest);
				String clientXML = "http://s" + serverID + ".app1101079868.qqopenapp.com/publish/GameConfigs/client.xml";
				String serverXML = "http://s" + serverID + ".app1101079868.qqopenapp.com/publish/GameConfigs/server.xml";
				if(!ids.contains(serverID))
				{
					ids.add(serverID);
					datas.add(new GameArea(serverID, serverName, clientXML, serverXML));
				}

				fromIndex = dest;
			}

			if (isStoped())
			{
				runing=false;
				return;
			}

			for (int i = 0; i < datas.size(); i++)
			{
				if (isStoped())
				{
					runing=false;
					return;
				}

				GameArea area = datas.get(i);

				update("��ȡ\"" + area.serverName + "\"�� server.xml (" + (i * 2 + 1) + "/" + (datas.size() * 2) + ")");

				String content = readWebContent(area.serverXML);
				if (isStoped())
				{
					runing=false;
					return;
				}
				if (content != null)
				{
					try
					{
						Document document = DocumentHelper.parseText(content);
						Node node = (Element) document.selectSingleNode("/config/server");
						if (node instanceof Element)
						{
							Element element = (Element) node;
							String host = element.attributeValue("host");
							String port = element.attributeValue("port");
							String world = element.attributeValue("world");

							area.host = host;
							area.port = port;
							area.world = world;
						}
					}
					catch (DocumentException e)
					{
						e.printStackTrace();
						error("����\"" + area.serverName + "\"��server.xml����!", e.getMessage());
						return;
					}
				}
				else
				{
					error("��ȡ\"" + area.serverName + "\"��server.xmlʧ��!", area.serverXML);
					return;
				}

				update("��ȡ\"" + area.serverName + "\"�� client.xml (" + (i * 2 + 2) + "/" + (datas.size() * 2) + ")");

				content = readWebContent(area.clientXML);
				if (isStoped())
				{
					runing=false;
					return;
				}
				if (content != null)
				{
					int index = content.indexOf("<config>");
					if (index != -1)
					{
						content = content.substring(index);
					}
					try
					{
						Document document = DocumentHelper.parseText(content);
						Node node = (Element) document.selectSingleNode("/config/client");
						if (node instanceof Element)
						{
							Element element = (Element) node;
							String ver = element.attributeValue("ver");

							area.version = ver;
						}
					}
					catch (DocumentException e)
					{
						e.printStackTrace();
						error("����\"" + area.serverName + "\"��client.xml����!", e.getMessage());
						return;
					}
				}
				else
				{
					error("��ȡ\"" + area.serverName + "\"��client.xmlʧ��!", area.serverXML);
					return;
				}
			}

			areas=datas;
			
			finish();
			runing = false;
		}

		private void error(final String status, final String error)
		{
			text = status;
			message = error;
		}

		private void update(final String status)
		{
			text = status;
			message = "";
		}

		private void finish()
		{
			text = "�����";
			message = "";
		}

		private String readWebContent(String path)
		{
			try
			{
				URL url = new URL(path);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");// ���ӱ�ͷ��ģ�����������ֹ����
				conn.setRequestProperty("Accept", "text/html");// ֻ����text/html���ͣ���ȻҲ���Խ���ͼƬ,pdf,*/*���⣬����tomcat/conf/web���涨����Щ
				conn.setConnectTimeout(5000);

				if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
				{
					InputStream input = conn.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));

					String line = null;
					StringBuffer sb = new StringBuffer();
					while ((line = reader.readLine()) != null)
					{
						sb.append(line).append("\r\n");
					}
					if (reader != null)
					{
						reader.close();
					}
					if (conn != null)
					{
						conn.disconnect();
					}

					return sb.toString();
				}
			}
			catch (Exception e)
			{
			}

			return null;
		}
	}
}
