package com.sms.ui.result;

import com.sms.dao.ResultsDAO;
import com.sms.dao.AttendanceDAO;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class ResultsPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private ResultsDAO dao = new ResultsDAO();
    private AttendanceDAO aDao = new AttendanceDAO();
    private String role;

    public ResultsPanel(String role) {
        this.role = role;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        initUI();
        loadResults();
    }

    private void initUI() {
        // Title bar
        JPanel titleBar = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Results & Grade Sheet");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

        JLabel sub = new JLabel("Semester-wise results with auto grade calculation");
        sub.setFont(new Font("Arial", Font.PLAIN, 12));
        sub.setForeground(Color.GRAY);

        titleBar.add(title, BorderLayout.NORTH);
        titleBar.add(sub, BorderLayout.CENTER);
        titleBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        // Buttons
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton addBtn = new JButton("+ Add Result");
        JButton viewBtn = new JButton("View Student CGPA");
        JButton refreshBtn = new JButton("Refresh");

        styleButton(addBtn, new Color(79, 70, 229));
        styleButton(viewBtn, new Color(16, 130, 90));
        styleButton(refreshBtn, new Color(100, 100, 100));

        addBtn.addActionListener(e -> showAddDialog());
        viewBtn.addActionListener(e -> showCGPADialog());
        refreshBtn.addActionListener(e -> loadResults());

        top.add(addBtn);
        if (role.equals("ADMIN")) top.add(viewBtn);
        top.add(refreshBtn);

        // Filter by semester
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filterBar.add(new JLabel("Filter by Semester:"));
        JComboBox<String> semFilter = new JComboBox<>(
                new String[]{"All", "1", "2", "3", "4"});
        semFilter.addActionListener(e -> {
            String sel = (String) semFilter.getSelectedItem();
            filterBySemester(sel);
        });
        filterBar.add(semFilter);

        JPanel controls = new JPanel(new BorderLayout());
        controls.add(top, BorderLayout.NORTH);
        controls.add(filterBar, BorderLayout.CENTER);

        // Table
        String[] cols = {"ID", "Student Name", "Roll No", "Subject",
                "Sem", "Internal", "External", "Total",
                "Max", "Grade", "Year"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.setSelectionBackground(new Color(79, 70, 229, 50));

        // Color rows by grade
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                                                           boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(
                        t, v, sel, foc, r, c);
                if (!sel) {
                    String grade = (String) model.getValueAt(r, 9);
                    if (grade == null) return comp;
                    switch (grade) {
                        case "O","A+" -> comp.setBackground(new Color(220, 255, 220));
                        case "A","B+" -> comp.setBackground(new Color(220, 240, 255));
                        case "B","C"  -> comp.setBackground(new Color(255, 250, 200));
                        case "F"      -> comp.setBackground(new Color(255, 220, 220));
                        default       -> comp.setBackground(Color.WHITE);
                    }
                }
                return comp;
            }
        });

        JPanel north = new JPanel(new BorderLayout());
        north.add(titleBar, BorderLayout.NORTH);
        north.add(controls, BorderLayout.CENTER);

        add(north, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
    }

    private void loadResults() {
        model.setRowCount(0);
        for (Object[] row : dao.getAllResults()) {
            model.addRow(row);
        }
    }

    private void filterBySemester(String sem) {
        model.setRowCount(0);
        for (Object[] row : dao.getAllResults()) {
            if (sem.equals("All") || String.valueOf(row[4]).equals(sem)) {
                model.addRow(row);
            }
        }
    }

    private void showAddDialog() {
        List<String[]> students = aDao.getAllStudentsBasic();
        List<String[]> subjects = aDao.getAllSubjects();

        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Add students first!"); return;
        }
        if (subjects.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Add subjects in MySQL first!"); return;
        }

        String[] sNames = students.stream()
                .map(s -> s[1] + " (" + s[2] + ")").toArray(String[]::new);
        JComboBox<String> studentBox = new JComboBox<>(sNames);

        String[] subNames = subjects.stream()
                .map(s -> s[1]).toArray(String[]::new);
        JComboBox<String> subjectBox = new JComboBox<>(subNames);

        JComboBox<String> semBox = new JComboBox<>(
                new String[]{"1","2","3","4","5","6","7","8"});

        JTextField internalField = new JTextField();
        JTextField externalField = new JTextField();
        JTextField maxField = new JTextField("100");
        JTextField yearField = new JTextField("2024-25");

        JPanel p = new JPanel(new GridLayout(7, 2, 8, 10));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        p.add(new JLabel("Student:")); p.add(studentBox);
        p.add(new JLabel("Subject:")); p.add(subjectBox);
        p.add(new JLabel("Semester:")); p.add(semBox);
        p.add(new JLabel("Internal Marks:")); p.add(internalField);
        p.add(new JLabel("External Marks:")); p.add(externalField);
        p.add(new JLabel("Max Marks:")); p.add(maxField);
        p.add(new JLabel("Exam Year:")); p.add(yearField);

        // Live grade preview
        JLabel preview = new JLabel("Grade will be auto-calculated");
        preview.setFont(new Font("Arial", Font.BOLD, 13));
        preview.setForeground(new Color(79, 70, 229));

        JPanel wrapper = new JPanel(new BorderLayout(0, 8));
        wrapper.add(p, BorderLayout.CENTER);
        wrapper.add(preview, BorderLayout.SOUTH);

        int res = JOptionPane.showConfirmDialog(this, wrapper,
                "Add Result", JOptionPane.OK_CANCEL_OPTION);

        if (res == JOptionPane.OK_OPTION) {
            try {
                int studentId = Integer.parseInt(
                        students.get(studentBox.getSelectedIndex())[0]);
                int subjectId = Integer.parseInt(
                        subjects.get(subjectBox.getSelectedIndex())[0]);
                int sem = Integer.parseInt((String) semBox.getSelectedItem());
                double internal = Double.parseDouble(internalField.getText().trim());
                double external = Double.parseDouble(externalField.getText().trim());
                double max = Double.parseDouble(maxField.getText().trim());
                String year = yearField.getText().trim();

                String grade = ResultsDAO.calculateGrade(internal + external, max);

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Total: " + (internal + external) + "/" + max +
                                "  →  Grade: " + grade + "\n\nSave this result?",
                        "Confirm", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    if (dao.addResult(studentId, subjectId, sem,
                            internal, external, max, year)) {
                        JOptionPane.showMessageDialog(this, "Result saved!");
                        loadResults();
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Enter valid numbers for marks.");
            }
        }
    }

    private void showCGPADialog() {
        List<String[]> students = aDao.getAllStudentsBasic();
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students found."); return;
        }

        String[] sNames = students.stream()
                .map(s -> s[1] + " (" + s[2] + ")").toArray(String[]::new);
        String choice = (String) JOptionPane.showInputDialog(this,
                "Select student:", "View CGPA",
                JOptionPane.PLAIN_MESSAGE, null, sNames, sNames[0]);

        if (choice != null) {
            int idx = java.util.Arrays.asList(sNames).indexOf(choice);
            int studentId = Integer.parseInt(students.get(idx)[0]);
            double cgpa = dao.getCGPA(studentId);

            List<Object[]> results = dao.getResultsByStudent(studentId);
            StringBuilder sb = new StringBuilder();
            sb.append("Student: ").append(students.get(idx)[1]).append("\n");
            sb.append("Roll No: ").append(students.get(idx)[2]).append("\n\n");
            sb.append(String.format("%-20s %-4s %-6s %-6s %-6s %s\n",
                    "Subject", "Sem", "Int", "Ext", "Total", "Grade"));
            sb.append("-".repeat(55)).append("\n");
            for (Object[] r : results) {
                sb.append(String.format("%-20s %-4d %-6.1f %-6.1f %-6.1f %s\n",
                        r[0], r[1], r[2], r[3], r[4], r[6]));
            }
            sb.append("-".repeat(55)).append("\n");
            sb.append(String.format("CGPA: %.2f / 10.0", cgpa));

            JTextArea area = new JTextArea(sb.toString());
            area.setFont(new Font("Monospaced", Font.PLAIN, 13));
            area.setEditable(false);
            area.setBackground(new Color(245, 245, 255));
            JOptionPane.showMessageDialog(this,
                    new JScrollPane(area), "CGPA Report",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}