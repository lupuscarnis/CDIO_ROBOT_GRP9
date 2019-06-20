
package application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import objects.FrameSize;
import objects.Robot;
import tools.I_Size_Scale;
import tools.OpenCVUtil;
import tools.Size_scale;

public class ImageProssesing implements I_ImageProssesing {

	// the FXML area for showing the mask
	Mat output;
	Mat output1;

	public Mat getOutput1() {
		return output1;
	}

	public void setOutput1(Mat output1) {
		this.output1 = output1;
	}

	public ImageProssesing() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see application.I_ImageProssesing#findBackAndFront(org.opencv.core.Mat)
	 */
	@Override
	public Mat findBackAndFront(Mat frame, List<Scalar> values, boolean robot) {

		/*Point front = findColor(frame, values.get(0), values.get(1), false);
		Point back = findColor(frame, values.get(2), values.get(3), true);*/

		Point front = findColorBG(frame, true);
		Point back = findColorBG(frame, false);
		
		if (robot) {

			Robot s = Robot.getInstance();
			if (Double.isNaN(back.x)) {
				s.setBackX(0);
			} else {
				s.setBackX(back.x);
			}

			if (Double.isNaN(back.y)) {
				s.setBackY(0);
			} else {
				s.setBackY(back.y);
			}

			if (Double.isNaN(front.x)) {
				s.setFrontX(0);
			} else {
				s.setFrontX(front.x);
			}

			if (Double.isNaN(front.y)) {
				s.setFrontY(0);
			} else {
				s.setFrontY(front.y);
			}

		}

		Imgproc.line(frame, back, front, new Scalar(0, 0, 0));

		Point centerpoint = new Point(((back.x + front.x) / 2), ((back.y + front.y) / 2));

		Imgproc.circle(frame, centerpoint, 5, new Scalar(0, 255, 0));
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
			if (iter > 200) {
				break;
			}
			for (int j = 0; j < dstNorm.cols(); j++) {
				if ((int) dstNormData[i * dstNorm.cols() + j] > threshold) {
					cornors.add(new Point(j, i));
					Imgproc.circle(dstNormScaled, new Point(j, i), 2, new Scalar(255, 255, 255));
					iter++;
					if (iter > 200) {
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

		return dstNormScaled;
	}

	public Point findColorBG(Mat frame, boolean front) {

		List<Mat> channels = new ArrayList<Mat>();
		Mat thresh = new Mat();
		Mat output = new Mat();
		Point centroid = new Point();

		Core.split(frame, channels);

		if (front) { // (blue)

			output = channels.get(0);

		} else {

			output = channels.get(1);

		}
		Imgproc.blur(output, output, new Size(7, 7));
		// dilate to remove some black gaps within balls
		Imgproc.dilate(output, output,
				Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5)));
		Imgproc.threshold(output, thresh, 200, 255,Imgproc.THRESH_BINARY);
		
		
		if (front) { // (blue)

			this.output = thresh;

		} else {

			this.output1 = thresh;

		}
		
		//output = Imgproc.adaptiveThreshold(output, output, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, I.THRESH_BINARY, 15, 40);

		List<MatOfPoint> contours = new ArrayList<>();
		Imgproc.findContours(thresh, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

		if (contours.size() > 0) {
			
			double maxArea = 0;
			int maxAreaIdx = 0;

			for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
				double contourArea = Imgproc.contourArea(contours.get(contourIdx));
				if (maxArea < contourArea) {
					maxArea = contourArea;
					maxAreaIdx = contourIdx;
				}
			}

			if (maxAreaIdx >= 0) {

				System.out.println("centroid");
				
				MatOfPoint largestContour = contours.get(maxAreaIdx);

				Moments moments = Imgproc.moments(largestContour);

				centroid.x = moments.get_m10() / moments.get_m00();
				centroid.y = moments.get_m01() / moments.get_m00();
				
				Imgproc.circle(frame, centroid, 5, new Scalar(0, 255, 0));

				System.out.println(centroid);
				
			}

		}

		return centroid;
	}

	/**
	 * Calculates the area of a rectangle based on the points from MatOfPoint2f
	 * rectangle
	 * 
	 * @param rectangle
	 * @return Size
	 */

	private Size getRectangleSize(MatOfPoint2f rectangle) {
		Point[] corners = rectangle.toArray();

		double top = getDistance(corners[0], corners[1]);
		double right = getDistance(corners[1], corners[2]);
		double bottom = getDistance(corners[2], corners[3]);
		double left = getDistance(corners[3], corners[0]);

		double averageWidth = (top + bottom) / 2f;
		double averageHeight = (right + left) / 2f;

		return new Size(new Point(averageWidth, averageHeight));
	}

	private double getDistance(Point p1, Point p2) {
		double dx = p2.x - p1.x;
		double dy = p2.y - p1.y;
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see application.I_ImageProssesing#findColor(org.opencv.core.Mat,
	 * org.opencv.core.Scalar, org.opencv.core.Scalar)
	 */
	@Override
	public Point findColor(Mat frame, Scalar minValues, Scalar maxValues, boolean c) {

		Mat hsvImage = new Mat();
		Mat blur = new Mat();
		Mat output = new Mat();
		Mat adapted = new Mat();
		Mat filtered = new Mat();
		Mat grayscale = new Mat();
		Mat hist = new Mat();
		Mat mask = new Mat();
		Mat morphOutput = new Mat();
		List<Mat> channels = new ArrayList<Mat>();

		Imgproc.GaussianBlur(frame, blur, new Size(25, 25), 0);
		Imgproc.cvtColor(blur, hsvImage, Imgproc.COLOR_BGR2HSV);

		Core.split(hsvImage, channels);

		/*
		 * Imgproc.adaptiveThreshold(channels.get(1), adapted, 255,
		 * Imgproc.ADAPTIVE_THRESH_MEAN_C,Imgproc.THRESH_BINARY_INV, 11, 2);
		 * 
		 * Imgproc.adaptiveThreshold(channels.get(2), filtered, 255,
		 * Imgproc.ADAPTIVE_THRESH_MEAN_C,Imgproc.THRESH_BINARY_INV, 11, 2);
		 */

		Imgproc.adaptiveThreshold(channels.get(2), channels.get(2), 255, Imgproc.ADAPTIVE_THRESH_MEAN_C,
				Imgproc.THRESH_BINARY, 9, 4);
		Core.merge(channels, hsvImage);
		Core.inRange(hsvImage, minValues, maxValues, output);

		// morphological operators
		// dilate with large element, erode with small ones
		Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(24, 24));
		Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12, 12));

		Imgproc.erode(output, morphOutput, erodeElement);
		Imgproc.erode(output, morphOutput, erodeElement);

		Imgproc.dilate(output, morphOutput, dilateElement);
		Imgproc.dilate(output, morphOutput, dilateElement);

		if (c) {
			this.output = morphOutput;

		} else {
			this.output1 = morphOutput;
		}

		// init
		List<MatOfPoint> contours = new ArrayList<>();
		Mat hierarchy = new Mat();
		// find contours
		Imgproc.findContours(morphOutput, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

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