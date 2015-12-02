package photos.app;

import photos.server.http.HttpRequest;
import photos.server.http.HttpResponse;

public interface App
{
	public void service(HttpRequest req, HttpResponse rep)
			 throws Exception;
	
	public String getAppName();
	
	public Share getShare();
}
