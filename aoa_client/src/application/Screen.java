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
import controllers.PlaygroundController;
import io.DataLoader;
import io.FXMLSource;
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

	private Screen me;
	
	
	
	public Screen(Stage window, Application app) {
		this.stage = window;
		this.app = app;
		this.me = this;
		this.runConnecting();
	}
	
	public void run(){
		this.stage.show();
		this.centerStage();
	}
	
	private Initializable runScreen(ScreenType screen) throws IOException {		
		ScreenSettings cfg = ViewConfig.getScreen(screen);
        FXMLSource src = DataLoader.loadLayout(cfg.getName());
		BorderPane root = (BorderPane) src.getRoot();

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
//		this.stage.sizeToScene();


		this.centerStage();		
		return src.getController();
	}
	
	private void centerStage(){
		Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
	    this.stage.setX((screenBounds.getWidth() - this.stage.getWidth()) / 2); 
	    this.stage.setY((screenBounds.getHeight() - this.stage.getHeight()) / 2);
	}
	
	public Void runLogin(){
		// new Runnable() -> run()
		Platform.runLater(() -> {
			try {
				LoginController controller = (LoginController) me.runScreen(ScreenType.Login);
				controller.setApp(me, app);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
		return null;
	}
	
	public void runConnecting(){
		Platform.runLater(() -> {
			try {
				MessageController controller = (MessageController) me.runScreen(ScreenType.Message);
				controller.setMessage(ViewConfig.MSG_CONNECTION);
				controller.setApp(me, app);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public void runWaiting(){
		Platform.runLater(() -> {
			try {
				MessageController controller = (MessageController) me.runScreen(ScreenType.Message);
				controller.setMessage(ViewConfig.MSG_WAITING_GAME_INIT);
				controller.setApp(me, app);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	
	public void runChecking(){
		Platform.runLater(() -> {
			try {
				MessageController controller = (MessageController) me.runScreen(ScreenType.Message);
				controller.setMessage(ViewConfig.MSG_CHECKING);
				controller.setApp(me, app);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

    public void runGameCenter(){
        Platform.runLater(() -> {
            try {
                GameCenterController controller = (GameCenterController) me.runScreen(ScreenType.GameCenter);
                controller.setApp(me, app);
                controller.updateRoomList();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void runGamePlayground(){
        Platform.runLater(() -> {
            try {
                PlaygroundController controller = (PlaygroundController) me.runScreen(ScreenType.Playground);
                controller.setApp(me, app);
                controller.prepare();
                //controller.updateRoomList();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
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