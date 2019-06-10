
package application;

import java.awt.image.ImageProducer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.swing.text.html.HTMLDocument.Iterator;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import objects.Robot;

public class ImageProssesing {
	// the FXML area for showing the mask
	Mat output;
	Scalar minValuesf = new Scalar(30, 55, 140);
	Scalar maxValuesf = new Scalar(50, 77, 180);

	Scalar minValuesb = new Scalar(10,180 ,210 );
	Scalar maxValuesb = new Scalar(30, 210, 250);

	public ImageProssesing() {
		// TODO Auto-generated constructor stub
	}

	public Mat findBackAndFront(Mat frame) {

		Point back = findColor(frame, minValuesb, maxValuesb);
		Point front = findColor(frame, minValuesf, maxValuesf);

		
		Robot s = Robot.getInstance();
		s.setBackX(back.x);
		s.setBackY(back.y);
		s.setFrontX(front.x);
		s.setFrontY(front.y);
		
		findCorners(frame, front, 160);
		Imgproc.line(frame, back, front, new Scalar(350, 255, 255));
		/*
		System.out.println("Distance: "+ Math.sqrt(Math.pow(s.getBackX() - s.getFrontX(), 2) + Math.pow(s.getBackY() - s.getFrontY(), 2)));
		System.out.println("Distance in cm: "+ (Math.sqrt(Math.pow(s.getBackX() - s.getFrontX(), 2) + Math.pow(s.getBackY() - s.getFrontY(), 2)))/s.getPixelToCM());
		*/
		
		return frame;

	}

	public Mat getOutput() {
		return output;
	}

	public void setOutput(Mat output) {
		this.output = output;
	}

	public void findCorners(Mat frame, org.opencv.core.Point center, int threshold) {

		Mat srcGray = new Mat();
		Mat dst = new Mat();
		Mat dstNorm = new Mat();
		Mat dstNormScaled = new Mat();
		List<Point> cornors = new ArrayList<Point>();
		List<Double> values = new ArrayList<Double>();

		HashMap<Double, Point> lengths = new HashMap<Double, Point>();

		dst = Mat.zeros(srcGray.size(), CvType.CV_32F);
		int blockSize = 2;
		int iter =0;
		int apertureSize = 3;
		double k = 0.04;
		Imgproc.cvtColor(frame, srcGray, Imgproc.COLOR_BGR2GRAY);
		Imgproc.cornerHarris(srcGray, dst, blockSize, apertureSize, k);
		Core.normalize(dst, dstNorm, 0, 255, Core.NORM_MINMAX);
		Core.convertScaleAbs(dstNorm, dstNormScaled);

		float[] dstNormData = new float[(int) (dstNorm.total() * dstNorm.channels())];
		dstNorm.get(0, 0, dstNormData);
		for (int i = 0; i < dstNorm.rows(); i++) {
			for (int j = 0; j < dstNorm.cols(); j++) {
				if ((int) dstNormData[i * dstNorm.cols() + j] > threshold) {
					cornors.add(new Point(j, i));
					Imgproc.circle(dstNormScaled, new Point(j, i), 3, new Scalar(255, 255, 255));
					iter++;
				}
			}
		}
		System.out.println("Iteration: " + iter+ "cornors: "+cornors.size());
		
		
		lengths = convertPointsToVectorsDistancesFromCenter(cornors, center);
		
		for(Double b : lengths.keySet()) {
			values.add(b);
		
	}
		 Collections.sort(values);
		cornors.clear();
	
		
		System.out.println("Short1: "+ values.get(0));
		System.out.println("Short2: "+ values.get(1));
		System.out.println("Short3: "+ values.get(2));
		System.out.println("Short4: "+ values.get(3));
		
		cornors.add(lengths.get(values.get(0)));
		cornors.add(lengths.get(values.get(1)));
		cornors.add(lengths.get(values.get(2)));
		cornors.add(lengths.get(values.get(3)));
		Size_scale ss = new Size_scale();
		ss.pixelToCm(cornors);	
		for(Point p: cornors)
			Imgproc.line(dstNormScaled, p, center, new Scalar(350, 255, 255));
		
		output = dstNormScaled;
		}


	public Point findColor(Mat frame, Scalar minValues, Scalar maxValues) {
		Mat hsvImage = new Mat();
		Mat output = new Mat();
		Mat filtered = new Mat();

		Imgproc.GaussianBlur(frame, filtered, new Size(45, 45), 0);
		Imgproc.cvtColor(filtered, hsvImage, Imgproc.COLOR_BGR2HSV);

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
		org.opencv.core.Point zonecenter = new org.opencv.core.Point(x / iter, y / iter);

		return zonecenter;

	}

	public HashMap<Double, Point> convertPointsToVectorsDistancesFromCenter(List<Point> pointList, Point center) {

		HashMap<Double, Point> lengths = new HashMap<Double, Point>();

		for (org.opencv.core.Point point : pointList) {

			lengths.put(Math.sqrt(Math.pow(point.x - center.x, 2) + Math.pow(point.y - center.y, 2)), point);

		}

		return lengths;
	}
	

}
