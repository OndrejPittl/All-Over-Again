package cz.kiv.ups.controllers;

import cz.kiv.ups.config.Routes;
import cz.kiv.ups.config.ViewConfig;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import cz.kiv.ups.model.Player;


public class PlayerController extends ScreenController {

    private static boolean tmpActive = true;

    @FXML
    private Label lbl_name;

    @FXML
    private Label lbl_status;

    @FXML
    private ImageView iv_activePlayerIcon;

    protected void init(){
        Image image = new Image(Routes.getImagesDir() + Routes.IMG_ACTIVE_PLAYER);
        this.iv_activePlayerIcon.setImage(image);
    }

    public void setData(Player p){
        this.iv_activePlayerIcon.setVisible(tmpActive);
        // this.iv_activePlayerIcon.setVisible(p.isActive());

        this.lbl_name.setText(p.getName());
        this.lbl_status.setText(ViewConfig.MSG_STATUS_ONLINE);

        this.tmpActive = false;
    }


}
