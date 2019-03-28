package dto;

import java.util.Arrays;
import java.util.List;

public class DTO {
	private float distance;
	private float rotation;
	private boolean ballpickup;
	
	
	public boolean isBallpickup() {
		return ballpickup;
	}



	public void setBallpickup(boolean ballpickup) {
		this.ballpickup = ballpickup;
	}



	public DTO() {
		distance = 0;
		rotation = 0;
		ballpickup = false;
	}



	public float getDistance() {
		return distance;
	}



	public void setDistance(float distance) {
		this.distance = distance;
	}



	public float getRotation() {
		return rotation;
	}



	public void setRotation(float rotation) {
		this.rotation = rotation;
	}
	
	
	@Override
	public String toString()
	{
		String s;
		
		if(ballpickup == true) {
			s = ""+"Distance:"+"{"+distance+"},"+"Rotation:{"+rotation+"},BallPickUp:{true}"; 
		}else {
			s = ""+"Distance:"+"{"+distance+"},"+"Rotation:{"+rotation+"},BallPickUp:{false}";
		}
		
		
		return s;
	}
	public DTO fillFromString(String data) {
		DTO s = new DTO();
		List<String> information = Arrays.asList(data.split(","));
		
		
		
		return s;
		
	}
	

}
