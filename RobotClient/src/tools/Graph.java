package tools;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class Graph {
	
	private Mat graph;
	
	public Graph() {
		graph = new Mat();
	}
	
	
	public Mat updateGraph(Mat frame) {
		
		return drawGraph(frame);
 
	}
	
	
	private Mat drawGraph(Mat size){
		
		int rows = size.rows() ;
		System.out.println("Rows: " + rows);
		int cols = size.cols() ;
		System.out.println("Cols: "+ cols );
		Mat graph = new Mat(rows, cols, size.type());
		graph.setTo(new Scalar(200,200,200));
		Imgproc.line(graph, new Point(10,0), new Point(10,graph.rows()-5), new Scalar(0, 0, 0));
	
		
		
		
		for(int i = 20 ; i < graph.rows(); i+=10) {
		Imgproc.line(graph, new Point(i,10), new Point(i,graph.rows()-5), new Scalar(150,150,150));
				
		}

		for(int i = 10 ; i < graph.cols(); i+=10) {
			Imgproc.line(graph, new Point(10,i), new Point(graph.cols()-5,i), new Scalar(150,150,150));
				
		}
		
		Imgproc.circle(graph, new Point(69,69), 8 ,new Scalar(255, 0, 0));
		
		
		
		return graph;
		
		
		
	}
	
	

}
