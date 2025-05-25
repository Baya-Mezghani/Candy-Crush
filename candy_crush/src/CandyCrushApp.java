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
    private static final int ROWS         = 9;
    private static final int COLS         = 9;
    private static final int MAX_MOVES    = 20;

    private int targetScore;

    private Game  game;
    private Board board;

    private final ImageView[][] cells = new ImageView[ROWS][COLS];
    private final Map<String,Image> images = new HashMap<>();

    private Label movesLabel = new Label();
    private Label timerLabel = new Label("Time: 00:00");
    private Label scoreLabel = new Label();

    private Timeline timeline;
    private int elapsedSeconds = 0;

    @Override
    public void start(Stage stage) {
        loadImages();
        showMenu(stage);
    }

    private void showMenu(Stage stage) {
        ImageView bg = new ImageView(images.get("BACKGROUND"));
        bg.setFitWidth(600);
        bg.setFitHeight(400);
        bg.setPreserveRatio(false);

        Label title = new Label("CANDY CRUSH");
        title.setFont(Font.font("Arial Rounded MT Bold", FontWeight.EXTRA_BOLD, 50));
        title.setTextFill(Color.DARKRED);
        title.setEffect(new DropShadow(5, Color.MAROON));

        Button startBtn = new Button("START");
        startBtn.setFont(Font.font("Arial Rounded MT Bold", FontWeight.BOLD, 24));
        startBtn.setTextFill(Color.WHITE);
        startBtn.setStyle(
                "-fx-background-radius:30; -fx-padding:10 30; " +
                        "-fx-background-color: linear-gradient(to right, #ff9a9e, #fad0c4)"
        );
        startBtn.setOnAction(e -> showDifficulty(stage));

        Label credit = new Label("Made by Melek & Baya");
        credit.setFont(Font.font("Arial", FontPosture.ITALIC, 18));
        credit.setTextFill(Color.DARKRED);

        VBox menuBox = new VBox(20, title, startBtn, credit);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setPadding(new Insets(20));
        menuBox.setBackground(new Background(new BackgroundFill(
                Color.rgb(255,255,255,0.5), new CornerRadii(15), Insets.EMPTY
        )));

        StackPane root = new StackPane(bg, menuBox);
        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Candy Crush");
        stage.show();
    }
    private void showDifficulty(Stage stage) {
        ImageView bg = new ImageView(images.get("BACKGROUND"));
        bg.setFitWidth(600); bg.setFitHeight(400); bg.setPreserveRatio(false);

        Label prompt = new Label("Select Difficulty");
        prompt.setFont(Font.font("Arial Rounded MT Bold", FontWeight.BOLD, 36));
        prompt.setTextFill(Color.MAROON);
        prompt.setEffect(new DropShadow(3, Color.DARKRED));

        // easy / medium / hard buttons
        Button easy = makeDiffButton("EASY\n(700 pts)", 800, stage);
        Button med  = makeDiffButton("MEDIUM\n(1200 pts)", 1200, stage);
        Button hard = makeDiffButton("HARD\n(1700 pts)", 1700, stage);

        HBox box = new HBox(20, easy, med, hard);
        box.setAlignment(Pos.CENTER);

        VBox vbox = new VBox(30, prompt, box);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));
        vbox.setBackground(new Background(new BackgroundFill(
                Color.rgb(255,255,255,0.6), new CornerRadii(15), Insets.EMPTY
        )));

        StackPane root = new StackPane(bg, vbox);
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }

    private Button makeDiffButton(String text, int pts, Stage stage) {
        Button b = new Button(text);
        b.setFont(Font.font("Arial Rounded MT Bold", FontWeight.BOLD, 20));
        b.setTextFill(Color.WHITE);
        b.setStyle(
                "-fx-background-radius:20; -fx-padding:10 20; " +
                        "-fx-background-color:linear-gradient(to bottom, #a1c4fd,#c2e9fb)"
        );
        b.setOnAction(e -> {
            targetScore = pts;
            showGame(stage);
        });
        return b;
    }

    private void showGame(Stage stage) {
        game  = new Game(ROWS, COLS, MAX_MOVES);
        board = game.getBoard();
        elapsedSeconds = 0;

        movesLabel.setText("Moves: " + MAX_MOVES);
        scoreLabel.setText("Score: 0/" + targetScore);
        timerLabel.setText("Time: 00:00");

        HBox topBar = new HBox(30, movesLabel, timerLabel, scoreLabel);
        topBar.setAlignment(Pos.CENTER);
        topBar.setStyle("-fx-padding:10; -fx-background-color: lightgoldenrodyellow;");

        GridPane grid = new GridPane();
        grid.setHgap(2); grid.setVgap(2);
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                ImageView iv = new ImageView();
                iv.setFitWidth(40); iv.setFitHeight(40);
                cells[r][c] = iv;
                grid.add(iv, c, r);
                final int rr = r, cc = c;
                iv.setOnMouseClicked(ev -> handleClick(rr, cc));
            }
        }

        BorderPane gamePane = new BorderPane(grid);
        gamePane.setTop(topBar);
        gamePane.setStyle("-fx-background-color: cornsilk; -fx-padding:10;");

        StackPane root = new StackPane(gamePane);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Candy Crush");
        stage.sizeToScene();
        stage.show();

        startTimer();
        refreshUI();
    }

    private void startTimer() {
        if (timeline != null) timeline.stop();
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            elapsedSeconds++;
            int m = elapsedSeconds/60, s = elapsedSeconds%60;
            timerLabel.setText(String.format("Time: %02d:%02d", m, s));
        }));
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

    private void loadImage(String key, String path) {
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) throw new IllegalStateException("Missing: " + path);
        images.put(key, new Image(is));
    }

    private void refreshUI() {
        movesLabel.setText("Moves: " + game.getMovesLeft());
        scoreLabel.setText(
                String.format("Score: %d/%d", game.getScore(), targetScore)
        );

        Candy[][] g = board.getGrid();
        for (int r=0; r<ROWS; r++) {
            for (int c=0; c<COLS; c++) {
                Candy candy = g[r][c];
                String key = candy.getColor() + "_" + candy.getType();
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

            if (game.getScore() >= targetScore) {
                showEndDialog("Congratulations!",
                        "You reached your goal!", true);
            } else if (game.getMovesLeft() == 0) {
                showEndDialog("Game Over",
                        "No moves left!\nFinal score: " + game.getScore(),
                        false);
            }
        }
    }

    private void showEndDialog(String header, String msg, boolean won) {
        if (timeline != null) {
            timeline.stop();
        }
        Label h = new Label(header);
        h.setFont(Font.font("Arial Rounded MT Bold", 36));
        h.setTextFill(won ? Color.LIGHTGREEN : Color.RED);

        Label m = new Label(msg);
        m.setFont(Font.font("Arial", 20));
        m.setTextFill(Color.WHITE);

        Button retry = new Button("PLAY AGAIN");
        retry.setFont(Font.font("Arial Rounded MT Bold", 24));
        retry.setTextFill(Color.WHITE);
        retry.setStyle(
                "-fx-background-radius:30; -fx-padding:10 20; " +
                        "-fx-background-color:linear-gradient(to right,#a18cd1,#fbc2eb)"
        );
        retry.setOnAction(ev -> {
            Stage stg = (Stage) retry.getScene().getWindow();
            showGame(stg);  // completely rebuilds the game UI
        });

        VBox box = new VBox(20, h, m, retry);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));
        box.setBackground(new Background(new BackgroundFill(
                Color.rgb(0,0,0,0.7), new CornerRadii(10), Insets.EMPTY
        )));

        // Overlay on top of the existing gamePane
        StackPane root = (StackPane) timerLabel.getScene().getRoot();
        root.getChildren().add(box);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
