package com.tanksdinos.tanksdinos;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class MenuManager {
    private enum MenuState {
        MAIN,
        GAME_TYPE,
        PLAYER_SETUP,
        LOADING,
        GAME_OVER
    }

    private String[] mainMenuOptions = {"Play Game", "Exit"};
    private String[] gameTypeOptions = {"1 Player", "2 Players", "Back"};
    private String[] setupOptions = {"Player 1 Color", "Volume: ", "Start Game", "Back"};
    private String[] twoPlayerSetupOptions = {"Player 1 Color", "Player 2 Color", "Volume: ", "Start Game", "Back"};
    private Color[] availableColors = {Color.GREEN, Color.BLUE, Color.RED, Color.YELLOW, Color.PURPLE};
    private int selectedOption = 0;
    private int selectedColor1 = 0;
    private int selectedColor2 = 1;
    private boolean isTwoPlayers = false;
    private double volume = 1.0;
    private MenuState menuState = MenuState.MAIN;
    private LoadingState loadingState = null;
    private long loadingStartTime;
    private static final int LOADING_DURATION = 500; // 0.5 segundos

    private class LoadingState {
        final MenuState nextState;
        final Runnable onComplete;
        
        LoadingState(MenuState nextState, Runnable onComplete) {
            this.nextState = nextState;
            this.onComplete = onComplete;
        }
    }

    public void update() {
        if (loadingState != null) {
            long elapsed = System.currentTimeMillis() - loadingStartTime;
            if (elapsed >= LOADING_DURATION) {
                menuState = loadingState.nextState;
                if (loadingState.onComplete != null) {
                    loadingState.onComplete.run();
                }
                loadingState = null;
            }
        }
    }

    public void handleInput(KeyCode code) {
        switch (menuState) {
            case MAIN:
                handleMainMenuInput(code);
                break;
            case GAME_TYPE:
                handleGameTypeInput(code);
                break;
            case PLAYER_SETUP:
                handlePlayerSetupInput(code);
                break;
            case GAME_OVER:
                handleGameOverInput(code);
                break;
        }
    }

    private void handleMainMenuInput(KeyCode code) {
        switch (code) {
            case UP:
            case DOWN:
                selectedOption = (selectedOption + 1) % mainMenuOptions.length;
                break;
            case ENTER:
                if (selectedOption == 0) {
                    menuState = MenuState.GAME_TYPE;
                    selectedOption = 0;
                } else {
                    System.exit(0);
                }
                break;
        }
    }

    private void handleGameTypeInput(KeyCode code) {
        switch (code) {
            case UP:
            case DOWN:
                selectedOption = (selectedOption + 1) % gameTypeOptions.length;
                break;
            case ENTER:
                if (selectedOption == 2) { // Back
                    menuState = MenuState.MAIN;
                } else {
                    isTwoPlayers = (selectedOption == 1);
                    menuState = MenuState.PLAYER_SETUP;
                    selectedOption = 0;
                }
                break;
            case ESCAPE:
                menuState = MenuState.MAIN;
                selectedOption = 0;
                break;
        }
    }

    private void handlePlayerSetupInput(KeyCode code) {
        int maxOptions = isTwoPlayers ? twoPlayerSetupOptions.length : setupOptions.length;
        switch (code) {
            case UP:
                selectedOption = (selectedOption - 1 + maxOptions) % maxOptions;
                break;
            case DOWN:
                selectedOption = (selectedOption + 1) % maxOptions;
                break;
            case LEFT:
                if (selectedOption == 0) {
                    selectedColor1 = (selectedColor1 - 1 + availableColors.length) % availableColors.length;
                } else if (isTwoPlayers && selectedOption == 1) {
                    selectedColor2 = (selectedColor2 - 1 + availableColors.length) % availableColors.length;
                } else if ((isTwoPlayers && selectedOption == 2) || (!isTwoPlayers && selectedOption == 1)) {
                    volume = Math.max(0, volume - 0.1);
                    Main.getInstance().getAudioManager().setVolume(volume);
                }
                break;
            case RIGHT:
                if (selectedOption == 0) {
                    selectedColor1 = (selectedColor1 + 1) % availableColors.length;
                } else if (isTwoPlayers && selectedOption == 1) {
                    selectedColor2 = (selectedColor2 + 1) % availableColors.length;
                } else if ((isTwoPlayers && selectedOption == 2) || (!isTwoPlayers && selectedOption == 1)) {
                    volume = Math.min(1, volume + 0.1);
                    Main.getInstance().getAudioManager().setVolume(volume);
                }
                break;
            case ENTER:
                if ((isTwoPlayers && selectedOption == 3) || (!isTwoPlayers && selectedOption == 2)) {
                    startGame();
                } else if ((isTwoPlayers && selectedOption == 4) || (!isTwoPlayers && selectedOption == 3)) {
                    menuState = MenuState.GAME_TYPE;
                    selectedOption = 0;
                }
                break;
            case ESCAPE:
                menuState = MenuState.GAME_TYPE;
                selectedOption = 0;
                break;
        }
    }

    private void handleGameOverInput(KeyCode code) {
        if (code == KeyCode.M) {
            menuState = MenuState.MAIN;
            selectedOption = 0;
        }
    }

    private void startGame() {
        // Iniciar pantalla de carga
        startLoading(MenuState.LOADING, () -> {
            // Este código se ejecuta cuando termina la carga
            Color color1 = availableColors[selectedColor1];
            Color color2 = isTwoPlayers ? availableColors[selectedColor2] : null;
            Main.startGame(isTwoPlayers, color1, color2);
        });
    }

    public void render(GraphicsContext gc) {
        // Fondo negro siempre
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, 800, 600);

        switch (menuState) {
            case MAIN:
                renderMainMenu(gc);
                break;
            case GAME_TYPE:
                renderGameTypeMenu(gc);
                break;
            case PLAYER_SETUP:
                renderPlayerSetup(gc);
                break;
            case LOADING:
                renderLoadingScreen(gc);
                break;
            case GAME_OVER:
                renderGameOver(gc);
                break;
        }
    }

    private void renderMainMenu(GraphicsContext gc) {
        // Limpia la pantalla con fondo negro
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, 800, 600);

        // Configura el estilo del texto
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 30));
        gc.setTextAlign(TextAlignment.CENTER);

        // Dibuja el título
        gc.setFont(new Font("Arial", 40));
        gc.fillText("TANKS VS DINOSAURS", 400, 100);

        // Dibuja las opciones del menú
        gc.setFont(new Font("Arial", 30));
        for (int i = 0; i < mainMenuOptions.length; i++) {
            if (i == selectedOption) {
                gc.setFill(Color.YELLOW); // Opción seleccionada en amarillo
            } else {
                gc.setFill(Color.WHITE);  // Otras opciones en blanco
            }
            gc.fillText(mainMenuOptions[i], 400, 250 + i * 50);
        }
    }

    private void renderGameTypeMenu(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, 800, 600);
        
        gc.setFill(Color.WHITE);
        gc.setFont(new Font(30));
        gc.setTextAlign(TextAlignment.CENTER);
        
        gc.fillText("Select Game Mode", 400, 200);
        
        for (int i = 0; i < gameTypeOptions.length; i++) {
            if (i == selectedOption) {
                gc.setFill(Color.YELLOW);
            } else {
                gc.setFill(Color.WHITE);
            }
            gc.fillText(gameTypeOptions[i], 400, 300 + i * 50);
        }
    }

    public void renderGameOver(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 40));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("GAME OVER", 400, 150);
        
        gc.setFont(new Font("Arial", 20));
        String[] lines = Main.getInstance().getGameWorld().getWinnerMessage().split("\n");
        for (int i = 0; i < lines.length; i++) {
            gc.fillText(lines[i], 400, 250 + i * 30);
        }
        
        gc.fillText("Press M to return to menu", 400, 430);
    }

    public void renderPauseMenu(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 40));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("PAUSE", 400, 300);
        gc.setFont(new Font("Arial", 20));
        gc.fillText("Press ESC to continue", 400, 350);
    }

    private void startLoading(MenuState nextState, Runnable onComplete) {
        loadingState = new LoadingState(nextState, onComplete);
        loadingStartTime = System.currentTimeMillis();
    }

    private void renderLoadingScreen(GraphicsContext gc) {
        // Título
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 30));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("Loading Game...", 400, 250);

        // Barra de progreso
        double progress = (System.currentTimeMillis() - loadingStartTime) / (double) LOADING_DURATION;
        double barWidth = 400;
        double barHeight = 20;
        double x = 400 - barWidth/2;
        double y = 300;

        // Borde de la barra
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeRect(x, y, barWidth, barHeight);

        // Progreso
        gc.setFill(Color.GREEN);
        gc.fillRect(x, y, barWidth * Math.min(progress, 1.0), barHeight);

        // Tips de juego
        gc.setFont(new Font("Arial", 16));
        String[] tips = {
            "Tip: Recoge power-ups para mejorar tu tanque",
            "Tip: El escudo te protege temporalmente del daño",
            "Tip: Disparo doble aumenta tu poder de fuego"
        };
        int tipIndex = (int)((System.currentTimeMillis() / 3000) % tips.length);
        gc.fillText(tips[tipIndex], 400, 350);
    }

    private void renderPlayerSetup(GraphicsContext gc) {
        renderMenuBackground(gc);
        gc.setFont(new Font("Arial", 30));
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.CENTER);
        
        String[] currentOptions = isTwoPlayers ? twoPlayerSetupOptions : setupOptions;
        
        for (int i = 0; i < currentOptions.length; i++) {
            double y = 200 + i * 50;
            boolean isSelected = (selectedOption == i);
            
            if (i == 0) { // Player 1 Color
                renderColorOption(gc, "Player 1 Color: ", selectedColor1, y, isSelected);
            } else if (isTwoPlayers && i == 1) { // Player 2 Color
                renderColorOption(gc, "Player 2 Color: ", selectedColor2, y, isSelected);
            } else if ((isTwoPlayers && i == 2) || (!isTwoPlayers && i == 1)) { // Volume
                String volumeText = "Volume: " + Math.round(volume * 100) + "%";
                renderMenuItem(gc, volumeText, y, isSelected);
            } else { // Other options
                renderMenuItem(gc, currentOptions[i], y, isSelected);
            }
        }
    }

    private void renderColorOption(GraphicsContext gc, String label, int colorIndex, double y, boolean isSelected) {
        gc.setFill(isSelected ? Color.YELLOW : Color.WHITE);
        gc.fillText(label, 300, y);
        gc.setFill(availableColors[colorIndex]);
        gc.fillRect(450, y - 20, 30, 30);
    }

    private void renderMenuItem(GraphicsContext gc, String text, double y, boolean isSelected) {
        gc.setFill(isSelected ? Color.YELLOW : Color.WHITE);
        gc.fillText(text, 400, y);
    }

    private void renderMenuBackground(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, 800, 600);
    }
}
