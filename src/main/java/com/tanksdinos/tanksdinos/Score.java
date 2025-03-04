package com.tanksdinos.tanksdinos;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Score {
    private String username;
    private int score;
    private long date;
    private boolean twoPlayers;

    public Score(String username, int score, long date, boolean twoPlayers) {
        this.username = username;
        this.score = score;
        this.date = date;
        this.twoPlayers = twoPlayers;
    }

    public String getUsername() { return username; }
    public int getScore() { return score; }
    public long getDate() { return date; }
    public boolean isTwoPlayers() { return twoPlayers; }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(new Date(date));
    }
} 