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
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.image.Image;

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
    private long countdownStartTime;
    private static final int COUNTDOWN_DURATION = 3000; // 3 segundos
    private boolean lastGameTwoPlayers = false;
    private Color lastColor1 = Color.GREEN;
    private Color lastColor2 = Color.YELLOW;

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
                    case COUNTDOWN:
                        renderCountdown();
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
        // Inicializar primero el AudioManager
        audioManager = new AudioManager();
        
        // Luego iniciar la precarga de recursos
        Thread resourceLoader = new Thread(() -> {
            try {
                // Precarga imágenes comunes
                new Image(getClass().getResourceAsStream("/images/tank.png"));
                new Image(getClass().getResourceAsStream("/images/dino.png"));
                new Image(getClass().getResourceAsStream("/images/background.jpg"));
                new Image(getClass().getResourceAsStream("/images/shield.png"));
            } catch (Exception e) {
                System.err.println("Error precargando recursos: " + e.getMessage());
            }
        });
        resourceLoader.setDaemon(true);
        resourceLoader.start();

        // Inicializar el resto de managers
        menuManager = new MenuManager();
        dbManager = DatabaseManager.getInstance();
        levelManager = new LevelManager();
        powerUpManager = new PowerUpManager();
        gameWorld = new GameWorld(levelManager, powerUpManager, audioManager, false, Color.GREEN, null);
    }

    private void setupCanvas() {
        canvas = new Canvas(WIDTH, HEIGHT);
        gc = canvas.getGraphicsContext2D();
    }

    private void setupInputHandling(Scene scene) {
        scene.setOnKeyPressed(e -> {
            switch (gameState) {
                case PLAYING:
                    if (e.getCode() == KeyCode.ESCAPE) {
                        gameState = GameState.MENU;
                        menuManager = new MenuManager();
                    } else {
                        gameWorld.handleInput(e.getCode(), true);
                    }
                    break;
                case MENU:
                    menuManager.handleInput(e.getCode());
                    break;
                case GAME_OVER:
                    handleGameOverInput(e.getCode());
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
        User currentUser = UserManager.getInstance().getCurrentUser();
        if (currentUser != null && !currentUser.isGuest()) {
            dbManager.saveScore(currentUser.getUsername(), lastScore, gameWorld.isTwoPlayers());
        }
    }

    private void handleGameOverInput(KeyCode code) {
        if (code == KeyCode.M) {
            gameState = GameState.MENU;
            menuManager = new MenuManager();
        }
    }

    private void update() {
        gameWorld.update();
    }

    private void render() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gameWorld.render(gc);
    }

    public static void startGame(boolean isTwoPlayers, Color color1, Color color2) {
        instance.lastGameTwoPlayers = isTwoPlayers;
        instance.lastColor1 = color1;
        instance.lastColor2 = color2;
        gameState = GameState.COUNTDOWN;
        instance.countdownStartTime = System.currentTimeMillis();
        instance.resetGame(isTwoPlayers, color1, color2);
    }

    private void resetGame(boolean isTwoPlayers, Color color1, Color color2) {
        gameWorld = new GameWorld(levelManager, powerUpManager, audioManager, isTwoPlayers, color1, color2);
    }

    private void renderCountdown() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gameWorld.render(gc);

        long elapsed = System.currentTimeMillis() - countdownStartTime;
        int count = 3 - (int)(elapsed / 1000);
        
        if (count <= 0) {
            gameState = GameState.PLAYING;
            return;
        }

        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 72));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(String.valueOf(count), WIDTH/2, HEIGHT/2);
    }

    public static Main getInstance() {
        return instance;
    }

    public AudioManager getAudioManager() {
        return audioManager;
    }

    public GameWorld getGameWorld() {
        return gameWorld;
    }

    public static void main(String[] args) {
        launch();
    }
}

enum GameState {
    MENU,
    COUNTDOWN,
    PLAYING,
    PAUSE,
    GAME_OVER,
    LEVEL_COMPLETE
}