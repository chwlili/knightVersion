package org.xml2as.builder;

public class Class
{
	/**
	 * �����ļ�
	 */
	public final String filePath;

	/**
	 * ����·��
	 */
	public final String xpath;

	/**
	 * ����
	 */
	public final String packName;

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
	public Class(String filePath, String xpath, String packName, String name, String comment, int order, ClassField[] fields)
	{
		this.filePath = filePath;
		this.xpath = xpath;

		this.packName = packName;
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