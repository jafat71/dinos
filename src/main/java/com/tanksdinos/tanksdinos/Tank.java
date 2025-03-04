package com.tanksdinos.tanksdinos;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.BlendMode;

public class Tank extends Sprite {
    protected static final double MOVE_SPEED = 3.0;
    protected static final int MAX_HEALTH = 100;
    protected int health;
    protected boolean hasShield;
    protected long shieldEndTime;
    protected boolean hasDoubleShot;
    protected long doubleShotEndTime;
    protected int shotPower;
    protected List<Projectile> projectiles;
    protected AudioManager audioManager;
    protected double velocityX;
    protected double velocityY;
    protected boolean movingLeft, movingRight, movingUp, movingDown;
    protected double baseSpeed = 4.0;
    private double speedMultiplier = 1.0;
    protected static final int MAX_PROJECTILES = 5;
    private static final Image shieldImage = new Image(Tank.class.getResourceAsStream("/images/shield.png"), 60, 60, true, true); // Precarga y redimensiona
    protected Color color;

    public Tank(double x, double y, AudioManager audioManager, Color color) {
        super(x, y, new Image(Tank.class.getResourceAsStream("/images/tank.png")), 50, 50);
        this.health = MAX_HEALTH;
        this.projectiles = new ArrayList<>();
        this.audioManager = audioManager;
        this.shotPower = 1;
        this.color = color != null ? color : Color.GREEN;
    }

    public void handlePowerUp(PowerUp powerUp) {
        switch (powerUp.getType()) {
            case REPAIR:
                health = Math.min(health + 50, MAX_HEALTH);
                break;
            case SHIELD:
                hasShield = true;
                shieldEndTime = System.currentTimeMillis();
                break;
            case DOUBLE_SHOT:
                hasDoubleShot = true;
                doubleShotEndTime = System.currentTimeMillis();
                break;
        }
        audioManager.playPowerUpSound();
    }

    public void shoot() {
        if (projectiles.size() >= MAX_PROJECTILES) return;
        
        audioManager.playShootSound();
        if (hasDoubleShot) {
            projectiles.add(new Projectile(x - 10, y, shotPower));
            projectiles.add(new Projectile(x + 10, y, shotPower));
        } else {
            projectiles.add(new Projectile(x, y, shotPower));
        }
    }

    @Override
    public void update() {
        if (!isDead()) {
            updateMovement();
            updatePowerUps();
            updateProjectiles();
        }
    }

    private void updateMovement() {
        if (!isDead()) {
            double currentSpeed = baseSpeed * speedMultiplier;
            
            // Resetear velocidades
            velocityX = 0;
            velocityY = 0;
            
            if (movingLeft) velocityX -= currentSpeed;
            if (movingRight) velocityX += currentSpeed;
            if (movingUp) velocityY -= currentSpeed;
            if (movingDown) velocityY += currentSpeed;

            // Normalizar diagonal
            if ((movingLeft || movingRight) && (movingUp || movingDown)) {
                velocityX *= 0.707;
                velocityY *= 0.707;
            }

            // Actualizar posición solo si no está muerto
            double nextX = x + velocityX;
            double nextY = y + velocityY;

            // Mantener dentro de los límites
            if (nextX >= 50 && nextX <= 750) x = nextX;
            if (nextY >= 50 && nextY <= 550) y = nextY;
        }
    }

    private void updatePowerUps() {
        long currentTime = System.currentTimeMillis();
        if (hasShield && currentTime > shieldEndTime) {
            hasShield = false;
        }
        if (hasDoubleShot && currentTime > doubleShotEndTime) {
            hasDoubleShot = false;
        }
    }

    public void setMoving(String direction, boolean moving) {
        switch (direction) {
            case "LEFT": movingLeft = moving; break;
            case "RIGHT": movingRight = moving; break;
            case "UP": movingUp = moving; break;
            case "DOWN": movingDown = moving; break;
        }
    }

    public void applySpeedBoost() {
        speedMultiplier = 1.5;
        // Volver a la velocidad normal después de 10 segundos
        new Thread(() -> {
            try {
                Thread.sleep(10000);
                speedMultiplier = 1.0;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
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
    }

    protected void renderHealthBar(GraphicsContext gc) {
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

    public void handleInput(KeyCode code, boolean isPressed) {
        if (!isDead()) {
            switch (code) {
                case LEFT: setMoving("LEFT", isPressed); break;
                case RIGHT: setMoving("RIGHT", isPressed); break;
                case UP: setMoving("UP", isPressed); break;
                case DOWN: setMoving("DOWN", isPressed); break;
                case SPACE: if (isPressed) shoot(); break;
            }
        }
    }

    public void repair(int amount) {
        health = Math.min(health + amount, MAX_HEALTH);
    }

    public void activateShield(int duration) {
        hasShield = true;
        shieldEndTime = System.currentTimeMillis() + duration;
    }

    public void activateDoubleShot(int duration) {
        hasDoubleShot = true;
        doubleShotEndTime = System.currentTimeMillis() + duration;
    }

    private void updateProjectiles() {
        projectiles.removeIf(Projectile::isDead);
        projectiles.forEach(Projectile::update);
    }

    protected void renderShield(GraphicsContext gc) {
        gc.setGlobalAlpha(0.4);  // Más transparente
        gc.drawImage(shieldImage, 
            x - width/2 - 5,     // Ajustar posición
            y - height/2 - 5);   // para centrar mejor
        gc.setGlobalAlpha(1.0);
    }
}
