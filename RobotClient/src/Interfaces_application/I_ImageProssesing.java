package Interfaces_application;

import java.util.HashMap;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

public interface I_ImageProssesing {

	Mat findBackAndFront(Mat frame);

	Mat getOutput();

	void setOutput(Mat output);

	void findCorners(Mat frame, org.opencv.core.Point center, int threshold);

	Point findColor(Mat frame, Scalar minValues, Scalar maxValues);

	HashMap<Double, Point> convertPointsToVectorsDistancesFromCenter(List<Point> pointList, Point center);

}