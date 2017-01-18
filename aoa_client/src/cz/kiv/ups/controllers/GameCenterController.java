package cz.kiv.ups.controllers;

import cz.kiv.ups.application.Application;
import cz.kiv.ups.config.ViewConfig;
import cz.kiv.ups.game.BoardDimension;
import cz.kiv.ups.game.GameDifficulty;
import cz.kiv.ups.game.GameType;
import cz.kiv.ups.model.Error;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import cz.kiv.ups.model.GameStatus;
import cz.kiv.ups.model.Room;
import cz.kiv.ups.model.ViewRoom;


public class GameCenterController extends ScreenController {

    private ObservableList<ViewRoom> rooms = FXCollections.observableArrayList();

	
	@FXML
	private ComboBox<GameType> cb_players;
	
	@FXML
	private ComboBox<GameDifficulty> cb_difficulty;

	@FXML
	private ComboBox<BoardDimension> cb_dimension;
	
	@FXML
	private Button btn_createGame;

	@FXML
	private Button btn_joinGame;

	@FXML
	private Button btn_gameList;
	
	@FXML
	private TableView<ViewRoom> tv_rooms;
	
	@FXML
	private TableColumn<ViewRoom, String> nicknamesColumn;
	
	@FXML
	private TableColumn<ViewRoom, String> playersColumn;

    @FXML
    private TableColumn<ViewRoom, String> difficultyColumn;

    @FXML
    private TableColumn<ViewRoom, String> dimensionColumn;

    @FXML
    private Label lbl_err_join;

	protected void init(){
		this.initJoinGameSection();
		this.initNewGameSection();
	}

	private void initNewGameSection(){
        this.cb_players.getItems().addAll(GameType.values());
        this.cb_difficulty.getItems().addAll(GameDifficulty.values());
        this.cb_dimension.getItems().addAll(BoardDimension.values());


        this.cb_players.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            this.enableNewGame();
        });

        this.cb_difficulty.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (this.isForbiddenCombination(newSelection, this.cb_dimension.getSelectionModel().getSelectedItem()))
                this.cb_dimension.valueProperty().set(null);
            this.enableNewGame();
        });

        this.cb_dimension.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (this.isForbiddenCombination(this.cb_difficulty.getSelectionModel().getSelectedItem(), newSelection))
                this.cb_difficulty.valueProperty().set(null);
            this.enableNewGame();
        });

    }

    private boolean isForbiddenCombination(GameDifficulty diff, BoardDimension dim){
        return diff == GameDifficulty.EASY && dim == BoardDimension.TINY;
    }

    private void enableNewGame(){
	    boolean gameTypeSelected = this.cb_players.getSelectionModel().getSelectedItem() != null,
                diffSelected = this.cb_difficulty.getSelectionModel().getSelectedItem() != null,
                dimSelected = this.cb_dimension.getSelectionModel().getSelectedItem() != null;

        this.btn_createGame.setDisable(!(gameTypeSelected && diffSelected && dimSelected));
    }

	private void initJoinGameSection(){
        nicknamesColumn.setCellValueFactory(cellData -> cellData.getValue().getViewNicknames());
        playersColumn.setCellValueFactory(cellData -> cellData.getValue().getViewPlayers());
        difficultyColumn.setCellValueFactory(cellData -> cellData.getValue().getViewDifficulty());
        dimensionColumn.setCellValueFactory(cellData -> cellData.getValue().getViewDimension());

//		nicknamesColumn.prefWidthProperty().bind(tv_rooms.widthProperty().multiply(0.5));
//		playersColumn.prefWidthProperty().bind(tv_rooms.widthProperty().multiply(0.25));
//		difficultyColumn.prefWidthProperty().bind(tv_rooms.widthProperty().multiply(0.25));

        tv_rooms.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
        nicknamesColumn.setMaxWidth(1f * Integer.MAX_VALUE * 45);
        playersColumn.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        difficultyColumn.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        dimensionColumn.setMaxWidth(1f * Integer.MAX_VALUE * 25);

        this.tv_rooms.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            this.btn_joinGame.setDisable(newSelection == null);
        });

        this.tv_rooms.setPlaceholder(new Label(ViewConfig.MSG_GAMECENTER_NO_ROOM));
    }

	public void updateRoomList(){
		this.rooms.clear();
		
		for (Room r : this.app.getRooms()) {
			this.rooms.add(new ViewRoom(r));
		}
		
		this.tv_rooms.setItems(this.rooms);

        int itemCount = tv_rooms.getItems().size();
        if(itemCount > 0) {
            this.tv_rooms.getSelectionModel().select(0);
        }

	}

	public void handleRoomListRefresh(){
	    Platform.runLater(() -> {
            this.app.requestRoomListAndWait();
            this.updateRoomList();
        });
	}

    public void handleNewGame(){
	    Application.changeStatus(GameStatus.ROOM_CREATING);

	    GameType type = this.cb_players.getSelectionModel().getSelectedItem();
	    GameDifficulty diff = this.cb_difficulty.getSelectionModel().getSelectedItem();
	    BoardDimension dim = this.cb_dimension.getSelectionModel().getSelectedItem();

        this.handleSelection(new Room(type, diff, dim));
    }

    public void handleJoinGame(){
        Application.changeStatus(GameStatus.ROOM_JOINING);
        ViewRoom r = tv_rooms.getSelectionModel().getSelectedItem();
        this.handleSelection(r.getRoom());
    }

    private void handleSelection(Room r){
        this.app.selectRoom(r);
        Platform.runLater(()->Application.awaitAtGuiBarrier("GUI releases. Room being selected. (11GRG)"));
    }

    @Override
    protected void registerErrorLabels(){
        // The errors cannot come in at once.
        this.registerErrorLabel(Error.GAME_REPLAY_REFUSED, this.lbl_err_join);
        this.registerErrorLabel(Error.ROOM_JOIN_REFUSED, this.lbl_err_join);
    }

}
