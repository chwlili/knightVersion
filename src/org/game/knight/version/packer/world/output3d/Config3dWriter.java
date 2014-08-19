package org.game.knight.version.packer.world.output3d;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import org.chw.util.FileUtil;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.world.model.Attire;
import org.game.knight.version.packer.world.model.AttireAction;
import org.game.knight.version.packer.world.model.AttireAnim;
import org.game.knight.version.packer.world.model.ImageFrame;
import org.game.knight.version.packer.world.model.Scene;
import org.game.knight.version.packer.world.task.RootTask;

public class Config3dWriter
{
	private RootTask root;
	private Attire3dWrite attireWriter;
	private Scene3dWriter sceneWriter;

	/**
	 * ���캯��
	 * 
	 * @param root
	 */
	public Config3dWriter(RootTask root)
	{
		this.root = root;
		this.attireWriter = new Attire3dWrite(root);
		this.sceneWriter = new Scene3dWriter(root);
	}

	/**
	 * ��ʼ
	 */
	public void start()
	{
		attireWriter.start();
		if (root.isCancel())
		{
			return;
		}
		attireWriter.saveVer();

		sceneWriter.start();
		if (root.isCancel())
		{
			return;
		}
		sceneWriter.saveVer();

		writeDB();
	}

	/**
	 * ���������Ϣ
	 */
	private void writeDB()
	{
		String attireURL = attireWriter.getOutputURL();
		File attireFile = new File(root.getOutputFolder().getPath() + attireURL);

		String worldURL = sceneWriter.getWorldCfgURL();
		File worldFile = new File(root.getOutputFolder().getPath() + worldURL);

		StringBuilder txt = new StringBuilder();
		txt.append("<project>\n");
		txt.append("\t<configs>\n");
		// txt.append(String.format("\t\t<config name=\"%s\" path=\"%s\" size=\"%s\"/>\n",
		// "uiAvatar", getExportedFileUrl(avatarFileKey),
		// getExportedFileSize(avatarFileKey)));
		txt.append(String.format("\t\t<config name=\"%s\" path=\"%s\" size=\"%s\"/>\n", "attire", attireURL, attireFile.length()));
		txt.append(String.format("\t\t<config name=\"%s\" path=\"%s\" size=\"%s\"/>\n", "world", worldURL, worldFile.length()));
		txt.append("\t</configs>\n");
		txt.append(getAttireSummay());
		txt.append(getSceneSummay());
		txt.append("</project>");

		try
		{
			FileUtil.writeFile(new File(root.getOutputFolder().getPath() + "/db.xml"), txt.toString().getBytes("UTF-8"));
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * ��ȡ�������û���
	 * 
	 * @return
	 */
	private String getSceneSummay()
	{
		Scene[] scenes = root.getWorldTable().getAllScene();
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

	/**
	 * ����װ��
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
				return params.length >= 3;// װ��: 1_ְҵID_ְҵ�ȼ�_����
			case 2:
				return params.length >= 4;// װ��: 2_��ʼID_����ID_ְҵID_����
			case 3:
				return true;// Ч��: 3_����
			case 4:
				return false;// ����: 4_����
			case 5:
				return false;// ��ǩ: 5_����
			case 6:
				return params.length >= 2;// ����: 6_ְҵID_����
			case 7:
				return false;// ???
			case 8:
				return true;// ����: 8_����ID_����
		}

		return false;
	}

	/**
	 * ��ȡװ�����û���
	 * 
	 * @param txt
	 */
	private String getAttireSummay()
	{
		StringBuilder txt = new StringBuilder();
		txt.append("\t<attires>\n");

		HashMap<String, Integer> url_size = new HashMap<String, Integer>();
		HashMap<String, Integer> url_id = new HashMap<String, Integer>();
		HashMap<AttireAction, HashSet<String>> action_urls = new HashMap<AttireAction, HashSet<String>>();
		HashMap<AttireAction, String> action_urlIDs = new HashMap<AttireAction, String>();

		for (Attire attire : root.getAttireTable().getAllAttire())
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
							ImageFrame frame = root.getImageFrameTable().get(anim.img.gid, anim.row, anim.col, i);
							if (frame != null)
							{
								Atlas atlas = root.getAtlasTable().findAtlasByImageFrame(frame);
								if (atlas != null)
								{
									if (!action_urls.containsKey(action))
									{
										action_urls.put(action, new HashSet<String>());
									}

									String url = atlas.atfURL;
									File file = new File(root.getOutputFolder().getPath() + url);

									url_size.put(url, (int) file.length());
									action_urls.get(action).add(url);
								}
							}
						}
					}
				}
			}
		}

		// ����URL
		String[] keys = url_size.keySet().toArray(new String[url_size.size()]);
		Arrays.sort(keys);

		// ȷ��URL���
		for (int i = 0; i < keys.length; i++)
		{
			url_id.put(keys[i], i + 1);
		}

		// ���URL�б�
		txt.append("\t\t<files>\n");
		for (String key : keys)
		{
			txt.append(String.format("\t\t\t<file id=\"%s\" url=\"%s\" size=\"%s\" />\n", url_id.get(key), key, url_size.get(key)));
		}
		txt.append("\t\t</files>\n");

		// ��������URL ID�б�
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

		for (Attire attire : root.getAttireTable().getAllAttire())
		{
			if (!filterAttire(attire))
			{
				continue;
			}

			String[] params = attire.typeParams;
			if (params[0].equals("1"))
			{
				// װ��
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
					GamePacker.error("ְҵ������װ����������" + attire.nativeName + "   (Ӧ��Ϊ��1_ְҵID_ְҵ�ȼ�_����)");
				}
			}
			else if (params[0].equals("2"))
			{
				// װ��
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
					GamePacker.error("��װ��������װ����������" + attire.nativeName + "   (Ӧ��Ϊ��2_��ʼID_����ID_ְҵID_����)");
				}
			}
			else if (params[0].equals("3"))
			{
				// Ч��
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
				// ����
			}
			else if (params[0].equals("5"))
			{
				// ��ǩ
			}
			else if (params[0].equals("6"))
			{
				// ����
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
					GamePacker.error("�뵶�������װ����������" + attire.nativeName + "   (Ӧ��Ϊ��6_ְҵID_����)");
				}
			}
			else if (params[0].equals("7"))
			{
			}
			else if (params[0].equals("8"))
			{
				// ����
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
					GamePacker.error("�����������װ����������" + attire.nativeName + "   (Ӧ��Ϊ��8_����ID_����)");
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
}
