package com.sms.dao;

import com.sms.db.DBConnection;
import com.sms.model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    private Connection conn = DBConnection.getConnection();

    public boolean addStudent(Student s) {
        String sql = "INSERT INTO students (roll_number, full_name, email, phone, class, department, gender) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getRollNumber());
            ps.setString(2, s.getFullName());
            ps.setString(3, s.getEmail());
            ps.setString(4, s.getPhone());
            ps.setString(5, s.getStudentClass());
            ps.setString(6, s.getDepartment());
            ps.setString(7, s.getGender());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateStudent(Student s) {
        String sql = "UPDATE students SET full_name=?, email=?, phone=?, class=?, department=?, gender=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getFullName());
            ps.setString(2, s.getEmail());
            ps.setString(3, s.getPhone());
            ps.setString(4, s.getStudentClass());
            ps.setString(5, s.getDepartment());
            ps.setString(6, s.getGender());
            ps.setInt(7, s.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteStudent(int id) {
        String sql = "DELETE FROM students WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY full_name";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Student s = new Student();
                s.setId(rs.getInt("id"));
                s.setRollNumber(rs.getString("roll_number"));
                s.setFullName(rs.getString("full_name"));
                s.setEmail(rs.getString("email"));
                s.setPhone(rs.getString("phone"));
                s.setStudentClass(rs.getString("class"));
                s.setDepartment(rs.getString("department"));
                s.setGender(rs.getString("gender"));
                list.add(s);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Search by name or roll number
    public List<Student> searchStudents(String keyword) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE full_name LIKE ? OR roll_number LIKE ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Student s = new Student();
                s.setId(rs.getInt("id"));
                s.setRollNumber(rs.getString("roll_number"));
                s.setFullName(rs.getString("full_name"));
                s.setEmail(rs.getString("email"));
                s.setPhone(rs.getString("phone"));
                list.add(s);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}