package com.tanksdinos.tanksdinos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:data/game.db";
    private static DatabaseManager instance;
    
    private DatabaseManager() {
        try {
            // Asegurar que existe el directorio
            new File("data").mkdirs();
            
            // Registrar el driver
            Class.forName("org.sqlite.JDBC");
            
            initializeDatabase();
        } catch (ClassNotFoundException e) {
            System.err.println("Error al cargar el driver SQLite: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // Crear tabla de usuarios si no existe
            conn.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS users (" +
                "username TEXT PRIMARY KEY," +
                "password TEXT NOT NULL" +
                ")"
            );
            
            // Crear tabla de puntuaciones si no existe
            conn.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS scores (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT," +
                "score INTEGER," +
                "date INTEGER," +
                "two_players BOOLEAN," +
                "FOREIGN KEY(username) REFERENCES users(username)" +
                ")"
            );
        } catch (SQLException e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean authenticateUser(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getString("password").equals(password);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean registerUser(String username, String password) {
        // Primero verificar si el usuario ya existe
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement checkStmt = conn.prepareStatement(
                "SELECT username FROM users WHERE username = ?"
            );
            checkStmt.setString(1, username);
            if (checkStmt.executeQuery().next()) {
                return false; // Usuario ya existe
            }

            // Si no existe, proceder con el registro
            PreparedStatement insertStmt = conn.prepareStatement(
                "INSERT INTO users(username, password) VALUES(?, ?)"
            );
            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void saveScore(String username, int score, boolean isTwoPlayers) {
        // Solo guardar para el ranking si es modo un jugador
        String sql = "INSERT INTO scores(username, score, date, two_players) VALUES(?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setInt(2, score);
            pstmt.setLong(3, System.currentTimeMillis());
            pstmt.setBoolean(4, isTwoPlayers);
            pstmt.executeUpdate();
            
            // Mensaje de información
            if (isTwoPlayers) {
                System.out.println("Nota: Las puntuaciones en modo dos jugadores no cuentan para el ranking principal.");
            } else {
                System.out.println("¡Puntuación guardada en el ranking principal!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Score> getHighScores() {
        List<Score> scores = new ArrayList<>();
        // Modificar para solo mostrar puntuaciones de un jugador
        String sql = "SELECT username, score, date, two_players " +
                     "FROM scores " + 
                     "WHERE two_players = 0 " +  // Solo modo un jugador
                     "GROUP BY username, score " +
                     "ORDER BY score DESC " +
                     "LIMIT 10";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                scores.add(new Score(
                    rs.getString("username"),
                    rs.getInt("score"),
                    rs.getLong("date"),
                    rs.getBoolean("two_players")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scores;
    }

    public List<Score> getUserScores(String username) {
        List<Score> scores = new ArrayList<>();
        String sql = "SELECT score, date, two_players FROM scores WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                scores.add(new Score(
                    username,
                    rs.getInt("score"),
                    rs.getLong("date"),
                    rs.getBoolean("two_players")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scores;
    }
}
