package com.tanksdinos.tanksdinos;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import java.util.List;

public class MenuManager {
    private enum MenuState {
        LOGIN,
        REGISTER,
        MAIN,
        GAME_TYPE,
        PLAYER_SETUP,
        LOADING,
        GAME_OVER,
        HIGH_SCORES,
        LOGIN_FORM,
        REGISTER_FORM
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
    private MenuState menuState = MenuState.LOGIN;
    private LoadingState loadingState = null;
    private long loadingStartTime;
    private static final int LOADING_DURATION = 500; // 0.5 segundos
    private String username = "";
    private String password = "";
    private boolean isEnteringUsername = true;
    private String errorMessage = "";
    private long errorMessageTime = 0;
    private static final long ERROR_MESSAGE_DURATION = 3000; // 3 segundos

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
            case LOGIN:
                handleLoginInput(code);
                break;
            case REGISTER:
                handleRegisterInput(code);
                break;
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
            case HIGH_SCORES:
                handleHighScoresInput(code);
                break;
            case LOGIN_FORM:
                handleLoginFormInput(code);
                break;
            case REGISTER_FORM:
                handleRegisterFormInput(code);
                break;
        }
    }

    private void handleMainMenuInput(KeyCode code) {
        switch (code) {
            case UP:
            case DOWN:
                selectedOption = (selectedOption + (code == KeyCode.UP ? -1 : 1) + 4) % 4;
                break;
            case ENTER:
                switch (selectedOption) {
                    case 0: // Play Game
                        menuState = MenuState.GAME_TYPE;
                        selectedOption = 0;
                        break;
                    case 1: // High Scores
                        menuState = MenuState.HIGH_SCORES;
                        selectedOption = 0;
                        break;
                    case 2: // Logout
                        UserManager.getInstance().logout();
                        menuState = MenuState.LOGIN;
                        selectedOption = 0;
                        break;
                    case 3: // Exit
                        System.exit(0);
                        break;
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
            case LOGIN:
                renderLoginMenu(gc);
                break;
            case REGISTER:
                renderRegisterMenu(gc);
                break;
            case HIGH_SCORES:
                renderHighScores(gc);
                break;
            case LOGIN_FORM:
                renderLoginForm(gc);
                break;
            case REGISTER_FORM:
                renderRegisterForm(gc);
                break;
        }
    }

    private void renderMainMenu(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, 800, 600);

        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 40));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("TANKS VS DINOSAURS", 400, 100);

        // Mostrar usuario actual
        gc.setFont(new Font("Arial", 20));
        User currentUser = UserManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            gc.fillText("Welcome! Please login or register", 400, 150);
            String[] options = {
                "Login",
                "Register",
                "Play as Guest",
                "Exit"
            };
            renderOptions(gc, options);
        } else {
            gc.fillText("Welcome, " + currentUser.getUsername() + "!", 400, 150);
            String[] options = {
                "Play Game",
                "High Scores",
                "Logout",
                "Exit"
            };
            renderOptions(gc, options);
        }
    }

    private void renderOptions(GraphicsContext gc, String[] options) {
        gc.setFont(new Font("Arial", 30));
        for (int i = 0; i < options.length; i++) {
            gc.setFill(selectedOption == i ? Color.YELLOW : Color.WHITE);
            gc.fillText(options[i], 400, 250 + i * 50);
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

    private void renderLoginMenu(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, 800, 600);
        
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 40));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("TANKS VS DINOSAURS", 400, 100);
        
        gc.setFont(new Font("Arial", 30));
        
        String[] options = {
            "Login",
            "Register",
            "Play as Guest",
            "Exit"
        };
        
        for (int i = 0; i < options.length; i++) {
            gc.setFill(selectedOption == i ? Color.YELLOW : Color.WHITE);
            gc.fillText(options[i], 400, 250 + i * 50);
        }
    }

    private void renderRegisterMenu(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, 800, 600);
        
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 40));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("REGISTER", 400, 100);
        
        gc.setFont(new Font("Arial", 20));
        gc.fillText("Username: " + username + (isEnteringUsername ? "_" : ""), 400, 200);
        gc.fillText("Password: " + "*".repeat(password.length()) + (!isEnteringUsername ? "_" : ""), 400, 250);
        gc.fillText("Press ENTER to confirm, ESC to cancel", 400, 350);
    }

    private void renderHighScores(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, 800, 600);
        
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 40));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("HIGH SCORES", 400, 100);

        List<Score> highScores = UserManager.getInstance().getHighScores();
        gc.setFont(new Font("Arial", 20));
        
        // Encabezados
        gc.fillText("Rank", 200, 180);
        gc.fillText("Player", 350, 180);
        gc.fillText("Score", 500, 180);
        gc.fillText("Date", 650, 180);

        // Listar puntuaciones
        int y = 220;
        for (int i = 0; i < highScores.size(); i++) {
            Score score = highScores.get(i);
            gc.setFill(i < 3 ? Color.YELLOW : Color.WHITE);
            gc.fillText("#" + (i + 1), 200, y);
            gc.fillText(score.getUsername(), 350, y);
            gc.fillText(String.valueOf(score.getScore()), 500, y);
            gc.fillText(score.getFormattedDate(), 650, y);
            y += 30;
        }

        gc.setFill(Color.GRAY);
        gc.fillText("Press ESC to return", 400, 550);
    }

    private void handleLoginInput(KeyCode code) {
        switch (code) {
            case UP:
                selectedOption = (selectedOption - 1 + 4) % 4;
                break;
            case DOWN:
                selectedOption = (selectedOption + 1) % 4;
                break;
            case ENTER:
                switch (selectedOption) {
                    case 0: // Login
                        menuState = MenuState.LOGIN_FORM;
                        resetLoginFields();
                        break;
                    case 1: // Register
                        menuState = MenuState.REGISTER_FORM;
                        resetLoginFields();
                        break;
                    case 2: // Guest
                        UserManager.getInstance().loginAsGuest();
                        menuState = MenuState.MAIN;
                        break;
                    case 3: // Exit
                        System.exit(0);
                        break;
                }
                break;
        }
    }

    private void resetLoginFields() {
        username = "";
        password = "";
        selectedOption = 0;
    }

    private void handleRegisterInput(KeyCode code) {
        // Similar a handleLoginInput
    }

    private void handleHighScoresInput(KeyCode code) {
        if (code == KeyCode.ESCAPE || code == KeyCode.ENTER) {
            menuState = MenuState.MAIN;
        }
    }

    private void renderLoginForm(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, 800, 600);
        
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 40));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("LOGIN", 400, 100);
        
        gc.setFont(new Font("Arial", 20));
        gc.setFill(selectedOption == 0 ? Color.YELLOW : Color.WHITE);
        gc.fillText("Username: " + username + (selectedOption == 0 ? "_" : ""), 400, 250);
        
        gc.setFill(selectedOption == 1 ? Color.YELLOW : Color.WHITE);
        gc.fillText("Password: " + "*".repeat(password.length()) + (selectedOption == 1 ? "_" : ""), 400, 300);
        
        gc.setFill(selectedOption == 2 ? Color.YELLOW : Color.WHITE);
        gc.fillText("Login", 400, 380);
        
        gc.setFill(Color.GRAY);
        gc.setFont(new Font("Arial", 16));
        gc.fillText("Press ESC to go back", 400, 500);
        gc.fillText("Use TAB to switch fields, ENTER to confirm", 400, 530);

        // Mostrar mensaje de error si existe
        if (!errorMessage.isEmpty() && System.currentTimeMillis() - errorMessageTime < ERROR_MESSAGE_DURATION) {
            gc.setFill(Color.RED);
            gc.setFont(new Font("Arial", 16));
            gc.fillText(errorMessage, 400, 450);
        }
    }

    private void handleLoginFormInput(KeyCode code) {
        switch (code) {
            case TAB:
                selectedOption = (selectedOption + 1) % 3;
                break;
            case UP:
            case DOWN:
                selectedOption = (selectedOption + (code == KeyCode.UP ? -1 : 1) + 3) % 3;
                break;
            case ESCAPE:
                menuState = MenuState.LOGIN;
                break;
            case ENTER:
                if (selectedOption == 2) {
                    if (username.isEmpty() || password.isEmpty()) {
                        showError("Username and password cannot be empty");
                    } else if (UserManager.getInstance().login(username, password)) {
                        menuState = MenuState.MAIN;
                    } else {
                        showError("Invalid username or password");
                    }
                }
                break;
            case BACK_SPACE:
                if (selectedOption == 0 && username.length() > 0) {
                    username = username.substring(0, username.length() - 1);
                } else if (selectedOption == 1 && password.length() > 0) {
                    password = password.substring(0, password.length() - 1);
                }
                break;
            default:
                if (code.isLetterKey() || code.isDigitKey()) {
                    if (selectedOption == 0) {
                        username += code.getChar();
                    } else if (selectedOption == 1) {
                        password += code.getChar();
                    }
                }
                break;
        }
    }

    private void handleRegisterFormInput(KeyCode code) {
        switch (code) {
            case TAB:
                selectedOption = (selectedOption + 1) % 3;
                break;
            case UP:
            case DOWN:
                selectedOption = (selectedOption + (code == KeyCode.UP ? -1 : 1) + 3) % 3;
                break;
            case ESCAPE:
                menuState = MenuState.LOGIN;
                break;
            case ENTER:
                if (selectedOption == 2) {
                    if (username.isEmpty() || password.isEmpty()) {
                        showError("Username and password cannot be empty");
                    } else if (username.length() < 3) {
                        showError("Username must be at least 3 characters");
                    } else if (password.length() < 4) {
                        showError("Password must be at least 4 characters");
                    } else if (UserManager.getInstance().register(username, password)) {
                        menuState = MenuState.MAIN;
                    } else {
                        showError("Username already exists");
                    }
                }
                break;
            case BACK_SPACE:
                if (selectedOption == 0 && username.length() > 0) {
                    username = username.substring(0, username.length() - 1);
                } else if (selectedOption == 1 && password.length() > 0) {
                    password = password.substring(0, password.length() - 1);
                }
                break;
            default:
                if (code.isLetterKey() || code.isDigitKey()) {
                    if (selectedOption == 0) {
                        username += code.getChar();
                    } else if (selectedOption == 1) {
                        password += code.getChar();
                    }
                }
                break;
        }
    }

    private void renderRegisterForm(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, 800, 600);
        
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 40));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("REGISTER", 400, 100);
        
        gc.setFont(new Font("Arial", 20));
        gc.setFill(selectedOption == 0 ? Color.YELLOW : Color.WHITE);
        gc.fillText("Username: " + username + (selectedOption == 0 ? "_" : ""), 400, 250);
        
        gc.setFill(selectedOption == 1 ? Color.YELLOW : Color.WHITE);
        gc.fillText("Password: " + "*".repeat(password.length()) + (selectedOption == 1 ? "_" : ""), 400, 300);
        
        gc.setFill(selectedOption == 2 ? Color.YELLOW : Color.WHITE);
        gc.fillText("Register", 400, 380);
        
        gc.setFill(Color.GRAY);
        gc.setFont(new Font("Arial", 16));
        gc.fillText("Press ESC to go back", 400, 500);
        gc.fillText("Use TAB to switch fields, ENTER to confirm", 400, 530);

        // Mostrar mensaje de error si existe
        if (!errorMessage.isEmpty() && System.currentTimeMillis() - errorMessageTime < ERROR_MESSAGE_DURATION) {
            gc.setFill(Color.RED);
            gc.setFont(new Font("Arial", 16));
            gc.fillText(errorMessage, 400, 450);
        }
    }

    private void showError(String message) {
        errorMessage = message;
        errorMessageTime = System.currentTimeMillis();
    }
}
