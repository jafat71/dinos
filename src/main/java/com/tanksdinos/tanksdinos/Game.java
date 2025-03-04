package com.tanksdinos.tanksdinos;

public class Game {
    private static Game instance;
    private GameState currentState = GameState.MENU;

    private Game() {}

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public void startGame() {
        currentState = GameState.PLAYING;
        // Initialize game components here
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public enum GameState {
        MENU,
        PLAYING,
        PAUSED,
        GAME_OVER
    }
}