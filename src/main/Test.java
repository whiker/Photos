package main;

import java.io.IOException;

import photos.server.Server;

public class Test
{
	public static void main(String[] args) throws IOException
	{
		test();
	}
	
	private static void test() throws IOException
	{
		Server server = new Server(8);
		server.start();
		
		System.out.println("start...");
		System.in.read();
		System.out.println("exit...");
		server.exit();
		System.out.println("exit");
	}
}
