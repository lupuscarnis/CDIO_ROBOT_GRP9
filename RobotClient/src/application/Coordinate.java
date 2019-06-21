package application;

public class Coordinate {
	boolean isAtWall = false;
	public boolean isAtWall() {
		return isAtWall;
	}

	public void setAtWall(boolean isAtWall) {
		this.isAtWall = isAtWall;
	}

	double x,y;
	enum type{	BALL, ROBOT, FENCE, GOAL, X	};

	
	public Coordinate(double x, double y) {
		this.x=x ;
		this.y=y ;
	}
	
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public Coordinate(double x, double y, String theType) {
		this.x = x;
		this.y = y;
		//Enums not working
		//this.type = type.valueOf(theType);
	}
	
}