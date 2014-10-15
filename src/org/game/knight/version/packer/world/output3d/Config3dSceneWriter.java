package org.game.knight.version.packer.world.output3d;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.chw.util.FileUtil;
import org.chw.util.MD5Util;
import org.chw.util.TextUtil;
import org.chw.util.ZlibUtil;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.world.BaseWriter;
import org.game.knight.version.packer.world.WorldWriter;
import org.game.knight.version.packer.world.model.Attire;
import org.game.knight.version.packer.world.model.AttireAction;
import org.game.knight.version.packer.world.model.AttireAnim;
import org.game.knight.version.packer.world.model.ImageFrame;
import org.game.knight.version.packer.world.model.Scene;
import org.game.knight.version.packer.world.model.SceneAnim;
import org.game.knight.version.packer.world.model.SceneBackLayer;
import org.game.knight.version.packer.world.model.SceneDoor;
import org.game.knight.version.packer.world.model.SceneForeLayer;
import org.game.knight.version.packer.world.model.SceneHot;
import org.game.knight.version.packer.world.model.SceneHotLink;
import org.game.knight.version.packer.world.model.SceneMonster;
import org.game.knight.version.packer.world.model.SceneMonsterBatch;
import org.game.knight.version.packer.world.model.SceneMonsterTimer;
import org.game.knight.version.packer.world.model.SceneNpc;
import org.game.knight.version.packer.world.model.ScenePart;
import org.game.knight.version.packer.world.model.SceneSection;
import org.game.knight.version.packer.world.model.SceneTrap;
import org.game.knight.version.packer.world.model.WorldCity;

public class Config3dSceneWriter extends BaseWriter
{
	private Config3d config;

	private String worldCfgKey;
	private String worldCfgURL;

	private HashMap<Scene, String> scene_url;
	private HashMap<Scene, String> scene_files;
	private HashMap<Scene, Integer> scene_size;

	private HashMap<String, String> newTable = new HashMap<String, String>();
	private HashMap<String, String> oldTable = new HashMap<String, String>();

	/**
	 * 构造函数
	 * 
	 * @param root
	 */
	public Config3dSceneWriter(WorldWriter root, Config3d config)
	{
		super(root, "3dScene");
		this.config = config;
	}

	/**
	 * 获取场景配置的URL
	 * 
	 * @param scene
	 * @return
	 */
	public String getSceneURLs(Scene scene)
	{
		return scene_files.get(scene);
	}

	/**
	 * 获取场景配置的总大小
	 * 
	 * @param scene
	 * @return
	 */
	public int getSceneSize(Scene scene)
	{
		return scene_size.get(scene);
	}

	/**
	 * 获取世界配置Key
	 * 
	 * @return
	 */
	public String getWorldCfgKey()
	{
		return worldCfgKey;
	}

	/**
	 * 获取世界配置URL
	 * 
	 * @return
	 */
	public String getWorldCfgURL()
	{
		return worldCfgURL;
	}

	@Override
	protected void startup() throws Exception
	{
		GamePacker.log("开始输出场景配置(3D)");
	}

	@Override
	protected void exec() throws Exception
	{
		scene_url = new HashMap<Scene, String>();

		for (Scene scene : root.worldTable.getAllScene())
		{
			writeSceneCfg(scene);

			if (root.isCancel())
			{
				return;
			}
		}

		if (root.isCancel())
		{
			return;
		}

		writerWorldCfg();

		if (root.isCancel())
		{
			return;
		}

		measureSceneLoadInfo();
	}

	/**
	 * 输出场景配置
	 * 
	 * @param scene
	 * @throws Exception
	 */
	private void writeSceneCfg(Scene scene) throws Exception
	{
		GamePacker.progress("输出场景", scene.sceneID + "." + scene.sceneName);

		String bgsPath = "";
		if (scene.bgs != null)
		{
			bgsPath = root.localToCdnURL(root.mp3Writer.getMp3URL(scene.bgs));
		}

		int[] sectionArr = new int[scene.sections.length];
		for (int i = 0; i < scene.sections.length; i++)
		{
			sectionArr[i] = scene.sections[i].position;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("<scene id=\"" + scene.sceneID + "\" type=\"" + scene.sceneType + "\" name=\"" + scene.sceneName + "\" group=\"" + scene.sceneGroup + "\" bgs=\"" + bgsPath + "\" defaultX=\"" + scene.defaultX + "\" defaultY=\"" + scene.defaultY + "\" sceneWidth=\"" + scene.sceneW + "\" sceneHeight=\"" + scene.sceneH + "\" viewOffsetX=\"" + scene.viewX + "\" viewOffsetY=\"" + scene.viewY + "\" beginX=\"" + scene.beginX + "\" timeLimit=\"" + Math.abs(scene.timeLimit) + "\" timeLimitType=\"" + (scene.timeLimit > 0 ? 1 : (scene.timeLimit < 0 ? 2 : 0)) + "\" sections=\"" + TextUtil.formatIntArray(sectionArr) + "\" >\n");
		sb.append("\t<grid><![CDATA[" + scene.grid + "]]></grid>\n");
		sb.append("\t<sections>\n");
		for (SceneSection section : scene.sections)
		{
			sb.append("\t\t<section x=\"" + section.position + "\" type=\"" + section.type + "\" />\n");
		}
		sb.append("\t</sections>\n");

		sb.append("\t<layers>\n");
		for (SceneBackLayer layer : scene.backLayers)
		{
			if (layer.img == null)
			{
				continue;
			}
			ImageFrame frame = root.frameTable.get(layer.img.imgFile.gid, 1, 1, 0);
			if (frame == null)
			{
				continue;
			}
			SliceImage img = config.sliceWriter.getSliceImage(frame);
			if (img == null)
			{
				continue;
			}

			sb.append("\t\t<layer offsetX=\"" + layer.x + "\" offsetY=\"" + layer.y + "\" scrollSpeed=\"" + layer.speed + "\" clipX=\"" + img.frame.clipX + "\" clipY=\"" + img.frame.clipY + "\" clipW=\"" + img.frame.clipW + "\" clipH=\"" + img.frame.clipH + "\" imgW=\"" + img.frame.frameW + "\" imgH=\"" + img.frame.frameH + "\" previewURL=\"" + root.localToCdnURL(img.previewURL) + "\" sliceRow=\"" + img.sliceRow + "\" sliceCol=\"" + img.sliceCol + "\" />\n");
		}
		sb.append("\t</layers>\n");

		sb.append("\t<foreLayers>\n");
		for (SceneForeLayer layer : scene.foreLayers)
		{
			if (layer.img == null)
			{
				continue;
			}
			ImageFrame frame = root.frameTable.get(layer.img.imgFile.gid, 1, 1, 0);
			if (frame == null)
			{
				continue;
			}
			SliceImage img = config.sliceWriter.getSliceImage(frame);
			if (img == null)
			{
				continue;
			}

			sb.append("\t\t<layer x=\"" + layer.x + "\" y=\"" + layer.y + "\" width=\"" + layer.w + "\" speed=\"" + layer.speed + "\" clipX=\"" + img.frame.clipX + "\" clipY=\"" + img.frame.clipY + "\" clipW=\"" + img.frame.clipW + "\" clipH=\"" + img.frame.clipH + "\" imgW=\"" + img.frame.frameW + "\" imgH=\"" + img.frame.frameH + "\" previewURL=\"" + root.localToCdnURL(img.previewURL) + "\" sliceRow=\"" + img.sliceRow + "\" sliceCol=\"" + img.sliceCol + "\" />\n");
		}
		sb.append("\t</foreLayers>\n");

		sb.append("\t<backAnims>\n");
		for (SceneAnim anim : scene.backAnims)
		{
			sb.append("\t\t<anim x=\"" + anim.x + "\" y=\"" + anim.y + "\" offsetX=\"" + anim.offsetX + "\" offsetY=\"" + anim.offsetY + "\" direction=\"" + anim.direction + "\" attire=\"" + (anim.attire != null ? anim.attire.gid : "") + "\"/>\n");
		}
		sb.append("\t</backAnims>\n");

		sb.append("\t<anims>\n");
		for (SceneAnim anim : scene.anims)
		{
			sb.append("\t\t<anim x=\"" + anim.x + "\" y=\"" + anim.y + "\" offsetX=\"" + anim.offsetX + "\" offsetY=\"" + anim.offsetY + "\" direction=\"" + anim.direction + "\" attire=\"" + (anim.attire != null ? anim.attire.gid : "") + "\"/>\n");
		}
		sb.append("\t</anims>\n");

		sb.append("\t<npcs>\n");
		for (SceneNpc npc : scene.npcs)
		{
			sb.append("\t\t<npc id=\"" + npc.id + "\" x=\"" + npc.x + "\" y=\"" + npc.y + "\" direction=\"" + npc.direction + "\" attire=\"" + (npc.attire != null ? npc.attire.gid : "") + "\"/>\n");
		}
		sb.append("\t</npcs>\n");

		sb.append("\t<doors>\n");
		for (SceneDoor door : scene.doors)
		{
			SceneHot hot = door.hot;
			if (hot != null)
			{
				sb.append("\t\t<door x=\"" + door.x + "\" y=\"" + door.y + "\" offsetX=\"0\" offsetY=\"0\" direction=\"" + door.direction + "\" attire=\"" + (door.attire != null ? door.attire.gid : "") + "\">\n");
				sb.append(String.format("\t\t\t<hot x=\"%s\" y=\"%s\" width=\"%s\" height=\"%s\" acceptableQuests=\"%s\" acceptedQuests=\"%s\" submitableQuests=\"%s\" submitedQuests=\"%s\">\n", hot.x, hot.y, hot.width, hot.height, hot.acceptableQuests, hot.acceptedQuests, hot.submitableQuests, hot.submitedQuests));
				for (SceneHotLink line : hot.links)
				{
					sb.append(String.format("\t\t\t\t<link toID=\"%s\" toName=\"%s\" toX=\"%s\" toY=\"%s\" />\n", line.toSceneID, line.toSceneName, line.toSceneX, line.toSceneY));
				}
				sb.append("\t\t\t</hot>\n");
				sb.append("\t\t</door>\n");
			}
		}
		sb.append("\t</doors>\n");

		int trapID = 1;
		sb.append("\t<traps>\n");
		for (SceneTrap trap : scene.traps)
		{
			sb.append(String.format("\t\t<trap id=\"%s\" type=\"%s\" x=\"%s\" y=\"%s\" width=\"%s\" height=\"%s\" quest=\"%s\"><![CDATA[%s]]></trap>\n", trapID, trap.type, trap.x, trap.y, trap.width, trap.height, trap.quest, trap.content));
			trapID++;
		}
		sb.append("\t</traps>\n");

		sb.append("\t<monsters>\n");
		HashSet<Integer> monsterIDs = new HashSet<Integer>();
		for (ScenePart part : scene.parts)
		{
			for (SceneMonsterTimer timer : part.timers)
			{
				for (SceneMonsterBatch batch : timer.getBatchList())
				{
					for (SceneMonster monster : batch.getMonsters())
					{
						if (!monsterIDs.contains(monster.monsterID))
						{
							sb.append("\t\t<monster id=\"" + monster.monsterID + "\" x=\"" + monster.x + "\" y=\"" + monster.y + "\" dir=\"" + monster.dir + "\" attire=\"" + (monster.attire != null ? monster.attire.gid : "") + "\" />\n");
							monsterIDs.add(monster.monsterID);
						}
					}
				}
			}
		}
		sb.append("\t</monsters>\n");
		sb.append("</scene>");

		// 存储文件
		byte[] bytes = sb.toString().getBytes("UTF-8");
		if (root.hasZIP())
		{
			bytes = ZlibUtil.compress(bytes);
		}

		String md5 = MD5Util.md5Bytes(bytes);
		String url = oldTable.get(md5);

		if (url == null)
		{
			url = root.optionTable.getNextExportFile() + ".xml";

			File outputFile = new File(root.getOutputFolder().getPath() + url);
			FileUtil.writeFile(outputFile, bytes);
			root.addFileSuffix(outputFile);
		}

		newTable.put(md5, url);

		scene_url.put(scene, url);
	}

	/**
	 * 输出世界配置
	 * 
	 * @throws Exception
	 */
	private void writerWorldCfg() throws Exception
	{
		// 生成配置内容
		GamePacker.progress("生成世界配置");
		StringBuilder sb = new StringBuilder();
		sb.append("<worldDB>\n");
		sb.append("\t<citys>\n");
		for (WorldCity city : root.worldTable.getCitys())
		{
			sb.append(String.format("\t\t<city id=\"%s\" name=\"%s\">\n", city.id, city.name));
			for (Scene scene : city.scenes)
			{
				sb.append(String.format("\t\t\t<scene id=\"%s\" name=\"%s\" type=\"%s\" group=\"%s\" cityID=\"%s\">\n", scene.sceneID, scene.sceneName, scene.sceneType, scene.sceneGroup, city.id));
				sb.append(String.format("\t\t\t\t%s\n", getFileListNode(scene)));
				sb.append(String.format("\t\t\t</scene>\n"));
			}
			sb.append("\t\t</city>\n");
		}
		sb.append("\t</citys>\n");
		sb.append("</worldDB>");

		// 存储文件
		byte[] bytes = sb.toString().getBytes("UTF-8");

		byte[] cfgBytes = root.convertXmlToAs(new ByteArrayInputStream(bytes), "$World.xml2");
		if (cfgBytes != null)
		{
			bytes = cfgBytes;
		}
		else if (root.hasZIP())
		{
			bytes = ZlibUtil.compress(bytes);
		}

		String md5 = MD5Util.md5Bytes(bytes);
		String url = oldTable.get(md5);

		if (url == null)
		{
			url = root.optionTable.getNextExportFile() + ".cfg";

			File outputFile = new File(root.getOutputFolder().getPath() + url);
			FileUtil.writeFile(outputFile, bytes);
			root.addFileSuffix(outputFile);
		}

		newTable.put(md5, url);

		worldCfgKey = cfgBytes != null ? "$World.xml" : "world";
		worldCfgURL = url;
	}

	private String getFileListNode(Scene scene)
	{
		HashSet<String> urls = new HashSet<String>();
		HashSet<Attire> highAttires = new HashSet<Attire>();

		for (SceneBackLayer layer : scene.backLayers)
		{
			if (layer.img != null)
			{
				ImageFrame frame = root.frameTable.get(layer.img.imgFile.gid, 1, 1, 0);
				SliceImage slice = config.sliceWriter.getSliceImage(frame);
				if (slice != null)
				{
					for (String url : slice.sliceURLs)
					{
						urls.add(url);
					}
				}
			}
		}
		for (SceneForeLayer layer : scene.foreLayers)
		{
			if (layer.img != null)
			{
				ImageFrame frame = root.frameTable.get(layer.img.imgFile.gid, 1, 1, 0);
				SliceImage slice = config.sliceWriter.getSliceImage(frame);
				if (slice != null)
				{
					for (String url : slice.sliceURLs)
					{
						urls.add(url);
					}
				}
			}
		}
		for (SceneAnim anim : scene.backAnims)
		{
			if (anim.attire != null)
			{
				highAttires.add(anim.attire);
			}
		}
		for (SceneAnim anim : scene.anims)
		{
			if (anim.attire != null)
			{
				highAttires.add(anim.attire);
			}
		}
		for (SceneNpc npc : scene.npcs)
		{
			if (npc.attire != null)
			{
				highAttires.add(npc.attire);
			}
		}
		// for (SceneDoor door : scene.doors)
		// {
		// if (door.attire != null)
		// {
		// attires.add(door.attire);
		// }
		// }
		for (ScenePart part : scene.parts)
		{
			for (SceneMonsterTimer timer : part.timers)
			{
				for (SceneMonsterBatch batch : timer.getBatchList())
				{
					for (SceneMonster monster : batch.getMonsters())
					{
						if (monster.attire != null)
						{
							highAttires.add(monster.attire);
						}
					}
				}
			}
		}

		for (Attire attire : highAttires)
		{
			for (AttireAction action : attire.actions)
			{
				for (AttireAnim anim : action.anims)
				{
					for (int i = 0; i < anim.times.length; i++)
					{
						if (anim.times[i] <= 0)
						{
							continue;
						}

						ImageFrame frame = root.frameTable.get(anim.img.gid, anim.row, anim.col, i);
						Atlas atlas = config.atlasWriter.findAtlasByImageFrame(frame);
						if (atlas != null)
						{
							urls.add(atlas.atfURL);
						}
					}
				}
			}
		}

		int sceneLength = 0;
		StringBuilder urlString = new StringBuilder();

		String cfgURL = scene_url.get(scene);
		File cfgFile = new File(root.getOutputFolder().getPath() + cfgURL);
		urlString.append(root.localToCdnURL(cfgURL));
		sceneLength += cfgFile.length();

		String[] urlArray = urls.toArray(new String[] {});
		Arrays.sort(urlArray);
		for (String url : urlArray)
		{
			File file = new File(root.getOutputFolder().getPath() + url);

			urlString.append(",");
			urlString.append(root.localToCdnURL(url));
			sceneLength += file.length();
		}

		return "<imgs files=\"" + urlString.toString() + "\" size=\"" + sceneLength + "\"/>";
	}

	/**
	 * 计算场景加载信息
	 */
	private void measureSceneLoadInfo()
	{
		scene_files = new HashMap<Scene, String>();
		scene_size = new HashMap<Scene, Integer>();

		for (WorldCity city : root.worldTable.getCitys())
		{
			for (Scene scene : city.scenes)
			{
				HashSet<String> urls = new HashSet<String>();
				HashSet<Attire> lowAttires = new HashSet<Attire>();
				HashSet<Attire> highAttires = new HashSet<Attire>();

				for (SceneBackLayer layer : scene.backLayers)
				{
					if (layer.img != null)
					{
						ImageFrame frame = root.frameTable.get(layer.img.imgFile.gid, 1, 1, 0);
						SliceImage slice = config.sliceWriter.getSliceImage(frame);
						if (slice != null)
						{
							urls.add(slice.previewURL);
						}
					}
				}
				for (SceneForeLayer layer : scene.foreLayers)
				{
					if (layer.img != null)
					{
						ImageFrame frame = root.frameTable.get(layer.img.imgFile.gid, 1, 1, 0);
						SliceImage slice = config.sliceWriter.getSliceImage(frame);
						if (slice != null)
						{
							urls.add(slice.previewURL);
						}
					}
				}
				for (SceneAnim anim : scene.backAnims)
				{
					if (anim.attire != null)
					{
						lowAttires.add(anim.attire);
					}
				}
				for (SceneAnim anim : scene.anims)
				{
					if (anim.attire != null)
					{
						lowAttires.add(anim.attire);
					}
				}
				for (SceneNpc npc : scene.npcs)
				{
					if (npc.attire != null)
					{
						highAttires.add(npc.attire);
					}
				}
				// for (SceneDoor door : scene.doors)
				// {
				// if (door.attire != null)
				// {
				// attires.add(door.attire);
				// }
				// }
				for (ScenePart part : scene.parts)
				{
					for (SceneMonsterTimer timer : part.timers)
					{
						for (SceneMonsterBatch batch : timer.getBatchList())
						{
							for (SceneMonster monster : batch.getMonsters())
							{
								if (monster.attire != null)
								{
									highAttires.add(monster.attire);
								}
							}
						}
					}
				}

				for (Attire attire : lowAttires)
				{
					for (AttireAction action : attire.actions)
					{
						for (AttireAnim anim : action.anims)
						{
							for (int i = 0; i < anim.times.length; i++)
							{
								if (anim.times[i] <= 0)
								{
									continue;
								}

								ImageFrame frame = root.frameTable.get(anim.img.gid, anim.row, anim.col, i);
								Atlas atlas = config.atlasWriter.findAtlasByImageFrame(frame);
								if (atlas != null)
								{
									urls.add(atlas.previewURL);
								}
							}
						}
					}
				}

				for (Attire attire : highAttires)
				{
					for (AttireAction action : attire.actions)
					{
						for (AttireAnim anim : action.anims)
						{
							for (int i = 0; i < anim.times.length; i++)
							{
								if (anim.times[i] <= 0)
								{
									continue;
								}

								ImageFrame frame = root.frameTable.get(anim.img.gid, anim.row, anim.col, i);
								Atlas atlas = config.atlasWriter.findAtlasByImageFrame(frame);
								if (atlas != null)
								{
									urls.add(atlas.atfURL);
								}
							}
						}
					}
				}

				String[] urlArray = urls.toArray(new String[urls.size()]);
				Arrays.sort(urlArray);

				int sceneLength = 0;
				StringBuilder urlString = new StringBuilder();

				String cfgURL = scene_url.get(scene);
				File cfgFile = new File(root.getOutputFolder().getPath() + cfgURL);
				urlString.append(root.localToCdnURL(cfgURL));
				sceneLength += cfgFile.length();

				for (int i = 0; i < urlArray.length; i++)
				{
					String url = urlArray[i];
					File file = new File(root.getOutputFolder().getPath() + url);

					urlString.append(",");
					urlString.append(root.localToCdnURL(urlArray[i]));
					sceneLength += file.length();
				}

				scene_files.put(scene, urlString.toString());
				scene_size.put(scene, sceneLength);
			}
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	//
	// 版本信息
	//
	// -------------------------------------------------------------------------------------------------------------------

	@Override
	protected void readHistory(BufferedReader reader) throws Exception
	{
		while (true)
		{
			String line = reader.readLine();
			if (line == null)
			{
				break;
			}

			line = line.trim();
			if (line.isEmpty())
			{
				continue;
			}

			String[] items = line.split("=");
			if (items.length == 2)
			{
				String key = items[0].trim();
				String val = items[1].trim();

				oldTable.put(key, val);
			}
		}
	}

	@Override
	protected void saveHistory(BufferedWriter writer) throws Exception
	{
		// 排序
		String[] keys = newTable.keySet().toArray(new String[newTable.size()]);
		Arrays.sort(keys);

		// 写入
		for (String key : keys)
		{
			writer.write(key + " = " + newTable.get(key) + "\n");
		}

		// 记录输出文件
		for (String url : newTable.values())
		{
			root.addOutputFile(url);
		}
	}
}
