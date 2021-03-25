package com.cs360.chess.ui;

import com.cs360.chess.Board;
import com.cs360.chess.Game;
import com.cs360.chess.IconMap;
import com.cs360.chess.piece.Piece;
import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChessBoardView extends Scene {

    private Game currentGame;
    static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private long throttle = 0;

    //FX stuff
    private GridPane tileGrid;
    private GridPane clickGrid;
    private GridPane pieceGrid;
    private StackPane boardStack;

    //private final TileView[][] tiles =new TileView[8][8];
    private BorderPane borderPane;

    private MenuBar menuBar = new MenuBar();
    private Menu game = new Menu("Game");
    private MenuItem newGame = new MenuItem("New Game");
    private MenuItem restart = new MenuItem("Restart");
    private MenuItem save = new MenuItem("Save");
    private MenuItem quit = new MenuItem("Quit");
    private MenuItem exit = new MenuItem("Exit");

    private final Menu undo = new Menu("");
    //We use bindings to make the rectangles automatically adjust to window size changes.
    //Since the board must be square, we must use a conditional binding.
    //If the width is larger than the height, the height property divided by 8 is used as the length of each side of the square.
    //If the height is larger than the width, the width property divided by 8 is used as the length of each side of the square.
    private ReadOnlyDoubleProperty widthProp;
    private DoubleBinding heightProp;
    private DoubleBinding size;

    public ChessBoardView(GameMenu gameMenu) {
        this(gameMenu, new Game());
    }

    public ChessBoardView(GameMenu gameMenu, Game currentGame) {
        super(new BorderPane());
        SvgImageLoaderFactory.install(); //Required so we can load SVG images in the program.
        IconMap.loadIcons(); //Loading our icons

        borderPane = (BorderPane) getRoot();
        this.currentGame = currentGame;

        //Tile grid holding the colored tiles of the board.
        tileGrid = new GridPane();
        //Piece grid holding the pieces
        pieceGrid = new GridPane();
        //Click grid where the user interacts with the board and where tiles are highlighted from
        clickGrid = new GridPane();
        //Stack pane which compiles each of the grids together in one object.
        boardStack = new StackPane();

        //Adding the menubar to the borderpane.
        game.getItems().addAll(save, restart, quit, exit);

        Label dummyUndo = new Label("Undo");
        dummyUndo.setOnMouseClicked(e -> {
            if (System.currentTimeMillis() - throttle < 1000) return;
            Board old = currentGame.undo();
            if (old == null) return;
            currentGame.undo();
            updateAnimateBoard(old, currentGame.getCurrentBoard(), true);
        });

        undo.setGraphic(dummyUndo);
        menuBar.getMenus().addAll(game);
        menuBar.getMenus().addAll(undo);

        borderPane.setTop(menuBar);

        //General alignment/placement of the grids
        tileGrid.setAlignment(Pos.CENTER);
        pieceGrid.setAlignment(Pos.CENTER);
        clickGrid.setAlignment(Pos.CENTER);
        boardStack.getChildren().add(tileGrid);
        boardStack.getChildren().add(pieceGrid);
        boardStack.getChildren().add(clickGrid);
        borderPane.setCenter(boardStack);

        widthProp = borderPane.widthProperty();
        heightProp = borderPane.heightProperty().subtract(menuBar.heightProperty());
        size = (DoubleBinding) Bindings.when(widthProp.greaterThan(heightProp)).then(heightProp.divide(8)).otherwise(widthProp.divide(8));

        //Action handling for menubar options
        save.setOnAction(actionEvent -> {
            FileChooser chooseLocation = new FileChooser();
            chooseLocation.setTitle("Save this chess game");
            chooseLocation.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("A chess game", "*.chess"));
            File saveLocation = chooseLocation.showOpenDialog(this.getWindow());
            try {
                FileOutputStream fos = new FileOutputStream(saveLocation);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(currentGame);
                oos.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            gameMenu.getStage().setScene(gameMenu.titleBorderPane.getScene());
        });

        restart.setOnAction(actionEvent -> {
            //TODO add board reset
        });

        quit.setOnAction(actionEvent -> {
            gameMenu.getStage().setScene(gameMenu.titleBorderPane.getScene());
        });

        exit.setOnAction(actionEvent -> {
            //Exit the game
            executor.shutdownNow();
            System.exit(0);
        });

        //Generating all the tiles on the tile grid.
        for (int column = 0; column < 8; column++) {
            for (int row = 0; row < 8; row++) {

                //We create a rectangle which is to be filled with the correct color to create a checkered pattern.
//                TileView tile = new TileView(column,row);
                Rectangle tile = new Rectangle();
                tile.setFill(((column + row) % 2 == 0) ? Color.TAN : Color.MAROON);

                //Setting the bindings and adding the tile
                tile.widthProperty().bind(size);
                tile.heightProperty().bind(size);
                tileGrid.add(tile, column, row);
                //tiles[column][row] = tile;

                ClickableTile clickableTile = new ClickableTile(column, row);
                clickableTile.setOpacity(0);
                clickableTile.addEventHandler(MouseEvent.MOUSE_CLICKED, tileClickEvent);
                clickableTile.widthProperty().bind(size);
                clickableTile.heightProperty().bind(size);
                clickGrid.add(clickableTile, column, row);
            }
        }

        //Update the board with the current board piece locations.
        updateBoard();
    }

    private void updateBoard() {
        pieceGrid.getChildren().clear();
        Board board = currentGame.getCurrentBoard();
        for(int column=0;column<8;column++){
            for(int row=0;row<8;row++){

                //We iterate through the board and find spaces where a piece exists.
                if(board.isPieceAt(column, row)){

                    //We get the SVG icon of the given piece and load it in an ImageView node.
                    ImageView img = new ImageView(IconMap.getIcon(board.getPieceAt(column, row))/*, tempBoard[column][row], column, row*/);

                    //Setting the bindings and adding the tile
                    img.fitWidthProperty().bind(size);
                    img.fitHeightProperty().bind(size);
                    pieceGrid.add(img, column, row);
                } else {
                    //Fill the spot with an empty and invisible tile if there is no piece in it.
//                    TileView clickableTile = new TileView(column, row);
                    Rectangle emptySpot = new Rectangle();
                    emptySpot.setOpacity(0);
                    emptySpot.widthProperty().bind(size);
                    emptySpot.heightProperty().bind(size);
                    pieceGrid.add(emptySpot, column, row);
                }
            }
        }
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    private void updateAnimateBoard(Board oldBoard, Board newBoard, boolean fillMissing) {
        for (int[] change : newBoard.getChangedPieceCoordinates(oldBoard)) {
            if (change == null || (change[0] == change[2] && change[1] == change[3])) continue;

            Node to = getCell(pieceGrid, change[2], change[3]);
            double x = to.getLayoutX();
            double y = to.getLayoutY();
            if (to instanceof ImageView) { //Theres another piece in the spot we want
                TranslateTransition translate = new TranslateTransition(Duration.millis(300));
                translate.setInterpolator(Interpolator.EASE_OUT);
                translate.setNode(to);
                translate.setByX((oldBoard.getPieceAt(change[2], change[3]).isBlack() ? -1 : 1) * 1000);
                translate.playFromStart();
                translate.setOnFinished(e -> pieceGrid.getChildren().remove(to));
            }

            Node from = getCell(pieceGrid, change[0], change[1]);//From should never be null
            TranslateTransition translate = new TranslateTransition(Duration.millis(300));
            translate.setInterpolator(Interpolator.EASE_OUT);
            translate.setNode(from);
            translate.setToX(x - from.getLayoutX());
            translate.setToY(y - from.getLayoutY());

            translate.setOnFinished(e -> {
                pieceGrid.getChildren().remove(to);
                pieceGrid.getChildren().remove(from);
                pieceGrid.layout();
                Rectangle emptySpot = new Rectangle();
                emptySpot.setOpacity(0);
                emptySpot.widthProperty().bind(size);
                emptySpot.heightProperty().bind(size);
                pieceGrid.add(emptySpot, change[0], change[1]);
                from.setTranslateX(0);
                from.setTranslateY(0);
                pieceGrid.add(from, change[2], change[3]);
                if (from instanceof ImageView) ((ImageView) from).setImage(IconMap.getIcon(newBoard.getPieceAt(change[2], change[3])));
            });

            translate.playFromStart();
        }

        if (fillMissing) {
            for (Piece newPiece : newBoard.getPieces()) {
                if (newPiece != null) {
                    boolean existsInOld = false;
                    for (Piece oldPiece : oldBoard.getPieces()) {
                        if (oldPiece != null) {
                            if (oldPiece.getUniqueId() == newPiece.getUniqueId()) {
                                existsInOld = true;
                                break; //The piece exists in both boards
                            }
                        }
                    }
                    if (!existsInOld) {
                        ImageView img = new ImageView(IconMap.getIcon(newPiece));
                        //Setting the bindings and adding the tile
                        img.fitWidthProperty().bind(size);
                        img.fitHeightProperty().bind(size);
                        img.setOpacity(0);
                        pieceGrid.add(img, newPiece.getColumn(), newPiece.getRow());
                        FadeTransition fadeIn = new FadeTransition(Duration.millis(200));
                        fadeIn.setNode(img);
                        fadeIn.setFromValue(0);
                        fadeIn.setToValue(1);
                        fadeIn.play();

                    }
                }
            }
        }

    }

    //event Handlers
    EventHandler<MouseEvent> tileClickEvent = event -> {
        if (!currentGame.getCurrentBoard().isWhiteToMove()) return;
        //remove everything to do with the last set of moves, wipe all markers
        clickGrid.getChildren().stream().filter(cell -> cell.getOpacity() == 1).forEach(cell -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200));
            fadeOut.setNode(cell);
            fadeOut.setToValue(0);
            fadeOut.play();
        });
        int[] location = currentGame.getSelectedLocation(); //Possible current selected location

        int column = ((ClickableTile) event.getSource()).getColumn();
        int row = ((ClickableTile) event.getSource()).getRow();

        Node selection = getCell(pieceGrid, column, row);

        if (selection instanceof ImageView && !currentGame.getCurrentBoard().getPieceAt(column, row).isBlack()) {
            ScaleTransition scale = new ScaleTransition(Duration.millis(100));
            scale.setNode(selection);
            scale.setToX(1.1f);
            scale.setToY(1.1f);
            scale.setCycleCount(1);
            scale.setAutoReverse(true);
            scale.play();
            currentGame.setSelectedLocation(new int[]{column, row});
            for(int[] coord : currentGame.getValidSpotsFrom(currentGame.getSelectedLocation()[0], currentGame.getSelectedLocation()[1])) {
                Node cell = getCell(clickGrid, coord[0], coord[1]);
                ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200));
                scaleDown.setNode(cell);
                scaleDown.setFromX(1.2);
                scaleDown.setFromY(1.2);
                scaleDown.setToX(1);
                scaleDown.setToY(1);
                scaleDown.setCycleCount(1);
                scaleDown.setAutoReverse(true);
                scaleDown.play();
                FadeTransition fadeIn = new FadeTransition(Duration.millis(200));
                fadeIn.setNode(cell);
                fadeIn.setToValue(1);
                fadeIn.play();
            }
        }
        
        if (!(selection instanceof ImageView) || currentGame.getCurrentBoard().getPieceAt(column, row).isBlack()) {
            if (location == null) return;
            if (isValidSpot(currentGame.getValidSpotsFrom(location[0], location[1]), column, row) && (currentGame.getCurrentBoard().isWhiteToMove())) {
                Board playerCopy = new Board(currentGame.getCurrentBoard());
                currentGame.movePiece(location[0], location[1], column, row);
                updateAnimateBoard(playerCopy, currentGame.getCurrentBoard(), false);
                currentGame.setSelectedLocation(null);
                Board aiCopy = new Board(currentGame.getCurrentBoard());
                Task<Board> task = new Task<>() {
                    @Override
                    protected Board call() {
                        currentGame.aiTurn();
                        return currentGame.getCurrentBoard();
                    }
                };
                task.setOnSucceeded(e -> updateAnimateBoard(aiCopy, currentGame.getCurrentBoard(), false));
                executor.schedule(task, 450, TimeUnit.MILLISECONDS);
            }
            currentGame.setSelectedLocation(null);
        }
        
        if (location != null && (location[0] != column || location[1] != row)) {
            Node old = getCell(pieceGrid, location[0], location[1]);
            if (old != null) {
                ScaleTransition scaleDown = new ScaleTransition(Duration.millis(100));
                scaleDown.setNode(old);
                scaleDown.setToX(1);
                scaleDown.setToY(1);
                scaleDown.setCycleCount(1);
                scaleDown.setAutoReverse(true);
                scaleDown.play();
            }
        }
    };

    private static Node getCell(GridPane gridPane, int column, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == column && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

    private static boolean isValidSpot(int[][] possibleMoves, int column, int row) {
        for (int[] location : possibleMoves) {
            if (location[0] == column && location[1] == row) return true;
        }
        return false;
    }

}
