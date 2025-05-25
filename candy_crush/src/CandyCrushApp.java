import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;


import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CandyCrushApp extends Application {
    private static final int ROWS = 9, COLS = 9;
    private static final int MAX_MOVES = 20;

    private final Game game  = new Game(ROWS, COLS, MAX_MOVES);
    private final Board board = game.getBoard();

    private final ImageView[][] cells = new ImageView[ROWS][COLS];
    private final Map<String,Image> images = new HashMap<>();

    private Label scoreLabel = new Label("Score: 0");
    private Label movesLabel = new Label("Moves: " + MAX_MOVES);
    private Label timerLabel = new Label("Time: 0s");

    private Timeline timeline;
    private int elapsedSeconds = 0;


    @Override
    public void start(Stage stage) {
        loadImages();
        showMenu(stage);
    }

    private void showMenu(Stage stage) {
        // Background image
        ImageView bg = new ImageView(images.get("BACKGROUND"));
        bg.setFitWidth(600);
        bg.setFitHeight(400);
        bg.setPreserveRatio(false);

        // Title in maroon
        Label title = new Label("CANDY CRUSH");
        title.setFont(Font.font("Arial Rounded MT Bold", FontWeight.EXTRA_BOLD, 50));
        title.setTextFill(Color.MAROON);
        title.setEffect(new DropShadow(5, Color.DARKRED));

        // Start Button (unchanged)
        Button startBtn = new Button("START");
        startBtn.setFont(Font.font("Arial Rounded MT Bold", FontWeight.BOLD, 24));
        startBtn.setTextFill(Color.WHITE);
        startBtn.setStyle(
                "-fx-background-radius: 30; " +
                        "-fx-padding: 10 30; " +
                        "-fx-background-color: " +
                        "linear-gradient(to right, #ff9a9e, #fad0c4)"
        );
        startBtn.setOnAction(e -> showGame(stage));

        // Credit, bigger and in gold
        Label credit = new Label("Made by Melek and Baya");
        credit.setFont(Font.font("Arial", FontPosture.ITALIC, 18));
        credit.setTextFill(Color.DARKRED);
        credit.setPadding(new Insets(5));

        // Menu layout with semi-transparent white backing
        VBox menuBox = new VBox(20, title, startBtn, credit);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setPadding(new Insets(20));
        menuBox.setBackground(new Background(new BackgroundFill(
                Color.rgb(255, 255, 255, 0.4),   // white at 60% opacity
                new CornerRadii(15),             // rounded corners
                Insets.EMPTY
        )));

        // Stack background + menu
        StackPane root = new StackPane(bg, menuBox);
        Scene scene = new Scene(root, 600, 400);

        stage.setScene(scene);
        stage.setTitle("Candy Crush");
        stage.show();
    }

    private void showGame(Stage stage) {
        // Top bar: moves | timer | score
        HBox topBar = new HBox(30, movesLabel, timerLabel, scoreLabel);
        topBar.setAlignment(Pos.CENTER);
        topBar.setStyle("-fx-padding:10; -fx-background-color: lightgoldenrodyellow;");

        // Grid of candies
        GridPane grid = new GridPane();
        grid.setHgap(2);
        grid.setVgap(2);
        for(int r=0;r<ROWS;r++) {
            for(int c=0;c<COLS;c++) {
                ImageView iv = new ImageView();
                iv.setFitWidth(40);
                iv.setFitHeight(40);
                cells[r][c] = iv;
                grid.add(iv, c, r);

                final int rr = r, cc = c;
                iv.setOnMouseClicked(ev -> handleClick(rr, cc));
            }
        }

        BorderPane root = new BorderPane(grid);
        root.setTop(topBar);
        root.setStyle("-fx-background-color: cornsilk; -fx-padding:10;");

        // Timer binding
        timerLabel.setText("Time: 00:00");
        startTimer();

        refreshUI();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Candy Crush");
        stage.sizeToScene();
        stage.show();
    }

    private void startTimer() {
        if (timeline != null) {
            timeline.stop();
        }
        elapsedSeconds = 0;
        timerLabel.setText("Time: 00:00");

        timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    elapsedSeconds++;
                    int mins = elapsedSeconds / 60;
                    int secs = elapsedSeconds % 60;
                    timerLabel.setText(String.format("Time: %02d:%02d", mins, secs));
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }


    private void loadImages() {
        for (CandyColor color : CandyColor.values()) {
            String lower = color.name().toLowerCase();
            String base  = lower.substring(0,1).toUpperCase() + lower.substring(1);

            loadImage(color + "_NORMAL",
                    "/images/" + base + ".png");
            loadImage(color + "_STRIPED_HORIZONTAL",
                    "/images/" + base + "-Striped-Horizontal.png");
            loadImage(color + "_STRIPED_VERTICAL",
                    "/images/" + base + "-Striped-Vertical.png");
            loadImage(color + "_EXPLOSIVE",
                    "/images/" + base + "-Wrapped.png");
            loadImage(color + "_COLOR_BOMB",
                    "/images/Bomb.png");
        }
        loadImage("BLANK",      "/images/blank.png");
        loadImage("SCROLL",     "/images/scroll.png");
        loadImage("BACKGROUND", "/images/background.jpg");
    }

    private void loadImage(String key, String resourcePath) {
        InputStream is = getClass().getResourceAsStream(resourcePath);
        if (is == null) {
            throw new IllegalStateException("Resource not found: " + resourcePath);
        }
        images.put(key, new Image(is));
    }

    private void refreshUI() {
        movesLabel.setText("Moves: "     + game.getMovesLeft());
        scoreLabel.setText("Score: "     + game.getScore());

        Candy[][] gridArr = board.getGrid();
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Candy candy = gridArr[r][c];
                String key   = candy.getColor() + "_" + candy.getType();
                cells[r][c].setImage(images.get(key));
            }
        }
    }

    private int[] lastClick = null;
    private void handleClick(int r, int c) {
        if (lastClick == null) {
            lastClick = new int[]{r, c};
        } else {
            game.makeMove(lastClick[0], lastClick[1], r, c);
            lastClick = null;
            refreshUI();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
