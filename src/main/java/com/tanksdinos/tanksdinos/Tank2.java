package com.tanksdinos.tanksdinos;

import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;

public class Tank2 extends Tank {
    private int score = 0;
    
    public Tank2(double x, double y, AudioManager audioManager, Color color) {
        super(x, y, audioManager, color != null ? color : Color.YELLOW);
    }

    @Override
    public void handleInput(KeyCode code, boolean isPressed) {
        if (!isDead()) {
            switch (code) {
                case A: setMoving("LEFT", isPressed); break;
                case D: setMoving("RIGHT", isPressed); break;
                case W: setMoving("UP", isPressed); break;
                case S: setMoving("DOWN", isPressed); break;
                case F: if (isPressed) shoot(); break;
            }
        }
    }

    public void addScore(int points) {
        this.score += points;
    }

    public int getScore() {
        return score;
    }

    @Override
    protected void renderHealthBar(GraphicsContext gc) {
        double barWidth = 50;
        double barHeight = 5;
        double healthPercent = (double) health / MAX_HEALTH;
        
        gc.setFill(Color.RED);
        gc.fillRect(x - barWidth/2, y - height/2 - 10, barWidth, barHeight);
        gc.setFill(color);
        gc.fillRect(x - barWidth/2, y - height/2 - 10, barWidth * healthPercent, barHeight);
    }
} 