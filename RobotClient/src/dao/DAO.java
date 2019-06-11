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
import connection.I_Connection;
import dto.I_DTO;

public class DAO implements I_DAO {
	private I_Connection c = null;
	private Socket s;
	private BufferedReader	in;
	private PrintWriter out;
	public DAO() {
	c = new Connection();
	s =	c.Connect();
	}
	
	/* (non-Javadoc)
	 * @see dao.I_DAO#sendData(dto.DTO)
	 */
	@Override
	public boolean sendData(I_DTO data) {
	if(!(c==null)) {
		
	
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
		return true;
		
	}
	
	return false;
	}
	
	
	

}

