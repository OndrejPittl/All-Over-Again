package cz.kiv.ups.controllers;

import cz.kiv.ups.application.Application;
import cz.kiv.ups.application.Logger;
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
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;
import cz.kiv.ups.model.FXMLSource;
import cz.kiv.ups.model.Player;
import cz.kiv.ups.model.Room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;


public class PlaygroundController extends ScreenController {

    private static Logger logger = Logger.getLogger();

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

    @FXML
    private GridPane gp_playground;

    @FXML
    private VBox vb_playerWrapper;



    private int fieldCount;

    private BoardFieldController[] fieldControllers;

    private ArrayList<GameMove> moves;
    private boolean waitAskResult = false;


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

                if(this.timerValue == 0) {
                    logger.debug("* * * * * END TURN: timer runs out");
                    this.endTurn();
                }
            })
        );

        logger.debug("Timer setting to: " + this.timerValue);
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
            controller.update(p, this.room.getCurrentPlayerID());

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
            logger.debug("* * * * * END TURN: move counter");
            this.endTurn();
        }

        logger.debug(Arrays.toString(this.moves.toArray()));
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

        logger.debug("------------   starting turn: " + this.turn.getTurn());

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
            else {
                logger.debug("* * * * * END TURN: not my turn (first turn without progress)");
                this.endTurn();
            }
            return;
        }

        Timeline timeline = new Timeline(
            new KeyFrame(Duration.millis(ViewConfig.TIMER_TURN_INTRO_MOVE_DURATION + 200), new EventHandler<ActionEvent>() {
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
                        else {
                            logger.debug("* * * * * END TURN: not my turn (after turn task)");
                            this.endTurn();
                        }
                    }
            )).play()
        );

        timeline.play();
    }

    public void updatePlayerList() {
        this.initPlayerList();
    }

    public void askPlayerWait() {
        boolean waiting = false;

        ButtonType waitBtn = new ButtonType(ViewConfig.MSG_ASK_YES, ButtonBar.ButtonData.YES);
        ButtonType leaveBtn = new ButtonType(ViewConfig.MSG_ASK_NO, ButtonBar.ButtonData.NO);
        Alert alert = new Alert(Alert.AlertType.WARNING, ViewConfig.MSG_ASK_OPPONENT_LEFT_CONTENT, waitBtn, leaveBtn);

        alert.setTitle(ViewConfig.MSG_ASK_OPPONENT_LEFT_TITLE);
        alert.setHeaderText(ViewConfig.MSG_ASK_OPPONENT_LEFT_HEADER);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == waitBtn) {
            waiting = true;
        }

        this.app.setWaitingAskResult(waiting);
    }

    public boolean isWaitingForPlayer() {
        return waitAskResult;
    }
}
