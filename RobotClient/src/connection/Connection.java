package connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Connection {
	private int port  = 4444;
	private String ip = "IP HERE";
	
	public Socket Connect()  {			
	
		
	Socket socket = null;
	try {
		socket = new Socket(ip, port);
	} catch (UnknownHostException e) {
		System.out.println("Host not found On Ip Adress"+ ip);
		e.printStackTrace();
	} catch (IOException e) {
		System.out.println("IO Exception!");
		e.printStackTrace();
	}
	if (socket.isConnected() == true) {
		System.out.println("Connection established");

	} else {
		System.out.println("Connection Failed!");
	}

	return socket;
}

	
}
