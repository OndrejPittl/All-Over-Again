package controllers;

import application.Application;
import application.Screen;
import config.GameConfig;
import config.ViewConfig;
import io.DataLoader;
import io.FXMLSource;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Player;
import model.Room;

import java.net.URL;
import java.util.ResourceBundle;


public class PlaygroundController implements Initializable {

    private Room room;

    private int dimension;



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



    private int fieldCount;

    private BoardFieldController[] fieldControllers;





    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    public void prepare(){
        this.room = this.app.getSelectedRoom();
        //this.dimension = this.room.getBoardDimension();
        this.dimension = GameConfig.DEFAULT_BOARD_DIMENSION;

        this.fieldCount = this.dimension * this.dimension;
        this.fieldControllers = new BoardFieldController[this.fieldCount];

        this.initPlayerList();
        this.initBoard();
    }

    private void initPlayerList(){

        // TODO: updatePlayerList(), zmena existujicich komponent

        FXMLSource src;
        BorderPane item;
        PlayerController controller;
        Room room = this.app.getSelectedRoom();
        Player[] players = room.getPlayers();

        vb_playerWrapper.getChildren().clear();

        for (Player p : players) {
            src = DataLoader.loadPartialLayout(ViewConfig.LAYOUT_PARTIAL_PLAYER_RECORD);

            controller = (PlayerController) src.getController();
            controller.setData(p);

            item = (BorderPane) src.getRoot();
            vb_playerWrapper.getChildren().add(item);
        }
    }

    private void initBoard(){
        int w = (int) (this.gp_playground.getPrefWidth() / this.dimension);

        double pSize = 100.0 / this.dimension;

        // this.gp_playground.setHgap(0);
        // this.gp_playground.setVgap(0);

        for (int i = 0; i < this.dimension; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(pSize);
            colConst.setHgrow(Priority.ALWAYS);
            this.gp_playground.getColumnConstraints().add(colConst);
        }

        for (int i = 0; i < this.dimension; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPercentHeight(pSize);
            rowConst.setVgrow(Priority.ALWAYS);
            this.gp_playground.getRowConstraints().add(rowConst);
        }

        for (int i = 0 ; i < this.fieldCount; i++) {
            FXMLSource src = DataLoader.loadPartialLayout(ViewConfig.LAYOUT_PARTIAL_BOARD_FIELD);
            this.fieldControllers[i] = (BoardFieldController) src.getController();

            BorderPane field = (BorderPane) src.getRoot();
            field.setPrefWidth(w);
            field.setPrefHeight(w);

            int c = i % GameConfig.DEFAULT_BOARD_DIMENSION,
                r = (int) Math.floor(i / GameConfig.DEFAULT_BOARD_DIMENSION);

            this.gp_playground.add(field, c, r);
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
