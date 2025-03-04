package com.tanksdinos.tanksdinos;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class MenuManager {
    private String[] menuOptions = {"Start Game", "Options", "Exit"};
    private int selectedOption = 0;

    public void update() {
        // La lógica de actualización se maneja en handleInput
    }

    public void render(GraphicsContext gc) {
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
        for (int i = 0; i < menuOptions.length; i++) {
            if (i == selectedOption) {
                gc.setFill(Color.YELLOW); // Opción seleccionada en amarillo
            } else {
                gc.setFill(Color.WHITE);  // Otras opciones en blanco
            }
            gc.fillText(menuOptions[i], 400, 250 + i * 50);
        }
    }

    public void handleInput(KeyCode code) {
        switch (code) {
            case UP:
                selectedOption = (selectedOption - 1 + menuOptions.length) % menuOptions.length;
                break;
            case DOWN:
                selectedOption = (selectedOption + 1) % menuOptions.length;
                break;
            case ENTER:
                executeOption();
                break;
        }
    }

    private void executeOption() {
        switch (selectedOption) {
            case 0: // Start Game
                Main.gameState = GameState.PLAYING;
                break;
            case 1: // Options
                // Implementar más tarde
                break;
            case 2: // Exit
                System.exit(0);
                break;
        }
    }

    public void renderPauseMenu(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 40));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("PAUSE", 400, 300);
        gc.setFont(new Font("Arial", 20));
        gc.fillText("Press ESC to continue", 400, 350);
    }

    public void renderGameOver(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 40));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("GAME OVER", 400, 300);
    }
}
