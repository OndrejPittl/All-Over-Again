package cz.kiv.ups.controllers;

import cz.kiv.ups.application.Application;
import cz.kiv.ups.application.Logger;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import cz.kiv.ups.model.Error;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;


public class ScreenController implements Initializable {

    private static Logger logger = Logger.getLogger();


    protected Application app;

    protected ArrayList<Error> errors;

    protected Map<Error, Label> errorLabels;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.errorLabels = new HashMap<>();
        this.init();
    }

    protected void init(){}

    public void setApp(Application app) {
        this.app = app;
        this.handleErrors();
    }

    private void updateErrors(){
        this.errors = this.app.getErrors();
    }

    private boolean hasErrors() {
        return errors.size() > 0;
    }

    private void handleErrors(){
        this.updateErrors();
        this.registerErrorLabels();

        if(!this.hasErrors() || !this.hasErrorLabels())
            return;

        for (Error err : this.errors) {
            Label lbl = this.errorLabels.get(err);
            lbl.setText(err.getErr());
            lbl.setVisible(true);
        }

        this.app.clearErrors();
    }

    private boolean hasErrorLabels(){
        return this.errorLabels.size() > 0;
    }

    protected void registerErrorLabels(){}

    protected void registerErrorLabel(Error err, Label lbl){
        this.errorLabels.put(err, lbl);
    }
}
