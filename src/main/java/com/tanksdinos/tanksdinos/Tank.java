package com.tanksdinos.tanksdinos;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyCode;

public class Tank extends GameObject {
    private static final double SPEED = 5.0;
    private static final double ROTATION = 3.0;
    private double angle;

    public Tank(double x, double y) {
        super(x, y, 40, 60);
        this.angle = 0;
    }

    public void handleInput(KeyCode code) {
        switch (code) {
            case LEFT:
                angle -= ROTATION;
                break;
            case RIGHT:
                angle += ROTATION;
                break;
            case UP:
                moveForward();
                break;
            case SPACE:
                shoot();
                break;
        }
    }

    private void moveForward() {
        double rad = Math.toRadians(angle);
        velocityX = Math.cos(rad) * SPEED;
        velocityY = Math.sin(rad) * SPEED;
    }

    private void shoot() {
        // Implement shooting mechanism
    }

    @Override
    public void update() {
        x += velocityX;
        y += velocityY;
        velocityX *= 0.95; // friction
        velocityY *= 0.95;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.save();
        gc.translate(x + width/2, y + height/2);
        gc.rotate(angle);
        gc.setFill(Color.GREEN);
        gc.fillRect(-width/2, -height/2, width, height);
        gc.restore();
    }
}
