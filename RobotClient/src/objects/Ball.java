package objects;

public class Ball {
	
	private double x;
	private double y;
	private boolean collected;
	

	
	public Ball(double x2, double y2) {
		this.x = x2;
		this.y = y2;
		collected = false;
		
	}



	public double getX() {
		return x;
	}



	public void setX(float x) {
		this.x = x;
	}



	public double getY() {
		return y;
	}



	public void setY(float y) {
		this.y = y;
	}



	public boolean isCollected() {
		return collected;
	}



	public void setCollected(boolean collected) {
		this.collected = collected;
	}

}
