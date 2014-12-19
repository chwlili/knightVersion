package org.game.knight.version.packer.world.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.chw.util.PathUtil;
import org.chw.util.XmlUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.swt.graphics.Rectangle;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.world.BaseWriter;
import org.game.knight.version.packer.world.WorldWriter;

public class WorldTable extends BaseWriter
{
	private SceneLink[] links;
	private WorldCity[] citys;
	private Scene[] scenes;
	private HashMap<Integer, Scene> sceneMap;

	/**
	 * 构造函数
	 * 
	 * @param root
	 */
	public WorldTable(WorldWriter root)
	{
		super(root, null);
	}

	/**
	 * 获取所有链接
	 * 
	 * @return
	 */
	public SceneLink[] getLinks()
	{
		return links;
	}

	/**
	 * 获取所有城市
	 * 
	 * @return
	 */
	public WorldCity[] getCitys()
	{
		return citys;
	}

	/**
	 * 获取所有场景
	 * 
	 * @return
	 */
	public Scene[] getAllScene()
	{
		return scenes;
	}

	/**
	 * 按ID获取场景
	 * 
	 * @param id
	 * @return
	 */
	public Scene getScene(int id)
	{
		return sceneMap.get(id);
	}

	@Override
	protected void startup() throws Exception
	{
		GamePacker.log("开始读取世界信息");
	}

	/**
	 * 构建
	 * 
	 * @throws Exception
	 */
	@Override
	protected void exec() throws Exception
	{
		ProjectFile[] links = root.fileTable.getAllLinkFiles();
		if (links.length <= 0)
		{
			return;
		}
		File file = links[0];

		// 读取XML
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);

		if (document != null)
		{
			buildLinks(document);
			buildScenes(document);
		}
	}

	// --------------------------------------------------------------------------------------------------------
	//
	// 构建链接列表
	//
	// --------------------------------------------------------------------------------------------------------

	/**
	 * 构建链接
	 * 
	 * @param document
	 */
	private void buildLinks(Document document)
	{
		HashMap<String, Integer> hash_sceneID = new HashMap<String, Integer>();
		HashMap<String, String> hash_sceneName = new HashMap<String, String>();
		HashMap<String, Integer> hash_type = new HashMap<String, Integer>();
		HashMap<String, Rectangle> hash_rect = new HashMap<String, Rectangle>();

		@SuppressWarnings("rawtypes")
		List cityNodes = document.selectNodes("linkData/citys/city");
		for (int i = 0; i < cityNodes.size(); i++)
		{
			Element cityNode = (Element) cityNodes.get(i);

			// 遍历场景
			@SuppressWarnings({ "rawtypes" })
			List sceneNodes = cityNode.selectNodes("scene");
			for (int j = 0; j < sceneNodes.size(); j++)
			{
				if (root.isCancel())
				{
					return;
				}

				Element sceneNode = (Element) sceneNodes.get(j);

				int sceneID = Integer.parseInt(sceneNode.attributeValue("id"));
				String sceneName = sceneNode.attributeValue("name");
				String scenePath = PathUtil.rebuildPath(sceneNode.attributeValue("scenePath"));

				ProjectFile sceneFile = root.fileTable.getSceneFile(scenePath);
				if (sceneFile != null)
				{
					Document sceneDom = null;
					try
					{
						SAXReader reader = new SAXReader();
						sceneDom = reader.read(sceneFile);
					}
					catch (DocumentException e)
					{
						GamePacker.error(e);
					}

					if (sceneDom != null)
					{
						// 热区
						@SuppressWarnings("rawtypes")
						List hotNodes = sceneDom.selectNodes("sceneData/hots/hot");
						for (int k = 0; k < hotNodes.size(); k++)
						{
							Element hotNode = (Element) hotNodes.get(k);

							String key = sceneID + "#" + hotNode.attributeValue("label");
							boolean isFrom = hotNode.attributeValue("type").equals("0");
							boolean isDest = hotNode.attributeValue("type").equals("1");

							int width = XmlUtil.parseInt(hotNode.attributeValue("width"), 0);
							int height = XmlUtil.parseInt(hotNode.attributeValue("height"), 0);
							int x = XmlUtil.parseInt(hotNode.attributeValue("x"), 0) - width / 2;
							int y = XmlUtil.parseInt(hotNode.attributeValue("y"), 0) - height / 2;

							if (isFrom || isDest)
							{
								int type = isFrom ? 1 : 2;
								Rectangle rect = new Rectangle(x, y, width, height);

								hash_sceneID.put(key, sceneID);
								hash_sceneName.put(key, sceneName);
								hash_type.put(key, type);
								hash_rect.put(key, rect);
							}
						}
					}
				}
			}
		}

		// 读取链接
		ArrayList<SceneLink> lineList = new ArrayList<SceneLink>();
		@SuppressWarnings({ "rawtypes" })
		List linkNodes = document.selectNodes("linkData/links/link");
		for (int i = 0; i < linkNodes.size(); i++)
		{
			Element linkNode = (Element) linkNodes.get(i);

			// 基本属性
			String from = linkNode.attributeValue("from");
			String dest = linkNode.attributeValue("dest");
			String achieve = linkNode.attributeValue("restrict");
			String finishQuests = linkNode.attributeValue("quests");
			String acceptQuests = linkNode.attributeValue("currQuests");
			int level = Integer.parseInt(linkNode.attributeValue("destLevel"));

			if (hash_sceneID.containsKey(from) && hash_sceneID.containsKey(dest))
			{
				int fromSceneID = hash_sceneID.get(from);
				String fromSceneName = hash_sceneName.get(from);
				Rectangle fromRect = hash_rect.get(from);

				int destSceneID = hash_sceneID.get(dest);
				String destSceneName = hash_sceneName.get(from);
				Rectangle destRect = hash_rect.get(dest);

				SceneLinkFrom linkFrom = new SceneLinkFrom(from, fromSceneID, fromSceneName, fromRect.x, fromRect.y, fromRect.width, fromRect.height);
				SceneLinkTarget linkTarget = new SceneLinkTarget(dest, destSceneID, destSceneName, destRect.x, destRect.y, destRect.width, destRect.height);
				lineList.add(new SceneLink(linkFrom, linkTarget, achieve, finishQuests, acceptQuests, level));
			}
		}
		links = lineList.toArray(new SceneLink[lineList.size()]);
	}

	// --------------------------------------------------------------------------------------------------------
	//
	// 构建场景列表
	//
	// --------------------------------------------------------------------------------------------------------

	/**
	 * 构建场景列表
	 * 
	 * @param document
	 */
	private void buildScenes(Document document)
	{
		ArrayList<WorldCity> allCitys = new ArrayList<WorldCity>();
		ArrayList<Scene> allScenes = new ArrayList<Scene>();
		HashMap<Integer, Scene> allSceneMap = new HashMap<Integer, Scene>();

		// 读取场景
		@SuppressWarnings({ "rawtypes" })
		List cityNodes = document.selectNodes("linkData/citys/city");
		for (int i = 0; i < cityNodes.size(); i++)
		{
			Element cityNode = (Element) cityNodes.get(i);

			// 城市基本信息
			int cityID = Integer.parseInt(cityNode.attributeValue("id"));
			String cityName = cityNode.attributeValue("name");
			String citySound = PathUtil.rebuildPath(cityNode.attributeValue("sound"));
			ArrayList<Scene> cityScenes = new ArrayList<Scene>();

			// 遍历场景
			@SuppressWarnings({ "rawtypes" })
			List sceneNodes = cityNode.selectNodes("scene");
			for (int j = 0; j < sceneNodes.size(); j++)
			{
				if (root.isCancel())
				{
					return;
				}

				Element sceneNode = (Element) sceneNodes.get(j);

				// 场景基本信息
				int sceneID = Integer.parseInt(sceneNode.attributeValue("id"));
				int timeLimit = sceneNode.attribute("maxTime") != null ? Integer.parseInt(sceneNode.attributeValue("maxTime")) : 0;
				int sceneType = Integer.parseInt(sceneNode.attributeValue("type"));
				String sceneName = sceneNode.attributeValue("name");
				int sceneGroup = Integer.parseInt(sceneNode.attributeValue("group"));
				String sceneSound = PathUtil.rebuildPath(sceneNode.attributeValue("sound"));
				String scenePath = PathUtil.rebuildPath(sceneNode.attributeValue("scenePath"));

				if (sceneSound == null || sceneSound.isEmpty())
				{
					sceneSound = citySound;
				}

				ProjectFile sceneFile = root.fileTable.getSceneFile(scenePath);
				ProjectMp3File bgsoundFile = root.fileTable.getMp3File(sceneSound);
				if (sceneFile != null)
				{
					Scene scene = buildScene(sceneID, sceneName, sceneType, timeLimit, sceneGroup, bgsoundFile, sceneFile);
					cityScenes.add(scene);
					allScenes.add(scene);
					allSceneMap.put(sceneID, scene);
				}
				else
				{
					GamePacker.error("场景配置未找到！", "城市：" + cityName + "，场景：" + sceneName + "，引用：" + scenePath);
				}
			}

			allCitys.add(new WorldCity(cityID, cityName, cityScenes.toArray(new Scene[cityScenes.size()])));
		}

		this.citys = allCitys.toArray(new WorldCity[allCitys.size()]);
		this.scenes = allScenes.toArray(new Scene[allScenes.size()]);
		this.sceneMap = allSceneMap;
	}

	// --------------------------------------------------------------------------------------------------------
	//
	// 创建场景
	//
	// --------------------------------------------------------------------------------------------------------

	private ProjectFile sceneFile;
	private int sceneID;
	private String sceneName;
	private int sceneGroup;
	private int sceneType;
	private int timeLimit;
	private ProjectMp3File sceneSound;
	private Hashtable<String, String> key_url;

	/**
	 * 创建场景
	 * 
	 * @param sceneID
	 * @param sceneName
	 * @param sceneType
	 * @param timeLimit
	 * @param sceneGroup
	 * @param sceneSound
	 * @param sceneFile
	 * @return
	 */
	private Scene buildScene(int sceneID, String sceneName, int sceneType, int timeLimit, int sceneGroup, ProjectMp3File sceneSound, ProjectFile sceneFile)
	{
		this.sceneID = sceneID;
		this.sceneName = sceneName;
		this.sceneType = sceneType;
		this.timeLimit = timeLimit;
		this.sceneGroup = sceneGroup;
		this.sceneSound = sceneSound;
		this.sceneFile = sceneFile;

		Document document = null;
		try
		{
			SAXReader reader = new SAXReader();
			document = reader.read(sceneFile);
		}
		catch (DocumentException e)
		{
			GamePacker.error(e);
		}

		if (document != null)
		{
			return buildSceneInfo(sceneFile, document);
		}

		return null;
	}

	/**
	 * 初始化导入表
	 * 
	 * @param document
	 */
	private void initImportTable(Document document)
	{
		key_url = new Hashtable<String, String>();

		@SuppressWarnings({ "rawtypes" })
		List list = document.selectNodes("sceneData/resources/resource");

		for (int i = 0; i < list.size(); i++)
		{
			Element node = (Element) list.get(i);

			String key = node.attributeValue("key");
			String path = PathUtil.rebuildPath(node.attributeValue("path"));

			key_url.put(key, path);
		}
	}

	/**
	 * 获取图像资源
	 * 
	 * @param ref
	 * @return
	 */
	private AttireBitmap getImgRes(String ref)
	{
		if (ref != null)
		{
			String absRef = PathUtil.rebuildPath(ref);
			String[] parts = absRef.split("/");

			String key = parts[0];
			// String type = parts[1];
			String name = parts[2];

			if (key_url.containsKey(key))
			{
				return root.attireTable.getBitmap(key_url.get(key), name);
			}
		}
		return null;
	}

	/**
	 * 获取装扮资源
	 * 
	 * @param ref
	 * @return
	 */
	private Attire getAttireRes(String ref)
	{
		if (ref != null)
		{
			String absRef = PathUtil.rebuildPath(ref);
			String[] parts = absRef.split("/");

			if (parts.length >= 3)
			{
				String key = parts[0];
				// String type = parts[1];
				String name = parts[2];

				if (key_url.containsKey(key))
				{
					return root.attireTable.getAttire(key_url.get(key), name);
				}
			}
		}
		return null;
	}

	/**
	 * 生成场景信息
	 * 
	 * @param document
	 * @param outputFolder
	 */
	private Scene buildSceneInfo(ProjectFile file, Document document)
	{
		initImportTable(document);

		@SuppressWarnings({ "rawtypes" })
		List list;

		// 基本信息
		Element node = (Element) document.selectSingleNode("sceneData/sceneInfo");
		int sceneW = XmlUtil.parseInt(node.attributeValue("sceneWidth"), 0);
		int sceneH = XmlUtil.parseInt(node.attributeValue("sceneHeight"), 0);
		int viewX = XmlUtil.parseInt(node.attributeValue("viewOffsetX"), 0);
		int viewY = XmlUtil.parseInt(node.attributeValue("viewOffsetY"), 0);
		int defaultX = XmlUtil.parseInt(node.attributeValue("defaultX"), 0);
		int defaultY = XmlUtil.parseInt(node.attributeValue("defaultY"), 0);
		int beginX = XmlUtil.parseInt(node.attributeValue("readyX"), Integer.MIN_VALUE);
		String grid = node.getText();

		if (sceneW == 0 || sceneH == 0)
		{
			GamePacker.error("场景大小不能为空!(" + sceneW + "," + sceneH + ")", sceneFile.url);
		}
		if (defaultX == 0 || defaultY == 0)
		{
			GamePacker.error("场景默认点不合法!(" + defaultX + "," + defaultY + ")", sceneFile.url);
		}
		if (beginX == Integer.MIN_VALUE && document.selectNodes("sceneData/timers/timer").size() > 0)
		{
			GamePacker.error("有怪的场景没有设置开始线！", sceneFile.url);
		}

		// 分屏信息
		ArrayList<SceneSection> sections = new ArrayList<SceneSection>();
		list = document.selectNodes("sceneData/splits/split");
		if (list.size() > 0)
		{
			for (int i = 0; i < list.size(); i++)
			{
				node = (Element) list.get(i);
				String pos = node.attributeValue("position");
				String type = node.attributeValue("type");

				if (pos != null && !pos.isEmpty())
				{
					sections.add(new SceneSection(XmlUtil.parseInt(pos, 0), XmlUtil.parseInt(type, 0)));
				}
				else
				{
					GamePacker.error("错误的分屏线设定！(index=" + i + " , x=" + pos + ")", sceneFile.url);
				}
			}
			sections.add(new SceneSection(sceneW, 0));
		}
		else
		{
			sections.add(new SceneSection(sceneW, 0));
		}
		Collections.sort(sections, new Comparator<SceneSection>()
		{
			@Override
			public int compare(SceneSection o1, SceneSection o2)
			{
				if (o1.position < o2.position)
				{
					return -1;
				}
				else if (o1.position > o2.position)
				{
					return 1;
				}
				return 0;
			}
		});

		// 背景图层
		ArrayList<SceneBackLayer> backLayers = new ArrayList<SceneBackLayer>();
		list = document.selectNodes("sceneData/layers/layer");
		for (int i = 0; i < list.size(); i++)
		{
			node = (Element) list.get(i);

			String path = node.attributeValue("imagePath");
			String name = node.attributeValue("label");
			int x = XmlUtil.parseInt(node.attributeValue("offsetX"), 0);
			int y = XmlUtil.parseInt(node.attributeValue("offsetY"), 0);
			float speed = ((float) XmlUtil.parseInt(node.attributeValue("scrollSpeed"), 0) / 1000);

			AttireBitmap img = getImgRes(path);
			if (img != null)
			{
				backLayers.add(new SceneBackLayer(name, x, y, speed, img));
			}
			else
			{
				GamePacker.warning("背景图层图像未找到！", sceneFile.url + "  名称:" + name + "   引用:" + path);
			}
		}

		// 前景图层
		ArrayList<SceneForeLayer> foreLayers = new ArrayList<SceneForeLayer>();
		list = document.selectNodes("sceneData/autoLayers/layer");
		for (int i = 0; i < list.size(); i++)
		{
			node = (Element) list.get(i);

			String path = node.attributeValue("imagePath");
			String name = node.attributeValue("label");
			int x = XmlUtil.parseInt(node.attributeValue("x"), 0);
			int y = XmlUtil.parseInt(node.attributeValue("y"), 0);
			int w = XmlUtil.parseInt(node.attributeValue("width"), 0);
			int speed = XmlUtil.parseInt(node.attributeValue("speed"), 0);

			AttireBitmap img = getImgRes(path);
			if (img != null)
			{
				foreLayers.add(new SceneForeLayer(name, x, y, w, speed, img));
			}
			else
			{
				GamePacker.warning("前景图层图像未找到！", sceneFile.url + "  名称:" + name + "   引用:" + path);
			}
		}

		// 背景动画
		ArrayList<SceneAnim> backAnims = new ArrayList<SceneAnim>();
		list = document.selectNodes("sceneData/stills/still");
		for (int i = 0; i < list.size(); i++)
		{
			node = (Element) list.get(i);

			String path = node.attributeValue("imagePath");
			String name = node.attributeValue("label");
			int x = XmlUtil.parseInt(node.attributeValue("x"), 0);
			int y = XmlUtil.parseInt(node.attributeValue("y"), 0);
			int offsetX = 0;
			int offsetY = 0;
			int direction = XmlUtil.parseInt(node.attributeValue("direction"), 1);

			Attire attire = getAttireRes(path);
			if (attire != null)
			{
				backAnims.add(new SceneAnim(name, x, y, offsetX, offsetY, direction, attire));
			}
			else
			{
				GamePacker.warning("背景动画装扮未找到！", sceneFile.url + "  名称:" + name + "   引用:" + path);
			}
		}

		// 前景动画
		ArrayList<SceneAnim> anims = new ArrayList<SceneAnim>();
		list = document.selectNodes("sceneData/anims/anim");
		for (int i = 0; i < list.size(); i++)
		{
			node = (Element) list.get(i);

			String path = node.attributeValue("imagePath");
			String name = node.attributeValue("label");
			int x = XmlUtil.parseInt(node.attributeValue("x"), 0);
			int y = XmlUtil.parseInt(node.attributeValue("y"), 0);
			int offsetX = XmlUtil.parseInt(node.attributeValue("offsetX"), 0);
			int offsetY = XmlUtil.parseInt(node.attributeValue("offsetY"), 0);
			int direction = XmlUtil.parseInt(node.attributeValue("direction"), 1);

			Attire attire = getAttireRes(path);
			if (attire != null)
			{
				anims.add(new SceneAnim(name, x, y, offsetX, offsetY, direction, attire));
			}
			else
			{
				GamePacker.warning("前景动画装扮未找到！", sceneFile.url + "  名称:" + name + "   引用:" + path);
			}
		}

		// NPC , 传送门 , 动画
		ArrayList<SceneNpc> npcs = new ArrayList<SceneNpc>();
		list = document.selectNodes("sceneData/npcs/npc");
		for (int i = 0; i < list.size(); i++)
		{
			node = (Element) list.get(i);

			String path = node.attributeValue("attire");
			String name = node.attributeValue("label").trim();
			int x = XmlUtil.parseInt(node.attributeValue("x"), 0);
			int y = XmlUtil.parseInt(node.attributeValue("y"), 0);
			int npcID = XmlUtil.parseInt(node.attributeValue("configID"), 0);
			int direction = XmlUtil.parseInt(node.attributeValue("direction"), 1);

			Attire attire = getAttireRes(path);
			if (attire != null)
			{
				npcs.add(new SceneNpc(name, npcID, x, y, direction, attire));
			}
			else
			{
				GamePacker.warning("NPC装扮未找到！", sceneFile.url + "  名称:" + name + "   引用:" + path);
			}
		}

		// 怪物列表
		ArrayList<ScenePart> parts = new ArrayList<ScenePart>();
		Hashtable<Integer, ScenePart> monsterParts = new Hashtable<Integer, ScenePart>();
		list = document.selectNodes("sceneData/timers/timer");
		for (int i = 0; i < list.size(); i++)
		{
			node = (Element) list.get(i);

			String timerName = node.attributeValue("label");
			int x = XmlUtil.parseInt(node.attributeValue("x"), 0);
			int y = XmlUtil.parseInt(node.attributeValue("y"), 0);
			boolean loop = node.attributeValue("loop").equals("true");

			SceneMonsterTimer timer = new SceneMonsterTimer(timerName, x, y, loop);

			for (int z = 0; z < sections.size(); z++)
			{
				if (timer.getX() < sections.get(z).position)
				{
					int index = z;

					ScenePart monsterPart = monsterParts.get(index);
					if (monsterPart == null)
					{
						int left = 0;
						int right = sections.get(index).position;
						if (index > 0)
						{
							left = sections.get(index - 1).position;
						}
						monsterPart = new ScenePart(left, right);
						monsterParts.put(index, monsterPart);
					}

					monsterPart.timers.add(timer);
					break;
				}
			}

			@SuppressWarnings({ "rawtypes" })
			List groups = node.selectNodes("group");
			for (int j = 0; j < groups.size(); j++)
			{
				Element group = (Element) groups.get(j);

				String groupName = group.attributeValue("label");
				int delay = XmlUtil.parseInt(group.attributeValue("delay"), 0);
				boolean obstruct = group.attributeValue("obstruct").equals("true");
				int loopCount = XmlUtil.parseInt(group.attributeValue("loop"), 0);
				boolean ignoreFirstDelay = group.attributeValue("ignoreable").equals("true");

				SceneMonsterBatch batch = new SceneMonsterBatch(delay, obstruct, loopCount, ignoreFirstDelay);
				timer.addSceneMonsterBatch(batch);

				@SuppressWarnings({ "rawtypes" })
				List monsters = group.selectNodes("monster");
				for (int k = 0; k < monsters.size(); k++)
				{
					Element monster = (Element) monsters.get(k);

					String monsterName = monster.attributeValue("label");
					Attire attire = getAttireRes(monster.attributeValue("attireID"));
					int monsterID = XmlUtil.parseInt(monster.attributeValue("id"), 0);
					int monsterX = XmlUtil.parseInt(monster.attributeValue("x"), 0);
					int monsterY = XmlUtil.parseInt(monster.attributeValue("y"), 0);
					int monsterDir = XmlUtil.parseInt(monster.attributeValue("direction"), 1);

					batch.addMonster(new SceneMonster(monsterID, attire, monsterX, monsterY, monsterDir));

					if (attire == null)
					{
						GamePacker.warning("怪物装扮未找到！", sceneFile.url + "  计时器:" + timerName + " 批次:" + groupName + " 怪物:" + monsterName);
					}
				}
			}
		}
		for (ScenePart scenePart : monsterParts.values())
		{
			parts.add(scenePart);
		}
		Collections.sort(parts, new Comparator<ScenePart>()
		{
			@Override
			public int compare(ScenePart o1, ScenePart o2)
			{
				if (o1.left < o2.left)
				{
					return -1;
				}
				else if (o1.left > o2.left)
				{
					return 1;
				}
				return 0;
			}
		});

		// 陷阱
		ArrayList<SceneTrap> traps = new ArrayList<SceneTrap>();
		list = document.selectNodes("sceneData/traps/trap");
		for (int i = 0; i < list.size(); i++)
		{
			node = (Element) list.get(i);

			int type = XmlUtil.parseInt(node.attributeValue("type"), 0);
			int x = XmlUtil.parseInt(node.attributeValue("x"), 0);
			int y = XmlUtil.parseInt(node.attributeValue("y"), 0);
			int width = XmlUtil.parseInt(node.attributeValue("width"), 0);
			int height = XmlUtil.parseInt(node.attributeValue("height"), 0);

			@SuppressWarnings({ "rawtypes" })
			List params = node.selectNodes("param");

			int quest = 0;
			String content = "";

			if (params.size() > 0)
			{
				String param1 = ((Element) params.get(0)).getText();
				if (param1 != null && !param1.isEmpty())
				{
					quest = XmlUtil.parseInt(param1, 0);
				}
			}
			if (params.size() > 1)
			{
				String param2 = ((Element) params.get(1)).getText();
				if (param2 != null && !param2.isEmpty())
				{
					content = param2;
				}
			}
			traps.add(new SceneTrap(type, x, y, width, height, quest, content));
		}

		// 热区
		ArrayList<SceneDoor> doors = new ArrayList<SceneDoor>();
		ArrayList<SceneNpc> deledNPC = new ArrayList<SceneNpc>();
		list = document.selectNodes("sceneData/hots/hot");
		for (int i = 0; i < list.size(); i++)
		{
			node = (Element) list.get(i);

			String key = sceneID + "#" + node.attributeValue("label");
			boolean isFrom = node.attributeValue("type").equals("0");
			boolean isDest = node.attributeValue("type").equals("1");

			String name = node.attributeValue("label");
			int width = XmlUtil.parseInt(node.attributeValue("width"), 0);
			int height = XmlUtil.parseInt(node.attributeValue("height"), 0);
			int x = XmlUtil.parseInt(node.attributeValue("x"), 0) - width / 2;
			int y = XmlUtil.parseInt(node.attributeValue("y"), 0) - height / 2;

			String acceptableQuestList = "";
			String acceptedQuestList = "";
			String submitableQuestList = "";
			String submitedQuestList = "";

			// 分析任务限制
			String quest = node.attributeValue("quest");
			if (quest != null && quest.isEmpty() == false)
			{
				String[] questPart = quest.split("\\|");
				if (questPart.length > 0)
				{
					acceptableQuestList = questPart[0];
				}
				if (questPart.length > 1)
				{
					acceptedQuestList = questPart[1];
				}
				if (questPart.length > 2)
				{
					submitableQuestList = questPart[2];
				}
				if (questPart.length > 3)
				{
					submitedQuestList = questPart[3];
				}
			}

			if (isFrom)
			{
				int doorX = x + width / 2;
				int doorY = y + height / 2;
				int doorDir = 1;
				Attire attire = null;

				for (SceneNpc npc : npcs)
				{
					// 转换ID为0名称为跳转点的的NPC到传送门列表
					if (npc.id == 0 && npc.name.equals("跳转点") && npc.x >= x && npc.y >= y && npc.x <= x + width && npc.y <= y + height)
					{
						deledNPC.add(npc);

						doorX = npc.x;
						doorY = npc.y;
						doorDir = npc.direction;
						attire = npc.attire;
						break;
					}
				}

				ArrayList<SceneHotLink> hotLinks = new ArrayList<SceneHotLink>();
				for (SceneLink link : links)
				{
					if (link.from.hash.equals(key))
					{
						hotLinks.add(new SceneHotLink(link.dest.sceneID, link.dest.sceneName, link.dest.x, link.dest.y));
					}
				}
				SceneHotLink[] hotLinkArray = hotLinks.toArray(new SceneHotLink[hotLinks.size()]);
				SceneHot sceneHot = new SceneHot(name, x, y, width, height, acceptableQuestList, acceptedQuestList, submitableQuestList, submitedQuestList, hotLinkArray);
				doors.add(new SceneDoor(doorX, doorY, doorDir, attire, sceneHot));
			}
			else if (isDest)
			{
			}
		}

		// 删除已经转移的NPC
		for (SceneNpc npc : deledNPC)
		{
			npcs.remove(npc);
		}

		// 转换ID为0的NPC到动画列表、传送门列表
		for (SceneNpc npc : npcs)
		{
			if (npc.id == 0)
			{
				deledNPC.add(npc);

				anims.add(new SceneAnim(npc.name, npc.x, npc.y, 0, 0, npc.direction, npc.attire));
			}
		}

		// 删除已经转移的NPC
		for (SceneNpc npc : deledNPC)
		{
			npcs.remove(npc);
		}

		// 保存场景结构
		SceneSection[] sectionArr = sections.toArray(new SceneSection[sections.size()]);
		SceneBackLayer[] backLayerArr = backLayers.toArray(new SceneBackLayer[backLayers.size()]);
		SceneForeLayer[] foreLayerArr = foreLayers.toArray(new SceneForeLayer[foreLayers.size()]);
		SceneAnim[] backAnimArr = backAnims.toArray(new SceneAnim[backAnims.size()]);
		SceneAnim[] foreAnimArr = anims.toArray(new SceneAnim[anims.size()]);
		SceneNpc[] npcArr = npcs.toArray(new SceneNpc[npcs.size()]);
		SceneDoor[] doorArr = doors.toArray(new SceneDoor[doors.size()]);
		ScenePart[] partArr = parts.toArray(new ScenePart[parts.size()]);
		SceneTrap[] trapArr = traps.toArray(new SceneTrap[traps.size()]);

		return new Scene(file, sceneID, sceneName, sceneGroup, sceneType, timeLimit, sceneW, sceneH, beginX, defaultX, defaultY, viewX, viewY, grid, sceneSound, sectionArr, backLayerArr, foreLayerArr, backAnimArr, foreAnimArr, npcArr, doorArr, partArr, trapArr);
	}

	@Override
	public void saveVer()
	{

	}
}
