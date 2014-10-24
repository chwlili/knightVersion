package org.game.knight.version.packer.game;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.chw.util.ZlibUtil;

import com.adobe.flash.abc.ABCParser;
import com.adobe.flash.abc.semantics.ClassInfo;
import com.adobe.flash.abc.semantics.Float4;
import com.adobe.flash.abc.semantics.InstanceInfo;
import com.adobe.flash.abc.semantics.Metadata;
import com.adobe.flash.abc.semantics.MethodInfo;
import com.adobe.flash.abc.semantics.Name;
import com.adobe.flash.abc.semantics.Namespace;
import com.adobe.flash.abc.semantics.Nsset;
import com.adobe.flash.abc.visitors.IABCVisitor;
import com.adobe.flash.abc.visitors.IClassVisitor;
import com.adobe.flash.abc.visitors.IMethodVisitor;
import com.adobe.flash.abc.visitors.IScriptVisitor;

public class TextFinder
{
	/**
	 * 清理SWF字节流
	 * 
	 * @param stream
	 * @return
	 * @throws FileNotFoundException
	 */
	public static String[] find(File file) throws FileNotFoundException
	{
		InputStream stream = new FileInputStream(file);
		final ArrayList<String> txts = new ArrayList<String>();

		try
		{
			int streamLength = stream.available();

			String flag = String.valueOf((char) (stream.read())) + String.valueOf((char) (stream.read())) + String.valueOf((char) (stream.read()));
			int ver = stream.read(); // ver

			stream.skip(4);

			byte[] bytes = null;// new byte[(int)(file.length())];

			if (flag.equals("CWS"))
			{
				byte[] cws = new byte[(int) (streamLength - 8)];
				stream.read(cws);
				bytes = ZlibUtil.decompress(cws);
			}
			else
			{
				bytes = new byte[(int) (streamLength - 8)];
				stream.read(bytes);
			}
			stream.close();

			ByteArrayInputStream byteInput = new ByteArrayInputStream(bytes);

			byteInput.mark(1024);

			int w = byteInput.read() >>> 3;
			int len = (int) ((w * 4 + 5) / 8);
			if ((w * 4 + 5) % 8 > 0)
			{
				len += 1;
			}

			int rate = ((byteInput.read() << 8) | byteInput.read());
			int frames = (byteInput.read() | (byteInput.read() << 8));
			len += 4;

			System.out.println("帧频:" + rate);
			System.out.println("帧数:" + frames);

			byteInput.reset();

			byte[] temp = new byte[len];
			byteInput.read(temp);

			while (byteInput.available() > 0)
			{
				int pos1 = byteInput.available();

				byteInput.mark(1024000);

				int tag = byteInput.read() + (byteInput.read() << 8);
				int tagID = tag >>> 6;
				int tagLen = tag & 0x3f;
				int preLen = 2;

				if (tagLen == 0x3f)
				{
					tagLen = byteInput.read() + (byteInput.read() << 8) + (byteInput.read() << 16) + (byteInput.read() << 24);
					preLen += 4;
				}

				if (tagID == 82)
				{
					// uint32
					byteInput.read();
					byteInput.read();
					byteInput.read();
					byteInput.read();

					// string
					for (int ch = byteInput.read(); ch != 0; ch = byteInput.read())
					{
					}

					int pos2 = byteInput.available();
					int byteLen = (preLen + tagLen) - (pos1 - pos2);

					byte[] tagContent = new byte[byteLen];
					byteInput.read(tagContent);

					ABCParser abc = new ABCParser(tagContent);
					abc.parseABC(new IABCVisitor()
					{

						@Override
						public void visitEnd()
						{
						}

						@Override
						public IScriptVisitor visitScript()
						{
							return null;
						}

						@Override
						public void visitPooledUInt(Long arg0)
						{
						}

						@Override
						public void visitPooledString(String txt)
						{
							if (txt.startsWith("#"))
							{
								txts.add(txt.substring(1));
							}
						}

						@Override
						public void visitPooledNsSet(Nsset arg0)
						{
						}

						@Override
						public void visitPooledNamespace(Namespace arg0)
						{
						}

						@Override
						public void visitPooledName(Name arg0)
						{
						}

						@Override
						public void visitPooledMetadata(Metadata arg0)
						{
						}

						@Override
						public void visitPooledInt(Integer arg0)
						{
						}

						@Override
						public void visitPooledFloat4(Float4 arg0)
						{
						}

						@Override
						public void visitPooledFloat(Float arg0)
						{
						}

						@Override
						public void visitPooledDouble(Double arg0)
						{
						}

						@Override
						public IMethodVisitor visitMethod(MethodInfo arg0)
						{
							return null;
						}

						@Override
						public IClassVisitor visitClass(InstanceInfo arg0, ClassInfo arg1)
						{
							return null;
						}

						@Override
						public void visit(int arg0, int arg1)
						{
						}
					});
				}

				byteInput.reset();

				byteInput.skip(preLen + tagLen);
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

		return txts.toArray(new String[] {});
	}
}
