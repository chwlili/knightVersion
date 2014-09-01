package org.game.knight.version.packer.world.output3d;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import org.chw.util.FileUtil;
import org.chw.util.MD5Util;
import org.chw.util.ZlibUtil;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.world.BaseWriter;
import org.game.knight.version.packer.world.WorldWriter;
import org.game.knight.version.packer.world.model.Attire;
import org.game.knight.version.packer.world.model.AttireAction;
import org.game.knight.version.packer.world.model.AttireAnim;
import org.game.knight.version.packer.world.model.AttireAudio;
import org.game.knight.version.packer.world.model.ImageFrame;

public class Config3dAttireWriter extends BaseWriter
{
	private Config3d config;
	private String outputURL;

	private HashMap<String, String> newTable = new HashMap<String, String>();
	private HashMap<String, String> oldTable = new HashMap<String, String>();

	/**
	 * ���캯��
	 * 
	 * @param root
	 */
	public Config3dAttireWriter(WorldWriter root, Config3d config)
	{
		super(root, "3dAttire");

		this.config = config;
	}

	/**
	 * ��ȡ���URL
	 * 
	 * @return
	 */
	public String getOutputURL()
	{
		return outputURL;
	}

	@Override
	protected void startup() throws Exception
	{
		GamePacker.progress("���3D��Ⱦװ������");
	}

	@Override
	protected void exec() throws Exception
	{
		Attire[] attires = root.getAttireTable().getAllAttire();
		Arrays.sort(attires, new Comparator<Attire>()
		{
			@Override
			public int compare(Attire o1, Attire o2)
			{
				return o1.gid.compareTo(o2.gid);
			}
		});

		// ͳ�����е���ͼ�ļ���ַ
		HashSet<String> atfURLs = new HashSet<String>();
		for (Attire attire : root.getAttireTable().getAllAttire())
		{
			for (AttireAction action : attire.actions)
			{
				for (AttireAnim anim : action.anims)
				{
					for (int i = 0; i < anim.row * anim.col; i++)
					{
						if (anim.times[i] > 0)
						{
							ImageFrame frame = root.getImageFrameTable().get(anim.img.gid, anim.row, anim.col, i);
							if (frame != null)
							{
								Atlas atlas = config.atlasWriter.findAtlasByImageFrame(frame);
								if (atlas != null)
								{
									atfURLs.add(atlas.atfURL);
								}
							}
						}
					}
				}
			}
		}

		// �����������·��
		String[] atfURLArray = atfURLs.toArray(new String[atfURLs.size()]);
		Arrays.sort(atfURLArray);

		// Ϊ�������·������ID
		HashMap<String, Integer> atfURL_refID = new HashMap<String, Integer>();
		for (int i = 0; i < atfURLArray.length; i++)
		{
			atfURL_refID.put(atfURLArray[i], i + 1);
		}

		// ���������ļ�
		StringBuilder attireText = new StringBuilder();
		attireText.append("<attires>\n");
		attireText.append("\t<textures>\n");
		for (int i = 0; i < atfURLArray.length; i++)
		{
			String atfURL = atfURLArray[i];
			int atfID = atfURL_refID.get(atfURL);
			File file = new File(root.getOutputFolder().getPath() + atfURL);

			attireText.append(String.format("\t\t<texture id=\"%s\" path=\"%s\" size=\"%s\" />\n", atfID, root.localToCdnURL(atfURL), file.length()));
		}
		attireText.append("\t</textures>\n");
		for (Attire attire : root.getAttireTable().getAllAttire())
		{
			attireText.append(String.format("\t<attire id=\"%s\" name=\"%s\" x=\"%s\" y=\"%s\" width=\"%s\" height=\"%s\">\n", attire.gid, attire.name, attire.hitRect.x, attire.hitRect.y, attire.hitRect.width, attire.hitRect.height));
			for (AttireAction action : attire.actions)
			{
				attireText.append(String.format("\t\t<action id=\"%s\" nameX=\"%s\" nameY=\"%s\">\n", action.id, action.hitRect.nameX, action.hitRect.nameY));
				for (AttireAnim anim : action.anims)
				{
					attireText.append("\t\t\t<anim x=\"" + anim.x + "\" y=\"" + anim.y + "\" scaleX=\"" + anim.scaleX + "\" scaleY=\"" + anim.scaleY + "\" flip=\"" + anim.flip + "\" actionID=\"" + action.id + "\" groupID=\"" + anim.groupID + "\" layerID=\"" + anim.layerID + "\">\n");

					int regionCount = anim.row * anim.col;
					for (int i = 0; i < regionCount; i++)
					{
						int delay = anim.times[i];
						if (delay > 0)
						{
							ImageFrame frame = root.getImageFrameTable().get(anim.img.gid, anim.row, anim.col, i);
							if (frame != null)
							{
								Atlas atlas = config.atlasWriter.findAtlasByImageFrame(frame);
								if (atlas != null)
								{
									attireText.append("\t\t\t\t<frame texture=\"" + root.localToCdnURL(atlas.atfURL) + "\" frameID=\"" + frame.file.gid + "_" + frame.row + "_" + frame.col + "_" + i + "\" frameW=\"" + frame.frameW + "\" frameH=\"" + frame.frameH + "\" delay=\"" + delay + "\"/>\n");
								}
							}
						}
					}
					attireText.append("\t\t\t</anim>\n");
				}
				for (AttireAudio audio : action.audios)
				{
					attireText.append(String.format("\t\t\t<audio path=\"%s\" loop=\"%s\" volume=\"%s\"/>\n", root.localToCdnURL(root.getMp3Writer().getMp3URL(audio.mp3)), audio.loop, audio.volume));
				}
				attireText.append("\t\t</action>\n");
			}
			attireText.append("\t</attire>\n");
		}
		attireText.append("</attires>");

		// �洢�ļ�
		byte[] bytes = null;
		try
		{
			bytes = attireText.toString().getBytes("UTF-8");
			if (root.hasZIP())
			{
				bytes = ZlibUtil.compress(bytes);
			}
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			GamePacker.error(e);
			return;
		}

		String md5 = MD5Util.md5Bytes(bytes);
		String url = oldTable.get(md5);

		if (url == null)
		{
			url = root.getGlobalOptionTable().getNextExportFile() + ".cfg";

			File outputFile = new File(root.getOutputFolder().getPath() + url);
			FileUtil.writeFile(outputFile, bytes);
			root.addFileSuffix(outputFile);
		}

		newTable.put(md5, url);
		outputURL = url;
	}

	// -------------------------------------------------------------------------------------------------------------------
	//
	// �汾��Ϣ
	//
	// -------------------------------------------------------------------------------------------------------------------

	@Override
	protected void readHistory(InputStream stream) throws Exception
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "utf8"));
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
	protected void saveHistory(OutputStream stream) throws Exception
	{
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream, "utf8"));

		// ����
		String[] keys = newTable.keySet().toArray(new String[newTable.size()]);
		Arrays.sort(keys);

		// д��
		for (String key : keys)
		{
			writer.write(key + " = " + newTable.get(key) + "\n");
		}

		// ��¼����ļ�
		root.addOutputFile(outputURL);
	}
}
