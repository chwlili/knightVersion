package org.xml2as.builder;

class InstanceField
{
	/**
	 * 元数据
	 */
	public final ClassField meta;

	/**
	 * 输入路径
	 */
	public final String xpath;

	/**
	 * 值
	 */
	public Object value;

	/**
	 * 构造函数
	 * 
	 * @param def
	 * @param xpath
	 */
	public InstanceField(ClassField def, String xpath)
	{
		this.meta = def;
		this.xpath = xpath;
	}
}