package application;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import config.ViewConfig;
import controllers.*;
import io.DataLoader;
import model.FXMLSource;
import javafx.application.Platform;
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


	private PlaygroundController playgroundController;
	
	
	
	public Screen(Stage window, Application app) {
		this.stage = window;
		this.app = app;
		this.me = this;
	}

    public void run(){
        Platform.runLater(() -> {
            this.centerStage();
            this.stage.show();
        });
    }
	
	private Initializable runScreen(ScreenType screen) throws IOException {
        System.out.println("--- running: " + screen.getName());

		ScreenSettings cfg = ViewConfig.getScreen(screen);

//		System.out.println("before loading.................");
        FXMLSource src = DataLoader.loadLayout(cfg.getName());
//		System.out.println("after loading.................");
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
        this.centerStage();

		return src.getController();
	}

    private void centerStage(double w, double h){
        Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        this.stage.setX((screenBounds.getWidth() - w) / 2);
        this.stage.setY((screenBounds.getHeight() - h) / 2);
    }

    private void centerStage(){
        this.centerStage(this.stage.getWidth(), this.stage.getHeight());
    }
	
	public Void runLogin(){
		// new Runnable() -> run()
		Platform.runLater(() -> {
			try {
				LoginController controller = (LoginController) me.runScreen(ScreenType.Login);
				controller.setApp(app);
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
				controller.setApp(app);
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
				controller.setApp(app);
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
				controller.setApp(app);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

    public void runGameCenter(){
        Platform.runLater(() -> {
            try {
                GameCenterController controller = (GameCenterController) me.runScreen(ScreenType.GameCenter);
                controller.setApp(app);
                controller.updateRoomList();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void runGamePlayground(){
        Platform.runLater(() -> {
            try {
                this.playgroundController = (PlaygroundController) me.runScreen(ScreenType.Playground);
                this.playgroundController.setApp(app);
                this.playgroundController.prepare();
                Application.awaitAtGuiBarrier("GUI releases after board initialization.");
                //controller.updateRoomList();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void runGameResults(){
        Platform.runLater(() -> {
            try {
                ResultsController controller = (ResultsController) me.runScreen(ScreenType.GameResult);
                controller.setApp(app);
                controller.update();
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

    public void beginTurn() {
        this.playgroundController.startTurn();
    }
}