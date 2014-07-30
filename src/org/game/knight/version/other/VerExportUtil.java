package org.game.knight.version.other;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.chw.util.CmdUtil;
import org.chw.util.FileUtil;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Link;
import org.game.knight.version.AppSetting;

public class VerExportUtil extends Composite
{
	private Composite input_box;
	private Text from_input;
	private Button from_btn;
	private Combo version_list;
	private Text dest_input;
	private Button dest_btn;
	private Button submit_btn;
	
	private Composite prog_box;
	private CLabel export_label;
	private ProgressBar export_progress;
	private Button cancel_btn;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public VerExportUtil(Composite parent, int style)
	{
		super(parent, style);
		FillLayout fillLayout = new FillLayout(SWT.HORIZONTAL);
		fillLayout.marginHeight = 5;
		fillLayout.marginWidth = 5;
		setLayout(fillLayout);
		
		group = new Group(this, SWT.NONE);
		group.setText("\u7248\u672C\u5BFC\u51FA");
		group.setLayout(new StackLayout());
		
		input_box = new Composite(group, SWT.NONE);
		input_box.setLayout(new GridLayout(3, false));
		
		link = new Link(input_box, SWT.NONE);
		link.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		link.setText("<a>\u7248\u672C\u5E93</a>\uFF1A");
		link.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				CmdUtil.openDir(from_input.getText());
			}
		});
		
		from_input = new Text(input_box, SWT.BORDER);
		from_input.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		from_btn = new Button(input_box, SWT.NONE);
		from_btn.setText("  ...  ");
		
		link_2 = new Link(input_box, SWT.NONE);
		link_2.setText("\u7248\u672C\u53F7\uFF1A");
		
		version_list = new Combo(input_box, SWT.READ_ONLY);
		version_list.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(input_box, SWT.NONE);
		
		link_1 = new Link(input_box, SWT.NONE);
		link_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		link_1.setText("<a>\u5BFC\u51FA\u5230</a>\uFF1A");
		link_1.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				CmdUtil.openDir(dest_input.getText());
			}
		});
		
		dest_input = new Text(input_box, SWT.BORDER);
		dest_input.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		dest_btn = new Button(input_box, SWT.NONE);
		dest_btn.setText("  ...  ");
		new Label(input_box, SWT.NONE);
		
		submit_btn = new Button(input_box, SWT.NONE);
		GridData gd_submit_btn = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_submit_btn.verticalIndent = 10;
		submit_btn.setLayoutData(gd_submit_btn);
		submit_btn.setText("    \u5F00 \u59CB    ");
		new Label(input_box, SWT.NONE);
		
		prog_box = new Composite(group, SWT.NONE);
		prog_box.setLayout(new GridLayout(1, false));
		
		export_label = new CLabel(prog_box, SWT.NONE);
		export_label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		export_label.setText("\u5BFC\u51FA\u4E2D...");
		
		export_progress = new ProgressBar(prog_box, SWT.NONE);
		export_progress.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		cancel_btn = new Button(prog_box, SWT.NONE);
		GridData gd_cancel_btn = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		gd_cancel_btn.verticalIndent = 10;
		cancel_btn.setLayoutData(gd_cancel_btn);
		cancel_btn.setText("    \u53D6 \u6D88    ");

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
		
		from_input.addVerifyListener(new VerifyListener()
		{
			@Override
			public void verifyText(VerifyEvent e)
			{
				resetVersionList(e.text);
			}
		});
		
		submit_btn.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				startRun();
			}
		});

		cancel_btn.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				stopRun();
			}
		});

		from_btn.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				DirectoryDialog dir = new DirectoryDialog(getShell());
				String path = dir.open();

				if (path != null)
				{
					from_input.setText(path);
				}
			}
		});
	}

	private void resetVersionList(String text)
	{
		String verName=section!=null && section.get("version")!=null ? section.get("version"):"";
		
		if(text!=null && !text.isEmpty())
		{
			File folder=new File(text);
			if(folder.exists() && folder.isDirectory())
			{
				File[] files=folder.listFiles();
				for(File file:files)
				{
					if(file.getName().toLowerCase().endsWith(".ver"))
					{
						version_list.add(file.getName());
						
						if(file.getName().equals(verName))
						{
							version_list.setText(verName);
						}
					}
				}
			}
		}
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

		from_input.setText(section.get("fromFolder")!=null ? section.get("fromFolder"):"");
		dest_input.setText(section.get("destFolder")!=null ? section.get("destFolder"):"");
		version_list.setText(section.get("version")!=null ? section.get("version"):"");
		
		if(from_input.getText()!=null && from_input.getText().isEmpty()==false)
		{
			File folder=new File(from_input.getText());
			if(folder.exists() && folder.isDirectory())
			{
				File[] files=folder.listFiles();
				for(File file:files)
				{
					if(file.getName().toLowerCase().endsWith(".ver"))
					{
						if(version_list.getText().equals(file.getName()))
						{
							return;
						}
					}
				}
			}
		}
		
		version_list.setText("");
	}

	/**
	 * 保存设定
	 */
	private void saveSetting()
	{
		section.put("fromFolder", from_input.getText());
		section.put("version", version_list.getText());
		section.put("destFolder", dest_input.getText());

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
		StackLayout layout = (StackLayout) group.getLayout();
		layout.topControl = input_box;
		group.layout(true);
	}

	/**
	 * 显示运行表单
	 */
	private void showRunForm()
	{
		StackLayout layout = (StackLayout) group.getLayout();
		layout.topControl = prog_box;
		group.layout(true);
	}

	/**
	 * 检查库目录
	 */
	private boolean checkFrom()
	{
		String from = from_input.getText();

		if (from != null && !from.isEmpty())
		{
			File folder=new File(from);
			if(folder.exists() || folder.isDirectory())
			{
				File[] files=folder.listFiles();
				for(File file:files)
				{
					if(file.getName().toLowerCase().endsWith(".ver"))
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 检查版本号
	 * @return
	 */
	private boolean checkVersion()
	{
		String from = from_input.getText();
		String version = version_list.getText();

		if (from != null && !from.isEmpty())
		{
			File folder=new File(from);
			if(folder.exists() || folder.isDirectory())
			{
				File[] files=folder.listFiles();
				for(File file:files)
				{
					if(file.getName().toLowerCase().endsWith(".ver"))
					{
						if(file.getName().equals(version))
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * 检查输出目录
	 */
	private boolean checkTo()
	{
		String to = dest_input.getText();

		if (to != null && !to.isEmpty())
		{
			File folder=new File(to);
			if (folder.exists() && folder.isDirectory())
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * 开始运行
	 */
	private void startRun()
	{
		if (runing)
		{
			MessageBox box = new MessageBox(getShell());
			box.setMessage("正在导出版本！");
			box.setText("");
			box.open();
			return;
		}

		if (!checkFrom())
		{
			MessageBox box = new MessageBox(getShell());
			box.setMessage("版本库路径无效！");
			box.setText("");
			box.open();
			return;
		}
		if(!checkVersion())
		{
			MessageBox box = new MessageBox(getShell());
			box.setMessage("版本号无效！");
			box.setText("");
			box.open();
			return;
		}
		if (!checkTo())
		{
			MessageBox box = new MessageBox(getShell());
			box.setMessage("输出目录无效！");
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
	private Group group;
	private Link link;
	private Link link_1;
	private Link link_2;

	/**
	 * 运行
	 */
	private void run()
	{
		final String fromPath = from_input.getText();
		final String version = version_list.getText();
		final String destPath = dest_input.getText();

		thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				runing = true;
				
				getDisplay().syncExec(new Runnable()
				{
					@Override
					public void run()
					{
						showRunForm();
						cancel_btn.setText("  取 消  ");
					}
				});
				
				File fromFolder=new File(fromPath);
				File destFolder=new File(destPath);
				
				/*
				StringBuilder sb=new StringBuilder();
				for(File moduleDir:fromFolder.listFiles())
				{
					if(!moduleDir.isDirectory())
					{
						continue;
					}
					
					for(File verDir:moduleDir.listFiles())
					{
						if(!verDir.getName().equals(".ver"))
						{
							continue;
						}
						
						for(File verFile:verDir.listFiles())
						{
							if(sb.length()>0)
							{
								sb.append("\n");
							}
							sb.append(verFile.getPath().substring(fromFolder.getPath().length()));
						}
					}
				}
				*/
				
				for(File file :fromFolder.listFiles())
				{
					if(file.getName().equals(version))
					{
						try
						{
							byte[] bytes=FileUtil.getFileBytes(file);
							String content = new String(bytes,"utf-8");
							//content+=sb.toString();
							content+="\n"+file.getPath().substring(fromFolder.getPath().length());
							content+="\n"+file.getPath().substring(fromFolder.getPath().length(),file.getPath().length()-4)+".xml";
							
							final String[] lines=content.split("\n");
							for(int i=0;i<lines.length;i++)
							{
								final String line=lines[i];
								final int index=i+1;
								
								if(stoped)
								{
									break;
								}
								
								getDisplay().syncExec(new Runnable()
								{
									public void run()
									{
										showRunForm();
										export_label.setText(line);
										export_progress.setMaximum(lines.length);
										export_progress.setMinimum(0);
										export_progress.setSelection(index);
									}
								});
								
								File fromFile=new File(fromFolder.getPath()+line);
								File destFile=new File(destFolder.getPath()+line);
								if(fromFile.exists() && fromFile.isFile())
								{
									if(destFile.getParentFile().exists()==false)
									{
										destFile.getParentFile().mkdirs();
									}
									
									try
									{
										FileUtil.copyTo(destFile, fromFile);
									}
									catch (IOException e)
									{
										e.printStackTrace();
									}
								}
							}
						}
						catch (UnsupportedEncodingException e)
						{
							e.printStackTrace();
						}
						break;
					}
				}
				
				getDisplay().syncExec(new Runnable()
				{
					@Override
					public void run()
					{
						export_label.setText("");
						cancel_btn.setText("  确 定  ");
					}
				});

				runing=false;
			}
		});

		thread.start();
	}
}