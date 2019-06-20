package behavior;

import java.util.ArrayList;
import java.util.List;

import application.CSystem;
import application.Coordinate;
import application.FXController;
import application.staticFXCont;
import dao.DAO;
import dao.I_DAO;
import dto.DTO;
import dto.I_DTO;
import objects.Ball;
import objects.BallList;
import objects.FrameSize;
import objects.Robot;

public class RobotController implements Runnable {
	int nrBalls = 0;
	int goals = 0;
	double ratio = 310;
	double robotDirection = 0;
	double frameHeight = 0;
	double frameWidth = 0;
	DAO dao;
	DTO dto;
	int iter = 0;

	CSystem cs;
	ArrayList<Coordinate> path;
	List<Ball> ballist;

	public RobotController() {
		dao = new DAO();
		dto = new DTO();
	}
	
	public void handleBallsToTheWall(int type, Coordinate robotFront,Coordinate robotBack,Coordinate ball){
		Coordinate intFrontOfBall;
		System.out.println("start of handleBall");
		if(type==1) {
			System.out.println("den fandt 1");
			//move to close in front of ball, perpendicular to the wall
			if(ball.getX()>100){
				intFrontOfBall = new Coordinate(ball.getX()+10,ball.getY());
				moveToPoint(intFrontOfBall);
				
			}else {
			intFrontOfBall = new Coordinate(ball.getX()-10,ball.getY());
			moveToPoint(intFrontOfBall);
			}
			moveRobotToBallAtWall(intFrontOfBall);
		//maybe some kind of move over and fucking get the ball. Needs to get updated position of robot
		//double dir = getDirection()
			
		}else if (type==2) {
			System.out.println("den fandt 2");
			//move to close in front of ball, perpendicular to the other wall
			if(ball.getY()>100){
				intFrontOfBall = new Coordinate(ball.getX(),ball.getY()+10);
				moveToPoint(intFrontOfBall);
				
			}else {
			intFrontOfBall = new Coordinate(ball.getX(),ball.getY()-10);
			moveToPoint(intFrontOfBall);
			}
			moveRobotToBallAtWall(intFrontOfBall);
		//maybe some kind of move over and fucking get the ball. Needs to get updated position of robot
		//double dir = getDirection()
			
		}else {
			System.out.println("den fandt 3");
			//move to close in front of ball, point "directly" towards the corner
			if(ball.getX()>100 && ball.getY()>100){
				intFrontOfBall = new Coordinate(ball.getX()+10,ball.getY()+10);
				moveToPoint(intFrontOfBall);
				
			}else if (ball.getX()<100 && ball.getY()>100) {
				intFrontOfBall = new Coordinate(ball.getX()-10,ball.getY()+10);
				moveToPoint(intFrontOfBall);
				
			}else if (ball.getX()<100 && ball.getY()<100) {
				intFrontOfBall = new Coordinate(ball.getX()-10,ball.getY()-10);
				moveToPoint(intFrontOfBall);
			}else{
				intFrontOfBall = new Coordinate(ball.getX()+10,ball.getY()-10);
				moveToPoint(intFrontOfBall);
				
			}
			
			moveRobotToBallAtWall(intFrontOfBall);
			send(-0.15,0,0,0);
		//maybe some kind of move over and fucking get the ball. Needs to get updated position of robot
		//double dir = getDirection()
			
		}
	}
	
	public int isBallAtWall(Coordinate ball){
		int output = 0;
		//checks whether ball is close to x y or neither wall
		// 0 = ball is out in the open (maybe close to cross)
		// 1 = ball has large or small x, close to sides with goals
		// 2 = ball has large or small y close to sides without goals
		// 3 = ball is in a corner
		
		System.out.println("is ball at wall when ball x "+ball.getX()+" y "+ball.getY()+" frame = "+frameWidth+" y "+frameHeight);
		//may need to change values getting compared
		
		
		if(ball.getX()<40 || ball.getX()>(frameWidth-40)) {
			output = 1;
		}
		if(ball.getY()<40 || ball.getY()>(frameHeight-40)) {
		output+=2;
		}
		
		return output;	
	}

	public void getView() {
		FXController fx = staticFXCont.getInstance().getfxInstance();
		boolean robotFound = false, ballsFound = false, frameFound = false;

		do {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			fx.runAnalysis(true);

			if (Robot.getInstance().getBackX() == 0 || Robot.getInstance().getFrontX() == 0) {
				System.err.println("Couldn't find robot");
				robotFound = false;
			} else {
				robotFound = true;
			}
			
			if (FrameSize.getInstance().getX() == 0 || FrameSize.getInstance().getY() == 0) {
				System.err.println("Couldn't find framesize");
				frameFound = false;
			} else {
				frameFound = true;
			}
			
			if (BallList.getInstance().getBallList().size() < 1) {
				System.err.println("Couldn't find Balls");
				ballsFound = false;
			} else {

				ballsFound = true;
			}

		} while (!(robotFound && ballsFound && frameFound));
		FrameSize framesize = FrameSize.getInstance();
		path = new ArrayList<Coordinate>();
		Robot rob = Robot.getInstance();
		BallList bl = BallList.getInstance();
		frameWidth = framesize.getX();
		frameHeight = framesize.getY();
		cs = new CSystem(framesize.getX(), framesize.getY());
		ballist = bl.getBallList();

		for (Ball ball : ballist) {
			if (ball.getX() != 0 && ball.getY() != 0) {
				System.out.println();
				cs.balls.add(new Coordinate(ball.getX(), ball.getY()));
			}

		}

		// front
		cs.robot.add(new Coordinate((int) rob.getFrontX(), (int) rob.getFrontY()));
		// back
		cs.robot.add(new Coordinate((int) rob.getBackX(), (int) rob.getBackY()));
		// call fx get everything

		// maybe just something like cs = the new coordinate system

	}

	public void scoreGoal() {

		double goalY = (frameWidth / 2);
		//only want like 10 in front of goal, but the border = 30
		double goalX = 40;
		Coordinate frontOfGoal = new Coordinate(goalX-0.3, goalY);
		// calculating direction to drive in front of goal
		System.out.println("front of goal is at   x "+goalX+" y "+goalY);
		double dir = calcDirection(cs.robot.get(0),cs.robot.get(1),frontOfGoal);
		
		send(0,dir,0,0);
		getView();
		// distance to in front of goal
		double distance = getDistance(cs.robot.get(0), frontOfGoal);
		// pixels get converted to cm
		send(distance,0,0,0);

		// System.out.println("vi sender "+dir+"distance"+distance+"for at komme foran
		// maal");
		
		// find direction to point towards goal
		getView();
		
		Coordinate goal = new Coordinate(0, cs.getYLength() / 2);
		
		 dir = calcDirection(cs.robot.get(0),cs.robot.get(1),goal);
		send(0,dir,0,0);
		// distance to in front of goal
		 distance = getDistance(cs.robot.get(0), goal);
		send(distance,0,180,0);
		
		System.out.println(goal.getX() + " goal tal " + goal.getY());
		// dir = getDirection(goal);

		System.out.println("vi sender " + dir + "distance for at komme parallelt med maalet");

		// send message to drive close to goal and release balls
		
		// can't remember which direction is which, but both need to turn the same
		// direction
		send(0,0,180,120);
		send(0,0,0,-120);
		send(-0.2,0,0,0);
		
	}

	public void run() {
		//noget man kan soege efter :D
		boolean firsttime = true;
		double dir = 10;
		int nrBalls =0;
		do {
			getView();
			path = findRoute();
			if(getDistance(cs.robot.get(0),path.get(0))>0.2) {
				
				isFar(path);
				
			}else {
				
				isClose(path);
				nrBalls++;
			}
			
		} while(true);


	}

	// check if can find balls

	// make sound signalling win and stop measuring time

	public double getDir(ArrayList<Coordinate> currentPath) {

		Coordinate nextBall = currentPath.get(0);
		double ballX = nextBall.getX();
		double ballY = nextBall.getY();
		System.out.println("Ball: " + ballX + " " + ballY);
		// direction needed for robot to move towards ball

		double X = (cs.robot.get(0).getX() + cs.robot.get(1).getX()) / 2;
		double Y = (cs.robot.get(0).getY() + cs.robot.get(1).getY()) / 2;
		Coordinate robotCenter = new Coordinate(X, Y);
		System.out.println("Robot center: " + robotCenter.getX() + " " + robotCenter.getY());
		System.out.println();
		System.out.println();
		// pixels get converted to cm

		double dir = 4;
		Coordinate temp = robotCenter;
		dir = calcDirection(cs.robot.get(0), robotCenter, nextBall);
		return dir;
		/*
		 * while(dir>2) {
		 * 
		 * while(cs.robot.get(0).getX()==temp.getX()
		 * &&cs.robot.get(0).getY()==temp.getY() ) { getView(); temp = new
		 * Coordinate(cs.robot.get(0).getX(),cs.robot.get(0).getY()); } dir =
		 * calcDirection(robotCenter,cs.robot.get(1),nextBall);
		 * 
		 * I_DTO dtoo = new DTO();
		 * 
		 * dtoo.setRotation((float) dir); dtoo.setDistance((float) 0);
		 * dtoo.setClawMove(0); dtoo.setBackClawMove(0);
		 * 
		 * 
		 * I_DAO data = new DAO(); data.sendData(dtoo);
		 * 
		 * data.reciveData(); }
		 * 
		 * I_DTO dtoo = new DTO();
		 * 
		 * dtoo.setRotation((float) 0); dtoo.setDistance((float) (distance-0.1));
		 * dtoo.setClawMove(180); dtoo.setBackClawMove(0);
		 * 
		 * 
		 * I_DAO data = new DAO(); data.sendData(dtoo);
		 * 
		 * data.reciveData();
		 * 
		 * dtoo = new DTO();
		 * 
		 * dtoo.setRotation((float) 0); dtoo.setDistance((float) 0.1); //may need
		 * seperate dto, to happen after movement dtoo.setClawMove(-180);
		 * dtoo.setBackClawMove(0);
		 * 
		 * data = new DAO(); data.sendData(dtoo);
		 * 
		 * data.reciveData();
		 * 
		 */
		/*
		 * I_DTO dtooo = new DTO(); dtooo.setDistance((float) 10);
		 * dtooo.setClawMove(-180);
		 * 
		 * I_DAO data2 = new DAO(); data2.sendData(dtooo);
		 */
	}

	public void moveToPoint(Coordinate newPoint) {
		
	// maybe needs to getView(); here, maybe it should be left to send or whoever calls it, discuss tomorrow	
System.out.println("der blev moved to point");
		double X = (cs.robot.get(0).getX() + cs.robot.get(0).getX()) / 2;
		double Y = (cs.robot.get(1).getY() + cs.robot.get(1).getY()) / 2;
		Coordinate robotCenter = new Coordinate(X, Y);

		// pixels get converted to cm

		double dir = 0;
		double dist =0;
		
		//if robotcenter is returned, there were no obstacles, if there were, it returns the coordinate in between
		//that should be moved to, before moving on
		Coordinate newCoordinate = detectObstacle(cs.robot.get(0),cs.robot.get(1),newPoint);
if((newCoordinate==robotCenter)) {
	//moveToPoint(newCoordinate); just move to the point you should
	dir =calcDirection(cs.robot.get(0), robotCenter, newPoint);
	dist = getDistance(cs.robot.get(0),newPoint);
	send(dist,dir,0,0);
		}else {
		//first move to the stop on the way, coordinate to avoid obstacle
		dir =calcDirection(cs.robot.get(0), robotCenter, newCoordinate);
		dist = getDistance(cs.robot.get(0),newCoordinate);
		send(dist,dir,0,0);
		
			//now move to the point you wanted
		dir =calcDirection(cs.robot.get(0), robotCenter, newPoint);
		dist = getDistance(cs.robot.get(0),newPoint);
		send(dist,dir,0,0);
		

		}	
	}

	public Coordinate detectObstacle(Coordinate robotFront, Coordinate robotBack, Coordinate ball) {
		double dir = calcDirection(robotFront, robotBack, ball);
		// need working frame sizes, not sure these work
		Coordinate cross = new Coordinate(frameWidth / 2, frameHeight / 2);
		Coordinate robotCenter = new Coordinate(robotFront.getX() + robotBack.getX(),
				robotFront.getY() + robotBack.getY());
		for (int i = 0; i < frameWidth / 2; i++) {

			if (robotFront.getX() * i > (cross.getX() - 30) && robotFront.getX() * i < cross.getX() + 30) {

				if (robotFront.getY() * i > (cross.getY() - 30) && robotFront.getY() * i < cross.getY() + 30) {
					// you are on a collision course with the cross

					if (Math.abs(robotCenter.getX() - cross.getX()) > Math.abs(robotCenter.getY() - cross.getY())) {
						//moveToPoint(new Coordinate(ball.getX(), robotCenter.getY()));
						return new Coordinate(ball.getX(), robotCenter.getY());
						// if center -x is greater than center-y move to other coordinate
					} else {
						//moveToPoint(new Coordinate(ball.getY(), robotCenter.getX()));
						return new Coordinate(ball.getY(), robotCenter.getX());
					}

				}

				// if(robotFront.getX()*i && robotFront.getY()*i)

			}

		}
		//this return is probably why moveToPoint had the if(coordinate == robotCenter)
		return robotCenter;
	}
			
	public ArrayList<Coordinate> findRoute() {

		Coordinate robotFront = cs.robot.get(0);
		Coordinate robotBack = cs.robot.get(1);
		double X = (double) (robotFront.getX() - robotBack.getX());
		double Y = (double) (robotFront.getY() - robotBack.getY());
		double min = 10000;
		Coordinate closest = new Coordinate(0, 0);
		int closestid = -1;
		ArrayList<Coordinate> sortedList = new ArrayList<Coordinate>();
		robotDirection = Math.toDegrees(Math.atan2(Y, X));

		// center of robot
		X = (robotFront.getX() + robotBack.getX()) / 2;
		Y = (robotFront.getY() + robotBack.getY()) / 2;
		Coordinate robotCenter = new Coordinate(X, Y);

		// fuck it, right now it will just find the nearest ball

		while (!cs.balls.isEmpty()) {

			for (int i = 0; i < cs.balls.size(); i++) {
					//used to be getDistance instead of calcDIrection
				if (getDistance(cs.balls.get(i),robotCenter) < min) {
					min = getDistance(cs.balls.get(i),robotCenter); //calcDirection(robotFront, robotCenter,cs.balls.get(i));
					closest = cs.balls.get(i);
					System.out.println("bolden er "+cs.balls.get(i).getX()+" y "+cs.balls.get(i).getY());
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
		double X = a.getX() - b.getX();
		double Y = a.getY() - b.getY();
		return (Math.sqrt(X * X + Y * Y))/ratio;
		
		

	}
	
	public void isClose(ArrayList<Coordinate> thePath) {
		double dir = 100;
	do {	
		getView();
		
		//if ball is near wall, do something different
		
		dir = calcDirection(cs.robot.get(0),cs.robot.get(1),thePath.get(0));
		send(0,dir,0,0);
		
	} while (!((dir <= 4) && (dir >= -4))) ;
			 
	float distance = (float) ((getDistance(cs.robot.get(0), thePath.get(0))) );
	System.out.println("Im driving, im doing it " + distance);
	
	send(distance-0.1,0,0,0);	
	
	send(0,0,180,0);
	
	send(0.2,0,0,0);
	
	send(0,0,-180,0);
			 
}
	
	public void isFar(ArrayList<Coordinate> thePath) {
		double dir = 100;
		do {
			getView();
			
			//if ball is near wall, do something different
			
			dir = calcDirection(cs.robot.get(0),cs.robot.get(1),thePath.get(0));
			send(0,dir,0,0);

			
		}while (!((dir <= 20) && (dir >= -20)));
		
		double distance =  ((getDistance(cs.robot.get(0), thePath.get(0))) );
		System.out.println("Im driving, im doing it " + distance);
		if(distance>30) {
			send(distance/2,0,0,0);
		}else {
			send(0.15,0,0,0);
		}
		
		
		
		
	}

	public double calcDirection(Coordinate robotFront, Coordinate robotBack, Coordinate ball) {

		Coordinate vector1 = new Coordinate((robotFront.getX() - robotBack.getX()),
				(robotFront.getY() - robotBack.getY()));
		Coordinate vector2 = new Coordinate((ball.getX() - robotBack.getX()), (ball.getY() - robotBack.getY()));

		// double angle = (Math.atan2(vector2.getY(),vector2.getX()) -
		// Math.atan2(vector1.getY(),vector1.getX()));

		double dot = vector1.getX() * vector2.getX() + vector1.getY() * vector2.getY(); // dot product between [x1, y1]
																						// and [x2, y2]
		double det = vector1.getX() * vector2.getY() - vector1.getY() * vector2.getX(); // determinant
		double angle = Math.atan2(det, dot); // atan2(y, x) or atan2(sin, cos)
		angle = Math.toDegrees(angle);
		angle = 0 - angle;
		System.out.println("signes ankel " + angle);
		return angle;

	}
	
	public void send(double distance, double direction, double claw, double backClaw) {
		//maybe send should not always receive data, which makes it wait, only some things need to wait
		/*
		if(direction<10 && direction>-10) {
			if(direction>0) {
				direction = 2;
			}else {
				direction = -2;
			}
		}
		*/
		dto.clearData();
		dto.setDistance((float)distance);
		dto.setRotation((float)direction);
		dto.setClawMove((float)claw);
		dto.setBackClawMove((float)backClaw);
		System.out.println("i sent "+dto.toString());
		dao.sendData(dto);
		dao.reciveData();
		
	}
	
	public void moveRobotToBallAtWall(Coordinate place) {
		dao.reciveData();
		getView();
		
		double X = (cs.robot.get(0).getX() + cs.robot.get(1).getX()) / 2;
		double Y = (cs.robot.get(0).getY() + cs.robot.get(1).getY()) / 2;
		Coordinate robotCenter = new Coordinate(X, Y);
		double dir = calcDirection(cs.robot.get(0),robotCenter,place);
		//make sure it gets far enough, you'll hit the wall anyways :)
		double dist = getDistance(cs.robot.get(0),place)+15;
		System.out.println("sends from moveRobot "+dir);
		dto.clearData();
		dto.setClawMove(180);
		dto.setRotation((float) dir);
		dto.setDistance((float)(dist-0.1));
		dao.sendData(dto);
		dao.reciveData();
		System.out.println("gets the ball from moveRobot");
		dto.clearData();
		
		dto.setDistance((float)(0.15));
		dao.sendData(dto);
		dao.reciveData();
		
		send(-0.1,0,-180,0);
		
		
	}

}
