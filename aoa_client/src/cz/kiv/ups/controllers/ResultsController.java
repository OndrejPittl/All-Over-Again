package cz.kiv.ups.controllers;


import cz.kiv.ups.application.Application;
import cz.kiv.ups.game.GameType;
import cz.kiv.ups.model.Player;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import cz.kiv.ups.model.GameStatus;

public class ResultsController extends ScreenController {

    @FXML
    private Label lbl_result;

    @FXML
    private Label lbl_turns;

    @FXML
    private Button btn_again;

    @Override
    protected void init() {}

    @Override
    protected void registerErrorLabels(){

    }

    public void update(){
        this.lbl_result.setText(this.app.getWinnerText());
        this.lbl_turns.setText(String.valueOf(this.app.getTurn()));

        if(this.app.getSelectedRoom().getType() == GameType.MULTIPLAYER)
            for (Player p : this.app.getSelectedRoom().getPlayers()) {
                if(!p.isOnline()) {
                    this.btn_again.setDisable(true);
                    return;
                }
            }
    }

    public void playAgain(){
        // new turn in the same room
        Application.changeStatus(GameStatus.GAME_RESTART);
        this.makeChoice();
    }

    public void leaveGame(){
        // leave a room, stay signed in
        this.makeChoice();
    }

    public void exitGame(){
        // exit a game to OS
        Application.changeStatus(GameStatus.EXIT_GAME);
        this.makeChoice();
    }

    private void makeChoice(){
        Platform.runLater(()->Application.awaitAtGuiBarrier("GUI releases GUIC with user interaction."));
    }

}
