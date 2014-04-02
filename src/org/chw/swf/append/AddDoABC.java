package org.chw.swf.append;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.chw.util.FileUtil;
import org.chw.util.ZlibUtil;


public class AddDoABC
{
	public static void append(File file,int width,int height,String packName,String className)
	{
		try
		{
			FileInputStream stream=new FileInputStream(file);
			
			String flag=String.valueOf((char)(stream.read()))+String.valueOf((char)(stream.read()))+String.valueOf((char)(stream.read()));
			int ver=stream.read(); //ver
			
			stream.skip(4);
			
			byte[] bytes=null;//new byte[(int)(file.length())];
			
			if(flag.equals("CWS"))
			{
				byte[] cws=new byte[(int)(file.length()-8)];
				stream.read(cws);
				bytes=ZlibUtil.decompress(cws);
			}
			else
			{
				bytes=new byte[(int)(file.length()-8)];
				stream.read(bytes);
			}
			stream.close();

			ByteArrayInputStream byteInput=new ByteArrayInputStream(bytes);
			
			byteInput.mark(1000);
			
			int w=byteInput.read()>>>3;
			int len=(int)((w*4+5)/8);
			if((w*4+5)%8>0)
			{
				len+=1;
			}
			
			byteInput.reset();
			byteInput.skip(len);
			
			System.out.println("Ö¡Æµ:"+((byteInput.read()<<8) | byteInput.read()));
			System.out.println("Ö¡Êý:"+(byteInput.read() | (byteInput.read()<<8)));
			
			int characterID=0;
			int insertPos=0;
			
			//System.out.println(byteInput.read()+(byteInput.read()<<8));
			while(byteInput.available()>0)
			{
				int tag=byteInput.read()+(byteInput.read()<<8);
				int tagID=tag>>>6;
				int tagLen=tag&0x3f;
				
				if(tagLen==0x3f)
				{
					tagLen=byteInput.read()+(byteInput.read()<<8)+(byteInput.read()<<16)+(byteInput.read()<<24);
				}
				
				System.out.println("±ê¼Ç:"+tagID+"("+tagLen+")");
				
				byteInput.mark(1000);
				
				if(tagID==21 || tagID==35 || tagID==6 || tagID==20 || tagID==36 || tagID==90)
				{
					characterID=byteInput.read()|(byteInput.read()<<8);
					byteInput.reset();
					byteInput.skip(tagLen);
					insertPos=bytes.length-byteInput.available();
					break;
				}
				else
				{
					byteInput.skip(tagLen);
				}
			}
			
			if(characterID!=0)
			{
				byte[] prev=new byte[insertPos];
				byte[] last=new byte[byteInput.available()];
				ByteArrayInputStream convert=new ByteArrayInputStream(bytes);
				convert.read(prev);
				convert.read(last);
				
				ByteArrayOutputStream zlibOutput=new ByteArrayOutputStream();
				zlibOutput.write(prev);
				zlibOutput.write(createDoAbc(width,height,packName,className));
				zlibOutput.write(createSymbol(characterID,packName,className));
				zlibOutput.write(last);
				
				byte[] zlibBytes=ZlibUtil.compress(zlibOutput.toByteArray());
				
				int fileLength=zlibOutput.size()+8;//fileContent.length;
				
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
				output.write(zlibBytes);
				
				//writer
				FileUtil.writeFile(file, output.toByteArray());
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
	}
	
	private static byte[] createDoAbc(int width,int height,String packName,String className)
	{
		SwfDoAbc abc=new SwfDoAbc();
		abc.addBitmap(new BitmapTag(width,height,packName,className));
		try
		{
			return abc.toBytes();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return new byte[0];
	}
	
	private static byte[] createSymbol(int id,String packName,String className) throws UnsupportedEncodingException, IOException
	{
		ByteArrayOutputStream writer=new ByteArrayOutputStream();
		
		writer.write(0x3F);
		writer.write(0x13);

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		buffer.write(0x01);
		buffer.write(0x00);
		
		// class name
		if (className != null && className.isEmpty() == false)
		{
			if (packName != null && packName.isEmpty() == false)
			{
				className = packName + "." + className;
			}

			// characterID
			buffer.write(id & 0xFF);
			buffer.write((id >> 8) & 0xFF);

			// className
			buffer.write(className.toString().getBytes("utf8"));
			buffer.write(0x00);
		}

		byte[] bytes = buffer.toByteArray();

		int length = bytes.length;

		writer.write(length & 0xFF);
		writer.write((length >> 8) & 0xFF);
		writer.write((length >> 16) & 0xFF);
		writer.write((length >> 24) & 0xFF);

		writer.write(bytes);
		return writer.toByteArray();
	}
}
