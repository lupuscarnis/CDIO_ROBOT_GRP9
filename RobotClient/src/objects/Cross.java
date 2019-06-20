package objects;

public class Cross {
	
	private static Cross size;
	double x,y;
	
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

	private Cross(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
public static Cross getInstance() {
		
		if(size == null) {
		
		size = new Cross(0,0);
		
		return size;
		
		}
		
		return size;
		
	}

}
