package org.game.knight.version.packer;

import java.io.File;

public class GamePackerConst
{
	/**
	 * ÿ��Ŀ¼���ļ���
	 */
	public static final int FILE_COUNT_EACH_DIR=1000;

	/**
	 * ��ȡJARĿ¼
	 * @return
	 */
	public static File getJarDir()
	{
		return new File(System.getProperty("user.home") + File.separatorChar + "GamePacker");
	}
}
