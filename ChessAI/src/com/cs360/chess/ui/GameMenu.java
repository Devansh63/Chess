package com.cs360.chess.ui;

import com.cs360.chess.Game;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class GameMenu extends Application {
    //Main container
    BorderPane titleBorderPane;
    //Inner container
    private GridPane centerGrid;
    //For alignment
    private StackPane titleStack;
    //Buttons

    private Stage stage;
    private Button twoPlayerButton;
    private Button loadButton;
    private Button exitButton;
    private Button easyButton;
    private Button normalButton;
    private Button hardButton;
    private Button masterButton;
    //Game title
    private Label titleLabel;
    public static void main(String[] args) {
        launch(args);
    }
    //Constructors for h-boxes
    public HBox addHBox1() {
        HBox hbox = new HBox();
        hbox.setSpacing(10);
        titleLabel = new Label("Chess");
        titleLabel.getStyleClass().add("titleLabel");
        hbox.getChildren().addAll(titleLabel);
        hbox.setAlignment(Pos.CENTER);
        return hbox;
    }
    /*public HBox addHBox2(){
        HBox hbox = new HBox();
        hbox.setSpacing(10);
        twoPlayerButton = new Button("Play 2 Player Game");
        twoPlayerButton.getStyleClass().add("titleButton");
        twoPlayerButton.setSkin(new HoverSkin(twoPlayerButton));
        hbox.getChildren().addAll(twoPlayerButton);
        hbox.setAlignment(Pos.CENTER);
        return hbox;
    }*/

    public HBox addHBox3(){
        HBox hbox = new HBox();
        hbox.setSpacing(10);
        easyButton = new Button("Play Easy Game");
        easyButton.getStyleClass().add("titleButton");
        easyButton.setPrefWidth(350);
        easyButton.setSkin(new HoverSkin(easyButton));
        normalButton = new Button("Play Normal Game");
        normalButton.getStyleClass().add("titleButton");
        normalButton.setPrefWidth(350);
        normalButton.setSkin(new HoverSkin(normalButton));
        hbox.getChildren().addAll(easyButton, normalButton);
        hbox.setAlignment(Pos.CENTER);
        return hbox;
    }

    public HBox addHBox4(){
        HBox hbox = new HBox();
        hbox.setSpacing(10);
        hardButton = new Button("Play Hard Game");
        hardButton.getStyleClass().add("titleButton");
        hardButton.setPrefWidth(350);
        hardButton.setSkin(new HoverSkin(hardButton));
        masterButton = new Button("Player Master Game");
        masterButton.getStyleClass().add("titleButton");
        masterButton.setPrefWidth(350);
        masterButton.setSkin(new HoverSkin(masterButton));
        hbox.getChildren().addAll(hardButton, masterButton);
        hbox.setAlignment(Pos.CENTER);
        return hbox;
    }

    public HBox addHBox5() {
        HBox hbox = new HBox();
        hbox.setSpacing(10);
        loadButton = new Button("Load Game");
        loadButton.getStyleClass().add("titleButton");
        loadButton.setPrefWidth(200);
        loadButton.setSkin(new HoverSkin(loadButton));
        exitButton = new Button("Exit Game");
        exitButton.getStyleClass().add("titleButton");
        exitButton.setPrefWidth(200);
        exitButton.setSkin(new HoverSkin(exitButton));
        hbox.getChildren().addAll(loadButton, exitButton);
        hbox.setAlignment(Pos.CENTER);
        return hbox;
    }
    //Constructor for v-box
    public VBox addVBox(){
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.getChildren().addAll(addHBox1(), /*addHBox2(),*/ addHBox3(), addHBox4(), addHBox5());
        vbox.setAlignment(Pos.CENTER);
        return vbox;
    }
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        //Initializing alignment elements, setting title
        titleBorderPane = new BorderPane();
        titleStack = new StackPane();
        titleStack.setId("pane");
        stage.setHeight(800);
        stage.setWidth(800);
        stage.setResizable(false);
        Scene titleScene = new Scene(titleBorderPane);
        stage.setTitle("Chess");
        //Applying the background image and other scene styling
        titleScene.getStylesheets().add(this.getClass().getResource("/StyleSheet.css").toExternalForm());
        //Adding elements to inner pane
        centerGrid = new GridPane();
        centerGrid.add(addVBox(), 0, 1);
        centerGrid.setAlignment(Pos.CENTER);
        //Adding the inner pane to the stack
        titleStack.getChildren().add(centerGrid);
        //Adding the stack to the main pane
        titleBorderPane.setCenter(titleStack);

        stage.setOnCloseRequest(e -> ChessBoardView.executor.shutdownNow());

        /*//Event handling for buttons, can be refactored
        twoPlayerButton.setOnAction(actionEvent -> {
            //TODO merge GUIs to allow scene selection/game starting
            //Initiate a new game and switch scenes, no AI usage
        });*/
        loadButton.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select a game to load");
            fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("A saved chess game", "*.chess"));
            File selected = fileChooser.showOpenDialog(stage);

            if (selected != null) {
                try {
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(selected));
                    Game game = (Game)ois.readObject();
                    ChessBoardView chessGame = new ChessBoardView(this, game);
                    stage.setScene(chessGame);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

        });
        exitButton.setOnAction(actionEvent -> System.exit(0));


        easyButton.setOnAction(actionEvent -> {
            ChessBoardView chessGame = new ChessBoardView(this);
            stage.setScene(chessGame);
            chessGame.getCurrentGame().setDepth(2);
        });

        normalButton.setOnAction(actionEvent -> {
            ChessBoardView chessGame = new ChessBoardView(this);
            stage.setScene(chessGame);
            chessGame.getCurrentGame().setDepth(3);
        });

        hardButton.setOnAction(actionEvent -> {
            ChessBoardView chessGame = new ChessBoardView(this);
            stage.setScene(chessGame);
            chessGame.getCurrentGame().setDepth(4);
        });

        masterButton.setOnAction(actionEvent -> {
            ChessBoardView chessGame = new ChessBoardView(this);
            stage.setScene(chessGame);
            chessGame.getCurrentGame().setDepth(5);
        });

        stage.setScene(titleScene);
        stage.show();
    }

    public Stage getStage() {
        return stage;
    }
}
//