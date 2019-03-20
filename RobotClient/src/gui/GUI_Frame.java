package gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.IconUIResource;

/* FrameDemo.java requires no other files. */
public class GUI_Frame extends JPanel implements ActionListener{
	static Label s;
	protected JTextArea textArea;
	/**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    public static void createAndShowGUI() {
    	s = new Label("Sensor input");
    	JButton button = new JButton("Press");
    	JFrame frame = new JFrame("Robot GUI");
    	button.setSize(new Dimension(100,100));
    	s.setSize(new Dimension(200,100));
    	
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(s);
        frame.getContentPane().add(button);
        frame.addKeyListener(new KeyListener() {
 
			
			@Override
			public void keyTyped(KeyEvent e) {
				
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				System.out.println(KeyEvent.getKeyText(e.getKeyCode()));
				
			}
		});
        
        JLabel emptyLabel = new JLabel("");
        emptyLabel.setPreferredSize(new Dimension(300, 300));
        frame.getContentPane().add(emptyLabel, BorderLayout.CENTER);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	

}