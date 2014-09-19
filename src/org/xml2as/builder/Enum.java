package org.xml2as.builder;

public class Enum
{
	/**
	 * ע��
	 */
	public final String comment;

	/**
	 * ö������
	 */
	public final String name;

	/**
	 * ö���ֶ�
	 */
	public final EnumField[] fields;

	/**
	 * ���캯��
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
	 * ��ֵ��ȡ���
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
