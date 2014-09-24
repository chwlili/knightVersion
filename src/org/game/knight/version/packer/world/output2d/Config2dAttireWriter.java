package org.game.knight.version.packer.world.output2d;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

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

public class Config2dAttireWriter extends BaseWriter
{
	private AttireSwfWriter attireSWFWriter;
	private String outputKey;
	private String outputURL;

	private HashMap<String, String> newTable = new HashMap<String, String>();
	private HashMap<String, String> oldTable = new HashMap<String, String>();

	/**
	 * ���캯��
	 * 
	 * @param root
	 */
	public Config2dAttireWriter(WorldWriter root, AttireSwfWriter attireSWFWriter)
	{
		super(root, "2dAttire");
		this.attireSWFWriter = attireSWFWriter;
	}

	/**
	 * ��ȡ�����
	 * 
	 * @return
	 */
	public String getOutputKey()
	{
		return outputKey;
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
		GamePacker.log("��ʼ���װ������(2D)");
	}

	@Override
	protected void exec() throws Exception
	{
		StringBuilder attireText = new StringBuilder();
		attireText.append("<attires>\n");
		for (Attire attire : root.attireTable.getAllAttire())
		{
			attireText.append(String.format("\t<attire id=\"%s\" name=\"%s\" x=\"%s\" y=\"%s\" width=\"%s\" height=\"%s\">\n", attire.gid, attire.name, attire.hitRect.x, attire.hitRect.y, attire.hitRect.width, attire.hitRect.height));

			for (AttireAction action : attire.actions)
			{
				attireText.append(String.format("\t\t<action id=\"%s\" nameX=\"%s\" nameY=\"%s\" >\n", action.id, action.hitRect.nameX, action.hitRect.nameY));

				for (AttireAnim anim : action.anims)
				{
					attireText.append(String.format("\t\t\t<anim x=\"%s\" y=\"%s\" scaleX=\"%s\" scaleY=\"%s\" flip=\"%s\" groupID=\"%s\" layerID=\"%s\">\n", anim.x, anim.y, anim.scaleX, anim.scaleY, anim.flip, anim.groupID, anim.layerID));

					int rowCount = anim.row;
					int colCount = anim.col;
					int regionCount = rowCount * colCount;
					for (int i = 0; i < regionCount; i++)
					{
						int delay = anim.times[i];
						if (delay > 0)
						{
							ImageFrame frame = root.frameTable.get(anim.img.gid, anim.row, anim.col, i);

							int offSetX = frame.clipX + frame.clipW / 2 - frame.frameW / 2;
							int offsetY = frame.clipY + frame.clipH - frame.frameH;

							// ֡��Ϣ
							String frameFileName = root.localToCdnURL(attireSWFWriter.getFrameFileURL(frame));
							String frameTypeName = attireSWFWriter.getFrameClassID(frame);

							attireText.append(String.format("\t\t\t\t<frame fileName=\"%s\" classID=\"%s\" x=\"%s\" y=\"%s\" delay=\"%s\"/>\n", frameFileName, frameTypeName, offSetX, offsetY, delay));
						}
					}

					attireText.append(String.format("\t\t\t</anim>\n"));
				}
				for (AttireAudio audio : action.audios)
				{
					String audioURL = root.mp3Writer.getMp3URL(audio.mp3);
					if (audioURL != null)
					{
						attireText.append(String.format("\t\t\t<audio path=\"%s\" loop=\"%s\" volume=\"%s\"/>\n", audioURL, audio.loop, audio.volume));
					}
				}

				attireText.append(String.format("\t\t</action>\n"));
			}

			attireText.append(String.format("\t</attire>\n"));
		}
		attireText.append("</attires>");

		// �洢�ļ�
		byte[] bytes = attireText.toString().getBytes("UTF-8");

		byte[] cfgBytes = root.convertXmlToAs(new ByteArrayInputStream(bytes), "attire.xml2");
		if (cfgBytes != null)
		{
			bytes = cfgBytes;
		}
		else if (root.hasZIP())
		{
			bytes = ZlibUtil.compress(bytes);
		}

		String key = cfgBytes != null ? "attire.xml" : "attire";
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
		outputKey = key;
		outputURL = url;
	}

	// -------------------------------------------------------------------------------------------------------------------
	//
	// �汾��Ϣ
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
