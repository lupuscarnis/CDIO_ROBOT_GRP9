package behavior;
import java.util.ArrayList;

import dao.DAO;
import dao.I_DAO;
import dto.DTO;
import dto.I_DTO;
import objects.Ball;
import objects.Robot;

public class RouteFinder implements I_RouteFinder{
	static int test = 0;
	 private static ArrayList nodes = new ArrayList<Node>();
	 
	 /* (non-Javadoc)
	 * @see behavior.I_RouteFinder#getABall(objects.Ball, objects.Robot)
	 */
	@Override
	public void getABall(Ball ball, Robot robot) {
		 //this method should be called on the next ball to collect
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
	
	dist = dist/1000;
	
	I_DTO dtoo = new DTO();
	dtoo.setRotation((float) dir);
	dtoo.setDistance((float) dist-100);
	dtoo.setClawMove(180);
	
	I_DAO data = new DAO();  
	data.sendData(dtoo);
	/*
	dtoo.setRotation((float) 0);
	dtoo.setDistance((float) 100);
	dtoo.setClawMove(-180);
	
	
	data.sendData(dtoo);
	*/
	
	
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
	    
	    /* (non-Javadoc)
		 * @see behavior.I_RouteFinder#findRoute(behavior.Node[], double)
		 */
	    @Override
		public Node[] findRoute(Node[] nodes, double angle/*maybe its own poisition, if not first in array*/) {
	    	Node robot = nodes[0];
	    	Node next;
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
	    			next =nodes[i];
	    		}
	    		
	    			
	    		}
	    		//else {return den mindste fra arrayet}
	    	}
	    	
	    	
	    	
			return nodes;
	    	
	    }


public class Node {
	int x=0,y=0,id=0,deg=361;
	boolean marked =false;

	public Node(int x, int y) {

		this.x = x;
		this.y = y;
	}
	
	public double measureDist(Node ball) {
		int X = x-ball.x;
		int Y = y-ball.y;
		return Math.sqrt(X*X+Y*Y);
		
	}

}

}
