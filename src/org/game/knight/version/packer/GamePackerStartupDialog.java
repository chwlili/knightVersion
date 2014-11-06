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
import org.eclipse.swt.widgets.Label;

public class GamePackerStartupDialog extends Dialog
{
	private Text text;
	private Text text_1;
	private Text text_2;
	private Text text_3;
	private Text text_4;
	private Text text_5;
	private Text text_6;

	private String serverHost;
	private String serverPort;
	private String serverID;
	private String testList;
	private String userList;
	private String runLang;
	private String authURL;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public GamePackerStartupDialog(Shell parentShell)
	{
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * 
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

		Label lblNewLabel_6 = new Label(container, SWT.NONE);
		lblNewLabel_6.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_6.setText("\u8BED\u8A00\u73AF\u5883\uFF1A");

		text_5 = new Text(container, SWT.BORDER);
		text_5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_5.setText(runLang);

		Label lblNewLabel_5 = new Label(container, SWT.NONE);
		lblNewLabel_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_5.setText("\u767B\u5F55\u670D\u52A1\uFF1A");

		text_6 = new Text(container, SWT.BORDER);
		text_6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_6.setText(authURL);

		Label label = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 5));

		CLabel lblNewLabel = new CLabel(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("服务地址：");

		text = new Text(container, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text.setText(serverHost);

		CLabel lblNewLabel_1 = new CLabel(container, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("服务端口：");

		text_1 = new Text(container, SWT.BORDER);
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_1.setText(serverPort);

		CLabel lblNewLabel_2 = new CLabel(container, SWT.NONE);
		lblNewLabel_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_2.setText("世界标识：");

		text_2 = new Text(container, SWT.BORDER);
		text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_2.setText(serverID);

		CLabel lblNewLabel_4 = new CLabel(container, SWT.NONE);
		lblNewLabel_4.setText("\u6D4B\u8BD5\u7528\u6237\uFF1A");

		text_4 = new Text(container, SWT.BORDER);
		text_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_4.setText(testList);

		CLabel lblNewLabel_3 = new CLabel(container, SWT.NONE);
		lblNewLabel_3.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		lblNewLabel_3.setText("调试帐号：");

		text_3 = new Text(container, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		text_3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		text_3.setText(userList);

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * 
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
		return new Point(450, 373);
	}

	public int open(String params)
	{
		serverHost = "";
		serverPort = "";
		serverID = "";
		testList = "";
		userList = "";
		runLang = "";
		authURL = "";

		if (params != null && params.trim() != "")
		{
			params = params.trim();

			String[] paramList = params.split(" ");
			if (paramList.length > 0)
			{
				serverHost = paramList[0];
			}
			if (paramList.length > 1)
			{
				serverPort = paramList[1];
			}
			if (paramList.length > 2)
			{
				serverID = paramList[2];
			}
			if (paramList.length > 3)
			{
				testList = paramList[3];
			}
			if (paramList.length > 4)
			{
				userList = paramList[4];
			}
			if (paramList.length > 5)
			{
				runLang = paramList[5];
			}
			if (paramList.length > 6)
			{
				authURL = paramList[6];
			}
		}

		if (runLang == null | runLang.isEmpty())
		{
			runLang = "zh";
		}
		if (authURL == null || authURL.isEmpty())
		{
			authURL = "http://192.168.1.127/qzone/auth.php";
		}

		return super.open();
	}

	@Override
	protected void okPressed()
	{
		serverHost = text.getText();
		serverPort = text_1.getText();
		serverID = text_2.getText();
		testList = text_4.getText();
		userList = text_3.getText();
		runLang = text_5.getText();
		authURL = text_6.getText();

		super.okPressed();
	}

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

	public String getRunLang()
	{
		return runLang;
	}

	public String getAuthURL()
	{
		return authURL;
	}
}
