package io;

import java.io.IOException;
import java.io.InputStream;

import application.Main;
import config.Routes;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.FXMLSource;

public class DataLoader {
	
	
	public static FXMLSource loadLayout(String path) {
        InputStream in = Main.class.getResourceAsStream(Routes.getLayoutFile(path));
        return DataLoader.loadFXML(in);
	}

    public static FXMLSource loadPartialLayout(String path) {
	    InputStream in = Main.class.getResourceAsStream(Routes.getPartialLayoutFile(path));
        return DataLoader.loadFXML(in);
    }
	public static void loadStylesheet(Stage stage, Scene scene, String name) {
		scene.getStylesheets().add(stage.getClass().getResource(Routes.getStyleFile(name)).toExternalForm());
	}

	private static FXMLSource loadFXML(InputStream in) {
	    FXMLLoader loader = new FXMLLoader();

        try {
            BorderPane root = (BorderPane) loader.load(in);
            Initializable controller = loader.getController();
            in.close();
            return new FXMLSource(root, controller);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
	}
}
