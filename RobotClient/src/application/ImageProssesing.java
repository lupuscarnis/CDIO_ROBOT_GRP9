
package application;

import java.io.IOException;
import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
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

		Point front = findColor(frame, values.get(0), values.get(1), false);
		Point back = findColor(frame, values.get(2), values.get(3) , true);
		

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see application.I_ImageProssesing#findColor(org.opencv.core.Mat,
	 * org.opencv.core.Scalar, org.opencv.core.Scalar)
	 */
	@Override
	public Point findColor(Mat frame, Scalar minValues, Scalar maxValues, boolean c ) {

		Mat hsvImage = new Mat();
		Mat blur = new Mat();
		Mat output = new Mat();
		Mat adapted = new Mat();
		Mat filtered = new Mat();
		Mat grayscale = new Mat();
		Mat hist  = new Mat();
		Mat mask = new Mat();
		Mat morphOutput = new Mat();
		List<Mat> channels = new ArrayList<Mat>();
	      
		MatOfFloat ranges = new MatOfFloat(0f, 256f);
		 MatOfInt histSize = new MatOfInt(25);
	
		 
		Imgproc.GaussianBlur(frame, blur, new Size(25, 25), 0);
		Imgproc.cvtColor(blur, hsvImage, Imgproc.COLOR_BGR2HSV);		
		hsvImage.copyTo(mask);
		Core.split(hsvImage, channels);	
		channels.get(2).copyTo(grayscale);;
		Imgproc.equalizeHist(grayscale,channels.get(2));
		/*
		Imgproc.threshold(channels.get(1),channels.get(1),51, 255, Imgproc.THRESH_TOZERO);
		Imgproc.threshold(channels.get(2),channels.get(2),51, 255, Imgproc.THRESH_TOZERO);
		*/
		
		Core.merge(channels,filtered);			
		Core.inRange(filtered, minValues, maxValues, output);
		 // morphological operators	
		// dilate with large element, erode with small ones
		 Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12, 12));
		 Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(6, 6));

		 Imgproc.erode(output, morphOutput, erodeElement);
		 Imgproc.erode(output, morphOutput, erodeElement);

		 Imgproc.dilate(output, morphOutput, dilateElement);
		 Imgproc.dilate(output, morphOutput, dilateElement);
		 if(c) {
		 this.output = morphOutput;
		 }else{
			 this.output1 =  morphOutput;			 
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
