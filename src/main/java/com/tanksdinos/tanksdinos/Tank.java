package com.tanksdinos.tanksdinos;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.List;

public class Tank extends Sprite {
    private static final double MOVE_SPEED = 3.0;
    private static final int MAX_HEALTH = 100;
    private int health;
    private boolean hasShield;
    private long shieldStartTime;
    private int shotPower;
    private boolean doubleShot;
    private List<Projectile> projectiles;
    private AudioManager audioManager;
    private double velocityX;
    private double velocityY;

    public Tank(double x, double y, AudioManager audioManager) {
        super(x, y, new Image(Tank.class.getResourceAsStream("/images/tank.png")), 50, 50);
        this.health = MAX_HEALTH;
        this.projectiles = new ArrayList<>();
        this.audioManager = audioManager;
        this.shotPower = 1;
    }

    public void handlePowerUp(PowerUp powerUp) {
        switch (powerUp.getType()) {
            case REPAIR:
                health = Math.min(health + 50, MAX_HEALTH);
                break;
            case SHIELD:
                hasShield = true;
                shieldStartTime = System.currentTimeMillis();
                break;
            case DOUBLE_SHOT:
                doubleShot = true;
                break;
            case POWER_SHOT:
                shotPower = 2;
                break;
        }
        audioManager.playPowerUpSound();
    }

    public void shoot() {
        audioManager.playShootSound();
        if (doubleShot) {
            projectiles.add(new Projectile(x - 10, y, shotPower));
            projectiles.add(new Projectile(x + 10, y, shotPower));
        } else {
            projectiles.add(new Projectile(x, y, shotPower));
        }
    }

    @Override
    public void update() {
        super.update();  // Esto actualizará la posición basada en la velocidad
        
        // Actualizar proyectiles
        projectiles.removeIf(Projectile::isDead);
        projectiles.forEach(Projectile::update);

        // Actualizar escudo
        if (hasShield && System.currentTimeMillis() - shieldStartTime > 10000) {
            hasShield = false;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        super.render(gc);
        
        // Renderizar proyectiles
        projectiles.forEach(p -> p.render(gc));

        // Renderizar escudo si está activo
        if (hasShield) {
            gc.setGlobalAlpha(0.5);
            gc.drawImage(new Image(Tank.class.getResourceAsStream("/images/shield.png")), x - width/2, y - height/2);
            gc.setGlobalAlpha(1.0);
        }

        // Renderizar barra de vida
        renderHealthBar(gc);
    }

    private void renderHealthBar(GraphicsContext gc) {
        double barWidth = 50;
        double barHeight = 5;
        double healthPercent = (double) health / MAX_HEALTH;
        
        gc.setFill(javafx.scene.paint.Color.RED);
        gc.fillRect(x - barWidth/2, y - height/2 - 10, barWidth, barHeight);
        gc.setFill(javafx.scene.paint.Color.GREEN);
        gc.fillRect(x - barWidth/2, y - height/2 - 10, barWidth * healthPercent, barHeight);
    }

    public List<Projectile> getProjectiles() {
        return projectiles;
    }

    public void takeDamage(int damage) {
        if (!hasShield) {
            health -= damage;
        }
    }

    public boolean isDead() {
        return health <= 0;
    }

    public double getVelocityX() { return velocityX; }
    public double getVelocityY() { return velocityY; }
}
