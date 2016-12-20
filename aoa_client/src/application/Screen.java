package application;

import java.io.IOException;
import java.io.InputStream;
import java.util.Observable;
import java.util.Observer;

import config.Routes;
import config.ViewConfig;
import controllers.GameCenterController;
import controllers.LoginController;
import controllers.MessageController;
import io.DataLoader;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.ScreenSettings;
import model.ScreenType;

public class Screen extends Stage implements Observer {
	
	private Application app;
	
	private Stage stage;

	
	
	
	
	public Screen(Stage window) {
		this.stage = window;
		this.app = Application.getInstance(); 
		this.runConnecting();
	}
	
	public void run(){
		this.stage.show();
		this.centerStage();
	}
	
	private Initializable runScreen(ScreenType screen) throws IOException {		
		ScreenSettings cfg = ViewConfig.getScreen(screen);
		InputStream in = Main.class.getResourceAsStream(Routes.getLayoutFile(cfg.getName()));
		FXMLLoader loader = new FXMLLoader();
        //loader.setBuilderFactory(new JavaFXBuilderFactory());
		
		//System.out.println(Routes.getLayoutFile(cfg.getName()));
		
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
		
		this.centerStage();		
		
		return loader.getController();		
	}
	
	private void centerStage(){
		Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
	    this.stage.setX((screenBounds.getWidth() - this.stage.getWidth()) / 2); 
	    this.stage.setY((screenBounds.getHeight() - this.stage.getHeight()) / 2);
	}
	
	public Void runLogin(){
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
		
		return null;
	}
	
	public void runConnecting(){
		Screen scrn = this;
		Platform.runLater(new Runnable() {
		    public void run() {
		    	try {
					MessageController controller = (MessageController) scrn.runScreen(ScreenType.Message);
					controller.setMessage(ViewConfig.MSG_CONNECTION);
					controller.setApp(scrn);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
		    }
		});
	}
	
	public void runChecking(){
		Screen scrn = this;
		Platform.runLater(new Runnable() {
		    public void run() {
		    	try {
					MessageController controller = (MessageController) scrn.runScreen(ScreenType.Message);
					controller.setMessage(ViewConfig.MSG_CHECKING);
					controller.setApp(scrn);
					
//					new Thread(new Task<Void>() {
//						protected Void call() throws Exception {
//							Application.awaitAtClientBarrier("GUI – waits for client checks username.");
//							runLogin();
//							return null;
//						}
//				    }).start();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
		    }
		});	
	}
	
	public void runGameCenter(){
		Screen scrn = this;
		Platform.runLater(new Runnable() {
		    public void run() {
		    	try {
					GameCenterController controller = (GameCenterController) scrn.runScreen(ScreenType.GameCenter);
					controller.setApp(scrn);		
					controller.updateRoomList();
				} catch (IOException e) {
					e.printStackTrace();
				}
		    }
		});	
	}
	
	
	
//	public void awaitAtBarrier(Callable<Void> callback, String str){
//	    new Thread(new Task<Boolean>() {
//	        @Override
//	        protected Boolean call() throws Exception {
//	        	Application.awaitAtClientBarrier(str);
//	        	callback.call();
//	            return null;
//	        }
//	    }).start();
//	}
	
	//observer
	@Override
	public void update(Observable o, Object arg) {
		//a change appeared
		
	}
	
	public Stage getWindow(){
		return this.stage;
	}
}