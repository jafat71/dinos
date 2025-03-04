package com.tanksdinos.tanksdinos;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import java.util.function.Consumer;

public class GameWorld {
    private Tank playerTank;
    private Tank2 player2Tank;  // Nuevo campo para el segundo jugador
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
    private static final double TANK_SPEED = 4.0;
    private static final int POWERUP_SPAWN_DELAY = 15000; // 15 segundos
    private static final int MAX_POWERUPS = 3; // Máximo de power-ups en pantalla
    private long lastPowerUpSpawnTime = 0;
    private boolean isTwoPlayers;
    private static final int MAX_DINOSAURS = 10; // Limitar número máximo de dinosaurios
    private static final int HORDE_SIZE = 10;
    private static final long HORDE_DELAY = 60000; // 1 minuto
    private long lastHordeTime = 0;
    private static final Point2D DINO_SPAWN_POINT = new Point2D(750, 50); // Esquina superior derecha
    private static final Point2D TANK1_SPAWN_POINT = new Point2D(50, 550); // Esquina inferior izquierda
    private static final Point2D TANK2_SPAWN_POINT = new Point2D(150, 550); // Cerca del primer tanque

    public GameWorld(LevelManager levelManager, PowerUpManager powerUpManager, AudioManager audioManager, 
                    boolean isTwoPlayers, Color color1, Color color2) {
        this.levelManager = levelManager;
        this.powerUpManager = powerUpManager;
        this.audioManager = audioManager;
        this.isTwoPlayers = isTwoPlayers;
        
        // Inicializar listas
        dinosaurs = new ArrayList<>();
        powerUps = new ArrayList<>();
        
        // Inicializar tanques con colores específicos
        playerTank = new Tank(TANK1_SPAWN_POINT.getX(), TANK1_SPAWN_POINT.getY(), audioManager, color1);
        if (isTwoPlayers) {
            player2Tank = new Tank2(TANK2_SPAWN_POINT.getX(), TANK2_SPAWN_POINT.getY(), audioManager, color2);
        }

        // Cargar imagen de fondo
        loadBackgroundImage();
        
        // Spawn inicial de dinosaurios
        spawnInitialDinosaurs();
    }

    public void handleInput(KeyCode code, boolean isPressed) {
        // Manejar input del jugador 1
        playerTank.handleInput(code, isPressed);
        
        // Manejar input del jugador 2 si está activo
        if (isTwoPlayers && player2Tank != null) {
            player2Tank.handleInput(code, isPressed);
        }
    }

    public void update() {
        if (Main.gameState == GameState.COUNTDOWN) {
            return;
        }

        // Verificar Game Over solo si ambos jugadores están muertos en modo 2 jugadores
        if (isGameOver()) {
            Main.gameState = GameState.GAME_OVER;
            return;
        }

        // Actualizar jugador 1 si está vivo
        if (!playerTank.isDead()) {
            playerTank.update();
        }

        // Actualizar jugador 2 si existe y está vivo
        if (isTwoPlayers && player2Tank != null && !player2Tank.isDead()) {
            player2Tank.update();
        }

        updateDinosaurs();
        updatePowerUps();
        checkCollisions();
    }

    private void updateDinosaurs() {
        // Actualizar dinosaurios existentes
        dinosaurs.removeIf(dino -> dino.isDead());
        dinosaurs.forEach(Dinosaur::update);

        // Verificar si necesitamos spawner nueva horda
        if (dinosaurs.isEmpty() || 
            System.currentTimeMillis() - lastHordeTime > HORDE_DELAY) {
            spawnHorde();
        }
    }

    private void spawnHorde() {
        audioManager.playDinoRoarSound();
        Dinosaur.increaseSpeed(); // Aumentar velocidad con cada horda
        
        for (int i = 0; i < HORDE_SIZE; i++) {
            double offsetX = random.nextDouble() * 100;
            double offsetY = random.nextDouble() * 100;
            dinosaurs.add(new Dinosaur(
                DINO_SPAWN_POINT.getX() - offsetX,
                DINO_SPAWN_POINT.getY() + offsetY,
                playerTank,
                player2Tank
            ));
        }
        lastHordeTime = System.currentTimeMillis();
    }

    private void updatePowerUps() {
        // Eliminar power-ups recogidos
        powerUps.removeIf(powerUp -> {
            boolean collected = false;
            if (powerUp.intersects(playerTank)) {
                applyPowerUp(powerUp, playerTank);
                collected = true;
            }
            if (isTwoPlayers && player2Tank != null && powerUp.intersects(player2Tank)) {
                applyPowerUp(powerUp, player2Tank);
                collected = true;
            }
            return collected;
        });

        // Verificar si debemos generar nuevos power-ups
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPowerUpSpawnTime > POWERUP_SPAWN_DELAY && powerUps.size() < MAX_POWERUPS) {
            spawnRandomPowerUp();
            lastPowerUpSpawnTime = currentTime;
        }
    }

    private void spawnRandomPowerUp() {
        // Generar posición aleatoria evitando bordes
        double x = 100 + random.nextDouble() * 600; // Entre 100 y 700
        double y = 100 + random.nextDouble() * 400; // Entre 100 y 500

        // Seleccionar tipo aleatorio
        PowerUp.Type[] types = PowerUp.Type.values();
        PowerUp.Type randomType = types[random.nextInt(types.length)];
        
        // Crear y añadir power-up
        PowerUp powerUp = new PowerUp(x, y, randomType);
        powerUps.add(powerUp);
        
        // Efecto de sonido
        audioManager.playPowerUpSpawnSound();
    }

    private void applyPowerUp(PowerUp powerUp, Tank tank) {
        switch (powerUp.getType()) {
            case REPAIR:
                tank.repair(50); // Recupera 50 de vida
                break;
            case SHIELD:
                tank.activateShield(15000); // 15 segundos de escudo
                break;
            case DOUBLE_SHOT:
                tank.activateDoubleShot(20000); // 20 segundos de disparo doble
                break;
        }
        audioManager.playPowerUpCollectSound();
    }

    private boolean isOffscreen(Sprite sprite) {
        return sprite.getX() < -50 || sprite.getX() > 850 || 
               sprite.getY() < -50 || sprite.getY() > 650;
    }

    private void spawnInitialDinosaurs() {
        // Reproducir rugido inicial
        audioManager.playDinoRoarSound();
        
        // Spawn de dinosaurios iniciales
        for (int i = 0; i < 5; i++) {
            double spawnX = DINO_SPAWN_POINT.getX() + random.nextDouble() * 50 - 25;
            double spawnY = DINO_SPAWN_POINT.getY() + random.nextDouble() * 50 - 25;
            
            Dinosaur dino = new Dinosaur(spawnX, spawnY, playerTank, player2Tank);
            dinosaurs.add(dino);
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
        double spawnX = DINO_SPAWN_POINT.getX() + random.nextDouble() * 50 - 25;
        double spawnY = DINO_SPAWN_POINT.getY() + random.nextDouble() * 50 - 25;
        
        Dinosaur dino = new Dinosaur(spawnX, spawnY, playerTank, player2Tank);
        dinosaurs.add(dino);
        
        if (dinosaurs.size() <= 3) {
            audioManager.playDinoRoarSound();
        }
    }

    private void checkCollisions() {
        for (Dinosaur dino : new ArrayList<>(dinosaurs)) {
            // Daño a jugadores vivos
            if (!playerTank.isDead() && dino.intersects(playerTank)) {
                playerTank.takeDamage(Tank.MAX_HEALTH / 5);
            }
            if (isTwoPlayers && player2Tank != null && !player2Tank.isDead() && dino.intersects(player2Tank)) {
                player2Tank.takeDamage(Tank.MAX_HEALTH / 5);
            }

            // Proyectiles de jugadores vivos
            if (!playerTank.isDead()) {
                checkProjectileCollisions(dino, playerTank.getProjectiles(), score -> this.score += score);
            }
            if (isTwoPlayers && player2Tank != null && !player2Tank.isDead()) {
                checkProjectileCollisions(dino, player2Tank.getProjectiles(), player2Tank::addScore);
            }
        }
    }

    private void checkProjectileCollisions(Dinosaur dino, List<Projectile> projectiles, Consumer<Integer> scoreUpdater) {
        for (Projectile proj : new ArrayList<>(projectiles)) {
            if (proj.intersects(dino)) {
                dino.takeDamage(proj.getDamage());
                proj.setDead(true);
                if (dino.isDead()) {
                    scoreUpdater.accept(100);
                }
            }
        }
    }

    public void render(GraphicsContext gc) {
        // Renderizar fondo
        if (backgroundImage != null) {
            gc.drawImage(backgroundImage, 0, 0);
        }

        // Renderizar dinosaurios
        dinosaurs.stream()
                .filter(dino -> !isOffscreen(dino))
                .forEach(dino -> dino.render(gc));

        // Renderizar power-ups
        powerUps.forEach(powerUp -> powerUp.render(gc));

        // Renderizar tanques si están vivos
        if (!playerTank.isDead()) {
            playerTank.render(gc);
        }
        
        if (isTwoPlayers && player2Tank != null && !player2Tank.isDead()) {
            player2Tank.render(gc);
        }

        // Renderizar scores
        renderScores(gc);
    }

    private void renderScores(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 20));
        gc.fillText("P1 Score: " + score, 20, 30);
        
        if (isTwoPlayers && player2Tank != null) {
            gc.setFill(Color.YELLOW);
            gc.fillText("P2 Score: " + player2Tank.getScore(), 650, 30);
        }
    }

    public int getScore() {
        return score;
    }

    private void loadBackgroundImage() {
        try {
            if (backgroundImage == null) {
                backgroundImage = new Image(GameWorld.class.getResourceAsStream("/images/background.jpg"), 
                                         800, 600, true, true); // Usar caching y suavizado
            }
        } catch (Exception e) {
            System.err.println("Error cargando imagen de fondo: " + e.getMessage());
        }
    }

    public boolean isGameOver() {
        if (!isTwoPlayers) {
            return playerTank.isDead();
        }
        return playerTank.isDead() && player2Tank.isDead();
    }

    public String getWinnerMessage() {
        if (!isTwoPlayers) {
            return "Final Score: " + score;
        }
        
        StringBuilder message = new StringBuilder();
        message.append("Player 1 Score: ").append(score).append("\n");
        message.append("Player 2 Score: ").append(player2Tank.getScore()).append("\n\n");
        
        if (playerTank.isDead() && player2Tank.isDead()) {
            message.append("¡Game Over!\n");
            if (score > player2Tank.getScore()) {
                message.append("Player 1 Wins!");
            } else if (player2Tank.getScore() > score) {
                message.append("Player 2 Wins!");
            } else {
                message.append("It's a Tie!");
            }
        } else if (playerTank.isDead()) {
            message.append("Player 1 Eliminated!");
        } else if (player2Tank.isDead()) {
            message.append("Player 2 Eliminated!");
        }
        
        return message.toString();
    }

    public void resetGame() {
        Dinosaur.resetSpeed(); // Resetear velocidad al iniciar nueva partida
        // ... resto del código de reset
    }

    private void checkGameOver() {
        if (isGameOver()) {
            if (!isTwoPlayers) {
                // Guardar puntuación solo en modo un jugador
                UserManager.getInstance().saveScore(score, false);
            }
            Main.gameState = GameState.GAME_OVER;
        }
    }

    public boolean isTwoPlayers() {
        return isTwoPlayers;
    }
}