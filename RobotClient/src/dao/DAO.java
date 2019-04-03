package dao;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import connection.Connection;
import dto.DTO;

public class DAO {
	private Connection c = null;
	private Socket s;
	private BufferedReader	in;
	private PrintWriter out;
	public DAO() {
	c = new Connection();
	s =	c.Connect();
	}
	
	public void sendData(DTO data) {
			
		try {
			
			out = new PrintWriter(s.getOutputStream(), true);
			
		} catch (IOException e) {
			System.out.println("Failed getting output stream");
			e.printStackTrace();
		}
		try {
			 in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		} catch (IOException e) {
		System.out.println("Failed getting input stream");
			e.printStackTrace();
		}
		
		
		out.println(data.toString());
		
	}
	
	
	
	
	

}

