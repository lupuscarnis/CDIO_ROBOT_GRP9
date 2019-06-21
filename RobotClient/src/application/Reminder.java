package application;
import java.util.Timer;
import java.util.TimerTask;

import objects.Robot;



	public class Reminder {
	  Timer timer;
	  

	  public Reminder(int seconds) {
	    timer = new Timer();
	    timer.schedule(new RemindTask(), seconds * 1000);
	  }

	  class RemindTask extends TimerTask {
		  
		  
		  
	    public void run() {
	      Robot.getInstance().setStopRobot(true);
	      timer.cancel(); //Terminate the timer thread
	    
	    }
	  }

	
	}