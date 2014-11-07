package org.game.knight.version;

import java.io.File;
import java.util.ArrayList;

import org.chw.util.FileUtil;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;
import org.game.knight.version.other.CdnSyncUtil;
import org.game.knight.version.other.ConfigDownloadUtil;
import org.game.knight.version.other.FolderSyncUtil;
import org.game.knight.version.other.Log2XlsUtil;
import org.game.knight.version.other.ServerCfgCopyUtil;
import org.game.knight.version.other.VerExportUtil;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.GamePackerConst;
import org.game.knight.version.packer.GamePackerExtendDialog;

public class Version
{
	protected Shell shlxx;
	private CTabFolder tabFolder_1;
	private GamePacker debugExporter;
	private GamePacker testExporter;
	private GamePacker releaseExporter;
	private ConfigDownloadUtil otherExporter;
	private ServerCfgCopyUtil serverCfgCopyer;
	private FolderSyncUtil folderSyncUtil;
	private VerExportUtil versionExporter;
	private CdnSyncUtil cdnSyncUtil;
	private Log2XlsUtil logXlsUtil;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
//		XLS xls = new XLS(new File("C:\\Users\\ds\\Desktop\\街机战士-v100-字符178029-20140805-TR-中译土耳其语-20140922.xls"), new File("C:\\Users\\ds\\Desktop\\街机战士_tr_2.xls"));
//		try
//		{
//			xls.run();
//		}
//		catch (FileNotFoundException e1)
//		{
//			e1.printStackTrace();
//		}
//		catch (IOException e1)
//		{
//			e1.printStackTrace();
//		}

		try
		{
			Version window = new Version();
			window.open();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	private void open()
	{
		Display display = Display.getDefault();

		shlxx = new Shell();
		shlxx.setImage(SWTResourceManager.getImage(Version.class, "/icons/appIcon.png"));
		shlxx.setSize(545, 797);
		shlxx.setText("\u6E38\u620F\u6253\u5305(N)");
		shlxx.setLayout(new FillLayout());

		initControls(shlxx);

		shlxx.open();
		shlxx.layout();

		while (!shlxx.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
	}

	/**
	 * 初始化控件
	 * 
	 * @param root
	 * @return
	 */
	public Composite initControls(Composite root)
	{
		Composite container = new Composite(root, SWT.NONE);
		FillLayout fl_shell = new FillLayout(SWT.HORIZONTAL);
		fl_shell.marginWidth = 10;
		fl_shell.marginHeight = 10;
		container.setLayout(fl_shell);

		tabFolder_1 = new CTabFolder(container, SWT.BORDER);
		tabFolder_1.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

		CTabItem tbtmNewItem_3 = new CTabItem(tabFolder_1, SWT.NONE);
		tbtmNewItem_3.setText("  公 共  ");
		Composite otherBox = new Composite(tabFolder_1, SWT.NONE);
		otherBox.setLayout(new GridLayout(1, false));
		otherExporter = new ConfigDownloadUtil(otherBox, SWT.NONE);
		otherExporter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		serverCfgCopyer = new ServerCfgCopyUtil(otherBox, SWT.NONE);
		serverCfgCopyer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		folderSyncUtil = new FolderSyncUtil(otherBox, SWT.NONE);
		folderSyncUtil.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		tbtmNewItem_3.setControl(otherBox);

		CTabItem versionItem = new CTabItem(tabFolder_1, SWT.NONE);
		versionItem.setText("  版本库  ");
		Composite versionBox = new Composite(tabFolder_1, SWT.NONE);
		versionBox.setLayout(new GridLayout(1, false));
		versionExporter = new VerExportUtil(versionBox, SWT.NONE);
		versionExporter.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		logXlsUtil = new Log2XlsUtil(versionBox, SWT.NONE);
		logXlsUtil.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		cdnSyncUtil = new CdnSyncUtil(versionBox, SWT.NONE);
		cdnSyncUtil.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		versionItem.setControl(versionBox);

		CTabItem checkerItem = new CTabItem(tabFolder_1, SWT.NONE);
		checkerItem.setText("  检查器  ");
		Composite checkerBox = new Composite(tabFolder_1, SWT.NONE);
		checkerBox.setLayout(new GridLayout(1, false));
		new GameServerCheckerUI(checkerBox, SWT.NONE);
		checkerItem.setControl(checkerBox);

		CTabItem tbtmNewItem = new CTabItem(tabFolder_1, SWT.NONE);
		tbtmNewItem.setText("  \u6D4B \u8BD5  ");
		debugExporter = new GamePacker(tabFolder_1, SWT.NONE);
		tbtmNewItem.setControl(debugExporter);

		CTabItem tbtmNewItem_2 = new CTabItem(tabFolder_1, SWT.NONE);
		tbtmNewItem_2.setText("  CDN_1  ");
		testExporter = new GamePacker(tabFolder_1, SWT.NONE);
		tbtmNewItem_2.setControl(testExporter);

		CTabItem tbtmNewItem_1 = new CTabItem(tabFolder_1, SWT.NONE);
		tbtmNewItem_1.setText("  CDN_2  ");
		releaseExporter = new GamePacker(tabFolder_1, SWT.NONE);
		tbtmNewItem_1.setControl(releaseExporter);

		writeAtfFiles();
		initSettings();
		createDynamicTabs();

		return tabFolder_1;
	}

	// ------------------------------------------------------------------------
	//
	// 初始化ATF工具
	//
	// ------------------------------------------------------------------------

	/**
	 * 写入ATF文件
	 */
	private void writeAtfFiles()
	{
		File file1 = new File(GamePackerConst.getJarDir().getPath() + File.separator + "png2atf.exe");
		if (!file1.exists())
		{
			FileUtil.writeFile(file1, getClass().getResourceAsStream("/exe/png2atf.exe"));
		}

		File file2 = new File(GamePackerConst.getJarDir().getPath() + File.separator + "PVRTexLib.dll");
		if (!file2.exists())
		{
			FileUtil.writeFile(file2, getClass().getResourceAsStream("/exe/PVRTexLib.dll"));
		}
	}

	// ------------------------------------------------------------------------
	//
	// 设定
	//
	// ------------------------------------------------------------------------

	private AppSetting setting;
	private IDialogSettings dynamicSetting;
	private ArrayList<String> folderNames = new ArrayList<String>();
	private ArrayList<String> folderSections = new ArrayList<String>();

	/**
	 * 初始化设定
	 */
	private void initSettings()
	{
		setting = new AppSetting("export");
		setting.open("export");

		debugExporter.initSetting(setting, "debug");
		testExporter.initSetting(setting, "test");
		releaseExporter.initSetting(setting, "release");
		otherExporter.initSetting(setting, "other");
		serverCfgCopyer.initSetting(setting, "serverCfgSync");
		folderSyncUtil.initSetting(setting, "folderSync");
		versionExporter.initSetting(setting, "verExpoter");
		cdnSyncUtil.initSetting(setting, "cdnSync");
		logXlsUtil.initSetting(setting, "sql2xls");

		dynamicSetting = setting.getSection("dynamicTabs");
		if (dynamicSetting == null)
		{
			dynamicSetting = setting.addNewSection("dynamicTabs");
			dynamicSetting.put("names", "");
			dynamicSetting.put("sections", "");
			setting.save();
		}
	}

	/**
	 * 保存设定
	 */
	private void saveSettings()
	{
		dynamicSetting = setting.getSection("dynamicTabs");

		StringBuilder sectionSB = new StringBuilder();
		for (int i = 0; i < folderSections.size(); i++)
		{
			if (i != 0)
			{
				sectionSB.append("\n");
			}
			sectionSB.append(folderSections.get(i));
		}

		StringBuilder nameSB = new StringBuilder();
		for (int i = 0; i < folderNames.size(); i++)
		{
			if (i != 0)
			{
				nameSB.append("\n");
			}
			nameSB.append(folderNames.get(i));
		}

		dynamicSetting.put("names", nameSB.toString());
		dynamicSetting.put("sections", sectionSB.toString());

		setting.save();
	}

	// ------------------------------------------------------------------------
	//
	// 创建
	//
	// ------------------------------------------------------------------------

	private int index = 0;

	/**
	 * 创建动态页面
	 */
	private void createDynamicTabs()
	{
		String names = dynamicSetting.get("names");
		if (names != null && !names.isEmpty())
		{
			String[] parts = names.split("\\n");
			for (int i = 0; i < parts.length; i++)
			{
				folderNames.add(parts[i]);
			}
		}

		String sections = dynamicSetting.get("sections");
		if (sections != null && !sections.isEmpty())
		{
			String[] parts = sections.split("\\n");
			for (int i = 0; i < parts.length; i++)
			{
				String sectionName = parts[i];

				folderSections.add(sectionName);

				String folderName = "";
				if (i < folderNames.size())
				{
					folderName = folderNames.get(i);
				}

				CTabItem folder = new CTabItem(tabFolder_1, SWT.CLOSE);
				folder.setText(folderName);

				GamePacker exporter = new GamePacker(tabFolder_1, SWT.NONE);
				exporter.initSetting(setting, sectionName);
				folder.setControl(exporter);
			}
		}

		CTabItem add = new CTabItem(tabFolder_1, SWT.NONE);
		add.setText(" + ");

		tabFolder_1.addSelectionListener(selectionListener);
		tabFolder_1.addCTabFolder2Listener(closeListener);
	}

	/**
	 * 选择监视器
	 */
	private SelectionAdapter selectionListener = new SelectionAdapter()
	{
		@Override
		public void widgetSelected(SelectionEvent e)
		{
			if (tabFolder_1.getSelectionIndex() == tabFolder_1.getItems().length - 1)
			{
				GamePackerExtendDialog create = new GamePackerExtendDialog(shlxx);
				if (create.open("") == 0)
				{
					String name = create.getTabName();
					String sectionName = null;

					int index = 0;
					while (true)
					{
						sectionName = name + "_" + index;
						if (!folderSections.contains(sectionName))
						{
							break;
						}
						index++;
					}

					folderNames.add(name);
					folderSections.add(sectionName);
					saveSettings();

					CTabItem folder = new CTabItem(tabFolder_1, SWT.CLOSE);
					folder.setText(name);

					GamePacker exporter = new GamePacker(tabFolder_1, SWT.NONE);
					exporter.initSetting(setting, sectionName);
					folder.setControl(exporter);

					tabFolder_1.getSelection().dispose();

					CTabItem add = new CTabItem(tabFolder_1, SWT.NONE);
					add.setText(" + ");

					tabFolder_1.setSelection(tabFolder_1.getItems().length - 2);
				}
				else
				{
					tabFolder_1.setSelection(index);
				}
			}
			else
			{
				index = tabFolder_1.getSelectionIndex();
			}
		}
	};

	/**
	 * 关闭监视器
	 */
	private CTabFolder2Adapter closeListener = new CTabFolder2Adapter()
	{
		@Override
		public void close(CTabFolderEvent event)
		{
			int begin = tabFolder_1.getItemCount();
			for (int i = 0; i < tabFolder_1.getItems().length; i++)
			{
				if (tabFolder_1.getItems()[i].getShowClose())
				{
					begin = i;
					break;
				}
			}

			for (int i = 0; i < tabFolder_1.getItems().length; i++)
			{
				if (tabFolder_1.getItems()[i].equals(event.item))
				{
					int index = i - begin;

					// setting.removeSection(folderSections.get(index));

					folderNames.remove(index);
					folderSections.remove(index);

					saveSettings();
					break;
				}
			}
		}
	};
}
