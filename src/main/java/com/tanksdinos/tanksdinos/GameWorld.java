package com.tanksdinos.tanksdinos;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import java.util.ArrayList;
import java.util.List;

public class GameWorld {
    private LevelManager levelManager;
    private PowerUpManager powerUpManager;
    private AudioManager audioManager;
    private int score = 0;
    private Tank playerTank;
    private List<Dinosaur> dinosaurs;

    public GameWorld(LevelManager levelManager, PowerUpManager powerUpManager, AudioManager audioManager) {
        this.levelManager = levelManager;
        this.powerUpManager = powerUpManager;
        this.audioManager = audioManager;
        playerTank = new Tank(400, 300);
        dinosaurs = new ArrayList<>();
        
        // Add some initial dinosaurs
        for (int i = 0; i < 5; i++) {
            dinosaurs.add(new Dinosaur(Math.random() * 800, Math.random() * 600, playerTank));
        }
    }

    public int getScore() {
        return score;
    }

    public void handleInput(KeyCode code) {
        playerTank.handleInput(code);
    }

    public void update() {
        playerTank.update();
        
        for (Dinosaur dino : dinosaurs) {
            dino.update();
        }

        // Check collisions
        checkCollisions();
    }

    private void checkCollisions() {
        // Implement collision detection between tank and dinosaurs
    }

    public void render(GraphicsContext gc) {
        playerTank.render(gc);
        for (Dinosaur dino : dinosaurs) {
            dino.render(gc);
        }
    }
}