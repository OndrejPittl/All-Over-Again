package controllers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import application.Screen;
import config.Routes;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MessageController implements Initializable {
	
	@FXML
	private ImageView iv_loading;
	
	@FXML
	private Label lbl_message;
	
	private Screen screen;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	    Image image = new Image(Routes.getImagesDir() + "loading.gif");
	    this.iv_loading.setImage(image);
	}
	
	public void setApp(Screen screen){
        this.screen = screen;
    }
	
	public void setApp(Screen screen, Object[] params){
        this.screen = screen;
    }
	
	public void setMessage(String msg){
		this.lbl_message.setText(msg);
	}
	
}
