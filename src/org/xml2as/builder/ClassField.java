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
	 * 字段类别
	 */
	private final int typeKind;

	/**
	 * 是否为列表
	 */
	public final Boolean repeted;

	/**
	 * 主键字段
	 */
	public final String[] indexKeys;

	/**
	 * 是否为切片列表
	 */
	public final Boolean slice;

	/**
	 * 切片时使用的分割字符
	 */
	public final String sliceChar;

	/**
	 * 需要多语言支持
	 */
	public final boolean NLS;

	/**
	 * 构造函数
	 * 
	 * @param xpath
	 * @param name
	 * @param comment
	 * @param type
	 * @param repeted
	 * @param indexKeys
	 */
	public ClassField(String xpath, String name, String comment, String type, int typeKind, boolean repeted, String[] indexKeys)
	{
		this(xpath, name, comment, type, typeKind, repeted, indexKeys, false, null, false);
	}

	/**
	 * 段构造函数
	 * 
	 * @param xpath
	 * @param name
	 * @param comment
	 * @param type
	 * @param repeted
	 * @param indexKeys
	 */
	public ClassField(String xpath, String name, String comment, String type, int typeKind, boolean slice, String sliceChar)
	{
		this(xpath, name, comment, type, typeKind, false, null, slice, sliceChar, false);
	}

	/**
	 * 构造函数
	 * 
	 * @param xpath
	 * @param name
	 * @param comment
	 * @param type
	 * @param repeted
	 * @param indexKeys
	 */
	public ClassField(String xpath, String name, String comment, String type, int typeKind, boolean repeted, String[] indexKeys, boolean slice, String sliceChar, boolean NLS)
	{
		this.xpath = xpath;
		this.name = name;
		this.comment = comment;
		this.type = type;
		this.typeKind = typeKind;
		this.repeted = repeted;
		this.indexKeys = indexKeys;
		this.slice = slice;
		this.sliceChar = sliceChar;
		this.NLS = NLS;
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
	 * 是否为基础类型
	 * 
	 * @return
	 */
	public boolean isBaseType()
	{
		return typeKind == 1;
	}

	/**
	 * 是否为枚举类型
	 * 
	 * @return
	 */
	public boolean isEnumType()
	{
		return typeKind == 2;
	}

	/**
	 * 是否为自定义类型
	 * 
	 * @return
	 */
	public boolean isExtendType()
	{
		return typeKind == 3;
	}

	/**
	 * 是否有索引
	 * 
	 * @return
	 */
	public boolean hasIndex()
	{
		return indexKeys != null && indexKeys.length > 0;
	}
}