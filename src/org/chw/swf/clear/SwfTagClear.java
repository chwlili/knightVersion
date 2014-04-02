package org.chw.swf.clear;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.chw.util.FileUtil;
import org.chw.util.ZlibUtil;


public class SwfTagClear
{

	/**
	 * 清理SWF文件
	 * @param stream
	 * @return
	 */
	public static void clearSwfFile(File file)
	{
		clearSwfFile(file,new int[]{71,77});
	}
	
	/**
	 * 清理SWF文件
	 * @param stream
	 * @return
	 */
	public static void clearSwfFile(File file,int[] tagIDs)
	{
		try
		{
			FileInputStream stream = new FileInputStream(file);
			byte[] bytes=clearSwfStream(stream,tagIDs);
			FileUtil.writeFile(file, bytes);
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 清理SWF字节流
	 * @param stream
	 * @return
	 */
	public static byte[] clearSwfStream(InputStream stream,int[] tagIDs)
	{
		try
		{
			int streamLength=stream.available();
			
			String flag=String.valueOf((char)(stream.read()))+String.valueOf((char)(stream.read()))+String.valueOf((char)(stream.read()));
			int ver=stream.read(); //ver
			
			stream.skip(4);
			
			byte[] bytes=null;//new byte[(int)(file.length())];
			
			if(flag.equals("CWS"))
			{
				byte[] cws=new byte[(int)(streamLength-8)];
				stream.read(cws);
				bytes=ZlibUtil.decompress(cws);
			}
			else
			{
				bytes=new byte[(int)(streamLength-8)];
				stream.read(bytes);
			}
			stream.close();

			ByteArrayInputStream byteInput=new ByteArrayInputStream(bytes);
			ByteArrayOutputStream copyOutput=new ByteArrayOutputStream();
			
			byteInput.mark(1024);
			
			int w=byteInput.read()>>>3;
			int len=(int)((w*4+5)/8);
			if((w*4+5)%8>0)
			{
				len+=1;
			}
			
			int rate=((byteInput.read()<<8) | byteInput.read());
			int frames=(byteInput.read() | (byteInput.read()<<8));
			len+=4;
			
			//System.out.println("帧频:"+rate);
			//System.out.println("帧数:"+frames);
			
			byteInput.reset();
			
			byte[] temp=new byte[len];
			byteInput.read(temp);
			copyOutput.write(temp);
			
			while(byteInput.available()>0)
			{
				byteInput.mark(1024000);
				
				int tag=byteInput.read()+(byteInput.read()<<8);
				int tagID=tag>>>6;
				int tagLen=tag&0x3f;
				int preLen=2;
				
				if(tagLen==0x3f)
				{
					tagLen=byteInput.read()+(byteInput.read()<<8)+(byteInput.read()<<16)+(byteInput.read()<<24);
					preLen+=4;
				}
				
				//System.out.println("标记:"+tagID+"("+tagLen+")");
				
				byteInput.reset();
				
				boolean ignore=false;
				for(int i=0;i<tagIDs.length;i++)
				{
					if(tagID==tagIDs[i])
					{
						ignore=true;
						break;
					}
				}
				
				if(ignore)
				{
					byteInput.skip(preLen+tagLen);
				}
				else
				{
					byte[] tagContent=new byte[preLen+tagLen];
					byteInput.read(tagContent);
					copyOutput.write(tagContent);
				}
			}
			
			byte[] copy=copyOutput.toByteArray();
			
			int fileLength=copy.length+8;
			
			//
			ByteArrayOutputStream output=new ByteArrayOutputStream();
			
			// flag
			output.write(0x43);// C
			//output.write(0x46);// C | F
			output.write(0x57);// W
			output.write(0x53);// S

			// version
			output.write(ver);// ver.10
			
			//length
			output.write(fileLength & 0xff);
			output.write((fileLength >> 8) & 0xff);
			output.write((fileLength >> 16) & 0xff);
			output.write((fileLength >> 24) & 0xff);
			
			//content
			copy=ZlibUtil.compress(copy);
			output.write(copy);
			
			//writer
			return output.toByteArray();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
}
