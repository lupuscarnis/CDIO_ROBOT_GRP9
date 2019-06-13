package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.osgi.OpenCVInterface;
import org.opencv.utils.Converters;
import org.opencv.videoio.VideoCapture;

import behavior.RobotController;
import dao.DAO;
import dao.I_DAO;
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
import tools.OpenCVUtil;
import tools.Utils;

/**
 * 
 * 
 * https://github.com/opencv-java
 */

public class FXController {
	// FXML camera button
	@FXML
	private Button cameraButton;
	// FXML robot button
	@FXML
	private Button robotButton;
	// the FXML area for showing the current frame
	@FXML
	private ImageView videoFrame;
	// the FXML area for showing the mask
	@FXML
	private ImageView maskImage;
	// the FXML area for showing the output of the morphological operations
	@FXML
	private ImageView morphImage;

	@FXML
	private ImageView cornerImage;

	@FXML
	private ImageView robotImage;

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
	@FXML
	private Slider H_CORNER;
	@FXML
	private Slider S_CORNER;
	@FXML
	private Slider V_CORNER;
	@FXML
	private Slider TRESHOLD;

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

	// For PlayingField
	@FXML
	private Slider C_Low;
	// For PlayingField
	@FXML
	private Slider C_Max;

	@FXML
	private Slider C_Kernel;

	boolean robotest = false;

	// a timer for acquiring the video stream
	private ScheduledExecutorService timer;
	// the OpenCV object that performs the video capture
	private VideoCapture capture = new VideoCapture();
	// a flag to change the button behavior
	private boolean cameraActive;
	// a flag to change the button behavior
	private boolean robotActive;
	// property for object binding
	private ObjectProperty<String> hsvValuesProp;

	// Homemade Image prosessing
	I_ImageProssesing ip = new ImageProssesing();

	RobotController rc = new RobotController();

	/******************************************
	 * * MAIN CONTROLS AND SETUP * *
	 ******************************************/
	// I_DAO dao = new DAO();
	// Use HSV or Hough for image analysis?
	boolean UseHSVImgDetection = false;

	// Sets the frames per second (33 = 33 frames per second)
	private int captureRate = 500;

	// Sets the id of the systems webcam
	private int webcamID = 1;

	// Switch between debug/production mode
	private boolean isDebug = false;

	// Debug image file
	private String debugImg1 = "Debugging/pic01.jpg";
	private String debugImg2 = "Debugging/pic01.jpg";
	private String debugImg3 = "Debugging/pic01.jpg";

	// Empty image file
	private String noImg = "Debugging/Default.jpg";

	/**
	 * The action triggered by pushing the button on the GUI
	 */
	@FXML
	private void startCamera() {
		// bind a text property with the string containing the current range of
		// HSV values for object detection
		hsvValuesProp = new SimpleObjectProperty<>();
		this.hsvCurrentValues.textProperty().bind(hsvValuesProp);

		// set a fixed width for all the image to show and preserve image ratio
		this.imageViewProperties(this.videoFrame, 400);
		this.imageViewProperties(this.maskImage, 200);
		this.imageViewProperties(this.cornerImage, 200);
		this.imageViewProperties(this.morphImage, 200);
		this.imageViewProperties(this.robotImage, 200);

		if (!this.cameraActive) {
			// start the video capture
			this.capture.open(webcamID);

			// is the video stream available?
			if (this.capture.isOpened() || isDebug) {

				this.cameraActive = true;

				// grab a frame every 33 ms (30 frames/sec)
				Runnable frameGrabber = new Runnable() {

					@Override
					public void run() {
						Mat cleanFrame = new Mat();
						Mat frame = new Mat();

						frame = grabFrame();

						// Find the rectangle of the playing field and crop the image
						//frame = findAndDrawRect(frame);

						if (UseHSVImgDetection) {
							frame = grabFrameHSV(frame);
						} else {
							frame = grabFrameHough(frame);
						}

						if (!isDebug) {

							// Find robot vector

						}
						// finds the pixels to cm Ratio
						Scalar minValuesc = new Scalar(((H_CORNER.getValue() / 2) - 10),
								((S_CORNER.getValue() / 100) * 255 - 10), ((V_CORNER.getValue() / 100) * 255 - 10));
						Scalar maxValuesc = new Scalar(((H_CORNER.getValue() / 2) + 10),
								((S_CORNER.getValue() / 100) * 255 + 10), ((V_CORNER.getValue() / 100) * 255 + 10));
/*					
						  Point p = ip.findColor(frame, minValuesc, maxValuesc);
						  ip.findCorners(frame, p, (int)TRESHOLD.getValue());
						  updateImageView(cornerImage, Utils.mat2Image(ip.getOutput()));
*/						 
						// finds the front and back of the robot
					
						updateImageView(robotImage, Utils.mat2Image(ip.findBackAndFront(frame)));

						// convert and show the frame
						Mat resizeimage = new Mat();
						Size scaleSize = new Size(600, 320);
						Imgproc.resize(frame, resizeimage, scaleSize, 0, 0, Imgproc.INTER_AREA);
						Image imageToShow = Utils.mat2Image(resizeimage);
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
	 * The action triggered by pushing the button on the GUI
	 */
	@FXML
	private void startRobot() {

		if (!this.robotActive) {

			this.robotActive = true;
			//rc.start();
			// update the button content
			this.robotButton.setText("Stop Camera");
			System.out.println("Robot starting...");

		} else {

			this.robotActive = false;
			// update the button content
			this.robotButton.setText("Start Camera");

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
		if (this.capture.isOpened() || isDebug) {
			try {

				if (isDebug == true) {

					// read from from test image
					frame = Imgcodecs.imread(debugImg1);

				} else {

					// read the current frame
					this.capture.read(frame);

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
	 * HSV IMAGE ANALYSIS
	 * 
	 * Do image analysis using HSV values
	 * 
	 * @return the {@link Image} to show
	 */
	private Mat grabFrameHSV(Mat frame) {

		// if the frame is not empty, process it
		if (!frame.empty()) {
			// init
			Mat blurredImage = new Mat();
			Mat hsvImage = new Mat();
			Mat mask = new Mat();
			Mat morphOutput = new Mat();

			// Applying GaussianBlur on the Image (Gives a much cleaner/less noisy result)
			Imgproc.GaussianBlur(frame, blurredImage, new Size(45, 45), 0);

			// convert the frame to HSV
			Imgproc.cvtColor(blurredImage, hsvImage, Imgproc.COLOR_BGR2HSV);

			// get thresholding values from the UI
			// remember: H ranges 0-180, S and V range 0-255
			Scalar minValues = new Scalar(this.hueStart.getValue(), this.saturationStart.getValue(),
					this.valueStart.getValue());
			Scalar maxValues = new Scalar(this.hueStop.getValue(), this.saturationStop.getValue(),
					this.valueStop.getValue());

			// show the current selected HSV range
			String valuesToPrint = "Hue range: " + minValues.val[0] + "-" + maxValues.val[0] + "\tSaturation range: "
					+ minValues.val[1] + "-" + maxValues.val[1] + "\tValue range: " + minValues.val[2] + "-"
					+ maxValues.val[2];

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

			// find the tennis ball(s) contours and show them
			frame = this.findAndDrawBalls(morphOutput, frame);

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
	private Mat grabFrameHough(Mat frame) {

		// if the frame is not empty, process it
		if (!frame.empty()) {
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
			String valuesToPrint = "Min dist: " + min_dist + ", Upper Threshold: " + uThresh + ", Center Threshold: "
					+ cTresh + ", Min Radius: " + minRad + ", Max Radius: " + maxRad;

			Utils.onFXThread(this.hsvValuesProp, valuesToPrint);

			Imgproc.HoughCircles(grayImage, circles, Imgproc.HOUGH_GRADIENT, 1.0, (double) grayImage.rows() / min_dist,
					uThresh, cTresh, minRad, maxRad);
			List<Point> p = new ArrayList<>();
			for (int x = 0; x < circles.cols(); x++) {

				double[] c = circles.get(0, x);
				Point center = new Point(Math.round(c[0]), Math.round(c[1]));
				p.add(center);
				// circle center

				Imgproc.circle(frame, center, 1, new Scalar(0, 100, 100), 3, 8, 0);
				// circle outline
				int radius = (int) Math.round(c[2]);
				Imgproc.circle(frame, center, radius, new Scalar(255, 0, 255), 3, 8, 0);
				Imgproc.circle(frame, center, 1, new Scalar(0, 255, 255), 3, 8, 0);

				// Print center coordinates

			}
			BallList s = BallList.getInstance();
			s.clearList();
			for (Point B : p) {
				s.add(new Ball(B.x, B.y));
			}

			for (int i = 0; i < p.size(); i++) {
				// System.out.println("Point (X,Y): "+p.get(i));

			}

		}

		return frame;
	}

	private Mat findAndDrawRect(Mat frame) {

		/*
		 * double ratio = 600 / Math.max(frame.width(), frame.height()); Size
		 * downscaledSize = new Size(frame.width() * ratio, frame.height() * ratio); Mat
		 * dst = new Mat(downscaledSize, frame.type()); Imgproc.resize(frame, dst,
		 * downscaledSize);
		 */
		Mat debugImg1 = Imgcodecs.imread(noImg);
		Mat detectedEdges = new Mat();
		Mat edges = new Mat();
		Mat blurredImage = new Mat();
		Mat hsvImage = new Mat();
		Mat mask = new Mat();

		// Applying GaussianBlur on the Image (Gives a much cleaner/less noisy result)
		Imgproc.GaussianBlur(frame, blurredImage, new Size(45, 45), 0);

		// convert the frame to HSV
		Imgproc.cvtColor(blurredImage, hsvImage, Imgproc.COLOR_BGR2HSV);

		// get thresholding values from the UI
		// remember: H ranges 0-180, S and V range 0-255
		Scalar minValues = new Scalar(this.hueStart.getValue(), this.saturationStart.getValue(),
				this.valueStart.getValue());
		Scalar maxValues = new Scalar(this.hueStop.getValue(), this.saturationStop.getValue(),
				this.valueStop.getValue());

		// show the current selected HSV range
		String valuesToPrint = "Hue range: " + minValues.val[0] + "-" + maxValues.val[0] + "\tSaturation range: "
				+ minValues.val[1] + "-" + maxValues.val[1] + "\tValue range: " + minValues.val[2] + "-"
				+ maxValues.val[2];

		Utils.onFXThread(this.hsvValuesProp, valuesToPrint);

		// threshold HSV image to select tennis balls
		Core.inRange(hsvImage, minValues, maxValues, mask);
		// show the partial output
		this.updateImageView(this.morphImage, Utils.mat2Image(mask));

		// try to filter everything inside the rectangle
		Imgproc.medianBlur(mask, detectedEdges, 9);

		// Imgproc.erode(detectedEdges, detectedEdges, new Mat());

		// canny detector, with ratio of lower:upper threshold of 3:1
		Imgproc.Canny(detectedEdges, edges, this.C_Low.getValue(), this.C_Max.getValue(), 3, true);
		// STEP 5: makes the object in white bigger to join nearby lines
		Imgproc.dilate(edges, edges, new Mat(), new Point(-1, -1), 1); // 1

		List<MatOfPoint> contours = new ArrayList<>();
		Imgproc.findContours(edges, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		// STEP 7: Sort the contours by length and only keep the largest one

		if (contours.size() > 0) {

			double maxArea = 0;
			int maxAreaIdx = -1;

			for (int idx = 0; idx != contours.size(); ++idx) {
				Mat contour = contours.get(idx);
				double contourarea = Imgproc.contourArea(contour);
				if (contourarea > maxArea) {
					maxArea = contourarea;
					maxAreaIdx = idx;
				}

			}

			if (maxAreaIdx >= 0) {

				MatOfPoint largestContour = contours.get(maxAreaIdx);

				// STEP 8: Generate the convex hull of this contour
				Mat convexHullMask = Mat.zeros(frame.rows(), frame.cols(), frame.type());
				MatOfInt hullInt = new MatOfInt();
				Imgproc.convexHull(largestContour, hullInt);
				MatOfPoint hullPoint = OpenCVUtil.getNewContourFromIndices(largestContour, hullInt);
				// Use approxPolyDP to simplify the convex hull (this should give a
				// quadrilateral)
				MatOfPoint2f polygon = new MatOfPoint2f();
				Imgproc.approxPolyDP(OpenCVUtil.convert(hullPoint), polygon, 20, true);
				List<MatOfPoint> tmp = new ArrayList<>();
				tmp.add(OpenCVUtil.convert(polygon));
				// restoreScaleMatOfPoint(tmp, 0.9);

				Imgproc.drawContours(convexHullMask, tmp, 0, new Scalar(25, 25, 255), 2);

				MatOfPoint2f finalCorners = new MatOfPoint2f();
				MatOfPoint2f maxCurve = new MatOfPoint2f();
				Point[] tmpPoints = polygon.toArray();
				for (Point point : tmpPoints) {
					point.x = point.x / 0.9;
					point.y = point.y / 0.9;
				}
				finalCorners.fromArray(tmpPoints);

				if (finalCorners.toArray().length == 4) {
					Size size = getRectangleSize(finalCorners);

					maxCurve = polygon;

					Mat result = Mat.zeros(size, frame.type());
					// Homography: Use findHomography to find the affine transformation
					// of the rectangle
					Mat homography = new Mat();
					MatOfPoint2f dstPoints = new MatOfPoint2f();
					Point[] arrDstPoints = { new Point(result.cols(), result.rows()), new Point(0, result.rows()),
							new Point(0, 0), new Point(result.cols(), 0) };
					dstPoints.fromArray(arrDstPoints);
					homography = Calib3d.findHomography(maxCurve, dstPoints);

					// Draws circles around the corners of the found rectangle

					/*
					 * double temp_double[] = dstPoints.get(0, 0); Point p1 = new
					 * Point(temp_double[0], temp_double[1]); Imgproc.circle(frame, new Point(p1.x,
					 * p1.y), 20, new Scalar(255, 0, 0), 5); //p1 is colored red
					 * 
					 * temp_double = dstPoints.get(1, 0); Point p2 = new Point(temp_double[0],
					 * temp_double[1]); Imgproc.circle(frame, new Point(p2.x, p2.y), 20, new
					 * Scalar(0, 255, 0), 5); //p2 is colored green
					 * 
					 * temp_double = dstPoints.get(2, 0); Point p3 = new Point(temp_double[0],
					 * temp_double[1]); Imgproc.circle(frame, new Point(p3.x, p3.y), 20, new
					 * Scalar(0, 0, 255), 5); //p3 is colored blue
					 * 
					 * temp_double = dstPoints.get(3, 0); Point p4 = new Point(temp_double[0],
					 * temp_double[1]); Imgproc.circle(frame, new Point(p4.x, p4.y), 20, new
					 * Scalar(0, 255, 255), 5); //p1 is colored violet
					 */

					// Warp the input image using the computed homography matrix
					Imgproc.warpPerspective(frame, result, homography, size);

					frame = result;

				} else {

					frame = debugImg1;

				}
			}

		} else {

			frame = debugImg1;
		}

		return frame;

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

	/**
	 * Given a binary image containing one or more closed surfaces, use it as a mask
	 * to find and highlight the objects contours
	 * 
	 * @param maskedImage
	 *            the binary image to be used as a mask
	 * @param frame
	 *            the original {@link Mat} image to be used for drawing the objects
	 *            contours
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

	/**
	 * Set typical {@link ImageView} properties: a fixed width and the information
	 * to preserve the original image ration
	 * 
	 * @param image
	 *            the {@link ImageView} to use
	 * @param dimension
	 *            the width of the image to set
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
	 * @param view
	 *            the {@link ImageView} to update
	 * @param image
	 *            the {@link Image} to show
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

}