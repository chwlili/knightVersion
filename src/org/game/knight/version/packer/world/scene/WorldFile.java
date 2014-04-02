package org.game.knight.version.packer.world.scene;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.chw.util.PathUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.world.WorldExporter;


public class WorldFile
{
	private File file;
	private String innerPath;
	private String innerDirPath;

	private Document document = null;
	
	private WorldCity[] citys;
	
	private ArrayList<SceneLink> links=new ArrayList<SceneLink>();
	private Hashtable<String, ArrayList<SceneLink>> fromToLink=new Hashtable<String, ArrayList<SceneLink>>();
	private Hashtable<String, ArrayList<SceneLink>> destToLink=new Hashtable<String, ArrayList<SceneLink>>();
	
	/**
	 * ���캯��
	 * @param file
	 * @param innerPath
	 * @param innerDirPath
	 */
	public WorldFile(File file,String innerPath,String innerDirPath)
	{
		this.file=file;
		this.innerPath=innerPath;
		this.innerDirPath=innerDirPath;
		
		openDocument();
		buildLinks();
	}
	
	/**
	 * ��ȡ�ڲ�·��
	 * @return
	 */
	public String getInnerPath()
	{
		return innerPath;
	}
	
	/**
	 * ��ȡ�ڲ�Ŀ¼·��
	 * @return
	 */
	public String getInnerDirPath()
	{
		return innerDirPath;
	}
	
	/**
	 * ��ȡ����
	 * @return
	 */
	public WorldCity[] getCitys()
	{
		return citys;
	}
	
	/**
	 * ��ȡ����
	 * @return
	 */
	public SceneLink[] getLinks()
	{
		SceneLink[] links=new SceneLink[this.links.size()];
		links=this.links.toArray(links);
		return links;
	}
	
	/**
	 * ������ȡ����
	 * @param hash
	 * @return
	 */
	public ArrayList<SceneLink> getLinksByFrom(String hash)
	{
		return fromToLink.get(hash);
	}
	
	/**
	 * ��Ŀ����ȡ����
	 * @param hash
	 * @return
	 */
	public ArrayList<SceneLink> getLinksByDest(String hash)
	{
		return destToLink.get(hash);
	}
	
	/**
	 * ���ĵ�
	 * @param dom
	 */
	private void openDocument()
	{
		try
		{
			SAXReader reader = new SAXReader();
			document = reader.read(file);
		}
		catch (DocumentException e)
		{
			//world.writeError("�����ļ�����ʧ��!\t"+e.getMessage());
			return;
		}
	}
	
	/**
	 * ��������
	 * 
	 */
	private void buildLinks()
	{
		if(document==null)
		{
			return;
		}
		
		@SuppressWarnings({ "rawtypes" })
		List nodes = document.selectNodes("linkData/links/link");
		for (int i = 0; i < nodes.size(); i++)
		{
			Element node = (Element) nodes.get(i);

			//������յ�
			String from = node.attributeValue("from");
			String dest = node.attributeValue("dest");
			
			//��������
			String achieve = node.attributeValue("restrict");
			String finishQuests = node.attributeValue("quests");
			String acceptQuests = node.attributeValue("currQuests");
			int level = Integer.parseInt(node.attributeValue("destLevel"));
			
			//��������
			SceneLink link=new SceneLink(achieve,finishQuests,acceptQuests,level);
			
			//��������
			if(!fromToLink.containsKey(from))
			{
				fromToLink.put(from, new ArrayList<SceneLink>());
			}
			fromToLink.get(from).add(link);
			
			//��Ŀ������
			if(!destToLink.containsKey(dest))
			{
				destToLink.put(dest, new ArrayList<SceneLink>());
			}
			destToLink.get(dest).add(link);
			
			//�����б�
			links.add(link);
		}
	}
	
	/**
	 * �ϲ�������Ϣ
	 */
	public void open(WorldExporter export)
	{
		ArrayList<WorldCity> cityList=new ArrayList<WorldCity>();
		
		//��ȡ����
		@SuppressWarnings({ "rawtypes" })
		List cityNodes = document.selectNodes("linkData/citys/city");
		for (int i = 0; i < cityNodes.size(); i++)
		{
			Element cityNode = (Element) cityNodes.get(i);

			// ���л�����Ϣ
			int cityID = Integer.parseInt(cityNode.attributeValue("id"));
			String cityName = cityNode.attributeValue("name");
			String citySound = PathUtil.rebuildPath(cityNode.attributeValue("sound"));
			//ImgFile cityIcon = getCityIcon(world, cityID);
			ArrayList<WorldScene> sceneList=new ArrayList<WorldScene>();
			
			
			// ��������
			@SuppressWarnings({ "rawtypes" })
			List sceneNodes = cityNode.selectNodes("scene");
			for (int j = 0; j < sceneNodes.size(); j++)
			{
				Element sceneNode = (Element) sceneNodes.get(j);

				// ����������Ϣ
				int sceneID = Integer.parseInt(sceneNode.attributeValue("id"));
				int timeLimit=sceneNode.attribute("maxTime")!=null ? Integer.parseInt(sceneNode.attributeValue("maxTime")):0;
				int sceneType = Integer.parseInt(sceneNode.attributeValue("type"));
				String sceneName = sceneNode.attributeValue("name");
				int sceneGroup = Integer.parseInt(sceneNode.attributeValue("group"));
				String sceneSound = PathUtil.rebuildPath(sceneNode.attributeValue("sound"));
				String scenePath = PathUtil.rebuildPath(sceneNode.attributeValue("scenePath"));
				
				if(sceneSound==null || sceneSound.isEmpty())
				{
					sceneSound=citySound;
				}
				
				Scene scene=export.getSceneFile(scenePath);
				if(scene!=null)
				{
					scene.setCityID(cityID);
					scene.setCityName(cityName);
					scene.setSceneID(sceneID);
					scene.setTimeLimit(timeLimit);
					scene.setSceneName(sceneName);
					scene.setSceneType(sceneType);
					scene.setSceneGroup(sceneGroup);
					
					if(sceneSound!=null && !sceneSound.isEmpty())
					{
						scene.setBackSound(export.getMp3File(sceneSound));
					}
					
					sceneList.add(new WorldScene(cityID, cityName, sceneID, sceneName, sceneType, timeLimit, sceneGroup, scene));
				}
				else
				{
					GamePacker.error("����δ�ҵ���",getInnerPath()+"   ���У�"+cityName+"��������"+sceneName+"�����ã�"+scenePath);
				}
			}
			
			WorldScene[] scenes = sceneList.toArray(new WorldScene[sceneList.size()]);
			
			cityList.add(new WorldCity(cityID,cityName,scenes));
		}
		
		WorldCity[] citys=new WorldCity[cityList.size()];
		citys=cityList.toArray(citys);
		
		this.citys=citys;
	}
}
