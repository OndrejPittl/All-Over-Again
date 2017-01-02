package controllers;


import application.Application;
import application.Screen;
import config.Routes;
import config.ViewConfig;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.GameColors;
import model.GameSymbols;

import java.net.URL;
import java.util.ResourceBundle;

public class BoardFieldController implements Initializable {

    private static final double ACTIVE_SPOT_SIZE = 1.3;

    private static final double PASSIVE_SPOT_SIZE = 0.9;

    private static final int COLOR_COUNT = GameColors.values().length;

    private static final int SYMBOL_COUNT = GameSymbols.values().length;

    private static final Background focusedBackground = new Background(
            new BackgroundFill(Color.web("e8fff9"),
                    CornerRadii.EMPTY,
                    Insets.EMPTY)
    );

    private static final Background unfocusedBackground = new Background(
            new BackgroundFill(Color.WHITE,
                    CornerRadii.EMPTY,
                    Insets.EMPTY)
    );


    private int colorPosition;

    private int symbolPosition;






    private Stage window;

    private Screen screen;

    private Application app;



    @FXML
    private BorderPane comp_boardField;

    @FXML
    private Canvas c_colors;

    @FXML
    private Canvas c_symbols;

    @FXML
    private VBox vb_symbolPane;

    @FXML
    private HBox hb_colorPane;

    @FXML
    private ImageView iv_symbol;

    @FXML
    private ImageView iv_arrow1;

    @FXML
    private ImageView iv_arrow2;

    @FXML
    private ImageView iv_arrow3;

    @FXML
    private ImageView iv_arrow4;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.init();
        this.initColorbar();
        this.initSymbolbar();

        this.bindEvents();
    }

    private void init(){
        this.colorPosition = 1;
        this.symbolPosition = 3;

        //this.comp_boardField.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        Image image = new Image(Routes.getImagesDir() + ViewConfig.LAYOUT_IMAGE_ACTIVE_PLAYER);
        this.iv_symbol.setImage(image);

        Image arr = new Image(Routes.getImagesDir() + ViewConfig.LAYOUT_IMAGE_ARROW);
        this.iv_arrow1.setImage(arr);
        this.iv_arrow2.setImage(arr);
        this.iv_arrow3.setImage(arr);
        this.iv_arrow4.setImage(arr);

        this.iv_symbol.fitWidthProperty().bind(comp_boardField.widthProperty().divide(2));
    }

    private void bindEvents(){
        this.comp_boardField.setOnMouseClicked((e) -> {
            this.comp_boardField.requestFocus();

        });

        this.comp_boardField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldState, Boolean newState) {
                setFieldActivity(newState);
            }
        });

        this.comp_boardField.setOnKeyPressed((e) -> {

            switch(e.getCode()) {
                case UP:
                    this.decreaseSymbolPosition();
                    break;
                case DOWN:
                    this.increaseSymbolPosition();
                    break;
                case LEFT:
                    this.decreaseColorPosition();
                    break;
                case RIGHT:
                    this.increaseColorPosition();
                    break;
                default:
                case ENTER:
                    this.comp_boardField.setDisable(true);
                    this.comp_boardField.setDisable(false);
                    break;
            }

            System.out.println(e.getCode());
        });

    }

    private void setFieldActivity(boolean isActive){
        this.hb_colorPane.setVisible(isActive);
        this.vb_symbolPane.setVisible(isActive);
    }



    private void initColorbar(){
        GraphicsContext g = this.c_colors.getGraphicsContext2D();
        GameColors[] gcs = GameColors.values();

        int activeSpots = 1,
            passiveSpots = COLOR_COUNT - activeSpots;


        double  w = this.c_colors.getWidth(),
                h = this.c_colors.getHeight(),
                size = w / COLOR_COUNT * 0.8,
                offset = (COLOR_COUNT * 0.2),
                currentX = offset;  //x = i * size + 2 * i * offset

        this.clearCanvas(g, this.c_colors);

        for(int i = 0; i < COLOR_COUNT; i++) {
            GameColors gc = gcs[i];
            Color c = gc.getColor();

            double currentSize = i == this.colorPosition ? size * ACTIVE_SPOT_SIZE : size * PASSIVE_SPOT_SIZE,
                   y = (h - currentSize) / 2;

            g.setFill(c);
            g.fillOval(currentX, y, currentSize, currentSize);

            currentX += currentSize + 2 * offset;
        }
    }

    private void initSymbolbar(){
        GraphicsContext g = this.c_symbols.getGraphicsContext2D();

        double  w = this.c_symbols.getWidth(),
                h = this.c_symbols.getHeight(),
                size = (h / SYMBOL_COUNT * 0.8),
                offset = (SYMBOL_COUNT * 0.2),
                currentY = offset;

        this.clearCanvas(g, this.c_symbols);

        g.setFill(Color.BLACK);

        for(int i = 0; i < SYMBOL_COUNT; i++) {

            double currentSize = i == this.symbolPosition ? size * ACTIVE_SPOT_SIZE : size * PASSIVE_SPOT_SIZE,
                    x = (w - currentSize) / 2;

            g.fillOval(x, currentY, currentSize, currentSize);
            currentY += currentSize + 2 * offset;
        }
    }

    private void clearCanvas(GraphicsContext g, Canvas c, Color col){
        g.setFill(col);
        g.fillRect(0, 0, c.getWidth(), c.getHeight());
    }

    private void clearCanvas(GraphicsContext g, Canvas c){
        this.clearCanvas(g, c, Color.WHITE);
    }

    private void increaseColorPosition(){
        this.colorPosition = ++this.colorPosition % COLOR_COUNT;
        this.initColorbar();
    }

    private void decreaseColorPosition(){
//        this.colorPosition = --this.colorPosition % COLOR_COUNT;
        this.colorPosition = --this.colorPosition < 0 ? COLOR_COUNT - 1 : this.colorPosition;
        this.initColorbar();
    }

    private void increaseSymbolPosition(){
        this.symbolPosition = ++this.symbolPosition % SYMBOL_COUNT;
        this.initSymbolbar();
    }

    private void decreaseSymbolPosition(){
//        this.symbolPosition = --this.symbolPosition % SYMBOL_COUNT;
        this.symbolPosition = --this.symbolPosition  < 0 ? SYMBOL_COUNT - 1 : this.symbolPosition ;
        this.initSymbolbar();
    }


    public void setApp(Screen screen, Application app){
        this.screen = screen;
        this.app = app;
    }

    public void setSymbol(){

    }
}
