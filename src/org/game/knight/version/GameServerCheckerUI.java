package org.game.knight.version;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import org.chw.util.FileUtil;
import org.chw.util.XmlUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
public class GameServerCheckerUI extends Composite implements Runnable
{

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public GameServerCheckerUI(Composite parent, int style)
	{
		super(parent, style);
		
		createContents();
	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}


	
	private static class TreeContentProvider implements ITreeContentProvider
	{
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}

		public void dispose()
		{
		}

		public Object[] getElements(Object inputElement)
		{
			return (Object[]) inputElement;
		}

		public Object[] getChildren(Object parentElement)
		{
			if (parentElement instanceof BoxNode)
			{
				return ((BoxNode) parentElement).areas;
			}
			return new Object[] {};
		}

		public Object getParent(Object element)
		{
			return null;
		}

		public boolean hasChildren(Object element)
		{
			return getChildren(element).length > 0;
		}
	}

	private class TableLabelProvider extends LabelProvider implements ITableLabelProvider
	{
		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

		public String getColumnText(Object element, int columnIndex)
		{
			GameServerChecker.GameArea area = (GameServerChecker.GameArea) element;
			if (columnIndex == 0)
			{
				return area.serverID;
			}
			else if (columnIndex == 1)
			{
				return area.serverName;
			}
			else if (columnIndex == 2)
			{
				return area.version;
			}
			else if (columnIndex == 3)
			{
				return area.host + ":" + area.port + "#" + area.world;
			}

			return element.toString();
		}
	}

	private static class ContentProvider implements IStructuredContentProvider
	{
		public Object[] getElements(Object inputElement)
		{
			return (Object[]) inputElement;
		}

		public void dispose()
		{
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}
	}
	
	private Table table;
	private TableViewer tableViewer;
	private TableColumn tblclmnNewColumn;
	private TableViewerColumn tableViewerColumn;
	private TableColumn tblclmnNewColumn_1;
	private TableViewerColumn tableViewerColumn_1;
	private TableColumn tblclmnNewColumn_2;
	private TableViewerColumn tableViewerColumn_2;
	private TableColumn tblclmnNewColumn_5;
	private TableViewerColumn tableViewerColumn_5;
	private Composite group;
	private Link loading;
	private Link saveTo;
	private Button tableMode;
	private Button tree1Mode;

	/**
	 * Create contents of the window.
	 */
	protected void createContents()
	{
		setLayout(new FillLayout());

		group = new Composite(getParent(), SWT.NONE);
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		composite = new Composite(group, SWT.NONE);
		composite.setLayout(new StackLayout());
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		tblclmnNewColumn_1 = tableViewerColumn_1.getColumn();
		tblclmnNewColumn_1.setWidth(100);
		tblclmnNewColumn_1.setText("ID");

		tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		tblclmnNewColumn = tableViewerColumn.getColumn();
		tblclmnNewColumn.setWidth(100);
		tblclmnNewColumn.setText("\u540D\u79F0");

		tableViewerColumn_5 = new TableViewerColumn(tableViewer, SWT.NONE);
		tblclmnNewColumn_5 = tableViewerColumn_5.getColumn();
		tblclmnNewColumn_5.setWidth(100);
		tblclmnNewColumn_5.setText("\u7248\u672C\u53F7");

		tableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
		tblclmnNewColumn_2 = tableViewerColumn_2.getColumn();
		tblclmnNewColumn_2.setWidth(109);
		tblclmnNewColumn_2.setText("\u670D\u52A1");
		tableViewer.setLabelProvider(new TableLabelProvider());
		tableViewer.setContentProvider(new ContentProvider());

		verTreeViewer = new TreeViewer(composite, SWT.BORDER|SWT.FULL_SELECTION);
		verTree = verTreeViewer.getTree();
		verTree.setLinesVisible(true);
		verTree.setHeaderVisible(true);

		treeViewerColumn = new TreeViewerColumn(verTreeViewer, SWT.NONE);
		trclmnNewColumn = treeViewerColumn.getColumn();
		trclmnNewColumn.setWidth(100);
		trclmnNewColumn.setText("\u7248\u672C");
		treeViewerColumn.setLabelProvider(new ColumnLabelProvider()
		{
			public Image getImage(Object element)
			{
				return null;
			}

			public String getText(Object element)
			{
				if (element instanceof BoxNode)
				{
					return ((BoxNode) element).name;
				}
				if (element instanceof GameServerChecker.GameArea)
				{
					return ((GameServerChecker.GameArea) element).serverName;
				}
				return "";
			}
		});

		treeViewerColumn_3 = new TreeViewerColumn(verTreeViewer, SWT.NONE);
		treeViewerColumn_3.setLabelProvider(new ColumnLabelProvider()
		{
			public Image getImage(Object element)
			{
				return null;
			}

			public String getText(Object element)
			{
				if (element instanceof GameServerChecker.GameArea)
				{
					return ((GameServerChecker.GameArea) element).serverID;
				}
				return "";
			}
		});
		trclmnNewColumn_3 = treeViewerColumn_3.getColumn();
		trclmnNewColumn_3.setWidth(100);
		trclmnNewColumn_3.setText("ID");

		treeViewerColumn_2 = new TreeViewerColumn(verTreeViewer, SWT.NONE);
		trclmnNewColumn_2 = treeViewerColumn_2.getColumn();
		trclmnNewColumn_2.setWidth(100);
		trclmnNewColumn_2.setText("\u670D\u52A1");
		treeViewerColumn_2.setLabelProvider(new ColumnLabelProvider()
		{
			public Image getImage(Object element)
			{
				return null;
			}

			public String getText(Object element)
			{
				if (element instanceof GameServerChecker.GameArea)
				{
					GameServerChecker.GameArea area = (GameServerChecker.GameArea) element;
					return area.host + ":" + area.port + "#" + area.world;
				}
				return "";
			}
		});
		verTreeViewer.setContentProvider(new TreeContentProvider());

		megerTreeViewer = new TreeViewer(composite, SWT.BORDER|SWT.FULL_SELECTION);
		megerTree = megerTreeViewer.getTree();
		megerTree.setLinesVisible(true);
		megerTree.setHeaderVisible(true);

		treeViewerColumn_4 = new TreeViewerColumn(megerTreeViewer, SWT.NONE);
		treeViewerColumn_4.setLabelProvider(new ColumnLabelProvider()
		{
			public Image getImage(Object element)
			{
				return null;
			}

			public String getText(Object element)
			{
				if (element instanceof BoxNode)
				{
					return ((BoxNode) element).name;
				}
				if (element instanceof GameServerChecker.GameArea)
				{
					return ((GameServerChecker.GameArea) element).serverName;
				}
				return "";
			}
		});
		trclmnNewColumn_4 = treeViewerColumn_4.getColumn();
		trclmnNewColumn_4.setWidth(100);
		trclmnNewColumn_4.setText("\u4E16\u754C");

		treeViewerColumn_5 = new TreeViewerColumn(megerTreeViewer, SWT.NONE);
		treeViewerColumn_5.setLabelProvider(new ColumnLabelProvider()
		{
			public Image getImage(Object element)
			{
				return null;
			}

			public String getText(Object element)
			{
				if (element instanceof GameServerChecker.GameArea)
				{
					return ((GameServerChecker.GameArea) element).serverID;
				}
				return "";
			}
		});
		trclmnNewColumn_5 = treeViewerColumn_5.getColumn();
		trclmnNewColumn_5.setWidth(100);
		trclmnNewColumn_5.setText("ID");

		treeViewerColumn_7 = new TreeViewerColumn(megerTreeViewer, SWT.NONE);
		treeViewerColumn_7.setLabelProvider(new ColumnLabelProvider()
		{
			public Image getImage(Object element)
			{
				return null;
			}

			public String getText(Object element)
			{
				if (element instanceof GameServerChecker.GameArea)
				{
					return ((GameServerChecker.GameArea) element).version;
				}
				return "";
			}
		});
		trclmnNewColumn_7 = treeViewerColumn_7.getColumn();
		trclmnNewColumn_7.setWidth(100);
		trclmnNewColumn_7.setText("\u7248\u672C");

		treeViewerColumn_8 = new TreeViewerColumn(megerTreeViewer, SWT.NONE);
		treeViewerColumn_8.setLabelProvider(new ColumnLabelProvider()
		{
			public Image getImage(Object element)
			{
				return null;
			}

			public String getText(Object element)
			{
				if (element instanceof GameServerChecker.GameArea)
				{
					GameServerChecker.GameArea area = (GameServerChecker.GameArea) element;
					return area.host + ":" + area.port + "#" + area.world;
				}
				return "";
			}
		});
		trclmnNewColumn_8 = treeViewerColumn_8.getColumn();
		trclmnNewColumn_8.setWidth(100);
		trclmnNewColumn_8.setText("\u670D\u52A1");
		megerTreeViewer.setContentProvider(new TreeContentProvider());

		Composite composite_2 = new Composite(group, SWT.NONE);
		GridLayout gl_composite_2 = new GridLayout(2, false);
		gl_composite_2.marginHeight = 0;
		gl_composite_2.marginWidth = 0;
		composite_2.setLayout(gl_composite_2);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Composite composite_3 = new Composite(composite_2, SWT.NONE);
		RowLayout rl_composite_3 = new RowLayout(SWT.HORIZONTAL);
		rl_composite_3.spacing = 10;
		composite_3.setLayout(rl_composite_3);
		composite_3.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));

		loading = new Link(composite_3, SWT.NONE);
		loading.setText("<a>\u5F00\u59CB\u8BFB\u53D6</a>");
		loading.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				onLoadingClick();
			}
		});

		saveTo = new Link(composite_3, SWT.NONE);
		saveTo.setText("<a>\u4FDD\u5B58\u5230\u672C\u5730</a>");
		saveTo.setVisible(false);
		saveTo.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				onSaveClick();
			}
		});

		Composite composite_4 = new Composite(composite_2, SWT.NONE);
		RowLayout rl_composite_4 = new RowLayout(SWT.HORIZONTAL);
		rl_composite_4.spacing = 10;
		composite_4.setLayout(rl_composite_4);
		composite_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

		tableMode = new Button(composite_4, SWT.RADIO);
		tableMode.setSelection(true);
		tableMode.setText("\u8868\u683C\u89C6\u56FE");
		tableMode.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (tableMode.getSelection())
				{
					((StackLayout) composite.getLayout()).topControl = table;
					composite.layout();
				}
			}
		});

		tree1Mode = new Button(composite_4, SWT.RADIO);
		tree1Mode.setText("\u7248\u672C\u89C6\u56FE");

		btnRadioButton = new Button(composite_4, SWT.RADIO);
		btnRadioButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				((StackLayout) composite.getLayout()).topControl = megerTree;
				composite.layout();
			}
		});
		btnRadioButton.setText("\u5408\u670D\u89C6\u56FE");
		tree1Mode.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (tree1Mode.getSelection())
				{
					((StackLayout) composite.getLayout()).topControl = verTree;
					composite.layout();
				}
			}
		});

		((StackLayout) composite.getLayout()).topControl = table;
	}

	private GameServerChecker reader = new GameServerChecker();
	private GameServerChecker.GameArea[] datas = null;
	private Tree verTree;
	private TreeViewer verTreeViewer;
	private TreeColumn trclmnNewColumn;
	private TreeViewerColumn treeViewerColumn;
	private TreeColumn trclmnNewColumn_2;
	private TreeViewerColumn treeViewerColumn_2;
	private Composite composite;
	private TreeColumn trclmnNewColumn_3;
	private TreeViewerColumn treeViewerColumn_3;
	private Button btnRadioButton;
	private Tree megerTree;
	private TreeViewer megerTreeViewer;
	private TreeColumn trclmnNewColumn_4;
	private TreeViewerColumn treeViewerColumn_4;
	private TreeColumn trclmnNewColumn_5;
	private TreeViewerColumn treeViewerColumn_5;
	private TreeColumn trclmnNewColumn_7;
	private TreeViewerColumn treeViewerColumn_7;
	private TreeColumn trclmnNewColumn_8;
	private TreeViewerColumn treeViewerColumn_8;

	private void onLoadingClick()
	{
		if (reader.isRuning())
		{
			reader.stop();
		}
		else
		{
			reader.start();
			loading.getDisplay().timerExec(100, this);
		}
	}

	@Override
	public void run()
	{
		if (reader.isRuning())
		{
			loading.setText(reader.getText() + "  <a>取消</a>");
			loading.setToolTipText(reader.getMessage());
			loading.getParent().pack();
			saveTo.setVisible(false);

			loading.getDisplay().timerExec(100, this);
		}
		else
		{
			GameServerChecker.GameArea[] datas = reader.getData();
			if (datas != null)
			{
				this.datas = datas;
			}

			loading.setText("<a>重新读取</a>");
			loading.setToolTipText("");
			loading.getParent().pack();
			saveTo.setVisible(this.datas != null);

			refreshList();
		}
	}

	private void onSaveClick()
	{
		FileDialog dlg = new FileDialog(loading.getShell());
		String path = dlg.open();
		if (path != null)
		{
			File file = new File(path);
			if (file.exists() && file.isFile())
			{
				Hashtable<String, GameServerChecker.GameArea> id_area = new Hashtable<String, GameServerChecker.GameArea>();
				for (GameServerChecker.GameArea area : datas)
				{
					id_area.put(area.serverID, area);
				}

				try
				{
					Document document = (new SAXReader()).read(file);// DocumentHelper.parseText(new
																		// String(FileUtil.getFileBytes(file),"utf8"));
					@SuppressWarnings("rawtypes")
					List nodes = (List) document.selectNodes("/config/*");
					for (Object node : nodes)
					{
						if (node instanceof Element)
						{
							Element element = (Element) node;
							if (element.attribute("id") != null)
							{
								String id = element.attributeValue("id");
								if (id_area.containsKey(id))
								{
									GameServerChecker.GameArea area = id_area.get(id);
									if (element.attribute("name") == null)
									{
										element.addAttribute("name", "");
									}
									if (element.attribute("host") == null)
									{
										element.addAttribute("host", "");
									}
									if (element.attribute("port") == null)
									{
										element.addAttribute("port", "");
									}
									if (element.attribute("world") == null)
									{
										element.addAttribute("world", "");
									}
									element.attribute("name").setValue(area.serverName);
									element.attribute("host").setValue(area.host);
									element.attribute("port").setValue(area.port);
									element.attribute("world").setValue(area.world);

									id_area.remove(id);
								}
							}
						}
					}

					for (GameServerChecker.GameArea area : datas)
					{
						if (id_area.containsKey(area.serverID))
						{
							Element element = document.getRootElement().addElement("serverBack");
							element.addAttribute("id", area.serverID);
							element.addAttribute("name", area.serverName);
							element.addAttribute("host", area.host);
							element.addAttribute("port", area.port);
							element.addAttribute("world", area.world);
						}
					}

					FileUtil.writeFile(file, XmlUtil.formatXML(document));
				}
				catch (DocumentException e)
				{
					e.printStackTrace();
					return;
				}
			}
			else
			{
				StringBuilder text = new StringBuilder();
				text.append("<config>\n");
				for (GameServerChecker.GameArea area : datas)
				{
					text.append(String.format("\t<serverBack id=\"%s\" name=\"%s\" host=\"%s\" port=\"%s\" world=\"%s\"/>\n", area.serverID, area.serverName, area.host, area.port, area.world));
				}
				text.append("<config>");
				text.append(" ");
				FileUtil.writeFile(file, text.toString());
			}
		}
	}

	private void refreshList()
	{
		tableViewer.setInput(this.datas);
		for (TableColumn column : tableViewer.getTable().getColumns())
		{
			column.pack();
		}

		Hashtable<String, ArrayList<GameServerChecker.GameArea>> versionGroup = new Hashtable<String, ArrayList<GameServerChecker.GameArea>>();
		for (GameServerChecker.GameArea area : this.datas)
		{
			String ver = area.version;
			if (!versionGroup.containsKey(ver))
			{
				versionGroup.put(ver, new ArrayList<GameServerChecker.GameArea>());
			}
			versionGroup.get(ver).add(area);
		}
		ArrayList<BoxNode> versionList = new ArrayList<BoxNode>();
		String[] versionKeys = versionGroup.keySet().toArray(new String[versionGroup.size()]);
		Arrays.sort(versionKeys);
		for (String key : versionKeys)
		{
			versionList.add(new BoxNode(key, versionGroup.get(key).toArray(new GameServerChecker.GameArea[versionGroup.get(key).size()])));
		}
		verTreeViewer.setInput(versionList.toArray(new BoxNode[versionList.size()]));
		verTreeViewer.expandAll();
		for (TreeColumn column : verTreeViewer.getTree().getColumns())
		{
			column.pack();
		}

		Hashtable<String, ArrayList<GameServerChecker.GameArea>> worldGroup = new Hashtable<String, ArrayList<GameServerChecker.GameArea>>();
		for (GameServerChecker.GameArea area : this.datas)
		{
			String ver = area.world;
			if (!worldGroup.containsKey(ver))
			{
				worldGroup.put(ver, new ArrayList<GameServerChecker.GameArea>());
			}
			worldGroup.get(ver).add(area);
		}
		ArrayList<BoxNode> worldList = new ArrayList<BoxNode>();
		String[] worldKeys = worldGroup.keySet().toArray(new String[worldGroup.size()]);
		Arrays.sort(worldKeys);
		for (String key : worldKeys)
		{
			worldList.add(new BoxNode(key, worldGroup.get(key).toArray(new GameServerChecker.GameArea[worldGroup.get(key).size()])));
		}
		megerTreeViewer.setInput(worldList.toArray(new BoxNode[worldList.size()]));
		megerTreeViewer.expandAll();
		for (TreeColumn column : megerTreeViewer.getTree().getColumns())
		{
			column.pack();
		}
	}

	private static class BoxNode
	{
		public String name;
		public GameServerChecker.GameArea[] areas;

		public BoxNode(String name, GameServerChecker.GameArea[] areas)
		{
			this.name = name;
			this.areas = areas;
		}
	}
}
