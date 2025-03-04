package com.tanksdinos.tanksdinos;

import javafx.scene.media.MediaPlayer;
import javafx.scene.media.Media;

public class AudioManager {
    private MediaPlayer shootSound;
    private MediaPlayer powerUpSound;
    private MediaPlayer explosionSound;
    private MediaPlayer dinoRoarSound;
    private double volume = 1.0;

    public AudioManager() {
        try {
            String audioPath = AudioManager.class.getResource("/audio").toExternalForm();
            shootSound = new MediaPlayer(new Media(audioPath + "/shoot.wav"));
            powerUpSound = new MediaPlayer(new Media(audioPath + "/powerup.wav"));
            explosionSound = new MediaPlayer(new Media(audioPath + "/explosion.wav"));
            dinoRoarSound = new MediaPlayer(new Media(audioPath + "/roar.wav"));
        } catch (Exception e) {
            System.err.println("Error cargando sonidos: " + e.getMessage());
        }
    }

    public void playShootSound() {
        if (shootSound != null) {
            shootSound.setVolume(volume);
            shootSound.play();
        }
    }

    public void playPowerUpSound() {
        if (powerUpSound != null) {
            powerUpSound.setVolume(volume);
            powerUpSound.play();
        }
    }

    public void playExplosionSound() {
        if (explosionSound != null) {
            explosionSound.setVolume(volume);
            explosionSound.play();
        }
    }

    public void playDinoRoarSound() {
        if (dinoRoarSound != null) {
            dinoRoarSound.setVolume(volume);
            dinoRoarSound.play();
        }
    }

    public void setVolume(double volume) {
        this.volume = Math.max(0.0, Math.min(1.0, volume));
    }
}
