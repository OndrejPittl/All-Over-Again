package controllers;

import application.Application;
import config.ErrorConfig;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import model.Error;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;


public class ScreenController implements Initializable {

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

        System.out.println("Handling errors....");

        System.out.println("errors: " + this.errors.size());
        System.out.println("lbls: " + this.errorLabels.size());

        for (Error err : this.errors) {
            Label lbl = this.errorLabels.get(err);
            lbl.setText(err.getErr());
            lbl.setVisible(true);
            System.out.println("Handling Error: " + err.getErr());
        }
    }

    private boolean hasErrorLabels(){
        return this.errorLabels.size() > 0;
    }

    protected void registerErrorLabels(){}

    protected void registerErrorLabel(Error err, Label lbl){
        this.errorLabels.put(err, lbl);
    }
}
