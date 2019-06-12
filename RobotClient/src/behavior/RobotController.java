package behavior;

import java.util.ArrayList;
import java.util.List;

import application.CSystem;
import application.Coordinate;
import behavior.RouteFinder.Node;
import dao.DAO;
import dao.I_DAO;
import dto.DTO;
import dto.I_DTO;
import objects.Ball;
import objects.BallList;
import objects.Robot;

public class RobotController {
	int nrBalls =0;
	int goals=0;
	double robotDirection = 0;
	
	CSystem cs;
	ArrayList<Coordinate> path;
	List<Ball> ballist;
	
	public RobotController() {
		
	}
	
	public void getView() {
		path = new ArrayList<Coordinate>();
	//hardcoded test!!! 
	cs = new CSystem(800,600);
	
	/*
	cs.balls.add(new Coordinate(85,7));
	cs.balls.add(new Coordinate(200,64));
	cs.balls.add(new Coordinate(600,245));
	*/
	 Robot rob=Robot.getInstance();
	 BallList bl = BallList.getInstance();
	 
	 ballist = bl.getBallList();
	 
	 cs.balls.add(new Coordinate (ballist.get(0).getX(), ballist.get(0).getY()));
	 cs.balls.add(new Coordinate (ballist.get(1).getX(), ballist.get(1).getY()));
	//front
	 //System.out.println(cs.balls.size());
	 System.out.println("Ballist"+ballist.get(0).getX()+"dens y er"+ballist.get(0).getY());
	 System.out.println("Ballist nummer 2 er "+ballist.get(1).getX()+"dens y er"+ballist.get(1).getY());
	 System.out.println("her er en ball"+cs.balls.get(0).getX());
	cs.robot.add(new Coordinate((int)rob.getFrontX(),(int) rob.getFrontY()));
	//back
	cs.robot.add(new Coordinate((int)rob.getBackX(),(int)rob.getBackY()));
	System.out.println("front robot x er "+cs.robot.get(0).getX()+"og y er "+cs.robot.get(0).getY());
	System.out.println("back robot x er "+cs.robot.get(1).getX()+"og y er "+cs.robot.get(1).getY());
	//call fx get everything
	
	// maybe just something like cs = the new coordinate system
	
		
	}
	
	public void start() {
		
		
		getView();
		
		while(goals<10) {
			
			if(nrBalls>5) {
				//score goal
				goals +=nrBalls;
				nrBalls=0;
			}else {
			
		path = findRoute();
		//System.out.println("i need ball"+path.size());
		
		getBall(path);
		nrBalls++;
		
		
		
			}
		}
		//check if can find balls
		
		//make sound signalling win and stop measuring time
		
		
	}
	
	public void getBall(ArrayList<Coordinate> currentPath) {
		
		Coordinate nextBall = currentPath.get(0);
		double ballX = nextBall.getX();
		double ballY = nextBall.getY();
		//System.out.println("i got next ball");
		//direction needed for robot to move towards ball
		double dir = Math.toDegrees(Math.atan((ballY-cs.robot.get(0).getY())/(ballX-cs.robot.get(0).getX())));
		double a2 = measureDist(nextBall, cs.robot.get(0))*measureDist(nextBall, cs.robot.get(0));
		double c2 = measureDist(nextBall, cs.robot.get(1))*measureDist(nextBall, cs.robot.get(1));
		double b2 = measureDist(cs.robot.get(0) ,cs.robot.get(0))*measureDist(cs.robot.get(0), cs.robot.get(0));
		double denominator = 2*measureDist(nextBall, cs.robot.get(0))*measureDist(cs.robot.get(0) ,cs.robot.get(0));
		dir = (a2+c2-b2)/denominator;
		dir = Math.acos(dir);
		//dir = Math.toDegrees()
		//System.out.println("got measurement");
		double distance = measureDist(cs.robot.get(0), nextBall);
		//pixels get converted to cm
		distance = distance*3.1;
		
		System.out.println("vi sender "+dir+"distance"+distance);
		
		I_DTO dtoo = new DTO();
		
		
		dtoo.setRotation((float) dir);
		dtoo.setDistance((float) distance);
		dtoo.setClawMove(180);
		System.out.println(dtoo.toString());
		
		I_DAO data = new DAO();  
		while(data.sendData(dtoo)) {
			
			
		}
		data.reciveData();
		/*
		I_DTO dtooo = new DTO();
		dtooo.setDistance((float) 100);
		dtooo.setClawMove(-180);
		
		I_DAO data2 = new DAO();  
		data2.sendData(dtooo);
		*/
	}
	
	
	public ArrayList<Coordinate> findRoute() {
		//System.out.println("er jeg tom"+cs.balls.size());
		//System.out.println("finder rute");
    	Coordinate robotFront = cs.robot.get(0);
    	Coordinate robotBack = cs.robot.get(1);
    	double X = (double) (robotFront.getX()-robotBack.getX());
    	double Y =  (double) (robotFront.getY()-robotBack.getY());
    	double min = 10000;
    	Coordinate closest = new Coordinate(0,0);
    	int closestid =-1;
    	ArrayList<Coordinate> sortedList = new ArrayList<Coordinate>();
		robotDirection =  Math.toDegrees(Math.atan2(Y, X));
		
		//center of robot
		X = (robotFront.getX()+robotBack.getX())/2;
		Y = (robotFront.getY()+robotBack.getY())/2;
		Coordinate robotCenter = new Coordinate(X,Y);
		
		//fuck it, right now it will just find the nearest ball
		//System.out.println("er udenfor while");
		while(!cs.balls.isEmpty()) {
			//System.out.println("er i while");
		for(int i =0; i< cs.balls.size(); i++) {
			//System.out.println("er i for");
		if(measureDist(cs.balls.get(i),robotCenter)<min) {
		min = measureDist(cs.balls.get(i),robotCenter);
		closest = cs.balls.get(i);
		closestid = i;
		}
		
		
		
		}
		sortedList.add(cs.balls.get(closestid));
		cs.balls.remove(closestid);
		min = 10000;
		//System.out.println("added"+closestid);
		
		}
		return sortedList;
    	
    }

	public double measureDist(Coordinate a, Coordinate b) {
		double X = a.getX()-b.getX();
		double Y = a.getY()-b.getY();
		return Math.sqrt(X*X+Y*Y);
		
		
	}
	//gang pixel med 3,1 for at faa cm

public class Node {
int x=0,y=0,id=0,deg=361;
boolean marked =false;

public Node(int x, int y) {

	this.x = x;
	this.y = y;
}


	
}
	
	
	
	
	

}
