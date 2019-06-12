package connection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import dao.DAO;
import dao.I_DAO;
import dto.DTO;
import dto.I_DTO;

public class ConsoleOutput implements Runnable{
	String input = "hello";
	public ConsoleOutput() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		I_DAO dao = new DAO();
		I_DTO sheet = new DTO();
	
		while(!input.equals("peace")) {
				
			System.out.println("Input format: Distance:Rotation:Arm_Rotation:BackClawMove");
			Scanner std = new Scanner(System.in);
			input = std.nextLine();
			
			ArrayList<String> splitString= new ArrayList<String>(Arrays.asList(input.split(":")));
			sheet.setDistance(Float.parseFloat(splitString.get(0)));
			sheet.setRotation(Float.parseFloat(splitString.get(1)));
			sheet.setClawMove(Float.parseFloat(splitString.get(2)));
			sheet.setBackClawMove(Float.parseFloat(splitString.get(3)));
			sheet.setBallpickup(false);
			dao.sendData(sheet);
			dao.reciveData();
			}
	
	}

}
