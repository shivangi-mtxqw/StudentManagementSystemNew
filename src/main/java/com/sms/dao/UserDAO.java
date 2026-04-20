package com.sms.dao;

import com.sms.db.DBConnection;
import java.sql.*;

public class UserDAO {
    public String[] login(String username, String password) {
        // returns {role, fullName} or null if invalid
        String sql = "SELECT role, full_name FROM users WHERE username=? AND password=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new String[]{ rs.getString("role"), rs.getString("full_name") };
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}