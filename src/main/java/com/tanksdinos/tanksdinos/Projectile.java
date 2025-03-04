package com.tanksdinos.tanksdinos;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Projectile extends Sprite {
    private static final double SPEED = 5.0;
    private static final Image projectileImage = new Image(Projectile.class.getResourceAsStream("/images/bullet.png"));
    private boolean dead = false;
    private int damage;
    private static final int BASE_DAMAGE = 25;
    private Color color;

    public Projectile(double x, double y, int powerMultiplier) {
        this(x, y, BASE_DAMAGE * powerMultiplier, Color.RED);
    }

    public Projectile(double x, double y, int damage, Color color) {
        super(x, y, null, 10, 10);
        this.damage = damage;
        this.color = color;
        this.velocityY = -SPEED;
    }

    @Override
    public void update() {
        super.update();
        
        // Marcar como muerto si sale de la pantalla
        if (y < -height || y > 600 || x < -width || x > 800) {
            dead = true;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!dead) {
            gc.setFill(color);
            gc.fillOval(x - width/2, y - height/2, width, height);
        }
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public int getDamage() {
        return damage;
    }
}
