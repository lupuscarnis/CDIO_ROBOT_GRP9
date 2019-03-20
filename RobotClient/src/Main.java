import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import gui.*;
public class Main {

	public static void main(String[] args) throws UnknownHostException, IOException {

		
		
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GUI_Frame.createAndShowGUI();
                
            }
        });
	}
	
}