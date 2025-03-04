package com.tanksdinos.tanksdinos;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class PowerUp extends Sprite {
    public enum Type {
        REPAIR,      // Repara el tanque
        SHIELD,      // Escudo temporal
        DOUBLE_SHOT, // Disparo doble
        POWER_SHOT   // Disparo más potente
    }

    private Type type;
    private static final Image powerUpImage = new Image(PowerUp.class.getResourceAsStream("/images/powerup.png"));

    public PowerUp(double x, double y, Type type) {
        super(x, y, powerUpImage);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public void render(GraphicsContext gc) {
        super.render(gc);
        // Aquí podrías añadir efectos visuales adicionales
    }
}
