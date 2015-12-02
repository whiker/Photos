package photos.task;

public class FixedBoss
{
	private int     m_num = 0;
	private Manager m_manager;
	
	public FixedBoss(int n)
	{
		m_num = n;
		m_manager = new Manager(n);
		
		for (int i = 0; i < n; ++i)
			m_manager.hireWorker();
	}
	
	public void exit()
	{
		for (int i = m_num; i > 0; i--)
			m_manager.fireWorker();
	}
	
	public void acceptTask(Task task)
	{
		m_manager.acceptTask(task);
	}
}
