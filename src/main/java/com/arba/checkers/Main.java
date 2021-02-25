package com.arba.checkers;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.arba.checkers.data.Cell;
import com.arba.checkers.data.Piece;
import com.arba.checkers.data.Point;
import com.arba.checkers.data.dict.ColorType;
import com.arba.checkers.data.dict.MoveResult;
import com.arba.checkers.rule.RussianCheckersRules;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import static com.arba.checkers.data.dict.ColorType.DARK;
import static com.arba.checkers.data.dict.ColorType.LIGHT;
import static com.arba.checkers.data.dict.PieceType.KING;
import static com.arba.checkers.data.dict.PieceType.MAN;
import static javafx.scene.control.Alert.AlertType.INFORMATION;
import static javafx.scene.layout.GridPane.getColumnIndex;
import static javafx.scene.layout.GridPane.getRowIndex;

public class Main extends Application {

    private Game game;
    private Point previouslyFocusedCell;
    private static final int borderSize = 5;
    private static final double minCellSize = 100.0;
    private double currentCellSize = minCellSize;
    private int numCols;
    private int numRows;
    private GridPane gameBoard;
    private BorderPane root = new BorderPane();

    private Map<Piece, String> pieceIcons = Map.of(
            new Piece(MAN, DARK), "/dark_man.png",
            new Piece(KING, DARK), "/dark_king.png",
            new Piece(MAN, LIGHT), "/light_man.png",
            new Piece(KING, LIGHT), "/light_king.png"
    );

    private Map<ColorType, String> cellIcons = Map.of(
            DARK, "/dark_wood_background.png",
            LIGHT, "/light_wood_background.png"
    );

    private EventHandler<? super MouseEvent> onCellSelectedHandler = mouseEvent -> {
        Node source = (Node) mouseEvent.getSource();
        Point focusedCell = new Point(getRowIndex(source), getColumnIndex(source));
        if (previouslyFocusedCell != null && game.canMove(previouslyFocusedCell, focusedCell)) {
            MoveResult result = game.move(previouslyFocusedCell, focusedCell);
            drawBattleField();
            if (result == MoveResult.OPPONENT_WON) {
                showGameOverAlert();
            }
        }
        previouslyFocusedCell = focusedCell;
    };

    private void showGameOverAlert() {
        Alert alert = new Alert(INFORMATION);
        ((Stage) alert.getDialogPane().getScene().getWindow())
                .getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
        alert.setTitle("Game over!");
        alert.setWidth(300.0);
        alert.setHeaderText(null);
        alert.setContentText((game.getState().getCurrentPlayer() == DARK ? LIGHT.name() : DARK.name()) + " player won!");
        alert.setOnCloseRequest(e -> System.exit(0));
        alert.showAndWait();
    }

    private void drawBattleField() {
        List<List<Cell>> battleField = game.getState().getBattleField();
        for (int rowIdx = 0; rowIdx < numRows; rowIdx++) {
            for (int columnIdx = 0; columnIdx < numCols; columnIdx++) {
                Node cell = cellToNode(battleField.get(rowIdx).get(columnIdx));
                cell.setOnMouseClicked(onCellSelectedHandler);
                gameBoard.add(cell, columnIdx, rowIdx);
            }
        }
        gameBoard.setVisible(false);
        gameBoard.setVisible(true);
    }

    private void initGameBord() {
        gameBoard = new GridPane();
        numCols = game.getRule().getBattleFieldSize().getX();
        numRows = game.getRule().getBattleFieldSize().getY();
        gameBoard.setStyle("-fx-border-color: black; -fx-border-width: " + borderSize + ";");
        IntStream.range(0, numCols).mapToObj(i -> new ColumnConstraints())
                .peek(c -> c.setPercentWidth(100.0 / numCols))
                .forEach(c -> gameBoard.getColumnConstraints().add(c));
        IntStream.range(0, numRows).mapToObj(i -> new RowConstraints())
                .peek(c -> c.setPercentHeight(100.0 / numRows))
                .forEach(c -> gameBoard.getRowConstraints().add(c));
        drawBattleField();
    }

    private Node cellToNode(Cell cell) {
        StackPane sp = new StackPane();
        if (cell.getColor() == DARK) {
            sp.setStyle("-fx-background-color: grey;");
        }
        if (cell.getPiece() != null) {
            sp.getChildren().add(getImageByPath(pieceIcons.get(cell.getPiece())));
        }
        return sp;
    }

    private ImageView getImageByPath(String iconPath) {
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
        imageView.setFitHeight(100.0);
        imageView.setFitWidth(100.0);
        return imageView;
    }

    @Override
    public void start(Stage stage) {
        game = new Game(new RussianCheckersRules());
        initGameBord();
        root = new BorderPane();
        root.setCenter(gameBoard);
        stage.setTitle("Checkers");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
        stage.setScene(new Scene(root, minCellSize * numCols + borderSize * 2, minCellSize * numRows + borderSize * 2));
        setupResizableHandlers(stage);
        stage.show();
    }

    private void setupResizableHandlers(Stage stage) {
        stage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (KeyCode.F.equals(event.getCode())) {
                stage.setFullScreen(!stage.isFullScreen());
                resize(stage);
            } else if (KeyCode.ESCAPE.equals(event.getCode())) {
                stage.setFullScreen(false);
                resize(stage);
            }
        });
        stage.heightProperty().addListener((obs, oh, nh) -> resize(stage));
        stage.widthProperty().addListener((obs, ow, nw) -> resize(stage));
        stage.fullScreenProperty().addListener((newValue) -> resize(stage));
        stage.showingProperty().addListener((observable, oldValue, showing) -> {
            if (showing) {//fix min size of window including borders
                stage.setMinHeight(stage.getHeight());
                stage.setMinWidth(stage.getWidth());
            }
        });
    }

    private void resize(Stage stage) {
        double nw = stage.getScene().getWidth();
        double nh = stage.getScene().getHeight();
        boolean isHorizontal = nw > nh;
        boolean isVertical = !isHorizontal;
        double diff = isHorizontal ? nw - nh : nh - nw;
        double half = diff / 2;
        gameBoard.setMinSize(isHorizontal ? nh : nw, isHorizontal ? nh : nw);
        root.setTop(region(isVertical, isVertical ? nw : 0, isVertical ? half : 0));
        root.setBottom(region(isVertical, isVertical ? nw : 0, isVertical ? half : 0));
        root.setLeft(region(isHorizontal, isHorizontal ? half : 0, isHorizontal ? nh : 0));
        root.setRight(region(isHorizontal, isHorizontal ? half : 0, isHorizontal ? nh : 0));
    }

    private Region region(boolean min, double width, double height) {
        Region region = new Region();
        region.setStyle("-fx-background-color: #000000;");
        if (min) {
            region.setMinSize(width, height);
        } else {
            region.setMaxSize(width, height);
        }
        return region;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
