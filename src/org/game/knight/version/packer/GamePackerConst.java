package org.game.knight.version.packer;

import java.io.File;

public class GamePackerConst
{
	/**
	 * 每个目录的文件数
	 */
	public static final int FILE_COUNT_EACH_DIR=1000;

	/**
	 * 获取JAR目录
	 * @return
	 */
	public static File getJarDir()
	{
		return new File(System.getProperty("user.home") + File.separatorChar + "GamePacker");
	}
}
