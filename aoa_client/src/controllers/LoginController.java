package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import application.Application;
import application.Main;
import application.Screen;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Player;
import validation.UserInputValidator;

public class LoginController implements Initializable {

	private Stage window;
	
	private Screen screen;
	
	private Application app;
	
	@FXML
	private TextField tf_username;
	
	@FXML
	private Button btn_login;
		
	
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	public void setApp(Screen screen){
        this.screen = screen;
        this.app = Application.getInstance();
    }

	public void handleLogin(){
		String nick = tf_username.getText();
		
		if(UserInputValidator.validateUsername(nick)) {
			this.app.registerPlayer(new Player(nick));
			//Application.awaitAtGuiBarrier();
			Application.awaitAtClientBarrier("GUI, relseases. Username entered. (G2R)");
			this.screen.runChecking();
		}
	}

	
	
	
	
	
}
