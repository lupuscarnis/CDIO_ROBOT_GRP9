import java.io.IOException;
import java.net.UnknownHostException;
import connection.*;
import gui.GUI_Frame;
public class Main {

	public static void main(String[] args) throws UnknownHostException, IOException {
/*
		//commands com = new commands();
		//com.start();
	
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GUI_Frame.createAndShowGUI();
                
            }
        });
			
			*/
		Connection con = new Connection();
		con.Connect("", 4444);
	 
			
	}
	
}