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
	private double frontX = 0;
	private double frontY = 0;
	private double backX = 0;
	private double backY = 0;
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
