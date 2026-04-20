package com.sms.ui.student;

import com.sms.dao.StudentDAO;
import com.sms.model.Student;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private StudentDAO dao = new StudentDAO();
    private String role;

    public StudentPanel(String role) {
        this.role = role;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        initUI();
        loadStudents();
    }

    private void initUI() {
        // Top bar: search + buttons
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));

        searchField = new JTextField(18);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> searchStudents());

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadStudents());

        top.add(new JLabel("Search:"));
        top.add(searchField);
        top.add(searchBtn);
        top.add(refreshBtn);

        if (role.equals("ADMIN")) {
            JButton addBtn = new JButton("Add Student");
            JButton editBtn = new JButton("Edit");
            JButton deleteBtn = new JButton("Delete");

            addBtn.addActionListener(e -> showAddDialog());
            deleteBtn.addActionListener(e -> deleteSelected());

            top.add(addBtn);
            top.add(editBtn);
            top.add(deleteBtn);
        }

        // Table
        String[] cols = {"ID", "Roll No", "Full Name", "Email", "Phone", "Class", "Department", "Gender"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setSelectionBackground(new Color(79, 70, 229, 60));

        JScrollPane scroll = new JScrollPane(table);

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private void loadStudents() {
        model.setRowCount(0);
        for (Student s : dao.getAllStudents()) {
            model.addRow(new Object[]{
                    s.getId(), s.getRollNumber(), s.getFullName(),
                    s.getEmail(), s.getPhone(), s.getStudentClass(),
                    s.getDepartment(), s.getGender()
            });
        }
    }

    private void searchStudents() {
        String kw = searchField.getText().trim();
        model.setRowCount(0);
        List<Student> results = kw.isEmpty() ? dao.getAllStudents() : dao.searchStudents(kw);
        for (Student s : results) {
            model.addRow(new Object[]{
                    s.getId(), s.getRollNumber(), s.getFullName(),
                    s.getEmail(), s.getPhone(), s.getStudentClass(),
                    s.getDepartment(), s.getGender()
            });
        }
    }

    private void showAddDialog() {
        JTextField roll = new JTextField(), name = new JTextField(),
                email = new JTextField(), phone = new JTextField(),
                cls = new JTextField(), dept = new JTextField();
        JComboBox<String> gender = new JComboBox<>(new String[]{"Male","Female","Other"});

        JPanel p = new JPanel(new GridLayout(7, 2, 8, 8));
        p.add(new JLabel("Roll No:")); p.add(roll);
        p.add(new JLabel("Full Name:")); p.add(name);
        p.add(new JLabel("Email:")); p.add(email);
        p.add(new JLabel("Phone:")); p.add(phone);
        p.add(new JLabel("Class:")); p.add(cls);
        p.add(new JLabel("Department:")); p.add(dept);
        p.add(new JLabel("Gender:")); p.add(gender);

        int res = JOptionPane.showConfirmDialog(this, p, "Add Student", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            Student s = new Student(roll.getText(), name.getText(), email.getText(),
                    phone.getText(), cls.getText(), dept.getText(), (String) gender.getSelectedItem());
            if (dao.addStudent(s)) {
                JOptionPane.showMessageDialog(this, "Student added!");
                loadStudents();
            }
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a student first."); return; }
        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this student?");
        if (confirm == JOptionPane.YES_OPTION && dao.deleteStudent(id)) {
            JOptionPane.showMessageDialog(this, "Deleted.");
            loadStudents();
        }
    }
}