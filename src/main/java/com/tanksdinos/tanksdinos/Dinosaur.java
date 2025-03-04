package com.tanksdinos.tanksdinos;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Dinosaur extends Sprite {
    private static final double SPEED = 2.0;
    private static final int MAX_HEALTH = 50;
    private static final Image dinoImage = new Image(Dinosaur.class.getResourceAsStream("/images/dino.png"));
    
    private Tank target;
    private int health;
    private boolean dead = false;

    public Dinosaur(double x, double y, Tank target) {
        super(x, y, dinoImage);
        this.target = target;
        this.health = MAX_HEALTH;
    }

    @Override
    public void update() {
        if (!dead) {
            // Movimiento hacia el tanque
            double dx = target.getX() - x;
            double dy = target.getY() - y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            
            if (distance > 0) {
                velocityX = (dx / distance) * SPEED;
                velocityY = (dy / distance) * SPEED;
            }
            
            super.update();
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!dead) {
            super.render(gc);
            
            // Renderizar barra de vida
            double healthPercent = (double) health / MAX_HEALTH;
            double barWidth = 40;
            double barHeight = 4;
            
            gc.setFill(javafx.scene.paint.Color.RED);
            gc.fillRect(x - barWidth/2, y - height/2 - 10, barWidth, barHeight);
            gc.setFill(javafx.scene.paint.Color.GREEN);
            gc.fillRect(x - barWidth/2, y - height/2 - 10, barWidth * healthPercent, barHeight);
        }
    }

    public void takeDamage(int damage) {
        if (!dead) {
            health -= damage;
            if (health <= 0) {
                dead = true;
            }
        }
    }

    public boolean isDead() {
        return dead;
    }
}