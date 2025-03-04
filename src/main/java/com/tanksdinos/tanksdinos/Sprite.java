package com.tanksdinos.tanksdinos;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;

public abstract class Sprite {
    protected double x;
    protected double y;
    protected double velocityX;
    protected double velocityY;
    protected double width;
    protected double height;
    protected Image image;
    private double lastX, lastY;
    protected Color color;

    public Sprite(double x, double y, Image image, double width, double height) {
        this.x = x;
        this.y = y;
        this.image = image;
        this.width = width;
        this.height = height;
        this.velocityX = 0;
        this.velocityY = 0;
        this.color = Color.WHITE;
    }

    public void setVelocity(double x, double y) {
        velocityX = x;
        velocityY = y;
    }

    public void addVelocity(double x, double y) {
        velocityX += x;
        velocityY += y;
    }

    public void update() {
        lastX = x;
        lastY = y;
        x += velocityX;
        y += velocityY;
    }

    public void render(GraphicsContext gc) {
        gc.drawImage(image, x - width/2, y - height/2, width, height);
    }

    public Rectangle2D getBoundary() {
        return new Rectangle2D(x - width/2, y - height/2, width, height);
    }

    public boolean intersects(Sprite other) {
        return other.getBoundary().intersects(this.getBoundary());
    }

    public void undoLastMove() {
        x = lastX;
        y = lastY;
    }

    // Getters y setters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
