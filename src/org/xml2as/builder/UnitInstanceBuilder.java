package org.xml2as.builder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class UnitInstanceBuilder
{
	private ClassTable classTable;

	private String xpath;

	private Hashtable<String, ArrayList<InstanceField>> path2Field;
	private Stack<ArrayList<InstanceField>> stackFields;

	/**
	 * 构建Instance
	 * 
	 * @param classTable
	 * @param stream
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static Instance build(ClassTable classTable, InputStream stream) throws SAXException, IOException, ParserConfigurationException
	{
		return new UnitInstanceBuilder().exec(classTable, stream);
	}

	/**
	 * 执行转换
	 * 
	 * @param stream
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	private Instance exec(ClassTable classTable, InputStream stream) throws SAXException, IOException, ParserConfigurationException
	{
		this.classTable = classTable;

		this.xpath = null;
		this.path2Field = new Hashtable<String, ArrayList<InstanceField>>();
		this.stackFields = new Stack<ArrayList<InstanceField>>();

		Class mainClass = classTable.getMainClass();
		ClassField mainField = new ClassField(mainClass.xpath, mainClass.name, mainClass.comment, mainClass.name, 3, false, null, false, null);

		InstanceField instanceField = new InstanceField(mainField, "");
		if (!path2Field.containsKey(instanceField.meta.xpath))
		{
			path2Field.put(instanceField.meta.xpath, new ArrayList<InstanceField>());
		}
		path2Field.get(instanceField.meta.xpath).add(instanceField);

		SAXParserFactory.newInstance().newSAXParser().parse(stream, new MyHandler());

		if (instanceField.value == null)
		{
			return new Instance(mainClass);
		}
		return (Instance) instanceField.value;
	}

	private class MyHandler extends DefaultHandler
	{
		@SuppressWarnings("unchecked")
		private void onEnterElement(String xpath)
		{
			// System.out.println(">" + xpath);

			stackFields.push(new ArrayList<InstanceField>());

			ArrayList<InstanceField> selfFields = new ArrayList<InstanceField>();

			ArrayList<InstanceField> fields = path2Field.get(xpath);
			if (fields == null)
			{
				return;
			}

			int size=fields.size();
			for (int i = 0; i < size; i++)
			{
				InstanceField field = fields.get(i);
				
				if (!field.meta.isExtendType())
				{
					continue;
				}

				if (field.value != null && !field.meta.repeted)
				{
					continue;
				}

				Class model = classTable.getClass(field.meta.type);
				if (model == null)
				{
					throw new Error("字段类型未找到! (" + field.meta.type + ")");
				}

				Instance fieldValue = new Instance(model);

				for (ClassField childFieldDef : model.fields)
				{
					String fieldXPath = childFieldDef.xpath.trim();
					if (fieldXPath.length() > 0)
					{
						while (fieldXPath.charAt(0) == '/' || fieldXPath.charAt(0) == '\\')
						{
							fieldXPath = fieldXPath.substring(1);
						}
					}
					if (fieldXPath.isEmpty())
					{
						fieldXPath = ".";
					}

					boolean isSelf = fieldXPath.equals(".");

					String path = isSelf ? xpath : xpath + "/" + childFieldDef.xpath;

					InstanceField childField = new InstanceField(childFieldDef, path);

					fieldValue.fields.add(childField);

					if (!path2Field.containsKey(path))
					{
						path2Field.put(path, new ArrayList<InstanceField>());
					}
					path2Field.get(path).add(childField);

					stackFields.lastElement().add(childField);

					if (isSelf && field.meta.isExtendType())
					{
						selfFields.add(childField);
					}
				}

				if (field.meta.repeted)
				{
					if (field.value == null)
					{
						field.value = new ArrayList<Object>();
					}

					((List<Instance>) field.value).add(fieldValue);
				}
				else
				{
					field.value = fieldValue;
				}
			}

			extendToAllSelf(selfFields);
		}

		private void extendToAllSelf(ArrayList<InstanceField> selfFields)
		{
			if (selfFields == null || selfFields.size() == 0)
			{
				return;
			}

			ArrayList<InstanceField> childs = new ArrayList<InstanceField>();

			for (InstanceField field : selfFields)
			{
				String xpath = field.xpath;

				if (!field.meta.isExtendType())
				{
					continue;
				}

				if (field.value != null && !field.meta.repeted)
				{
					continue;
				}

				Class model = classTable.getClass(field.meta.type);
				if (model == null)
				{
					throw new Error("字段类型未找到! (" + field.meta.type + ")");
				}

				Instance fieldValue = new Instance(model);

				for (ClassField childFieldDef : model.fields)
				{
					String fieldXPath = childFieldDef.xpath.trim();
					if (fieldXPath.length() > 0)
					{
						while (fieldXPath.charAt(0) == '/' || fieldXPath.charAt(0) == '\\')
						{
							fieldXPath = fieldXPath.substring(1);
						}
					}
					if (fieldXPath.isEmpty())
					{
						fieldXPath = ".";
					}

					boolean isSelf = fieldXPath.equals(".");

					String path = isSelf ? xpath : xpath + "/" + childFieldDef.xpath;

					InstanceField childField = new InstanceField(childFieldDef, path);

					fieldValue.fields.add(childField);

					if (!path2Field.containsKey(path))
					{
						path2Field.put(path, new ArrayList<InstanceField>());
					}
					path2Field.get(path).add(childField);

					stackFields.lastElement().add(childField);

					if (isSelf && field.meta.isExtendType())
					{
						childs.add(childField);
					}
				}

				if (field.meta.repeted)
				{
					if (field.value == null)
					{
						field.value = new ArrayList<Object>();
					}

					((List<Instance>) field.value).add(fieldValue);
				}
				else
				{
					field.value = fieldValue;
				}
			}

			extendToAllSelf(childs);
		}

		/**
		 * 处理属性值
		 * 
		 * @param xpath
		 * @param attributeName
		 * @param attributeValue
		 */
		private void onAttribute(String xpath, String attributeName, String attributeValue)
		{
			// System.out.println(" " + xpath + "@" + attributeName + " = " +
			// attributeValue);

			xpath = xpath + "/@" + attributeName;

			ArrayList<InstanceField> fields = path2Field.get(xpath);
			if (fields == null)
			{
				return;
			}

			for (InstanceField field : fields)
			{
				if (field.meta.isExtendType())
				{
					continue;
				}

				if (field.value != null && !field.meta.repeted)
				{
					continue;
				}

				setFieldValue(field, attributeValue);
			}
		}

		/**
		 * 处理文本值
		 * 
		 * @param xpath
		 * @param text
		 */
		private void onText(String xpath, String text)
		{
			// System.out.println(" " + xpath + " = " + text);

			ArrayList<InstanceField> fields = path2Field.get(xpath);
			if (fields == null)
			{
				return;
			}

			for (InstanceField field : fields)
			{
				if (field.meta.isExtendType())
				{
					continue;
				}

				if (field.value != null && !field.meta.repeted)
				{
					continue;
				}

				setFieldValue(field, text);
			}
		}

		/**
		 * 设置字段值
		 * 
		 * @param field
		 * @param text
		 */
		private void setFieldValue(InstanceField field, String text)
		{
			if (text == null)
			{
				return;
			}
			if (field.meta.repeted)
			{
				if (field.value == null)
				{
					field.value = new ArrayList<Object>();
				}

				((List<Object>) field.value).add(parseFieldValue(field, text));
			}
			else
			{
				if (field.meta.slice && !field.meta.isExtendType())
				{
					@SuppressWarnings("rawtypes")
					ArrayList list = new ArrayList();
					String[] parts = text.split(field.meta.sliceChar);
					for (String part : parts)
					{
						list.add(parseFieldValue(field, part));
					}
					field.value = list;
				}
				else
				{
					field.value = parseFieldValue(field, text);
				}
			}
		}

		/**
		 * 解析字段值
		 * 
		 * @param field
		 * @param text
		 * @return
		 */
		private Object parseFieldValue(InstanceField field, String text)
		{
			Object fieldValue = null;

			if (text != null)
			{
				text = text.trim();
			}

			if (field.meta.isBoolean())
			{
				fieldValue = text != null && !text.isEmpty() && !text.equals("false") && !text.equals("0");
			}
			else if (field.meta.isInt() || field.meta.isUint())
			{
				try
				{
					fieldValue = Integer.parseInt(text);
				}
				catch (NumberFormatException err)
				{
					fieldValue = 0;
				}
			}
			else if (field.meta.isNumber())
			{
				try
				{
					fieldValue = Float.parseFloat(text);
				}
				catch (NumberFormatException err)
				{
					fieldValue = 0.0f;
				}
			}
			else if (field.meta.isString())
			{
				fieldValue = text;
			}
			else if (field.meta.isEnumType())
			{
				return classTable.getEnum(field.meta.type).getOrder(text);
			}

			return fieldValue;
		}

		private void onExitElement(String xpath)
		{
			// System.out.println("<" + xpath);

			ArrayList<InstanceField> fields = stackFields.pop();
			for (InstanceField field : fields)
			{
				String path = field.xpath;

				ArrayList<InstanceField> table = path2Field.get(path);
				if (table != null)
				{
					table.remove(field);
				}
			}
		}

		@Override
		public void startDocument() throws SAXException
		{
			xpath = "/";
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
		{
			if (xpath.equals("/"))
			{
				xpath = xpath + qName;
			}
			else
			{
				xpath = xpath + "/" + qName;
			}

			onEnterElement(xpath);

			for (int i = 0; i < attributes.getLength(); i++)
			{
				String attName = attributes.getQName(i);
				String attValue = attributes.getValue(i);

				onAttribute(xpath, attName, attValue);
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException
		{
			String text = new String(ch, start, length);
			text = text.trim();
			if (!text.isEmpty())
			{
				onText(xpath, text);
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException
		{
			onExitElement(xpath);
			xpath = xpath.substring(0, xpath.length() - qName.length() - 1);
		}

		@Override
		public void endDocument() throws SAXException
		{
			super.endDocument();
		}
	}

}
