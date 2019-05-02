package connection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import dao.DAO;
import dto.DTO;

public class ConsoleOutput implements Runnable{
	String input = "hello";
	public ConsoleOutput() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		DAO dao = new DAO();
		DTO sheet = new DTO();
	
		while(!input.equals("exit")) {
				
			System.out.println("Input format: Distance:Rotation");
			Scanner std = new Scanner(System.in);
			input = std.nextLine();
			
			ArrayList<String> splitString= new ArrayList<String>(Arrays.asList(input.split(":")));
			sheet.setDistance(Float.parseFloat(splitString.get(0)));
			sheet.setRotation(0);
			sheet.setBallpickup(false);
			dao.sendData(sheet);
			System.out.println(splitString.get(0));
			}
	
	}

}
