import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws UnknownHostException, IOException {

	
		
		
		Socket socket = new Socket("192.168.43.69", 4444);
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		if (socket.isConnected() == true) {
			System.out.println("Connection established");

		} else {
			System.out.println("Connection Failed!");
		}
		Scanner key = new Scanner(System.in);
		String userInput= "x";
		
		 
		  
		  
		out.println(userInput);
		while (!(userInput.equals("peace"))) {
			userInput = key.nextLine();
			out.println(userInput);

		}

	}
}