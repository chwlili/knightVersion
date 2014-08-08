package org.game.knight.version.packer.world;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;

import org.chw.util.FileUtil;
import org.chw.util.MD5Util;
import org.chw.util.TextUtil;
import org.chw.util.ZlibUtil;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.base.AbsExporter;
import org.game.knight.version.packer.world.model.Attire;
import org.game.knight.version.packer.world.model.AttireAction;
import org.game.knight.version.packer.world.model.AttireAnim;
import org.game.knight.version.packer.world.model.AttireAudio;
import org.game.knight.version.packer.world.model.AttireFile;
import org.game.knight.version.packer.world.model.ProjectImgFile;
import org.game.knight.version.packer.world.model.ProjectMp3File;
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
import org.game.knight.version.packer.world.scene.WorldFile;
import org.game.knight.version.packer.world.scene.WorldScene;

public class WorldExporter extends AbsExporter
{
	private boolean zip;
	private boolean mobile;
	private boolean writeRegionImg;

	/**
	 * 构造函数
	 * 
	 * @param src
	 * @param dst
	 */
	public WorldExporter(File src, File dst, boolean zip, boolean isMobile, boolean writeRegionImg)
	{
		super("导出世界", src, dst);

		this.zip = zip;
		this.mobile = isMobile;
		this.writeRegionImg = writeRegionImg;
	}

	// -----------------------------------------------------------------
	//
	// 文件表
	//
	// -----------------------------------------------------------------

	private Hashtable<String, File> files = new Hashtable<String, File>();
	private Hashtable<String, ProjectImgFile> imgs = new Hashtable<String, ProjectImgFile>();
	private Hashtable<String, ProjectMp3File> mp3s = new Hashtable<String, ProjectMp3File>();
	private Hashtable<String, AttireFile> attires = new Hashtable<String, AttireFile>();
	private Hashtable<String, File> params = new Hashtable<String, File>();
	private Hashtable<String, Scene> scenes = new Hashtable<String, Scene>();
	private WorldFile link;

	private WorldAttires attireManager;

	/**
	 * 获取文件
	 * 
	 * @param path
	 * @return
	 */
	public File getFile(String path)
	{
		return files.get(path);
	}

	/**
	 * 获取图像文件
	 * 
	 * @param path
	 * @return
	 */
	public ProjectImgFile getImgFile(String path)
	{
		return imgs.get(path);
	}

	/**
	 * 获取音效文件
	 * 
	 * @param path
	 * @return
	 */
	public ProjectMp3File getMp3File(String path)
	{
		return mp3s.get(path);
	}

	/**
	 * 获取装扮文件
	 * 
	 * @param url
	 * @return
	 */
	public AttireFile getAttireFile(String url)
	{
		return attires.get(url);
	}

	/**
	 * 获取场景文件
	 * 
	 * @param url
	 * @return
	 */
	public Scene getSceneFile(String url)
	{
		return scenes.get(url);
	}

	/**
	 * 获取世界文件
	 * 
	 * @return
	 */
	public WorldFile getWorldFile()
	{
		return link;
	}

	// -------------------------------------------------------------------------------
	//
	// 版本信息
	//
	// -------------------------------------------------------------------------------

	private GridImgTable clipTable;
	private TextureSetTable textureSetTable;

	@Override
	protected void openVers()
	{
		super.openVers();

		clipTable = new GridImgTable();
		clipTable.open(new File(getDestDir().getPath() + "/.ver/imgclip"));

		textureSetTable = new TextureSetTable(this);
		textureSetTable.open(new File(getDestDir().getPath() + "/.ver/textures"));
	}

	@Override
	protected void saveVers()
	{
		super.saveVers();

		clipTable.save();

		textureSetTable.save();
	}

	/**
	 * 图像裁切信息表
	 * 
	 * @return
	 */
	public GridImgTable getGridImgTable()
	{
		return clipTable;
	}

	/**
	 * 贴图合并信息表
	 * 
	 * @return
	 */
	public TextureSetTable getTextureSetTable()
	{
		return textureSetTable;
	}

	// -------------------------------------------------------------------------------
	//
	// 导出
	//
	// -------------------------------------------------------------------------------

	@Override
	protected void exportContent() throws Exception
	{
		if (GamePacker.isCancel())
		{
			return;
		}

		// 初始化所有文件
		readDir(getSourceDir());

		if (GamePacker.isCancel())
		{
			return;
		}

		// 同步所有装扮
		syncAttires();

		if (GamePacker.isCancel())
		{
			return;
		}

		GamePacker.beginLogSet("读取世界");
		link.open(this);
		GamePacker.endLogSet();

		if (GamePacker.isCancel())
		{
			return;
		}

		// 同步所有场景
		syncScenes();

		if (GamePacker.isCancel())
		{
			return;
		}

		// 同步世界
		syncWorld();
	}

	/**
	 * 读取所有文件
	 * 
	 * @param dir
	 */
	private void readDir(File dir)
	{
		// 遍历目录
		ArrayList<File> dirs = new ArrayList<File>();
		ArrayList<File> files = new ArrayList<File>();

		dirs.add(dir);
		while (dirs.size() > 0)
		{
			File curr = dirs.remove(0);
			if (curr.isDirectory())
			{
				File[] childs = curr.listFiles();
				for (int i = 0; i < childs.length; i++)
				{
					File child = childs[i];

					if (child.isHidden())
					{
						continue;
					}

					if (child.isDirectory())
					{
						dirs.add(child);
					}
					else
					{
						files.add(child);
					}
				}
			}
		}

		// 分类文件
		for (int i = 0; i < files.size(); i++)
		{
			File file = files.get(i);

			String innerPath = file.getPath().substring(getSourceDir().getPath().length()).replaceAll("\\\\", "/");
			String innerDirPath = file.getParentFile().getPath().substring(getSourceDir().getPath().length()).replaceAll("\\\\", "/");
			String fileID = getChecksumTable().getGID(innerPath);

			GamePacker.progress(String.format("初始化文件(%s/%s) : %s", i + 1, files.size(), innerPath));

			String path = innerPath.toLowerCase();
			if (path.endsWith(".png") || path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".gif"))
			{
				imgs.put(innerPath, new ProjectImgFile(file, innerPath));
			}
			else if (path.endsWith(".mp3"))
			{
				mp3s.put(innerPath, new ProjectMp3File(file, innerPath));
			}
			else if (path.endsWith(".res") || path.endsWith(".attire"))
			{
				attires.put(innerPath, new AttireFile(fileID, file, innerPath, innerDirPath));
			}
			else if (path.endsWith(".textures"))
			{
				params.put(innerPath, file);
			}
			else if (path.endsWith(".scene"))
			{
				scenes.put(innerPath, new Scene(file, innerPath, innerDirPath));
			}
			else if (path.endsWith(".link"))
			{
				link = new WorldFile(file, innerPath, innerDirPath);
			}

			this.files.put(innerPath, file);
		}
	}

	private String attireFileKey;
	private String avatarFileKey;
	private Hashtable<Scene, String> sceneFileKeys;

	/**
	 * 同步所有装扮
	 * 
	 * @throws Exception
	 */
	private void syncAttires() throws Exception
	{
		// 装扮贴图管理器
		attireManager = new WorldAttires(getDestDir().getParentFile().getPath(), getChecksumTable(), clipTable, textureSetTable, writeRegionImg,zip);
		attireManager.build(this, params, attires, scenes);

		// 检测取消
		if (GamePacker.isCancel())
		{
			return;
		}

		// 生成装扮配置
		GamePacker.beginLogSet("输出装扮配置");
		GamePacker.log("生成装扮配置");
		StringBuilder attireText = new StringBuilder();
		attireText.append("<attires>\n");
		attireText.append("\t<textures>\n");
		for (Texture texture : attireManager.getTextures())
		{
			int id = attireManager.getTextureID(texture);
			if (id != 0)
			{
				int size = attireManager.getTextureSize(texture);
				String path = attireManager.getTexturePath(texture);

				attireText.append(String.format("\t\t<texture id=\"%s\" path=\"%s\" size=\"%s\" />\n", id, path, size));
			}
		}
		attireText.append("\t</textures>\n");

		for (AttireFile attireFile : attires.values())
		{
			for (Attire attire : attireFile.getAllAttires())
			{
				if (attire.isAnimAttire() || attire.getKey().startsWith("0_"))
				{
					continue;
				}

				attireText.append(String.format("\t<attire id=\"%s.%s\" name=\"%s\" x=\"%s\" y=\"%s\" width=\"%s\" height=\"%s\">\n", attire.getFileID(), attire.getKey(), attire.getRefKey(), attire.getHitRect().x, attire.getHitRect().y, attire.getHitRect().width, attire.getHitRect().height));
				for (AttireAction action : attire.getActions())
				{
					attireText.append(String.format("\t\t<action id=\"%s\" nameX=\"%s\" nameY=\"%s\" textures=\"%s\" >\n", action.id, action.nameX, action.nameY, attireManager.getActionTextureIDs(action)));
					for (AttireAnim anim : action.animList)
					{
						String ownerChecksumID = getChecksumTable().getGID(anim.img.url);

						attireText.append("\t\t\t<anim x=\"" + anim.x + "\" y=\"" + anim.y + "\" scaleX=\"" + anim.scaleX + "\" scaleY=\"" + anim.scaleY + "\" flip=\"" + anim.flip + "\" actionID=\"" + action.id + "\" groupID=\"" + anim.groupID + "\" layerID=\"" + anim.layerID + "\">\n");

						int regionCount = anim.row * anim.col;
						for (int i = 0; i < regionCount; i++)
						{
							int delay = anim.times[i];
							if (delay > 0)
							{
								Region region = attireManager.getTextureRegion(anim.bagID, ownerChecksumID, anim.row, anim.col, i);
								if (region != null)
								{
									attireText.append("\t\t\t\t<frame texture=\"" + region.getTexturePath() + "\" frameID=\"" + region.getOwnerChecksum() + "_" + i + "\" frameW=\"" + region.getW() + "\" frameH=\"" + region.getH() + "\" delay=\"" + delay + "\"/>\n");
								}
							}
						}

						attireText.append("\t\t\t</anim>\n");
					}
					for (AttireAudio audio : action.audioList)
					{
						String audioURL = exportFile(getChecksumTable().getGID(audio.mp3.url), MD5Util.addSuffix(FileUtil.getFileBytes(audio.mp3.file)),"mp3");
						attireText.append(String.format("\t\t\t<audio path=\"%s\" loop=\"%s\" volume=\"%s\"/>\n", audioURL, audio.loop, audio.volume));
					}
					attireText.append("\t\t</action>\n");
				}
				attireText.append("\t</attire>\n");
			}
		}
		attireText.append("</attires>");

		// 存储文件
		GamePacker.log("保存装扮配置");
		byte[] attireBytes = attireText.toString().getBytes("UTF-8");
		if (zip)
		{
			attireBytes = ZlibUtil.compress(attireBytes);
		}
		String attireBytesKey = (zip ? "zlib_md5" : "md5") + MD5Util.md5Bytes(attireBytes);

		exportFile(attireBytesKey, MD5Util.addSuffix(attireBytes), "cfg");

		attireFileKey = attireBytesKey;

		GamePacker.endLogSet();

		exportUIAttires();
	}

	/**
	 * 导出UI装扮表
	 * 
	 * @throws IOException
	 */
	private void exportUIAttires() throws IOException
	{
		avatarFileKey = new AvatarExport1(this).exportUIAttires(attires, attireManager, zip);
		// attire2FileKey = new AvatarExport2(this).export(attires,
		// attireManager, zip);
	}

	/**
	 * 同步所有场景
	 * 
	 * @throws Exception
	 */
	private void syncScenes() throws Exception
	{
		sceneFileKeys = new Hashtable<Scene, String>();

		// 场景资源
		GamePacker.beginLogSet("合并场景资源");
		for (Scene scene : scenes.values())
		{
			GamePacker.progress("查找装扮");
			HashSet<Attire> attires = new HashSet<Attire>();
			for (SceneAnim anim : scene.getBackAnims())
			{
				if (anim.attire != null)
				{
					attires.add(anim.attire);
				}
			}
			for (SceneAnim anim : scene.getAnims())
			{
				if (anim.attire != null)
				{
					attires.add(anim.attire);
				}
			}
			for (SceneDoor door : scene.getDoors())
			{
				if (door.attire != null)
				{
					attires.add(door.attire);
				}
			}

			Attire[] attireArray = new Attire[attires.size()];
			attireArray = attires.toArray(attireArray);
			Arrays.sort(attireArray, new Comparator<Attire>()
			{
				@Override
				public int compare(Attire o1, Attire o2)
				{
					return o1.getKey().compareTo(o2.getKey());
				}
			});
			scene.setAttires(attireArray);
		}
		GamePacker.endLogSet();

		if (GamePacker.isCancel())
		{
			return;
		}

		// 场景配置
		GamePacker.beginLogSet("输出场景配置");
		for (Scene scene : scenes.values())
		{
			GamePacker.progress("输出场景", scene.getInnerPath());

			String bgsPath = "";
			if (scene.getBackSound() != null)
			{
				bgsPath = exportFile("md5"+getChecksumTable().getGID(scene.getBackSound().url), MD5Util.addSuffix(FileUtil.getFileBytes(scene.getBackSound().file)),"mp3");
			}

			int[] sectionArr = new int[scene.getSections().size()];
			for (int i = 0; i < scene.getSections().size(); i++)
			{
				sectionArr[i] = scene.getSections().get(i).position;
			}

			StringBuilder sb = new StringBuilder();
			sb.append("<scene id=\"" + scene.getSceneID() + "\" type=\"" + scene.getSceneType() + "\" name=\"" + scene.getSceneName() + "\" group=\"" + scene.getSceneGroup() + "\" bgs=\"" + bgsPath + "\" defaultX=\"" + scene.getDefaultX() + "\" defaultY=\"" + scene.getDefaultY() + "\" sceneWidth=\"" + scene.getSceneWidth() + "\" sceneHeight=\"" + scene.getSceneHeight() + "\" viewOffsetX=\"" + scene.getSceneViewXOffset() + "\" viewOffsetY=\"" + scene.getSceneViewYOffset() + "\" beginX=\"" + scene.getSceneBeginX() + "\" timeLimit=\"" + Math.abs(scene.getTimeLimit()) + "\" timeLimitType=\"" + (scene.getTimeLimit() > 0 ? 1 : (scene.getTimeLimit() < 0 ? 2 : 0)) + "\" sections=\"" + TextUtil.formatIntArray(sectionArr) + "\" >\n");

			if (mobile)
			{
				sb.append("\t<grid><![CDATA[" + scene.getSceneGridArray() + "]]></grid>\n");
			}
			else
			{
				sb.append("\t<grid><![CDATA[" + scene.getSceneGrid() + "]]></grid>\n");
			}

			sb.append("\t<sections>\n");
			for (SceneSection section : scene.getSections())
			{
				sb.append("\t\t<section x=\"" + section.position + "\" type=\"" + section.type + "\" />\n");
			}
			sb.append("\t</sections>\n");

			sb.append("\t<attires>\n");
			for (Attire attire : scene.getAttires())
			{
				sb.append("\t\t<attire name=\"" + attire.getKey() + "\" x=\"" + attire.getHitRect().x + "\" y=\"" + attire.getHitRect().y + "\" width=\"" + attire.getHitRect().width + "\" height=\"" + attire.getHitRect().height + "\" nameX=\"" + attire.getNameX() + "\" nameY=\"" + attire.getNameY() + "\">\n");
				for (AttireAction action : attire.getActions())
				{
					sb.append("\t\t\t<action id=\"" + action.id + "\" x=\"" + action.hitRect.x + "\" y=\"" + action.hitRect.y + "\" width=\"" + action.hitRect.width + "\" height=\"" + action.hitRect.height + "\" nameX=\"" + action.nameX + "\" nameY=\"" + action.nameY + "\">\n");
					for (AttireAnim anim : action.animList)
					{
						sb.append("\t\t\t\t<anim x=\"" + anim.x + "\" y=\"" + anim.y + "\" scaleX=\"" + anim.scaleX + "\" scaleY=\"" + anim.scaleY + "\" flip=\"" + anim.flip + "\" actionID=\"" + action.id + "\" groupID=\"" + anim.groupID + "\" layerID=\"" + anim.layerID + "\">\n");

						String ownerChecksumID = getChecksumTable().getGID(anim.img.url);
						int regionCount = anim.row * anim.col;
						for (int i = 0; i < regionCount; i++)
						{
							int delay = anim.times[i];
							if (delay > 0)
							{
								Region region = attireManager.getTextureRegion(anim.bagID, ownerChecksumID, anim.row, anim.col, i);
								if (region != null)
								{
									sb.append("\t\t\t\t\t<frame texture=\"" + region.getTexturePath() + "\" frameID=\"" + region.getOwnerChecksum() + "_" + i + "\" frameW=\"" + region.getW() + "\" frameH=\"" + region.getH() + "\" delay=\"" + delay + "\"/>\n");
								}
								else
								{
									GamePacker.error("场景动画图像区域未换到！ " + scene.getInnerPath() + "     attireName=" + attire.getKey() + "  action=" + action.id + "  anim=" + anim.groupID + "," + anim.layerID);
								}
							}
						}
						sb.append("\t\t\t\t</anim>\n");
					}
					for (AttireAudio audio : action.audioList)
					{
						String audioURL = exportFile(getChecksumTable().getGID(audio.mp3.url), audio.mp3.file);
						sb.append(String.format("\t\t\t<audio path=\"%s\" loop=\"%s\" volume=\"%s\"/>\n", audioURL, audio.loop, audio.volume));
					}
					sb.append("\t\t\t</action>\n");
				}
				sb.append("\t\t</attire>\n");
			}
			sb.append("\t</attires>\n");

			sb.append("\t<layers>\n");
			for (SceneBackLayer layer : scene.getBackLayers())
			{
				if (layer.img != null)
				{
					Region region = attireManager.getTextureRegion(layer.atfGroup, getChecksumTable().getGID(layer.img.url), 1, 1, 0);
					if (region != null)
					{
						sb.append("\t\t<layer x=\"" + layer.x + "\" y=\"" + layer.y + "\" speed=\"" + layer.speed + "\" texture=\"" + region.getTexturePath() + "\" textureName=\"" + region.getOwnerChecksum() + "_0\" />\n");
					}
					else
					{
						GamePacker.error("背景图层图像区域未换到！ " + scene.getInnerPath() + "     layerName=" + layer.name + "  img=" + layer.img.url);
					}
				}
			}
			sb.append("\t</layers>\n");

			sb.append("\t<foreLayers>\n");
			for (SceneForeLayer layer : scene.getForeLayers())
			{
				if (layer.img != null)
				{
					Region region = attireManager.getTextureRegion(layer.atfGroup, getChecksumTable().getGID(layer.img.url), 1, 1, 0);
					if (region != null)
					{
						sb.append("\t\t<layer x=\"" + layer.x + "\" y=\"" + layer.y + "\" width=\"" + layer.w + "\" speed=\"" + layer.speed + "\" texture=\"" + region.getTexturePath() + "\" textureName=\"" + region.getOwnerChecksum() + "_0\" />\n");
					}
					else
					{
						GamePacker.error("自动图层图像区域未换到！ " + scene.getInnerPath() + "     layerName=" + layer.name + "  img=" + layer.img.url);
					}
				}
			}
			sb.append("\t</foreLayers>\n");

			sb.append("\t<backAnims>\n");
			for (SceneAnim anim : scene.getBackAnims())
			{
				sb.append("\t\t<anim x=\"" + anim.x + "\" y=\"" + anim.y + "\" offsetX=\"" + anim.offsetX + "\" offsetY=\"" + anim.offsetY + "\" direction=\"" + anim.direction + "\" attire=\"" + (anim.attire != null ? anim.attire.getRefKey() : "") + "\"/>\n");
			}
			sb.append("\t</backAnims>\n");

			sb.append("\t<anims>\n");
			for (SceneAnim anim : scene.getAnims())
			{
				sb.append("\t\t<anim x=\"" + anim.x + "\" y=\"" + anim.y + "\" offsetX=\"" + anim.offsetX + "\" offsetY=\"" + anim.offsetY + "\" direction=\"" + anim.direction + "\" attire=\"" + (anim.attire != null ? anim.attire.getRefKey() : "") + "\"/>\n");
			}
			sb.append("\t</anims>\n");

			sb.append("\t<npcs>\n");
			for (SceneNpc npc : scene.getNpcs())
			{
				sb.append("\t\t<npc id=\"" + npc.id + "\" x=\"" + npc.x + "\" y=\"" + npc.y + "\" direction=\"" + npc.direction + "\" attire=\"" + (npc.attire != null ? npc.attire.getRefKey() : "") + "\"/>\n");
			}
			sb.append("\t</npcs>\n");

			sb.append("\t<doors>\n");
			for (SceneDoor door : scene.getDoors())
			{
				SceneHot hot = door.getHot();
				if (hot != null)
				{
					sb.append("\t\t<door x=\"" + door.x + "\" y=\"" + door.y + "\" offsetX=\"0\" offsetY=\"0\" direction=\"" + door.direction + "\" attire=\"" + (door.attire != null ? door.attire.getRefKey() : "") + "\">\n");
					sb.append(String.format("\t\t\t<hot x=\"%s\" y=\"%s\" width=\"%s\" height=\"%s\" acceptableQuests=\"%s\" acceptedQuests=\"%s\" submitableQuests=\"%s\" submitedQuests=\"%s\">\n", hot.x, hot.y, hot.width, hot.height, hot.acceptableQuests, hot.acceptedQuests, hot.submitableQuests, hot.submitedQuests));
					for (SceneHotLink line : hot.getLinks())
					{
						sb.append(String.format("\t\t\t\t<link toID=\"%s\" toName=\"%s\" toX=\"%s\" toY=\"%s\" />\n", line.getToID(), line.getToName(), line.getToX(), line.getToY()));
					}
					sb.append("\t\t\t</hot>\n");
					sb.append("\t\t</door>\n");
				}
			}
			sb.append("\t</doors>\n");

			int trapID = 1;
			sb.append("\t<traps>\n");
			for (SceneTrap trap : scene.getTraps())
			{
				sb.append(String.format("\t\t<trap id=\"%s\" type=\"%s\" x=\"%s\" y=\"%s\" width=\"%s\" height=\"%s\" quest=\"%s\"><![CDATA[%s]]></trap>\n", trapID, trap.type, trap.x, trap.y, trap.width, trap.height, trap.quest, trap.content));
				trapID++;
			}
			sb.append("\t</traps>\n");

			sb.append("\t<monsters>\n");
			HashSet<Integer> monsterIDs = new HashSet<Integer>();
			for (ScenePart part : scene.getParts())
			{
				for (SceneMonsterTimer timer : part.timers)
				{
					for (SceneMonsterBatch batch : timer.getBatchList())
					{
						for (SceneMonster monster : batch.getMonsters())
						{
							if (!monsterIDs.contains(monster.monsterID))
							{
								sb.append("\t\t<monster id=\"" + monster.monsterID + "\" x=\"" + monster.x + "\" y=\"" + monster.y + "\" dir=\"" + monster.dir + "\" attire=\"" + (monster.attire != null ? monster.attire.getFileID() + "." + monster.attire.getKey() : "") + "\" />\n");
								monsterIDs.add(monster.monsterID);
							}
						}
					}
				}
			}
			sb.append("\t</monsters>\n");

			sb.append("</scene>");

			// 导出场景
			byte[] sceneBytes = sb.toString().getBytes("UTF-8");
			if (zip)
			{
				sceneBytes = ZlibUtil.compress(sceneBytes);
			}
			String sceneBytesKey = (zip ? "zlib_md5" : "zlib") + MD5Util.md5Bytes(sceneBytes);
			exportFile(sceneBytesKey, MD5Util.addSuffix(sceneBytes), "xml");
			sceneFileKeys.put(scene, sceneBytesKey);

			if (GamePacker.isCancel())
			{
				return;
			}
		}
		GamePacker.endLogSet();
	}

	/**
	 * 同步世界
	 * 
	 * @throws Exception
	 */
	private void syncWorld() throws Exception
	{
		if (link == null)
		{
			return;
		}

		Hashtable<Scene, Integer> scene_size = new Hashtable<Scene, Integer>();
		Hashtable<Scene, String> scene_path = new Hashtable<Scene, String>();

		GamePacker.beginLogSet("输出世界配置");
		GamePacker.log("生成世界配置");
		StringBuilder sb = new StringBuilder();
		sb.append("<worldDB>\n");
		sb.append("\t<citys>\n");
		for (WorldCity city : link.getCitys())
		{
			sb.append(String.format("\t\t<city id=\"%s\" name=\"%s\">\n", city.id, city.name));
			for (WorldScene worldScene : city.scenes)
			{
				Scene scene = worldScene.data;

				int sceneSize = 0;
				StringBuilder files = new StringBuilder();

				HashSet<String> outterPaths = new HashSet<String>();

				sceneSize += getExportedFileSize(sceneFileKeys.get(scene));
				files.append(getExportedFileUrl(sceneFileKeys.get(scene)));

				HashSet<Texture> textureHash = new HashSet<Texture>();
				HashSet<Attire> attires = new HashSet<Attire>();
				for (SceneAnim anim : scene.getAnims())
				{
					if (anim.attire != null)
					{
						attires.add(anim.attire);
					}
				}
				for (SceneAnim anim : scene.getBackAnims())
				{
					if (anim.attire != null)
					{
						attires.add(anim.attire);
					}
				}
				for (SceneNpc npc : scene.getNpcs())
				{
					if (npc.attire != null)
					{
						attires.add(npc.attire);
					}
				}
				for (ScenePart part : scene.getParts())
				{
					for (SceneMonsterTimer timer : part.timers)
					{
						for (SceneMonsterBatch batch : timer.getBatchList())
						{
							for (SceneMonster monster : batch.getMonsters())
							{
								if (monster.attire != null)
								{
									attires.add(monster.attire);
								}
							}
						}
					}
				}
				for (Attire attire : attires)
				{
					for (AttireAction action : attire.getActions())
					{
						Texture[] textures = attireManager.getActionTextures(action);
						for (Texture texture : textures)
						{
							textureHash.add(texture);
						}
					}
				}
				for (SceneBackLayer layer : scene.getBackLayers())
				{
					if (layer.img != null)
					{
						ProjectImgFile img = layer.img;
						Texture texture = attireManager.getImgTextures(layer.atfGroup, getChecksumTable().getGID(img.url));
						if (texture != null)
						{
							textureHash.add(texture);
						}
					}
				}
				for (SceneForeLayer layer : scene.getForeLayers())
				{
					if (layer.img != null)
					{
						ProjectImgFile img = layer.img;
						Texture texture = attireManager.getImgTextures(layer.atfGroup, getChecksumTable().getGID(img.url));
						if (texture != null)
						{
							textureHash.add(texture);
						}
					}
				}

				ArrayList<String> fileURLs = new ArrayList<String>();
				for (Texture texture : textureHash)
				{
					String url = texture.getAtfFilePath();
					String atfPath = getDestDir().getParentFile().getPath() + url.replace(".atf", ".atf");
					//String xmlPath = getDestDir().getParentFile().getPath() + url.replace(".atf", ".xml");

					if (!outterPaths.contains(atfPath))
					{
						outterPaths.add(atfPath);

						File atfFile = new File(atfPath);

						sceneSize += atfFile.length();
						fileURLs.add(url.replace(".atf", ".atf"));
					}
//					if (!outterPaths.contains(xmlPath))
//					{
//						outterPaths.add(xmlPath);
//
//						File xmlFile = new File(xmlPath);
//
//						sceneSize += xmlFile.length();
//						fileURLs.add(url.replace(".atf", ".xml"));
//					}
				}

				String[] fileURL_Array = fileURLs.toArray(new String[fileURLs.size()]);
				Arrays.sort(fileURL_Array);
				for (String url : fileURL_Array)
				{
					files.append("," + url);
				}

				scene.restHotArea(this);

				scene_size.put(scene, sceneSize);
				scene_path.put(scene, files.toString());

				sb.append(String.format("\t\t\t<scene id=\"%s\" name=\"%s\" type=\"%s\" group=\"%s\" level=\"%s\" achieve=\"%s\" finishQuest=\"%s\" acceptQuest=\"%s\" />\n", worldScene.sceneID, worldScene.sceneName, worldScene.sceneType, worldScene.sceneGroup, 0, "-", "-", "-"));
			}
			sb.append("\t\t</city>\n");
		}
		sb.append("\t</citys>\n");
		sb.append("</worldDB>");
		GamePacker.log("保存世界配置");
		byte[] worldBytes = sb.toString().getBytes("UTF-8");
		if (zip)
		{
			worldBytes = ZlibUtil.compress(worldBytes);
		}
		String worldBytesKey = (zip ? "zlib_md5" : "md5") + MD5Util.md5Bytes(worldBytes);
		exportFile(worldBytesKey, MD5Util.addSuffix(worldBytes), "cfg");
		GamePacker.endLogSet();

		if (GamePacker.isCancel())
		{
			return;
		}
		
		GamePacker.beginLogSet("输出汇总信息");
		GamePacker.log("生成汇总信息");
		StringBuilder txt = new StringBuilder();
		txt.append("<project>\n");
		txt.append("\t<configs>\n");
		txt.append(String.format("\t\t<config name=\"%s\" path=\"%s\" size=\"%s\"/>\n", "uiAvatar", getExportedFileUrl(avatarFileKey), getExportedFileSize(avatarFileKey)));
		txt.append(String.format("\t\t<config name=\"%s\" path=\"%s\" size=\"%s\"/>\n", "world", getExportedFileUrl(worldBytesKey), getExportedFileSize(worldBytesKey)));
		txt.append(String.format("\t\t<config name=\"%s\" path=\"%s\" size=\"%s\"/>\n", "attire", getExportedFileUrl(attireFileKey), getExportedFileSize(attireFileKey)));
		txt.append("\t</configs>\n");

		txt.append("\t<attires>\n");
		Hashtable<String, Long> actionFileUrls = new Hashtable<String, Long>();
		StringBuilder roles = new StringBuilder();
		StringBuilder equips = new StringBuilder();
		StringBuilder effects = new StringBuilder();
		StringBuilder labels = new StringBuilder();
		StringBuilder horses=new StringBuilder();
		for (AttireFile attireFile : attires.values())
		{
			for (Attire attire : attireFile.getAllAttires())
			{
				if (attire.isAnimAttire() || attire.getKey().startsWith("0_"))
				{
					continue;
				}

				String[] params = attire.getParams();
				if (params.length == 0)
				{
					continue;
				}

				if (params[0].equals("1"))
				{
					// 装扮
					if (params.length >= 3)
					{
						roles.append(String.format("\t\t<role faction=\"%s\" sectLv=\"%s\" name=\"%s\">\n", params[1], params[2], attire.getRefKey()));
						for (AttireAction action : attire.getActions())
						{
							if (action.animList.size() > 0)
							{
								roles.append(String.format("\t\t\t<action id=\"%s\" size=\"%s\" files=\"%s\"/>\n", action.id, attireManager.getActionSize(action), attireManager.getActionPaths(action)));

								String[] urls = attireManager.getActionPaths(action).split("\\,");
								for (String url : urls)
								{
									if (!actionFileUrls.containsKey(url))
									{
										File actionFile = new File(getDestDir().getParentFile().getPath() + url);
										if (actionFile.exists())
										{
											actionFileUrls.put(url, actionFile.length());
										}
									}
								}
							}
						}
						roles.append(String.format("\t\t</role>\n"));
					}
					else
					{
						GamePacker.error("职业关联的装扮命名错误：" + attire.getRefKey() + "   (应该为：1_职业ID_职业等级_名称)");
					}
				}
				else if (params[0].equals("2"))
				{
					// 装备
					if (params.length >= 4)
					{
						equips.append(String.format("\t\t<equip fromID=\"%s\" toID=\"%s\" faction=\"%s\" name=\"%s\">\n", params[1], params[2], params[3], attire.getRefKey()));
						for (AttireAction action : attire.getActions())
						{
							if (action.animList.size() > 0)
							{
								equips.append(String.format("\t\t\t<action id=\"%s\" size=\"%s\" files=\"%s\"/>\n", action.id, attireManager.getActionSize(action), attireManager.getActionPaths(action)));

								String[] urls = attireManager.getActionPaths(action).split("\\,");
								for (String url : urls)
								{
									if (!actionFileUrls.containsKey(url))
									{
										File actionFile = new File(getDestDir().getParentFile().getPath() + url);
										if (actionFile.exists())
										{
											actionFileUrls.put(url, actionFile.length());
										}
									}
								}
							}
						}
						equips.append(String.format("\t\t</equip>\n"));
					}
					else
					{
						GamePacker.error("与装备关联的装扮命名错误：" + attire.getRefKey() + "   (应该为：2_起始ID_结束ID_职业ID_名称)");
					}
				}
				else if (params[0].equals("3"))
				{
					// 效果
					effects.append(String.format("\t\t<effect effectID=\"%s\">\n", attire.getRefKey()));
					for (AttireAction action : attire.getActions())
					{
						if (action.animList.size() > 0)
						{
							effects.append(String.format("\t\t\t<action id=\"0\" size=\"%s\" files=\"%s\"/>\n", attireManager.getActionSize(action), attireManager.getActionPaths(action)));

							String[] urls = attireManager.getActionPaths(action).split("\\,");
							for (String url : urls)
							{
								if (!actionFileUrls.containsKey(url))
								{
									File actionFile = new File(getDestDir().getParentFile().getPath() + url);
									if (actionFile.exists())
									{
										actionFileUrls.put(url, actionFile.length());
									}
								}
							}
						}
					}
					effects.append(String.format("\t\t</effect>\n"));
				}
				else if (params[0].equals("4"))
				{
					// 怪物
				}
				else if (params[0].equals("5"))
				{
					// 标签
					// TextureSet
					// textureSet=textureSetTable.getTextureSet(attire.getTextureSetKey());
					// labels.append(String.format("\t\t<label labelID=\"%s\" size=\"%s\" files=\"%s\"/>\n",attire.getRefKey(),getTextureSetSize(textureSet),getTextureSetURLs(textureSet)));
				}
				else if (params[0].equals("6"))
				{
					// 刀光
					if (params.length >= 3)
					{
						roles.append(String.format("\t\t<roleEffect faction=\"%s\" sectLv=\"%s\" name=\"%s\">\n", params[1], params[2], attire.getRefKey()));
						for (AttireAction action : attire.getActions())
						{
							if (action.animList.size() > 0)
							{
								roles.append(String.format("\t\t\t<action id=\"%s\" size=\"%s\" files=\"%s\"/>\n", action.id, attireManager.getActionSize(action), attireManager.getActionPaths(action)));

								String[] urls = attireManager.getActionPaths(action).split("\\,");
								for (String url : urls)
								{
									if (!actionFileUrls.containsKey(url))
									{
										File actionFile = new File(getDestDir().getParentFile().getPath() + url);
										if (actionFile.exists())
										{
											actionFileUrls.put(url, actionFile.length());
										}
									}
								}
							}
						}
						roles.append(String.format("\t\t</roleEffect>\n"));
					}
					else
					{
						GamePacker.error("与刀光关联的装扮命名错误：" + attire.getRefKey() + "   (应该为：6_职业ID_名称)");
					}
				}
				else if (params[0].equals("7"))
				{
				}
				else if(params[0].equals("8"))
				{
					if (params.length >= 2)
					{
						horses.append(String.format("\t\t<horse horseID=\"%s\" name=\"%s\">\n", params[1],attire.getRefKey()));
						for (AttireAction action : attire.getActions())
						{
							if (action.animList.size() > 0)
							{
								horses.append(String.format("\t\t\t<action id=\"%s\" size=\"%s\" files=\"%s\"/>\n", action.id, attireManager.getActionSize(action), attireManager.getActionPaths(action)));
	
								String[] urls = attireManager.getActionPaths(action).split("\\,");
								for (String url : urls)
								{
									if (!actionFileUrls.containsKey(url))
									{
										File actionFile = new File(getDestDir().getParentFile().getPath() + url);
										if (actionFile.exists())
										{
											actionFileUrls.put(url, actionFile.length());
										}
									}
								}
							}
						}
						horses.append(String.format("\t\t</horse>\n"));
					}
					else
					{
						GamePacker.error("与坐骑关联的装扮命名错误：" + attire.getRefKey() + "   (应该为：0_坐骑ID_名称)");
					}
				}
			}
		}

		txt.append("\t\t<files>\n");
		String[] rowKeys = actionFileUrls.keySet().toArray(new String[actionFileUrls.size()]);
		Arrays.sort(rowKeys);
		for (String rowKey : rowKeys)
		{
			txt.append(String.format("\t\t\t<file url=\"%s\" size=\"%s\" />\n", rowKey, actionFileUrls.get(rowKey)));
		}
		txt.append("\t\t</files>\n");

		txt.append(roles);
		txt.append(equips);
		txt.append(effects);
		txt.append(labels);
		txt.append(horses);
		txt.append("\t</attires>\n");

		txt.append("\t<scenes>\n");
		for (WorldCity city : link.getCitys())
		{
			for (WorldScene worldScene : city.scenes)
			{
				Scene scene = worldScene.data;
				txt.append(String.format("\t\t<scene id=\"%s\" type=\"%s\" size=\"%s\" files=\"%s\" />\n", worldScene.sceneID, worldScene.sceneType, scene_size.get(scene), scene_path.get(scene)));
			}
		}
		txt.append("\t</scenes>\n");
		txt.append("</project>");
		
		GamePacker.log("保存汇总信息");
		FileUtil.writeFile(new File(getDestDir().getPath() + "/db.xml"), txt.toString().getBytes("UTF-8"));
		GamePacker.endLogSet();
		
		//
		StringBuilder second_ver = new StringBuilder();
		second_ver.append("<project>\n");
		second_ver.append("\t<configs>\n");
		second_ver.append(String.format("\t\t<config name=\"%s\" path=\"%s\" size=\"%s\"/>\n", "uiAvatar", getExportedFileUrl(avatarFileKey), getExportedFileSize(avatarFileKey)));
		second_ver.append(String.format("\t\t<config name=\"%s\" path=\"%s\" size=\"%s\"/>\n", "world", getExportedFileUrl(worldBytesKey), getExportedFileSize(worldBytesKey)));
		//second_ver.append(String.format("\t\t<config name=\"%s\" path=\"%s\" size=\"%s\"/>\n", "attire", getExportedFileUrl(attireFileKey), getExportedFileSize(attireFileKey)));
		second_ver.append("\t</configs>\n");

		AvatarExport2 test=new AvatarExport2(this);
		test.export(scenes, attires, attireManager, zip, mobile);
		second_ver.append(test.getVersionData());
		
		second_ver.append("</project>");
		
		GamePacker.log("保存汇总信息");
		FileUtil.writeFile(new File(getDestDir().getPath() + "/db1.xml"), second_ver.toString().getBytes("UTF-8"));
		GamePacker.endLogSet();

		// 生成文件列表
		String[] fileUrls = getExportedFileUrls();
		String[] textureUrls = getTextureSetTable().getTextureFileURLs();

		String[] urlList = new String[fileUrls.length + textureUrls.length];
		for (int i = 0; i < fileUrls.length; i++)
		{
			urlList[i] = fileUrls[i];
		}
		for (int i = 0; i < textureUrls.length; i++)
		{
			urlList[i + fileUrls.length] = textureUrls[i];
		}
		Arrays.sort(urlList);

		GamePacker.beginLogSet("输出文件汇总");
		GamePacker.log("生成文件汇总");
		StringBuilder filesSB = new StringBuilder();
		for (String url : urlList)
		{
			filesSB.append(url + "\n");
		}
		GamePacker.log("保存文件汇总");
		FileUtil.writeFile(new File(getDestDir().getPath() + "/db.ver"), filesSB.toString().getBytes("UTF-8"));
		GamePacker.endLogSet();
	}

}
