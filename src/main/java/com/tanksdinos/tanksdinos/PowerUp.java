package com.tanksdinos.tanksdinos;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class PowerUp extends Sprite {
    public enum Type {
        REPAIR("Repair", Color.GREEN),
        SHIELD("Shield", Color.BLUE),
        DOUBLE_SHOT("Double Shot", Color.RED);

        private final String name;
        private final Color color;

        Type(String name, Color color) {
            this.name = name;
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public Color getColor() {
            return color;
        }
    }

    private static final Image powerUpImage = new Image(PowerUp.class.getResourceAsStream("/images/present.png"));
    private final Type type;
    private static final int SIZE = 30;

    public PowerUp(double x, double y, Type type) {
        super(x, y, powerUpImage, SIZE, SIZE);
        this.type = type;
    }

    @Override
    public void render(GraphicsContext gc) {
        // Renderizar imagen base
        super.render(gc);
        
        // Añadir efecto de brillo
        gc.setGlobalAlpha(0.3);
        gc.setFill(type.color);
        gc.fillOval(x - width/2, y - height/2, width, height);
        gc.setGlobalAlpha(1.0);
    }

    public Type getType() {
        return type;
    }
}
