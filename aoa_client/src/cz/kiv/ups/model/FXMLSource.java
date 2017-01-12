package cz.kiv.ups.model;

import javafx.fxml.Initializable;
import javafx.scene.Parent;

/**
 * Created by OndrejPittl on 01.01.17.
 */
public class FXMLSource {

    Parent root;

    Initializable controller;

    public FXMLSource(Parent root, Initializable controller) {
        this.root = root;
        this.controller = controller;
    }

    public Parent getRoot() {
        return root;
    }

    public void setRoot(Parent root) {
        this.root = root;
    }

    public Initializable getController() {
        return controller;
    }

    public void setController(Initializable controller) {
        this.controller = controller;
    }
}
