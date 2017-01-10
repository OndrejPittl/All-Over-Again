package controllers;


import application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ResultsController extends ScreenController {

    @FXML
    private Label lbl_result;

    @FXML
    private Label lbl_turns;

    @FXML
    private Button btn_again;

    @FXML
    private Button btn_leave;

    @FXML
    private Button btn_exit;


    @Override
    protected void init() {}

    @Override
    protected void registerErrorLabels(){

    }

    public void update(){
        this.lbl_result.setText(this.app.getWinnerText());
        this.lbl_turns.setText(String.valueOf(this.app.getTurn()));
    }

    public void playAgain(){
        // automatically new turn in the same room
        this.makeChoice();
    }

    public void leaveGame(){
        // leave a room, stay signed in
        this.app.disconnectRoom();
        this.makeChoice();
    }

    public void exitGame(){
        // exit a game to OS
        this.app.setExitingGame(true);
        this.makeChoice();
    }

    private void makeChoice(){
        Application.awaitAtGuiBarrier("GUI releases after user interaction.");
    }

}
