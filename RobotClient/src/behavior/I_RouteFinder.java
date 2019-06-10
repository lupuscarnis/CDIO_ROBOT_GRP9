package behavior;

import behavior.RouteFinder.Node;
import objects.Ball;
import objects.Robot;

public interface I_RouteFinder {

	void getABall(Ball ball, Robot robot);

	Node[] findRoute(Node[] nodes, double angle);

}