package org.game.knight.version.packer.world.output3d;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

import org.eclipse.swt.graphics.Rectangle;
import org.game.knight.version.packer.world.model.Attire;
import org.game.knight.version.packer.world.model.AttireAction;
import org.game.knight.version.packer.world.model.AttireAnim;
import org.game.knight.version.packer.world.model.ProjectImgFile;
import org.game.knight.version.packer.world.model.Scene;
import org.game.knight.version.packer.world.model.SceneBackLayer;
import org.game.knight.version.packer.world.model.SceneForeLayer;
import org.game.knight.version.packer.world.task.RootTask;

public class ImageFrameTable
{
	private RootTask root;

	private Frame[][] frameTable;
	private int index;
	
	private HashMap<ProjectImgFile, ImageFrame[]> clipTable;

	/**
	 * 构造函数
	 * @param root
	 */
	public ImageFrameTable(RootTask root)
	{
		this.root = root;
	}

	/**
	 * 开始
	 */
	public void start()
	{
		frameTable=findAllFrame(root);
		clipTable=new HashMap<ProjectImgFile, ImageFrame[]>();
		
		ExecutorService exec=Executors.newFixedThreadPool(5);
		for(int i=0;i<5;i++)
		{
			exec.execute(new Runnable()
			{
				@Override
				public void run()
				{
					while(true)
					{
						Frame[] frameList=getNextFrames();
						if(frameList==null || isCancel())
						{
							break;
						}
						
						finishFrames(clipFrameSet(frameList));
					}
				}
			});
		}
		
		while(!root.isCancel() && !isFinished())
		{
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				break;
			}
		}
	}
	
	/**
	 * 是否已经取消
	 * @return
	 */
	private synchronized boolean isCancel()
	{
		return false;
	}
	
	/**
	 * 是否已经完成
	 * @return
	 */
	private synchronized boolean isFinished()
	{
		return clipTable.size()==frameTable.length;
	}
	
	/**
	 * 获取下一组帧
	 * @return
	 */
	private synchronized Frame[] getNextFrames()
	{
		Frame[] result=null;
		if(index<frameTable.length)
		{
			result=frameTable[index];
			index++;
		}
		return result;
	}
	
	/**
	 * 完成一组帧
	 * @param frames
	 */
	private synchronized void finishFrames(ImageFrame[] frames)
	{
		clipTable.put(frames[0].file, frames);
	}

	/**
	 * 查找所有图像帧
	 */
	private static Frame[][] findAllFrame(RootTask root)
	{
		HashMap<String, HashMap<String, Frame>> frameMap = new HashMap<String, HashMap<String, Frame>>();

		for (Scene scene : root.getWorldTable().getAllScene())
		{
			for (SceneBackLayer layer : scene.backLayers)
			{
				String fileID=layer.img.imgFile.gid+"_1_1";
				if(!frameMap.containsKey(fileID))
				{
					frameMap.put(fileID, new HashMap<String, Frame>());
				}
				String id = layer.img.imgFile.gid + "_1_1_0";
				if (!frameMap.get(fileID).containsKey(id))
				{
					frameMap.get(fileID).put(id, new Frame(layer.img.imgFile, 1, 1, 0));
				}
			}
			for (SceneForeLayer layer : scene.foreLayers)
			{
				String fileID=layer.img.imgFile.gid+"_1_1";
				if(!frameMap.containsKey(fileID))
				{
					frameMap.put(fileID, new HashMap<String, Frame>());
				}
				String id = layer.img.imgFile.gid + "_1_1_0";
				if (!frameMap.get(fileID).containsKey(id))
				{
					frameMap.get(fileID).put(id, new Frame(layer.img.imgFile, 1, 1, 0));
				}
			}
		}

		for (Attire attire : root.getAttireTable().getAllAttire())
		{
			for (AttireAction action : attire.actions)
			{
				for (AttireAnim anim : action.anims)
				{
					for (int i = 0; i < anim.times.length; i++)
					{
						if (anim.times[i] > 0)
						{
							String fileID=anim.img.gid + "_" + anim.row + "_" + anim.col;
							if(!frameMap.containsKey(fileID))
							{
								frameMap.put(fileID, new HashMap<String, Frame>());
							}
							
							String id = anim.img.gid + "_" + anim.row + "_" + anim.col + "_" + i;
							if (!frameMap.get(fileID).containsKey(id))
							{
								frameMap.get(fileID).put(id, new Frame(anim.img, anim.row, anim.col, i));
							}
						}
					}
				}
			}
		}
		
		ArrayList<Frame[]> frameSet=new ArrayList<Frame[]>();
		for(HashMap<String,Frame> map:frameMap.values())
		{
			frameSet.add(map.values().toArray(new Frame[map.size()]));
		}
		
		return frameSet.toArray(new Frame[frameSet.size()][]);
	}
	

	/**
	 * 裁切图像帧列表
	 * 
	 * @param file
	 * @return
	 */
	private static ImageFrame[] clipFrameSet(Frame[] frames)
	{
		ArrayList<ImageFrame> result=new ArrayList<ImageFrame>();
		try
		{
			Frame frame=frames[0];
			ProjectImgFile file=frame.file;
			
			BufferedImage image = ImageIO.read(file);
			
			int frameW=file.width/frame.col;
			int frameH=file.height/frame.row;
			for(int i=0;i<frames.length;i++)
			{
				frame=frames[i];
				int row=frame.index/frame.col;
				int col=frame.index%frame.col;
				
				int frameX=col*frameW;
				int frameY=row*frameH;
				Rectangle rect=clipFrame(image,frameX,frameY,frameW,frameH);
				
				result.add(new ImageFrame(frame.file, frame.row, frame.col, frame.index, frameX, frameY, frameW, frameH, rect.x, rect.y, rect.width, rect.height));
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return result.toArray(new ImageFrame[result.size()]);
	}

	/**
	 * 裁切图像帧
	 * @param image
	 * @param frameX
	 * @param frameY
	 * @param frameW
	 * @param frameH
	 * @return
	 */
	private static Rectangle clipFrame(BufferedImage image, int frameX, int frameY, int frameW, int frameH)
	{
		int[] pixels = new int[frameW * frameH];
		pixels = image.getRGB(frameX, frameY, frameW, frameH, pixels, 0, frameW);

		int l = -1;
		int t = -1;
		int r = -1;
		int b = -1;

		int maxX = frameW;
		for (int i = 0; i < frameH; i++)
		{
			for (int j = 0; j < maxX; j++)
			{
				int index = i * frameW + j;

				byte alpha = (byte) (pixels[index] >> 24 & 0xFF);
				if (alpha != 0)
				{
					if (t == -1)
					{
						t = i;
					}

					l = j;
					maxX = j;
					break;
				}
			}

			if (l == 0)
			{
				break;
			}
		}

		if (l == -1 || t == -1)
		{
			return new Rectangle(0, 0, 1, 1);
		}

		int minX = 0;
		for (int i = frameH - 1; i >= 0; i--)
		{
			for (int j = frameW - 1; j >= minX; j--)
			{
				int index = i * frameW + j;

				byte alpha = (byte) (pixels[index] >> 24 & 0xFF);
				if (alpha != 0)
				{
					if (b == -1)
					{
						b = i;
					}

					r = j;
					minX = j;

					break;
				}
			}

			if (r == 0)
			{
				break;
			}
		}
		
		if(r==-1 || b==-1)
		{
			throw new Error("反向遍历后得出的结果不一致！");
		}
		
		return new Rectangle(l, t, r-l+1, b-t+1);
	}
	
	
	/**
	 * 帧信息类型
	 * @author ds
	 *
	 */
	private static class Frame
	{
		public final ProjectImgFile file;
		public final int row;
		public final int col;
		public final int index;

		public Frame(ProjectImgFile file, int row, int col, int index)
		{
			this.file = file;
			this.row = row;
			this.col = col;
			this.index = index;
		}
	}
}
