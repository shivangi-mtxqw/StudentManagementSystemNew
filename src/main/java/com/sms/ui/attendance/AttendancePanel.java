package com.sms.ui.attendance;

import com.sms.dao.AttendanceDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class AttendancePanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private AttendanceDAO dao = new AttendanceDAO();

    public AttendancePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        initUI();
        loadAttendance();
    }

    private void initUI() {
        JLabel title = new JLabel("Attendance Management");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

        JLabel sub = new JLabel("Mark and track student attendance by subject");
        sub.setFont(new Font("Arial", Font.PLAIN, 12));
        sub.setForeground(Color.GRAY);
        sub.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));

        JButton markBtn = new JButton("+ Mark Attendance");
        markBtn.setBackground(new Color(79, 70, 229));
        markBtn.setForeground(Color.WHITE);
        markBtn.setFocusPainted(false);
        markBtn.setBorderPainted(false);
        markBtn.setFont(new Font("Arial", Font.BOLD, 12));
        markBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        markBtn.addActionListener(e -> showMarkDialog());
        refreshBtn.addActionListener(e -> loadAttendance());

        top.add(markBtn);
        top.add(refreshBtn);

        String[] cols = {"ID", "Student Name", "Roll No", "Subject", "Date", "Status"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.setSelectionBackground(new Color(79, 70, 229, 60));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                                                           boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(
                        t, v, sel, foc, r, c);
                if (!sel) {
                    Object status = model.getValueAt(r, 5);
                    if ("Present".equals(status))
                        comp.setBackground(new Color(220, 255, 220));
                    else if ("Absent".equals(status))
                        comp.setBackground(new Color(255, 220, 220));
                    else if ("Late".equals(status))
                        comp.setBackground(new Color(255, 250, 200));
                    else
                        comp.setBackground(Color.WHITE);
                }
                return comp;
            }
        });

        JPanel north = new JPanel(new BorderLayout());
        north.add(title, BorderLayout.NORTH);
        north.add(sub, BorderLayout.CENTER);
        north.add(top, BorderLayout.SOUTH);

        add(north, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadAttendance() {
        model.setRowCount(0);
        for (Object[] row : dao.getAllAttendance()) {
            model.addRow(row);
        }
    }

    private void showMarkDialog() {
        List<String[]> students = dao.getAllStudentsBasic();
        List<String[]> subjects = dao.getAllSubjects();

        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No students found. Please add students first.",
                    "No Students", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (subjects.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No subjects found. Please add subjects in MySQL.",
                    "No Subjects", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] studentNames = students.stream()
                .map(s -> s[1] + " (" + s[2] + ")")
                .toArray(String[]::new);
        JComboBox<String> studentBox = new JComboBox<>(studentNames);

        String[] subjectNames = subjects.stream()
                .map(s -> s[1]).toArray(String[]::new);
        JComboBox<String> subjectBox = new JComboBox<>(subjectNames);

        JComboBox<String> statusBox = new JComboBox<>(
                new String[]{"Present", "Absent", "Late"});

        JTextField dateField = new JTextField(LocalDate.now().toString());

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Student:"));           form.add(studentBox);
        form.add(new JLabel("Subject:"));           form.add(subjectBox);
        form.add(new JLabel("Date (YYYY-MM-DD):")); form.add(dateField);
        form.add(new JLabel("Status:"));            form.add(statusBox);

        int res = JOptionPane.showConfirmDialog(this, form,
                "Mark Attendance", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (res == JOptionPane.OK_OPTION) {
            try {
                int studentId = Integer.parseInt(
                        students.get(studentBox.getSelectedIndex())[0]);
                int subjectId = Integer.parseInt(
                        subjects.get(subjectBox.getSelectedIndex())[0]);
                String date = dateField.getText().trim();
                String status = (String) statusBox.getSelectedItem();

                if (dao.markAttendance(studentId, subjectId, date, status)) {
                    JOptionPane.showMessageDialog(this,
                            "Attendance marked: " + status,
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadAttendance();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to mark attendance.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error: " + ex.getMessage());
            }
        }
    }
}