
package application;

import java.awt.image.ImageProducer;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
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


	public ImageProssesing() {
		// TODO Auto-generated constructor stub
	}
	public Mat findBackAndFront(Mat frame) {
		Mat hsvImage = new Mat();
		Mat output1 = new Mat();
		Mat output2 = new Mat();
		Mat morphOutput = new Mat();
		Mat filtered = new Mat();
		List<Mat> matlist = new ArrayList<Mat>(3);
		//Imgproc.blur(frame, filtered, new Size(7, 7));
		Imgproc.GaussianBlur(frame, filtered, new Size(45,45), 0);
		Imgproc.cvtColor(filtered, hsvImage, Imgproc.COLOR_BGR2HSV);
		Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(24, 24));
		Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12, 12));
		//for square 1 
		Scalar minValues = new Scalar(35, 70, 180);
		Scalar maxValues = new Scalar(50, 90, 200);
	    
		Core.inRange(hsvImage, minValues, maxValues, output1);
		
		//for square 2 
		minValues.set(new double[] { 24, 81, 220 });
		maxValues.set(new double[] { 38, 101, 240 });
		
		Core.inRange(hsvImage, minValues, maxValues, output2);
		Imgproc.erode(output2, morphOutput, erodeElement);
		Imgproc.dilate(morphOutput, morphOutput, dilateElement);
		output2 = morphOutput; 
		
		// init
		List<MatOfPoint> contours1 = new ArrayList<>();
		List<MatOfPoint> contours2 = new ArrayList<>();
		Mat hierarchy = new Mat();

		// find contours
		Imgproc.findContours(output1, contours1, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
		Imgproc.findContours(output2, contours2, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
		
		List<org.opencv.core.Point> front = new ArrayList<>();
		List<org.opencv.core.Point> back = new ArrayList<>();
	
		
		
		
		for (MatOfPoint s : contours1) {
			for (int i = 0; i < s.toList().size(); i++)
				front.add(s.toList().get(i));

		}
		for (MatOfPoint s : contours2) {
			for (int i = 0; i < s.toList().size(); i++)
				back.add(s.toList().get(i));

		}


		
		
		double x = 0;
		double y = 0;
		int iter = 0;
		for (org.opencv.core.Point b : front) {
			iter++;
			x += b.x;
			y += b.y;

		
		}
		org.opencv.core.Point frontCenter = new org.opencv.core.Point(x / iter, y / iter);
		System.out.println("Itrations: "+ iter );
		iter = 0;
		x = 0;
		y = 0;
		for (org.opencv.core.Point b : back) {
			iter++;
			x += b.x;
			y += b.y;
		}
		org.opencv.core.Point backCenter = new org.opencv.core.Point(x / iter, y / iter);
		
		for(org.opencv.core.Point b : front) {
		Imgproc.line(frame, b,front.get(0), new Scalar(350, 255, 255));
		
		}
		  
		  //System.out.println("Front" + frontCenter.x+","+frontCenter.y +" "+ "Back:" +
		  //frontCenter.x+","+frontCenter.y);
		 
		//Imgproc.line(frame, backCenter, frontCenter, new Scalar(350, 255, 255));
		Robot s = Robot.getInstance();
		s.setBackX(backCenter.x);
		s.setBackY(backCenter.y);
		s.setFrontX(frontCenter.x);
		s.setFrontY(frontCenter.y);

    
		
		return frame;
	}
	public Mat findCorners( Mat frame) {
		Mat hsvImage = new Mat();
		Mat output1 = new Mat();
		Mat output2 = new Mat();
		Mat morphOutput = new Mat();
		Mat filtered = new Mat();
	/*
		Imgproc.GaussianBlur(frame, filtered, new Size(7, 7), 0);
		Imgproc.cvtColor(filtered, hsvImage, Imgproc.COLOR_BGR2HSV);
		Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(24, 24));
		Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12, 12));
		//for square 1 
		Scalar minValues = new Scalar(14, 195, 205);
		Scalar maxValues = new Scalar(20, 215, 225);
	
		Core.inRange(hsvImage, minValues, maxValues, output1);
		*/
		
		 int threshold = 200;
	
		Mat srcGray = new Mat();
	    Mat dst = new Mat();
	    Mat dstNorm = new Mat();
	    Mat dstNormScaled = new Mat();
	    
	    int blockSize = 2;
	    int apertureSize = 3;
	    double k = 0.04;
		Imgproc.cvtColor(frame, srcGray,Imgproc.COLOR_BGR2GRAY);
       Imgproc.cornerHarris(srcGray, dst, blockSize, apertureSize, k);
       Core.normalize(dst, dstNorm, 0, 255, Core.NORM_MINMAX);
       Core.convertScaleAbs(dstNorm, dstNormScaled);
       float[] dstNormData = new float[(int) (dstNorm.total() * dstNorm.channels())];
       dstNorm.get(0, 0, dstNormData);
       for (int i = 0; i < dstNorm.rows(); i++) {
           for (int j = 0; j < dstNorm.cols(); j++) {
               if ((int) dstNormData[i * dstNorm.cols() + j] > threshold) {
                   Imgproc.circle(dstNormScaled, new Point(j, i), 5, new Scalar(0), 2, 8, 0);
               }
           }
       }
    		

	
	return dstNormScaled;
	
	}


}
