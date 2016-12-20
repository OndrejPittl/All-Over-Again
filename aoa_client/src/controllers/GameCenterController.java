package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import application.Application;
import application.Screen;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.Room;
import model.ViewRoom;

public class GameCenterController implements Initializable {

	private Screen screen;
	
	private Application app;
	
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
	private TableView<ViewRoom> tv_rooms;
	
	@FXML
	private TableColumn<ViewRoom, String> nicknamesColumn;
	
	@FXML
	private TableColumn<ViewRoom, String> playersColumn;
	
	@FXML
	private TableColumn<ViewRoom, String> difficultyColumn;
	
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
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

	}
	
	public void setApp(Screen screen){
        this.screen = screen;
        this.app = Application.getInstance();
    }
	
	public void updateRoomList(){
		this.rooms.clear();
		
		for (Room r : this.app.getRooms()) {
			this.rooms.add(new ViewRoom(r));
		}
		
		this.tv_rooms.setItems(this.rooms);
	}
	
}
