package com.sms.dao;

import com.sms.db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultsDAO {
    private Connection conn = DBConnection.getConnection();

    public boolean addResult(int studentId, int subjectId, int semester,
                             double internal, double external,
                             double maxMarks, String year) {
        double total = internal + external;
        String grade = calculateGrade(total, maxMarks);

        String sql = """
            INSERT INTO results 
            (student_id, subject_id, semester, internal_marks, 
             external_marks, max_marks, grade, exam_year)
            VALUES (?,?,?,?,?,?,?,?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, subjectId);
            ps.setInt(3, semester);
            ps.setDouble(4, internal);
            ps.setDouble(5, external);
            ps.setDouble(6, maxMarks);
            ps.setString(7, grade);
            ps.setString(8, year);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<Object[]> getAllResults() {
        List<Object[]> list = new ArrayList<>();
        String sql = """
            SELECT r.id, s.full_name, s.roll_number, sub.subject_name,
                   r.semester, r.internal_marks, r.external_marks,
                   r.total_marks, r.max_marks, r.grade, r.exam_year
            FROM results r
            JOIN students s ON r.student_id = s.id
            JOIN subjects sub ON r.subject_id = sub.id
            ORDER BY s.full_name, r.semester
            """;
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getInt(5),
                        rs.getDouble(6), rs.getDouble(7),
                        rs.getDouble(8), rs.getDouble(9),
                        rs.getString(10), rs.getString(11)
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Object[]> getResultsByStudent(int studentId) {
        List<Object[]> list = new ArrayList<>();
        String sql = """
            SELECT sub.subject_name, r.semester, r.internal_marks,
                   r.external_marks, r.total_marks, r.max_marks, r.grade
            FROM results r
            JOIN subjects sub ON r.subject_id = sub.id
            WHERE r.student_id = ?
            ORDER BY r.semester
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getString(1), rs.getInt(2),
                        rs.getDouble(3), rs.getDouble(4),
                        rs.getDouble(5), rs.getDouble(6), rs.getString(7)
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // CGPA calculation for a student
    public double getCGPA(int studentId) {
        String sql = """
            SELECT AVG(
                CASE grade
                    WHEN 'O'  THEN 10
                    WHEN 'A+' THEN 9
                    WHEN 'A'  THEN 8
                    WHEN 'B+' THEN 7
                    WHEN 'B'  THEN 6
                    WHEN 'C'  THEN 5
                    ELSE 0
                END
            ) FROM results WHERE student_id = ?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    // Summary stats for dashboard
    public int getTotalStudents() {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM students")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int getTotalSubjects() {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM subjects")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public double getOverallAttendancePct() {
        String sql = """
            SELECT 
              ROUND(100.0 * SUM(status='Present') / COUNT(*), 1)
            FROM attendance
            """;
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    // Grade as per standard university pattern
    public static String calculateGrade(double obtained, double max) {
        double pct = (obtained / max) * 100;
        if (pct >= 90) return "O";
        if (pct >= 80) return "A+";
        if (pct >= 70) return "A";
        if (pct >= 60) return "B+";
        if (pct >= 50) return "B";
        if (pct >= 40) return "C";
        return "F";
    }
}
