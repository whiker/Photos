package photos.server.http;

import java.io.InputStream;

public class HttpRequest
{
	public String  uri;
	public String  query;
	public boolean isStatic;
	
	public int cookie;
	
	int nContent;
	public byte[] content;
	
	// parser
	private HttpRequestParser m_parser =
			new HttpRequestParser(this);
	
	// ServiceTask
	public void ready(InputStream in) throws Exception
	{
		uri = query = null;
		cookie = nContent = -1;
		
		m_parser.ready(in);
		m_parser.parseUrl();
		m_parser.parseHeader();
	}
	
	// content
	public int getContent() throws Exception
	{
		if (nContent <= 0)
			return nContent;
		
		int n = m_parser.getContent();
		return n;
	}
}
