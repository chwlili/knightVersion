package org.game.knight.version.packer;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import org.chw.util.FileUtil;
import org.chw.util.XmlUtil;
import org.chw.util.ZlibUtil;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.game.knight.version.AppSetting;
import org.game.knight.version.packer.cfg.ConfigExporter;
import org.game.knight.version.packer.files.FilesExporter;
import org.game.knight.version.packer.game.GameExporter;
import org.game.knight.version.packer.icon.IconExporter;
import org.game.knight.version.packer.view.ViewExport;
import org.game.knight.version.packer.world.WorldExporter;


public class GamePacker extends Composite
{
	private Text cfgInput;
	private Text fileInput;
	private Text iconInput;
	private Text viewInput;
	private Text worldInput;
	private Text codeInput;
	private Text idcInput;
	private Text cdnInput;
	private Text verInput;
	private Button cfgSelection;
	private Button fileSelection;
	private Button iconSelection;
	private Button viewSelection;
	private Button worldSelection;
	private Button codeSelection;
	private Link cfgLabel;
	private Link codeLabel;
	private Link worldLabel;
	private Link viewLabel;
	private Link iconLabel;
	private Link fileLabel;
	private Button fileButton;
	private Button iconButton;
	private Button viewButton;
	private Button worldButton;
	private Button codeButton;
	private Button cdnButton;
	private Button idcButton;
	private Link idcLabel;
	private Button cfgButton;
	private Link cdnLabel;
	private Group params;
	private Button submitButton;
	private Button zlibSelection;
	private Button clearupSelection;
	private Button mobileSelection;
	private Button writeRegionImgSelection;
	private Composite settingPage;
	private Composite outputPage;
	private TreeColumn trclmnNewColumn;
	private TreeViewerColumn treeViewerColumn;
	private Button cancelButton;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public GamePacker(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new StackLayout());

		settingPage = new Composite(this, SWT.NONE);
		settingPage.setLayout(new GridLayout(1, false));

		scrolledComposite = new ScrolledComposite(settingPage, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gd_scrolledComposite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_scrolledComposite.heightHint = 463;
		scrolledComposite.setLayoutData(gd_scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		composite = new Composite(scrolledComposite, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		Group inputs = new Group(composite, SWT.NONE);
		inputs.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		inputs.setSize(440, 237);
		GridLayout gl_inputs = new GridLayout(4, false);
		gl_inputs.marginHeight = 15;
		gl_inputs.marginWidth = 20;
		inputs.setLayout(gl_inputs);
		inputs.setText("����");

		cfgSelection = new Button(inputs, SWT.CHECK);

		cfgLabel = new Link(inputs, SWT.NONE);
		cfgLabel.setText("<a>����</a>��");

		cfgInput = new Text(inputs, SWT.BORDER);
		cfgInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		cfgButton = new Button(inputs, SWT.NONE);
		cfgButton.setText("    ...    ");

		fileSelection = new Button(inputs, SWT.CHECK);

		fileLabel = new Link(inputs, SWT.NONE);
		fileLabel.setText("<a>�ļ�</a>��");

		fileInput = new Text(inputs, SWT.BORDER);
		fileInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		fileButton = new Button(inputs, SWT.NONE);
		fileButton.setText("    ...    ");

		iconSelection = new Button(inputs, SWT.CHECK);

		iconLabel = new Link(inputs, SWT.NONE);
		iconLabel.setText("<a>ͼ��</a>��");

		iconInput = new Text(inputs, SWT.BORDER);
		iconInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		iconButton = new Button(inputs, SWT.NONE);
		iconButton.setText("    ...    ");

		viewSelection = new Button(inputs, SWT.CHECK);

		viewLabel = new Link(inputs, SWT.NONE);
		viewLabel.setText("<a>��ͼ</a>��");

		viewInput = new Text(inputs, SWT.BORDER);
		viewInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		viewButton = new Button(inputs, SWT.NONE);
		viewButton.setText("    ...    ");

		worldSelection = new Button(inputs, SWT.CHECK);

		worldLabel = new Link(inputs, SWT.NONE);
		worldLabel.setText("<a>����</a>��");

		worldInput = new Text(inputs, SWT.BORDER);
		worldInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		worldButton = new Button(inputs, SWT.NONE);
		worldButton.setText("    ...    ");

		codeSelection = new Button(inputs, SWT.CHECK);

		codeLabel = new Link(inputs, SWT.NONE);
		codeLabel.setText("<a>����</a>��");

		codeInput = new Text(inputs, SWT.BORDER);
		codeInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		codeButton = new Button(inputs, SWT.NONE);
		codeButton.setText("    ...    ");

		Group outputs = new Group(composite, SWT.NONE);
		outputs.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		outputs.setSize(440, 201);
		GridLayout gl_outputs = new GridLayout(3, false);
		gl_outputs.marginHeight = 15;
		gl_outputs.marginWidth = 20;
		outputs.setLayout(gl_outputs);
		outputs.setText("���");

		Link link_9 = new Link(outputs, SWT.NONE);
		link_9.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		link_9.setText("<a>�汾��</a>��");

		verInput = new Text(outputs, SWT.BORDER);
		verInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(outputs, SWT.NONE);

		cdnLabel = new Link(outputs, SWT.NONE);
		cdnLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		cdnLabel.setText("<a>��Դ��</a>��");

		cdnInput = new Text(outputs, SWT.BORDER);
		cdnInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		cdnButton = new Button(outputs, SWT.NONE);
		cdnButton.setText("    ...    ");

		idcLabel = new Link(outputs, SWT.NONE);
		idcLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		idcLabel.setText("<a>���ÿ�</a>��");

		idcInput = new Text(outputs, SWT.BORDER);
		idcInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		idcButton = new Button(outputs, SWT.NONE);
		idcButton.setText("    ...    ");

		startupLabel = new Link(outputs, SWT.NONE);
		startupLabel.setText("<a>������</a>��");

		startupInput = new Text(outputs, SWT.BORDER);
		startupInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		startupButton = new Button(outputs, SWT.NONE);
		startupButton.setText("    ...    ");
		new Label(outputs, SWT.NONE);

		paramInput = new Text(outputs, SWT.BORDER);
		paramInput.setEditable(false);
		paramInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		paramButton = new Button(outputs, SWT.NONE);
		paramButton.setText("    ...    ");

		params = new Group(composite, SWT.NONE);
		params.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		params.setSize(440, 11);
		GridLayout gl_params = new GridLayout(5, false);
		gl_params.marginHeight = 15;
		params.setLayout(gl_params);
		params.setText("����");

		zlibSelection = new Button(params, SWT.CHECK);
		zlibSelection.setText("\u538B\u7F29XML");
		
		swfSelection = new Button(params, SWT.CHECK);
		swfSelection.setText("\u538B\u7F29\u89C6\u56FEImg");

		clearupSelection = new Button(params, SWT.CHECK);
		clearupSelection.setText("\u6E05\u7406");

		mobileSelection = new Button(params, SWT.CHECK);
		mobileSelection.setText("\u624B\u673A\u914D\u7F6E\u683C\u5F0F");
		
		writeRegionImgSelection=new Button(params,SWT.CHECK);
		writeRegionImgSelection.setText("���Сͼ");
		
		scrolledComposite.setContent(composite);
		scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		submitButton = new Button(settingPage, SWT.NONE);
		submitButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		submitButton.setSize(72, 27);
		submitButton.setText("    ȷ ��    ");

		outputPage = new Composite(this, SWT.NONE);
		GridLayout gl_outputPage = new GridLayout(1, false);
		outputPage.setLayout(gl_outputPage);

		treeViewer = new TreeViewer(outputPage, SWT.BORDER);
		Tree tree = treeViewer.getTree();
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		treeViewer.setContentProvider(new TaskContentProvider());
		treeViewer.setLabelProvider(new TaskLabelProvider());

		treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
		trclmnNewColumn = treeViewerColumn.getColumn();
		trclmnNewColumn.setWidth(434);
		trclmnNewColumn.setText(" ");
		treeViewerColumn.setLabelProvider(new TaskCellLabelProvider());

		cancelButton = new Button(outputPage, SWT.NONE);
		cancelButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		cancelButton.setText("    ȡ ��    ");

		initInputHandler();

		showSettingPage();
	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

	// --------------------------------------------------------------------------------
	//
	// �������Ŀ¼
	//
	// --------------------------------------------------------------------------------

	/**
	 * ��ʼ���������ѡ��
	 */
	private void initInputHandler()
	{
		//���ڹر�
		getShell().addDisposeListener(new DisposeListener()
		{
			@Override
			public void widgetDisposed(DisposeEvent e)
			{
				cancelExport();
			}
		});
		
		// �����ǩ
		cfgLabel.addSelectionListener(new DirOpenHandler(cfgInput));
		fileLabel.addSelectionListener(new DirOpenHandler(fileInput));
		iconLabel.addSelectionListener(new DirOpenHandler(iconInput));
		viewLabel.addSelectionListener(new DirOpenHandler(viewInput));
		worldLabel.addSelectionListener(new DirOpenHandler(worldInput));
		codeLabel.addSelectionListener(new DirOpenHandler(codeInput));

		// ���밴ť
		cfgButton.addSelectionListener(new DirSelectionHandler(cfgInput));
		fileButton.addSelectionListener(new DirSelectionHandler(fileInput));
		iconButton.addSelectionListener(new DirSelectionHandler(iconInput));
		viewButton.addSelectionListener(new DirSelectionHandler(viewInput));
		worldButton.addSelectionListener(new DirSelectionHandler(worldInput));
		codeButton.addSelectionListener(new DirSelectionHandler(codeInput));

		// �����ǩ
		cdnLabel.addSelectionListener(new DirOpenHandler(cdnInput));
		idcLabel.addSelectionListener(new DirOpenHandler(idcInput));
		startupLabel.addSelectionListener(new DirOpenHandler(startupInput));

		// ���밴ť
		cdnButton.addSelectionListener(new DirSelectionHandler(cdnInput));
		idcButton.addSelectionListener(new DirSelectionHandler(idcInput));
		startupButton.addSelectionListener(new DirSelectionHandler(startupInput));
		paramButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				GamePackerStartupDialog dialog = new GamePackerStartupDialog(getShell());
				if (dialog.open(paramInput.getText()) == 0)
				{
					paramInput.setText(dialog.getServerHost() + " " + dialog.getServerPort() + " " + dialog.getServerID() + " " + dialog.getTests() + " " + dialog.getUsers());
				}
			}
		});

		// �ύ��ť
		submitButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				toExport();
			}
		});

		// ȡ����ť
		cancelButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				toSetting();
			}
		});
	}

	/**
	 * Ŀ¼ѡ������
	 * 
	 * @author chw
	 * 
	 */
	private class DirSelectionHandler extends SelectionAdapter
	{
		private Text input;

		public DirSelectionHandler(Text input)
		{
			this.input = input;
		}

		@Override
		public void widgetSelected(SelectionEvent e)
		{
			DirectoryDialog dir = new DirectoryDialog(getShell());
			String path = dir.open();
			if (path != null)
			{
				input.setText(path);
			}
		}
	};

	/**
	 * Ŀ¼�򿪴�����
	 * 
	 * @author chw
	 * 
	 */
	private class DirOpenHandler extends SelectionAdapter
	{
		private Text input;

		public DirOpenHandler(Text input)
		{
			this.input = input;
		}

		@Override
		public void widgetSelected(SelectionEvent e)
		{
			String path = this.input.getText();

			if (path == null || path.isEmpty()) { return; }

			File file = new File(path);
			if (!file.exists() || !file.isDirectory()) { return; }

			try
			{
				Process p = Runtime.getRuntime().exec("cmd /c explorer " + file.getPath());
				BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "GBK"));
				String msg = null;
				while ((msg = br.readLine()) != null)
				{
					System.out.println(msg);
				}

				BufferedReader errorOutput = new BufferedReader(new InputStreamReader(p.getErrorStream(), "GBK"));
				String error = null;
				while ((error = errorOutput.readLine()) != null)
				{
					System.out.println(error);
				}

				br.close();
				p.destroy();
			}
			catch (Exception err)
			{
				err.printStackTrace();
			}
		}
	}

	// --------------------------------------------------------------------------------
	//
	// ��ʷ��¼��Ϣ
	//
	// --------------------------------------------------------------------------------

	private AppSetting setting;
	private IDialogSettings section;

	/**
	 * ��ʼ���趨
	 * 
	 * @param setting
	 * @param sectionName
	 */
	public void initSetting(AppSetting setting, String sectionName)
	{
		this.setting = setting;

		section = setting.getSection(sectionName);
		if (section == null)
		{
			section = setting.addNewSection(sectionName);
		}

		cfgSelection.setSelection(section.getBoolean("cfgSelection"));
		cfgInput.setText(section.get("cfgInput") != null ? section.get("cfgInput") : "");

		fileSelection.setSelection(section.getBoolean("fileSelection"));
		fileInput.setText(section.get("fileInput") != null ? section.get("fileInput") : "");

		iconSelection.setSelection(section.getBoolean("iconSelection"));
		iconInput.setText(section.get("iconInput") != null ? section.get("iconInput") : "");

		viewSelection.setSelection(section.getBoolean("viewSelection"));
		viewInput.setText(section.get("viewInput") != null ? section.get("viewInput") : "");

		worldSelection.setSelection(section.getBoolean("worldSelection"));
		worldInput.setText(section.get("worldInput") != null ? section.get("worldInput") : "");

		codeSelection.setSelection(section.getBoolean("codeSelection"));
		codeInput.setText(section.get("codeInput") != null ? section.get("codeInput") : "");

		verInput.setText(section.get("verInput") != null ? section.get("verInput") : "");
		cdnInput.setText(section.get("cdnInput") != null ? section.get("cdnInput") : "");
		idcInput.setText(section.get("idcInput") != null ? section.get("idcInput") : "");
		startupInput.setText(section.get("startupInput") != null ? section.get("startupInput") : "");
		paramInput.setText(section.get("paramInput") != null ? section.get("paramInput") : "");

		zlibSelection.setSelection(section.getBoolean("zlibSelection"));
		swfSelection.setSelection(section.getBoolean("imgZipSelection"));
		mobileSelection.setSelection(section.getBoolean("mobileSelection"));
		clearupSelection.setSelection(section.getBoolean("clearupSelection"));
		writeRegionImgSelection.setSelection(section.getBoolean("writeRegionImgSelection"));
	}

	/**
	 * �����趨
	 */
	private void saveSetting()
	{
		section.put("cfgSelection", cfgSelection.getSelection());
		section.put("cfgInput", cfgInput.getText());

		section.put("fileSelection", fileSelection.getSelection());
		section.put("fileInput", fileInput.getText());

		section.put("iconSelection", iconSelection.getSelection());
		section.put("iconInput", iconInput.getText());

		section.put("viewSelection", viewSelection.getSelection());
		section.put("viewInput", viewInput.getText());

		section.put("worldSelection", worldSelection.getSelection());
		section.put("worldInput", worldInput.getText());

		section.put("codeSelection", codeSelection.getSelection());
		section.put("codeInput", codeInput.getText());

		section.put("verInput", verInput.getText());
		section.put("cdnInput", cdnInput.getText());
		section.put("idcInput", idcInput.getText());
		section.put("startupInput", startupInput.getText());
		section.put("paramInput", paramInput.getText());

		section.put("zlibSelection", zlibSelection.getSelection());
		section.put("imgZipSelection", swfSelection.getSelection());
		section.put("mobileSelection", mobileSelection.getSelection());
		section.put("clearupSelection", clearupSelection.getSelection());
		section.put("writeRegionImgSelection",writeRegionImgSelection.getSelection());

		setting.save();
	}

	// --------------------------------------------------------------------------------
	//
	// ��ʼ����
	//
	// --------------------------------------------------------------------------------

	/**
	 * ��ʼ����
	 */
	private void toExport()
	{
		int inputCount = 0;

		if (cfgSelection.getSelection())
		{
			if (!checkInputDir(cfgInput.getText(), "����")) { return; }
			inputCount++;
		}

		if (fileSelection.getSelection())
		{
			if (!checkInputDir(fileInput.getText(), "�ļ�")) { return; }
			inputCount++;
		}

		if (iconSelection.getSelection())
		{
			if (!checkInputDir(iconInput.getText(), "ͼ��")) { return; }
			inputCount++;
		}

		if (viewSelection.getSelection())
		{
			if (!checkInputDir(viewInput.getText(), "��ͼ")) { return; }
			inputCount++;
		}

		if (worldSelection.getSelection())
		{
			if (!checkInputDir(worldInput.getText(), "����")) { return; }
			inputCount++;
		}

		if (codeSelection.getSelection())
		{
			if (!checkInputDir(codeInput.getText(), "����")) { return; }
			inputCount++;
		}

		if (inputCount > 0)
		{
			if (verInput.getText() == null || verInput.getText().isEmpty())
			{
				MessageDialog.openError(getShell(), "���벻�Ϸ�", "�汾�Ų���Ϊ��!");
				return;
			}
			if (!checkOutputDir(cdnInput.getText(), "��Դ��", false)) { return; }
			if (!checkOutputDir(idcInput.getText(), "���ÿ�", true)) { return; }

			// �����趨
			saveSetting();

			// ִ�е���
			execExport();
		}
		else
		{
			MessageDialog.openError(getShell(), "û������", "û�������κ�����!");
		}
	}

	/**
	 * �������Ŀ¼
	 * 
	 * @param path
	 * @param name
	 * @return
	 */
	private boolean checkInputDir(String path, String name)
	{
		if (path == null || path.isEmpty())
		{
			MessageDialog.openError(getShell(), "���벻�Ϸ�", name + "Ŀ¼����Ϊ��!");
			return false;
		}
		File file = new File(path);
		if (!file.exists())
		{
			MessageDialog.openError(getShell(), "���벻�Ϸ�", name + "��" + path + "  ������!");
			return false;
		}
		if (!file.isDirectory())
		{
			MessageDialog.openError(getShell(), "���벻�Ϸ�", name + "��" + path + "  ����һ����Ч��Ŀ¼!");
			return false;
		}
		return true;
	}

	/**
	 * ������Ŀ¼
	 * 
	 * @param path
	 * @param name
	 * @param allowNull
	 * @return
	 */
	private boolean checkOutputDir(String path, String name, boolean allowNull)
	{
		if (!allowNull)
		{
			return checkInputDir(path, name);
		}
		else
		{
			if (path != null && !path.isEmpty()) { return checkInputDir(path, name); }
		}
		return true;
	}

	// --------------------------------------------------------------------------------
	//
	// ��ʼ�趨
	//
	// --------------------------------------------------------------------------------

	/**
	 * ��ʼ�趨
	 */
	private void toSetting()
	{
		showSettingPage();
	}

	// --------------------------------------------------------------------------------------------------
	//
	// ҳ���л�����
	//
	// --------------------------------------------------------------------------------------------------

	/**
	 * ��ʾ����ҳ��
	 */
	private void showSettingPage()
	{
		cancelExport();

		getShell().setDefaultButton(submitButton);
		((StackLayout) this.getLayout()).topControl = settingPage;
		layout();
	}

	/**
	 * ��ʾ����ҳ��
	 */
	private void showOutputPage()
	{
		getShell().setDefaultButton(null);
		((StackLayout) this.getLayout()).topControl = outputPage;
		layout();
	}

	// --------------------------------------------------------------------------------------------------
	//
	// ���ȹ���
	//
	// --------------------------------------------------------------------------------------------------

	private static Hashtable<Thread, LogManager> logs = new Hashtable<Thread, LogManager>();

	/**
	 * ��ȡ���������
	 * 
	 * @return
	 */
	private static LogManager getTask()
	{
		Thread thread = Thread.currentThread();
		LogManager task = logs.get(thread);
		if (task == null)
		{
			task = new LogManager();
			logs.put(thread, task);
		}
		return task;
	}

	private static LogManager getTask(Thread thread)
	{
		return logs.get(thread);
	}

	public static void beginTask(String name)
	{
		getTask().beginTask(name);
	}

	public static void beginLogSet(String name)
	{
		beginLogSet(name, "");
	}

	public static void beginLogSet(String name, String path)
	{
		getTask().beginLogSet(1, name, path);
	}

	public static void endLogSet()
	{
		getTask().endLogSet();
	}

	public static void endTask()
	{
		getTask().endTask();
	}

	public static void log(String text)
	{
		log(text, "");
	}

	public static void log(String text, String path)
	{
		getTask().log(text, path);
	}

	public static void warning(String text)
	{
		warning(text, "");
	}

	public static void warning(String text, String path)
	{
		getTask().warning(text, path);
	}

	public static void progress(String text)
	{
		progress(text, "");
	}

	public static void progress(String text, String path)
	{
		getTask().progress(text, path);
	}

	public static void error(String text)
	{
		error(text, "");
	}

	public static void error(String text, String path)
	{
		getTask().error(text, path);
	}

	public static void error(Exception exception)
	{
		getTask().beginLogSet(3, exception.getMessage(), "");
		StackTraceElement[] elements = exception.getStackTrace();
		for (StackTraceElement element : elements)
		{
			error(element.toString());
		}
		getTask().endLogSet();
	}

	public static boolean isCancel()
	{
		return getTask().isCancel();
	}

	// --------------------------------------------------------------------------------------------------
	//
	// ���ȹ���
	//
	// --------------------------------------------------------------------------------------------------

	private boolean execing;
	private boolean viewing;
	private boolean refreshed;
	private Thread execThread;
	private Thread viewThread;
	private TreeViewer treeViewer;
	private Text startupInput;
	private Button startupButton;
	private Link startupLabel;
	private Text paramInput;
	private Button paramButton;
	private ScrolledComposite scrolledComposite;
	private Composite composite;
	private Button swfSelection;

	/**
	 * ȡ������
	 */
	private void cancelExport()
	{
		if (execThread != null && getTask(execThread) != null)
		{
			getTask(execThread).cancel();
		}
	}

	/**
	 * ִ�е���
	 */
	private void execExport()
	{
		// �����ҳ
		showOutputPage();

		final boolean cfgSelected = cfgSelection.getSelection();
		final boolean fileSelected = fileSelection.getSelection();
		final boolean iconSelected = iconSelection.getSelection();
		final boolean viewSelected = viewSelection.getSelection();
		final boolean worldSelected = worldSelection.getSelection();
		final boolean codeSelected = codeSelection.getSelection();

		final String cfgPath = cfgInput.getText();
		final String filePath = fileInput.getText();
		final String iconPath = iconInput.getText();
		final String viewPath = viewInput.getText();
		final String worldPath = worldInput.getText() + File.separatorChar + "src";
		final String codePath = codeInput.getText();

		final String ver = verInput.getText();
		final String cdnPath = cdnInput.getText();
		final String idcPath = idcInput.getText();
		final String startupPath = startupInput.getText();
		final String startupParam = paramInput.getText();

		final boolean zip = zlibSelection.getSelection();
		final boolean img = swfSelection.getSelection();
		final boolean clearup = clearupSelection.getSelection();
		final boolean isMobile=mobileSelection.getSelection();
		final boolean writeRegionImg=writeRegionImgSelection.getSelection();

		execing = false;
		viewing = false;
		refreshed = true;

		execThread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				execing = true;

				if (cfgSelected)
				{
					ConfigExporter configs = new ConfigExporter(new File(cfgPath), new File(cdnPath + File.separatorChar + "configs"),zip);
					configs.publish();
				}

				if (fileSelected)
				{
					FilesExporter files = new FilesExporter(new File(filePath), new File(cdnPath + File.separatorChar + "files"),zip);
					files.publish();
				}

				if (iconSelected)
				{
					IconExporter icons = new IconExporter(new File(iconPath), new File(cdnPath + File.separatorChar + "icons"),zip,new File(cfgPath));
					icons.publish();
				}

				if (viewSelected)
				{
					ViewExport views = new ViewExport(new File(viewPath), new File(cdnPath + File.separatorChar + "views"),img);
					views.publish();
				}

				if (worldSelected)
				{
					WorldExporter world = new WorldExporter(new File(worldPath), new File(cdnPath + File.separatorChar + "world"),zip,isMobile,writeRegionImg);
					world.publish();
				}

				if (codeSelected)
				{
					GameExporter code = new GameExporter(new File(codePath), new File(cdnPath + File.separatorChar + "games"), new File(startupPath), cdnPath, ver, startupParam);
					code.publish();
				}
				
				if(!GamePacker.isCancel())
				{
					GamePacker.beginTask("�ϲ��汾��Ϣ");
					try
					{
						mergerFiles(new File(cdnPath), new File(idcPath), ver,zip);
					}
					catch (Exception exception)
					{
						error(exception);
					}
					GamePacker.endTask();
	
					GamePacker.beginTask("ͬ���汾��ͱ��ÿ�");
					try
					{
						mergerFileUrls(new File(cdnPath), new File(idcPath), ver, clearup);
					}
					catch (Exception exception)
					{
						error(exception);
					}
					GamePacker.endTask();
				}

				execing = false;
			}
		});
		execThread.start();

		viewThread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				viewing = true;

				while (!isDisposed())
				{
					if (refreshed)
					{
						refreshed = false;
						getDisplay().asyncExec(new Runnable()
						{
							@Override
							public void run()
							{
								if (execThread != null && getTask(execThread) != null)
								{
									if (getTask(execThread) != treeViewer.getInput())
									{
										treeViewer.setInput(getTask(execThread));
									}
									else
									{
										treeViewer.refresh();

										ArrayList<Log> logSets = new ArrayList<Log>();

										Log last = getTask(execThread).getCurrLog();

										logSets.add(last);
										while (last.getParent() != null && last.getParent() != getTask(execThread))
										{
											logSets.add(0, last.getParent());
											last = last.getParent();
										}

										Object[] tasks = new Object[logSets.size()];
										tasks = logSets.toArray(tasks);
										treeViewer.setSelection(new TreeSelection(new TreePath(tasks)));
									}

									if (getTask(execThread).isCancel() && execing)
									{
										cancelButton.setEnabled(false);
									}
									else
									{
										cancelButton.setEnabled(true);
									}
								}

								refreshed = true;
								cancelButton.setText(execing ? "    ȡ ��    " : "    ȷ ��    ");
							}
						});
					}

					if (!execing)
					{
						if (viewing)
						{
							viewing = false;
						}
						else
						{
							break;
						}
					}

					try
					{
						Thread.sleep(300);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		});
		viewThread.start();
	}

	/**
	 * ��ȡģ��Ŀ¼
	 * 
	 * @param cdnDIR
	 * @return
	 */
	private File[] getModuleDirs(File cdnDIR)
	{
		ArrayList<File> modules = new ArrayList<File>();

		for (File file : cdnDIR.listFiles())
		{
			if (file.isDirectory() && !file.isHidden())
			{
				modules.add(file);
			}
		}

		File[] files = new File[modules.size()];
		files=modules.toArray(files);
		
		return files;
	}

	private void mergerFiles(File cdnDir, File idcDir, String ver,boolean zip) throws Exception
	{
		Document dom = DocumentHelper.createDocument();
		dom.addElement("project");

		Element root = dom.getRootElement();

		// ��ȡ����ģ��Ŀ¼
		File[] modules = getModuleDirs(cdnDir);

		// �ϲ�����ģ����ļ���ַ
		for (File module : modules)
		{
			File dbFile = new File(module.getPath() + File.separatorChar + "db.xml");

			Document document = (new SAXReader()).read(dbFile);
			for (Object node : document.getRootElement().elements())
			{
				Element element = (Element) node;
				element.detach();
				root.add(element);
			}
			
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String time=df.format(new Date());
			
			root.addAttribute("version", ver);
			root.addAttribute("time", time);
		}
		
		
		// ��������
		byte[] content = XmlUtil.formatXML(dom.asXML()).getBytes("UTF-8");
		if(zip)
		{
			content=ZlibUtil.compress(content);
		}
		
		//policy����
		byte[] policyContent="<?xml version=\"1.0\"?>\r\n<!DOCTYPE cross-domain-policy SYSTEM \"http://www.macromedia.com/xml/dtds/cross-domain-policy.dtd\">\r\n\r\n<cross-domain-policy>\r\n\t<allow-access-from domain=\"*\" />\r\n</cross-domain-policy>".getBytes("UTF-8");
		
		// �����CDN
		GamePacker.log("����汾��Ϣ����Դ�⣡");
		File verFile = new File(cdnDir.getPath() + File.separatorChar + ver + ".xml");
		File finalFile = new File(cdnDir.getPath() + File.separatorChar + "final.xml");
		File policyFile=new File(cdnDir.getPath()+File.separatorChar+"policy.xml");
		File crossdomainFile=new File(cdnDir.getPath()+File.separatorChar+"crossdomain.xml");
		
		FileUtil.writeFile(verFile, content);
		FileUtil.writeFile(finalFile, content);
		FileUtil.writeFile(policyFile, policyContent);
		FileUtil.writeFile(crossdomainFile, policyContent);

		// ͬ����IDC
		if (idcDir != null && idcDir.exists() && idcDir.isDirectory())
		{
			GamePacker.log("����汾��Ϣ�����ÿ⣡");
			File idcVerFile = new File(idcDir.getPath() + verFile.getPath().substring(cdnDir.getPath().length()));
			File idcXmlFile = new File(idcDir.getPath() + finalFile.getPath().substring(cdnDir.getPath().length()));
			File idcPolicyFile=new File(idcDir.getPath() + policyFile.getPath().substring(cdnDir.getPath().length()));
			
			FileUtil.writeFile(idcVerFile, content);
			FileUtil.writeFile(idcXmlFile, content);
			FileUtil.writeFile(idcPolicyFile, policyContent);
		}
	}

	/**
	 * �ϲ��汾�ļ���ַ��
	 * 
	 * @param cdnDir
	 * @param idcDir
	 * @param ver
	 * @param clearup
	 * @throws Exception
	 */
	private void mergerFileUrls(File cdnDir, File idcDir, String ver, boolean clearup) throws Exception
	{
		StringBuilder urls = new StringBuilder();

		// ��ȡ����ģ��Ŀ¼
		File[] modules = getModuleDirs(cdnDir);

		// �ϲ�����ģ����ļ���ַ
		for (File module : modules)
		{
			File dbFile = new File(module.getPath() + File.separatorChar + "db.ver");
			String dbText = new String(FileUtil.getFileBytes(dbFile),"UTF-8");
			urls.append(dbText);
		}

		// ����ϲ�����ļ���ַ��
		GamePacker.log("�����ļ���¼��");
		byte[] content = urls.toString().getBytes("UTF-8");
		File verFile = new File(cdnDir.getPath() + File.separatorChar + ver + ".ver");
		File finalFile = new File(cdnDir.getPath() + File.separatorChar + "final.ver");
		FileUtil.writeFile(verFile, content);
		FileUtil.writeFile(finalFile, content);

		// �ϲ�����ļ���ַ����
		String[] newURLs = urls.toString().replaceAll("\\r", "").split("\\n");

		// ���Ƶ����ÿ�
		if (idcDir != null && idcDir.exists() && idcDir.isDirectory())
		{
			GamePacker.beginLogSet("ͬ���ļ������ÿ⣡");
			for (String url : newURLs)
			{
				File cdnFile = new File(cdnDir.getPath() + url);
				File idcFile = new File(idcDir.getPath() + url);
				if (cdnFile.exists() && !idcFile.exists())
				{
					GamePacker.progress("ͬ���ļ�:"+url);
					FileUtil.copyTo(idcFile, cdnFile);
				}
			}
			GamePacker.endLogSet();
		}

		// ����
		if (clearup)
		{
			GamePacker.beginLogSet("������Ч�ļ���");
			
			// �ҳ����д��ڵİ汾�ļ�
			Hashtable<String, File> oldFiles = new Hashtable<String, File>();
			for (int i = 0; i < modules.length; i++)
			{
				File module = modules[i];
				for (File dir : module.listFiles())
				{
					if (dir.isDirectory() && !dir.isHidden() && !dir.getName().equals(".ver"))
					{
						for (File file : dir.listFiles())
						{
							if (!file.isDirectory() && !file.isHidden())
							{
								String absURL = file.getPath();
								String relURL = absURL.substring(cdnDir.getPath().length(), absURL.length()).replaceAll("\\\\", "/");
								oldFiles.put(relURL, file);
							}
						}
					}
				}
			}

			// ������Ч�İ汾�ļ�
			for (String url : newURLs)
			{
				oldFiles.remove(url);
			}

			// ɾ����Ч�İ汾�ļ�
			for (File file : oldFiles.values())
			{
				String url=file.getPath().substring(cdnDir.getPath().length());
				
				GamePacker.progress("ɾ���ļ�:"+url);
				
				file.delete();

				if (idcDir != null && idcDir.exists() && idcDir.isDirectory())
				{
					// ����IDC
					File idcFile = new File(idcDir.getPath() + url);
					if (idcFile.exists())
					{
						idcFile.delete();
					}
				}
			}

			// ɾ����Ч�İ汾�ļ���
			for (File file : cdnDir.listFiles())
			{
				if (file.isFile() && !file.isHidden() && file.getName().endsWith(".ver"))
				{
					GamePacker.progress("����汾�ļ���");
					
					if (!file.getName().equals("final.ver") && !file.getName().equals(ver + ".ver"))
					{
						String filePath = file.getPath();

						File oldVerFile = file;
						File oldXmlFile = new File(filePath.substring(0, filePath.length() - 3) + "xml");

						if (oldVerFile.exists())
						{
							oldVerFile.delete();
						}
						if (oldXmlFile.exists())
						{
							oldXmlFile.delete();
						}

						if (idcDir != null && idcDir.exists() && idcDir.isDirectory())
						{
							// ����IDC
							File oldIdcVerFile = new File(idcDir.getPath() + oldVerFile.getPath().substring(cdnDir.getPath().length()));
							File oldIdcXmlFile = new File(idcDir.getPath() + oldXmlFile.getPath().substring(cdnDir.getPath().length()));

							if (oldIdcVerFile.exists())
							{
								oldIdcVerFile.delete();
							}
							if (oldIdcXmlFile.exists())
							{
								oldIdcXmlFile.delete();
							}
						}
					}
				}
			}
			
			GamePacker.endLogSet();
		}
	}
}
