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
	 * 构造函数
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
	 * 构建
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
	// 参数读取
	//
	// ----------------------------------------------------------------------------------------------------------------------------------------

	/**
	 * 解析参数文件
	 * 
	 * @param paramTable
	 */
	private void readParams(Hashtable<String, File> paramFiles)
	{
		int index = 0;
		for (File file : paramFiles.values())
		{
			index++;

			GamePacker.progress(String.format("解析输出参数(%s/%s) : %s", index, paramFiles.size(), file.getPath()));

			Document document = null;

			try
			{
				SAXReader reader = new SAXReader();
				document = reader.read(file);
			}
			catch (DocumentException e)
			{
				GamePacker.error("输出参数文件解析失败！(" + file.getPath() + ")   " + e);
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

								GamePacker.error("输出参数ID冲突！(" + file.getPath() + " : " + id + ") -> ( " + old.getFile().getPath() + " : " + id + ")");
							}
							else
							{
								this.paramTable.put(id, new ExportParam(file, id, w, h, param));
							}

							continue;
						}
					}
					GamePacker.error("无效的输出参数！(" + file.getPath() + ")  " + line);
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
	// 资源读取
	//
	// ----------------------------------------------------------------------------------------------------------------------------------------

	/**
	 * 解析装扮
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
			GamePacker.progress(String.format("解析装扮文件(%s/%s) : %s", index, attires.size(), attireFile.getInnerPath()));
			attireFile.open(exporter);

			if (GamePacker.isCancel())
			{
				return;
			}
		}
	}

	/**
	 * 解析场景
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
			GamePacker.progress(String.format("解析场景文件(%s/%s) : %s", index, scenes.size(), scene.getInnerPath()));
			scene.open(exporter);

			if (GamePacker.isCancel())
			{
				return;
			}
		}
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------
	//
	// 资源分类
	//
	// ----------------------------------------------------------------------------------------------------------------------------------------

	// 所有动画序列
	private HashSet<GridImgKey> gifKeys = new HashSet<GridImgKey>();

	// 组ID_动画序列
	private Hashtable<String, Hashtable<GridImgKey, GridImgKey>> group_gifKeys = new Hashtable<String, Hashtable<GridImgKey, GridImgKey>>();

	// 动作_组ID列表
	private Hashtable<AttireAction, HashSet<String>> action_groupList = new Hashtable<AttireAction, HashSet<String>>();

	/**
	 * 分类装扮资源
	 * 
	 * @param attires
	 */
	private void classifyAttireResource(Hashtable<String, AttireFile> attires)
	{
		Hashtable<String, AttireFile> checkTable = new Hashtable<String, AttireFile>();
		Hashtable<String, AttireFile> checkTable2 = new Hashtable<String, AttireFile>();

		// 遍历所有装扮
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
					GamePacker.error("装扮定义冲突:" + attire.getKey() + ", fileA=" + (checkTable.get(attire.getKey()).getInnerPath()) + " ,fileB=" + attireFile.getInnerPath());
				}
				checkTable.put(attire.getKey(), attireFile);

				if (attire.getRefKey() != null && checkTable2.containsKey(attire.getRefKey()))
				{
					GamePacker.error("装扮定义冲突:" + attire.getRefKey() + ", fileA=" + (checkTable2.get(attire.getRefKey()).getInnerPath()) + " ,fileB=" + attireFile.getInnerPath());
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

						// 关联动作到组ID列表
						if (!action_groupList.containsKey(action))
						{
							action_groupList.put(action, new HashSet<String>());
						}
						action_groupList.get(action).add(atfGroup);

						// 关联组ID到动画序列，并合并动画序列的时间列表。
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

						// 保存动画序列引用
						gifKeys.add(gifKey);

						// 检测取消
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
	 * 分类场景资源
	 * 
	 * @param attires
	 */
	private void classifySceneResource(Hashtable<String, AttireFile> attires)
	{
		for (AttireFile attireFile : attires.values())
		{
			// 遍历图像列表
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

				// 关联组ID到动画序列，并合并动画序列的时间列表。
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

				// 保存动画序列引用
				gifKeys.add(gifKey);

				// 检测取消
				if (GamePacker.isCancel())
				{
					return;
				}
			}

			// 遍历动画列表
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

						// 关联动作到组ID列表
						if (!action_groupList.containsKey(action))
						{
							action_groupList.put(action, new HashSet<String>());
						}
						action_groupList.get(action).add(atfGroup);

						// 关联组ID到动画序列，并合并动画序列的时间列表。
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

						// 保存动画序列引用
						gifKeys.add(gifKey);

						// 检测取消
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
	 * 规格化时间数组
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
	 * 合并时间数组
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
	// 裁切透明区域
	//
	// ----------------------------------------------------------------------------------------------------------------------------------------

	/**
	 * 切除透明区域
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
			GamePacker.progress(String.format("裁切图像矩形(%s/%s) : %s", index, addedGifs.size(), gif.getFileInnerPath()));
			clipTable.add(gif);
		}
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------
	//
	// 输出贴图纹理
	//
	// ----------------------------------------------------------------------------------------------------------------------------------------

	// 组ID_贴图集ID
	private Hashtable<String, TextureSetKey> group_textureSetKey = new Hashtable<String, TextureSetKey>();

	/**
	 * 输出纹理
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

			// 检测取消
			if (GamePacker.isCancel())
			{
				return;
			}
		}

		// 检测取消
		if (GamePacker.isCancel())
		{
			return;
		}

		int index = 0;
		for (TextureSetKey texturesetKey : addedTextureSetKeys)
		{
			index++;

			GamePacker.progress(String.format("计算贴图集(%s/%s) : %s (此贴图集共计%s个小图)", index, addedTextureSetKeys.size(), textureSetKey_GroupID.get(texturesetKey), texturesetKey.getClips().length));
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

			// 检测取消
			if (GamePacker.isCancel())
			{
				return;
			}
		}

		// 检测取消
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

			// 检测取消
			if (GamePacker.isCancel())
			{
				return;
			}
		}
	}

	/**
	 * 输出贴图
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

		// 跳过两个ID
		// world.getOptionTable().getNextFileID();
		// world.getOptionTable().getNextFileID();

		// 确定png路径
		String pngFilePath = "/" + folderID + "/" + fileID + ".png";
		textureData.setPngFilePath("/" + world.getDestDir().getName() + pngFilePath);

		// 确定xml路径
		String atfFilePath = "/" + folderID + "/" + fileID + ".atf";
		textureData.setAtfFilePath("/" + world.getDestDir().getName() + atfFilePath);

		// 确定xml路径
		String xmlFilePath = "/" + folderID + "/" + fileID + ".xml";
		textureData.setXmlFilePath("/" + world.getDestDir().getName() + xmlFilePath);

		// 建立目录
		File testFile = new File(world.getDestDir().getPath() + "/" + folderID);
		if (!testFile.exists())
		{
			testFile.mkdirs();
		}

		// 合并贴图png文件，xml文件
		GamePacker.progress(String.format("输出贴图(%s/%s) : [%s] 合并中...", index, count, groupName));
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

		// 检测取消
		if (GamePacker.isCancel())
		{
			return;
		}

		// 保存png文件
		GamePacker.progress(String.format("输出贴图(%s/%s) : [%s] 输出PNG( %s )", index, count, groupName, pngFilePath));
		File savePng = new File(world.getDestDir().getPath() + pngFilePath);
		ImageIO.write(texture, "png", savePng);

		// 检测取消
		if (GamePacker.isCancel())
		{
			return;
		}

		// 保存xml文件
		GamePacker.progress(String.format("输出贴图(%s/%s) : [%s] 输出XML( %s )", index, count, groupName, xmlFilePath));
		StringBuilder xmlContent = new StringBuilder();
		xmlContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xmlContent.append("<TextureAtlas imagePath=\"" + ("/" + world.getDestDir().getName() + pngFilePath) + "\">");
		xmlContent.append(atlas.toString());
		xmlContent.append("</TextureAtlas>");

		// 检测取消
		if (GamePacker.isCancel())
		{
			return;
		}

		// 保存ATF文件
		GamePacker.progress(String.format("输出贴图(%s/%s) : [%s] 输出ATF( %s )", index, count, groupName, atfFilePath));
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
			GamePacker.error("输出ATF失败！", pngFilePath + " .xml .atf");
		}

		// 输出debug regions
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

		// 删除临时png
		if (savePng.exists() && keepAtfPng==false)
		{
			savePng.delete();
		}
	}

	/**
	 * 输出ATF
	 * 
	 * @param input
	 * @param output
	 * @param type
	 */
	private void writeATF(File input, File output, String type)
	{
		// 过滤-z参数
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
	 * 调用CMD
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
	// 初始化所有贴图信息
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
	 * 初始化所有贴图数据
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

		// 初始化贴图信息
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

		// 建立贴图相关的数据表
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

		// 初始化动作信息
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
	// 贴图相关信息
	//
	// --------------------------------------------------------------------------------

	/**
	 * 获取贴图集
	 * 
	 * @return
	 */
	public Texture[] getTextures()
	{
		return textures;
	}

	/**
	 * 获取贴图ID
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
	 * 获取贴图大小
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
	 * 获取贴图路径
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
	// 区域相关信息
	//
	// --------------------------------------------------------------------------------

	/**
	 * 获取贴图区域
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
	 * 获取图像相关的贴图
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
	// 动作相关信息
	//
	// --------------------------------------------------------------------------------

	/**
	 * 按动作获取贴图集
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
	 * 获取动作贴图ID列表
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
	 * 获取动作的文件大小
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
	 * 获取动作的文件路径集
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
