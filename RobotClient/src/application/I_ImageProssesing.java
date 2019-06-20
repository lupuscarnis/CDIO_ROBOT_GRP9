package application;

import java.util.HashMap;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

public interface I_ImageProssesing {



	Mat getOutput();

	void setOutput(Mat output);

	Mat findCorners(Mat frame, org.opencv.core.Point center, int threshold);


	

	HashMap<Double, Point> convertPointsToVectorsDistancesFromCenter(List<Point> pointList, Point center);

	Mat findBackAndFront(Mat frame, List<Scalar> values, boolean robot);
	
	Mat getOutput1();

	void setOutput1(Mat output);

	Point findColor(Mat frame, Scalar minValues, Scalar maxValues, boolean c);
	

}