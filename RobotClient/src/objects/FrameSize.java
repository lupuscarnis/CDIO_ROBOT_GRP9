package objects;

public class FrameSize {
	private static FrameSize size;
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

	private FrameSize(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
public static FrameSize getInstance() {
		
		if(size == null) {
		
		size = new FrameSize(0,0);
		
		return size;
		
		}
		
		return size;
		
	}

}