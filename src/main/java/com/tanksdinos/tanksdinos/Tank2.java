package com.tanksdinos.tanksdinos;

import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.text.Font;
import javafx.scene.effect.BlendMode;

public class Tank2 extends Tank {
    private static final Image tank2Image = new Image(Tank.class.getResourceAsStream("/images/tank.png"));
    private int score = 0;
    
    public Tank2(double x, double y, AudioManager audioManager, Color color) {
        super(x, y, audioManager, color != null ? color : Color.YELLOW);
    }

    @Override
    public void handleInput(KeyCode code, boolean isPressed) {
        switch (code) {
            case A: setMoving("LEFT", isPressed); break;
            case D: setMoving("RIGHT", isPressed); break;
            case W: setMoving("UP", isPressed); break;
            case S: setMoving("DOWN", isPressed); break;
            case F: if (isPressed) shoot(); break;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isDead()) {
            // Guardar estado actual
            gc.save();
            
            // Renderizar imagen base del tanque
            super.render(gc);
            
            // Aplicar color con blend
            gc.setGlobalBlendMode(BlendMode.MULTIPLY);
            gc.setGlobalAlpha(0.6);
            gc.setFill(color);
            gc.fillRect(x - width/2, y - height/2, width, height);
            
            // Restaurar estado
            gc.setGlobalAlpha(1.0);
            gc.setGlobalBlendMode(BlendMode.SRC_OVER);
            gc.restore();
            
            // Renderizar proyectiles y otros elementos
            projectiles.forEach(p -> p.render(gc));
            renderHealthBar(gc);
            
            if (hasShield) {
                renderShield(gc);
            }
        }
        
        gc.setFill(Color.YELLOW);
        gc.setFont(new Font("Arial", 20));
        gc.fillText("P2 Score: " + score, 650, 30);
    }

    @Override
    public void shoot() {
        if (projectiles.size() >= MAX_PROJECTILES) return;
        
        audioManager.playShootSound();
        if (hasDoubleShot) {
            projectiles.add(new Projectile(x - 10, y, shotPower, color));
            projectiles.add(new Projectile(x + 10, y, shotPower, color));
        } else {
            projectiles.add(new Projectile(x, y, shotPower, color));
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