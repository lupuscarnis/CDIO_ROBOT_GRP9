package dto;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DTO implements I_DTO {
	private float distance;
	private float rotation;
	private float clawMove;
	private float backClawMove;
	private boolean ballpickup;
	
	
	/* (non-Javadoc)
	 * @see dto.I_DTO#isBallpickup()
	 */
	@Override
	public boolean isBallpickup() {
		return ballpickup;
	}



	/* (non-Javadoc)
	 * @see dto.I_DTO#setBallpickup(boolean)
	 */
	@Override
	public void setBallpickup(boolean ballpickup) {
		this.ballpickup = ballpickup;
	}



	public DTO() {
		distance = 0;
		rotation = 0;
		clawMove = 0;
		backClawMove =0; 
		ballpickup = false;
	}
	
	public float getBackClawMove() {
		return backClawMove;
	}
	
	public void setBackClawMove(float clawMove) {
		this.backClawMove =clawMove;
	}

	/* (non-Javadoc)
	 * @see dto.I_DTO#getDistance()
	 */
	@Override
	public float getDistance() {
		return distance;
	}



	/* (non-Javadoc)
	 * @see dto.I_DTO#setDistance(float)
	 */
	@Override
	public void setDistance(float distance) {
		this.distance = distance;
	}
	
	/* (non-Javadoc)
	 * @see dto.I_DTO#setClawMove(float)
	 */
	@Override
	public void setClawMove(float clawMove) {
		this.clawMove = clawMove;
	}
	
	/* (non-Javadoc)
	 * @see dto.I_DTO#getClawMove()
	 */
	@Override
	public float getClawMove() {
		return clawMove;
	}



	/* (non-Javadoc)
	 * @see dto.I_DTO#getRotation()
	 */
	@Override
	public float getRotation() {
		return rotation;
	}



	/* (non-Javadoc)
	 * @see dto.I_DTO#setRotation(float)
	 */
	@Override
	public void setRotation(float rotation) {
		this.rotation = rotation;
	}
	
	
	/* (non-Javadoc)
	 * @see dto.I_DTO#toString()
	 */
	@Override
	public String toString()
	{
		String s;
		
		if(ballpickup == true) {
			s = ""+"Distance:"+"{"+distance+"},"+"Rotation:{"+rotation+"},"+"backClawMove:{"+backClawMove+"},"+"clawMove:{"+clawMove+"},BallPickUp:{true}"; 
		}else {
			s = ""+"Distance:"+"{"+distance+"},"+"Rotation:{"+rotation+"},"+"backClawMove:{"+backClawMove+"},"+"clawMove:{"+clawMove+"},BallPickUp:{false}";
		}
		
		
		return s;
	}
	/* (non-Javadoc)
	 * @see dto.I_DTO#fillFromString(java.lang.String)
	 */
	@Override
	public I_DTO fillFromString(String data) {

		I_DTO s = new DTO();
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
