package org.game.knight.version.config;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.game.knight.version.AppSetting;

public class CdnSyncUtil extends Composite
{
	private Text url_input;
	private Browser browser;
	private Button submit_btn;
	private CLabel url_label;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public CdnSyncUtil(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		grpcdn = new Group(this, SWT.NONE);
		grpcdn.setText("\u540C\u6B65CDN");
		grpcdn.setLayout(new GridLayout(3, false));
				
						url_label = new CLabel(grpcdn, SWT.NONE);
						url_label.setText("\u8BF7\u6C42\u5730\u5740\uFF1A");
						
								url_input = new Text(grpcdn, SWT.BORDER);
								url_input.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
										
												submit_btn = new Button(grpcdn, SWT.NONE);
												submit_btn.addSelectionListener(new SelectionAdapter()
												{
													@Override
													public void widgetSelected(SelectionEvent e)
													{
														saveSetting();
														browser.setUrl(url_input.getText());
													}
												});
												submit_btn.setText("  \u540C \u6B65  ");
												
														browser = new Browser(grpcdn, SWT.NONE);
														browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));

	}

	@Override
	protected void checkSubclass()
	{

	}


	// --------------------------------------------------------------------------------
	//
	// 历史记录信息
	//
	// --------------------------------------------------------------------------------

	private AppSetting setting;
	private IDialogSettings section;
	private Group grpcdn;

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

		url_input.setText(section!=null && section.get("sync_url")!=null ? section.get("sync_url"):"");
	}

	/**
	 * 保存设定
	 */
	private void saveSetting()
	{
		section.put("sync_url", url_input.getText());

		setting.save();
	}
	
}
