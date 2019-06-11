package application;

public class Coordinate {
	
	int x,y;
	enum type{	BALL, ROBOT, FENCE, GOAL, X	};

	
	public Coordinate(int x, int y) {
		x = this.x;
		y = this.y;
	}
	
	public Coordinate(int x, int y, String theType) {
		this.x = x;
		this.y = y;
		//Enums not working
		//this.type = type.valueOf(theType);
	}
	
}