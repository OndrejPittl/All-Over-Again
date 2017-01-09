package controllers;

import application.Application;
import application.Screen;
import config.Routes;
import config.ViewConfig;
import game.GameMove;
import io.DataLoader;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.FXMLSource;
import model.Player;
import model.Room;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;


public class PlaygroundController extends ScreenController {

    private Room room;

    private int dimension;

    private Timeline timer;
    private int timerValue;


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

    @FXML
    private VBox vb_timerWrapper;



    private int fieldCount;

    private BoardFieldController[] fieldControllers;

    private ArrayList<GameMove> moves;




    public void prepare(){
        this.updateInfo();
        this.initPlayerList();
        this.initBoard();
    }

    private void updateInfo(){
        this.moves = new ArrayList<>();
        this.room = this.app.getSelectedRoom();
        this.dimension = this.room.getBoardDimension().getDimension();
        //this.dimension = GameConfig.DEFAULT_BOARD_DIMENSION;
        this.fieldCount = this.dimension * this.dimension;
        this.fieldControllers = new BoardFieldController[this.fieldCount];
    }

    private void initTimer(){
        this.timerValue = this.app.getTurnTime();
        this.lbl_timer.setText(String.valueOf(this.timerValue));

        this.timer = new Timeline (
            new KeyFrame(Duration.millis(1000), (e) -> {

                this.timerValue--;
                this.lbl_timer.setText(String.valueOf(this.timerValue));

                if(this.timerValue == 0) {
                    this.endTurn();
                }
            })
        );

        System.out.println("Timer setting to: " + this.timerValue);
        this.timer.setCycleCount(this.timerValue);
//        this.timer.setDelay(Duration.millis(1000));
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
            src = DataLoader.loadPartialLayout(Routes.LAYOUT_PARTIAL_PLAYER_RECORD);

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
            FXMLSource src = DataLoader.loadPartialLayout(Routes.LAYOUT_PARTIAL_BOARD_FIELD);
            this.fieldControllers[i] = (BoardFieldController) src.getController();
            this.fieldControllers[i].fitSize(w);
            this.fieldControllers[i].setPlaygroundController(this);
            this.fieldControllers[i].setDifficulty(this.room.getDifficulty());
            this.fieldControllers[i].setIndex(i);

            BorderPane field = (BorderPane) src.getRoot();

            int c = i % this.dimension,
                r = (int) Math.floor(i / this.dimension);

            this.gp_playground.add(field, c, r);
        }

    }

    public void registerMove(GameMove m){
        this.moves.add(m);
        System.out.println(Arrays.toString(this.moves.toArray()));
    }

    public void startTurn() {
        Platform.runLater(() -> {

//            if(!this.app.isTurnDataOK()) {
//                // konec hry!
//
//
//
//                return;
//            }


            this.initTimer();
            this.playTurnTask();
        });
    }

    private void proceedTurnStart(){
        this.enableBoard();
        //this.vb_timerWrapper.setVisible(true);
        this.timer.play();
    }


    public void endTurn(){
        this.disableBoard();
        //this.vb_timerWrapper.setVisible(false);
        this.timer.stop();
        this.app.storeProgress(this.moves);
        this.moves.clear();

        Application.awaitAtGuiBarrier("GUI releases. Turn ends.");

//        this.app.proceedEndTurn(this.moves);
    }

    private void enableBoard(){
        this.gp_playground.setDisable(false);
    }

    private void disableBoard(){
        this.gp_playground.setDisable(true);
        BoardFieldController.stopActivity();
    }

    private void playTurnTask(){
        GameMove[] progress = this.app.getProgress();

        if(progress == null) {
            this.proceedTurnStart();
            return;
        }

        Timeline timeline = new Timeline(
            new KeyFrame(Duration.millis(ViewConfig.TIMER_TURN_INTRO_MOVE_DURATION), new EventHandler<ActionEvent>() {
                int i = 0;
                @Override
                public void handle(ActionEvent event) {
                    Platform.runLater(() -> {
                        GameMove m = progress[i];
                        BoardFieldController c = fieldControllers[m.getIndex()];
                        c.displayMove(m);
                        i++;
                    });
                }
            })
        );
        timeline.setCycleCount(progress.length);
        timeline.setOnFinished((e) ->
            new Timeline(new KeyFrame(
                Duration.millis(ViewConfig.TIMER_TURN_INTRO_MOVE_DURATION),
                (ActionEvent) -> this.proceedTurnStart()
            )).play()
        );

        timeline.play();
    }
}
