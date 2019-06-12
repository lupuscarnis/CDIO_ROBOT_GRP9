package objects;

public class Robot {
	private static Robot robot;
	private double pixelToCM;
	public double getPixelToCM() {
		return pixelToCM;
	}
	public void setPixelToCM(double pixelToCM) {
		this.pixelToCM = pixelToCM;
	}
	private double frontX;
	private double frontY;
	private double backX;
	private double backY;
	private Robot() {
	
	}
	public static Robot getInstance() {
	
	if(robot == null) {
		robot = new Robot();
		
		return robot;
	}else {
		return robot;
	}
	
	
}
	
	public double getFrontX() {
		return frontX;
	}
	public void setFrontX(double frontX) {
		this.frontX = frontX;
	}
	public double getFrontY() {
		return frontY;
	}
	public void setFrontY(double frontY) {
		this.frontY = frontY;
	}
	public double getBackX() {
		return backX;
	}
	public void setBackX(double backX) {
		this.backX = backX;
	}
	public double getBackY() {
		return backY;
	}
	public void setBackY(double backY) {
		this.backY = backY;
	}
	
	
	
	
}
