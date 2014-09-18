package org.xml2as.builder;

public class Class
{
	/**
	 * 输入路径
	 */
	public final String xpath;

	/**
	 * 类名
	 */
	public final String name;

	/**
	 * 注释
	 */
	public final String comment;

	/**
	 * 字段列表
	 */
	public final ClassField[] fields;

	/**
	 * 类型ID
	 */
	public final int order;

	/**
	 * 构造函数
	 * 
	 * @param filePath
	 * @param xpath
	 * @param packName
	 * @param name
	 * @param comment
	 */
	public Class(String xpath, String name, String comment, int order, ClassField[] fields)
	{
		this.xpath = xpath;

		this.name = name;
		this.comment = comment;
		this.order = order;
		this.fields = fields;
	}

	/**
	 * 按字段名查找字段
	 * 
	 * @param id
	 * @return
	 */
	public ClassField getField(String name)
	{
		for (ClassField field : fields)
		{
			if (field.name.equals(name))
			{
				return field;
			}
		}
		return null;
	}
}