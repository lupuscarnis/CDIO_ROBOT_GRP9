package application;

import java.util.ArrayList;

public class CSystem {
	
	int xLength, yLength;
	public ArrayList<Coordinate> balls;
	public ArrayList<Coordinate> border;
	public ArrayList<Coordinate> robot;
	public ArrayList<Coordinate> cross;
	
	
public void addPoint(Coordinate p) {
	
	balls.add(p);
	
}

public void clear() {
	
	balls.clear();
	
}

public boolean isEmpty() {
	if(balls.isEmpty()) {
		return false;
	}
	return true;
	
}


public CSystem(int xLength, int yLength) {
	
	this.xLength = xLength;
	this.yLength = yLength;
	 balls = new ArrayList<Coordinate>();;
	border= new ArrayList<Coordinate>();;
	robot= new ArrayList<Coordinate>();;
	cross= new ArrayList<Coordinate>();;
}
	
}