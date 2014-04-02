package org.chw.swf.append;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SwfDoAbcWriter extends DataOutputStream
{

	private ByteArrayOutputStream bytes;
	public SwfDoAbcWriter(ByteArrayOutputStream arg0)
	{
		super(arg0);
		bytes=arg0;
	}
	
	public byte[] getBytes()
	{
		return bytes.toByteArray();
	}
	
	public void writeU30(int val) throws IOException
	{
		while(true)
		{
			if((val & ~0x7F)==0)
			{
				writeByte(val);
				return;
			}
			else
			{
				writeByte((val & 0x7F)|0x80);
				val>>=7;
			}
		}
	}

	public void writeU32(int val) throws IOException
	{
		while(true)
		{
			writeByte((val & 0x7F) | 0x80);
			
			val=val>>7;
			
			if(val==0)
			{
				break;
			}
		}
	}

	public void writeS32(int val) throws IOException
	{
		while(true)
		{
			writeByte((val & 0x7F) | 0x80);
			
			val=val>>7;
			
			if(val==0)
			{
				break;
			}
		}
	}
}
