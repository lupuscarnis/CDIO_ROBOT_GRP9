package behavior;

import objects.Ball;
import objects.Robot;

public interface I_RouteFinder {

	void getABall(Ball ball, Robot robot);

	Node[] findRoute(Node[] nodes, double angle/*maybe its own poisition, if not first in array*/);

}