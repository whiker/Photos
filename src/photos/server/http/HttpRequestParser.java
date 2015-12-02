package photos.server.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;

import photos.iobuffer.MyInputBuffer;

public class HttpRequestParser
{
	private byte[]  m_line = new byte[MyInputBuffer.MaxLineLength];

	private MyInputBuffer m_in = new MyInputBuffer();
	
	private HttpRequest m_req;
	HttpRequestParser(HttpRequest req)
	{ m_req = req; }
	
	// Input
	void ready(InputStream in) throws Exception
	{
		m_in.ready(in);
	}
	
	// URL
	void parseUrl() throws Exception
	{
		int n = m_in.getLine(m_line);
		
		int i, j, k, l;
		for (i = 3;   i < n && m_line[i] != ' '; ++i);
		for (j = i+1; j < n && m_line[j] != ' '; ++j);
		
		if (j >= n)
			throw new Exception("error " + new String(m_line,0,n));
		if (m_line[i+1] == '/')
			++i;
		++i;
		
		for (k = i+1; k < j && m_line[k] != '?'; ++k);
		
		// uri
		m_req.uri = new String(m_line, i, k-i);
		for (l = i; l < k && m_line[l] != '%'; ++l);
		if (l < k)
			m_req.uri = URLDecoder.decode(m_req.uri, "UTF-8");
		for (l = i; l < k && m_line[l] != '.'; ++l);
		m_req.isStatic = l < k;
		
		// query
		if (k < j)
			m_req.query = new String(m_line, k+1, j-k-1);
	}
	
	// Header
	public void parseHeader() throws Exception
	{
		for (int n; ( n = m_in.getLine(m_line) ) > 0; )
		{
			String line = new String(m_line, 0, n);
			
			if (line.startsWith("Cookie"))
			{
				int i = 7;
				while (m_line[i] != '=') ++i;
				++i;
				
				String value = new String(m_line, i, n-i);
				m_req.cookie = Integer.parseInt(value);
			}
			else if (line.startsWith("Content-Length"))
			{
				int i = 15;
				while ( i < n && (m_line[i]<'1' || m_line[i]>'9') )
					++i;
				
				String value = new String(m_line, i, n-i);
				m_req.nContent = Integer.parseInt(value);
			}
		}
	}
	
	// Content
	int getContent() throws IOException
	{
		int n = m_in.directRead();
		if (n > 0)
		{
			m_req.nContent -= n;
			m_req.content = m_in.getBuf();
		}
		else
			m_req.nContent = -1;
		return n;
	}
}
