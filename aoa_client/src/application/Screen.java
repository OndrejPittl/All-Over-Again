package application;

import java.io.IOException;
import java.io.InputStream;
import java.util.Observable;
import java.util.Observer;

import config.AppConfig;
import config.CommunicationConfig;
import config.Routes;
import controllers.LoginController;
import controllers.MessageController;
import io.DataLoader;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.ScreenType;
import model.ScreenSettings;

public class Screen extends Stage implements Observer {
	
	private Application app;
	
	private Stage stage;

	
	
	
	
	public Screen(Stage window) {
		this.stage = window;
		this.app = Application.getInstance();
		//this.app.setGUI(this);

		//this.runLogin();
		this.runConnecting();
	}
	
	public void run(){
		this.stage.show();
	}
	
	private Initializable runScreen(ScreenType screen) throws IOException {		
		ScreenSettings cfg = AppConfig.getScreen(screen);
		InputStream in = Main.class.getResourceAsStream(Routes.getLayoutFile(cfg.getName()));
		FXMLLoader loader = new FXMLLoader();
        //loader.setBuilderFactory(new JavaFXBuilderFactory());
		
		System.out.println(Routes.getLayoutFile(cfg.getName()));
		
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
		//this.stage.sizeToScene();
		
		return loader.getController();		
	}
	
	public void runLogin(){
		Screen scrn = this;
		Platform.runLater(new Runnable() {
		    public void run() {
		    	try {
					LoginController controller = (LoginController) scrn.runScreen(ScreenType.Login);
					controller.setApp(scrn);
				} catch (IOException e) {
					e.printStackTrace();
				}
		    }
		});
	}
	
	public void runConnecting(){
		try {
			MessageController controller = (MessageController) this.runScreen(ScreenType.Message);
			controller.setMessage(CommunicationConfig.MSG_CONNECTION);
			controller.setApp(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	//observer
	@Override
	public void update(Observable o, Object arg) {
		//a change appeared
		
	}
	
	public Stage getWindow(){
		return this.stage;
	}
}