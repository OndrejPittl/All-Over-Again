package cz.kiv.ups.controllers;

import cz.kiv.ups.config.Routes;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MessageController extends ScreenController {

	@FXML
	private ImageView iv_loading;
	
	@FXML
	private Label lbl_message;

	protected void init(){
		Image image = new Image(Routes.getImagesDir() + Routes.IMG_LOADER);
		this.iv_loading.setImage(image);
//		System.out.println("after init.................");
    }

	public void setMessage(String msg){
		this.lbl_message.setText(msg);
	}
	
}
