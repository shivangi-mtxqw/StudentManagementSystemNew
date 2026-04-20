package com.sms.dao;

import com.sms.db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {
    private Connection conn = DBConnection.getConnection();

    public boolean markAttendance(int studentId, int subjectId,
                                  String date, String status) {
        String sql = "INSERT INTO attendance " +
                "(student_id, subject_id, attendance_date, status) " +
                "VALUES (?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, subjectId);
            ps.setString(3, date);
            ps.setString(4, status);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<Object[]> getAllAttendance() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT a.id, s.full_name, s.roll_number, " +
                "sub.subject_name, a.attendance_date, a.status " +
                "FROM attendance a " +
                "JOIN students s ON a.student_id = s.id " +
                "JOIN subjects sub ON a.subject_id = sub.id " +
                "ORDER BY a.attendance_date DESC";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5), rs.getString(6)
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<String[]> getAllStudentsBasic() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT id, full_name, roll_number FROM students " +
                "ORDER BY full_name";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{
                        rs.getString(1), rs.getString(2), rs.getString(3)
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<String[]> getAllSubjects() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT id, subject_name FROM subjects " +
                "ORDER BY subject_name";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{
                        rs.getString(1), rs.getString(2)
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}