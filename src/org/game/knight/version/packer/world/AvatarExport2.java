package org.game.knight.version.packer.world;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.chw.swf.writer.SwfBitmap;
import org.chw.swf.writer.SwfWriter;
import org.chw.util.FileUtil;
import org.chw.util.MD5Util;
import org.chw.util.TextUtil;
import org.chw.util.ZlibUtil;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.world.model.Attire;
import org.game.knight.version.packer.world.model.AttireAction;
import org.game.knight.version.packer.world.model.AttireAnim;
import org.game.knight.version.packer.world.model.AttireAudio;
import org.game.knight.version.packer.world.model.AttireFile;
import org.game.knight.version.packer.world.model.ProjectImgFile;
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

public class AvatarExport2
{
	private static final String AVATAR2_FRAME_PACK = "knight.avatar2.frames";

	private WorldExporter world;

	private String cfgFileKey;

	private Hashtable<Scene, String> scene_url = new Hashtable<Scene, String>();
	private Hashtable<Scene, Integer> scene_size = new Hashtable<Scene, Integer>();

	private StringBuilder versionData = new StringBuilder();

	private Hashtable<String, String> boundle_exportID = new Hashtable<String, String>();

	private Hashtable<ProjectImgFile, String> img_key = new Hashtable<ProjectImgFile, String>();
	private Hashtable<ProjectImgFile, String> img_typeName = new Hashtable<ProjectImgFile, String>();

	/**
	 * ���캯��
	 * 
	 * @param world
	 */
	public AvatarExport2(WorldExporter world)
	{

		this.world = world;
	}

	/**
	 * ��ȡ�汾����
	 * 
	 * @return
	 */
	public String getVersionData()
	{
		return versionData.toString();
	}
	
	public String getCfgFileKey()
	{
		return cfgFileKey;
	}

	public void export(Hashtable<String, Scene> scenes, Hashtable<String, AttireFile> attires, WorldAttires attireManager, boolean zip, boolean mobile) throws IOException
	{
		versionData = new StringBuilder();

		openHistoryFile(new File(world.getDestDir().getPath() + "/.ver/avatar2"));

		exportAttires1(attires, attireManager, zip);

		exportScenes(scenes, zip, mobile);

		saveHistoryFile();
	}

	/**
	 * ����װ���
	 * 
	 * @throws IOException
	 */
	private void exportAttires1(Hashtable<String, AttireFile> attires, WorldAttires attireManager, boolean zip) throws IOException
	{
		final Hashtable<String, ArrayList<Region>> boundle_regions = new Hashtable<String, ArrayList<Region>>();
		final Hashtable<String, String> boundle_fileID = new Hashtable<String, String>();
		final ArrayList<String> boundle_news = new ArrayList<String>();

		final Hashtable<Region, ProjectImgFile> region_img = new Hashtable<Region, ProjectImgFile>();
		final Hashtable<Region, String> region_frameTypeName = new Hashtable<Region, String>();

		GamePacker.progress("����װ������");
		for (AttireFile attireFile : attires.values())
		{
			for (ProjectImgFile img : attireFile.getAllImgs())
			{
				String bagID = attireFile.getImgGroupID(img);
				String imgSHA = world.getChecksumTable().getGID(img.url);

				Region region = new Region(imgSHA, 0, 0, 0, img.width, img.height, 0, 0, img.width, img.height, 0, 0, 0);
				if (region != null)
				{
					if (!boundle_regions.containsKey(bagID))
					{
						boundle_regions.put(bagID, new ArrayList<Region>());
					}

					ArrayList<Region> regions = boundle_regions.get(bagID);
					if (!regions.contains(region))
					{
						regions.add(region);

						String frameID = "2dfile_" + imgSHA + "_" + 1 + "_" + 1 + "_" + "frame" + 0;
						String frameTypeName = getValue(frameID);
						if (frameTypeName == null)
						{
							frameTypeName = "$" + getNextClassID();
						}

						putValue(frameID, frameTypeName);

						region_frameTypeName.put(region, frameTypeName);
						region_img.put(region, img);

						img_typeName.put(img, frameTypeName);
					}
				}
			}

			for (Attire attire : attireFile.getAllAttires())
			{
				for (AttireAction action : attire.getActions())
				{
					for (AttireAnim anim : action.animList)
					{
						String bagID = anim.bagID;
						String imgSHA = world.getChecksumTable().getGID(anim.img.url);

						int rowCount = anim.row;
						int colCount = anim.col;
						int regionCount = rowCount * colCount;

						for (int i = 0; i < regionCount; i++)
						{
							int delay = anim.times[i];
							if (delay > 0)
							{
								Region region = attireManager.getTextureRegion(anim.bagID, imgSHA, anim.row, anim.col, i);
								if (region != null)
								{
									if (!boundle_regions.containsKey(bagID))
									{
										boundle_regions.put(bagID, new ArrayList<Region>());
									}

									ArrayList<Region> regions = boundle_regions.get(bagID);
									if (!regions.contains(region))
									{
										regions.add(region);

										String frameID = "2dframe_" + imgSHA + "_" + rowCount + "_" + colCount + "_" + "frame" + i;
										String frameTypeName = getValue(frameID);
										if (frameTypeName == null)
										{
											frameTypeName = "$" + getNextClassID();
										}

										putValue(frameID, frameTypeName);

										region_frameTypeName.put(region, frameTypeName);
										region_img.put(region, anim.img);
									}
								}
							}
						}
					}
				}
			}
		}

		GamePacker.progress("����װ������");
		for (String key : boundle_regions.keySet())
		{
			ArrayList<Region> regions = boundle_regions.get(key);
			Collections.sort(regions, new Comparator<Region>()
			{
				@Override
				public int compare(Region o1, Region o2)
				{
					return region_frameTypeName.get(o1).compareTo(region_frameTypeName.get(o2));
				}
			});

			StringBuilder fileIDStream = new StringBuilder();
			for (Region region : regions)
			{
				if (fileIDStream.length() > 0)
				{
					fileIDStream.append(",");
				}
				fileIDStream.append(region_frameTypeName.get(region));
			}

			String fileID = fileIDStream.toString();

			boundle_fileID.put(key, fileID);

			if (!world.hasExportedFile(fileID))
			{
				boundle_news.add(key);
			}
		}

		GamePacker.progress("���װ����Դ");
		for (int i = 0; i < boundle_news.size(); i++)
		{
			String key = boundle_news.get(i);
			String fileID = boundle_fileID.get(key);
			ArrayList<Region> regions = boundle_regions.get(key);

			ArrayList<String> regionTypes = new ArrayList<String>();
			ArrayList<byte[]> regionBytes = new ArrayList<byte[]>();

			for (int j = 0; j < regions.size(); j++)
			{
				GamePacker.progress(String.format("���װ����Դ(%s/%s)������ͼ��(%s/%s)", i + 1, boundle_news.size(), j + 1, regions.size()));

				// ����PNG
				Region region = regions.get(j);
				ProjectImgFile regionImg = region_img.get(region);
				BufferedImage img = ImageIO.read(regionImg.file);

				BufferedImage texture = new BufferedImage(region.getClipW(), region.getClipH(), BufferedImage.TYPE_INT_ARGB);
				Graphics2D graphics = (Graphics2D) texture.getGraphics();
				graphics.drawImage(img, 0, 0, region.getClipW(), region.getClipH(), region.getX() + region.getClipX(), region.getY() + region.getClipY(), region.getX() + region.getClipX() + region.getClipW(), region.getY() + region.getClipY() + region.getClipH(), null);
				graphics.dispose();

				ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
				ImageIO.write(texture, "png", outputBytes);

				regionTypes.add(region_frameTypeName.get(region));
				regionBytes.add(outputBytes.toByteArray());

				if (GamePacker.isCancel())
				{
					return;
				}
			}

			// ���SWF
			GamePacker.progress(String.format("���װ����Դ(%s/%s)�����swf�ļ�", i + 1, boundle_news.size()));
			SwfWriter swf = new SwfWriter();
			for (int j = 0; j < regionTypes.size(); j++)
			{
				swf.addBitmap(new SwfBitmap(regionBytes.get(j), AVATAR2_FRAME_PACK, regionTypes.get(j), true));
			}
			world.exportFile(fileID, MD5Util.addSuffix(swf.toBytes(true)), "swf");

			if (GamePacker.isCancel())
			{
				return;
			}
		}

		if (GamePacker.isCancel())
		{
			return;
		}

		// ��ǵ��ļ�����ID
		for (AttireFile attireFile : attires.values())
		{
			for (ProjectImgFile img : attireFile.getAllImgs())
			{
				img_key.put(img, boundle_fileID.get(attireFile.getImgGroupID(img)));
			}
		}
		boundle_exportID = boundle_fileID;

		GamePacker.progress("���װ������");
		StringBuilder attireText = new StringBuilder();
		attireText.append("<attires>\n");
		for (AttireFile attireFile : attires.values())
		{
			for (Attire attire : attireFile.getAllAttires())
			{
				attireText.append(String.format("\t<attire id=\"%s.%s\" name=\"%s\" x=\"%s\" y=\"%s\" width=\"%s\" height=\"%s\">\n", attire.getFileID(), attire.getKey(), attire.getRefKey(), attire.getHitRect().x, attire.getHitRect().y, attire.getHitRect().width, attire.getHitRect().height));

				for (AttireAction action : attire.getActions())
				{
					attireText.append(String.format("\t\t<action id=\"%s\" nameX=\"%s\" nameY=\"%s\" >\n", action.id, action.nameX, action.nameY));

					for (AttireAnim anim : action.animList)
					{
						attireText.append(String.format("\t\t\t<anim x=\"%s\" y=\"%s\" scaleX=\"%s\" scaleY=\"%s\" flip=\"%s\" groupID=\"%s\" layerID=\"%s\">\n", anim.x, anim.y, anim.scaleX, anim.scaleY, anim.flip, anim.groupID, anim.layerID));

						String bagID = anim.bagID;
						String imgSHA = world.getChecksumTable().getGID(anim.img.url);

						int rowCount = anim.row;
						int colCount = anim.col;
						int regionCount = rowCount * colCount;

						for (int i = 0; i < regionCount; i++)
						{
							int delay = anim.times[i];
							if (delay > 0)
							{
								Region region = attireManager.getTextureRegion(anim.bagID, imgSHA, anim.row, anim.col, i);
								if (region != null)
								{
									int offSetX = region.getClipX() + region.getClipW() / 2 - region.getW() / 2;
									int offsetY = region.getClipY() + region.getClipH() - region.getH();

									// ֡��Ϣ
									String frameFileName = world.getExportedFileUrl(boundle_fileID.get(bagID));
									String frameTypeName = region_frameTypeName.get(region);

									attireText.append(String.format("\t\t\t\t<frame fileName=\"%s\" classID=\"%s\" x=\"%s\" y=\"%s\" delay=\"%s\"/>\n", frameFileName, frameTypeName, offSetX, offsetY, delay));
								}
							}
						}

						attireText.append(String.format("\t\t\t</anim>\n"));
					}
					for (AttireAudio audio : action.audioList)
					{
						String audioURL = world.exportFile(world.getChecksumTable().getGID(audio.mp3.url), MD5Util.addSuffix(FileUtil.getFileBytes(audio.mp3.file)),"mp3");
						attireText.append(String.format("\t\t\t<audio path=\"%s\" loop=\"%s\" volume=\"%s\"/>\n", audioURL, audio.loop, audio.volume));
					}

					attireText.append(String.format("\t\t</action>\n"));
				}

				attireText.append(String.format("\t</attire>\n"));
			}
		}
		attireText.append("</attires>");

		byte[] contentBytes = attireText.toString().getBytes("UTF-8");
		if (zip)
		{
			contentBytes = ZlibUtil.compress(contentBytes);
		}

		cfgFileKey = (zip ? "zlib_md5" : "md5") + MD5Util.md5Bytes(contentBytes);

		world.exportFile(cfgFileKey, MD5Util.addSuffix(contentBytes), "cfg");

		//
		StringBuilder configs = new StringBuilder();
		configs.append(String.format("\t<configs>\n"));
		configs.append(String.format("\t\t<config name=\"%s\" path=\"%s\" size=\"%s\"/>\n", "attire", world.getExportedFileUrl(cfgFileKey), world.getExportedFileSize(cfgFileKey)));
		configs.append(String.format("\t</configs>\n"));

		versionData.append(configs.toString());

		//
		StringBuilder txt = new StringBuilder();

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
					// װ��
					if (params.length >= 3)
					{
						roles.append(String.format("\t\t<role faction=\"%s\" sectLv=\"%s\" name=\"%s\">\n", params[1], params[2], attire.getRefKey()));

						for (AttireAction action : attire.getActions())
						{
							if (action.animList.size() > 0)
							{
								ActionInfo info = getActionID(action);

								roles.append(String.format("\t\t\t<action id=\"%s\" size=\"%s\" files=\"%s\"/>\n", action.id, info.size, info.urls));

								String[] urls = info.urls.split("\\,");
								for (String url : urls)
								{
									if (!actionFileUrls.containsKey(url))
									{
										File actionFile = new File(world.getDestDir().getParentFile().getPath() + url);
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
						GamePacker.error("ְҵ������װ����������" + attire.getRefKey() + "   (Ӧ��Ϊ��1_ְҵID_ְҵ�ȼ�_����)");
					}
				}
				else if (params[0].equals("2"))
				{
					// װ��
					if (params.length >= 4)
					{
						equips.append(String.format("\t\t<equip fromID=\"%s\" toID=\"%s\" faction=\"%s\" name=\"%s\">\n", params[1], params[2], params[3], attire.getRefKey()));

						for (AttireAction action : attire.getActions())
						{
							if (action.animList.size() > 0)
							{
								ActionInfo info=getActionID(action);
								
								equips.append(String.format("\t\t\t<action id=\"%s\" size=\"%s\" files=\"%s\"/>\n", action.id, info.size, info.urls));

								String[] urls = info.urls.split("\\,");
								for (String url : urls)
								{
									if (!actionFileUrls.containsKey(url))
									{
										File actionFile = new File(world.getDestDir().getParentFile().getPath() + url);
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
						GamePacker.error("��װ��������װ����������" + attire.getRefKey() + "   (Ӧ��Ϊ��2_��ʼID_����ID_ְҵID_����)");
					}
				}
				else if (params[0].equals("3"))
				{
					// Ч��
					effects.append(String.format("\t\t<effect effectID=\"%s\">\n", attire.getRefKey()));

					for (AttireAction action : attire.getActions())
					{
						if (action.animList.size() > 0)
						{
							ActionInfo info=getActionID(action);
							
							effects.append(String.format("\t\t\t<action id=\"%s\" size=\"%s\" files=\"%s\"/>\n", action.id, info.size, info.urls));

							String[] urls = info.urls.split("\\,");
							for (String url : urls)
							{
								if (!actionFileUrls.containsKey(url))
								{
									File actionFile = new File(world.getDestDir().getParentFile().getPath() + url);
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
					// ����
				}
				else if (params[0].equals("5"))
				{
					// ��ǩ
					// TextureSet
					// textureSet=textureSetTable.getTextureSet(attire.getTextureSetKey());
					// labels.append(String.format("\t\t<label labelID=\"%s\" size=\"%s\" files=\"%s\"/>\n",attire.getRefKey(),getTextureSetSize(textureSet),getTextureSetURLs(textureSet)));
				}
				else if (params[0].equals("6"))
				{
					// ����
					if (params.length >= 3)
					{
						roles.append(String.format("\t\t<roleEffect faction=\"%s\" sectLv=\"%s\" name=\"%s\">\n", params[1], params[2], attire.getRefKey()));

						for (AttireAction action : attire.getActions())
						{
							if (action.animList.size() > 0)
							{
								ActionInfo info=getActionID(action);
								
								roles.append(String.format("\t\t\t<action id=\"%s\" size=\"%s\" files=\"%s\"/>\n", action.id, info.size, info.urls));

								String[] urls = info.urls.split("\\,");
								for (String url : urls)
								{
									if (!actionFileUrls.containsKey(url))
									{
										File actionFile = new File(world.getDestDir().getParentFile().getPath() + url);
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
						GamePacker.error("�뵶�������װ����������" + attire.getRefKey() + "   (Ӧ��Ϊ��6_ְҵID_����)");
					}
				}
				else if (params[0].equals("7"))
				{
				}
				else if(params[0].equals("8"))
				{
					horses.append(String.format("\t\t<horse horseID=\"%s\" name=\"%s\">\n", params[1],attire.getRefKey()));

					if (params.length >= 2)
					{
						for (AttireAction action : attire.getActions())
						{
							if (action.animList.size() > 0)
							{
								ActionInfo info=getActionID(action);
								
								roles.append(String.format("\t\t\t<action id=\"%s\" size=\"%s\" files=\"%s\"/>\n", action.id, info.size, info.urls));
	
								String[] urls = info.urls.split("\\,");
								for (String url : urls)
								{
									if (!actionFileUrls.containsKey(url))
									{
										File actionFile = new File(world.getDestDir().getParentFile().getPath() + url);
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
						GamePacker.error("�����������װ����������" + attire.getRefKey() + "   (Ӧ��Ϊ��0_����ID_����)");
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
		txt.append("\t</attires>\n");

		versionData.append(txt.toString());
	}

	private static class ActionInfo
	{
		public int size;
		public String urls;

		public ActionInfo(String urls, int size)
		{
			this.urls = urls;
			this.size = size;
		}
	}

	private ActionInfo getActionID(AttireAction action)
	{
		HashSet<String> bagIDs = new HashSet<String>();
		for (AttireAnim anim : action.animList)
		{
			bagIDs.add(anim.bagID);
		}

		int size = 0;
		StringBuilder urls = new StringBuilder();

		for (String bagID : bagIDs)
		{
			String key = boundle_exportID.get(bagID);
			if (urls.length() > 0)
			{
				urls.append(",");
			}
			urls.append(world.getExportedFileUrl(key));

			size += (int) world.getExportedFileSize(key);
		}

		return new ActionInfo(urls.toString(), size);
	}

	/**
	 * ����������
	 * 
	 * @throws IOException
	 */
	private void exportScenes(Hashtable<String, Scene> scenes, boolean zip, boolean mobile) throws IOException
	{
		StringBuilder sceneData = new StringBuilder();
		sceneData.append("\t<scenes>\n");

		for (Scene scene : scenes.values())
		{
			GamePacker.log("������� : " + scene.getSceneName() + " (" + scene.getInnerPath() + ")");

			Hashtable<String, Integer> url_size = new Hashtable<String, Integer>();
			ArrayList<Attire> sceneAttires = new ArrayList<Attire>();

			String bgsPath = "";
			if (scene.getBackSound() != null)
			{
				bgsPath = world.exportFile("md5"+world.getChecksumTable().getGID(scene.getBackSound().url), scene.getBackSound().file);
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

			sb.append("\t<layers>\n");
			for (SceneBackLayer layer : scene.getBackLayers())
			{
				if (layer.img != null)
				{
					String url = world.getExportedFileUrl(img_key.get(layer.img));
					Integer size = (int) world.getExportedFileSize(img_key.get(layer.img));

					url_size.put(url, size);

					sb.append("\t\t<layer x=\"" + layer.x + "\" y=\"" + layer.y + "\" speed=\"" + layer.speed + "\" fileURL=\"" + url + "\" fileType=\"" + img_typeName.get(layer.img) + "\" />\n");
				}
			}
			sb.append("\t</layers>\n");

			sb.append("\t<foreLayers>\n");
			for (SceneForeLayer layer : scene.getForeLayers())
			{
				if (layer.img != null)
				{
					String url = world.getExportedFileUrl(img_key.get(layer.img));
					Integer size = (int) world.getExportedFileSize(img_key.get(layer.img));

					url_size.put(url, size);

					sb.append("\t\t<layer x=\"" + layer.x + "\" y=\"" + layer.y + "\" width=\"" + layer.w + "\" speed=\"" + layer.speed + "\" fileURL=\"" + url + "\" fileType=\"" + img_typeName.get(layer.img) + "\"/>\n");
				}
			}
			sb.append("\t</foreLayers>\n");

			sb.append("\t<backAnims>\n");
			for (SceneAnim anim : scene.getBackAnims())
			{
				if (anim.attire != null)
				{
					sceneAttires.add(anim.attire);
					sb.append("\t\t<anim x=\"" + anim.x + "\" y=\"" + anim.y + "\" offsetX=\"" + anim.offsetX + "\" offsetY=\"" + anim.offsetY + "\" direction=\"" + anim.direction + "\" attire=\"" + anim.attire.getFileID() + "." + anim.attire.getRefKey() + "\"/>\n");
				}
			}
			sb.append("\t</backAnims>\n");

			sb.append("\t<anims>\n");
			for (SceneAnim anim : scene.getAnims())
			{
				if (anim.attire != null)
				{
					sceneAttires.add(anim.attire);
					sb.append("\t\t<anim x=\"" + anim.x + "\" y=\"" + anim.y + "\" offsetX=\"" + anim.offsetX + "\" offsetY=\"" + anim.offsetY + "\" direction=\"" + anim.direction + "\" attire=\"" + anim.attire.getFileID() + "." + anim.attire.getRefKey() + "\"/>\n");
				}
			}
			sb.append("\t</anims>\n");

			sb.append("\t<npcs>\n");
			for (SceneNpc npc : scene.getNpcs())
			{
				if (npc.attire != null)
				{
					sceneAttires.add(npc.attire);
					sb.append("\t\t<npc id=\"" + npc.id + "\" x=\"" + npc.x + "\" y=\"" + npc.y + "\" direction=\"" + npc.direction + "\" attire=\"" + npc.attire.getFileID() + "." + npc.attire.getRefKey() + "\"/>\n");
				}
			}
			sb.append("\t</npcs>\n");

			sb.append("\t<doors>\n");
			for (SceneDoor door : scene.getDoors())
			{
				SceneHot hot = door.getHot();
				if (hot != null)
				{
					if (door.attire != null)
					{
						sceneAttires.add(door.attire);
					}

					sb.append("\t\t<door x=\"" + door.x + "\" y=\"" + door.y + "\" offsetX=\"0\" offsetY=\"0\" direction=\"" + door.direction + "\" attire=\"" + (door.attire != null ? door.attire.getFileID() + "." + door.attire.getKey() : "") + "\">\n");
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
								if (monster.attire != null)
								{
									sceneAttires.add(monster.attire);

									sb.append("\t\t<monster id=\"" + monster.monsterID + "\" attire=\"" + monster.attire.getFileID() + "." + monster.attire.getKey() + "\" />\n");
									monsterIDs.add(monster.monsterID);
								}
							}
						}
					}
				}
			}
			sb.append("\t</monsters>\n");

			sb.append("</scene>");

			// ��������
			byte[] sceneBytes = sb.toString().getBytes("UTF-8");
			if (zip)
			{
				sceneBytes = ZlibUtil.compress(sceneBytes);
			}
			String sceneBytesKey = (zip ? "zlib_md5" : "md5") + MD5Util.md5Bytes(sceneBytes);
			world.exportFile(sceneBytesKey, MD5Util.addSuffix(sceneBytes), "xml");

			// ����URL�ʹ�С
			scene_url.put(scene, world.getExportedFileUrl(sceneBytesKey));
			scene_size.put(scene, (int) world.getExportedFileSize(sceneBytesKey));

			// ��¼��Դ�ļ�
			for (Attire attire : sceneAttires)
			{
				for (AttireAction action : attire.getActions())
				{
					for (AttireAnim anim : action.animList)
					{
						String url = world.getExportedFileUrl(boundle_exportID.get(anim.bagID));
						int size = (int) world.getExportedFileSize(boundle_exportID.get(anim.bagID));

						url_size.put(url, size);
					}
				}
			}

			// �ܼƴ�С
			int totalSize = 0;
			for (Integer size : url_size.values())
			{
				totalSize += size;
			}
			totalSize += (int) world.getExportedFileSize(sceneBytesKey);

			// �ܼ��ļ�
			StringBuilder totalUrls = new StringBuilder();
			totalUrls.append(world.getExportedFileUrl(sceneBytesKey));
			for (String url : url_size.keySet())
			{
				totalUrls.append("," + url);
			}

			//
			sceneData.append(String.format("\t\t<scene id=\"%s\" type=\"%s\" size=\"%s\" files=\"%s\"/>\n", scene.getSceneID(), scene.getSceneType(), totalSize, totalUrls.toString()));

			if (GamePacker.isCancel())
			{
				return;
			}
		}

		sceneData.append("\t</scenes>\n");

		versionData.append(sceneData.toString());
	}

	// ---------------------------------------------------------------------------------------------------------
	//
	// �汾�⴦��
	//
	// ---------------------------------------------------------------------------------------------------------

	private int nextClassID = 0;
	private Hashtable<String, String> old_table;
	private Hashtable<String, String> new_table;

	private File historyFile;

	/**
	 * ��ȡ��һ�����͵�ID
	 * 
	 * @return
	 */
	private int getNextClassID()
	{
		int result = nextClassID;
		nextClassID++;
		return result;
	}

	/**
	 * ��ȡֵ
	 * 
	 * @param key
	 * @return
	 */
	private String getValue(String key)
	{
		if (old_table.containsKey(key))
		{
			new_table.put(key, old_table.get(key));
			old_table.remove(key);
		}
		if (new_table.containsKey(key))
		{
			return new_table.get(key);
		}

		return null;
	}

	/**
	 * д��ֵ
	 * 
	 * @param key
	 * @param value
	 */
	private void putValue(String key, String value)
	{
		if (old_table.containsKey(key))
		{
			old_table.remove(key);
		}
		new_table.put(key, value);
	}

	/**
	 * ����ʷ�ļ�
	 */
	private void openHistoryFile(File file)
	{
		historyFile = file;

		old_table = new Hashtable<String, String>();
		new_table = new Hashtable<String, String>();

		if (!historyFile.exists())
		{
			return;
		}

		BufferedReader input = null;
		try
		{
			input = new BufferedReader(new InputStreamReader(new FileInputStream(historyFile), "UTF-8"));
			while (true)
			{
				String line = input.readLine();
				if (line == null)
				{
					break;
				}

				line.trim();

				if (line.isEmpty())
				{
					continue;
				}

				String[] parts = line.split("=");
				String key = parts[0].trim();
				String val = parts[1].trim();

				if (key.isEmpty() || val.isEmpty())
				{
					continue;
				}

				if (key.charAt(0) == '$')
				{
					String propName = key.substring(1);
					String propValue = val;

					if (propName.equals("nextClassID"))
					{
						nextClassID = Integer.parseInt(propValue);
					}
				}
				else
				{
					old_table.put(key, val);
				}
			}
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (input != null)
			{
				try
				{
					input.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * ������ʷ�ļ�
	 */
	private void saveHistoryFile()
	{
		StringBuilder output = new StringBuilder();

		output.append("$nextClassID = " + nextClassID + "\n");

		String[] keys = new_table.keySet().toArray(new String[new_table.size()]);
		Arrays.sort(keys);

		for (String key : keys)
		{
			output.append(key + " = " + new_table.get(key) + "\n");
		}

		// ���浽�ļ�
		try
		{
			byte[] bytes = output.toString().getBytes("UTF-8");
			FileUtil.writeFile(historyFile, bytes);
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}
}
