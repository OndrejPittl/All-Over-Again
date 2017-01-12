package cz.kiv.ups.controllers;

import cz.kiv.ups.application.Application;
import cz.kiv.ups.config.Routes;
import cz.kiv.ups.config.ViewConfig;
import cz.kiv.ups.game.GameMove;
import cz.kiv.ups.game.GameTurn;
import cz.kiv.ups.io.DataLoader;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.Duration;
import cz.kiv.ups.model.FXMLSource;
import cz.kiv.ups.model.Player;
import cz.kiv.ups.model.Room;

import java.util.ArrayList;
import java.util.Arrays;


public class PlaygroundController extends ScreenController {

    private Room room;

    private GameTurn turn;

    private boolean amIActive;

    private int dimension;

    private Timeline timer;

    private int timerValue;

    private int moveCounter = 0;


    @FXML
    private Label lbl_timer;

    @FXML
    private Label lbl_movesDone;

    @FXML
    private Label lbl_movesRequired;

    @FXML
    private Button btn_giveUp;

//    @FXML
//    private Button btn_btn;
//
//    @FXML
//    private Button btn_exitGame;

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
        this.fieldCount = this.dimension * this.dimension;
        this.fieldControllers = new BoardFieldController[this.fieldCount];
    }

    private void initTimer(){
        this.timerValue = this.app.getTurnTime();
        this.lbl_timer.setText(String.valueOf(this.timerValue));

        if(!this.amIActive)
            this.lbl_timer.setOpacity(.5);
        else
            this.lbl_timer.setOpacity(1);

        this.timer = new Timeline (
            new KeyFrame(Duration.millis(1000), (e) -> {

                this.timerValue--;
                this.lbl_timer.setText(String.valueOf(this.timerValue));

                if(this.timerValue == 0 || this.isNewTurn()) {
                    this.endTurn();
                }
            })
        );

        System.out.println("Timer setting to: " + this.timerValue);
        this.timer.setCycleCount(this.timerValue);
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
        this.moveCounter++;
        this.moves.add(m);
        this.updateMoveStats();

        if(this.moveCounter == this.turn.getTurn()) {
            this.endTurn();
        }

        System.out.println(Arrays.toString(this.moves.toArray()));
    }

    private void updateMoveStats(){
        if(this.amIActive) {
            this.lbl_movesDone.setText(String.valueOf(this.moveCounter));
            this.lbl_movesRequired.setText(String.valueOf(this.turn.getTurn()));
        } else {
            this.lbl_movesDone.setText("-");
            this.lbl_movesRequired.setText("-");
        }
    }

    public void startTurn() {
        this.moveCounter = 0;
        this.turn = this.app.getGameTurn();
        this.amIActive = this.app.amIActive();

        System.out.println("------------   starting turn: " + this.turn.getTurn());

        Platform.runLater(() -> {
            this.updateMoveStats();
            this.initTimer();
            this.initButtons();
            this.playTurnTask();
        });
    }

    private void initButtons() {
        this.btn_giveUp.setDisable(!this.amIActive);
    }

    private void proceedTurnStart(){
        if(this.amIActive) this.enableBoard();
        this.timer.play();
    }

    public void endTurn(){
        this.disableBoard();
        this.timer.stop();
        this.app.storeProgress(this.moves);
        this.moves.clear();

        Platform.runLater(() -> Application.awaitAtGuiBarrier("GUI releases. Turn ends."));
    }

    private boolean isNewTurn(){
        return this.app.getGameTurn().getTurn() != this.turn.getTurn();
    }

    public void stopGame() {
        Platform.runLater(() -> this.endTurn());
    }

    private void enableBoard(){
        this.gp_playground.setDisable(false);
    }

    private void disableBoard(){
        this.gp_playground.setDisable(true);
        BoardFieldController.stopActivity();
    }

    private void playTurnTask(){
        GameMove[] progress = this.turn.getMoves();

        if(progress == null) {
            if(this.amIActive)
                this.proceedTurnStart();
            else this.endTurn();
            return;
        }

        Timeline timeline = new Timeline(
            new KeyFrame(Duration.millis(ViewConfig.TIMER_TURN_INTRO_MOVE_DURATION), new EventHandler<ActionEvent>() {
                int i = 0;
                @Override
                public void handle(ActionEvent event) {
                    Platform.runLater(() -> {

                        if(i  >= progress.length)
                            return;

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
                    (ActionEvent) -> {
                        if(this.amIActive)
                            this.proceedTurnStart();
                        else this.endTurn();
                    }
            )).play()
        );

        timeline.play();
    }
}
