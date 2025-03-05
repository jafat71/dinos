package com.tanksdinos.tanksdinos;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.geometry.Point2D;
import java.util.Random;

public class Dinosaur extends Sprite {
    private static final double WANDER_SPEED = 1.0;
    private static final double CHASE_SPEED = 2.0;
    private static final double DETECTION_RANGE = 200;
    private static final int MAX_HEALTH = 50;
    private static final Image dinoImage = new Image(Dinosaur.class.getResourceAsStream("/images/dino.png"));
    
    private Tank currentTarget;
    private Tank player1;
    private Tank player2;
    private int health = 100;
    private boolean dead = false;
    private Point2D wanderTarget;
    private boolean isChasing = false;
    private Random random = new Random();
    private static double speedMultiplier = 1.0;
    private static double baseSpeed = 1.0;
    private static final double SPEED_INCREMENT = 0.2;
    private double speed;

    public Dinosaur(double x, double y, Tank player1, Tank player2) {
        super(x, y, dinoImage, 40, 40);
        this.player1 = player1;
        this.player2 = player2;
        this.health = MAX_HEALTH;
        updateTarget();
        
        double initialSpeed = baseSpeed + random.nextDouble() * 0.5;
        this.velocityX = initialSpeed;
        this.velocityY = initialSpeed;
    }

    private void updateTarget() {
        if (player2 == null || player2.isDead()) {
            checkPlayerDistance(player1);
        } else {
            double dist1 = getDistanceTo(player1);
            double dist2 = getDistanceTo(player2);
            if (dist1 < dist2) {
                checkPlayerDistance(player1);
            } else {
                checkPlayerDistance(player2);
            }
        }
    }

    private void checkPlayerDistance(Tank player) {
        double distance = getDistanceTo(player);
        isChasing = distance < DETECTION_RANGE;
        currentTarget = player;
    }

    private void wander() {
        if (wanderTarget == null || getDistanceTo(wanderTarget.getX(), wanderTarget.getY()) < 10) {
            // Generar nuevo punto aleatorio para vagar
            wanderTarget = new Point2D(
                100 + random.nextDouble() * 600,
                100 + random.nextDouble() * 400
            );
        }

        // Moverse hacia el punto de vagabundeo
        double dx = wanderTarget.getX() - x;
        double dy = wanderTarget.getY() - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 0) {
            velocityX = (dx / distance) * baseSpeed;
            velocityY = (dy / distance) * baseSpeed;
        }
    }

    private void chaseTarget() {
        double dx = currentTarget.getX() - x;
        double dy = currentTarget.getY() - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 0) {
            velocityX = (dx / distance) * (baseSpeed * 1.5);
            velocityY = (dy / distance) * (baseSpeed * 1.5);
        }
    }

    private double getDistanceTo(Tank tank) {
        double dx = tank.getX() - x;
        double dy = tank.getY() - y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private double getDistanceTo(double targetX, double targetY) {
        double dx = targetX - x;
        double dy = targetY - y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public void update() {
        if (!isDead()) {
            updateTarget();
            
            if (isChasing) {
                chaseTarget();
            } else {
                wander();
            }
            
            // Movimiento existente multiplicado por el speedMultiplier
            double actualSpeed = baseSpeed * speedMultiplier;
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
        health -= damage;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public static void increaseSpeed() {
        baseSpeed *= 1.25;
        System.out.println("¡Nueva horda! Velocidad de dinosaurios aumentada a: " + baseSpeed);
    }

    public static void resetSpeed() {
        baseSpeed = 1.0;
    }

    public static double getBaseSpeed() {
        return baseSpeed;
    }
}