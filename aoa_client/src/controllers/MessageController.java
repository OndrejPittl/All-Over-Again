package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import application.Application;
import application.Screen;
import config.Routes;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MessageController implements Initializable {

	private Application app;

	@FXML
	private ImageView iv_loading;
	
	@FXML
	private Label lbl_message;
	
	private Screen screen;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	    Image image = new Image(Routes.getImagesDir() + Routes.IMG_LOADER);
	    this.iv_loading.setImage(image);
	}

	public void setApp(Screen screen, Application app){
        this.screen = screen;
        this.app = app;
    }

	public void setMessage(String msg){
		this.lbl_message.setText(msg);
	}
	
}
