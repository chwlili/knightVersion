package org.chw.swf.writer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.chw.util.ZlibUtil;


public class SwfWriter
{
	// head
	private int version = 10;
	private int stageWidth = 500;
	private int stageHeight = 400;
	private int frameRate = 30;

	// file attribute
	private boolean useDirectBlit = true;
	private boolean useGPU = true;
	private boolean useActionScript3 = true;
	private boolean useNetwork = true;

	// background
	private int background = 0xffffff;

	// bitmaps
	private ArrayList<SwfBitmap> bitmaps = new ArrayList<SwfBitmap>();
	
	// mp3s
	private ArrayList<SwfMp3> mp3s=new ArrayList<SwfMp3>();
	
	//bytes
	private ArrayList<SwfXML> xmls=new ArrayList<SwfXML>();

	// doabc
	private SwfDoAbc abc = new SwfDoAbc();
	
	//doabcs
	private ArrayList<SwfDoAbc> abcList=new ArrayList<SwfDoAbc>();
	private int abcTypeCount=0;

	/**
	 * 获取版本
	 * 
	 * @return
	 */
	public int getVersion()
	{
		return version;
	}

	/**
	 * 设置版本
	 * 
	 * @param version
	 */
	public void setVersion(int version)
	{
		this.version = version;
	}

	/**
	 * 获取舞台宽度
	 * 
	 * @return
	 */
	public int getStageWidth()
	{
		return stageWidth;
	}

	/**
	 * 设置舞台宽度
	 * 
	 * @param stageWidth
	 */
	public void setStageWidth(int stageWidth)
	{
		this.stageWidth = stageWidth;
	}

	/**
	 * 获取舞台高度
	 * 
	 * @return
	 */
	public int getStageHeight()
	{
		return stageHeight;
	}

	/**
	 * 设置舞台高度
	 * 
	 * @param stageHeight
	 */
	public void setStageHeight(int stageHeight)
	{
		this.stageHeight = stageHeight;
	}

	/**
	 * 获取帧频
	 * 
	 * @return
	 */
	public int getFrameRate()
	{
		return frameRate;
	}

	/**
	 * 设置帧频
	 * 
	 * @return
	 */
	public void setFrameRate(int frameRate)
	{
		this.frameRate = frameRate;
	}

	/**
	 * 是否使用硬件加速
	 * 
	 * @return
	 */
	public boolean isUseDirectBlit()
	{
		return useDirectBlit;
	}

	/**
	 * 设置是否使用硬件加速
	 * 
	 * @param useDirectBlit
	 */
	public void setUseDirectBlit(boolean useDirectBlit)
	{
		this.useDirectBlit = useDirectBlit;
	}

	/**
	 * 是否使用GPU加速
	 * 
	 * @return
	 */
	public boolean isUseGPU()
	{
		return useGPU;
	}

	/**
	 * 设置是否使用GPU加速
	 * 
	 * @param useGPU
	 */
	public void setUseGPU(boolean useGPU)
	{
		this.useGPU = useGPU;
	}

	/**
	 * 是否使用AS3
	 * 
	 * @return
	 */
	public boolean isUseActionScript3()
	{
		return useActionScript3;
	}

	/**
	 * 设置是否使用AS3
	 * 
	 * @param useActionScript3
	 */
	public void setUseActionScript3(boolean useActionScript3)
	{
		this.useActionScript3 = useActionScript3;
	}

	/**
	 * 是否使用网络
	 * 
	 * @return
	 */
	public boolean isUseNetwork()
	{
		return useNetwork;
	}

	/**
	 * 设置是否使用网格
	 * 
	 * @param useNetwork
	 */
	public void setUseNetwork(boolean useNetwork)
	{
		this.useNetwork = useNetwork;
	}

	/**
	 * 获取背景颜色
	 * 
	 * @return
	 */
	public int getBackground()
	{
		return background;
	}

	/**
	 * 设置背景颜色
	 * 
	 * @param background
	 */
	public void setBackground(int background)
	{
		this.background = background;
	}

	/**
	 * 添加位图
	 * 
	 * @param file
	 * @param quality
	 */
	public void addBitmap(SwfBitmap bitmap)
	{
		bitmaps.add(bitmap);
		
		if(abcTypeCount==0 || abcTypeCount>30)
		{
			abcList.add(new SwfDoAbc());
		}
		
		SwfDoAbc last=abcList.get(abcList.size()-1);
		last.addBitmap(bitmap);
		abcTypeCount++;
	}
	
	/**
	 * 添加音效
	 * @param mp3s
	 */
	public void addMp3(SwfMp3 mp3)
	{
		mp3s.add(mp3);

		if(abcTypeCount==0 || abcTypeCount>30)
		{
			abcList.add(new SwfDoAbc());
		}
		
		SwfDoAbc last=abcList.get(abcList.size()-1);
		last.addMp3(mp3);
		abcTypeCount++;
	}
	
	/**
	 * 添加XML
	 * @param xml
	 */
	public void addXml(SwfXML xml)
	{
		xmls.add(xml);

		if(abcTypeCount==0 || abcTypeCount>30)
		{
			abcList.add(new SwfDoAbc());
		}
		
		SwfDoAbc last=abcList.get(abcList.size()-1);
		last.addXml(xml);
		abcTypeCount++;
	}

	/**
	 * 转换为字节数组
	 * 
	 * @return
	 * @throws IOException
	 */
	public byte[] toBytes(boolean compress) throws IOException
	{
		ByteArrayOutputStream body = new ByteArrayOutputStream();

		writeHead(body);
		writeFileAttribute(body);
		writeBackgroud(body);
		writerDefineSceneTag(body);
		writeBitmapData(body);
		writeMp3Data(body);
		writeXmlData(body);
		writeDoAbc(body);
		writeSymbolClass(body);
		writeShowFrameTag(body);
		writeEndTag(body);

		byte[] bytes = body.toByteArray();

		int length = bytes.length + 8;

		if (compress)
		{
			bytes = ZlibUtil.compress(bytes);
		}

		ByteArrayOutputStream result = new ByteArrayOutputStream();

		// flag
		result.write(compress ? 0x43 : 0x46);// C | F
		result.write(0x57);// W
		result.write(0x53);// S

		// version
		result.write(version);// ver.10

		// length
		result.write(length & 0xff);
		result.write((length >> 8) & 0xff);
		result.write((length >> 16) & 0xff);
		result.write((length >> 24) & 0xff);

		try
		{
			result.write(bytes);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return result.toByteArray();
	}

	/**
	 * 文件头
	 * 
	 * @param writer
	 */
	private void writeHead(ByteArrayOutputStream writer)
	{
		// 写入舞台大小
		int xMin = 0;
		int xMax = stageWidth * 20;
		int yMin = 0;
		int yMax = stageHeight * 20;

		String xMinBit = Integer.toBinaryString(xMin);
		String xMaxBit = Integer.toBinaryString(xMax);
		String yMinBit = Integer.toBinaryString(yMin);
		String yMaxBit = Integer.toBinaryString(yMax);

		int bitCount = Math.max(Math.max(xMinBit.length(), xMaxBit.length()), Math.max(yMinBit.length(), yMaxBit.length())) + 1;

		String headBit = Integer.toBinaryString(bitCount);

		//
		int i = 0;
		StringBuilder sb = new StringBuilder();

		// head
		i = 0;
		while (headBit.length() + i < 5)
		{
			sb.append("0");
			i++;
		}
		sb.append(headBit);

		// xMin
		i = 0;
		while (xMinBit.length() + i < bitCount)
		{
			sb.append("0");
			i++;
		}
		sb.append(xMinBit);

		// xMax
		i = 0;
		while (xMaxBit.length() + i < bitCount)
		{
			sb.append("0");
			i++;
		}
		sb.append(xMaxBit);

		// yMin
		i = 0;
		while (yMinBit.length() + i < bitCount)
		{
			sb.append("0");
			i++;
		}
		sb.append(yMinBit);

		// yMax
		i = 0;
		while (yMaxBit.length() + i < bitCount)
		{
			sb.append("0");
			i++;
		}
		sb.append(yMaxBit);

		// 补0
		while (sb.length() % 8 != 0)
		{
			sb.append("0");
		}

		String bits = sb.toString();

		int byteCount = bits.length() / 8;
		for (int j = 0; j < byteCount; j++)
		{
			writer.write(Integer.parseInt(bits.substring(j * 8, j * 8 + 8), 2));
		}

		// frameRate
		writer.write(00);
		writer.write(frameRate);

		// frameCount
		writer.write(0x01);
		writer.write(0x00);
	}

	/**
	 * 文件属性
	 * 
	 * @param writer
	 */
	private void writeFileAttribute(ByteArrayOutputStream writer)
	{
		writer.write(0x44);
		writer.write(0x11);

		int flag = 0;
		if (useDirectBlit)
		{
			flag |= 0x40;
		}
		if (useGPU)
		{
			flag |= 0x20;
		}
		if (useActionScript3)
		{
			flag |= 0x08;
		}
		if (useNetwork)
		{
			flag |= 0x01;
		}

		writer.write(flag);
		writer.write(0x00);
		writer.write(0x00);
		writer.write(0x00);
	}

	/**
	 * 背景
	 * 
	 * @param writer
	 */
	private void writeBackgroud(ByteArrayOutputStream writer)
	{
		writer.write(0x43);
		writer.write(0x02);

		writer.write(((background >> 16) & 0xFF));
		writer.write(((background >> 8) & 0xFF));
		writer.write(background & 0xFF);
	}

	/**
	 * 场景定义
	 * 
	 * @param writer
	 */
	private void writerDefineSceneTag(ByteArrayOutputStream writer)
	{
		writer.write(0xBF);
		writer.write(0x15);
		writer.write(0x0C);
		writer.write(0x00);
		writer.write(0x00);
		writer.write(0x00);
		writer.write(0x01);
		writer.write(0x00);
		writer.write(0xE5);
		writer.write(0x9C);
		writer.write(0xBA);
		writer.write(0xE6);
		writer.write(0x99);
		writer.write(0xAF);
		writer.write(0x20);
		writer.write(0x31);
		writer.write(0x00);
		writer.write(0x00);
	}

	/**
	 * 写入脚本
	 * @param writer
	 * @throws IOException
	 */
	private void writeDoAbc(ByteArrayOutputStream writer) throws IOException
	{
		for(SwfDoAbc abc:abcList)
		{
			writer.write(abc.toBytes());
		}
	}
	
	/**
	 * 写入图像
	 * 
	 * @param writer
	 */
	private void writeBitmapData(ByteArrayOutputStream writer)
	{
		for (int i = 0; i < bitmaps.size(); i++)
		{
			SwfBitmap bitmap = bitmaps.get(i);
			int characterID=i+1;
			
			byte[] rgb = bitmap.getRGB();
			byte[] alphas = bitmap.getAlpha();

			if (rgb != null)
			{
				try
				{
					int length = rgb.length + 2;

					if (alphas != null)
					{
						length += 4;
						length += alphas.length;

						writer.write(0xFF);
						writer.write(0x08);
						writer.write(length & 0xFF);
						writer.write((length >> 8) & 0xFF);
						writer.write((length >> 16) & 0xFF);
						writer.write((length >> 24) & 0xFF);
						writer.write(characterID & 0xFF);
						writer.write((characterID >> 8) & 0xFF);

						length = rgb.length;
						writer.write(length & 0xFF);
						writer.write((length >> 8) & 0xFF);
						writer.write((length >> 16) & 0xFF);
						writer.write((length >> 24) & 0xFF);

						writer.write(rgb);
						writer.write(alphas);
					}
					else
					{
						writer.write(0x7F);
						writer.write(0x05);
						writer.write(length & 0xFF);
						writer.write((length >> 8) & 0xFF);
						writer.write((length >> 16) & 0xFF);
						writer.write((length >> 24) & 0xFF);
						writer.write(characterID & 0xFF);
						writer.write((characterID >> 8) & 0xFF);
						writer.write(rgb);
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 写入Mp3数据
	 * @param write
	 */
	private void writeMp3Data(ByteArrayOutputStream writer)
	{
		for(int i=0;i<mp3s.size();i++)
		{
			SwfMp3 mp3=mp3s.get(i);
			
			int characterID=bitmaps.size()+i+1;

			try
			{
				int length=mp3.getBytes().length+9;
	
				writer.write(0xBF);
				writer.write(0x03);
				writer.write(length & 0xFF);
				writer.write((length >> 8) & 0xFF);
				writer.write((length >> 16) & 0xFF);
				writer.write((length >> 24) & 0xFF);
				writer.write(characterID & 0xFF);
				writer.write((characterID >> 8) & 0xFF);
				writer.write(0x2E);// soundFormat , rate , sample ,mono ,
				writer.write(0x00);
				writer.write(0x99);
				writer.write(0x00);
				writer.write(0x00);//
				writer.write(0x00);
				writer.write(0x00);// sample skip;
				writer.write(mp3.getBytes());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 写入XML数据
	 * @param write
	 */
	private void writeXmlData(ByteArrayOutputStream writer)
	{
		for(int i=0;i<xmls.size();i++)
		{
			SwfXML xml=xmls.get(i);
			
			int characterID=bitmaps.size()+mp3s.size()+i+1;

			try
			{
				int length=xml.getBytes().length+6;
	
				writer.write(0xFF);
				writer.write(0x15);
				writer.write(length & 0xFF);
				writer.write((length >> 8) & 0xFF);
				writer.write((length >> 16) & 0xFF);
				writer.write((length >> 24) & 0xFF);
				writer.write(characterID & 0xFF);
				writer.write((characterID >> 8) & 0xFF);
				writer.write(0x00);
				writer.write(0x00);
				writer.write(0x00);
				writer.write(0x00);
				writer.write(xml.getBytes());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * 写入图像类型文件
	 * 
	 * @param writer
	 * @param bitmap
	 * @param characterID
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	private void writeSymbolClass(ByteArrayOutputStream writer) throws UnsupportedEncodingException, IOException
	{
		writer.write(0x3F);
		writer.write(0x13);

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		int count=bitmaps.size()+mp3s.size()+xmls.size();
		buffer.write(count & 0xFF);
		buffer.write((count >> 8) & 0xFF);
		
		int characterID=1;
		
		for (int i = 0; i < bitmaps.size(); i++)
		{
			SwfBitmap bitmap = bitmaps.get(i);
			
			// class name
			String packName = bitmap.getPackName();
			String className = bitmap.getClassName();
			if (className != null && className.isEmpty() == false)
			{
				if (packName != null && packName.isEmpty() == false)
				{
					className = packName + "." + className;
				}

				// characterID
				buffer.write(characterID & 0xFF);
				buffer.write((characterID >> 8) & 0xFF);

				// className
				buffer.write(className.toString().getBytes("utf8"));
				buffer.write(0x00);
			}
			
			characterID++;
		}
		for(int j=0;j<mp3s.size();j++)
		{
			SwfMp3 mp3=mp3s.get(j);
			
			//class name
			String packName=mp3.getPackName();
			String className=mp3.getClassName();
			if(className!=null && className.isEmpty()==false)
			{
				if(packName!=null && packName.isEmpty()==false)
				{
					className=packName+"."+className;
					
					// characterID
					buffer.write(characterID & 0xFF);
					buffer.write((characterID >> 8) & 0xFF);

					// className
					buffer.write(className.toString().getBytes("utf8"));
					buffer.write(0x00);
				}
			}
			
			characterID++;
		}
		for(int j=0;j<xmls.size();j++)
		{
			SwfXML xml=xmls.get(j);
			
			//class name
			String packName=xml.getPackName();
			String className=xml.getClassName();
			if(className!=null && className.isEmpty()==false)
			{
				if(packName!=null && packName.isEmpty()==false)
				{
					className=packName+"."+className;
					
					// characterID
					buffer.write(characterID & 0xFF);
					buffer.write((characterID >> 8) & 0xFF);

					// className
					buffer.write(className.toString().getBytes("utf8"));
					buffer.write(0x00);
				}
			}
			
			characterID++;
		}

		byte[] bytes = buffer.toByteArray();

		int length = bytes.length;

		writer.write(length & 0xFF);
		writer.write((length >> 8) & 0xFF);
		writer.write((length >> 16) & 0xFF);
		writer.write((length >> 24) & 0xFF);

		writer.write(bytes);
	}

	/**
	 * 显示帧
	 * 
	 * @param writer
	 */
	private void writeShowFrameTag(ByteArrayOutputStream writer)
	{
		writer.write(0x40);
		writer.write(0x00);
	}

	/**
	 * 结束
	 * 
	 * @param writer
	 */
	private void writeEndTag(ByteArrayOutputStream writer)
	{
		writer.write(0x00);
		writer.write(0x00);
	}
}
