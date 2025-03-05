package com.tanksdinos.tanksdinos;

import javafx.scene.media.AudioClip;
import javafx.scene.media.MediaPlayer;

public class AudioManager {
    private AudioClip shootSound;
    private AudioClip explosionSound;
    private AudioClip dinoRoarSound;
    private AudioClip powerUpSound;
    private double volume = 1.0;
    private double currentVolume = 1.0;

    public AudioManager() {
        try {
            // Cargar sonidos usando la ruta correcta
            shootSound = new AudioClip(getClass().getResource("/audio/shoot.wav").toExternalForm());
            explosionSound = new AudioClip(getClass().getResource("/audio/explosion.wav").toExternalForm());
            dinoRoarSound = new AudioClip(getClass().getResource("/audio/dino.mp3").toExternalForm());
            powerUpSound = new AudioClip(getClass().getResource("/audio/powerup.wav").toExternalForm());
        } catch (Exception e) {
            System.err.println("Error cargando sonidos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void playShootSound() {
        if (shootSound != null) {
            shootSound.play(volume);
        }
    }

    public void playExplosionSound() {
        if (explosionSound != null) {
            explosionSound.play(volume);
        }
    }

    public void playDinoRoarSound() {
        if (dinoRoarSound != null) {
            dinoRoarSound.play(volume);
        }
    }

    public void playPowerUpSound() {
        if (powerUpSound != null) {
            powerUpSound.play(volume);
        }
    }

    // Alias para mantener compatibilidad
    public void playPowerUpSpawnSound() {
        playPowerUpSound();
    }

    public void playPowerUpCollectSound() {
        playPowerUpSound();
    }

    public void setVolume(double volume) {
        //  0.0 y 1.0
        this.volume = Math.max(0.0, Math.min(1.0, volume));
        
        // Guarda el nivel de volumen actual
        this.currentVolume = this.volume;
    }
}
