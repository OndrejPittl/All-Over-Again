package cz.kiv.ups.model;

import javafx.fxml.Initializable;
import javafx.scene.Parent;

public class FXMLSource {

    private Parent root;

    private Initializable controller;

    public FXMLSource(Parent root, Initializable controller) {
        this.root = root;
        this.controller = controller;
    }

    public Parent getRoot() {
        return root;
    }

    public Initializable getController() {
        return controller;
    }

    public void setController(Initializable controller) {
        this.controller = controller;
    }
}
