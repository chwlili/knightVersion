package org.xml2as.builder;

public class ClassField
{
	/**
	 * ����·��
	 */
	public final String xpath;

	/**
	 * �ֶ�����
	 */
	public final String name;

	/**
	 * �ֶ�ע��
	 */
	public final String comment;

	/**
	 * �ֶ�����
	 */
	public final String type;

	/**
	 * �Ƿ�Ϊ�б�
	 */
	public final Boolean repeted;

	/**
	 * �����ֶ�
	 */
	public final String[] indexKeys;

	/**
	 * �Ƿ�Ϊ��Ƭ�б�
	 */
	public final Boolean slice;

	/**
	 * ��Ƭʱʹ�õķָ��ַ�
	 */
	public final String sliceChar;

	/**
	 * ���캯��
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
		this(xpath, name, comment, type, repeted, indexKeys, false, null);
	}

	/**
	 * �ι��캯��
	 * 
	 * @param xpath
	 * @param name
	 * @param comment
	 * @param type
	 * @param repeted
	 * @param indexKeys
	 */
	public ClassField(String xpath, String name, String comment, String type, boolean slice, String sliceChar)
	{
		this(xpath, name, comment, type, false, null, slice, sliceChar);
	}

	/**
	 * ���캯��
	 * 
	 * @param xpath
	 * @param name
	 * @param comment
	 * @param type
	 * @param repeted
	 * @param indexKeys
	 */
	public ClassField(String xpath, String name, String comment, String type, boolean repeted, String[] indexKeys, boolean slice, String sliceChar)
	{
		this.xpath = xpath;
		this.name = name;
		this.comment = comment;
		this.type = type;
		this.repeted = repeted;
		this.indexKeys = indexKeys;
		this.slice = slice;
		this.sliceChar = sliceChar;
	}

	/**
	 * �Ƿ�Ϊboolean
	 * 
	 * @return
	 */
	public boolean isBoolean()
	{
		return type.equals("Boolean");
	}

	/**
	 * �Ƿ�Ϊint
	 * 
	 * @return
	 */
	public boolean isInt()
	{
		return type.equals("int");
	}

	/**
	 * �Ƿ�Ϊuint
	 * 
	 * @return
	 */
	public boolean isUint()
	{
		return type.equals("uint");
	}

	/**
	 * �Ƿ�ΪNumber
	 * 
	 * @return
	 */
	public boolean isNumber()
	{
		return type.equals("Number");
	}

	/**
	 * �Ƿ�ΪString
	 * 
	 * @return
	 */
	public boolean isString()
	{
		return type.equals("String");
	}

	/**
	 * �Ƿ�Ϊ�Զ�������
	 * 
	 * @return
	 */
	public boolean isExtendType()
	{
		return !(isBoolean() || isInt() || isUint() || isNumber() || isString());
	}
}