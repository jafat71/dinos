package com.tanksdinos.tanksdinos;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import java.io.IOException;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

public class Main extends Application {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private Canvas canvas;
    private GraphicsContext gc;
    private GameWorld gameWorld;
    public static GameState gameState = GameState.MENU;
    private MenuManager menuManager;
    private AudioManager audioManager;
    private DatabaseManager dbManager;
    private LevelManager levelManager;
    private PowerUpManager powerUpManager;
    public static int lastScore = 0;
    private static Main instance;

    @Override
    public void start(Stage stage) throws IOException {
        instance = this;
        initializeManagers();
        setupCanvas();
        
        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root);
        setupInputHandling(scene);

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                switch (gameState) {
                    case MENU:
                        menuManager.update();
                        menuManager.render(gc);
                        break;
                    case PLAYING:
                        update();
                        render();
                        break;
                    case PAUSE:
                        renderPauseScreen();
                        break;
                    case GAME_OVER:
                        handleGameOver();
                        break;
                }
            }
        }.start();

        stage.setTitle("Tanks vs Dinosaurs");
        stage.setScene(scene);
        stage.show();
    }

    private void initializeManagers() {
        menuManager = new MenuManager();
        audioManager = new AudioManager();
        dbManager = new DatabaseManager();
        levelManager = new LevelManager();
        powerUpManager = new PowerUpManager();
        gameWorld = new GameWorld(levelManager, powerUpManager, audioManager);
    }

    private void setupCanvas() {
        canvas = new Canvas(WIDTH, HEIGHT);
        gc = canvas.getGraphicsContext2D();
    }

    private void setupInputHandling(Scene scene) {
        scene.setOnKeyPressed(e -> {
            switch (gameState) {
                case PLAYING:
                    gameWorld.handleInput(e.getCode(), true);
                    break;
                case MENU:
                case GAME_OVER:
                    menuManager.handleInput(e.getCode());
                    break;
                case PAUSE:
                    handlePauseInput(e.getCode());
                    break;
            }
        });

        scene.setOnKeyReleased(e -> {
            if (gameState == GameState.PLAYING) {
                gameWorld.handleInput(e.getCode(), false);
            }
        });
    }

    private void handlePauseInput(KeyCode code) {
        if (code == KeyCode.ESCAPE) {
            gameState = GameState.PLAYING;
        }
    }

    private void renderPauseScreen() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gameWorld.render(gc);
        menuManager.renderPauseMenu(gc);
    }

    private void handleGameOver() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        lastScore = gameWorld.getScore();
        menuManager.renderGameOver(gc);
        dbManager.saveScore(lastScore);
    }

    private void update() {
        gameWorld.update();
    }

    private void render() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gameWorld.render(gc);
    }

    public static void restartGame() {
        
        gameState = GameState.PLAYING;
        instance.resetGame();
    }

    private void resetGame() {
        gameWorld = new GameWorld(levelManager, powerUpManager, audioManager);
    }

    public static void main(String[] args) {
        launch();
    }
}

enum GameState {
    MENU,
    PLAYING,
    PAUSE,
    GAME_OVER,
    LEVEL_COMPLETE
}