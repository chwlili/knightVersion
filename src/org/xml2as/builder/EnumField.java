package org.xml2as.builder;

public class EnumField
{
	/**
	 * 注释
	 */
	public final String comment;

	/**
	 * 名称
	 */
	public final String name;

	/**
	 * 值
	 */
	public final String value;

	/**
	 * 序号
	 */
	public final int order;

	/**
	 * 构造函数
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
