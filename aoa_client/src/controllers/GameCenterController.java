package controllers;

import application.Application;
import game.GameDifficulty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.Room;
import model.ViewRoom;

public class GameCenterController extends ScreenController {

    private ObservableList<ViewRoom> rooms = FXCollections.observableArrayList();

	
	@FXML
	private ComboBox<String> cb_players;
	
	@FXML
	private ComboBox<String> cb_difficulty;
	
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
	
	


	protected void init(){
		this.initJoinGameSection();
		this.initNewGameSection();
	}

	private void initNewGameSection(){
        cb_players.getItems().addAll("Singleplayer", "Multiplayer");
        cb_difficulty.getItems().addAll("Easy", "Medium", "Expert");
        this.cb_players.getSelectionModel().select(0);
        this.cb_difficulty.getSelectionModel().select(0);
    }

	private void initJoinGameSection(){
        nicknamesColumn.setCellValueFactory(cellData -> cellData.getValue().getViewNicknames());
        playersColumn.setCellValueFactory(cellData -> cellData.getValue().getViewPlayers());
        difficultyColumn.setCellValueFactory(cellData -> cellData.getValue().getViewDifficulty());

//		nicknamesColumn.prefWidthProperty().bind(tv_rooms.widthProperty().multiply(0.5));
//		playersColumn.prefWidthProperty().bind(tv_rooms.widthProperty().multiply(0.25));
//		difficultyColumn.prefWidthProperty().bind(tv_rooms.widthProperty().multiply(0.25));

        tv_rooms.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
        nicknamesColumn.setMaxWidth(1f * Integer.MAX_VALUE * 60);
        playersColumn.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        difficultyColumn.setMaxWidth(1f * Integer.MAX_VALUE * 20);

        this.tv_rooms.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            this.btn_joinGame.setDisable(newSelection == null);
        });
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
        this.app.updateRoomList();
        this.updateRoomList();
	}

    public void handleNewGame(){
	    int playerLimitIndex = this.cb_players.getSelectionModel().getSelectedIndex(),
            diffIndex = this.cb_difficulty.getSelectionModel().getSelectedIndex();

        this.handleSelection(new Room(playerLimitIndex, GameDifficulty.getNth(diffIndex)));
    }

    public void handleJoinGame(){
        ViewRoom r = tv_rooms.getSelectionModel().getSelectedItem();
        this.handleSelection(r.getRoom());
    }

    private void handleSelection(Room r){
        this.app.selectRoom(r);
        Application.awaitAtGuiBarrier("GUI releases. Room being selected. (11GRG)");
    }
}
