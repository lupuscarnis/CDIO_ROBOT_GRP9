package objects;

import java.util.ArrayList;
import java.util.List;

public class BallList {
	private static BallList ballList;
	private List<Ball> list ;
	
	
	private BallList() {
		list = new ArrayList();
	}
	
	public static BallList getInstance() {
		
		if(ballList == null) {
			return new BallList();
		}
		
		return ballList;
		
	}
	
public List getBallList() {
	return list;
}
public void add(Ball ball) {
	
	list.add(ball);
	
}
public Ball getIndex(int index) {
	return list.get(index);
	
}
public void clearList() {
	list.clear();
}
	
	

}
