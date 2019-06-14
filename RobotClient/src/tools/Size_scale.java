package tools;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.MatOfPoint;

import objects.Robot;

public class Size_scale implements I_Size_Scale {
	private double Ratio;

	public Size_scale() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see application.I_Size_Scale#pixelToCm(java.util.List)
	 */
	@Override
	public void pixelToCm(List<org.opencv.core.Point> list) {
		org.opencv.core.Point start = list.get(0);
		List<Double> lengths = new ArrayList<>();

		for (org.opencv.core.Point point : list) {

			lengths.add(Math.sqrt(Math.pow(point.x - start.x, 2) + Math.pow(point.y - start.y, 2)));
		}
		Double pixellengths = 0.0;
		for (Double b : lengths) {

			if (b >= pixellengths) {
				pixellengths = b;
			}
				}
		lengths.remove(pixellengths);
		pixellengths = 0.0;

		for (Double a : lengths) {

			if (a >= pixellengths) {
				pixellengths = a;
			}
			

		}
		this.Ratio = (double)pixellengths/7;
		Robot.getInstance().setPixelToCM(Ratio);
		//System.out.println("Pixels for 1cm: "+Ratio);
	}
}