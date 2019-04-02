package behavior;

public class Node {
	int x=0,y=0,id=0,deg=361;
	boolean marked =false;

	public Node(int x, int y) {

		this.x = x;
		this.y = y;
	}
	
	public double measureDist(Node ball) {
		int X = x-ball.x;
		int Y = y-ball.y;
		return Math.sqrt(X*X+Y*Y);
		
	}

}
