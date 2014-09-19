package org.xml2as.builder;

public class Enum
{
	/**
	 * 注释
	 */
	public final String comment;

	/**
	 * 枚举名称
	 */
	public final String name;

	/**
	 * 枚举字段
	 */
	public final EnumField[] fields;

	/**
	 * 构造函数
	 * 
	 * @param name
	 * @param fields
	 */
	public Enum(String comment, String name, EnumField[] fields)
	{
		this.comment = comment;
		this.name = name;
		this.fields = fields;
	}

	/**
	 * 按值获取序号
	 * 
	 * @param text
	 * @return
	 */
	public int getOrder(String text)
	{
		if (text != null)
		{
			for (EnumField field : fields)
			{
				if (text.equals(field.value))
				{
					return field.order;
				}
			}
		}
		return 0;
	}
}
