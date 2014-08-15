package org.game.knight.version.packer.world.output3d;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.GamePackerConst;

public final class TextureHelper
{
	/**
	 * 统一宽度为2的次方
	 * 
	 * @param value
	 * @return
	 */
	public static int normalizeWH(int value)
	{
		int[] sizes = new int[] { 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048 };
		for (int size : sizes)
		{
			if (value <= size)
			{
				return size;
			}
		}
		return 2048;
	}

	/**
	 * 调用CMD
	 * 
	 * @param cmd
	 */
	public static void png2atf(File input, File output)
	{
		String atfCMD = "png2atf";

		File atfExe = new File(GamePackerConst.getJarDir().getPath() + File.separatorChar + "png2atf.exe");
		if (atfExe.exists())
		{
			atfCMD = atfExe.getPath();
		}
		atfCMD += " -c d -r -n 0,0 -i " + input.getPath() + " -o " + output.getPath();

		try
		{
			Process p = Runtime.getRuntime().exec(atfCMD);

			BufferedReader errorOutput = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String error = null;
			while ((error = errorOutput.readLine()) != null)
			{
				System.out.println(error);
			}

			p.destroy();
		}
		catch (Exception e)
		{
			GamePacker.error(e);
		}
	}
}
