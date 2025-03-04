package com.tanksdinos.tanksdinos;

import java.util.List;
import java.util.ArrayList;

public class User {
    private String username;
    private String password;
    private List<Score> scores;
    private boolean isGuest;

    public User(String username, String password, List<Score> scores) {
        this(username, password, scores, false);
    }

    public User(String username, String password, List<Score> scores, boolean isGuest) {
        this.username = username;
        this.password = password;
        this.scores = scores != null ? scores : new ArrayList<>();
        this.isGuest = isGuest;
    }

    public void addScore(Score score) {
        if (!isGuest) {
            scores.add(score);
        }
    }

    public String getUsername() { return username; }
    public List<Score> getScores() { return scores; }
    public boolean isGuest() { return isGuest; }
    public String getPassword() {
        return password;
    }
} 