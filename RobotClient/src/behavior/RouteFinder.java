package behavior;
import java.util.ArrayList;

import objects.Ball;
import objects.Robot;

public class RouteFinder{
	static int test = 0;
	 private static ArrayList nodes = new ArrayList<Node>();
	 
	 public void getABall(Ball ball, Robot robot) {
	double ballX = ball.getX();
	double ballY = ball.getY();
	// front = x1, y1
	double robotDir = Math.toDegrees(Math.atan((robot.getBackY()-robot.getFrontY())/(robot.getBackX()-robot.getFrontX())));
	
	//direction needed for robot to move towards ball
	double dir = Math.toDegrees(Math.atan((ballY-robot.getFrontY())/(ballX-robot.getFrontX())));
	
	//calculating distance from robot front to ball
	double doubleDist = (ballX-robot.getFrontX())*(ballX-robot.getFrontX())+(ballY-robot.getFrontY())*(ballY-robot.getFrontY());
	//actual distance may have to be converted to a different unit
	double dist = Math.sqrt(doubleDist);
	
	
	
	 }

	 public RouteFinder() {
		 
		 
	 }
	 
	 
	    public static void add(Node node) {
	        nodes.add(node);
	    }
	    
	    public static Node get(int index){
	        return (Node)nodes.get(index);
	    }
	    
	    public static int numberOfCities(){
	        return nodes.size();
	    }
	    
	    /* just used for testing, it got in nicolais way... now it is no more
	    public static void main(String[] args) {
	    	System.out.println("start");
	    	for(int i =0; i<3628800;i++) {
	    		
	    		test+=i;
	    		
	    	}
	    	System.out.println(test);
	    }
	    */
	    
	    public Node[] findRoute(Node[] nodes, double angle/*maybe its own poisition, if not first in array*/) {
	    	Node robot = nodes[0];
	double currentMin =361;
	    	 double[] angles = new double[nodes.length-1];
	    	 ArrayList<Double> degs = new ArrayList<Double>();
	    	 double smallest = 1000;
	    	
	    	for(int i = 1; i<nodes.length-1; i++) {
	    		int X = nodes[i].x - robot.x;
	    		int Y = nodes[i].y - robot.y;
	    		double deg =  Math.atan2(Y, X);
	    		angles[i] = Math.toDegrees(deg);
	    		degs.add(Math.toDegrees(deg));
	    		
	    		
	    }
	    	degs.sort(null);
	    	//CHANGE to use nodes own distance
	    	for(int i = 1; i<nodes.length-1; i++) {
	    		if(degs.indexOf(0)-degs.indexOf(i)>-20 && degs.indexOf(0)-degs.indexOf(i)<20) {
	    		if(robot.measureDist(nodes[i])<smallest) {
	    			smallest = robot.measureDist(nodes[i]);
	    		}
	    		
	    			
	    		}
	    		//else {return den mindste fra arrayet}
	    	}
	    	
	    	
	    	
			return nodes;
	    	
	    }
}
