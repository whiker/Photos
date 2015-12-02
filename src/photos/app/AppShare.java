package photos.app;

import photos.server.http.HttpRequest;
import photos.server.http.HttpResponse;

public class AppShare implements App
{
	//private String m_appName;
	private Share  m_share;
	
	public void addShare(String appName, Share share)
	{
		//m_appName = "/" + appName;
		m_share = share;
	}
	
	@Override
	public void service(HttpRequest req, HttpResponse rep)
			throws Exception
	{
		String uri = req.uri;
		
		if (uri.length() > 6)
		{
			byte[] ret = m_share.share(uri.substring(6));
			if (ret == null)
				rep.send404();
			else
				rep.send(ret, ret.length);
		}
		else
			rep.sendFile("share/share.html");
	}
	
	@Override
	public String getAppName() { return "share"; }
	
	@Override
	public Share getShare() { return null; }
}
