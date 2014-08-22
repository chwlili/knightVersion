package org.game.knight.version.packer.world.output3d;

import org.game.knight.version.packer.world.model.ImageFrame;

public class SliceImage
{
	/**
	 * ԭʼͼ��֡
	 */
	public final ImageFrame frame;

	/**
	 * Ԥ��ͼ·��
	 */
	public final String previewURL;

	/**
	 * ��Ƭ����
	 */
	public final int sliceRow;

	/**
	 * ��Ƭ����
	 */
	public final int sliceCol;

	/**
	 * ��Ƭͼ��URL�б�
	 */
	public final String[] sliceURLs;

	/**
	 * ���캯��
	 * 
	 * @param img
	 * @param previewURL
	 * @param sliceRow
	 * @param sliceCol
	 * @param sliceURLs
	 */
	public SliceImage(ImageFrame frame, String previewURL, int sliceRow, int sliceCol, String[] sliceURLs)
	{
		this.frame = frame;
		this.previewURL = previewURL;
		this.sliceRow = sliceRow;
		this.sliceCol = sliceCol;
		this.sliceURLs = sliceURLs;
	}
}
