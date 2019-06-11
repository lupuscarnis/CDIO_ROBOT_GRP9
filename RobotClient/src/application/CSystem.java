package application;

import java.util.ArrayList;

public class CSystem {
	
	int xLength, yLength;
	ArrayList<Coordinate> points;
	
public void addPoint(Coordinate p) {
	
	points.add(p);
	
}

public CSystem(int xLength, int yLength) {
	
	this.xLength = xLength;
	this.yLength = yLength;
	this.points = new  ArrayList<Coordinate>();
}
	
}