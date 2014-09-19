package org.xml2as.builder;

public class EnumField
{
	/**
	 * ע��
	 */
	public final String comment;

	/**
	 * ����
	 */
	public final String name;

	/**
	 * ֵ
	 */
	public final String value;

	/**
	 * ���
	 */
	public final int order;

	/**
	 * ���캯��
	 * 
	 * @param name
	 * @param value
	 */
	public EnumField(String comment, String name, String value, int order)
	{
		this.comment = comment;
		this.name = name;
		this.value = value;
		this.order = order;
	}
}
