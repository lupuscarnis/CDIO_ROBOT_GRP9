package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.osgi.OpenCVInterface;
import org.opencv.videoio.VideoCapture;

import application.Utils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import nu.pattern.OpenCV;
import objects.Ball;
import objects.BallList;
import objects.Robot;

/**
 * https://github.com/opencv-java
 */

public class FXController {
	// FXML camera button
	@FXML
	private Button cameraButton;
	// the FXML area for showing the current frame
	@FXML
	private ImageView videoFrame;
	// the FXML area for showing the mask
	@FXML
	private ImageView maskImage;
	// the FXML area for showing the output of the morphological operations
	@FXML
	private ImageView morphImage;
	// FXML slider for setting HSV ranges
	@FXML
	private Slider hueStart;
	@FXML
	private Slider hueStop;
	@FXML
	private Slider saturationStart;
	@FXML
	private Slider saturationStop;
	@FXML
	private Slider valueStart;
	@FXML
	private Slider valueStop;
	// FXML label to show the current values set with the sliders
	@FXML
	private Label hsvCurrentValues;

	// For Hough
	@FXML
	private Slider H_minDist;
	@FXML
	private Slider H_uThresh;
	@FXML
	private Slider H_cTresh;
	@FXML
	private Slider H_minRad;
	@FXML
	private Slider H_maxRad;

	// a timer for acquiring the video stream
	private ScheduledExecutorService timer;
	// the OpenCV object that performs the video capture
	private VideoCapture capture = new VideoCapture();
	// a flag to change the button behavior
	private boolean cameraActive;

	// property for object binding
	private ObjectProperty<String> hsvValuesProp;

	/******************************************
	 * * MAIN CONTROLS AND SETUP * *
	 ******************************************/

    // Use HSV or Hough for image analysis?
	private boolean useHSVImgDetection = false;

    // Sets the frames per second (33 = 33 frames per second)
	private int captureRate = 250;

    // Sets the id of the systems webcam
	private int webcamID = 0;

	// Switch between debug/production mode
	private boolean isDebug = true;
	
	// Debug image file
	private String debugImg = "Debugging/pic01.jpg";
	
	/**
	 * The action triggered by pushing the button on the GUI
	 */
	@FXML
	private void startCamera() {
		// bind a text property with the string containing the current range of
		// HSV values for object detection
		hsvValuesProp = new SimpleObjectProperty<>();
		// this.hsvCurrentValues.textProperty().bind(hsvValuesProp);

		// set a fixed width for all the image to show and preserve image ratio
		this.imageViewProperties(this.videoFrame, 400);
		this.imageViewProperties(this.maskImage, 200);
		this.imageViewProperties(this.morphImage, 200);

		
		if (!this.cameraActive) {
			// start the video capture
			this.capture.open(webcamID);

			// is the video stream available?
			if (this.capture.isOpened()) {
				this.cameraActive = true;

				// grabs frames from webcam
				Runnable frameGrabber = new Runnable() {

					@Override
					public void run() {
						
						Mat frame = new Mat();
						
						if (useHSVImgDetection) {
							  frame = grabFrame();						
							} else {
							  frame = grabFrameHough();
							}
						
						    // Find robot vector
							frame = findBackAndFront(frame);

							// Find the rectangle of the playing field
							frame = findAndDrawRect(frame);
							
						// convert and show the frame
						Image imageToShow = Utils.mat2Image(frame);
						updateImageView(videoFrame, imageToShow);
					}
				};

				this.timer = Executors.newSingleThreadScheduledExecutor();
				this.timer.scheduleAtFixedRate(frameGrabber, 0, captureRate, TimeUnit.MILLISECONDS);

				// update the button content
				this.cameraButton.setText("Stop Camera");
				
			} else {
				// log the error
				System.err.println("Failed to open the camera connection...");
			}
		} else {
			// the camera is not active at this point
			this.cameraActive = false;
			// update again the button content
			this.cameraButton.setText("Start Camera");

			// stop the timer
			this.stopAcquisition();
		}
	}

	/**
	 * Get a frame from the opened video stream (if any)
	 * 
	 * @return the {@link Image} to show
	 */
	private Mat grabFrame() {
		Mat frame = new Mat();

		// check if the capture is open
		if (this.capture.isOpened()) {
			try {
			
				if (isDebug == true) {
					
					// read from from test image
					frame = Imgcodecs.imread(debugImg);

				} else {
					
					// read the current frame
					this.capture.read(frame);
					
				}
				
				// if the frame is not empty, process it
				if (!frame.empty()) {
					// init
					Mat blurredImage = new Mat();
					Mat hsvImage = new Mat();
					Mat mask = new Mat();
					Mat morphOutput = new Mat();

					// remove some noise
					// Imgproc.blur(frame, blurredImage, new Size(7, 7));

					// Applying GaussianBlur on the Image (Gives a much cleaner/less noisy result)
					Imgproc.GaussianBlur(frame, blurredImage, new Size(45, 45), 0);

					/*
					 * Experimental grayscale -->
					 * http://answers.opencv.org/question/34970/detection-of-table-tennis-balls-and-
					 * color-correction/ When using grayscale only the hue min/max slider have an
					 * effect on the detection.
					 */
					// Imgproc.cvtColor(blurredImage, grayImage, Imgproc.COLOR_BGR2GRAY);
					
					// convert the frame to HSV
					Imgproc.cvtColor(blurredImage, hsvImage, Imgproc.COLOR_BGR2HSV);

					// get thresholding values from the UI
					// remember: H ranges 0-180, S and V range 0-255
					Scalar minValues = new Scalar(this.hueStart.getValue(), this.saturationStart.getValue(),
							this.valueStart.getValue());
					Scalar maxValues = new Scalar(this.hueStop.getValue(), this.saturationStop.getValue(),
							this.valueStop.getValue());

					// show the current selected HSV range
					String valuesToPrint = "Hue range: " + minValues.val[0] + "-" + maxValues.val[0]
							+ "\tSaturation range: " + minValues.val[1] + "-" + maxValues.val[1] + "\tValue range: "
							+ minValues.val[2] + "-" + maxValues.val[2];

					Utils.onFXThread(this.hsvValuesProp, valuesToPrint);

					// threshold HSV image to select tennis balls
					Core.inRange(hsvImage, minValues, maxValues, mask);
					// show the partial output
					this.updateImageView(this.maskImage, Utils.mat2Image(mask));

					// morphological operators
					// dilate with large element, erode with small ones
					Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(24, 24));
					Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12, 12));

					Imgproc.erode(mask, morphOutput, erodeElement);
					Imgproc.erode(morphOutput, morphOutput, erodeElement);

					Imgproc.dilate(morphOutput, morphOutput, dilateElement);
					Imgproc.dilate(morphOutput, morphOutput, dilateElement);

					// show the partial output
					this.updateImageView(this.morphImage, Utils.mat2Image(morphOutput));
					// frame = findBackAndFront(frame);
					// find the tennis ball(s) contours and show them
					frame = this.findAndDrawBalls(morphOutput, frame);

				}

			} catch (Exception e) {
				// log the (full) error
				System.err.print("Exception during the image elaboration...");
				e.printStackTrace();
			}
		}

		return frame;
	}

	
	/**
	 * HOUGH IMAGE ANALYSIS
	 * 
	 * Get a frame from the opened video stream (if any)
	 * 
	 * @return the {@link Image} to show
	 */
	private Mat grabFrameHough()
	{
		
		Mat frame = new Mat();
	      
		// check if the capture is open

			try
			{
				
				if (isDebug == true) {
					
					// read from from test image
					frame = Imgcodecs.imread(debugImg);

				} else {
					
					// read the current frame
					this.capture.read(frame);
					
				}
				
				// if the frame is not empty, process it
				if (!frame.empty())
				{
					// init

					Mat grayImage = new Mat();

					
					Imgproc.cvtColor(frame, grayImage, Imgproc.COLOR_BGR2GRAY);
					
					Imgproc.medianBlur(grayImage, grayImage, 5);
					
					Mat circles = new Mat();
					
					int min_dist = new Integer((int) this.H_minDist.getValue());
					double uThresh = new Double(this.H_uThresh.getValue());
					double cTresh = new Double(this.H_cTresh.getValue());
					int minRad = new Integer((int) this.H_minRad.getValue());
					int maxRad = new Integer((int) this.H_maxRad.getValue());

					// show the current selected HSV range
					String valuesToPrint = "Min dist: " + min_dist + ", Upper Threshold: " + uThresh
							+ ", Center Threshold: " + cTresh + ", Min Radius: " + minRad + ", Max Radius: "
							+ maxRad;
					
					Utils.onFXThread(this.hsvValuesProp, valuesToPrint);
					
			        /*grayImage: Input image (grayscale).
			        circles: A vector that stores sets of 3 values: xc,yc,r for each detected circle.
			        HOUGH_GRADIENT: Define the detection method. Currently this is the only one available in OpenCV.
			        dp = 1: The inverse ratio of resolution.
			        min_dist = gray.rows/16: Minimum distance between detected centers.
			        param_1 = 200: Upper threshold for the internal Canny edge detector.
			        param_2 = 100*: Threshold for center detection.
			        min_radius = 0: Minimum radius to be detected. If unknown, put zero as default.
			        max_radius = 0: Maximum radius to be detected. If unknown, put zero as default.*/
			        Imgproc.HoughCircles(grayImage, circles, Imgproc.HOUGH_GRADIENT, 1.0,(double)grayImage.rows()/min_dist,uThresh, cTresh, minRad, maxRad);

			        
			        
			        
			        for (int x = 0; x < circles.cols(); x++) {
			        	
			            List<Point> p = new ArrayList<>();
			            
			            double[] c = circles.get(0, x);
			            Point center = new Point(Math.round(c[0]), Math.round(c[1]));
			            p.add(center);
			            // circle center
			    		BallList s = BallList.getInstance();
			    		s.clearList();
			    		for (Point B : p) {
			    			s.add(new Ball(B.x, B.y));
			    		}

			            Imgproc.circle(frame, center, 1, new Scalar(0,100,100), 3, 8, 0 );
			            // circle outline
			            int radius = (int) Math.round(c[2]);
			            Imgproc.circle(frame, center, radius, new Scalar(255,0,255), 3, 8, 0 );
			            Imgproc.circle(frame, center, 1, new Scalar(0,255,255), 3, 8, 0 );
			            
			            // Print center coordinates 
			            
			            for (int i = 0; i < p.size(); i++) {
			            	//System.out.println("Point (X,Y): "+p.get(i));

			            }
			        }

				}
				
			}
			catch (Exception e)
			{
				// log the (full) error
				System.err.print("Exception during the image elaboration...");
				e.printStackTrace();
			}

		
		return frame;
	}
	
	
	/**
	 * Given a binary image containing one or more closed surfaces, use it as a mask
	 * to find and highlight the objects contours
	 * 
	 * @param maskedImage the binary image to be used as a mask
	 * @param frame       the original {@link Mat} image to be used for drawing the
	 *                    objects contours
	 * @return the {@link Mat} image with the objects contours framed
	 */
	private Mat findAndDrawBalls(Mat maskedImage, Mat frame) {
		Random rand = new Random(12345);
		// init
		List<MatOfPoint> contours = new ArrayList<>();
		Mat hierarchy = new Mat();
		Mat output = new Mat();

		// find contours
		Imgproc.findContours(maskedImage, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

		// Moments can be used to find the center of an image/polygon
		List<Moments> m = new ArrayList<>(contours.size());

		for (int i = 0; i < contours.size(); i++) {
			m.add(Imgproc.moments(contours.get(i)));
		}

		// Stores the points of each centroid
		List<Point> p = new ArrayList<>(contours.size());
		BallList s = BallList.getInstance();
		s.clearList();
		for (Point B : p) {
			s.add(new Ball(B.x, B.y));
		}

		for (int i = 0; i < contours.size(); i++) {
			// add 1e-5 to avoid division by zero
			p.add(new Point(m.get(i).m10 / (m.get(i).m00 + 1e-5), m.get(i).m01 / (m.get(i).m00 + 1e-5)));
		}

		Mat drawing = Mat.zeros(output.size(), CvType.CV_8UC3);

		// Draws the centroid and contour around the object
		for (int i = 0; i < contours.size(); i++) {
			Scalar color = new Scalar(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
			// Imgproc.drawContours(drawing, contours, i, color, 2);
			Imgproc.drawContours(frame, contours, i, new Scalar(250, 0, 0), 2);
			Imgproc.circle(frame, p.get(i), 4, color, -1);
		}

		
		// System.out.println("\t Info: Area and Contour Length \n");
		for (int i = 0; i < contours.size(); i++) {
			System.out.println("Point (X,Y): " + p.get(i));
			/*
			 * System.out.
			 * format(" * Contour[%d] - Area (M_00) = %.2f - Area OpenCV: %.2f - Length: %.2f\n"
			 * , i, m.get(i).m00, Imgproc.contourArea(contours.get(i)),
			 * Imgproc.arcLength(new MatOfPoint2f(contours.get(i).toArray()), true));
			 */
		}

		// if any contour exist...
		if (hierarchy.size().height > 0 && hierarchy.size().width > 0) {
			// for each contour, display it in blue
			for (int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0]) {
				// https://www.programcreek.com/java-api-examples/?class=org.opencv.imgproc.Imgproc&method=drawContours
				Imgproc.drawContours(frame, contours, idx, new Scalar(0, 250, 0), 2);
			}
		}

		return frame;
	}

	
	private Mat findAndDrawRect(Mat frame) {
		
		
		
		return frame;
		
	}
	
	/**
	 * Set typical {@link ImageView} properties: a fixed width and the information
	 * to preserve the original image ration
	 * 
	 * @param image     the {@link ImageView} to use
	 * @param dimension the width of the image to set
	 */
	private void imageViewProperties(ImageView image, int dimension) {
		// set a fixed width for the given ImageView
		image.setFitWidth(dimension);
		// preserve the image ratio
		image.setPreserveRatio(true);
	}

	/**
	 * Stop the acquisition from the camera and release all the resources
	 */
	private void stopAcquisition() {
		if (this.timer != null && !this.timer.isShutdown()) {
			try {
				// stop the timer
				this.timer.shutdown();
				this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// log any exception
				System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
			}
		}

		if (this.capture.isOpened()) {
			// release the camera
			this.capture.release();
		}
	}

	/**
	 * Update the {@link ImageView} in the JavaFX main thread
	 * 
	 * @param view  the {@link ImageView} to update
	 * @param image the {@link Image} to show
	 */
	private void updateImageView(ImageView view, Image image) {
		Utils.onFXThread(view.imageProperty(), image);
	}

	/**
	 * On application close, stop the acquisition from the camera
	 */
	protected void setClosed() {
		this.stopAcquisition();
	}

	/*
	 * Finds the Front and back and draws a arrow
	 * 
	 * 
	 */
	private Mat findBackAndFront(Mat frame) {

		Mat hsvImage = new Mat();
		Mat output1 = new Mat();
		Mat output2 = new Mat();

		Mat filtered = new Mat();
		Imgproc.blur(frame, filtered, new Size(7, 7));
		Imgproc.cvtColor(filtered, hsvImage, Imgproc.COLOR_BGR2HSV);
		Scalar minValues = new Scalar(30, 110, 230);
		Scalar maxValues = new Scalar(40, 120, 255);
		Core.inRange(hsvImage, minValues, maxValues, output1);

		minValues.set(new double[] { 20, 185, 245 });
		maxValues.set(new double[] { 30, 195, 255 });
		Core.inRange(hsvImage, minValues, maxValues, output2);
		
		// init
		List<MatOfPoint> contours1 = new ArrayList<>();
		List<MatOfPoint> contours2 = new ArrayList<>();
		Mat hierarchy = new Mat();

		// find contours
		Imgproc.findContours(output1, contours1, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
		Imgproc.findContours(output2, contours2, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
		//System.out.println(""+contours1.size()+":"+ contours2.size());
		// if (contours1.size() > 0 && contours2.size() > 0) {
		List<org.opencv.core.Point> front = new ArrayList<>();
		List<org.opencv.core.Point> back = new ArrayList<>();
		System.out.println("" + contours1.size() + ":" + contours2.size());
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

		iter = 0;
		x = 0;
		y = 0;
		for (org.opencv.core.Point b : back) {
			iter++;
			x += b.x;
			y += b.y;
		}
		org.opencv.core.Point backCenter = new org.opencv.core.Point(x / iter, y / iter);
		
		  System.out.println("Front" + frontCenter.x+","+frontCenter.y +" "+ "Back:" +
		  frontCenter.x+","+frontCenter.y);
		 
		Imgproc.line(frame, backCenter, frontCenter, new Scalar(350, 255, 255));
		Robot s = Robot.getInstance();
		s.setBackX(backCenter.x);
		s.setBackY(backCenter.y);
		s.setFrontX(frontCenter.x);
		s.setFrontY(frontCenter.y);
		return frame;
	}

}