package objects;

public class Ball {
	
	private float x;
	private float y;
	private boolean collected;
	

	
	public Ball(float x, float y) {
		this.x = x;
		this.y = y;
		collected = false;
		
	}



	public float getX() {
		return x;
	}



	public void setX(float x) {
		this.x = x;
	}



	public float getY() {
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
