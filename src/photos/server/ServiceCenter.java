package photos.server;

import java.net.Socket;

import photos.task.FixedBoss;

public class ServiceCenter
{
	private int           m_num;
	private ServiceTask[] m_tasks;
	private FixedBoss     m_works;
	
	public ServiceCenter(int n)
	{
		m_num = n + 1;
		m_tasks = new ServiceTask[m_num];
		for (int i = 0; i <= n; ++i)
		{
			m_tasks[i] = new ServiceTask();
			m_tasks[i].id = i;
		}
		m_works = new FixedBoss(n);
	}
	
	public void handle(Socket conn)
	{
		for (int i = 0; i < m_num; ++i)
		{
			if (m_tasks[i].isIdle)
			{
				ServiceTask task = m_tasks[i];
				if (task.ready(conn))
					m_works.acceptTask(task);
				return;
			}
		}
	}
	
	public void exit()
	{
		m_works.exit();
	}
}
