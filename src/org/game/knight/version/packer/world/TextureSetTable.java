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
import java.util.Comparator;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.chw.util.FileUtil;
import org.chw.util.MD5Util;
import org.chw.util.MaxRects;
import org.chw.util.MaxRects.Rect;
import org.chw.util.MaxRects.RectSet;
import org.chw.util.XmlUtil;
import org.chw.util.ZlibUtil;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.GamePackerConst;

public class TextureSetTable
{
	private WorldExporter world;

	private File file;
	private Hashtable<TextureSetKey, TextureSet> textureSetTable;
	private Hashtable<TextureSetKey, TextureSet> revisionTable;

	/**
	 * 构造函数
	 * 
	 * @param checksumTable
	 * @param imgClipTable
	 */
	public TextureSetTable(WorldExporter world)
	{
		this.world = world;
	}

	/**
	 * 获取贴图集
	 * 
	 * @param key
	 * @return
	 */
	public TextureSet getTextureSet(TextureSetKey key)
	{
		if (key == null)
		{
			return null;
		}
		return revisionTable.get(key);
	}

	/**
	 * 获取所有贴图
	 * 
	 * @return
	 */
	public Texture[] getAllTextures()
	{
		ArrayList<Texture> allTexture = new ArrayList<Texture>();
		for (TextureSet set : revisionTable.values())
		{
			for (Texture texture : set.getTextures())
			{
				allTexture.add(texture);
			}
		}
		return allTexture.toArray(new Texture[allTexture.size()]);
	}

	/**
	 * 获取帖图文件地址
	 * 
	 * @return
	 */
	public String[] getTextureFileURLs()
	{
		ArrayList<String> files = new ArrayList<String>();
		for (TextureSet set : revisionTable.values())
		{
			for (Texture texture : set.getTextures())
			{
				files.add(texture.getAtfFilePath());
				files.add(texture.getPngFilePath());
				files.add(texture.getXmlFilePath());
			}
		}

		String[] urls = new String[files.size()];
		urls = files.toArray(urls);
		Arrays.sort(urls);

		return urls;
	}

	/**
	 * 是否包含贴图集
	 * 
	 * @param key
	 */
	public boolean contains(TextureSetKey key)
	{
		if (revisionTable.containsKey(key))
		{
			return true;
		}

		if (textureSetTable.containsKey(key))
		{
			return true;
		}
		return false;
	}

	/**
	 * 添加贴图集
	 * 
	 * @param checksumSet
	 */
	public void add(TextureSetKey key)
	{
		if (revisionTable.containsKey(key))
		{
			return;
		}

		if (textureSetTable.containsKey(key))
		{
			revisionTable.put(key, textureSetTable.get(key));

			textureSetTable.remove(key);
		}
		else
		{
			int regionCount = 0;

			// 统计网格图像的总格数
			GridImgKey[] gridImgs = key.getClips();
			for (int i = 0; i < gridImgs.length; i++)
			{
				GridImgKey gridImgKey = gridImgs[i];
				regionCount += gridImgKey.getRowCount() * gridImgKey.getColCount();
			}

			// 建立图像所有的网格区域
			Region[] regions = new Region[regionCount];
			int index = 0;
			for (int i = 0; i < gridImgs.length; i++)
			{
				GridImgKey gridImgKey = gridImgs[i];

				GridImg[] clips = world.getGridImgTable().getClips(gridImgKey);
				for (int j = 0; j < clips.length; j++)
				{
					String imgID = world.getChecksumTable().getChecksumID(gridImgKey.getFileInnerPath());
					String regionKey = imgID + "_" + gridImgKey.getRowCount() + "_" + gridImgKey.getColCount();

					regions[index] = new Region(regionKey, j, clips[j], gridImgKey.getFile(), gridImgKey.getTimes()[j]);
					index++;
				}
			}

			// 记录贴图集
			revisionTable.put(key, new TextureSet(key, mergerTextures(regions, key.getWidth(), key.getHeight())));
		}
	}

	/**
	 * 打开贴图集配置
	 * 
	 * @param file
	 */
	public void open(File file)
	{
		this.file = file;

		this.textureSetTable = new Hashtable<TextureSetKey, TextureSet>();
		this.revisionTable = new Hashtable<TextureSetKey, TextureSet>();

		if (file == null || !file.exists() || !file.isFile())
		{
			return;
		}

		BufferedReader input = null;
		try
		{
			input = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
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

				// 还原贴图集KEY
				int ww = 2048;
				int hh = 2048;
				String type = "DXT";
				int typeIndex = key.indexOf(">");
				if (typeIndex != -1)
				{
					type = key.substring(0, typeIndex);
					key = key.substring(typeIndex + 1);

					int sizeIndex = type.indexOf("<");
					if (sizeIndex != -1)
					{
						String[] wh = type.substring(0, sizeIndex).split(",");
						if (wh.length == 2)
						{
							ww = XmlUtil.parseInt(wh[0], ww);
							hh = XmlUtil.parseInt(wh[1], hh);
						}
						
						type = type.substring(sizeIndex + 1);
					}
				}
				String[] keys = key.split("\\+");
				GridImgKey[] clipKeys = new GridImgKey[keys.length];
				for (int i = 0; i < keys.length; i++)
				{
					String[] clipParams = keys[i].split("_");
					String[] timeParams = clipParams.length > 3 ? clipParams[3].split("\\.") : new String[] { "0" };

					int[] times = new int[timeParams.length];
					for (int j = 0; j < times.length; j++)
					{
						times[j] = Integer.parseInt(timeParams[j]);
						times[j] = times[j] > 0 ? 1 : 0;
					}

					clipKeys[i] = new GridImgKey(clipParams[0], Integer.parseInt(clipParams[1]), Integer.parseInt(clipParams[2]), times);
				}

				// 还原贴图信息
				String[] vals = val.split("\\+");
				Texture[] textures = new Texture[vals.length];
				for (int i = 0; i < vals.length; i++)
				{
					String[] params = vals[i].split(":");
					String[] textureFiles = params[0].split(",");
					String[] textureRegions = params[1].split(";");

					String atfSavePath = textureFiles[0];
					String pngSavePath = textureFiles[1];
					String xmlSavePath = textureFiles[2];

					Region[] regions = new Region[textureRegions.length];
					for (int j = 0; j < textureRegions.length; j++)
					{
						String[] regionParams = textureRegions[j].split(",");
						String ownerChecksum = regionParams[0];
						int index = Integer.parseInt(regionParams[1]);
						int x = Integer.parseInt(regionParams[2]);
						int y = Integer.parseInt(regionParams[3]);
						int w = Integer.parseInt(regionParams[4]);
						int h = Integer.parseInt(regionParams[5]);
						int clipX = Integer.parseInt(regionParams[6]);
						int clipY = Integer.parseInt(regionParams[7]);
						int clipW = Integer.parseInt(regionParams[8]);
						int clipH = Integer.parseInt(regionParams[9]);
						int textureX = Integer.parseInt(regionParams[10]);
						int textureY = Integer.parseInt(regionParams[11]);
						int textureR = Integer.parseInt(regionParams[12]);

						regions[j] = new Region(ownerChecksum, index, x, y, w, h, clipX, clipY, clipW, clipH, textureX, textureY, textureR);
					}

					textures[i] = new Texture(atfSavePath, pngSavePath, xmlSavePath, regions);
				}

				TextureSetKey textureSetKey = new TextureSetKey(ww, hh, type, clipKeys);
				TextureSet textureSet = new TextureSet(textureSetKey, textures);

				textureSetTable.put(textureSetKey, textureSet);
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
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
	 * 保存贴图集配置
	 * 
	 * @param file
	 */
	public void save()
	{
		saveAs(file);
	}

	/**
	 * 另存贴图集配置
	 * 
	 * @param file
	 */
	public void saveAs(File file)
	{
		if (file == null || revisionTable == null)
		{
			return;
		}

		// 排序贴图集KEY
		TextureSetKey[] keys = new TextureSetKey[revisionTable.size()];
		keys = revisionTable.keySet().toArray(keys);
		Arrays.sort(keys, new Comparator<TextureSetKey>()
		{
			@Override
			public int compare(TextureSetKey o1, TextureSetKey o2)
			{
				return o1.toString().compareTo(o2.toString());
			}
		});

		// 合并贴图集内容
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < keys.length; i++)
		{
			TextureSetKey key = keys[i];
			TextureSet textureSet = revisionTable.get(key);

			sb.append(key.toString());
			sb.append(" = ");

			for (Texture texture : textureSet.getTextures())
			{
				sb.append(texture.getAtfFilePath() + "," + texture.getPngFilePath() + "," + texture.getXmlFilePath() + ":");

				for (Region region : texture.getRegions())
				{
					sb.append(region.getOwnerChecksum() + ",");
					sb.append(region.getIndex() + ",");
					sb.append(region.getX() + ",");
					sb.append(region.getY() + ",");
					sb.append(region.getW() + ",");
					sb.append(region.getH() + ",");
					sb.append(region.getClipX() + ",");
					sb.append(region.getClipY() + ",");
					sb.append(region.getClipW() + ",");
					sb.append(region.getClipH() + ",");
					sb.append(region.getTextureX() + ",");
					sb.append(region.getTextureY() + ",");
					sb.append(region.getTextureR() + ";");
				}

				if (texture != textureSet.getTextures()[textureSet.getTextures().length - 1])
				{
					sb.append("+");
				}
			}

			sb.append("\n");
		}

		// 清理无效贴图文件
		for (TextureSet textureSet : textureSetTable.values())
		{
			for (Texture texture : textureSet.getTextures())
			{
				File atfFile = new File(world.getDestDir().getPath() + texture.getAtfFilePath());
				if (atfFile.exists() && atfFile.isFile())
				{
					// atfFile.delete();
				}

				File pngFile = new File(world.getDestDir().getPath() + texture.getPngFilePath());
				if (pngFile.exists() && pngFile.isFile())
				{
					// pngFile.delete();
				}

				File xmlFile = new File(world.getDestDir().getPath() + texture.getXmlFilePath());
				if (xmlFile.exists() && xmlFile.isFile())
				{
					// xmlFile.delete();
				}
			}
		}

		// 保存到文件
		try
		{
			byte[] bytes = sb.toString().getBytes("UTF-8");
			FileUtil.writeFile(file, bytes);
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 合并贴图
	 * 
	 * @param regions
	 * @param w
	 * @param h
	 * @return
	 */
	private Texture[] mergerTextures(Region[] regions, int w, int h)
	{
		MaxRects rects=new MaxRects(w,h,false);
		
		for(Region region:regions)
		{
			if (region.getTime() <= 0)
			{
				System.out.println("忽略空白区域：" + region.getFile().getPath() + " frame(" + region.getX() + "," + region.getY() + "," + region.getW() + "," + region.getH() + ") clip(" + region.getClipX() + "," + region.getClipY() + "," + region.getClipW() + "," + region.getClipH() + ") texture(" + region.getTextureX() + "," + region.getTextureY() + ")");
				continue;
			}
			rects.push(region, Math.min(2048, region.getClipW()), Math.min(2048,region.getClipH()));
		}
		
		try
		{
			rects.pack();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		int textureCount=rects.getRectSets().size();
		Texture[] textures=new Texture[textureCount];
		for(int i=0;i<textureCount;i++)
		{
			RectSet set=rects.getRectSets().get(i);
			
			int regionCount=set.getRects().size();
			Region[] regionList=new Region[regionCount];
			for(int j=0;j<regionCount;j++)
			{
				Rect rect=set.getRects().get(j);
				
				Region region=(Region)rect.data;
				region.setTextureX(rect.x);
				region.setTextureY(rect.y);
				
				regionList[j]=region;
			}
			
			textures[i]=new Texture(set.getWidth(), set.getHeight(), regionList);
		}
		
		return textures;
	}
	
	private int[] sizes = new int[] { 32, 64, 128, 256, 512, 1024, 2048 };

	/**
	 * 调整大小
	 * 
	 * @param size
	 * @return
	 */
	public int adjustSize(int size)
	{
		if (size > sizes[sizes.length - 1])
		{
			return sizes[sizes.length - 1];
		}

		int last = 0;
		for (int i = 0; i < sizes.length; i++)
		{
			last = sizes[i];
			if (size <= last)
			{
				break;
			}
		}

		return last;
	}

	/**
	 * 保存贴图
	 * 
	 * @param textureData
	 * @throws IOException
	 */
	public void saveTexture(Texture textureData, String type) throws IOException
	{
		long fileID=world.getOptionTable().getNextFileID();
		long folderID = (fileID - 1) / GamePackerConst.FILE_COUNT_EACH_DIR + 1;
		
		// 确定png路径
		String pngFilePath = "/" + folderID + "/" + fileID + ".png";
		textureData.setPngFilePath("/" + world.getDestDir().getName() + pngFilePath);

		// 确定atf路径
		//world.getOptionTable().getNextFileID();
		String atfFilePath = "/" + folderID + "/" + fileID + ".atf";
		textureData.setAtfFilePath("/" + world.getDestDir().getName() + atfFilePath);

		// 确定xml路径
		//world.getOptionTable().getNextFileID();
		String xmlFilePath = "/" + folderID + "/" + fileID + ".xml";
		textureData.setXmlFilePath("/" + world.getDestDir().getName() + xmlFilePath);
		
		GamePacker.log("输出贴图", pngFilePath + " .xml .atf");

		// 合并贴图png文件，贴图xml文件
		int drawX = 0;
		int drawY = 0;

		BufferedImage texture = new BufferedImage(textureData.getWidth(), textureData.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = (Graphics2D) texture.getGraphics();

		StringBuilder atlas = new StringBuilder();
		atlas.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		atlas.append("<TextureAtlas imagePath=\"" + ("/" + world.getDestDir().getName() + pngFilePath) + "\">");

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
		atlas.append("</TextureAtlas>");

		graphics.dispose();

		if (GamePacker.isCancel())
		{
			return;
		}

		// 保存png文件
		File saveFile = new File(world.getDestDir().getPath() + pngFilePath);
		if (saveFile.getParentFile().exists() == false)
		{
			saveFile.mkdirs();
		}
		ImageIO.write(texture, "png", saveFile);

		if (GamePacker.isCancel())
		{
			return;
		}

		// 保存ATF文件
		File saveAtf = new File(world.getDestDir().getPath() + atfFilePath);

		File atfExe = new File(GamePackerConst.getJarDir().getPath() + File.separatorChar + "png2atf.exe");

		boolean hasZ = false;

		// 过滤-z参数
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

		if (atfExe.exists())
		{
			writeATF(atfExe.getPath() + " -i " + saveFile.getPath() + " -o " + saveAtf.getPath() + " " + type);
		}
		else
		{
			writeATF("png2atf -i " + saveFile.getPath() + " -o " + saveAtf.getPath() + " " + type);
		}

		if (hasZ)
		{
			byte[] atfBytes = FileUtil.getFileBytes(saveAtf);
			atfBytes = ZlibUtil.compress(atfBytes);
			FileUtil.writeFile(saveAtf, atfBytes);
		}
		
		if(saveAtf.exists())
		{
			byte[] atfBytes=FileUtil.getFileBytes(saveAtf);
			byte[] xmlBytes=atlas.toString().getBytes("utf8");
			
			ByteArrayOutputStream temp=new ByteArrayOutputStream();
			temp.write(atfBytes);
			temp.write(xmlBytes);
			temp.write(xmlBytes.length);
			
			byte[] newBytes=temp.toByteArray();
			
			FileUtil.writeFile(saveAtf, MD5Util.addSuffix(newBytes));
		}

		if (!saveAtf.exists())
		{
			GamePacker.log("输出ATF失败！", pngFilePath + " .xml .atf");
		}
	}

	private void writeATF(String cmd)
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
}
