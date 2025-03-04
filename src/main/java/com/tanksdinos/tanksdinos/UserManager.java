package com.tanksdinos.tanksdinos;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private static final String USERS_FILE = "users.txt";
    private static final String SCORES_FILE = "scores.txt";
    private User currentUser;
    private static UserManager instance;
    private Map<String, UserData> users = new HashMap<>();
    private DatabaseManager db;

    private static class UserData {
        String password;
        List<Score> scores = new ArrayList<>();
    }

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    private UserManager() {
        db = DatabaseManager.getInstance();
        loadUsers();
        loadSession();
    }

    private void loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            saveUsers();
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    UserData userData = new UserData();
                    userData.password = parts[1];
                    userData.scores = loadUserScores(parts[0]);
                    users.put(parts[0], userData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveUsers() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (Map.Entry<String, UserData> entry : users.entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue().password);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Score> loadUserScores(String username) {
        List<Score> scores = new ArrayList<>();
        File file = new File(SCORES_FILE);
        if (!file.exists()) return scores;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[0].equals(username)) {
                    scores.add(new Score(
                        username,
                        Integer.parseInt(parts[1]),
                        Long.parseLong(parts[2]),
                        Boolean.parseBoolean(parts[3])
                    ));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scores;
    }

    private void saveSession() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("session.txt"))) {
            if (currentUser != null && !currentUser.isGuest()) {
                writer.println(currentUser.getUsername() + "," + currentUser.getPassword());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSession() {
        File file = new File("session.txt");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine();
                if (line != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        login(parts[0], parts[1]);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean login(String username, String password) {
        if (db.authenticateUser(username, password)) {
            List<Score> scores = db.getUserScores(username);
            currentUser = new User(username, password, scores);
            return true;
        }
        return false;
    }

    public boolean register(String username, String password) {
        if (db.registerUser(username, password)) {
            currentUser = new User(username, password, new ArrayList<>());
            return true;
        }
        return false;
    }

    public void saveScore(int score, boolean isTwoPlayers) {
        if (currentUser != null && !currentUser.isGuest()) {
            db.saveScore(currentUser.getUsername(), score, isTwoPlayers);
        }
    }

    public void loginAsGuest() {
        currentUser = new User("Guest", "", new ArrayList<>(), true);
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public List<Score> getHighScores() {
        return db.getHighScores();
    }
} 