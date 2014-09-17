package org.xml2as.builder;

import java.util.ArrayList;

class Instance
{
	/**
	 * 类型定义
	 */
	public final Class type;

	/**
	 * 字段列表
	 */
	public final ArrayList<InstanceField> fields = new ArrayList<InstanceField>();

	/**
	 * 构造函数
	 * 
	 * @param type
	 */
	public Instance(Class type)
	{
		this.type = type;
	}

	/**
	 * 查找字段
	 * 
	 * @param name
	 * @return
	 */
	public InstanceField getField(String name)
	{
		for (InstanceField field : fields)
		{
			if (field.meta.name.equals(name))
			{
				return field;
			}
		}
		return null;
	}
}