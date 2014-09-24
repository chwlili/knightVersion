package org.game.knight.version.packer.world.output2d;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import org.chw.util.FileUtil;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.world.BaseWriter;
import org.game.knight.version.packer.world.WorldWriter;
import org.game.knight.version.packer.world.model.Attire;
import org.game.knight.version.packer.world.model.AttireAction;
import org.game.knight.version.packer.world.model.AttireAnim;
import org.game.knight.version.packer.world.model.ImageFrame;
import org.game.knight.version.packer.world.model.Scene;

public class Config2d extends BaseWriter
{
	public final AttireSwfWriter attireSwfWriter;
	public final Config2dAttireWriter attireWriter;
	public final Config2dSceneWriter sceneWriter;

	/**
	 * 构造函数
	 * 
	 * @param root
	 */
	public Config2d(WorldWriter root)
	{
		super(root, null);
		this.attireSwfWriter = new AttireSwfWriter(root);
		this.attireWriter = new Config2dAttireWriter(root, attireSwfWriter);
		this.sceneWriter = new Config2dSceneWriter(root, attireSwfWriter);
	}

	@Override
	protected void exec() throws Exception
	{
		ArrayList<BaseWriter> writers = new ArrayList<BaseWriter>();
		writers.add(attireSwfWriter);
		writers.add(attireWriter);
		writers.add(sceneWriter);

		for (BaseWriter write : writers)
		{
			write.run();
		}

		if (!root.isCancel())
		{
			writeDB();
		}
	}

	@Override
	public void saveVer() throws Exception
	{
		ArrayList<BaseWriter> writers = new ArrayList<BaseWriter>();
		writers.add(attireSwfWriter);
		writers.add(attireWriter);
		writers.add(sceneWriter);

		for (BaseWriter write : writers)
		{
			write.saveVer();
		}
	}

	/**
	 * 输出汇总信息
	 */
	private void writeDB()
	{
		String attireKey = attireWriter.getOutputKey();
		String attireURL = attireWriter.getOutputURL();
		File attireFile = new File(root.getOutputFolder().getPath() + attireURL);

		String worldURL = sceneWriter.getWorldCfgURL();
		File worldFile = new File(root.getOutputFolder().getPath() + worldURL);

		String uiAttireURL = root.gameUIAttireWriter.getCfgFileURL();
		File uiAttireFile = new File(root.getOutputFolder().getPath() + uiAttireURL);

		StringBuilder txt = new StringBuilder();
		txt.append("<project>\n");
		txt.append("\t<configs>\n");
		txt.append(String.format("\t\t<config name=\"%s\" path=\"%s\" size=\"%s\"/>\n", "uiAvatar", root.localToCdnURL(uiAttireURL), uiAttireFile.length()));
		txt.append(String.format("\t\t<config name=\"%s\" path=\"%s\" size=\"%s\"/>\n", attireKey, root.localToCdnURL(attireURL), attireFile.length()));
		txt.append(String.format("\t\t<config name=\"%s\" path=\"%s\" size=\"%s\"/>\n", "world", root.localToCdnURL(worldURL), worldFile.length()));
		txt.append("\t</configs>\n");
		txt.append(getAttireSummay());
		txt.append(getSceneSummay());
		txt.append("</project>");

		try
		{
			FileUtil.writeFile(new File(root.getOutputFolder().getPath() + "/db1.xml"), txt.toString().getBytes("UTF-8"));
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 获取场景配置汇总
	 * 
	 * @return
	 */
	private String getAttireSummay()
	{
		StringBuilder txt = new StringBuilder();
		txt.append("\t<attires>\n");

		HashMap<String, Integer> url_size = new HashMap<String, Integer>();
		HashMap<String, Integer> url_id = new HashMap<String, Integer>();
		HashMap<AttireAction, HashSet<String>> action_urls = new HashMap<AttireAction, HashSet<String>>();
		HashMap<AttireAction, String> action_urlIDs = new HashMap<AttireAction, String>();

		for (Attire attire : root.attireTable.getAllAttire())
		{
			if (!filterAttire(attire))
			{
				continue;
			}

			for (AttireAction action : attire.actions)
			{
				if (action.anims.length <= 0)
				{
					continue;
				}

				for (AttireAnim anim : action.anims)
				{
					for (int i = 0; i < anim.times.length; i++)
					{
						if (anim.times[i] > 0)
						{
							ImageFrame frame = root.frameTable.get(anim.img.gid, anim.row, anim.col, i);
							if (frame == null)
							{
								continue;
							}

							if (!action_urls.containsKey(action))
							{
								action_urls.put(action, new HashSet<String>());
							}

							String url = attireSwfWriter.getFrameFileURL(frame);
							File file = new File(root.getOutputFolder().getPath() + url);

							url_size.put(url, (int) file.length());
							action_urls.get(action).add(url);
						}
					}
				}
			}
		}

		// 排序URL
		String[] keys = url_size.keySet().toArray(new String[url_size.size()]);
		Arrays.sort(keys);

		// 确定URL编号
		for (int i = 0; i < keys.length; i++)
		{
			url_id.put(keys[i], i + 1);
		}

		// 输出URL列表
		txt.append("\t\t<files>\n");
		for (String key : keys)
		{
			txt.append(String.format("\t\t\t<file id=\"%s\" url=\"%s\" size=\"%s\" />\n", url_id.get(key), root.localToCdnURL(key), url_size.get(key)));
		}
		txt.append("\t\t</files>\n");

		// 排序动作的URL ID列表
		for (AttireAction action : action_urls.keySet())
		{
			HashSet<String> urls = action_urls.get(action);
			int[] ids = new int[urls.size()];

			int index = 0;
			for (String url : urls)
			{
				ids[index] = url_id.get(url);
				index++;
			}

			Arrays.sort(ids);

			StringBuilder idString = new StringBuilder();
			for (int id : ids)
			{
				if (idString.length() > 0)
				{
					idString.append(",");
				}
				idString.append(id);
			}

			action_urlIDs.put(action, idString.toString());
		}

		StringBuilder roles = new StringBuilder();
		StringBuilder equips = new StringBuilder();
		StringBuilder effects = new StringBuilder();
		StringBuilder labels = new StringBuilder();
		StringBuilder horses = new StringBuilder();

		for (Attire attire : root.attireTable.getAllAttire())
		{
			if (!filterAttire(attire))
			{
				continue;
			}

			String[] params = attire.typeParams;
			if (params[0].equals("1"))
			{
				// 装扮
				if (params.length >= 3)
				{
					roles.append(String.format("\t\t<role faction=\"%s\" sectLv=\"%s\" name=\"%s\">\n", params[1], params[2], attire.name));
					for (AttireAction action : attire.actions)
					{
						if (action.anims.length > 0)
						{
							roles.append(String.format("\t\t\t<action id=\"%s\" files=\"%s\"/>\n", action.id, action_urlIDs.get(action)));
						}
					}
					roles.append(String.format("\t\t</role>\n"));
				}
				else
				{
					GamePacker.error("职业关联的装扮命名错误：" + attire.nativeName + "   (应该为：1_职业ID_职业等级_名称)");
				}
			}
			else if (params[0].equals("2"))
			{
				// 装备
				if (params.length >= 4)
				{
					equips.append(String.format("\t\t<equip fromID=\"%s\" toID=\"%s\" faction=\"%s\" name=\"%s\">\n", params[1], params[2], params[3], attire.name));
					for (AttireAction action : attire.actions)
					{
						if (action.anims.length > 0)
						{
							equips.append(String.format("\t\t\t<action id=\"%s\" files=\"%s\"/>\n", action.id, action_urlIDs.get(action)));
						}
					}
					equips.append(String.format("\t\t</equip>\n"));
				}
				else
				{
					GamePacker.error("与装备关联的装扮命名错误：" + attire.nativeName + "   (应该为：2_起始ID_结束ID_职业ID_名称)");
				}
			}
			else if (params[0].equals("3"))
			{
				// 效果
				effects.append(String.format("\t\t<effect effectID=\"%s\">\n", attire.name));
				for (AttireAction action : attire.actions)
				{
					if (action.anims.length > 0)
					{
						effects.append(String.format("\t\t\t<action id=\"0\" files=\"%s\"/>\n", action_urlIDs.get(action)));
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
			}
			else if (params[0].equals("6"))
			{
				// 刀光
				if (params.length >= 3)
				{
					roles.append(String.format("\t\t<roleEffect faction=\"%s\" sectLv=\"%s\" name=\"%s\">\n", params[1], params[2], attire.name));
					for (AttireAction action : attire.actions)
					{
						if (action.anims.length > 0)
						{
							roles.append(String.format("\t\t\t<action id=\"%s\" files=\"%s\"/>\n", action.id, action_urlIDs.get(action)));
						}
					}
					roles.append(String.format("\t\t</roleEffect>\n"));
				}
				else
				{
					GamePacker.error("与刀光关联的装扮命名错误：" + attire.nativeName + "   (应该为：6_职业ID_名称)");
				}
			}
			else if (params[0].equals("7"))
			{
			}
			else if (params[0].equals("8"))
			{
				// 坐骑
				if (params.length >= 2)
				{
					horses.append(String.format("\t\t<horse horseID=\"%s\" name=\"%s\">\n", params[1], attire.name));
					for (AttireAction action : attire.actions)
					{
						if (action.anims.length > 0)
						{
							horses.append(String.format("\t\t\t<action id=\"%s\" files=\"%s\"/>\n", action.id, action_urlIDs.get(action)));
						}
					}
					horses.append(String.format("\t\t</horse>\n"));
				}
				else
				{
					GamePacker.error("与坐骑关联的装扮命名错误：" + attire.nativeName + "   (应该为：8_坐骑ID_名称)");
				}
			}
		}

		txt.append(roles);
		txt.append(equips);
		txt.append(effects);
		txt.append(labels);
		txt.append(horses);
		txt.append("\t</attires>\n");

		return txt.toString();
	}

	/**
	 * 过滤装扮
	 * 
	 * @param attire
	 * @return
	 */
	private boolean filterAttire(Attire attire)
	{
		if (attire.isAnimAttire() || attire.nativeName.startsWith("0_"))
		{
			return false;
		}

		String[] params = attire.typeParams;
		if (params.length == 0)
		{
			return false;
		}

		int type = 0;

		try
		{
			type = Integer.parseInt(params[0]);
		}
		catch (NumberFormatException exception)
		{
		}

		switch (type)
		{
			case 1:
				return params.length >= 3;// 装扮: 1_职业ID_职业等级_名称
			case 2:
				return params.length >= 4;// 装备: 2_起始ID_结束ID_职业ID_名称
			case 3:
				return true;// 效果: 3_名称
			case 4:
				return false;// 怪物: 4_名称
			case 5:
				return false;// 标签: 5_名称
			case 6:
				return params.length >= 2;// 刀光: 6_职业ID_名称
			case 7:
				return false;// ???
			case 8:
				return true;// 坐骑: 8_坐骑ID_名称
		}

		return false;
	}

	/**
	 * 获取场景配置汇总
	 * 
	 * @return
	 */
	private String getSceneSummay()
	{
		Scene[] scenes = root.worldTable.getAllScene();
		Arrays.sort(scenes, new Comparator<Scene>()
		{
			@Override
			public int compare(Scene o1, Scene o2)
			{
				return o1.sceneID - o2.sceneID;
			}
		});

		StringBuilder txt = new StringBuilder();
		txt.append("\t<scenes>\n");
		for (Scene scene : scenes)
		{
			txt.append(String.format("\t\t<scene id=\"%s\" type=\"%s\" size=\"%s\" files=\"%s\" />\n", scene.sceneID, scene.sceneType, sceneWriter.getSceneSize(scene), sceneWriter.getSceneURLs(scene)));
		}
		txt.append("\t</scenes>\n");
		return txt.toString();
	}
}
