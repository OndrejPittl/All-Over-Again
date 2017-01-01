package controllers;

import config.Routes;
import config.ViewConfig;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;


public class PlayerController implements Initializable {


    @FXML
    private Label lbl_name;

    @FXML
    private Label lbl_status;

    @FXML
    private ImageView iv_activePlayerIcon;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Image image = new Image(Routes.getImagesDir() + ViewConfig.LAYOUT_IMAGE_ACTIVE_PLAYER);
        this.iv_activePlayerIcon.setImage(image);
    }

    public void setData(String name){
        this.lbl_name.setText(name);
        this.lbl_status.setText(ViewConfig.MSG_STATUS_ONLINE);
    }


}
