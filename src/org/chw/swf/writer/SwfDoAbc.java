package org.chw.swf.writer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public class SwfDoAbc
{
	static int Namespace = 0x08;
	static int PackageNamespace = 0x16;
	static int PackageInternalNs = 0x17;
	static int ProtectedNamespace = 0x18;
	static int ExplicitNamespace = 0x19;
	static int StaticProtectedNs = 0x1A;
	static int PrivateNs = 0x05;
	
	private Hashtable<Integer, Integer> intHash=new Hashtable<Integer, Integer>();
	private ArrayList<Integer> intList=new ArrayList<Integer>();

	private Hashtable<Integer, Integer> uintHash=new Hashtable<Integer, Integer>();
	private ArrayList<Integer> uintList=new ArrayList<Integer>();

	private Hashtable<Double, Integer> doubleHash=new Hashtable<Double, Integer>();
	private ArrayList<Double> doubleList=new ArrayList<Double>();

	private Hashtable<String, Integer> stringHash=new Hashtable<String, Integer>();
	private ArrayList<String> stringList=new ArrayList<String>();

	private Hashtable<String, Integer> namespaceHash=new Hashtable<String, Integer>();
	private ArrayList<String> namespaceList=new ArrayList<String>();

	private Hashtable<String, Integer> qnameHash=new Hashtable<String, Integer>();
	private ArrayList<String> qnameList=new ArrayList<String>();
	
	/**
	 * 获取int索引
	 * @param val
	 * @return
	 */
	public int getIntIndex(int val)
	{
		if(!intHash.containsKey(val))
		{
			intList.add(val);
			intHash.put(val, intList.size());
		}
		
		return intHash.get(val);
	}

	/**
	 * 获取uint索引
	 * @param val
	 * @return
	 */
	public int getUintIndex(int val)
	{
		if(!uintHash.containsKey(val))
		{
			uintList.add(val);
			uintHash.put(val, uintList.size());
		}
		
		return uintHash.get(val);
	}

	/**
	 * 获取double索引
	 * @param val
	 * @return
	 */
	public int getDoubleIndex(double val)
	{
		if(!doubleHash.containsKey(val))
		{
			doubleList.add(val);
			doubleHash.put(val, doubleList.size());
		}
		
		return doubleHash.get(val);
	}

	/**
	 * 获取string索引
	 * @param val
	 * @return
	 */
	public int getStringIndex(String val)
	{
		if(!stringHash.containsKey(val))
		{
			stringList.add(val);
			stringHash.put(val, stringList.size());
		}
		
		return stringHash.get(val);
	}
	
	/**
	 * 获取名称空间索引
	 * @param kind
	 * @param name
	 * @return
	 */
	public int getNamespaceIndex(int kind,String packName)
	{
		String val="<"+kind+">"+getStringIndex(packName);
		
		if(!namespaceHash.containsKey(val))
		{
			namespaceList.add(val);
			namespaceHash.put(val, namespaceList.size());
		}
		
		return namespaceHash.get(val);
	}
	
	/**
	 * 获取QName索引
	 * @param kind
	 * @param packName
	 * @param name
	 * @return
	 */
	public int getQnameIndex(int kind,String packName,String name)
	{
		String val=getNamespaceIndex(kind,packName)+"::"+getStringIndex(name);
		
		if(!qnameHash.containsKey(val))
		{
			qnameList.add(val);
			qnameHash.put(val, qnameList.size());
		}
		
		return qnameHash.get(val);
	}
	
	private ArrayList<SwfBitmap> bitmaps=new ArrayList<SwfBitmap>();
	private ArrayList<SwfMp3> mp3s=new ArrayList<SwfMp3>();
	private ArrayList<SwfXML> xmls=new ArrayList<SwfXML>();
	
	/**
	 * 添加位图
	 */
	public void addBitmap(SwfBitmap bitmap)
	{
		bitmaps.add(bitmap);
	}
	
	/**
	 * 添加音效
	 * @param mp3s
	 */
	public void addMp3(SwfMp3 mp3)
	{
		mp3s.add(mp3);
	}
	
	/**
	 * 添加XML
	 * @param xml
	 */
	public void addXml(SwfXML xml)
	{
		xmls.add(xml);
	}
	
	
	/**
	 * 转换成字节
	 * @return
	 * @throws IOException
	 */
	public byte[] toBytes() throws IOException
	{
		
		ByteArrayOutputStream byteStream=new ByteArrayOutputStream();
		if(bitmaps.size()>0)
		{
			byteStream.write(getBitmapABC());
		}
		if(mp3s.size()>0)
		{
			byteStream.write(getMp3ABC());
		}
		if(xmls.size()>0)
		{
			byteStream.write(getXmlABC());
		}
		
		if(byteStream.size()>0)
		{
			return byteStream.toByteArray();
		}
		else
		{
			return new byte[0];
		}
	}

	private byte[] getBitmapABC_(ArrayList<SwfBitmap> bitmaps) throws IOException
	{
		SwfDoAbcWriter abcOutput=new SwfDoAbcWriter(new ByteArrayOutputStream());
		
		//写入方法声明
		abcOutput.writeU30(bitmaps.size()*3);
		for(int i=0;i<bitmaps.size();i++)
		{
			SwfBitmap bitmap=bitmaps.get(i);
			
			//实例构造函数
			abcOutput.writeU30(2);//参数个数
			abcOutput.writeU30(0);//返回类型
			abcOutput.writeU30(getQnameIndex(SwfDoAbc.PackageNamespace, "", "int"));//参数1类型
			abcOutput.writeU30(getQnameIndex(SwfDoAbc.PackageNamespace, "", "int"));//参数2类型
			abcOutput.writeU30(0);//方法名称
			abcOutput.write(0x08);//flag 带默认值
			abcOutput.writeU30(0x02);//默认值数量
			abcOutput.writeU30(getIntIndex(bitmap.getWidth()));//int index
			abcOutput.writeByte(0x03);//int pool
			abcOutput.writeU30(getIntIndex(bitmap.getHeight()));//int index
			abcOutput.writeByte(0x03);//int pool

			//静态构造函数
			abcOutput.writeU30(0);//参数个数
			abcOutput.writeU30(0);//返回类型
			abcOutput.writeU30(0);//方法名称
			abcOutput.writeByte(0);//flag

			//脚本构造函数
			abcOutput.writeU30(0);//参数个数
			abcOutput.writeU30(0);//返回类型
			abcOutput.writeU30(0);//方法名称
			abcOutput.writeByte(0);//flag
		}
		
		//写入元数据
		abcOutput.writeByte(0);
		
		//写入类数量
		abcOutput.writeU30(bitmaps.size());
		
		//写入实例信息
		for(int i=0;i<bitmaps.size();i++)
		{
			SwfBitmap bitmap=bitmaps.get(i);
			
			//class name
			abcOutput.writeU30(getQnameIndex(SwfDoAbc.PackageNamespace, bitmap.getPackName(), bitmap.getClassName()));
			//super class name
			abcOutput.writeU30(getQnameIndex(SwfDoAbc.PackageNamespace,"flash.display","BitmapData"));
			//flag
			abcOutput.writeByte(0x01|0x08);
			//protectedNs
			abcOutput.writeU30(getNamespaceIndex(SwfDoAbc.ProtectedNamespace, bitmap.getClassName()));
			//interface count
			abcOutput.writeU30(0x00);
			//init method
			abcOutput.writeU30(i*3+0);
			//trait count
			abcOutput.writeU30(0x00);
		}
		
		//写入类型信息
		for(int i=0;i<bitmaps.size();i++)
		{
			//init method
			abcOutput.writeU30(i*3+1);
			//trait count
			abcOutput.writeU30(0x00);
		}
		
		//写入脚本信息
		abcOutput.writeU30(bitmaps.size());//脚本数量
		for(int i=0;i<bitmaps.size();i++)
		{
			SwfBitmap bitmap=bitmaps.get(i);
			
			//init method
			abcOutput.writeU30(i*3+2);
			//trait count
			abcOutput.writeU30(0x01);
			//class info
			abcOutput.writeU30(getQnameIndex(SwfDoAbc.PackageNamespace, bitmap.getPackName(), bitmap.getClassName()));
			//kind (class)
			abcOutput.writeByte(0x04);
			//slotID (class)
			abcOutput.write(0x00);
			//classIndex
			abcOutput.write(i);
		}
		
		//写入方法体信息
		abcOutput.writeU30(bitmaps.size()*3);
		for(int i=0;i<bitmaps.size();i++)
		{
			SwfBitmap bitmap=bitmaps.get(i);
			
			//实例构造函数
			abcOutput.writeU30(i*3+0);
			abcOutput.writeU30(3);//max stack;
			abcOutput.writeU30(3);//local count;
			abcOutput.writeU30(5);//init_scope_depth;
			abcOutput.writeU30(6);//max_scope_depth;
			abcOutput.writeU30(8);//code_length;
			abcOutput.writeByte(0xd0);//getlocal0
			abcOutput.writeByte(0x30);//pushscope
			abcOutput.writeByte(0xd0);//getlocal0
			abcOutput.writeByte(0xd1);//getlocal1
			abcOutput.writeByte(0xd2);//getlocal2
			abcOutput.writeByte(0x49);//constructsuper
			abcOutput.writeByte(0x02);//参数个数为2
			abcOutput.writeByte(0x47);//returnvoid
			abcOutput.writeU30(0x00);//exciptioin_count
			abcOutput.writeU30(0x00);//trait_count
			
			//静态构造函数
			abcOutput.writeU30(i*3+1);//method ref
			abcOutput.writeU30(1);//max stack;
			abcOutput.writeU30(1);//local count;
			abcOutput.writeU30(4);//init_scope_depth;
			abcOutput.writeU30(5);//max_scope_depth;
			abcOutput.writeU30(3);//code_length;
			abcOutput.writeByte(0xd0);//getlocal0
			abcOutput.writeByte(0x30);//pushscope
			abcOutput.writeByte(0x47);//returnvoid
			abcOutput.writeU30(0x00);//exciptioin_count
			abcOutput.writeU30(0x00);//trait_count

			//脚本构造函数
			abcOutput.writeU30(i*3+2);
			abcOutput.writeU30(0x02);//max stack;
			abcOutput.writeU30(0x01);//local count;
			abcOutput.writeU30(0x01);//init_scope_depth;
			abcOutput.writeU30(0x04);//max_scope_depth;
			abcOutput.writeU30(0x13);//code_length;
			abcOutput.writeByte(0xd0);//getlocal0;
			abcOutput.writeByte(0x30);//pushscope
			abcOutput.writeByte(0x65);//getscopeobject
			abcOutput.writeByte(0x00);//0
			abcOutput.writeByte(0x60);//getlex
			abcOutput.writeU30(getQnameIndex(SwfDoAbc.PackageNamespace, "", "Object"));
			abcOutput.writeByte(0x30);//pushscope
			abcOutput.writeByte(0x60);//getlex
			abcOutput.writeU30(getQnameIndex(SwfDoAbc.PackageNamespace,"flash.display","BitmapData"));
			abcOutput.writeByte(0x30);//pushscope
			abcOutput.writeByte(0x60);//getlex
			abcOutput.writeU30(getQnameIndex(SwfDoAbc.PackageNamespace,"flash.display","BitmapData"));
			abcOutput.writeByte(0x58);//newclass
			abcOutput.writeByte(i);//ClassInfo 索引
			abcOutput.writeByte(0x1d);//popscope
			abcOutput.writeByte(0x1d);//popscope
			abcOutput.writeByte(0x68);//initproperty
			abcOutput.writeU30(getQnameIndex(SwfDoAbc.PackageNamespace,bitmap.getPackName(),bitmap.getClassName()));
			abcOutput.writeByte(0x47);//returnvoid
			abcOutput.writeU30(0x00);//exciptioin_count
			abcOutput.writeU30(0x00);//trait_count
		}
		
		
		SwfDoAbcWriter output=new SwfDoAbcWriter(new ByteArrayOutputStream());
		
		//DoAbc Tag
		output.writeByte(0xBF);
		output.writeByte(0x14);
		
		//length
		output.writeByte(0x00);
		output.writeByte(0x00);
		output.writeByte(0x00);
		output.writeByte(0x00);
		
		//flag
		output.writeByte(0x01);
		output.writeByte(0x00);
		output.writeByte(0x00);
		output.writeByte(0x00);
		
		//name
		output.writeByte(0x00);
		
		//minor_version
		output.writeByte(0x10);
		output.writeByte(0x00);
		
		//major_version
		output.writeByte(0x2E);
		output.writeByte(0x00);
		
		//int pool
		int intCount=intList.size();
		if(intCount>0)
		{
			output.writeU30(intCount+1);
			for(int i=0;i<intCount;i++)
			{
				output.writeU30(intList.get(i));
			}
		}
		else
		{
			output.writeU30(0);
		}

		//uint pool
		int uintCount=uintList.size();
		if(uintCount>0)
		{
			output.writeU30(uintCount+1);
			for(int i=0;i<uintCount;i++)
			{
				output.writeU30(uintList.get(i));
			}
		}
		else
		{
			output.writeU30(0);
		}

		//double pool
		int doubleCount=doubleList.size();
		if(doubleCount>0)
		{
			output.writeU30(doubleCount+1);
			for(int i=0;i<doubleCount;i++)
			{
				output.writeDouble(doubleList.get(i));
			}
		}
		else
		{
			output.writeU30(0);
		}
		
		//string pool
		int stringCount=stringList.size();
		if(stringCount>0)
		{
			output.writeU30(stringCount+1);
			for(int i=0;i<stringCount;i++)
			{
				byte[] utf8=stringList.get(i).getBytes("utf8");
				output.writeU30(utf8.length);
				output.write(utf8);
			}
		}
		else
		{
			output.writeU30(0);
		}
		
		//namespace pool
		int namespaceCount=namespaceList.size();
		if(namespaceCount>0)
		{
			output.writeU30(namespaceCount+1);
			for(int i=0;i<namespaceCount;i++)
			{
				String txt=namespaceList.get(i);
				int index=txt.indexOf(">");
				int kind=Integer.parseInt(txt.substring(1,index));
				int name=Integer.parseInt(txt.substring(index+1));
				output.writeByte(kind);
				output.writeU30(name);
			}
		}
		else
		{
			output.writeU30(0);
		}
		
		//namespaceSet pool
		output.writeU30(0);
		
		//qname pool
		int qnameCount=qnameList.size();
		if(qnameCount>0)
		{
			output.writeU30(qnameCount+1);
			for(int i=0;i<qnameCount;i++)
			{
				String txt=qnameList.get(i);
				int index=txt.indexOf("::");
				int ns=Integer.parseInt(txt.substring(0,index));
				int name=Integer.parseInt(txt.substring(index+2));
				output.writeByte(0x07);
				output.writeU30(ns);
				output.writeU30(name);
			}
		}
		else
		{
			output.writeU30(0);
		}
		
		output.write(abcOutput.getBytes());
		
		byte[] result=output.getBytes();
		
		int length=result.length-6;
		
		result[2]=(byte) ((length) & 0xFF);
		result[3]=(byte) ((length>>8) & 0xFF);
		result[4]=(byte) ((length>>16) & 0xFF);
		result[5]=(byte) ((length>>24) & 0xFF);
		
		return result;
	}
	
	
	private byte[] getBitmapABC() throws IOException
	{
		SwfDoAbcWriter abcOutput=new SwfDoAbcWriter(new ByteArrayOutputStream());
		
		//写入方法声明
		abcOutput.writeU30(bitmaps.size()*3);
		for(int i=0;i<bitmaps.size();i++)
		{
			SwfBitmap bitmap=bitmaps.get(i);
			
			//实例构造函数
			abcOutput.writeU30(2);//参数个数
			abcOutput.writeU30(0);//返回类型
			abcOutput.writeU30(getQnameIndex(SwfDoAbc.PackageNamespace, "", "int"));//参数1类型
			abcOutput.writeU30(getQnameIndex(SwfDoAbc.PackageNamespace, "", "int"));//参数2类型
			abcOutput.writeU30(0);//方法名称
			abcOutput.write(0x08);//flag 带默认值
			abcOutput.writeU30(0x02);//默认值数量
			abcOutput.writeU30(getIntIndex(bitmap.getWidth()));//int index
			abcOutput.writeByte(0x03);//int pool
			abcOutput.writeU30(getIntIndex(bitmap.getHeight()));//int index
			abcOutput.writeByte(0x03);//int pool

			//静态构造函数
			abcOutput.writeU30(0);//参数个数
			abcOutput.writeU30(0);//返回类型
			abcOutput.writeU30(0);//方法名称
			abcOutput.writeByte(0);//flag

			//脚本构造函数
			abcOutput.writeU30(0);//参数个数
			abcOutput.writeU30(0);//返回类型
			abcOutput.writeU30(0);//方法名称
			abcOutput.writeByte(0);//flag
		}
		
		//写入元数据
		abcOutput.writeByte(0);
		
		//写入类数量
		abcOutput.writeU30(bitmaps.size());
		
		//写入实例信息
		for(int i=0;i<bitmaps.size();i++)
		{
			SwfBitmap bitmap=bitmaps.get(i);
			
			//class name
			abcOutput.writeU30(getQnameIndex(SwfDoAbc.PackageNamespace, bitmap.getPackName(), bitmap.getClassName()));
			//super class name
			abcOutput.writeU30(getQnameIndex(SwfDoAbc.PackageNamespace,"flash.display","BitmapData"));
			//flag
			abcOutput.writeByte(0x01|0x08);
			//protectedNs
			abcOutput.writeU30(getNamespaceIndex(SwfDoAbc.ProtectedNamespace, bitmap.getClassName()));
			//interface count
			abcOutput.writeU30(0x00);
			//init method
			abcOutput.writeU30(i*3+0);
			//trait count
			abcOutput.writeU30(0x00);
		}
		
		//写入类型信息
		for(int i=0;i<bitmaps.size();i++)
		{
			//init method
			abcOutput.writeU30(i*3+1);
			//trait count
			abcOutput.writeU30(0x00);
		}
		
		//写入脚本信息
		abcOutput.writeU30(bitmaps.size());//脚本数量
		for(int i=0;i<bitmaps.size();i++)
		{
			SwfBitmap bitmap=bitmaps.get(i);
			
			//init method
			abcOutput.writeU30(i*3+2);
			//trait count
			abcOutput.writeU30(0x01);
			//class info
			abcOutput.writeU30(getQnameIndex(SwfDoAbc.PackageNamespace, bitmap.getPackName(), bitmap.getClassName()));
			//kind (class)
			abcOutput.writeByte(0x04);
			//slotID (class)
			abcOutput.write(0x00);
			//classIndex
			abcOutput.write(i);
		}
		
		//写入方法体信息
		abcOutput.writeU30(bitmaps.size()*3);
		for(int i=0;i<bitmaps.size();i++)
		{
			SwfBitmap bitmap=bitmaps.get(i);
			
			//实例构造函数
			abcOutput.writeU30(i*3+0);
			abcOutput.writeU30(3);//max stack;
			abcOutput.writeU30(3);//local count;
			abcOutput.writeU30(5);//init_scope_depth;
			abcOutput.writeU30(6);//max_scope_depth;
			abcOutput.writeU30(8);//code_length;
			abcOutput.writeByte(0xd0);//getlocal0
			abcOutput.writeByte(0x30);//pushscope
			abcOutput.writeByte(0xd0);//getlocal0
			abcOutput.writeByte(0xd1);//getlocal1
			abcOutput.writeByte(0xd2);//getlocal2
			abcOutput.writeByte(0x49);//constructsuper
			abcOutput.writeByte(0x02);//参数个数为2
			abcOutput.writeByte(0x47);//returnvoid
			abcOutput.writeU30(0x00);//exciptioin_count
			abcOutput.writeU30(0x00);//trait_count
			
			//静态构造函数
			abcOutput.writeU30(i*3+1);//method ref
			abcOutput.writeU30(1);//max stack;
			abcOutput.writeU30(1);//local count;
			abcOutput.writeU30(4);//init_scope_depth;
			abcOutput.writeU30(5);//max_scope_depth;
			abcOutput.writeU30(3);//code_length;
			abcOutput.writeByte(0xd0);//getlocal0
			abcOutput.writeByte(0x30);//pushscope
			abcOutput.writeByte(0x47);//returnvoid
			abcOutput.writeU30(0x00);//exciptioin_count
			abcOutput.writeU30(0x00);//trait_count

			//脚本构造函数
			abcOutput.writeU30(i*3+2);
			abcOutput.writeU30(0x02);//max stack;
			abcOutput.writeU30(0x01);//local count;
			abcOutput.writeU30(0x01);//init_scope_depth;
			abcOutput.writeU30(0x04);//max_scope_depth;
			abcOutput.writeU30(0x13);//code_length;
			abcOutput.writeByte(0xd0);//getlocal0;
			abcOutput.writeByte(0x30);//pushscope
			abcOutput.writeByte(0x65);//getscopeobject
			abcOutput.writeByte(0x00);//0
			abcOutput.writeByte(0x60);//getlex
			abcOutput.writeU30(getQnameIndex(SwfDoAbc.PackageNamespace, "", "Object"));
			abcOutput.writeByte(0x30);//pushscope
			abcOutput.writeByte(0x60);//getlex
			abcOutput.writeU30(getQnameIndex(SwfDoAbc.PackageNamespace,"flash.display","BitmapData"));
			abcOutput.writeByte(0x30);//pushscope
			abcOutput.writeByte(0x60);//getlex
			abcOutput.writeU30(getQnameIndex(SwfDoAbc.PackageNamespace,"flash.display","BitmapData"));
			abcOutput.writeByte(0x58);//newclass
			abcOutput.writeByte(i);//ClassInfo 索引
			abcOutput.writeByte(0x1d);//popscope
			abcOutput.writeByte(0x1d);//popscope
			abcOutput.writeByte(0x68);//initproperty
			abcOutput.writeU30(getQnameIndex(SwfDoAbc.PackageNamespace,bitmap.getPackName(),bitmap.getClassName()));
			abcOutput.writeByte(0x47);//returnvoid
			abcOutput.writeU30(0x00);//exciptioin_count
			abcOutput.writeU30(0x00);//trait_count
		}
		
		
		SwfDoAbcWriter output=new SwfDoAbcWriter(new ByteArrayOutputStream());
		
		//DoAbc Tag
		output.writeByte(0xBF);
		output.writeByte(0x14);
		
		//length
		output.writeByte(0x00);
		output.writeByte(0x00);
		output.writeByte(0x00);
		output.writeByte(0x00);
		
		//flag
		output.writeByte(0x01);
		output.writeByte(0x00);
		output.writeByte(0x00);
		output.writeByte(0x00);
		
		//name
		output.writeByte(0x00);
		
		//minor_version
		output.writeByte(0x10);
		output.writeByte(0x00);
		
		//major_version
		output.writeByte(0x2E);
		output.writeByte(0x00);
		
		//int pool
		int intCount=intList.size();
		if(intCount>0)
		{
			output.writeU30(intCount+1);
			for(int i=0;i<intCount;i++)
			{
				output.writeU30(intList.get(i));
			}
		}
		else
		{
			output.writeU30(0);
		}

		//uint pool
		int uintCount=uintList.size();
		if(uintCount>0)
		{
			output.writeU30(uintCount+1);
			for(int i=0;i<uintCount;i++)
			{
				output.writeU30(uintList.get(i));
			}
		}
		else
		{
			output.writeU30(0);
		}

		//double pool
		int doubleCount=doubleList.size();
		if(doubleCount>0)
		{
			output.writeU30(doubleCount+1);
			for(int i=0;i<doubleCount;i++)
			{
				output.writeDouble(doubleList.get(i));
			}
		}
		else
		{
			output.writeU30(0);
		}
		
		//string pool
		int stringCount=stringList.size();
		if(stringCount>0)
		{
			output.writeU30(stringCount+1);
			for(int i=0;i<stringCount;i++)
			{
				byte[] utf8=stringList.get(i).getBytes("utf8");
				output.writeU30(utf8.length);
				output.write(utf8);
			}
		}
		else
		{
			output.writeU30(0);
		}
		
		//namespace pool
		int namespaceCount=namespaceList.size();
		if(namespaceCount>0)
		{
			output.writeU30(namespaceCount+1);
			for(int i=0;i<namespaceCount;i++)
			{
				String txt=namespaceList.get(i);
				int index=txt.indexOf(">");
				int kind=Integer.parseInt(txt.substring(1,index));
				int name=Integer.parseInt(txt.substring(index+1));
				output.writeByte(kind);
				output.writeU30(name);
			}
		}
		else
		{
			output.writeU30(0);
		}
		
		//namespaceSet pool
		output.writeU30(0);
		
		//qname pool
		int qnameCount=qnameList.size();
		if(qnameCount>0)
		{
			output.writeU30(qnameCount+1);
			for(int i=0;i<qnameCount;i++)
			{
				String txt=qnameList.get(i);
				int index=txt.indexOf("::");
				int ns=Integer.parseInt(txt.substring(0,index));
				int name=Integer.parseInt(txt.substring(index+2));
				output.writeByte(0x07);
				output.writeU30(ns);
				output.writeU30(name);
			}
		}
		else
		{
			output.writeU30(0);
		}
		
		output.write(abcOutput.getBytes());
		
		byte[] result=output.getBytes();
		
		int length=result.length-6;
		
		result[2]=(byte) ((length) & 0xFF);
		result[3]=(byte) ((length>>8) & 0xFF);
		result[4]=(byte) ((length>>16) & 0xFF);
		result[5]=(byte) ((length>>24) & 0xFF);
		
		return result;
	}
	

	/**
	 * 获取Mp3 ABC
	 * @return
	 * @throws IOException
	 */
	private byte[] getMp3ABC() throws IOException
	{
		SwfDoAbcWriter abcOutput=new SwfDoAbcWriter(new ByteArrayOutputStream());
		
		//写入方法声明
		abcOutput.writeU30(mp3s.size()*3);
		for(int i=0;i<mp3s.size();i++)
		{
			//实例构造函数
			abcOutput.writeU30(0);//参数个数
			abcOutput.writeU30(0);//返回类型
			abcOutput.writeU30(0);//方法名称
			abcOutput.write(0);//flag

			//静态构造函数
			abcOutput.writeU30(0);//参数个数
			abcOutput.writeU30(0);//返回类型
			abcOutput.writeU30(0);//方法名称
			abcOutput.writeByte(0);//flag

			//脚本构造函数
			abcOutput.writeU30(0);//参数个数
			abcOutput.writeU30(0);//返回类型
			abcOutput.writeU30(0);//方法名称
			abcOutput.writeByte(0);//flag
		}
		
		//写入元数据
		abcOutput.writeByte(0);
		
		//写入类数量
		abcOutput.writeU30(mp3s.size());
		
		//写入实例信息
		for(int i=0;i<mp3s.size();i++)
		{
			SwfMp3 mp3=mp3s.get(i);
			
			//class name
			abcOutput.writeU30(getQnameIndex(SwfDoAbc.PackageNamespace, mp3.getPackName(), mp3.getClassName()));
			//super class name
			abcOutput.writeU30(getQnameIndex(SwfDoAbc.PackageNamespace,"flash.media","Sound"));
			//flag
			abcOutput.writeByte(0x01|0x08);
			//protectedNs
			abcOutput.writeU30(getNamespaceIndex(SwfDoAbc.ProtectedNamespace, mp3.getClassName()));
			//interface count
			abcOutput.writeU30(0x00);
			//init method
			abcOutput.writeU30(i*3+0);
			//trait count
			abcOutput.writeU30(0x00);
		}
		
		//写入类型信息
		for(int i=0;i<mp3s.size();i++)
		{
			//init method
			abcOutput.writeU30(i*3+1);
			//trait count
			abcOutput.writeU30(0x00);
		}
		
		//写入脚本信息
		abcOutput.writeU30(mp3s.size());//脚本数量
		for(int i=0;i<mp3s.size();i++)
		{
			SwfMp3 mp3=mp3s.get(i);
			
			//init method
			abcOutput.writeU30(i*3+2);
			//trait count
			abcOutput.writeU30(0x01);
			//class info
			abcOutput.writeU30(getQnameIndex(SwfDoAbc.PackageNamespace, mp3.getPackName(), mp3.getClassName()));
			//kind (class)
			abcOutput.writeByte(0x04);
			//slotID (class)
			abcOutput.write(0x00);
			//classIndex
			abcOutput.write(i);
		}
		
		//写入方法体信息
		abcOutput.writeU30(mp3s.size()*3);
		for(int i=0;i<mp3s.size();i++)
		{
			SwfMp3 mp3=mp3s.get(i);
			
			//实例构造函数
			abcOutput.writeU30(i*3+0);
			abcOutput.writeU30(2);//max stack;
			abcOutput.writeU30(1);//local count;
			abcOutput.writeU30(1);//init_scope_depth;
			abcOutput.writeU30(2);//max_scope_depth;
			abcOutput.writeU30(6);//code_length;
			abcOutput.writeByte(0xd0);//getlocal0
			abcOutput.writeByte(0x30);//pushscope
			abcOutput.writeByte(0xd0);//getlocal0
			abcOutput.writeByte(0x49);//constructsuper
			abcOutput.writeByte(0x00);//参数个数为0
			abcOutput.writeByte(0x47);//returnvoid
			abcOutput.writeU30(0x00);//exciptioin_count
			abcOutput.writeU30(0x00);//trait_count
			
			//静态构造函数
			abcOutput.writeU30(i*3+1);//method ref
			abcOutput.writeU30(1);//max stack;
			abcOutput.writeU30(1);//local count;
			abcOutput.writeU30(4);//init_scope_depth;
			abcOutput.writeU30(5);//max_scope_depth;
			abcOutput.writeU30(3);//code_length;
			abcOutput.writeByte(0xd0);//getlocal0
			abcOutput.writeByte(0x30);//pushscope
			abcOutput.writeByte(0x47);//returnvoid
			abcOutput.writeU30(0x00);//exciptioin_count
			abcOutput.writeU30(0x00);//trait_count

			//脚本构造函数
			abcOutput.writeU30(i*3+2);
			abcOutput.writeU30(0x02);//max stack;
			abcOutput.writeU30(0x01);//local count;
			abcOutput.writeU30(0x01);//init_scope_depth;
			abcOutput.writeU30(0x04);//max_scope_depth;
			abcOutput.writeU30(0x13);//code_length;
			abcOutput.writeByte(0xd0);//getlocal0;
			abcOutput.writeByte(0x30);//pushscope
			abcOutput.writeByte(0x65);//getscopeobject
			abcOutput.writeByte(0x00);//0
			abcOutput.writeByte(0x60);//getlex
			abcOutput.writeU30(getQnameIndex(SwfDoAbc.PackageNamespace, "", "Object"));
			abcOutput.writeByte(0x30);//pushscope
			abcOutput.writeByte(0x60);//getlex
			abcOutput.writeU30(getQnameIndex(SwfDoAbc.PackageNamespace,"flash.media","Sound"));
			abcOutput.writeByte(0x30);//pushscope
			abcOutput.writeByte(0x60);//getlex
			abcOutput.writeU30(getQnameIndex(SwfDoAbc.PackageNamespace,"flash.media","Sound"));
			abcOutput.writeByte(0x58);//newclass
			abcOutput.writeByte(i);//ClassInfo 索引
			abcOutput.writeByte(0x1d);//popscope
			abcOutput.writeByte(0x1d);//popscope
			abcOutput.writeByte(0x68);//initproperty
			abcOutput.writeU30(getQnameIndex(SwfDoAbc.PackageNamespace,mp3.getPackName(),mp3.getClassName()));
			abcOutput.writeByte(0x47);//returnvoid
			abcOutput.writeU30(0x00);//exciptioin_count
			abcOutput.writeU30(0x00);//trait_count
		}
		
		
		SwfDoAbcWriter output=new SwfDoAbcWriter(new ByteArrayOutputStream());
		
		//DoAbc Tag
		output.writeByte(0xBF);
		output.writeByte(0x14);
		
		//length
		output.writeByte(0x00);
		output.writeByte(0x00);
		output.writeByte(0x00);
		output.writeByte(0x00);
		
		//flag
		output.writeByte(0x01);
		output.writeByte(0x00);
		output.writeByte(0x00);
		output.writeByte(0x00);
		
		//name
		output.writeByte(0x00);
		
		//minor_version
		output.writeByte(0x10);
		output.writeByte(0x00);
		
		//major_version
		output.writeByte(0x2E);
		output.writeByte(0x00);
		
		//int pool
		int intCount=intList.size();
		if(intCount>0)
		{
			output.writeU30(intCount+1);
			for(int i=0;i<intCount;i++)
			{
				output.writeU30(intList.get(i));
			}
		}
		else
		{
			output.writeU30(0);
		}

		//uint pool
		int uintCount=uintList.size();
		if(uintCount>0)
		{
			output.writeU30(uintCount+1);
			for(int i=0;i<uintCount;i++)
			{
				output.writeU30(uintList.get(i));
			}
		}
		else
		{
			output.writeU30(0);
		}

		//double pool
		int doubleCount=doubleList.size();
		if(doubleCount>0)
		{
			output.writeU30(doubleCount+1);
			for(int i=0;i<doubleCount;i++)
			{
				output.writeDouble(doubleList.get(i));
			}
		}
		else
		{
			output.writeU30(0);
		}
		
		//string pool
		int stringCount=stringList.size();
		if(stringCount>0)
		{
			output.writeU30(stringCount+1);
			for(int i=0;i<stringCount;i++)
			{
				byte[] utf8=stringList.get(i).getBytes("utf8");
				output.writeU30(utf8.length);
				output.write(utf8);
			}
		}
		else
		{
			output.writeU30(0);
		}
		
		//namespace pool
		int namespaceCount=namespaceList.size();
		if(namespaceCount>0)
		{
			output.writeU30(namespaceCount+1);
			for(int i=0;i<namespaceCount;i++)
			{
				String txt=namespaceList.get(i);
				int index=txt.indexOf(">");
				int kind=Integer.parseInt(txt.substring(1,index));
				int name=Integer.parseInt(txt.substring(index+1));
				output.writeByte(kind);
				output.writeU30(name);
			}
		}
		else
		{
			output.writeU30(0);
		}
		
		//namespaceSet pool
		output.writeU30(0);
		
		//qname pool
		int qnameCount=qnameList.size();
		if(qnameCount>0)
		{
			output.writeU30(qnameCount+1);
			for(int i=0;i<qnameCount;i++)
			{
				String txt=qnameList.get(i);
				int index=txt.indexOf("::");
				int ns=Integer.parseInt(txt.substring(0,index));
				int name=Integer.parseInt(txt.substring(index+2));
				output.writeByte(0x07);
				output.writeU30(ns);
				output.writeU30(name);
			}
		}
		else
		{
			output.writeU30(0);
		}
		
		output.write(abcOutput.getBytes());
		
		byte[] result=output.getBytes();
		
		int length=result.length-6;
		
		result[2]=(byte) ((length) & 0xFF);
		result[3]=(byte) ((length>>8) & 0xFF);
		result[4]=(byte) ((length>>16) & 0xFF);
		result[5]=(byte) ((length>>24) & 0xFF);
		
		return result;
	}
	
	
	/**
	 * 获取XML ABC
	 * @return
	 * @throws IOException
	 */
	private byte[] getXmlABC() throws IOException
	{
		SwfDoAbcWriter abcOutput=new SwfDoAbcWriter(new ByteArrayOutputStream());
		
		//写入方法声明
		abcOutput.writeU30(xmls.size()*3);
		for(int i=0;i<xmls.size();i++)
		{
			//实例构造函数
			abcOutput.writeU30(0);//参数个数
			abcOutput.writeU30(0);//返回类型
			abcOutput.writeU30(0);//方法名称
			abcOutput.write(0);//flag

			//静态构造函数
			abcOutput.writeU30(0);//参数个数
			abcOutput.writeU30(0);//返回类型
			abcOutput.writeU30(0);//方法名称
			abcOutput.writeByte(0);//flag

			//脚本构造函数
			abcOutput.writeU30(0);//参数个数
			abcOutput.writeU30(0);//返回类型
			abcOutput.writeU30(0);//方法名称
			abcOutput.writeByte(0);//flag
		}
		
		//写入元数据
		abcOutput.writeByte(0);
		
		//写入类数量
		abcOutput.writeU30(xmls.size());
		
		//写入实例信息
		for(int i=0;i<xmls.size();i++)
		{
			SwfXML xml=xmls.get(i);
			
			//class name
			abcOutput.writeU30(getQnameIndex(SwfDoAbc.PackageNamespace, xml.getPackName(), xml.getClassName()));
			//super class name
			abcOutput.writeU30(getQnameIndex(SwfDoAbc.PackageNamespace,"flash.utils","ByteArray"));
			//flag
			abcOutput.writeByte(0x01|0x08);
			//protectedNs
			abcOutput.writeU30(getNamespaceIndex(SwfDoAbc.ProtectedNamespace, xml.getClassName()));
			//interface count
			abcOutput.writeU30(0x00);
			//init method
			abcOutput.writeU30(i*3+0);
			//trait count
			abcOutput.writeU30(0x00);
		}
		
		//写入类型信息
		for(int i=0;i<xmls.size();i++)
		{
			//init method
			abcOutput.writeU30(i*3+1);
			//trait count
			abcOutput.writeU30(0x00);
		}
		
		//写入脚本信息
		abcOutput.writeU30(xmls.size());//脚本数量
		for(int i=0;i<xmls.size();i++)
		{
			SwfXML xml=xmls.get(i);
			
			//init method
			abcOutput.writeU30(i*3+2);
			//trait count
			abcOutput.writeU30(0x01);
			//class info
			abcOutput.writeU30(getQnameIndex(SwfDoAbc.PackageNamespace, xml.getPackName(), xml.getClassName()));
			//kind (class)
			abcOutput.writeByte(0x04);
			//slotID (class)
			abcOutput.write(0x00);
			//classIndex
			abcOutput.write(i);
		}
		
		//写入方法体信息
		abcOutput.writeU30(xmls.size()*3);
		for(int i=0;i<xmls.size();i++)
		{
			SwfXML xml=xmls.get(i);
			
			//实例构造函数
			abcOutput.writeU30(i*3+0);
			abcOutput.writeU30(2);//max stack;
			abcOutput.writeU30(1);//local count;
			abcOutput.writeU30(1);//init_scope_depth;
			abcOutput.writeU30(2);//max_scope_depth;
			abcOutput.writeU30(6);//code_length;
			abcOutput.writeByte(0xd0);//getlocal0
			abcOutput.writeByte(0x30);//pushscope
			abcOutput.writeByte(0xd0);//getlocal0
			abcOutput.writeByte(0x49);//constructsuper
			abcOutput.writeByte(0x00);//参数个数为0
			abcOutput.writeByte(0x47);//returnvoid
			abcOutput.writeU30(0x00);//exciptioin_count
			abcOutput.writeU30(0x00);//trait_count
			
			//静态构造函数
			abcOutput.writeU30(i*3+1);//method ref
			abcOutput.writeU30(1);//max stack;
			abcOutput.writeU30(1);//local count;
			abcOutput.writeU30(4);//init_scope_depth;
			abcOutput.writeU30(5);//max_scope_depth;
			abcOutput.writeU30(3);//code_length;
			abcOutput.writeByte(0xd0);//getlocal0
			abcOutput.writeByte(0x30);//pushscope
			abcOutput.writeByte(0x47);//returnvoid
			abcOutput.writeU30(0x00);//exciptioin_count
			abcOutput.writeU30(0x00);//trait_count

			//脚本构造函数
			abcOutput.writeU30(i*3+2);
			abcOutput.writeU30(0x02);//max stack;
			abcOutput.writeU30(0x01);//local count;
			abcOutput.writeU30(0x01);//init_scope_depth;
			abcOutput.writeU30(0x04);//max_scope_depth;
			abcOutput.writeU30(0x13);//code_length;
			abcOutput.writeByte(0xd0);//getlocal0;
			abcOutput.writeByte(0x30);//pushscope
			abcOutput.writeByte(0x65);//getscopeobject
			abcOutput.writeByte(0x00);//0
			abcOutput.writeByte(0x60);//getlex
			abcOutput.writeU30(getQnameIndex(SwfDoAbc.PackageNamespace, "", "Object"));
			abcOutput.writeByte(0x30);//pushscope
			abcOutput.writeByte(0x60);//getlex
			abcOutput.writeU30(getQnameIndex(SwfDoAbc.PackageNamespace,"flash.utils","ByteArray"));
			abcOutput.writeByte(0x30);//pushscope
			abcOutput.writeByte(0x60);//getlex
			abcOutput.writeU30(getQnameIndex(SwfDoAbc.PackageNamespace,"flash.utils","ByteArray"));
			abcOutput.writeByte(0x58);//newclass
			abcOutput.writeByte(i);//ClassInfo 索引
			abcOutput.writeByte(0x1d);//popscope
			abcOutput.writeByte(0x1d);//popscope
			abcOutput.writeByte(0x68);//initproperty
			abcOutput.writeU30(getQnameIndex(SwfDoAbc.PackageNamespace,xml.getPackName(),xml.getClassName()));
			abcOutput.writeByte(0x47);//returnvoid
			abcOutput.writeU30(0x00);//exciptioin_count
			abcOutput.writeU30(0x00);//trait_count
		}
		
		
		SwfDoAbcWriter output=new SwfDoAbcWriter(new ByteArrayOutputStream());
		
		//DoAbc Tag
		output.writeByte(0xBF);
		output.writeByte(0x14);
		
		//length
		output.writeByte(0x00);
		output.writeByte(0x00);
		output.writeByte(0x00);
		output.writeByte(0x00);
		
		//flag
		output.writeByte(0x01);
		output.writeByte(0x00);
		output.writeByte(0x00);
		output.writeByte(0x00);
		
		//name
		output.writeByte(0x00);
		
		//minor_version
		output.writeByte(0x10);
		output.writeByte(0x00);
		
		//major_version
		output.writeByte(0x2E);
		output.writeByte(0x00);
		
		//int pool
		int intCount=intList.size();
		if(intCount>0)
		{
			output.writeU30(intCount+1);
			for(int i=0;i<intCount;i++)
			{
				output.writeU30(intList.get(i));
			}
		}
		else
		{
			output.writeU30(0);
		}

		//uint pool
		int uintCount=uintList.size();
		if(uintCount>0)
		{
			output.writeU30(uintCount+1);
			for(int i=0;i<uintCount;i++)
			{
				output.writeU30(uintList.get(i));
			}
		}
		else
		{
			output.writeU30(0);
		}

		//double pool
		int doubleCount=doubleList.size();
		if(doubleCount>0)
		{
			output.writeU30(doubleCount+1);
			for(int i=0;i<doubleCount;i++)
			{
				output.writeDouble(doubleList.get(i));
			}
		}
		else
		{
			output.writeU30(0);
		}
		
		//string pool
		int stringCount=stringList.size();
		if(stringCount>0)
		{
			output.writeU30(stringCount+1);
			for(int i=0;i<stringCount;i++)
			{
				byte[] utf8=stringList.get(i).getBytes("utf8");
				output.writeU30(utf8.length);
				output.write(utf8);
			}
		}
		else
		{
			output.writeU30(0);
		}
		
		//namespace pool
		int namespaceCount=namespaceList.size();
		if(namespaceCount>0)
		{
			output.writeU30(namespaceCount+1);
			for(int i=0;i<namespaceCount;i++)
			{
				String txt=namespaceList.get(i);
				int index=txt.indexOf(">");
				int kind=Integer.parseInt(txt.substring(1,index));
				int name=Integer.parseInt(txt.substring(index+1));
				output.writeByte(kind);
				output.writeU30(name);
			}
		}
		else
		{
			output.writeU30(0);
		}
		
		//namespaceSet pool
		output.writeU30(0);
		
		//qname pool
		int qnameCount=qnameList.size();
		if(qnameCount>0)
		{
			output.writeU30(qnameCount+1);
			for(int i=0;i<qnameCount;i++)
			{
				String txt=qnameList.get(i);
				int index=txt.indexOf("::");
				int ns=Integer.parseInt(txt.substring(0,index));
				int name=Integer.parseInt(txt.substring(index+2));
				output.writeByte(0x07);
				output.writeU30(ns);
				output.writeU30(name);
			}
		}
		else
		{
			output.writeU30(0);
		}
		
		output.write(abcOutput.getBytes());
		
		byte[] result=output.getBytes();
		
		int length=result.length-6;
		
		result[2]=(byte) ((length) & 0xFF);
		result[3]=(byte) ((length>>8) & 0xFF);
		result[4]=(byte) ((length>>16) & 0xFF);
		result[5]=(byte) ((length>>24) & 0xFF);
		
		return result;
	}
}
