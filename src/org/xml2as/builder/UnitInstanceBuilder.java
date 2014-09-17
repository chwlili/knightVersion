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
	public static Instance[] build(ClassTable classTable, InputStream stream) throws SAXException, IOException, ParserConfigurationException
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
	private Instance[] exec(ClassTable classTable, InputStream stream) throws SAXException, IOException, ParserConfigurationException
	{
		this.classTable = classTable;

		this.xpath = null;
		this.path2Field = new Hashtable<String, ArrayList<InstanceField>>();
		this.stackFields = new Stack<ArrayList<InstanceField>>();

		ArrayList<ClassField> fields = new ArrayList<ClassField>();
		for (Class clazz : classTable.getAllMainClass())
		{
			fields.add(new ClassField(clazz.xpath, clazz.name, clazz.comment, clazz.name));
		}

		if (fields.size() > 0)
		{
			Instance instance = new Instance(new Class("", "", "", "", "", 0, fields.toArray(new ClassField[] {})));
			for (ClassField field : instance.type.fields)
			{
				InstanceField instanceField = new InstanceField(field, "");
				if (!path2Field.containsKey(instanceField.meta.xpath))
				{
					path2Field.put(instanceField.meta.xpath, new ArrayList<InstanceField>());
				}
				path2Field.get(instanceField.meta.xpath).add(instanceField);
				instance.fields.add(instanceField);
			}

			SAXParserFactory.newInstance().newSAXParser().parse(stream, new MyHandler());

			ArrayList<Instance> result = new ArrayList<Instance>();
			for (InstanceField field : instance.fields)
			{
				result.add((Instance) field.value);
			}
			return result.toArray(new Instance[] {});
		}

		return new Instance[] {};
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

			for (InstanceField field : fields)
			{
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

					if (!isSelf)
					{
						if (!path2Field.containsKey(path))
						{
							path2Field.put(path, new ArrayList<InstanceField>());
						}
						path2Field.get(path).add(childField);

						stackFields.lastElement().add(childField);
					}
					else
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

					if (!isSelf)
					{
						if (!path2Field.containsKey(path))
						{
							path2Field.put(path, new ArrayList<InstanceField>());
						}
						path2Field.get(path).add(childField);

						stackFields.lastElement().add(childField);
					}
					else
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

		@SuppressWarnings("unchecked")
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

				Object fieldValue = null;

				if (field.meta.isBoolean())
				{
					fieldValue = attributeValue != null && !attributeValue.trim().isEmpty() && !attributeValue.trim().equals("false") && !attributeValue.trim().equals("0");
				}
				else if (field.meta.isInt() || field.meta.isUint())
				{
					try
					{
						fieldValue = Integer.parseInt(attributeValue);
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
						fieldValue = Float.parseFloat(attributeValue);
					}
					catch (NumberFormatException err)
					{
						fieldValue = 0;
					}
				}
				else if (field.meta.isString())
				{
					fieldValue = attributeValue;
				}

				if (field.meta.repeted)
				{
					if (field.value == null)
					{
						field.value = new ArrayList<Object>();
					}

					((List<Object>) field.value).add(fieldValue);
				}
				else
				{
					field.value = fieldValue;
				}
			}
		}

		@SuppressWarnings("unchecked")
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

				Object fieldValue = null;

				if (field.meta.isBoolean())
				{
					fieldValue = text != null && !text.trim().isEmpty() && !text.trim().equals("false") && !text.trim().equals("0");
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
						fieldValue = Float.NaN;
					}
				}
				else if (field.meta.isString())
				{
					fieldValue = text;
				}

				if (field.meta.repeted)
				{
					if (field.value == null)
					{
						field.value = new ArrayList<Object>();
					}

					((List<Object>) field.value).add(fieldValue);
				}
				else
				{
					field.value = fieldValue;
				}
			}
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
