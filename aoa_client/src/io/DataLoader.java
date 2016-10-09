package io;

import java.io.IOException;

import config.Routes;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DataLoader {
	
	
	public static Parent loadLayout(Stage stage, String name){
		try {
			
			Class<? extends Stage> c = stage.getClass();
			return FXMLLoader.load(c.getResource(Routes.getLayoutFile(name)));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	public static void loadStylesheet(Stage stage, Scene scene, String name) {
		scene.getStylesheets().add(stage.getClass().getResource(Routes.getStyleFile(name)).toExternalForm());
	}
	
	
	
//	private String determineLayout(Application scene){
//		
//	}

}
