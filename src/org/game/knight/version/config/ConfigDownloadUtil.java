package org.game.knight.version.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

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


public class ConfigDownloadUtil extends Composite
{
	private Text fromInput;
	private Text toInput;
	private Composite inputBox;
	private Composite runBox;
	private Button submit;
	private Button cancel;
	private Button toBrowse;
	private CLabel progLabel;
	private ProgressBar progBar;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ConfigDownloadUtil(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new StackLayout());

		inputBox = new Composite(this, SWT.NONE);
		inputBox.setLayout(new GridLayout(1, false));

		group = new Group(inputBox, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		group.setLayout(new GridLayout(3, false));
		group.setText("��������");

		link_1 = new Link(group, SWT.NONE);
		link_1.setText("<a>���ݵ�ַ</a>��");

		fromInput = new Text(group, SWT.BORDER);
		fromInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		fromInput.setSize(73, 23);
		new Label(group, SWT.NONE);

		link = new Link(group, SWT.NONE);
		link.setText("<a>����Ŀ¼</a>��");
		link.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				CmdUtil.openDir(toInput.getText());
			}
		});

		toInput = new Text(group, SWT.BORDER);
		toInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		toBrowse = new Button(group, SWT.NONE);
		toBrowse.setText("  ...  ");
		new Label(group, SWT.NONE);

		submit = new Button(group, SWT.NONE);
		submit.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		submit.setText("    �� ʼ    ");
		new Label(group, SWT.NONE);
		link_1.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				String url = fromInput.getText();
				url = (url.startsWith("http://") || url.startsWith("https://")) ? url : ("http://" + url).intern();

				CmdUtil.openWeb(url);
			}
		});

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
		progLabel.setText("��ȡ��...");
		
		progBar = new ProgressBar(runBox, SWT.NONE);
		progBar.setSelection(0);
		progBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		cancel = new Button(runBox, SWT.NONE);
		GridData gd_cancel = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		gd_cancel.verticalIndent = 10;
		cancel.setLayoutData(gd_cancel);
		cancel.setText("    ȡ ��    ");

		initHandler();
	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

	// ------------------------------------------------------------------------------
	//
	// ��ʼ��������
	//
	// ------------------------------------------------------------------------------

	/**
	 * ��ʼ��������
	 */
	private void initHandler()
	{
		showInputForm();

		//���ڹر�
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

		toBrowse.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				DirectoryDialog dir = new DirectoryDialog(getShell());
				String path = dir.open();

				if (path != null)
				{
					toInput.setText(path);
				}
			}
		});
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

		fromInput.setText(section.get("fromInput") != null ? section.get("fromInput") : "");
		toInput.setText(section.get("toInput") != null ? section.get("toInput") : "");
	}

	/**
	 * �����趨
	 */
	private void saveSetting()
	{
		section.put("fromInput", fromInput.getText());
		section.put("toInput", toInput.getText());

		setting.save();
	}

	// --------------------------------------------------------------------------------
	//
	// ���л�
	//
	// --------------------------------------------------------------------------------

	/**
	 * ��ʾ�����
	 */
	private void showInputForm()
	{
		StackLayout layout = (StackLayout) getLayout();
		layout.topControl = inputBox;
		layout(true);
	}

	/**
	 * ��ʾ���б�
	 */
	private void showRunForm()
	{
		StackLayout layout = (StackLayout) getLayout();
		layout.topControl = runBox;
		layout(true);
	}

	/**
	 * ���ԴĿ¼�͹鵵Ŀ¼
	 */
	private boolean checkFrom()
	{
		String from = fromInput.getText();

		if (from == null || from == "")
		{
			return false;
		}
		else
		{
			// File fromFile=new File(from);
			// if(fromFile.exists()==false || fromFile.isDirectory()==false)
			// {
			// return false;
			// }
		}
		return true;
	}

	/**
	 * ���鵵Ŀ¼
	 */
	private boolean checkTo()
	{
		String to = toInput.getText();

		if (to == null || to == "")
		{
			return false;
		}
		else
		{
			File toFile = new File(to);
			if (toFile.exists() && !toFile.isDirectory())
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * ��ʼ����
	 */
	private void startRun()
	{
		if (runing)
		{
			MessageBox box = new MessageBox(getShell());
			box.setMessage("�Ѿ���ͬ�������ˣ�");
			box.setText("");
			box.open();
			return;
		}

		if (!checkFrom())
		{
			MessageBox box = new MessageBox(getShell());
			box.setMessage("���ݵ�ַ��Ч��");
			box.setText("");
			box.open();
			return;
		}
		if (!checkTo())
		{
			MessageBox box = new MessageBox(getShell());
			box.setMessage("�鵵Ŀ¼��Ч��");
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
	 * ֹͣ����
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
	private Link link;
	private Link link_1;
	private Group group;

	/**
	 * ����
	 */
	private void run()
	{
		final String fromURL = fromInput.getText();
		final String destPath = toInput.getText();// new
													// File(toInput.getText());

		thread = new Thread(new Runnable()
		{
			private void updateText(final String text,final boolean error)
			{
				if (!isDisposed())
				{
					getDisplay().asyncExec(new Runnable()
					{
						@Override
						public void run()
						{
							progLabel.setText(text);
							
							if(error)
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
				updateText(text,false);
			}
			private void showError(String text)
			{
				updateText(text,true);
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
							cancel.setText("    ȡ ��    ");
						}
					});
				}

				boolean error = false;

				final String url = (fromURL.startsWith("http://") || fromURL.startsWith("https://")) ? fromURL : ("http://" + fromURL).intern();

				showStatus("��ȡ��ҳ:" + url);

				String content = readWebContent(url);
				if (content == null)
				{
					showError("�޷���ȡ��ҳ:" + url);
					error = true;
				}
				else
				{
					ArrayList<String> urls = new ArrayList<String>();

					// ��������ҳ��
					int index = 0;
					String prefix = "<a href=\"xml.php?n=";
					while (true)
					{
						index = content.indexOf(prefix, index);
						if (index != -1)
						{
							int last = content.indexOf("\"", index + prefix.length());
							if (last != -1)
							{
								urls.add(content.substring(index + prefix.length(), last));
								index = last;
							}
							else
							{
								break;
							}
						}
						else
						{
							break;
						}
					}
					
					final int count=urls.size();
					if (!isDisposed())
					{
						getDisplay().syncExec(new Runnable()
						{
							@Override
							public void run()
							{
								progBar.setMinimum(0);
								progBar.setMaximum(count);
							}
						});
					}
					
					// ������������
					if (urls.size() > 0)
					{
						for (int i = 0; i < urls.size(); i++)
						{
							if (stoped)
							{
								break;
							}

							final int select=i+1;
							if (!isDisposed())
							{
								getDisplay().syncExec(new Runnable()
								{
									@Override
									public void run()
									{
										progBar.setSelection(select);
									}
								});
							}
							
							String name = urls.get(i);
							String path = url + "?n=" + name;

							showStatus("��������:" + path);
							String text = readWebContent(path);
							
							if (text != null)
							{
								showStatus("�洢����:" + path);
								try
								{
									FileUtil.writeFile(new File(destPath + File.separatorChar + name + ".xml"), text.getBytes("UTF-8"));
								}
								catch (UnsupportedEncodingException e)
								{
									showError("�޷��洢����:" + e.getMessage());
									error = true;
									break;
								}
							}
							else
							{
								showError("�޷���������:" + path);
								error = true;
								break;
							}
						}
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
								cancel.setText("    ȷ ��    ");
							}
						});
					}
				}
				else
				{
					if (!error)
					{
						showStatus("���!");
					}

					if (!isDisposed())
					{
						getDisplay().asyncExec(new Runnable()
						{
							@Override
							public void run()
							{
								cancel.setText("    ȷ ��    ");
							}
						});
					}
				}
			}
		});

		thread.start();
	}

	private String readWebContent(String path)
	{
		try
		{
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");// ���ӱ�ͷ��ģ�����������ֹ����
			conn.setRequestProperty("Accept", "text/html");// ֻ����text/html���ͣ���ȻҲ���Խ���ͼƬ,pdf,*/*���⣬����tomcat/conf/web���涨����Щ
			conn.setConnectTimeout(5000);

			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
			{
				InputStream input = conn.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));

				String line = null;
				StringBuffer sb = new StringBuffer();
				while ((line = reader.readLine()) != null)
				{
					sb.append(line).append("\r\n");
				}
				if (reader != null)
				{
					reader.close();
				}
				if (conn != null)
				{
					conn.disconnect();
				}

				return sb.toString();
			}
		}
		catch (Exception e)
		{
		}

		return null;
	}
}
