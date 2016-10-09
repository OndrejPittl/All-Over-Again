package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import view.Screen;

public class LoginController implements Initializable {

	private Stage window;
	
	private Screen screen;
	
	private TextField tf_username;
	
	private Button btn_login;
		
	
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	public void setApp(Screen screen){
        this.screen = screen;
    }

	public void handleLogin(){
		System.out.println("logged in!!!");
	}

	
	
	
	
}
