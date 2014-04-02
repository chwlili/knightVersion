package org.game.knight.version.packer;

public class LogManager extends LogList
{
	private Log currLog;
	private LogList currLogSet;

	private Log currState;

	private boolean cancel;

	/**
	 * ���캯��
	 */
	public LogManager()
	{
		super(null, 0, "", "");
	}

	/**
	 * ��ȡ��ǰ��
	 * 
	 * @return
	 */
	public Log getCurrLog()
	{
		if (currState != null && currState.getParent() != null) { return currState; }
		return currLog;
	}

	/**
	 * ��ʼ����
	 * 
	 * @param name
	 */
	public void beginTask(String name)
	{
		resetState();
		currLogSet = (LogList) this.addChild(new LogList(this, 0, name, ""));
		currLog = currLogSet;
	}

	/**
	 * ��ʼ��־��
	 * 
	 * @param name
	 */
	public void beginLogSet(int type, String name, String path)
	{
		resetState();
		currLogSet = (LogList) currLogSet.addChild(new LogList(currLogSet, type, name, path));
		currLog = currLogSet;
	}

	/**
	 * ֹͣ��־��
	 */
	public void endLogSet()
	{
		resetState();
		currLogSet = currLogSet.getParent();
	}

	/**
	 * ֹͣ����
	 */
	public void endTask()
	{
		resetState();
		currLogSet = this;
	}

	/**
	 * �����־��Ϣ
	 * 
	 * @param text
	 */
	public void log(String text, String path)
	{
		resetState();
		currLog = currLogSet.addChild(new Log(currLogSet, 1, text, path));
	}

	/**
	 * ���������Ϣ
	 * 
	 * @param text
	 * @param path
	 */
	public void warning(String text, String path)
	{
		resetState();
		currLog = currLogSet.addChild(new Log(currLogSet, 2, text, path));
	}

	/**
	 * ���������Ϣ
	 * 
	 * @param text
	 */
	public void error(String text, String path)
	{
		resetState();
		currLog = currLogSet.addChild(new Log(currLogSet, 3, text, path));
	}

	/**
	 * ���������Ϣ
	 */
	public void progress(String text, String path)
	{
		if (currState == null)
		{
			currState = new Log(currLogSet, 1, text, path);
			currLogSet.addChild(currState);
		}

		currState.setText(text);
		currState.setPath(path);
	}

	private void resetState()
	{
		if (currState != null)
		{
			currState.getParent().removeChild(currState);
			currState.setParent(null);
			currState = null;
		}
	}

	/**
	 * ȡ��
	 */
	public void cancel()
	{
		if (!cancel)
		{
			cancel = true;
		}
	}

	/**
	 * �Ƿ���ȡ��
	 * 
	 * @return
	 */
	public boolean isCancel()
	{
		return cancel;
	}
}
