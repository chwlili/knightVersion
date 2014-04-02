package org.game.knight.version.packer;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class GamePackerStartupDialog extends Dialog
{
	private Text text;
	private Text text_1;
	private Text text_2;
	private Text text_3;
	private Text text_4;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public GamePackerStartupDialog(Shell parentShell)
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
		lblNewLabel.setText("�����ַ��");
		
		text = new Text(container, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text.setText(serverHost);
		
		CLabel lblNewLabel_1 = new CLabel(container, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("����˿ڣ�");
		
		text_1 = new Text(container, SWT.BORDER);
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_1.setText(serverPort);
		
		CLabel lblNewLabel_2 = new CLabel(container, SWT.NONE);
		lblNewLabel_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_2.setText("�����ʶ��");
		
		text_2 = new Text(container, SWT.BORDER);
		text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_2.setText(serverID);
		
		CLabel lblNewLabel_4 = new CLabel(container, SWT.NONE);
		lblNewLabel_4.setText("�����û�");
		
		text_4 = new Text(container, SWT.BORDER);
		text_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_4.setText(testList);
		
		CLabel lblNewLabel_3 = new CLabel(container, SWT.NONE);
		lblNewLabel_3.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		lblNewLabel_3.setText("�����ʺţ�");
		
		text_3 = new Text(container, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		text_3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		text_3.setText(userList);
		
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
		return new Point(450, 300);
	}
	
	public int open(String params)
	{
		serverHost="";
		serverPort="";
		serverID="";
		testList="";
		userList="";
		
		if(params!=null && params.trim()!="")
		{
			params=params.trim();
			
			String[] paramList=params.split(" ");
			if(paramList.length>0)
			{
				serverHost=paramList[0];
			}
			if(paramList.length>1)
			{
				serverPort=paramList[1];
			}
			if(paramList.length>2)
			{
				serverID=paramList[2];
			}
			if(paramList.length>3)
			{
				testList=paramList[3];
			}
			if(paramList.length>4)
			{
				userList=paramList[4];
			}
		}
		
		return super.open();
	}
	
	@Override
	protected void okPressed()
	{
		serverHost=text.getText();
		serverPort=text_1.getText();
		serverID=text_2.getText();
		testList=text_4.getText();
		userList=text_3.getText();
		
		super.okPressed();
	}
	
	private String serverHost;
	private String serverPort;
	private String serverID;
	private String testList;
	private String userList;
	
	public String getServerHost()
	{
		return serverHost;
	}
	
	public String getServerPort()
	{
		return serverPort;
	}
	
	public String getServerID()
	{
		return serverID;
	}
	
	public String getTests()
	{
		return testList;
	}
	
	public String getUsers()
	{
		return userList;
	}
}
