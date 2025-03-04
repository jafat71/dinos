package com.tanksdinos.tanksdinos;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameWorld {
    private Tank playerTank;
    private List<Dinosaur> dinosaurs;
    private List<PowerUp> powerUps;
    private LevelManager levelManager;
    private PowerUpManager powerUpManager;
    private AudioManager audioManager;
    private Image backgroundImage;
    private int score = 0;
    private Random random = new Random();
    private static final int SPAWN_DELAY = 5000; // 5 segundos
    private long lastSpawnTime = 0;
    private static final double TANK_SPEED = 5.0;

    public GameWorld(LevelManager levelManager, PowerUpManager powerUpManager, AudioManager audioManager) {
        this.levelManager = levelManager;
        this.powerUpManager = powerUpManager;
        this.audioManager = audioManager;
        
        // Inicializar listas y objetos
        playerTank = new Tank(400, 500, audioManager);
        dinosaurs = new ArrayList<>();
        powerUps = new ArrayList<>();
        
        try {
            backgroundImage = new Image(GameWorld.class.getResourceAsStream("/images/background.jpg"));
        } catch (Exception e) {
            System.err.println("Error cargando imagen de fondo: " + e.getMessage());
        }

        // Spawn inicial de dinosaurios
        spawnInitialDinosaurs();
    }

    public void handleInput(KeyCode code, boolean isPressed) {
        switch (code) {
            case LEFT:
                playerTank.setVelocity(isPressed ? -TANK_SPEED : 0, playerTank.getVelocityY());
                break;
            case RIGHT:
                playerTank.setVelocity(isPressed ? TANK_SPEED : 0, playerTank.getVelocityY());
                break;
            case UP:
                playerTank.setVelocity(playerTank.getVelocityX(), isPressed ? -TANK_SPEED : 0);
                break;
            case DOWN:
                playerTank.setVelocity(playerTank.getVelocityX(), isPressed ? TANK_SPEED : 0);
                break;
            case SPACE:
                if (isPressed) playerTank.shoot();
                break;
        }
    }

    public void update() {
        // Actualizar tanque
        playerTank.update();
        
        // Mantener el tanque dentro de los límites
        keepTankInBounds();

        // Actualizar dinosaurios
        updateDinosaurs();

        // Actualizar power-ups
        updatePowerUps();

        // Comprobar colisiones
        checkCollisions();

        // Spawn de nuevos dinosaurios
        checkDinosaurSpawn();
    }

    private void keepTankInBounds() {
        if (playerTank.getX() < 50) playerTank.setX(50);
        if (playerTank.getX() > 750) playerTank.setX(750);
        if (playerTank.getY() < 50) playerTank.setY(50);
        if (playerTank.getY() > 550) playerTank.setY(550);
    }

    private void updateDinosaurs() {
        dinosaurs.removeIf(dino -> dino.isDead());
        for (Dinosaur dino : dinosaurs) {
            dino.update();
        }
    }

    private void updatePowerUps() {
        powerUps.removeIf(powerUp -> {
            if (powerUp.intersects(playerTank)) {
                playerTank.handlePowerUp(powerUp);
                return true;
            }
            return false;
        });
    }

    private void checkCollisions() {
        // Colisiones entre proyectiles y dinosaurios
        for (Projectile projectile : playerTank.getProjectiles()) {
            for (Dinosaur dino : dinosaurs) {
                if (!projectile.isDead() && projectile.intersects(dino)) {
                    dino.takeDamage(projectile.getDamage());
                    projectile.setDead(true);
                    if (dino.isDead()) {
                        score += 100;
                        audioManager.playExplosionSound();
                        spawnPowerUp(dino.getX(), dino.getY());
                    }
                }
            }
        }

        // Colisiones entre dinosaurios y tanque
        for (Dinosaur dino : dinosaurs) {
            if (dino.intersects(playerTank)) {
                playerTank.takeDamage(10);
                if (playerTank.isDead()) {
                    Main.gameState = GameState.GAME_OVER;
                }
            }
        }
    }

    private void spawnPowerUp(double x, double y) {
        if (random.nextDouble() < 0.3) { // 30% de probabilidad
            PowerUp.Type[] types = PowerUp.Type.values();
            PowerUp.Type randomType = types[random.nextInt(types.length)];
            powerUps.add(new PowerUp(x, y, randomType));
        }
    }

    private void spawnInitialDinosaurs() {
        for (int i = 0; i < 5; i++) {
            spawnDinosaur();
        }
    }

    private void checkDinosaurSpawn() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSpawnTime > SPAWN_DELAY) {
            spawnDinosaur();
            lastSpawnTime = currentTime;
        }
    }

    private void spawnDinosaur() {
        double x = random.nextDouble() * 700 + 50;
        double y = random.nextDouble() * 200 + 50;
        dinosaurs.add(new Dinosaur(x, y, playerTank));
        audioManager.playDinoRoarSound();
    }

    public void render(GraphicsContext gc) {
        // Renderizar fondo
        if (backgroundImage != null) {
            gc.drawImage(backgroundImage, 0, 0, 800, 600);
        }

        // Renderizar power-ups
        for (PowerUp powerUp : powerUps) {
            powerUp.render(gc);
        }

        // Renderizar dinosaurios
        for (Dinosaur dino : dinosaurs) {
            dino.render(gc);
        }

        // Renderizar tanque
        playerTank.render(gc);

        // Renderizar score
        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.fillText("Score: " + score, 20, 30);
    }

    public int getScore() {
        return score;
    }
}