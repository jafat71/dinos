package com.tanksdinos.tanksdinos;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Projectile extends Sprite {
    private static final double SPEED = 5.0;
    private static final Image projectileImage = new Image(Projectile.class.getResourceAsStream("/images/bullet.png"));
    private boolean dead = false;
    private int damage;
    private static final int BASE_DAMAGE = 10;

    public Projectile(double x, double y, int powerMultiplier) {
        super(x, y, projectileImage);
        this.velocityY = -SPEED;  // Proyectil se mueve hacia arriba
        this.damage = BASE_DAMAGE * powerMultiplier;
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
            super.render(gc);
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
