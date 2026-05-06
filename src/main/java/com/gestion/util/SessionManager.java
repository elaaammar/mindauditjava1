package com.gestion.util;

public class SessionManager {
    private static SessionManager instance;
    private int userId = 1; // Default
    private boolean isAdmin = true; // Default

    private SessionManager() {}
    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }
    public int getUserId() { return userId; }
    public boolean isAdmin() { return isAdmin; }
}
