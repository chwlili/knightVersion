package org.game.knight.version.packer.world.output2d;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;

import org.chw.util.FileUtil;
import org.chw.util.MD5Util;
import org.chw.util.ZlibUtil;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.world.WorldWriter;
import org.game.knight.version.packer.world.model.Attire;
import org.game.knight.version.packer.world.model.AttireAction;
import org.game.knight.version.packer.world.model.AttireAnim;
import org.game.knight.version.packer.world.model.AttireAudio;
import org.game.knight.version.packer.world.model.ImageFrame;

public class Config2dAttireWriter
{
	private WorldWriter root;
	private AttireSwfWriter attireSWFWriter;
	private String outputURL;

	private HashMap<String, String> newTable = new HashMap<String, String>();
	private HashMap<String, String> oldTable = new HashMap<String, String>();

	/**
	 * 构造函数
	 * 
	 * @param root
	 */
	public Config2dAttireWriter(WorldWriter root, AttireSwfWriter attireSWFWriter)
	{
		this.root = root;
		this.attireSWFWriter = attireSWFWriter;
	}

	/**
	 * 开始
	 */
	public void start()
	{
		GamePacker.progress("输出装扮配置");

		openVer();

		writeAttireConfig();
		
		saveVer();
	}

	/**
	 * 获取输出URL
	 * 
	 * @return
	 */
	public String getOutputURL()
	{
		return outputURL;
	}

	/**
	 * 输出装扮配置
	 */
	private void writeAttireConfig()
	{
		GamePacker.progress("输出装扮配置");
		StringBuilder attireText = new StringBuilder();
		attireText.append("<attires>\n");
		for (Attire attire : root.getAttireTable().getAllAttire())
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
							ImageFrame frame = root.getImageFrameTable().get(anim.img.gid, anim.row, anim.col, i);

							int offSetX = frame.clipX + frame.clipW / 2 - frame.frameW / 2;
							int offsetY = frame.clipY + frame.clipH - frame.frameH;

							// 帧信息
							String frameFileName = root.localToCdnURL(attireSWFWriter.getFrameFileURL(frame));
							String frameTypeName = attireSWFWriter.getFrameClassID(frame);

							attireText.append(String.format("\t\t\t\t<frame fileName=\"%s\" classID=\"%s\" x=\"%s\" y=\"%s\" delay=\"%s\"/>\n", frameFileName, frameTypeName, offSetX, offsetY, delay));
						}
					}

					attireText.append(String.format("\t\t\t</anim>\n"));
				}
				for (AttireAudio audio : action.audios)
				{
					String audioURL = root.getMp3Writer().getMp3URL(audio.mp3);
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

		// 存储文件
		byte[] bytes = null;
		try
		{
			bytes = attireText.toString().getBytes("UTF-8");
			if(root.hasZIP())
			{
				bytes=ZlibUtil.compress(bytes);
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
		return new File(root.getOutputFolder().getPath() + File.separatorChar + ".ver" + File.separatorChar + "2dAttire");
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
			GamePacker.error(e);
			return;
		}

		// 记录输出文件
		root.addOutputFile(outputURL);
	}
}
