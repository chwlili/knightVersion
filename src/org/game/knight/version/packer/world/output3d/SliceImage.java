package org.game.knight.version.packer.world.output3d;

import org.game.knight.version.packer.world.model.ProjectImgFile;

public class SliceImage
{
	/**
	 * 原始图像
	 */
	public final ProjectImgFile img;

	/**
	 * 预览图路径
	 */
	public final String previewURL;

	/**
	 * 切片行数
	 */
	public final int sliceRow;

	/**
	 * 切片列数
	 */
	public final int sliceCol;

	/**
	 * 切片图像URL列表
	 */
	public final String[] sliceURLs;

	/**
	 * 构造函数
	 * @param img
	 * @param previewURL
	 * @param sliceRow
	 * @param sliceCol
	 * @param sliceURLs
	 */
	public SliceImage(ProjectImgFile img, String previewURL, int sliceRow, int sliceCol, String[] sliceURLs)
	{
		this.img = img;
		this.previewURL = previewURL;
		this.sliceRow = sliceRow;
		this.sliceCol = sliceCol;
		this.sliceURLs = sliceURLs;
	}
}
