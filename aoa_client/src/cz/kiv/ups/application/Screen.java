package cz.kiv.ups.application;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import cz.kiv.ups.config.ViewConfig;
import cz.kiv.ups.controllers.*;
import cz.kiv.ups.io.DataLoader;
import cz.kiv.ups.model.FXMLSource;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import cz.kiv.ups.model.ScreenSettings;
import cz.kiv.ups.model.ScreenType;

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
        Platform.runLater(() -> this.init() );
    }

    private void init(){
		this.stage.setOnCloseRequest((e) -> {
			Application.disconnect(true);
		});

		this.stage.show();
        this.centerStage();
	}
	
	private Initializable runScreen(ScreenType screen) throws IOException {
        System.out.println("--- running: " + screen.getName());

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

        //this.centerStage();

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

    public void updatePlayerList(){
        Platform.runLater(() -> {
            this.playgroundController.updatePlayerList();
        });
    }

    public void askPlayerWait(){
        Platform.runLater(() -> {
            this.playgroundController.askPlayerWait();
            Application.awaitAtGuiBarrier("GUI: releasess GUIC with user interaction.");
        });

        Application.awaitAtGuiBarrier("GUIC: waits for GUI for user interaction.");
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