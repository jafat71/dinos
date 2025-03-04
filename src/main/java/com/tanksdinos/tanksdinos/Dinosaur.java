package com.tanksdinos.tanksdinos;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Dinosaur extends GameObject {
    private static final double SPEED = 2.0;
    private Tank target;

    public Dinosaur(double x, double y, Tank target) {
        super(x, y, 50, 50);
        this.target = target;
    }

    @Override
    public void update() {
        // Simple AI to follow the tank
        double dx = target.x - x;
        double dy = target.y - y;
        double distance = Math.sqrt(dx*dx + dy*dy);
        
        if (distance > 0) {
            velocityX = (dx/distance) * SPEED;
            velocityY = (dy/distance) * SPEED;
        }

        x += velocityX;
        y += velocityY;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.RED);
        gc.fillRect(x, y, width, height);
    }
}