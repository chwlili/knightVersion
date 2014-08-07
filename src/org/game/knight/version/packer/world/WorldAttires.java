package org.game.knight.version.packer.world;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.chw.util.FileUtil;
import org.chw.util.MD5Util;
import org.chw.util.XmlUtil;
import org.chw.util.ZlibUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.GamePackerConst;
import org.game.knight.version.packer.base.ChecksumTable;
import org.game.knight.version.packer.world.attire.Attire;
import org.game.knight.version.packer.world.attire.AttireAction;
import org.game.knight.version.packer.world.attire.AttireAnim;
import org.game.knight.version.packer.world.attire.AttireFile;
import org.game.knight.version.packer.world.scene.Scene;

public class WorldAttires
{
	private static String AttireDefParam = "-n 0:0 -r -q 30 -s";
	private static String AttireDefParamID = "default";

	private String outputPath;
	private ChecksumTable shaTable;
	private GridImgTable clipTable;
	private TextureSetTable textureSetTable;

	private boolean keepAtfPng = false;
	private boolean writeRegionImg = false;

	private boolean zip = false;

	private Hashtable<String, ExportParam> paramTable = new Hashtable<String, ExportParam>();

	/**
	 * ���캯��
	 * 
	 * @param shaTable
	 * @param clipTable
	 * @param textureSetTable
	 */
	public WorldAttires(String outputPath, ChecksumTable shaTable, GridImgTable clipTable, TextureSetTable textureSetTable, boolean keepAtfPng, boolean writeRegionImg, boolean zip)
	{
		this.outputPath = outputPath;
		this.shaTable = shaTable;
		this.clipTable = clipTable;
		this.textureSetTable = textureSetTable;
		this.keepAtfPng = keepAtfPng;
		this.writeRegionImg = writeRegionImg;
		this.zip = zip;
	}

	/**
	 * ����
	 * 
	 * @param attires
	 */
	public void build(WorldExporter exporter, Hashtable<String, File> params, Hashtable<String, AttireFile> attires, Hashtable<String, Scene> scenes)
	{
		readParams(params);
		readAttires(exporter, attires);
		readScenes(exporter, scenes);
		classifyAttireResource(attires);
		classifySceneResource(attires);

		clipAlphaArea();
		writeTextures(exporter);

		initAllTextureData(exporter);
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------
	//
	// ������ȡ
	//
	// ----------------------------------------------------------------------------------------------------------------------------------------

	/**
	 * ���������ļ�
	 * 
	 * @param paramTable
	 */
	private void readParams(Hashtable<String, File> paramFiles)
	{
		int index = 0;
		for (File file : paramFiles.values())
		{
			index++;

			GamePacker.progress(String.format("�����������(%s/%s) : %s", index, paramFiles.size(), file.getPath()));

			Document document = null;

			try
			{
				SAXReader reader = new SAXReader();
				document = reader.read(file);
			}
			catch (DocumentException e)
			{
				GamePacker.error("��������ļ�����ʧ�ܣ�(" + file.getPath() + ")   " + e);
			}

			if (document != null)
			{
				String text = document.getRootElement().getText();

				String[] lines = text.split("\n");
				for (String line : lines)
				{
					line = line.trim();

					if (line.isEmpty())
					{
						continue;
					}

					String[] fields = line.split("=");
					if (fields.length == 2)
					{
						String id = fields[0].trim();

						String[] params = fields[1].trim().split(",", 3);
						if (params.length == 3)
						{
							int w = XmlUtil.parseInt(params[0].trim(), 0);
							int h = XmlUtil.parseInt(params[1].trim(), 0);
							String param = params[2].trim();

							if (this.paramTable.containsKey(id))
							{
								ExportParam old = this.paramTable.get(id);

								GamePacker.error("�������ID��ͻ��(" + file.getPath() + " : " + id + ") -> ( " + old.getFile().getPath() + " : " + id + ")");
							}
							else
							{
								this.paramTable.put(id, new ExportParam(file, id, w, h, param));
							}

							continue;
						}
					}
					GamePacker.error("��Ч�����������(" + file.getPath() + ")  " + line);
				}
			}
		}

		if (!this.paramTable.containsKey(AttireDefParamID))
		{
			this.paramTable.put(AttireDefParamID, new ExportParam(null, AttireDefParamID, 2048, 2048, AttireDefParam));
		}
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------
	//
	// ��Դ��ȡ
	//
	// ----------------------------------------------------------------------------------------------------------------------------------------

	/**
	 * ����װ��
	 * 
	 * @param exporter
	 * @param attires
	 */
	private void readAttires(WorldExporter exporter, Hashtable<String, AttireFile> attires)
	{
		int index = 0;
		for (AttireFile attireFile : attires.values())
		{
			index++;
			GamePacker.progress(String.format("����װ���ļ�(%s/%s) : %s", index, attires.size(), attireFile.getInnerPath()));
			attireFile.open(exporter);

			if (GamePacker.isCancel())
			{
				return;
			}
		}
	}

	/**
	 * ��������
	 * 
	 * @param exporter
	 * @param scenes
	 */
	private void readScenes(WorldExporter exporter, Hashtable<String, Scene> scenes)
	{
		int index = 0;
		for (Scene scene : scenes.values())
		{
			index++;
			GamePacker.progress(String.format("���������ļ�(%s/%s) : %s", index, scenes.size(), scene.getInnerPath()));
			scene.open(exporter);

			if (GamePacker.isCancel())
			{
				return;
			}
		}
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------
	//
	// ��Դ����
	//
	// ----------------------------------------------------------------------------------------------------------------------------------------

	// ���ж�������
	private HashSet<GridImgKey> gifKeys = new HashSet<GridImgKey>();

	// ��ID_��������
	private Hashtable<String, Hashtable<GridImgKey, GridImgKey>> group_gifKeys = new Hashtable<String, Hashtable<GridImgKey, GridImgKey>>();

	// ����_��ID�б�
	private Hashtable<AttireAction, HashSet<String>> action_groupList = new Hashtable<AttireAction, HashSet<String>>();

	/**
	 * ����װ����Դ
	 * 
	 * @param attires
	 */
	private void classifyAttireResource(Hashtable<String, AttireFile> attires)
	{
		Hashtable<String, AttireFile> checkTable = new Hashtable<String, AttireFile>();
		Hashtable<String, AttireFile> checkTable2 = new Hashtable<String, AttireFile>();

		// ��������װ��
		for (AttireFile attireFile : attires.values())
		{
			for (Attire attire : attireFile.getAllAttires())
			{
				if (attire.isAnimAttire() || attire.getKey().startsWith("0_"))
				{
					continue;
				}

				if (attire.getKey() != null && checkTable.containsKey(attire.getKey()))
				{
					GamePacker.error("װ�綨���ͻ:" + attire.getKey() + ", fileA=" + (checkTable.get(attire.getKey()).getInnerPath()) + " ,fileB=" + attireFile.getInnerPath());
				}
				checkTable.put(attire.getKey(), attireFile);

				if (attire.getRefKey() != null && checkTable2.containsKey(attire.getRefKey()))
				{
					GamePacker.error("װ�綨���ͻ:" + attire.getRefKey() + ", fileA=" + (checkTable2.get(attire.getRefKey()).getInnerPath()) + " ,fileB=" + attireFile.getInnerPath());
				}
				checkTable2.put(attire.getRefKey(), attireFile);

				for (AttireAction action : attire.getActions())
				{
					for (AttireAnim anim : action.getAnims())
					{
						ImgFile img = anim.getImg();
						String imgPath = anim.getImg().getInnerpath();
						String imgSHA1 = shaTable.getChecksumID(imgPath);
						int imgRowCount = anim.getRow();
						int imgColCount = anim.getCol();
						int[] imgTimes = normalTimeArray(anim.getTimes());

						String atfGroup = anim.getBagID();
						if (!paramTable.containsKey(atfGroup))
						{
							atfGroup = AttireDefParamID;
						}

						GridImgKey gifKey = new GridImgKey(imgSHA1, imgRowCount, imgColCount, img, imgTimes);

						// ������������ID�б�
						if (!action_groupList.containsKey(action))
						{
							action_groupList.put(action, new HashSet<String>());
						}
						action_groupList.get(action).add(atfGroup);

						// ������ID���������У����ϲ��������е�ʱ���б�
						if (!group_gifKeys.containsKey(atfGroup))
						{
							group_gifKeys.put(atfGroup, new Hashtable<GridImgKey, GridImgKey>());
						}
						Hashtable<GridImgKey, GridImgKey> table = group_gifKeys.get(atfGroup);
						if (table.containsKey(gifKey))
						{
							GridImgKey savedGifKey = table.get(gifKey);
							savedGifKey.setTime(mergerTimeArray(imgTimes, savedGifKey.getTimes()));
							gifKey = savedGifKey;
						}
						table.put(gifKey, gifKey);

						// ���涯����������
						gifKeys.add(gifKey);

						// ���ȡ��
						if (GamePacker.isCancel())
						{
							return;
						}
					}
				}
			}
		}
	}

	/**
	 * ���ೡ����Դ
	 * 
	 * @param attires
	 */
	private void classifySceneResource(Hashtable<String, AttireFile> attires)
	{
		for (AttireFile attireFile : attires.values())
		{
			// ����ͼ���б�
			for (ImgFile img : attireFile.getAllImgs())
			{
				String imgPath = img.getInnerpath();
				String imgSHA1 = shaTable.getChecksumID(imgPath);
				int imgRowCount = 1;
				int imgColCount = 1;
				int[] imgTimes = new int[] { 1 };

				String atfGroup = attireFile.getImgGroupID(img);
				if (!paramTable.containsKey(atfGroup))
				{
					atfGroup = AttireDefParamID;
				}

				GridImgKey gifKey = new GridImgKey(imgSHA1, imgRowCount, imgColCount, img, imgTimes);

				// ������ID���������У����ϲ��������е�ʱ���б�
				if (!group_gifKeys.containsKey(atfGroup))
				{
					group_gifKeys.put(atfGroup, new Hashtable<GridImgKey, GridImgKey>());
				}
				Hashtable<GridImgKey, GridImgKey> table = group_gifKeys.get(atfGroup);
				if (table.containsKey(gifKey))
				{
					GridImgKey savedGifKey = table.get(gifKey);
					savedGifKey.setTime(mergerTimeArray(imgTimes, savedGifKey.getTimes()));
					gifKey = savedGifKey;
				}
				table.put(gifKey, gifKey);

				// ���涯����������
				gifKeys.add(gifKey);

				// ���ȡ��
				if (GamePacker.isCancel())
				{
					return;
				}
			}

			// ���������б�
			for (Attire attire : attireFile.getAllAttires())
			{
				if (!attire.isAnimAttire())
				{
					continue;
				}

				for (AttireAction action : attire.getActions())
				{
					for (AttireAnim anim : action.getAnims())
					{
						ImgFile img = anim.getImg();
						String imgPath = anim.getImg().getInnerpath();
						String imgSHA1 = shaTable.getChecksumID(imgPath);
						int imgRowCount = anim.getRow();
						int imgColCount = anim.getCol();
						int[] imgTimes = normalTimeArray(anim.getTimes());

						String atfGroup = anim.getBagID();
						if (!paramTable.containsKey(atfGroup))
						{
							atfGroup = AttireDefParamID;
						}

						GridImgKey gifKey = new GridImgKey(imgSHA1, imgRowCount, imgColCount, img, imgTimes);

						// ������������ID�б�
						if (!action_groupList.containsKey(action))
						{
							action_groupList.put(action, new HashSet<String>());
						}
						action_groupList.get(action).add(atfGroup);

						// ������ID���������У����ϲ��������е�ʱ���б�
						if (!group_gifKeys.containsKey(atfGroup))
						{
							group_gifKeys.put(atfGroup, new Hashtable<GridImgKey, GridImgKey>());
						}
						Hashtable<GridImgKey, GridImgKey> table = group_gifKeys.get(atfGroup);
						if (table.containsKey(gifKey))
						{
							GridImgKey savedGifKey = table.get(gifKey);
							savedGifKey.setTime(mergerTimeArray(imgTimes, savedGifKey.getTimes()));
							gifKey = savedGifKey;
						}
						table.put(gifKey, gifKey);

						// ���涯����������
						gifKeys.add(gifKey);

						// ���ȡ��
						if (GamePacker.isCancel())
						{
							return;
						}
					}
				}
			}
		}
	}

	/**
	 * ���ʱ������
	 * 
	 * @param a
	 * @return
	 */
	private int[] normalTimeArray(int[] a)
	{
		int[] list = new int[a.length];
		for (int i = 0; i < a.length; i++)
		{
			list[i] = a[i] > 0 ? 1 : 0;
		}
		return list;
	}

	/**
	 * �ϲ�ʱ������
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private int[] mergerTimeArray(int[] a, int[] b)
	{
		int[] list = new int[Math.max(a.length, b.length)];
		for (int i = 0; i < list.length; i++)
		{
			int aVal = 0;
			int bVal = 0;
			if (i < a.length)
			{
				aVal = a[i];
			}
			if (i < b.length)
			{
				bVal = b[i];
			}
			list[i] = aVal <= 0 && bVal <= 0 ? 0 : 1;
		}
		return list;
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------
	//
	// ����͸������
	//
	// ----------------------------------------------------------------------------------------------------------------------------------------

	/**
	 * �г�͸������
	 */
	private void clipAlphaArea()
	{
		ArrayList<GridImgKey> addedGifs = new ArrayList<GridImgKey>();

		GridImgKey[] gifs = gifKeys.toArray(new GridImgKey[gifKeys.size()]);
		for (GridImgKey gif : gifs)
		{
			if (clipTable.has(gif))
			{
				clipTable.add(gif);
			}
			else
			{
				addedGifs.add(gif);
			}
		}

		int index = 0;
		for (GridImgKey gif : addedGifs)
		{
			index++;
			GamePacker.progress(String.format("����ͼ�����(%s/%s) : %s", index, addedGifs.size(), gif.getFileInnerPath()));
			clipTable.add(gif);
		}
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------
	//
	// �����ͼ����
	//
	// ----------------------------------------------------------------------------------------------------------------------------------------

	// ��ID_��ͼ��ID
	private Hashtable<String, TextureSetKey> group_textureSetKey = new Hashtable<String, TextureSetKey>();

	/**
	 * �������
	 */
	private void writeTextures(WorldExporter world)
	{
		ArrayList<TextureSetKey> addedTextureSetKeys = new ArrayList<TextureSetKey>();
		ArrayList<Texture> addedTextures = new ArrayList<Texture>();

		Hashtable<TextureSetKey, String> textureSetKey_GroupID = new Hashtable<TextureSetKey, String>();
		Hashtable<Texture, String> texture_GroupID = new Hashtable<Texture, String>();

		for (String groupID : group_gifKeys.keySet())
		{
			ExportParam param = paramTable.get(groupID);

			Hashtable<GridImgKey, GridImgKey> gifs = group_gifKeys.get(groupID);

			GridImgKey[] gifArray = gifs.values().toArray(new GridImgKey[gifs.size()]);

			TextureSetKey textureSetKey = new TextureSetKey(param.getWidth(), param.getHeight(), param.getParam(), gifArray);
			if (textureSetTable.contains(textureSetKey))
			{
				textureSetTable.add(textureSetKey);
			}
			else
			{
				addedTextureSetKeys.add(textureSetKey);
			}

			group_textureSetKey.put(groupID, textureSetKey);
			textureSetKey_GroupID.put(textureSetKey, groupID);

			// ���ȡ��
			if (GamePacker.isCancel())
			{
				return;
			}
		}

		// ���ȡ��
		if (GamePacker.isCancel())
		{
			return;
		}

		int index = 0;
		for (TextureSetKey texturesetKey : addedTextureSetKeys)
		{
			index++;

			GamePacker.progress(String.format("������ͼ��(%s/%s) : %s (����ͼ������%s��Сͼ)", index, addedTextureSetKeys.size(), textureSetKey_GroupID.get(texturesetKey), texturesetKey.getClips().length));
			textureSetTable.add(texturesetKey);

			TextureSet textureSet = textureSetTable.getTextureSet(texturesetKey);

			for (Texture texture : textureSet.getTextures())
			{
				if (texture_GroupID.containsKey(texture))
				{
					GamePacker.error("??? conflict!");
				}
				addedTextures.add(texture);
				texture_GroupID.put(texture, textureSetKey_GroupID.get(texturesetKey));
			}

			// ���ȡ��
			if (GamePacker.isCancel())
			{
				return;
			}
		}

		// ���ȡ��
		if (GamePacker.isCancel())
		{
			return;
		}

		index = 0;
		for (Texture texture : addedTextures)
		{
			String groupID = texture_GroupID.get(texture);
			ExportParam param = paramTable.get(groupID);

			index++;
			try
			{
				writeTexture(world, texture, param.getParam(), groupID, index, addedTextures.size());
			}
			catch (IOException e)
			{
				GamePacker.error(e);
			}

			// ���ȡ��
			if (GamePacker.isCancel())
			{
				return;
			}
		}
	}

	/**
	 * �����ͼ
	 * 
	 * @param world
	 * @param textureData
	 * @param type
	 * @param index
	 * @param count
	 * @throws IOException
	 */
	private void writeTexture(WorldExporter world, Texture textureData, String type, String groupName, int index, int count) throws IOException
	{
		long fileID = world.getOptionTable().getNextFileID();
		long folderID = (fileID - 1) / GamePackerConst.FILE_COUNT_EACH_DIR + 1;

		// ��������ID
		// world.getOptionTable().getNextFileID();
		// world.getOptionTable().getNextFileID();

		// ȷ��png·��
		String pngFilePath = "/" + folderID + "/" + fileID + ".png";
		textureData.setPngFilePath("/" + world.getDestDir().getName() + pngFilePath);

		// ȷ��xml·��
		String atfFilePath = "/" + folderID + "/" + fileID + ".atf";
		textureData.setAtfFilePath("/" + world.getDestDir().getName() + atfFilePath);

		// ȷ��xml·��
		String xmlFilePath = "/" + folderID + "/" + fileID + ".xml";
		textureData.setXmlFilePath("/" + world.getDestDir().getName() + xmlFilePath);

		// ����Ŀ¼
		File testFile = new File(world.getDestDir().getPath() + "/" + folderID);
		if (!testFile.exists())
		{
			testFile.mkdirs();
		}

		// �ϲ���ͼpng�ļ���xml�ļ�
		GamePacker.progress(String.format("�����ͼ(%s/%s) : [%s] �ϲ���...", index, count, groupName));
		int drawX = 0;
		int drawY = 0;
		StringBuilder atlas = new StringBuilder();
		BufferedImage texture = new BufferedImage(textureData.getWidth(), textureData.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = (Graphics2D) texture.getGraphics();
		for (Region region : textureData.getRegions())
		{
			drawX = region.getTextureX();
			drawY = region.getTextureY();

			atlas.append("<SubTexture name=\"" + region.getOwnerChecksum() + "_" + region.getIndex() + "\" x=\"" + drawX + "\" y=\"" + drawY + "\" width=\"" + region.getClipW() + "\" height=\"" + region.getClipH() + "\" frameX=\"" + (region.getClipX() > 0 ? -region.getClipX() : 0) + "\" frameY=\"" + (region.getClipY() > 0 ? -region.getClipY() : 0) + "\" frameWidth=\"" + region.getW() + "\" frameHeight=\"" + region.getH() + "\"/>");

			BufferedImage img = ImageIO.read(region.getFile());
			graphics.drawImage(img, drawX, drawY, drawX + region.getClipW(), drawY + region.getClipH(), region.getX() + region.getClipX(), region.getY() + region.getClipY(), region.getX() + region.getClipX() + region.getClipW(), region.getY() + region.getClipY() + region.getClipH(), null);

			if (GamePacker.isCancel())
			{
				return;
			}
		}
		graphics.dispose();

		// ���ȡ��
		if (GamePacker.isCancel())
		{
			return;
		}

		// ����png�ļ�
		GamePacker.progress(String.format("�����ͼ(%s/%s) : [%s] ���PNG( %s )", index, count, groupName, pngFilePath));
		File savePng = new File(world.getDestDir().getPath() + pngFilePath);
		ImageIO.write(texture, "png", savePng);

		// ���ȡ��
		if (GamePacker.isCancel())
		{
			return;
		}

		// ����xml�ļ�
		GamePacker.progress(String.format("�����ͼ(%s/%s) : [%s] ���XML( %s )", index, count, groupName, xmlFilePath));
		StringBuilder xmlContent = new StringBuilder();
		xmlContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xmlContent.append("<TextureAtlas imagePath=\"" + ("/" + world.getDestDir().getName() + pngFilePath) + "\">");
		xmlContent.append(atlas.toString());
		xmlContent.append("</TextureAtlas>");

		// ���ȡ��
		if (GamePacker.isCancel())
		{
			return;
		}

		// ����ATF�ļ�
		GamePacker.progress(String.format("�����ͼ(%s/%s) : [%s] ���ATF( %s )", index, count, groupName, atfFilePath));
		File atfInput = new File(world.getDestDir().getPath() + pngFilePath);
		File atfOutput = new File(world.getDestDir().getPath() + atfFilePath);
		if (atfInput.exists() && atfInput.isFile())
		{
			writeATF(atfInput, atfOutput, type);

			if (atfOutput.exists())
			{
				byte[] atfBytes = FileUtil.getFileBytes(atfOutput);
				byte[] xmlBytes = ZlibUtil.compress(xmlContent.toString().getBytes("utf8"));

				ByteArrayOutputStream temp = new ByteArrayOutputStream();
				temp.write(atfBytes);
				temp.write(xmlBytes);
				temp.write((xmlBytes.length >>> 24) & 0xFF);
				temp.write((xmlBytes.length >>> 16) & 0xFF);
				temp.write((xmlBytes.length >>> 8) & 0xFF);
				temp.write(xmlBytes.length & 0xFF);

				FileUtil.writeFile(atfOutput, MD5Util.addSuffix(temp.toByteArray()));
			}
		}
		if (!atfOutput.exists() || !atfOutput.isFile())
		{
			GamePacker.error("���ATFʧ�ܣ�", pngFilePath + " .xml .atf");
		}

		// ���debug regions
		if (writeRegionImg)
		{
			int regionIndex = 0;
			for (Region region : textureData.getRegions())
			{
				if (region.getClipW() > 0 && region.getClipH() > 0 && region.getTime() > 0)
				{
					BufferedImage inputIMG = ImageIO.read(region.getFile());

					BufferedImage outputIMG = new BufferedImage(region.getClipW(), region.getClipH(), BufferedImage.TYPE_INT_ARGB);
					Graphics2D outputGraphics = (Graphics2D) outputIMG.getGraphics();
					outputGraphics.drawImage(inputIMG, 0, 0, region.getClipW(), region.getClipH(), region.getX() + region.getClipX(), region.getY() + region.getClipY(), region.getX() + region.getClipX() + region.getClipW(), region.getY() + region.getClipY() + region.getClipH(), null);
					outputGraphics.dispose();

					File outputFile = new File(world.getDestDir().getPath() + "/" + folderID + "/" + fileID + "/" + regionIndex + ".png");
					if (outputFile.getParentFile().exists() == false)
					{
						outputFile.mkdirs();
					}
					ImageIO.write(outputIMG, "png", outputFile);

					regionIndex++;
				}
			}
		}

		// ɾ����ʱpng
		if (savePng.exists() && keepAtfPng==false)
		{
			savePng.delete();
		}
	}

	/**
	 * ���ATF
	 * 
	 * @param input
	 * @param output
	 * @param type
	 */
	private void writeATF(File input, File output, String type)
	{
		// ����-z����
		boolean hasZ = false;

		if (type.indexOf(" -z") != -1)
		{
			int rIndex = type.indexOf(" -r");
			if (rIndex == 0)
			{
				type = type.substring(rIndex + 3);
			}
			else if (rIndex > 0)
			{
				type = type.substring(0, rIndex) + type.substring(rIndex + 3);
			}

			hasZ = true;
		}

		File atfExe = new File(GamePackerConst.getJarDir().getPath() + File.separatorChar + "png2atf.exe");
		if (atfExe.exists())
		{
			callCMD(atfExe.getPath() + " -i " + input.getPath() + " -o " + output.getPath() + " " + type);
		}
		else
		{
			callCMD("png2atf -i " + input.getPath() + " -o " + output.getPath() + " " + type);
		}

		if (hasZ && output.exists() && output.isFile())
		{
			byte[] atfBytes = FileUtil.getFileBytes(output);
			atfBytes = ZlibUtil.compress(atfBytes);
			FileUtil.writeFile(output, atfBytes);
		}
	}

	/**
	 * ����CMD
	 * 
	 * @param cmd
	 */
	private void callCMD(String cmd)
	{
		try
		{
			Process p = Runtime.getRuntime().exec(cmd);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String msg = null;
			while ((msg = br.readLine()) != null)
			{
				if (msg.isEmpty())
				{
					continue;
				}
				System.out.println(msg);
			}

			BufferedReader errorOutput = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String error = null;
			while ((error = errorOutput.readLine()) != null)
			{
				System.out.println(error);
			}

			br.close();
			p.destroy();
		}
		catch (Exception e)
		{
			GamePacker.error(e);
		}
	}

	// ---------------------------------------------------------------------------------------------------------------------------------------
	//
	// ��ʼ��������ͼ��Ϣ
	//
	// ---------------------------------------------------------------------------------------------------------------------------------------

	private Texture[] textures = null;
	private Hashtable<Texture, Integer> texture_id = new Hashtable<Texture, Integer>();
	private Hashtable<Texture, String> texture_path = new Hashtable<Texture, String>();
	private Hashtable<Texture, Integer> texture_size = new Hashtable<Texture, Integer>();

	private Hashtable<String, Region> regionID_region = new Hashtable<String, Region>();
	private Hashtable<Region, Texture> region_texture = new Hashtable<Region, Texture>();

	private Hashtable<AttireAction, Texture[]> action_textures = new Hashtable<AttireAction, Texture[]>();
	private Hashtable<AttireAction, String> action_textureIDs = new Hashtable<AttireAction, String>();
	private Hashtable<AttireAction, Integer> action_size = new Hashtable<AttireAction, Integer>();
	private Hashtable<AttireAction, String> action_urls = new Hashtable<AttireAction, String>();

	/**
	 * ��ʼ��������ͼ����
	 */
	private void initAllTextureData(WorldExporter exporter)
	{
		textures = textureSetTable.getAllTextures();
		Arrays.sort(textures, new Comparator<Texture>()
		{
			@Override
			public int compare(Texture arg0, Texture arg1)
			{
				return arg0.getAtfFilePath().compareTo(arg1.getAtfFilePath());
			}
		});

		// ��ʼ����ͼ��Ϣ
		for (int i = 0; i < textures.length; i++)
		{
			Texture texture = textures[i];

			String url = texture.getAtfFilePath();
			File atfFile = new File(outputPath + url.replace(".atf", ".atf"));
			File xmlFile = new File(outputPath + url.replace(".atf", ".xml"));

			texture_id.put(texture, i + 1);
			texture_size.put(texture, (int) (atfFile.length() + xmlFile.length()));
			texture_path.put(texture, url);
		}

		// ������ͼ��ص����ݱ�
		for (String groupID : group_gifKeys.keySet())
		{
			ExportParam param = paramTable.get(groupID);

			Hashtable<GridImgKey, GridImgKey> gifs = group_gifKeys.get(groupID);

			GridImgKey[] gifArray = gifs.values().toArray(new GridImgKey[gifs.size()]);

			TextureSetKey textureSetKey = new TextureSetKey(param.getWidth(), param.getHeight(), param.getParam(), gifArray);
			TextureSet textureSet = textureSetTable.getTextureSet(textureSetKey);
			for (Texture texture : textureSet.getTextures())
			{
				for (Region region : texture.getRegions())
				{
					region.setTexturePath(texture.getAtfFilePath());

					String regionID = groupID + "_" + region.getOwnerChecksum() + "_" + region.getIndex();

					regionID_region.put(regionID, region);
					region_texture.put(region, texture);
				}
			}
		}

		// ��ʼ��������Ϣ
		for (AttireAction action : action_groupList.keySet())
		{
			HashSet<Integer> ids = new HashSet<Integer>();
			HashSet<Texture> textures = new HashSet<Texture>();

			HashSet<String> atfGroupList = action_groupList.get(action);
			for (String atfGroup : atfGroupList)
			{
				TextureSetKey setKey = group_textureSetKey.get(atfGroup);
				TextureSet setVal = textureSetTable.getTextureSet(setKey);
				for (Texture texture : setVal.getTextures())
				{
					int textureID = getTextureID(texture);
					if (textureID > 0)
					{
						ids.add(textureID);
						textures.add(texture);
					}
				}
			}

			Integer[] idArray = ids.toArray(new Integer[ids.size()]);
			Arrays.sort(idArray);

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < idArray.length; i++)
			{
				if (i > 0)
				{
					sb.append(",");
				}
				sb.append(idArray[i]);
			}

			int actionSize = 0;
			String[] actionURLs = new String[textures.size() /** 2 */
			];
			int index = 0;
			for (Texture texture : textures)
			{
				actionSize += getTextureSize(texture);
				actionURLs[index] = getTexturePath(texture);
				index++;

				// actionURLs[index] = texture.getXmlFilePath();
				// index++;
			}
			Arrays.sort(actionURLs);

			StringBuilder urlList = new StringBuilder();
			for (String url : actionURLs)
			{
				if (urlList.length() > 0)
				{
					urlList.append(",");
				}
				urlList.append(url);
			}

			action_textures.put(action, textures.toArray(new Texture[textures.size()]));
			action_textureIDs.put(action, sb.toString());
			action_size.put(action, actionSize);
			action_urls.put(action, urlList.toString());
		}
	}

	// --------------------------------------------------------------------------------
	//
	// ��ͼ�����Ϣ
	//
	// --------------------------------------------------------------------------------

	/**
	 * ��ȡ��ͼ��
	 * 
	 * @return
	 */
	public Texture[] getTextures()
	{
		return textures;
	}

	/**
	 * ��ȡ��ͼID
	 * 
	 * @param texture
	 * @return
	 */
	public int getTextureID(Texture texture)
	{
		if (texture != null && texture_id.containsKey(texture))
		{
			return texture_id.get(texture);
		}
		return 0;
	}

	/**
	 * ��ȡ��ͼ��С
	 * 
	 * @param texture
	 * @return
	 */
	public int getTextureSize(Texture texture)
	{
		if (texture != null && texture_size.containsKey(texture))
		{
			return texture_size.get(texture);
		}
		return 0;
	}

	/**
	 * ��ȡ��ͼ·��
	 * 
	 * @param texture
	 * @return
	 */
	public String getTexturePath(Texture texture)
	{
		if (texture != null && texture_path.containsKey(texture))
		{
			return texture_path.get(texture);
		}
		return "";
	}

	// --------------------------------------------------------------------------------
	//
	// ���������Ϣ
	//
	// --------------------------------------------------------------------------------

	/**
	 * ��ȡ��ͼ����
	 * 
	 * @param imgID
	 * @param row
	 * @param col
	 * @param index
	 * @return
	 */
	public Region getTextureRegion(String atfGroupID, String imgID, int row, int col, int index)
	{
		if (!paramTable.containsKey(atfGroupID))
		{
			atfGroupID = AttireDefParamID;
		}
		return regionID_region.get(atfGroupID + "_" + imgID + "_" + row + "_" + col + "_" + index);
	}

	/**
	 * ��ȡͼ����ص���ͼ
	 * 
	 * @param img
	 * @return
	 */
	public Texture getImgTextures(String atfGroupID, String shaID)
	{
		Region region = getTextureRegion(atfGroupID, shaID, 1, 1, 0);
		if (region != null)
		{
			return region_texture.get(region);
		}
		return null;
	}

	// --------------------------------------------------------------------------------
	//
	// ���������Ϣ
	//
	// --------------------------------------------------------------------------------

	/**
	 * ��������ȡ��ͼ��
	 * 
	 * @param action
	 * @return
	 */
	public Texture[] getActionTextures(AttireAction action)
	{
		if (action != null && action_textures.containsKey(action))
		{
			return action_textures.get(action);
		}
		return new Texture[] {};
	}

	/**
	 * ��ȡ������ͼID�б�
	 * 
	 * @param action
	 * @return
	 */
	public String getActionTextureIDs(AttireAction action)
	{
		if (action != null && action_textureIDs.containsKey(action))
		{
			return action_textureIDs.get(action);
		}
		return "";
	}

	/**
	 * ��ȡ�������ļ���С
	 * 
	 * @param action
	 * @return
	 */
	public int getActionSize(AttireAction action)
	{
		if (action != null && action_size.containsKey(action))
		{
			return action_size.get(action);
		}
		return 0;
	}

	/**
	 * ��ȡ�������ļ�·����
	 * 
	 * @param action
	 * @return
	 */
	public String getActionPaths(AttireAction action)
	{
		if (action != null && action_urls.containsKey(action))
		{
			return action_urls.get(action);
		}
		return "";
	}
}
