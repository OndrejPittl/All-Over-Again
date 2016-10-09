package view;

import java.io.IOException;
import java.io.InputStream;

import application.Main;
import config.AppConfig;
import config.Routes;
import config.ScreenConfig;
import config.ScreenEnum;
import controllers.LoginController;
import io.DataLoader;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Screen extends Stage {
	
	private Stage stage;
	
	
	
	
	public Screen(Stage window) {
		this.stage = window;
		this.runLogin();
	}
	
	public void run(){
		this.stage.show();
	}
	
	private Initializable runScreen(ScreenEnum screen) throws IOException{		
		ScreenConfig cfg = AppConfig.getScreen(screen);
		
		InputStream in = Main.class.getResourceAsStream(Routes.getLayoutFile(cfg.getName()));
		
		FXMLLoader loader = new FXMLLoader();
        loader.setBuilderFactory(new JavaFXBuilderFactory());
		
        BorderPane root = (BorderPane) loader.load(in);
		in.close();
		
		Scene scene = new Scene(
			root,
			cfg.getWidth(),
			cfg.getHeight()
		);
				
		DataLoader.loadStylesheet(this, scene, "style");
		
		this.stage.setMinWidth(cfg.getMinWidth());
		this.stage.setMinHeight(cfg.getMinHeight());
		this.stage.setTitle(cfg.getTitle());
		this.stage.setScene(scene);
		this.stage.sizeToScene();
		
		return (Initializable) loader.getController();		
	}
	
	
	
	public void runLogin(){
		try {
			LoginController controller = (LoginController) this.runScreen(ScreenEnum.Login);
			controller.setApp(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public Stage getWindow(){
		return this.stage;
	}
}