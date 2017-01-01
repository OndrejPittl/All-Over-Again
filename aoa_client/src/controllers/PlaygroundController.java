package controllers;

import application.Application;
import application.Screen;
import config.ViewConfig;
import io.DataLoader;
import io.FXMLSource;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Player;
import model.Room;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by OndrejPittl on 31.12.16.
 */
public class PlaygroundController implements Initializable {


    private Stage window;

    private Screen screen;

    private Application app;

    @FXML
    private Label lbl_timer;

    @FXML
    private Button btn_giveUp;

    @FXML
    private Button btn_btn;

    @FXML
    private Button btn_exitGame;

    @FXML
    private GridPane gp_playground;

    @FXML
    private VBox vb_playerWrapper;



    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    public void prepare(){
        this.initPlayerList();
    }

    private void initPlayerList(){
        FXMLSource src;
        BorderPane item;
        PlayerController controller;
        Room room = this.app.getSelectedRoom();
        Player[] players = room.getPlayers();

        vb_playerWrapper.getChildren().clear();

        for (Player p : players) {
            src = DataLoader.loadPartialLayout(ViewConfig.LAYOUT_PARTIAL_PLAYER_RECORD);

            controller = (PlayerController) src.getController();
            controller.setData(p.getName());

            item = (BorderPane) src.getRoot();
            vb_playerWrapper.getChildren().add(item);
        }
    }

    public void setApp(Screen screen, Application app){
        this.screen = screen;
        this.app = app;
    }

    public void handleLogin(){
        //String nick = tf_username.getText();
        //
        //if(UserInputValidator.validateUsername(nick)) {
        //    this.app.registerPlayer(new Player(nick));
        //    //Application.awaitAtGuiBarrier();
        //
        //    Application.awaitAtGuiBarrier("GUI relseases. Username entered. (5GRG)");
        //}
    }
}
