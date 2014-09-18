package org.xml2as.builder;

public class Class
{
	/**
	 * ����·��
	 */
	public final String xpath;

	/**
	 * ����
	 */
	public final String name;

	/**
	 * ע��
	 */
	public final String comment;

	/**
	 * �ֶ��б�
	 */
	public final ClassField[] fields;

	/**
	 * ����ID
	 */
	public final int order;

	/**
	 * ���캯��
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
	 * ���ֶ��������ֶ�
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