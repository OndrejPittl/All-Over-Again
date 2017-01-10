package controllers;

import application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Error;
import model.Player;
import validation.UserInputValidator;

public class LoginController extends ScreenController {

	@FXML
	private TextField tf_username;
	
	@FXML
	private Button btn_login;

	@FXML
	private Label lbl_err_username;


	@Override
	protected void init() {

	}

	public void handleLogin(){
		String nick = tf_username.getText();
		
		if(UserInputValidator.validateUsername(nick)) {
			this.app.signIn(new Player(nick));
			Application.awaitAtGuiBarrier("GUI relseases. Username entered. (5GRG)");
		}
	}

	@Override
	protected void registerErrorLabels(){
		this.registerErrorLabel(Error.USERNAME_TAKEN, this.lbl_err_username);
	}

	
	
	
	
	
}
