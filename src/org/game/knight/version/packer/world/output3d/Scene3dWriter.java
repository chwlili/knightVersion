package org.game.knight.version.packer.world.output3d;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.chw.util.FileUtil;
import org.chw.util.MD5Util;
import org.chw.util.TextUtil;
import org.game.knight.version.packer.GamePacker;
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
import org.game.knight.version.packer.world.task.RootTask;

public class Scene3dWriter
{
	private RootTask root;

	private HashMap<Scene, String> scene_url;

	private HashMap<String, String> newTable;
	private HashMap<String, String> oldTable;

	/**
	 * 构造函数
	 * 
	 * @param root
	 */
	public Scene3dWriter(RootTask root)
	{
		this.root = root;
	}

	/**
	 * 获取场景配置的输出路径
	 * 
	 * @param scene
	 * @return
	 */
	public String getSceneCfgURL(Scene scene)
	{
		return scene_url.get(scene);
	}

	/**
	 * 开始
	 */
	public void start()
	{
		openVer();

		writerAllSceneCfg();
	}

	/**
	 * 输出所有场景配置
	 */
	private void writerAllSceneCfg()
	{
		scene_url = new HashMap<Scene, String>();

		for (Scene scene : root.getWorldTable().getAllScene())
		{
			writeSceneCfg(scene);

			if (root.isCancel())
			{
				return;
			}
		}
	}

	/**
	 * 输出场景配置
	 * 
	 * @param scene
	 */
	private void writeSceneCfg(Scene scene)
	{
		GamePacker.progress("输出场景", scene.sceneID + "." + scene.sceneName);

		String bgsPath = "";
		if (scene.bgs != null)
		{
			bgsPath = root.getMp3Writer().getMp3URL(scene.bgs);
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

			SliceImage img = root.getSliceImageWriter().getSliceImage(layer.img.imgFile);
			if (img == null)
			{
				continue;
			}

			sb.append("\t\t<layer x=\"" + layer.x + "\" y=\"" + layer.y + "\" speed=\"" + layer.speed + "\" width=\"" + img.img.width + "\" height=\"" + img.img.height + "\" preview=\"" + img.previewURL + "\" row=\"" + img.sliceRow + "\" col=\"" + img.sliceCol + "\" />\n");
		}
		sb.append("\t</layers>\n");

		sb.append("\t<foreLayers>\n");
		for (SceneForeLayer layer : scene.foreLayers)
		{
			if (layer.img == null)
			{
				continue;
			}

			SliceImage img = root.getSliceImageWriter().getSliceImage(layer.img.imgFile);
			if (img == null)
			{
				continue;
			}

			sb.append("\t\t<layer x=\"" + layer.x + "\" y=\"" + layer.y + "\" width=\"" + layer.w + "\" speed=\"" + layer.speed + "\" width=\"" + img.img.width + "\" height=\"" + img.img.height + "\" preview=\"" + img.previewURL + "\" row=\"" + img.sliceRow + "\" col=\"" + img.sliceCol + "\" />\n");
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
		byte[] bytes = null;
		try
		{
			bytes = sb.toString().getBytes("UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			GamePacker.error(e);
			return;
		}

		String md5 = /* (zip ? "zlib_" : "")+ */MD5Util.md5Bytes(bytes);
		String url = oldTable.get(md5);

		if (url == null)
		{
			url = root.getGlobalOptionTable().getNextExportFile() + ".cfg";
			FileUtil.writeFile(new File(root.getOutputFolder().getPath() + url), bytes);
		}

		newTable.put(md5, url);

		scene_url.put(scene, url);
	}

	// -------------------------------------------------------------------------------------------------------------------
	//
	// 版本信息
	//
	// -------------------------------------------------------------------------------------------------------------------

	/**
	 * 获取版本文件
	 * 
	 * @return
	 */
	private File getVerFile()
	{
		return new File(root.getOutputFolder().getPath() + File.separatorChar + ".ver" + File.separatorChar + "3dScene");
	}

	/**
	 * 打开版本信息
	 */
	private void openVer()
	{
		newTable = new HashMap<String, String>();
		oldTable = new HashMap<String, String>();

		if (!getVerFile().exists())
		{
			return;
		}

		String text = null;
		try
		{
			text = new String(FileUtil.getFileBytes(getVerFile()), "utf8");
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return;
		}

		String[] lines = text.split("\n");
		for (String line : lines)
		{
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

	/**
	 * 保存版本信息
	 */
	public void saveVer()
	{
		if (newTable == null)
		{
			return;
		}

		String[] keys = newTable.keySet().toArray(new String[newTable.size()]);
		Arrays.sort(keys);

		StringBuilder output = new StringBuilder();
		for (String key : keys)
		{
			output.append(key + " = " + newTable.get(key) + "\n");
		}

		try
		{
			FileUtil.writeFile(getVerFile(), output.toString().getBytes("utf8"));
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}
}
