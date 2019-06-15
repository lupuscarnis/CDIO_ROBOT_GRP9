package behavior;

import java.util.ArrayList;
import java.util.List;

import application.CSystem;
import application.Coordinate;
import dao.DAO;
import dao.I_DAO;
import dto.DTO;
import dto.I_DTO;
import objects.Ball;
import objects.BallList;
import objects.FrameSize;
import objects.Robot;

public class RobotController {
	int nrBalls =0;
	int goals=0;
	double ratio = 310;
	double robotDirection = 0;
	
	CSystem cs;
	ArrayList<Coordinate> path;
	List<Ball> ballist;
	
	public RobotController() {
		
	}
	
	public void getView() {
		path = new ArrayList<Coordinate>();
	
	
	
	 FrameSize framesize = FrameSize.getInstance();
	 Robot rob=Robot.getInstance();
	 BallList bl = BallList.getInstance();
	 System.out.println("getView kald "+rob.getBackX()+rob.getFrontX());
	 
	 cs = new CSystem(framesize.getX(),framesize.getY());
	 
	 ballist = bl.getBallList();
	 
	 System.out.println("the size of balls"+ballist.size());
	 for(int i =0; i<ballist.size(); i++) {
		 if(!(ballist.get(i).getX()==0  && ballist.get(i).getY()==0)) {
		 cs.balls.add(new Coordinate (ballist.get(i).getX(), ballist.get(i).getY()));
		 System.out.println("ball found in robotcontroller X "+(ballist.get(i).getX()+" og Y"+ballist.get(i).getY()));
		 }
		
	 }
	 
	 
	 
	//front
	 //System.out.println(cs.balls.size());
	 //System.out.println("Ballist nummer 2 er "+ballist.get(1).getX()+"dens y er"+ballist.get(1).getY());
	 System.out.println("her er en ball 0's X "+cs.balls.get(0).getX()+" og Y "+cs.balls.get(0).getX());
	cs.robot.add(new Coordinate((int)rob.getFrontX(),(int) rob.getFrontY()));
	//back
	cs.robot.add(new Coordinate((int)rob.getBackX(),(int)rob.getBackY()));
	System.out.println("front robot x er "+cs.robot.get(0).getX()+"og y er "+cs.robot.get(0).getY());
	System.out.println("back robot x er "+cs.robot.get(1).getX()+"og y er "+cs.robot.get(1).getY());
	//call fx get everything
	
	// maybe just something like cs = the new coordinate system
	
		
	}
	
	public void scoreGoal() {
		
	double goalY = cs.getYLength()/2;
	//probably needs 
	double goalX = 10;
	Coordinate frontOfGoal = new Coordinate(goalX,goalY);
	//calculating direction to drive in front of goal
		
	double	dir = getDirection(frontOfGoal);
	
	
		//distance to in front of goal
		double distance = getDistance(cs.robot.get(0), frontOfGoal);
		//pixels get converted to cm
		distance = distance*ratio;
		
System.out.println("vi sender "+dir+"distance"+distance+"for at komme foran maal");
		//send message to drive in front of goal
		/*
		I_DTO dtoo = new DTO();
		
		dtoo.setRotation((float) dir);
		dtoo.setDistance((float) distance);
		System.out.println(dtoo.toString());
		
		I_DAO data = new DAO();  
		data.sendData(dtoo);
			
			
		
		data.reciveData();
		
		//find direction to point towards goal
		
		Coordinate goal = new Coordinate(0,cs.getYLength()/2);
	System.out.println(goal.getX()+" goal tal "+goal.getY());
		//dir = getDirection(goal);
		
		System.out.println("vi sender "+dir+"distance for at komme parallelt med maalet");
		
		//send message to drive close to goal and release balls
		
		I_DTO scoreDTO = new DTO();
		
		scoreDTO.setRotation((float) dir);
		scoreDTO.setDistance((float) 5);
		//can't remember which direction is which, but both need to turn the same direction
		scoreDTO.setClawMove(360);
		scoreDTO.setBackClawMove(120);
		scoreDTO.setBackClawMove(-120);
		
		System.out.println(scoreDTO.toString());
		
		I_DAO data2 = new DAO();  
		data2.sendData(scoreDTO);
			
			
		
		data2.reciveData();
		
		
		
		
		
	}
	
	public void start() {
		
		int counter =0;
		getView();
		
		while(cs.robot.size()>0) {
			//needs to be in here to get new location of balls
			counter++;
			if(counter>10) {
				break;
			}
			getView();
			if(nrBalls>5) {
				//scoreGoal();
				goals +=nrBalls;
				nrBalls=0;
			}else {
			
		path = findRoute();
		
		
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
		//direction needed for robot to move towards ball
		double X = (cs.robot.get(0).getX()+cs.robot.get(0).getX())/2;
		double Y = ( cs.robot.get(1).getY()+ cs.robot.get(1).getY())/2;
		Coordinate robotCenter = new Coordinate(X,Y);
		
		double distance = getDistance(cs.robot.get(0), nextBall);
		//pixels get converted to cm
		distance = distance/ratio;
		
		
		
		double dir = 4;
		Coordinate temp = robotCenter;
		while(dir>2) {
			
		while(cs.robot.get(0).getX()==temp.getX() &&cs.robot.get(0).getY()==temp.getY() ) {
			getView();
			 temp = new Coordinate(cs.robot.get(0).getX(),cs.robot.get(0).getY());
		}
		dir = calcDirection(robotCenter,cs.robot.get(1),nextBall);
		
		I_DTO dtoo = new DTO();
		
		dtoo.setRotation((float) dir);
		dtoo.setDistance((float) 0);
		dtoo.setClawMove(0);
		dtoo.setBackClawMove(0);
	
		
		I_DAO data = new DAO();  
		data.sendData(dtoo);
		
		data.reciveData();
		}
		
I_DTO dtoo = new DTO();
		
		dtoo.setRotation((float) 0);
		dtoo.setDistance((float) (distance-0.1));
		dtoo.setClawMove(180);
		dtoo.setBackClawMove(0);
	
		
		I_DAO data = new DAO();  
		data.sendData(dtoo);
		
		data.reciveData();
		
dtoo = new DTO();
		
		dtoo.setRotation((float) 0);
		dtoo.setDistance((float) 0.1);
		//may need seperate dto, to happen after movement
		dtoo.setClawMove(-180);
		dtoo.setBackClawMove(0);
		
		data = new DAO();  
		data.sendData(dtoo);	
		
		data.reciveData();
		
		
		/*
		I_DTO dtooo = new DTO();
		dtooo.setDistance((float) 10);
		dtooo.setClawMove(-180);
		
		I_DAO data2 = new DAO();  
		data2.sendData(dtooo);
		*/
	}
	
	
	public ArrayList<Coordinate> findRoute() {
		
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
		
		while(!cs.balls.isEmpty()) {
			
		for(int i =0; i< cs.balls.size(); i++) {
			
		if(getDistance(cs.balls.get(i),robotCenter)<min) {
		min = getDistance(cs.balls.get(i),robotCenter);
		closest = cs.balls.get(i);
		closestid = i;
		}
		
		
		
		}
		sortedList.add(cs.balls.get(closestid));
		cs.balls.remove(closestid);
		min = 10000;
	
		}
		return sortedList;
    	
    }

	public double getDistance(Coordinate a, Coordinate b) {
		double X = a.getX()-b.getX();
		double Y = a.getY()-b.getY();
		return Math.sqrt(X*X+Y*Y);
		
		
	}
	

	
	public double calcDirection(Coordinate robotFront, Coordinate robotBack, Coordinate ball){
	
		
	
		
		
		Coordinate vector1 = new Coordinate( (robotFront.getX()-robotBack.getX()), (robotFront.getY()-robotBack.getY()));
		Coordinate vector2 = new Coordinate( (ball.getX()-robotBack.getX()), (ball.getY()-robotBack.getY()));
		
		double upper = vector1.getX()*vector2.getX()+vector1.getY()*vector2.getY();
		double lower = getDistance(robotBack,robotFront)*getDistance(robotBack,ball);
		
	//double angle = (Math.atan2(vector2.getY(),vector2.getX()) - Math.atan2(vector1.getY(),vector1.getX()));
	System.out.println();
	
		double dot = vector1.getX()*vector2.getX() + vector1.getY()*vector2.getY();      // dot product between [x1, y1] and [x2, y2]
				double det = vector1.getX()*vector2.getY() - vector1.getY()*vector2.getX();      // determinant
				double angle = Math.atan2(det, dot);  // atan2(y, x) or atan2(sin, cos)
				angle = Math.toDegrees(angle);
		angle = 0-angle;
	System.out.println("signes ankel "+angle);
	return angle;
		
	}
	



	
	
	
	
	

}
