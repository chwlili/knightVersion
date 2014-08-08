package org.game.knight.version.packer.world.model;

import java.io.File;

public class AtfParam
{
	/**
	 * �����ļ�
	 */
	public final File file;
	/**
	 * ID
	 */
	public final String id;
	/**
	 * ��Ȳ���
	 */
	public final int width;
	/**
	 * �߶Ȳ���
	 */
	public final int height;
	/**
	 * ��������
	 */
	public final String other;

	/**
	 * ���캯��
	 * @param file
	 * @param id
	 * @param w
	 * @param h
	 * @param param
	 */
	public AtfParam(File file,String id,int width,int height,String other)
	{
		this.file=file;
		this.id=id;
		this.width=width;
		this.height=height;
		this.other=other;
	}
}