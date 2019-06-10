package dto;

public interface I_DTO {

	boolean isBallpickup();

	void setBallpickup(boolean ballpickup);

	float getDistance();

	void setDistance(float distance);

	void setClawMove(float clawMove);

	float getClawMove();

	float getRotation();

	void setRotation(float rotation);

	String toString();

	I_DTO fillFromString(String data);

}