package objects;

public class Border {
	private float x;
	private float y;
	private Border border1;
	private Border border2;
	public Border(float x, float y,Border border1, Border border2) {
		this.border1 = border1;
		this.border2 = border2;
		this.x = x;
		this.y = y;
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
	public Border getBorder1() {
		return border1;
	}
	public void setBorder1(Border border1) {
		this.border1 = border1;
	}
	public Border getBorder2() {
		return border2;
	}
	public void setBorder2(Border border2) {
		this.border2 = border2;
	}

}
