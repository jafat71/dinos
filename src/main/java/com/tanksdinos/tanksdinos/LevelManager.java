package com.tanksdinos.tanksdinos;

import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.geometry.Point2D;

public class LevelManager {
    private List<Point2D> spawnPoints;
    private Random random;

    public LevelManager() {
        spawnPoints = new ArrayList<>();
        random = new Random();
        setupSpawnPoints();
    }

    private void setupSpawnPoints() {
        // Puntos de spawn en los bordes del mapa
        for (int i = 0; i < 4; i++) {
            spawnPoints.add(new Point2D(50 + random.nextInt(700), 50));  // Arriba
            spawnPoints.add(new Point2D(50 + random.nextInt(700), 550)); // Abajo
            spawnPoints.add(new Point2D(50, 50 + random.nextInt(500)));  // Izquierda
            spawnPoints.add(new Point2D(750, 50 + random.nextInt(500))); // Derecha
        }
    }

    public void render(GraphicsContext gc) {
        // Ya no renderizamos bloques
    }

    public List<Point2D> getSpawnPoints() {
        return spawnPoints;
    }

    public boolean checkCollision(Sprite sprite) {
        return false; // Ya no hay colisiones con bloques
    }
}
