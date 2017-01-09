package controllers;


import config.GameConfig;
import config.Routes;
import config.ViewConfig;
import game.GameColor;
import game.GameDifficulty;
import game.GameMove;
import game.GameSymbol;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.Timer;
import java.util.TimerTask;

public class BoardFieldController extends ScreenController {

    private static final double ACTIVE_SPOT_SIZE = 1.3;

    private static final double PASSIVE_SPOT_SIZE = 0.9;

    private static final int COLOR_COUNT = GameColor.count();

    private static final int SYMBOL_COUNT = GameSymbol.count();

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

    private static BoardFieldController activeField = null;


    private GameDifficulty difficulty;


    private boolean isActive;

    private int index;

    private GameColor color;

    private GameSymbol symbol;




    private PlaygroundController playgroundController;



    @FXML
    private BorderPane comp_boardFieldWrapper;

    @FXML
    private BorderPane comp_boardField;

    @FXML
    private Pane p_dump;

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



    protected void init(){
        this.initLayout();
        this.bindEvents();
    }

    private void initLayout(){
        this.resetField();

        Image arr = new Image(Routes.getImagesDir() + Routes.IMG_ARROW);
        this.iv_arrow1.setImage(arr);
        this.iv_arrow2.setImage(arr);
        this.iv_arrow3.setImage(arr);
        this.iv_arrow4.setImage(arr);

        this.iv_symbol.fitWidthProperty().bind(comp_boardField.widthProperty().divide(2));
    }

    private void updateBars(){
        // umyslne bez breaku
        switch (this.difficulty) {
            case EXPERT:
                if(this.isActive) this.initSymbolBar();
                this.vb_symbolPane.setVisible(isActive);
            case NORMAL:
                if(this.isActive) this.initColorBar();
                this.hb_colorPane.setVisible(isActive);
            default:
            case EASY:
                break;
        }
    }

    private void bindEvents(){
        this.isActive = true;

        this.comp_boardField.setOnMouseClicked((e) -> this.comp_boardField.requestFocus());

        this.comp_boardField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldState, Boolean newState) {
                setFieldActivity(newState);
            }
        });

        this.comp_boardField.setOnKeyPressed((e) -> {
            switch(e.getCode()) {
                case UP:
                    if(!this.difficulty.isGreaterEqualThan(GameDifficulty.EXPERT))
                        break;
                    this.decreaseSymbolPosition();
                    this.updateSymbol();
                    this.updateBars();
                    break;
                case DOWN:
                    if(!this.difficulty.isGreaterEqualThan(GameDifficulty.EXPERT))
                        break;
                    this.increaseSymbolPosition();
                    this.updateSymbol();
                    this.updateBars();
                    break;
                case LEFT:
                    if(!this.difficulty.isGreaterEqualThan(GameDifficulty.NORMAL))
                        break;
                    this.decreaseColorPosition();
                    this.updateSymbol();
                    this.updateBars();
                    break;
                case RIGHT:
                    if(!this.difficulty.isGreaterEqualThan(GameDifficulty.NORMAL))
                        break;
                    this.increaseColorPosition();
                    this.updateSymbol();
                    this.updateBars();
                    break;
                case SPACE:
                case ENTER:
                    this.registerMove();
                    this.endMove();
                    break;
                case ESCAPE:
                case DELETE:
                    this.endMove();
                    break;
                default:
                    break;
            }
        });
    }

    private void setFieldActivity(boolean isActive){
        this.isActive = isActive;
        this.storeActiveFieldIndex();
        this.updateBars();
        this.updateSymbol();
    }

    private void storeActiveFieldIndex() {
        if(this.isActive) {
            BoardFieldController.activeField = this;
        } else {
            BoardFieldController.activeField = null;
        }
    }

    private void initColorBar(){
        GraphicsContext g = this.c_colors.getGraphicsContext2D();
        GameColor[] gcs = GameColor.values();

        double  w = this.c_colors.getWidth(),
                h = this.c_colors.getHeight(),
                size = w / COLOR_COUNT * 0.8,
                offset = (COLOR_COUNT * 0.2),
                currentX = offset;  //x = i * size + 2 * i * offset

        this.clearCanvas(g, this.c_colors);

        for(int i = 0; i < COLOR_COUNT; i++) {
            GameColor gc = gcs[i];
            Color c = gc.getColor();

            double currentSize = i == this.color.getIndex() ? size * ACTIVE_SPOT_SIZE : size * PASSIVE_SPOT_SIZE,
                   y = (h - currentSize) / 2;

            g.setFill(c);
            g.fillOval(currentX, y, currentSize, currentSize);

            currentX += currentSize + 2 * offset;
        }
    }

    private void initSymbolBar(){
        GraphicsContext g = this.c_symbols.getGraphicsContext2D();

        double  w = this.c_symbols.getWidth(),
                h = this.c_symbols.getHeight(),
                size = (h / SYMBOL_COUNT * 0.8),
                offset = (SYMBOL_COUNT * 0.2),
                currentY = offset;

        this.clearCanvas(g, this.c_symbols);

        g.setFill(Color.BLACK);

        for(int i = 0; i < SYMBOL_COUNT; i++) {

            double currentSize = i == this.symbol.getIndex() ? size * ACTIVE_SPOT_SIZE : size * PASSIVE_SPOT_SIZE,
                    x = (w - currentSize) / 2;

            g.fillOval(x, currentY, currentSize, currentSize);
            currentY += currentSize + 2 * offset;
        }
    }

    private void updateSymbol(){
        if(this.isActive){
            this.iv_symbol.setImage(GameConfig.getSymbolImage(this.color.getIndex(), this.symbol.getIndex()));
        }

        this.iv_symbol.setVisible(this.isActive);
    }

    private void clearCanvas(GraphicsContext g, Canvas c, Color col){
        g.setFill(col);
        g.fillRect(0, 0, c.getWidth(), c.getHeight());
    }

    private void clearCanvas(GraphicsContext g, Canvas c){
        this.clearCanvas(g, c, Color.WHITE);
    }

    private void increaseColorPosition(){
        this.color = (GameColor) this.color.next();
        System.out.println("selected color: " + this.color.getIndex());
//        this.initColorBar();
    }

    private void decreaseColorPosition(){
        this.color = (GameColor) this.color.previous();
        System.out.println("selected color: " + this.color.getIndex());
//        this.initColorBar();
    }

    private void increaseSymbolPosition(){
        this.symbol = (GameSymbol) this.symbol.next();
        System.out.println("selected symbol: " + this.symbol.getIndex());
//        this.initSymbolBar();
    }

    private void decreaseSymbolPosition(){
        this.symbol = (GameSymbol) this.symbol.previous();
        System.out.println("selected symbol: " + this.symbol.getIndex());
//        this.initSymbolBar();
    }

    public void fitSize(int w) {
        this.comp_boardField.setPrefWidth(w);
        this.comp_boardField.setPrefHeight(w);
        this.comp_boardFieldWrapper.setPrefWidth(w);
        this.comp_boardFieldWrapper.setPrefHeight(w);
    }

    public GameMove serializeMove() {
        switch (this.difficulty) {
            case EXPERT:
                return new GameMove(this.index, this.color, this.symbol);
            case NORMAL:
                return new GameMove(this.index, this.color);
            default:
            case EASY:
                return new GameMove(this.index);
        }
    }

    public void registerMove() {
        this.playgroundController.registerMove(this.serializeMove());
    }

    public void endMove() {
        this.p_dump.requestFocus();
        this.isActive = false;
        this.resetField();
    }


    private void resetField(){
        this.color = GameColor.GOLD;
        this.symbol = GameSymbol.Symbol1;
    }

    public void setPlaygroundController(PlaygroundController playgroundController) {
        this.playgroundController = playgroundController;
    }



    public BorderPane getControlElement() {
        return this.comp_boardField;
    }

    public void setDifficulty(GameDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void displayMove(GameMove m) {
        this.isActive = true;
        this.color = m.getColor();
        this.symbol = m.getSymbol();
        this.setFieldActivity(isActive);

        new Timer().schedule(new TimerTask() {
            public void run() {
                Platform.runLater(() -> {
                    resetField();
                    endMove();
                    setFieldActivity(isActive);
                });

            }
        }, ViewConfig.TIMER_TURN_INTRO_MOVE_DURATION);
    }


    public static void stopActivity(){
        if(isAnyFieldActive()) {
            BoardFieldController.activeField.endMove();
        }
    }

    private static boolean isAnyFieldActive(){
        return BoardFieldController.activeField != null;
    }
}
