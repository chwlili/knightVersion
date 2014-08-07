package org.game.knight.version.other;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFConditionalFormattingRule;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatternFormatting;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFSheetConditionalFormatting;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.chw.util.CmdUtil;
import org.chw.util.FileUtil;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.game.knight.version.AppSetting;

public class Log2XlsUtil extends Composite
{
	private Text logInput;
	private Composite inputBox;
	private Composite runBox;
	private Button submit;
	private Button cancel;
	private Button logBrowse;
	private CLabel progLabel;
	private ProgressBar progBar;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public Log2XlsUtil(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new StackLayout());

		inputBox = new Composite(this, SWT.NONE);
		FillLayout fl_inputBox = new FillLayout(SWT.HORIZONTAL);
		inputBox.setLayout(fl_inputBox);

		grplog = new Group(inputBox, SWT.NONE);
		GridLayout gl_grplog = new GridLayout(3, false);
		gl_grplog.marginTop = 10;
		grplog.setLayout(gl_grplog);
		grplog.setText("\u8F6C\u6362LOG\u6570\u636E");

		logLabel = new Link(grplog, SWT.NONE);
		logLabel.setText("<a>\u65E5\u5FD7\u76EE\u5F55</a>\uFF1A");
		logLabel.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				CmdUtil.openDir(logInput.getText());
			}
		});

		logInput = new Text(grplog, SWT.BORDER);
		logInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		logBrowse = new Button(grplog, SWT.NONE);
		logBrowse.setText("  ...  ");

		errLabel = new Link(grplog, SWT.NONE);
		errLabel.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				CmdUtil.openDir(errInput.getText());
			}
		});
		errLabel.setText("<a>\u9519\u8BEF\u65E5\u5FD7</a>:");

		errInput = new Text(grplog, SWT.BORDER);
		errInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		errBrowse = new Button(grplog, SWT.NONE);
		errBrowse.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				DirectoryDialog dir = new DirectoryDialog(getShell());
				String path = dir.open();

				if (path != null)
				{
					errInput.setText(path);
				}
			}
		});
		errBrowse.setText("  ...  ");
		new Label(grplog, SWT.NONE);

		submit = new Button(grplog, SWT.NONE);
		GridData gd_submit = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		gd_submit.verticalIndent = 15;
		submit.setLayoutData(gd_submit);
		submit.setText("    开 始    ");
		new Label(grplog, SWT.NONE);

		runBox = new Composite(this, SWT.NONE);
		GridLayout gl_runBox = new GridLayout(1, false);
		gl_runBox.marginTop = 10;
		gl_runBox.marginRight = 10;
		gl_runBox.marginLeft = 10;
		gl_runBox.marginHeight = 10;
		gl_runBox.marginBottom = 10;
		runBox.setLayout(gl_runBox);

		progLabel = new CLabel(runBox, SWT.NONE);
		progLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		progLabel.setText("\u8F6C\u6362\u4E2D...");

		progBar = new ProgressBar(runBox, SWT.NONE);
		progBar.setSelection(0);
		progBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		cancel = new Button(runBox, SWT.NONE);
		GridData gd_cancel = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		gd_cancel.verticalIndent = 10;
		cancel.setLayoutData(gd_cancel);
		cancel.setText("    取 消    ");

		initHandler();
	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

	// ------------------------------------------------------------------------------
	//
	// 初始化处理器
	//
	// ------------------------------------------------------------------------------

	/**
	 * 初始化处理器
	 */
	private void initHandler()
	{
		showInputForm();

		// 窗口关闭
		getShell().addDisposeListener(new DisposeListener()
		{
			@Override
			public void widgetDisposed(DisposeEvent e)
			{
				stopRun();
			}
		});

		submit.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				startRun();
			}
		});

		cancel.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				stopRun();
			}
		});

		logBrowse.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				DirectoryDialog dir = new DirectoryDialog(getShell());
				String path = dir.open();

				if (path != null)
				{
					logInput.setText(path);
				}
			}
		});
	}

	// --------------------------------------------------------------------------------
	//
	// 历史记录信息
	//
	// --------------------------------------------------------------------------------

	private AppSetting setting;
	private IDialogSettings section;

	/**
	 * 初始化设定
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

		logInput.setText(section.get("logInput") != null ? section.get("logInput") : "");
	}

	/**
	 * 保存设定
	 */
	private void saveSetting()
	{
		section.put("logInput", logInput.getText());

		setting.save();
	}

	// --------------------------------------------------------------------------------
	//
	// 表单切换
	//
	// --------------------------------------------------------------------------------

	/**
	 * 显示输入表单
	 */
	private void showInputForm()
	{
		StackLayout layout = (StackLayout) getLayout();
		layout.topControl = inputBox;
		layout(true);
	}

	/**
	 * 显示运行表单
	 */
	private void showRunForm()
	{
		StackLayout layout = (StackLayout) getLayout();
		layout.topControl = runBox;
		layout(true);
	}

	/**
	 * 检查源目录和归档目录
	 */
	private boolean checkFrom()
	{
		String from = logInput.getText();

		if (from == null || from == "")
		{
			return false;
		}
		else
		{
			File fromFile = new File(from);
			if (fromFile.exists() == false || fromFile.isDirectory() == false)
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * 检查归档目录
	 */
	private boolean checkTo()
	{
		// String to = saveInput.getText();
		//
		// if (to == null || to == "")
		// {
		// return false;
		// }
		// else
		// {
		// File toFile = new File(to);
		// if (toFile.exists() && toFile.isDirectory())
		// {
		// return false;
		// }
		// }

		return true;
	}

	/**
	 * 开始运行
	 */
	private void startRun()
	{
		if (runing)
		{
			MessageBox box = new MessageBox(getShell());
			box.setMessage("已经在同步数据了！");
			box.setText("");
			box.open();
			return;
		}

		if (!checkFrom())
		{
			MessageBox box = new MessageBox(getShell());
			box.setMessage("日志目录无效！");
			box.setText("");
			box.open();
			return;
		}
		if (!checkTo())
		{
			MessageBox box = new MessageBox(getShell());
			box.setMessage("输出文件无效！");
			box.setText("");
			box.open();
			return;
		}

		showRunForm();

		runing = false;
		stoped = false;

		saveSetting();

		run();
	}

	/**
	 * 停止运行
	 */
	private void stopRun()
	{
		if (runing)
		{
			stoped = true;
		}
		else
		{
			showInputForm();
		}
	}

	private boolean runing = false;
	private boolean stoped = false;
	private Thread thread;
	private Link logLabel;
	private Group grplog;
	private Link errLabel;
	private Text errInput;
	private Button errBrowse;

	
	/**
	 * 错误LOG转换到
	 * @author ds
	 *
	 */
	private class ErrorLog2EXCELTask
	{
		private File[] sqlFiles;

		private int index;
		private int finishCount;
		private boolean cancel;

		public ErrorLog2EXCELTask(File folder)
		{
			ArrayList<File> sqls = new ArrayList<File>();

			File[] files = folder.isDirectory() ? folder.listFiles() : new File[] {};
			for (File file : files)
			{
				if (file.isFile() && !file.isHidden() && file.getPath().endsWith(".sql"))
				{
					File xsl = new File(file.getPath().replaceAll("\\.sql$", ".xsl"));
					if (!xsl.exists())
					{
						sqls.add(file);
					}
				}
			}

			sqlFiles = sqls.toArray(new File[sqls.size()]);
		}

		public void start()
		{
			ExecutorService exec = Executors.newFixedThreadPool(5);
			for (int i = 0; i < 5; i++)
			{
				exec.execute(new ErrorRunnable());
			}
		}

		public synchronized void cancel()
		{
			cancel = true;
		}
		
		public synchronized int getFinishCount()
		{
			return finishCount;
		}
		
		public synchronized int getTotalCount()
		{
			return sqlFiles.length;
		}

		private synchronized File getNextFile()
		{
			File result = null;
			if (index < sqlFiles.length)
			{
				result = sqlFiles[index];
				index++;
			}
			return result;
		}

		private synchronized boolean isCancel()
		{
			return cancel;
		}

		private class ErrorRunnable implements Runnable
		{
			@Override
			public void run()
			{
				while(true)
				{
					File file=getNextFile();
					if(file==null || isCancel())
					{
						break;
					}
					
					writeXls(file);
				}
			}
			
			private void writeXls(File file)
			{
				try
				{
					String text = new String(FileUtil.getFileBytes(file), "utf8");
					String[] tables=text.split("\\nBye\\n");
					for(String table:tables)
					{
						table=table.replaceAll("--------------\\n(.*?)\\n--------------\\n\\n", "$1\n");
					}
				}
				catch (UnsupportedEncodingException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 运行
	 */
	private void run()
	{
		final String fromURL = logInput.getText();
		final String errorURL = errInput.getText();

		thread = new Thread(new Runnable()
		{
			private void updateText(final String text, final boolean error)
			{
				if (!isDisposed())
				{
					getDisplay().asyncExec(new Runnable()
					{
						@Override
						public void run()
						{
							progLabel.setText(text);

							if (error)
							{
								progLabel.setForeground(new Color(null, 255, 0, 0));
							}
							else
							{
								progLabel.setForeground(new Color(null, 0, 0, 0));
							}
						}
					});
				}
			}

			private void showStatus(String text)
			{
				updateText(text, false);
			}

			private void showError(String text)
			{
				updateText(text, true);
			}

			@Override
			public void run()
			{
				runing = true;

				if (!isDisposed())
				{
					getDisplay().asyncExec(new Runnable()
					{
						@Override
						public void run()
						{
							cancel.setText("    取 消    ");
						}
					});
				}

				boolean error = false;

				ArrayList<File> sqls = new ArrayList<File>();

				File logDir = new File(fromURL);
				for (File logFile : logDir.listFiles())
				{
					if (logFile.isFile() && !logFile.isHidden() && logFile.getName().toLowerCase().endsWith(".sql"))
					{
						String url = logFile.getPath();
						url = url.substring(0, url.length() - ".sql".length()) + ".xls";

						File tmp = new File(url);
						if (!tmp.exists())
						{
							sqls.add(logFile);
						}
					}
				}

				final int sqlCount = sqls.size();
				if (!isDisposed())
				{
					getDisplay().syncExec(new Runnable()
					{
						@Override
						public void run()
						{
							progBar.setMinimum(0);
							progBar.setMaximum(sqlCount);
						}
					});
				}

				for (int i = 0; i < sqls.size(); i++)
				{
					File file = sqls.get(i);

					HSSFWorkbook book = new HSSFWorkbook();

					final int sqlI = i + 1;
					if (!isDisposed())
					{
						getDisplay().syncExec(new Runnable()
						{
							@Override
							public void run()
							{
								progBar.setSelection(sqlI);
							}
						});
					}

					ArrayList<String> counts = new ArrayList<String>();
					ArrayList<String> comms = new ArrayList<String>();

					try
					{
						String text = new String(FileUtil.getFileBytes(file), "utf8");
						text = text.replaceAll(" *(.*?)\\s*\\n--------------\\n(select count.*?)\\n--------------\\s*\\+.*?\\+\\s*\\|\\s*count.*?\\n\\s*\\+.*?\\+\\s\\|                     (\\s*[0-9]*)\\s\\|\\s*\\+.*?\\+\\s.*?\\n\\s*Bye.*?\\n(\\s*剧情.*?\\n)?", "$3\t$1\n");
						String[] lines = text.split("\\n");
						for (String line : lines)
						{
							line = line.trim();
							int index = line.indexOf("\t");
							if (index != -1)
							{
								String part1 = line.substring(0, index).trim();
								String part2 = line.substring(index + 1).trim();

								int count = -1;
								try
								{
									count = Integer.parseInt(part1);
								}
								catch (Error err)
								{
								}

								if (count != -1 && !part2.equals("起名出错"))
								{
									counts.add(part1);
									comms.add(part2);
								}
							}
						}
					}
					catch (UnsupportedEncodingException e)
					{
						e.printStackTrace();
						showError(e.getMessage());
						error = true;
					}

					HSSFCellStyle countStyle = book.createCellStyle();
					countStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
					countStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

					HSSFCellStyle commStyle = book.createCellStyle();
					commStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
					commStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

					HSSFCellStyle headStyle = book.createCellStyle();
					headStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
					headStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
					headStyle.setFillForegroundColor(HSSFColor.TEAL.index);
					HSSFFont font = book.createFont();
					font.setColor(HSSFColor.WHITE.index);
					font.setBoldweight((short) 2);
					headStyle.setFont(font);
					headStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

					HSSFCellStyle numberStyle = book.createCellStyle();
					numberStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
					numberStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
					numberStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));

					int prev1 = -1;

					HSSFSheet sheet = book.createSheet(file.getName());
					sheet.setColumnWidth(0, 10000);
					sheet.setColumnWidth(1, 3000);
					sheet.setColumnWidth(2, 3000);
					sheet.setColumnWidth(3, 3000);
					sheet.setColumnWidth(4, 3000);
					sheet.setColumnWidth(6, 5000);

					HSSFRow title = sheet.createRow(0);
					title.setHeight((short) 500);
					HSSFCell head = null;
					head = title.createCell(0);
					head.setCellValue("说明");
					head.setCellStyle(headStyle);
					head.setCellType(HSSFCell.CELL_TYPE_STRING);
					head = title.createCell(1);
					head.setCellValue("人数");
					head.setCellStyle(headStyle);
					head.setCellType(HSSFCell.CELL_TYPE_STRING);
					head = title.createCell(2);
					head.setCellValue("流存比");
					head.setCellStyle(headStyle);
					head.setCellType(HSSFCell.CELL_TYPE_STRING);
					head = title.createCell(3);
					head.setCellValue("流失比");
					head.setCellStyle(headStyle);
					head.setCellType(HSSFCell.CELL_TYPE_STRING);
					head = title.createCell(4);
					head.setCellValue("流失数");
					head.setCellStyle(headStyle);
					head.setCellType(HSSFCell.CELL_TYPE_STRING);

					int index = 1;
					for (int j = 0; j < counts.size(); j++)
					{
						int curr = Integer.parseInt(counts.get(j));
						if (curr == 0)
						{
							continue;
						}

						HSSFRow row = sheet.createRow(index);

						HSSFCell cell = null;

						cell = row.createCell(0);
						cell.setCellValue(comms.get(j));
						cell.setCellStyle(commStyle);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);

						cell = row.createCell(1);
						cell.setCellValue(curr);
						cell.setCellStyle(countStyle);
						cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);

						if (index > 1 && curr > 0)
						{
							cell = row.createCell(2);
							cell.setCellValue((double) curr / prev1);
							cell.setCellStyle(numberStyle);
							cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);

							cell = row.createCell(3);
							cell.setCellValue(1 - ((double) curr) / prev1);
							cell.setCellStyle(numberStyle);
							cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);

							cell = row.createCell(4);
							cell.setCellValue(prev1 - curr);
							cell.setCellStyle(countStyle);
							cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);

							if (index > 2)
							{
								HSSFSheetConditionalFormatting scf = sheet.getSheetConditionalFormatting();

								HSSFConditionalFormattingRule rule1 = scf.createConditionalFormattingRule("C" + index + "<0.99001");
								HSSFPatternFormatting cf1 = rule1.createPatternFormatting();
								cf1.setFillBackgroundColor(HSSFColor.LIGHT_GREEN.index);

								HSSFConditionalFormattingRule rule2 = scf.createConditionalFormattingRule("C" + index + "<0.98001");
								HSSFPatternFormatting cf2 = rule2.createPatternFormatting();
								cf2.setFillBackgroundColor(HSSFColor.LIGHT_YELLOW.index);

								HSSFConditionalFormattingRule rule3 = scf.createConditionalFormattingRule("C" + index + "<0.97001");
								HSSFPatternFormatting cf3 = rule3.createPatternFormatting();
								cf3.setFillBackgroundColor(HSSFColor.LIGHT_ORANGE.index);
								scf.addConditionalFormatting(new CellRangeAddress[] { CellRangeAddress.valueOf("A" + index + ":E" + index + "") }, new HSSFConditionalFormattingRule[] { rule3, rule2, rule1 });
							}

							if (index == 5)
							{
								HSSFCellStyle cellStyle = book.createCellStyle();
								cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
								cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
								cellStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
								cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

								cell = row.createCell(6);
								cell.setCellValue("流存比低于99%");
								cell.setCellStyle(cellStyle);
								cell.setCellType(HSSFCell.CELL_TYPE_STRING);
							}
							else if (index == 6)
							{
								HSSFCellStyle cellStyle = book.createCellStyle();
								cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
								cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
								cellStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
								cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

								cell = row.createCell(6);
								cell.setCellValue("流存比低于98%");
								cell.setCellStyle(cellStyle);
								cell.setCellType(HSSFCell.CELL_TYPE_STRING);
							}
							else if (index == 7)
							{
								HSSFCellStyle cellStyle = book.createCellStyle();
								cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
								cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
								cellStyle.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
								cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

								cell = row.createCell(6);
								cell.setCellValue("流存比低于97%");
								cell.setCellStyle(cellStyle);
								cell.setCellType(HSSFCell.CELL_TYPE_STRING);
							}
						}

						if (curr > 0)
						{
							prev1 = curr;
						}

						index++;
					}

					try
					{
						FileOutputStream output = new FileOutputStream(new File(file.getPath() + ".xls"));
						book.write(output);
						output.close();
					}
					catch (FileNotFoundException e)
					{
						e.printStackTrace();
						showError(e.getMessage());
						error = true;
						break;
					}
					catch (IOException e)
					{
						e.printStackTrace();
						showError(e.getMessage());
						error = true;
						break;
					}

					if (stoped)
					{
						break;
					}
				}

				runing = false;

				if (stoped)
				{
					if (!isDisposed())
					{
						getDisplay().asyncExec(new Runnable()
						{
							@Override
							public void run()
							{
								cancel.setText("    确 定    ");
							}
						});
					}
				}
				else
				{
					if (!error)
					{
						showStatus("完成!");
					}

					if (!isDisposed())
					{
						getDisplay().asyncExec(new Runnable()
						{
							@Override
							public void run()
							{
								cancel.setText("    确 定    ");
							}
						});
					}
				}
			}
		});

		thread.start();
	}
}
