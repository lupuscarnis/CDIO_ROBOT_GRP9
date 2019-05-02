package application;
	
import java.io.IOException;
import java.util.Scanner;

import org.opencv.core.Core;

import connection.ConsoleOutput;
import dao.DAO;
import dto.DTO;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;

/* https://github.com/opencv-java
 *  Implementation of OpenCV for use with webcam 
 *  
 */
public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage)
	{
		try
		{
			// load the FXML resource
			FXMLLoader loader = new FXMLLoader(getClass().getResource("SampleJFX.fxml"));
			// store the root element so that the controllers can use it
			BorderPane root = (BorderPane) loader.load();
			// set a whitesmoke background
			root.setStyle("-fx-background-color: whitesmoke;");
			// create and style a scene
			Scene scene = new Scene(root, 800, 600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			// create the stage with the given title and the previously created
			// scene
			primaryStage.setTitle("Gucci Gang!");
			primaryStage.setScene(scene);
			// show the GUI
			primaryStage.show();
			
			// set the proper behavior on closing the application
			FXController controller = loader.getController();
			primaryStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we)
				{
					controller.setClosed();
				}
			}));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		// load the native OpenCV library
		//System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		// Load the library from maven instead of native (userlib)
//		DAO dao = new DAO();
//		DTO Danny = new DTO();
//		Danny.setDistance(0);
//		Danny.setRotation(0);
//		Danny.setBallpickup(false);
//		dao.sendData(Danny);
//		
		nu.pattern.OpenCV.loadShared();
		Thread console = new Thread( new ConsoleOutput());
		console.start();
		launch(args);
		try {
			console.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
		
		
		
	}
}
