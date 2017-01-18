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
    private Label lbl_you;

    @FXML
    private ImageView iv_activePlayerIcon;

    protected void init(){
        Image image = new Image(Routes.getImagesDir() + Routes.IMG_ACTIVE_PLAYER);
        this.iv_activePlayerIcon.setImage(image);
    }

    public void update(Player p, int currentPlayerID){

        // player name
        this.lbl_name.setText(p.getName());

        // player status
        if (p.isOnline()) {
                this.lbl_status.setText(ViewConfig.MSG_STATUS_ONLINE);
                this.lbl_status.setTextFill(ViewConfig.CORRECT);
            } else {
                this.lbl_status.setText(ViewConfig.MSG_STATUS_OFFLINE);
                this.lbl_status.setTextFill(ViewConfig.INCORRECT);
            }

        // you flag
        this.lbl_you.setVisible(currentPlayerID == p.getID());

        // active player flag
        this.iv_activePlayerIcon.setVisible(p.isActive());
    }
}
