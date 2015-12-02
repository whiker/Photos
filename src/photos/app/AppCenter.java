package photos.app;

import photos.app.photo.AppPhoto;
import photos.server.http.HttpRequest;
import photos.server.http.HttpResponse;

public class AppCenter
{
	// App path
	public final static String AppPath = System.getProperty("user.dir") + "/app-01/";
	
	// singleton
	private static AppCenter m_obj = new AppCenter();
	public  static AppCenter getInstance() { return m_obj; }
	
	// apps
	private AppLogin m_login = new AppLogin();
	private AppShare m_share = new AppShare();
	private AppPhoto m_photo = new AppPhoto();
	
	public AppCenter()
	{
		m_share.addShare(m_photo.getAppName(), m_photo.getShare());
	}
	
	// service
	public void service(HttpRequest req, HttpResponse rep)
			throws Exception
	{
		String uri = req.uri;
		
		if (uri.length() == 0 || uri.startsWith("share"))
			m_share.service(req, rep);
		else if (uri.startsWith("login"))
			m_login.service(req, rep);
		else if (uri.startsWith("photo"))
		{
			if (m_login.isLogin(req.cookie))
				m_photo.service(req, rep);
			else
				m_login.responseHtml(rep);
		}
		else if (uri.startsWith("favicon"))
			rep.sendFile("favicon.ico");
		else
			rep.send404();
	}
}
