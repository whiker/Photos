package photos.app;

import java.io.BufferedReader;
import java.io.FileReader;

import photos.server.http.HttpRequest;
import photos.server.http.HttpResponse;

public class AppLogin implements App
{
	private String m_username = null;
	private String m_password = null;
	
	private int  m_sessionID = -1;
	private long m_expired;
	
	public AppLogin()
	{
		try {
			String path = AppCenter.AppPath + "login/user.dat";
			FileReader inp = new FileReader(path);
			BufferedReader reader = new BufferedReader(inp);
			m_username = reader.readLine();
			m_password = reader.readLine();
			inp.close();
			reader.close();
		} catch (Exception e) {
			m_username = m_password = null;
		}
	}
	
	@Override
	public void service(HttpRequest req, HttpResponse rep)
			throws Exception
	{
		if (req.isStatic)
		{
			rep.sendFile(req.uri);
			return;
		}
		
		if (req.query != null)
		{
			String[] tokens = req.query.split("=");
			if (tokens.length == 2 &&
				tokens[0].equals(m_username) &&
				tokens[1].equals(m_password))
			{
				int rand = -1;
				synchronized (this)
				{
					long t = System.currentTimeMillis();
					rand = (int) (t % 1000000000);
					if (rand == 0) rand = 1;
					m_sessionID = rand;
					m_expired = t + 1800000;
				}
				rep.cookie = rand;
				rep.send200();
			}
			else
				rep.send404();
		}
		else
			rep.sendFile("login/login.html");
	}
	
	public boolean isLogin(int sessionID)
	{
		if (m_sessionID <= 0 || sessionID <= 0)
			return false;
		
		long t = System.currentTimeMillis();
		if (t > m_expired)
		{
			m_sessionID = -1;
			return false;
		}
		
		if (sessionID != m_sessionID)
			return false;
		
		m_expired = t + 1800000;
		return true;
	}
	
	public void responseHtml(HttpResponse rep)
			throws Exception
	{
		rep.sendFile("login/login.html");
	}
	
	@Override
	public String getAppName() { return "login"; }
	
	@Override
	public Share getShare() { return null; }
}
