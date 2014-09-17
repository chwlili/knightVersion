package org.xml2as.builder;

public class ClassField
{
	/**
	 * 输入路径
	 */
	public final String xpath;

	/**
	 * 字段名称
	 */
	public final String name;

	/**
	 * 字段注释
	 */
	public final String comment;

	/**
	 * 字段类型
	 */
	public final String type;

	/**
	 * 是否为列表
	 */
	public final Boolean repeted;

	/**
	 * 主键字段
	 */
	public final String[] indexKeys;

	/**
	 * 单值字段构造函数
	 * 
	 * @param xpath
	 * @param name
	 * @param comment
	 * @param type
	 */
	public ClassField(String xpath, String name, String comment, String type)
	{
		this(xpath, name, comment, type, false, null);
	}

	/**
	 * 多值字段构造函数
	 * 
	 * @param xpath
	 * @param name
	 * @param comment
	 * @param type
	 * @param repeted
	 */
	public ClassField(String xpath, String name, String comment, String type, boolean repeted)
	{
		this(xpath, name, comment, type, repeted, null);
	}

	/**
	 * 多值带索引字段构造函数
	 * 
	 * @param xpath
	 * @param name
	 * @param comment
	 * @param type
	 * @param repeted
	 * @param indexKeys
	 */
	public ClassField(String xpath, String name, String comment, String type, boolean repeted, String[] indexKeys)
	{
		this.xpath = xpath;
		this.name = name;
		this.comment = comment;
		this.type = type;
		this.repeted = repeted;
		this.indexKeys = indexKeys;
	}

	/**
	 * 是否为boolean
	 * 
	 * @return
	 */
	public boolean isBoolean()
	{
		return type.equals("Boolean");
	}

	/**
	 * 是否为int
	 * 
	 * @return
	 */
	public boolean isInt()
	{
		return type.equals("int");
	}

	/**
	 * 是否为uint
	 * 
	 * @return
	 */
	public boolean isUint()
	{
		return type.equals("uint");
	}

	/**
	 * 是否为Number
	 * 
	 * @return
	 */
	public boolean isNumber()
	{
		return type.equals("Number");
	}

	/**
	 * 是否为String
	 * 
	 * @return
	 */
	public boolean isString()
	{
		return type.equals("String");
	}

	/**
	 * 是否为自定义类型
	 * 
	 * @return
	 */
	public boolean isExtendType()
	{
		return !(isBoolean() || isInt() || isUint() || isNumber() || isString());
	}
}