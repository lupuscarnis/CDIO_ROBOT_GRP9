package application;

import java.util.ArrayList;

public class CSystem {
	
	double xLength, yLength;
	
	public double getXLength() {
		return xLength;
	}

	public void setXLength(double xLength) {
		this.xLength = xLength;
	}

	public double getYLength() {
		return yLength;
	}

	public void setYLength(double yLength) {
		this.yLength = yLength;
	}


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


public CSystem(double xLength, double yLength) {
	
	this.xLength = xLength;
	this.yLength = yLength;
	 balls = new ArrayList<Coordinate>();;
	border= new ArrayList<Coordinate>();;
	robot= new ArrayList<Coordinate>();;
	cross= new ArrayList<Coordinate>();;
}
	
}