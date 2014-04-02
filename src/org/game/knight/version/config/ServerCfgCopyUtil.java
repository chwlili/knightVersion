package org.game.knight.version.config;

import java.io.File;

import org.chw.util.CmdUtil;
import org.chw.util.FileUtil;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.game.knight.version.AppSetting;

public class ServerCfgCopyUtil extends Composite
{
	private Text skillFromTxt;
	private Text skillDestTxt;
	private Text worldFromTxt;
	private Text worldDestTxt;
	private Text iconFromTxt;
	private Text iconDestTxt;
	private Button submit;
	private Button iconDestBtn;
	private Button iconFromBtn;
	private Button worldDestBtn;
	private Button worldFromBtn;
	private Button skillDestBtn;
	private Button skillFromBtn;
	private Link iconDestLink;
	private Link iconFromLink;
	private Link worldDestLink;
	private Link worldFromLink;
	private Link skillDestLink;
	private Link skillFromLink;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ServerCfgCopyUtil(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new GridLayout(1, false));

		Group group = new Group(this, SWT.NONE);
		group.setLayout(new GridLayout(3, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		group.setText("\u540C\u6B65\u540E\u7AEF\u914D\u7F6E");

		skillFromLink = new Link(group, SWT.NONE);
		skillFromLink.setText("<a>\u6280\u80FD\u6765\u6E90</a>\uFF1A");

		skillFromTxt = new Text(group, SWT.BORDER);
		skillFromTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		skillFromBtn = new Button(group, SWT.NONE);
		skillFromBtn.setText("  ...  ");

		skillDestLink = new Link(group, SWT.NONE);
		skillDestLink.setText("<a>\u6280\u80FD\u76EE\u6807</a>\uFF1A");

		skillDestTxt = new Text(group, SWT.BORDER);
		skillDestTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		skillDestBtn = new Button(group, SWT.NONE);
		skillDestBtn.setText("  ...  ");

		Label label = new Label(group, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gd_label = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		gd_label.verticalIndent = 5;
		label.setLayoutData(gd_label);

		worldFromLink = new Link(group, SWT.NONE);
		worldFromLink.setText("<a>\u4E16\u754C\u6765\u6E90</a>\uFF1A");

		worldFromTxt = new Text(group, SWT.BORDER);
		worldFromTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		worldFromBtn = new Button(group, SWT.NONE);
		worldFromBtn.setText("  ...  ");

		worldDestLink = new Link(group, SWT.NONE);
		worldDestLink.setText("<a>\u4E16\u754C\u76EE\u6807</a>\uFF1A");

		worldDestTxt = new Text(group, SWT.BORDER);
		worldDestTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		worldDestBtn = new Button(group, SWT.NONE);
		worldDestBtn.setText("  ...  ");

		Label label_1 = new Label(group, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gd_label_1 = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		gd_label_1.verticalIndent = 5;
		label_1.setLayoutData(gd_label_1);

		iconFromLink = new Link(group, SWT.NONE);
		iconFromLink.setText("<a>\u56FE\u6807\u6765\u6E90</a>\uFF1A");

		iconFromTxt = new Text(group, SWT.BORDER);
		iconFromTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		iconFromBtn = new Button(group, SWT.NONE);
		iconFromBtn.setText("  ...  ");

		iconDestLink = new Link(group, SWT.NONE);
		iconDestLink.setText("<a>\u56FE\u6807\u76EE\u6807</a>\uFF1A");

		iconDestTxt = new Text(group, SWT.BORDER);
		iconDestTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		iconDestBtn = new Button(group, SWT.NONE);
		iconDestBtn.setText("  ...  ");
		new Label(group, SWT.NONE);

		submit = new Button(group, SWT.NONE);
		submit.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		submit.setText("    \u5F00  \u59CB    ");
		new Label(group, SWT.NONE);

		initHandler();
	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
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

		skillFromTxt.setText(section.get("skillFrom") != null ? section.get("skillFrom") : "");
		skillDestTxt.setText(section.get("skillDest") != null ? section.get("skillDest") : "");
		worldFromTxt.setText(section.get("worldFrom") != null ? section.get("worldFrom") : "");
		worldDestTxt.setText(section.get("worldDest") != null ? section.get("worldDest") : "");
		iconFromTxt.setText(section.get("iconFrom") != null ? section.get("iconFrom") : "");
		iconDestTxt.setText(section.get("iconDest") != null ? section.get("iconDest") : "");
	}

	/**
	 * 保存设定
	 */
	private void saveSetting()
	{
		section.put("skillFrom", skillFromTxt.getText());
		section.put("skillDest", skillDestTxt.getText());
		section.put("worldFrom", worldFromTxt.getText());
		section.put("worldDest", worldDestTxt.getText());
		section.put("iconFrom", iconFromTxt.getText());
		section.put("iconDest", iconDestTxt.getText());

		setting.save();
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
		initInput(skillFromLink, skillFromBtn, skillFromTxt);
		initInput(skillDestLink, skillDestBtn, skillDestTxt);
		initInput(worldFromLink, worldFromBtn, worldFromTxt);
		initInput(worldDestLink, worldDestBtn, worldDestTxt);
		initInput(iconFromLink, iconFromBtn, iconFromTxt);
		initInput(iconDestLink, iconDestBtn, iconDestTxt);

		submit.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				execCopy();
			}
		});
	}

	/**
	 * 初始化输入框
	 * 
	 * @param link
	 * @param btn
	 * @param text
	 */
	private void initInput(final Link link, final Button btn, final Text text)
	{
		link.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				CmdUtil.openDirAndSelect(text.getText());
			}
		});

		btn.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				FileDialog dir = new FileDialog(getShell());
				String path = dir.open();

				if (path != null)
				{
					text.setText(path);
				}
			}
		});
	}

	/**
	 * 执行同步
	 */
	private void execCopy()
	{
		// 保存设置
		saveSetting();
		
		int finishCount=0;
		
		// 技能处理
		if (skillFromTxt.getText() != null && skillFromTxt.getText().trim().isEmpty() == false)
		{
			File from = new File(skillFromTxt.getText());
			File dest = new File(skillDestTxt.getText());
			if (from.exists() && from.isFile())
			{
				if (skillDestTxt.getText() != null && skillDestTxt.getText().trim().isEmpty() == false)
				{
					try
					{
						FileUtil.copyTo(dest, from);
						
						finishCount++;
					}
					catch (Throwable err)
					{
						MessageBox box = new MessageBox(getShell());
						box.setMessage("技能复制失败！" + err.getMessage());
						box.setText("");
						box.open();
					}
				}
				else
				{
					MessageBox box = new MessageBox(getShell());
					box.setMessage("技能目标为空！技能将不会复制！");
					box.setText("");
					box.open();
				}
			}
			else
			{
				MessageBox box = new MessageBox(getShell());
				box.setMessage("技能来源无效！技能将不会复制！");
				box.setText("");
				box.open();
			}
		}
		else
		{
			MessageBox box = new MessageBox(getShell());
			box.setMessage("技能来源为空！技能将不会复制！");
			box.setText("");
			box.open();
		}

		// 世界处理
		if (worldFromTxt.getText() != null && worldFromTxt.getText().trim().isEmpty() == false)
		{
			File from = new File(worldFromTxt.getText());
			File dest = new File(worldDestTxt.getText());
			if (from.exists() && from.isFile())
			{
				if (worldDestTxt.getText() != null && worldDestTxt.getText().trim().isEmpty() == false)
				{
					try
					{
						FileUtil.copyTo(dest, from);

						finishCount++;
					}
					catch (Throwable err)
					{
						MessageBox box = new MessageBox(getShell());
						box.setMessage("世界复制失败！" + err.getMessage());
						box.setText("");
						box.open();
					}
				}
				else
				{
					MessageBox box = new MessageBox(getShell());
					box.setMessage("世界目标为空！世界将不会复制！");
					box.setText("");
					box.open();
				}
			}
			else
			{
				MessageBox box = new MessageBox(getShell());
				box.setMessage("世界来源无效！世界将不会复制！");
				box.setText("");
				box.open();
			}
		}
		else
		{
			MessageBox box = new MessageBox(getShell());
			box.setMessage("世界来源为空！世界将不会复制！");
			box.setText("");
			box.open();
		}

		// 世界处理
		if (iconFromTxt.getText() != null && iconFromTxt.getText().trim().isEmpty() == false)
		{
			File from = new File(iconFromTxt.getText());
			File dest = new File(iconDestTxt.getText());
			if (from.exists() && from.isFile())
			{
				if (iconDestTxt.getText() != null && iconDestTxt.getText().trim().isEmpty() == false)
				{
					try
					{
						FileUtil.copyTo(dest, from);

						finishCount++;
					}
					catch (Throwable err)
					{
						MessageBox box = new MessageBox(getShell());
						box.setMessage("图标复制失败！" + err.getMessage());
						box.setText("");
						box.open();
					}
				}
				else
				{
					MessageBox box = new MessageBox(getShell());
					box.setMessage("图标目标为空！图标将不会复制！");
					box.setText("");
					box.open();
				}
			}
			else
			{
				MessageBox box = new MessageBox(getShell());
				box.setMessage("图标来源无效！图标将不会复制！");
				box.setText("");
				box.open();
			}
		}
		else
		{
			MessageBox box = new MessageBox(getShell());
			box.setMessage("图标来源为空！图标将不会复制！");
			box.setText("");
			box.open();
		}
		
		if(finishCount==3)
		{
			MessageBox box = new MessageBox(getShell());
			box.setMessage("同步完成！");
			box.setText("");
			box.open();
		}
	}
}
