package org.game.knight.version.packer;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class GamePackerExtendDialog extends Dialog
{
	private Text text;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public GamePackerExtendDialog(Shell parentShell)
	{
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gl_container = new GridLayout(2, false);
		gl_container.marginHeight = 20;
		gl_container.marginWidth = 20;
		container.setLayout(gl_container);
		
		CLabel lblNewLabel = new CLabel(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("名称：");
		
		text = new Text(container, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text.setText(tabName);
		
		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize()
	{
		return new Point(450, 152);
	}
	
	public int open(String params)
	{
		tabName="";
		
		if(params!=null && params.trim()!="")
		{
			params=params.trim();
			
			String[] paramList=params.split(" ");
			if(paramList.length>0)
			{
				tabName=paramList[0];
			}
		}
		
		return super.open();
	}
	
	@Override
	protected void okPressed()
	{
		String txt=text.getText();
		if(txt==null || txt.isEmpty())
		{
			MessageDialog.openError(getShell(), "输入不合法", "名称不能为空!");
			return;
		}
		
		tabName=text.getText();
		
		super.okPressed();
	}
	
	private String tabName;
	
	public String getTabName()
	{
		return tabName;
	}
	
}
