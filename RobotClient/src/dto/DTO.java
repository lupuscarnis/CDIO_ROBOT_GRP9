package dto;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DTO {
	private float distance;
	private float rotation;
	private float clawMove;
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
		clawMove = 0;
		ballpickup = false;
	}



	public float getDistance() {
		return distance;
	}



	public void setDistance(float distance) {
		this.distance = distance;
	}
	
	public void setClawMove(float clawMove) {
		this.clawMove = clawMove;
	}
	
	public float getClawMove() {
		return clawMove;
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
			s = ""+"Distance:"+"{"+distance+"},"+"Rotation:{"+rotation+"},"+"clawMove:{"+clawMove+"},BallPickUp:{true}"; 
		}else {
			s = ""+"Distance:"+"{"+distance+"},"+"Rotation:{"+rotation+"},"+"clawMove:{"+clawMove+"},BallPickUp:{false}";
		}
		
		
		return s;
	}
	public DTO fillFromString(String data) {

		DTO s = new DTO();
		HashMap<String, Float> map = new HashMap<String, Float>();
		List<String> information = Arrays.asList(data.split(","));
		for (String subtask : information) {
			List<String> substring;
			substring = Arrays.asList(subtask.split(":"));
			substring.get(1).replace("{", "");
			substring.get(1).replace("}", "");
			if (substring.get(1).equals("true") || substring.get(1).equals("false")) {
				if (substring.get(1).equals("true")) {
					map.put(substring.get(0), (float) 1.0);

				} else {
					map.put(substring.get(0), (float) -1.0);
				}

			} else {

				map.put(substring.get(0), Float.parseFloat(substring.get(1)));

			}

		}
		s.setClawMove(map.get("clawMove"));
		s.setDistance(map.get("Distance"));
		s.setRotation(map.get("Rotation"));

		if (1 == map.get("BallPickUp")) {
			s.setBallpickup(true);
		} else {
			s.setBallpickup(false);
		}

		return s;


	}
	

}
