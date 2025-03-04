package com.tanksdinos.tanksdinos;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Block extends Sprite {
    private static final Image blockImage = new Image(Block.class.getResourceAsStream("/images/house.png"));
    private boolean destructible;
    private int health;

    public Block(double x, double y, boolean destructible) {
        super(x, y, blockImage, 60, 60);  // Tamaño ajustado para la casa
        this.destructible = destructible;
        this.health = destructible ? 50 : Integer.MAX_VALUE;
    }

    public void takeDamage(int damage) {
        if (destructible) {
            health -= damage;
        }
    }

    public boolean isDestroyed() {
        return health <= 0;
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isDestroyed()) {
            super.render(gc);
            if (destructible && health < 50) {
                // Efecto de daño
                gc.setGlobalAlpha(0.3);
                gc.setFill(Color.RED);
                gc.fillRect(x - width/2, y - height/2, width, height);
                gc.setGlobalAlpha(1.0);
            }
        }
    }
} 