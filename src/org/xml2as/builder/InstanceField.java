package org.xml2as.builder;

class InstanceField
{
	/**
	 * Ԫ����
	 */
	public final ClassField meta;

	/**
	 * ����·��
	 */
	public final String xpath;

	/**
	 * ֵ
	 */
	public Object value;

	/**
	 * ���캯��
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