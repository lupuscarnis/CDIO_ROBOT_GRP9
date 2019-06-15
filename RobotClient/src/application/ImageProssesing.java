
package application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import objects.Robot;
import tools.I_Size_Scale;
import tools.Size_scale;



public class ImageProssesing implements I_ImageProssesing {


	// the FXML area for showing the mask
	Mat output;

	public ImageProssesing() {
		
		  
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see application.I_ImageProssesing#findBackAndFront(org.opencv.core.Mat)
	 */
	@Override
	public Mat findBackAndFront(Mat frame, List<Scalar> values) {
		
		
	
	
		

		Scalar minValuesb = new Scalar(160,70,170);
		Scalar maxValuesb = new Scalar(180,90,190);
		Scalar minValuesf = new Scalar(105,100,175);
		Scalar maxValuesf = new Scalar(125,129,210);
/*
		Point front = findColor(frame, values.get(0), values.get(1));
		
		Point back = findColor(frame,values.get(2), values.get(3));
*/

		Point front = findColor(frame, minValuesf, maxValuesf);
		
		Point back = findColor(frame,minValuesb, maxValuesb);

		Robot s = Robot.getInstance();
		s.setBackX(back.x);
		s.setBackY(back.y);
		s.setFrontX(front.x);
		s.setFrontY(front.y);

		
		Imgproc.line(frame, back, front, new Scalar(350, 255, 255));
	
		return frame;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see application.I_ImageProssesing#getOutput()
	 */
	@Override
	public Mat getOutput() {
		return output;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see application.I_ImageProssesing#setOutput(org.opencv.core.Mat)
	 */
	@Override
	public void setOutput(Mat output) {
		this.output = output;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see application.I_ImageProssesing#findCorners(org.opencv.core.Mat,
	 * org.opencv.core.Point, int)
	 */
	@Override
	public Mat findCorners(Mat frame, org.opencv.core.Point center, int threshold) {

		
		Mat srcGray = new Mat();
		Mat dst = new Mat();
		Mat dstNorm = new Mat();
		Mat dstNormScaled = new Mat();
		List<Point> cornors = new ArrayList<Point>();
		List<Double> values = new ArrayList<Double>();

		HashMap<Double, Point> lengths = new HashMap<Double, Point>();

		dst = Mat.zeros(srcGray.size(), CvType.CV_32F);
		int blockSize = 2;
		int iter = 0;
		int apertureSize = 3;
		double k = 0.04;
		Imgproc.cvtColor(frame, srcGray, Imgproc.COLOR_BGR2GRAY);
		Imgproc.cornerHarris(srcGray, dst, blockSize, apertureSize, k);
		Core.normalize(dst, dstNorm, 0, 255, Core.NORM_MINMAX);
		Core.convertScaleAbs(dstNorm, dstNormScaled);
		lengths.clear();
		
		float[] dstNormData = new float[(int) (dstNorm.total() * dstNorm.channels())];
		dstNorm.get(0, 0, dstNormData);
		
		for (int i = 0; i < dstNorm.rows(); i++) {
			if(iter > 200) {
				break;
			}
			for (int j = 0; j < dstNorm.cols(); j++) {
				if ((int) dstNormData[i * dstNorm.cols() + j] > threshold) {
					cornors.add(new Point(j, i));
					Imgproc.circle(dstNormScaled, new Point(j, i), 2, new Scalar(255, 255, 255));
					iter++;
					if(iter > 200) {
						break;
					}
				} 
			}
		}
		
		if (cornors.size() > 4) {

			lengths = convertPointsToVectorsDistancesFromCenter(cornors, center);

			for (Double b : lengths.keySet()) {
				values.add(b);

			}
			Collections.sort(values);
			cornors.clear();

			cornors.add(lengths.get(values.get(0)));
			cornors.add(lengths.get(values.get(1)));
			cornors.add(lengths.get(values.get(2)));
			cornors.add(lengths.get(values.get(3)));
			I_Size_Scale ss = new Size_scale();

			ss.pixelToCm(cornors);
			for (Point p : cornors) {
				Imgproc.line(dstNormScaled, p, center, new Scalar(350, 255, 255));	
			}
			
		}
		output = dstNormScaled;
		return dstNormScaled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see application.I_ImageProssesing#findColor(org.opencv.core.Mat,
	 * org.opencv.core.Scalar, org.opencv.core.Scalar)
	 */
	@Override
	public Point findColor(Mat frame, Scalar minValues, Scalar maxValues) {

		Mat hsvImage = new Mat();
		Mat output = new Mat();
		Mat filtered = new Mat();

		Imgproc.cvtColor(frame, hsvImage, Imgproc.COLOR_BGR2HSV);

		Core.inRange(hsvImage, minValues, maxValues, output);

		// init
		List<MatOfPoint> contours = new ArrayList<>();
		Mat hierarchy = new Mat();

		// find contours
		Imgproc.findContours(output, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

		List<org.opencv.core.Point> point = new ArrayList<>();

		for (MatOfPoint s : contours) {
			for (int i = 0; i < s.toList().size(); i++)
				point.add(s.toList().get(i));

		}

		double x = 0;
		double y = 0;
		int iter = 0;
		for (org.opencv.core.Point b : point) {
			iter++;
			x += b.x;
			y += b.y;

		}
		org.opencv.core.Point zonecenter = new org.opencv.core.Point((x / iter), (y / iter));

		return zonecenter;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * application.I_ImageProssesing#convertPointsToVectorsDistancesFromCenter(java.
	 * util.List, org.opencv.core.Point)
	 */
	@Override
	public HashMap<Double, Point> convertPointsToVectorsDistancesFromCenter(List<Point> pointList, Point center) {

		HashMap<Double, Point> lengths = new HashMap<Double, Point>();

		for (org.opencv.core.Point point : pointList) {

			lengths.put(Math.sqrt(Math.pow(point.x - center.x, 2) + Math.pow(point.y - center.y, 2)), point);

		}

		return lengths;
	}

}
