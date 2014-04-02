package org.game.knight.version.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.chw.util.CmdUtil;
import org.chw.util.FileUtil;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

public class FolderSyncUtil extends Composite
{
	private Text src_input;
	private Text dst_input;
	private CLabel prog_lbl;
	private Button cancel_btn;
	private ProgressBar prog_bar;
	private Group group;
	private Composite form_box;
	private Composite prog_box;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public FolderSyncUtil(Composite parent, int style)
	{
		super(parent, style);
		FillLayout fillLayout = new FillLayout(SWT.HORIZONTAL);
		fillLayout.marginHeight = 5;
		fillLayout.marginWidth = 5;
		setLayout(fillLayout);

		group = new Group(this, SWT.NONE);
		group.setText("\u76EE\u5F55\u540C\u6B65");
		group.setLayout(new StackLayout());

		form_box = new Composite(group, SWT.NONE);
		form_box.setLayout(new GridLayout(3, false));

		Link src_link = new Link(form_box, SWT.NONE);
		src_link.setText("<a>\u6765\u6E90\u76EE\u5F55</a>\uFF1A");
		src_link.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				CmdUtil.openDir(src_input.getText());
			}
		});

		src_input = new Text(form_box, SWT.BORDER);
		src_input.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button src_btn = new Button(form_box, SWT.NONE);
		src_btn.setText("  ...  ");
		src_btn.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				DirectoryDialog dlg = new DirectoryDialog(getShell());
				String path = dlg.open();
				if (path != null)
				{
					src_input.setText(path);
				}
			}
		});

		Link dst_link = new Link(form_box, SWT.NONE);
		dst_link.setText("<a>\u76EE\u6807\u76EE\u5F55</a>\uFF1A");
		dst_link.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				CmdUtil.openDir(dst_input.getText());
			}
		});

		dst_input = new Text(form_box, SWT.BORDER);
		dst_input.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button dst_btn = new Button(form_box, SWT.NONE);
		dst_btn.setText("  ...  ");
		dst_btn.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				DirectoryDialog dlg = new DirectoryDialog(getShell());
				String path = dlg.open();
				if (path != null)
				{
					dst_input.setText(path);
				}
			}
		});
		new Label(form_box, SWT.NONE);

		Button begin_btn = new Button(form_box, SWT.NONE);
		begin_btn.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				run();
			}
		});
		GridData gd_begin_btn = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_begin_btn.verticalIndent = 10;
		begin_btn.setLayoutData(gd_begin_btn);
		begin_btn.setText("    \u786E \u5B9A    ");
		new Label(form_box, SWT.NONE);

		prog_box = new Composite(group, SWT.NONE);
		prog_box.setLayout(new GridLayout(1, false));

		prog_lbl = new CLabel(prog_box, SWT.NONE);
		prog_lbl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		prog_lbl.setText("");

		prog_bar = new ProgressBar(prog_box, SWT.NONE);
		prog_bar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		cancel_btn = new Button(prog_box, SWT.NONE);
		cancel_btn.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				run();
			}
		});
		GridData gd_cancel_btn = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_cancel_btn.verticalIndent = 10;
		cancel_btn.setLayoutData(gd_cancel_btn);
		cancel_btn.setText("    \u53D6 \u6D88    ");

		showForm();
	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
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
		this.section = null;

		if (setting != null)
		{
			section = setting.getSection(sectionName);
			if (section == null)
			{
				section = setting.addNewSection(sectionName);
			}

			src_input.setText(section.get("fromFolder") != null ? section.get("fromFolder") : "");
			dst_input.setText(section.get("destFolder") != null ? section.get("destFolder") : "");
		}
	}

	/**
	 * �����趨
	 */
	public void saveSetting()
	{
		if (setting != null)
		{
			section.put("fromFolder", src_input.getText());
			section.put("destFolder", dst_input.getText());

			setting.save();
		}
	}

	// --------------------------------------------------------------------------------
	//
	// ���л�
	//
	// --------------------------------------------------------------------------------

	/**
	 * ��ʾ��
	 */
	private void showForm()
	{
		((StackLayout) (group.getLayout())).topControl = form_box;
		group.layout();
	}

	/**
	 * ��ʾ����
	 */
	private void showProg()
	{
		prog_lbl.setText("");
		prog_bar.setSelection(0);
		prog_bar.setState(SWT.NORMAL);
		cancel_btn.setText("  ȡ ��  ");

		((StackLayout) (group.getLayout())).topControl = prog_box;

		group.layout();
	}

	// --------------------------------------------------------------------------------
	//
	// ִ�д���
	//
	// --------------------------------------------------------------------------------

	private Thread thread = null;

	private boolean runing = false;
	private boolean canceled = false;

	/**
	 * ����
	 */
	private void run()
	{
		if (runing)
		{
			canceled = true;
		}
		else
		{
			if (thread != null)
			{
				thread = null;
				showForm();
			}
			else
			{
				execute();
			}
		}
	}

	@Override
	public void dispose()
	{
		super.dispose();

		if (runing)
		{
			canceled = true;
		}
	}

	/**
	 * ִ��
	 */
	private void execute()
	{
		final File srcFolder = new File(src_input.getText());
		final File dstFolder = new File(dst_input.getText());
		
		if(!srcFolder.exists() || !srcFolder.isDirectory())
		{
			MessageBox msg=new MessageBox(getShell());
			msg.setText("����");
			msg.setMessage("��ԴĿ¼�����ڻ���һ��Ŀ¼!");
			msg.open();
			return;
		}
		if(!dstFolder.exists() || !dstFolder.isDirectory())
		{
			MessageBox msg=new MessageBox(getShell());
			msg.setText("����");
			msg.setMessage("Ŀ��Ŀ¼�����ڻ���һ��Ŀ¼!");
			msg.open();
			return;
		}
		
		saveSetting();
		
		showProg();

		Runnable exec = new Runnable()
		{
			@Override
			public void run()
			{
				if (canceled)
				{
					return;
				}

				boolean stoped = false;
				boolean errored = false;

				File[] src_files = getFiles(srcFolder);
				File[] dst_files = getFiles(dstFolder);

				//
				ArrayList<File> deledFiles = new ArrayList<File>();
				for (File file : dst_files)
				{
					File curr = new File(srcFolder.getPath() + file.getPath().substring(dstFolder.getPath().length()));
					if (!curr.exists())
					{
						deledFiles.add(file);
					}
				}

				// �����ļ�
				for (int i = 0; i < src_files.length; i++)
				{
					if (canceled)
					{
						stoped = true;
						break;
					}

					File from = src_files[i];
					File dest = new File(dstFolder.getPath() + from.getPath().substring(srcFolder.getPath().length()));

					final int index = i + 1;
					final int total = src_files.length + deledFiles.size();
					final String label = "�����ļ�:" + from.getPath();

					getDisplay().syncExec(new Runnable()
					{
						@Override
						public void run()
						{
							prog_bar.setMaximum(total);
							prog_bar.setMinimum(0);
							prog_bar.setSelection(index);
							prog_bar.setState(SWT.NORMAL);

							prog_lbl.setText(label);
						}
					});

					try
					{
						FileUtil.copyTo(dest, from);
					}
					catch (IOException e)
					{
						if (!canceled)
						{
							final String errMSG=e.getMessage();
							getDisplay().syncExec(new Runnable()
							{
								@Override
								public void run()
								{
									prog_bar.setMaximum(total);
									prog_bar.setMinimum(0);
									prog_bar.setSelection(index);
									prog_bar.setState(SWT.ERROR);

									prog_lbl.setText(errMSG);
								}
							});
						}

						stoped = true;
						errored = true;
						break;
					}
				}

				// ɾ���ļ�
				if (!stoped)
				{
					for (int i = 0; i < deledFiles.size(); i++)
					{
						if (canceled)
						{
							stoped = true;
							break;
						}

						File file = deledFiles.get(i);

						final int index = src_files.length + i + 1;
						final int total = src_files.length + deledFiles.size();
						final String label = "ɾ���ļ�:" + file.getPath();

						getDisplay().syncExec(new Runnable()
						{
							@Override
							public void run()
							{
								prog_bar.setMaximum(total);
								prog_bar.setMinimum(0);
								prog_bar.setSelection(index);
								prog_bar.setState(SWT.NORMAL);

								prog_lbl.setText(label);
							}
						});

						file.delete();
					}
				}

				if (canceled)
				{
					thread=null;
					getDisplay().syncExec(new Runnable()
					{
						@Override
						public void run()
						{
							showForm();
						}
					});
				}
				else
				{
					if(errored)
					{
						getDisplay().syncExec(new Runnable()
						{
							@Override
							public void run()
							{
								cancel_btn.setText("  ȷ ��  ");
							}
						});
					}
					else
					{
						getDisplay().syncExec(new Runnable()
						{
							@Override
							public void run()
							{
								prog_lbl.setText("���!");
								prog_bar.setState(SWT.NORMAL);
								
								cancel_btn.setText("  ȷ ��  ");
							}
						});
					}
				}
				
				runing = false;
				canceled = false;
			}

			// ��ȡ�ļ�
			private File[] getFiles(File folder)
			{
				ArrayList<File> results = new ArrayList<File>();

				if (folder != null && folder.exists() && folder.isDirectory())
				{
					ArrayList<File> folders = new ArrayList<File>();
					folders.add(folder);

					while (folders.size() > 0)
					{
						folder = folders.remove(0);

						File[] files = folder.listFiles();
						for (File file : files)
						{
							if (file.isHidden())
							{
								continue;
							}

							if (file.isDirectory())
							{
								folders.add(file);
							}
							else
							{
								results.add(file);
							}
						}
					}
				}

				return results.toArray(new File[results.size()]);
			}
		};

		runing = true;
		canceled = false;

		thread = new Thread(exec);
		thread.start();
	}

}
