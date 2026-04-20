package com.sms.ui.dashboard;

import com.sms.dao.ResultsDAO;
import com.sms.ui.attendance.AttendancePanel;
import com.sms.ui.login.LoginFrame;
import com.sms.ui.result.ResultsPanel;
import com.sms.ui.student.StudentPanel;
import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {
    private String role, fullName;

    public DashboardFrame(String role, String fullName) {
        this.role = role;
        this.fullName = fullName;
        setTitle("SMS Dashboard — " + fullName + " [" + role + "]");
        setSize(1100, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {

        // ── Sidebar ──────────────────────────────────────────────
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(30, 30, 50));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        JLabel appName = new JLabel("SMS System");
        appName.setFont(new Font("Arial", Font.BOLD, 18));
        appName.setForeground(Color.WHITE);
        appName.setAlignmentX(Component.CENTER_ALIGNMENT);
        appName.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel userLabel = new JLabel(fullName);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        userLabel.setForeground(new Color(150, 150, 200));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel roleLabel = new JLabel("[" + role + "]");
        roleLabel.setFont(new Font("Arial", Font.BOLD, 11));
        roleLabel.setForeground(new Color(100, 200, 150));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        roleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // ── Tabs ─────────────────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.PLAIN, 13));
        tabs.addTab("Students",   new StudentPanel(role));
        tabs.addTab("Attendance", new AttendancePanel());
        tabs.addTab("Results",    new ResultsPanel(role));

        // ── Sidebar nav buttons ───────────────────────────────────
        String[] navItems = {"Students", "Attendance", "Results"};
        for (int i = 0; i < navItems.length; i++) {
            final int index = i;
            JButton btn = createNavButton(navItems[i]);
            btn.addActionListener(e -> tabs.setSelectedIndex(index));
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(4));
        }

        sidebar.add(Box.createVerticalGlue());

        JButton logoutBtn = createNavButton("Logout");


logoutBtn.setForeground(new Color(220, 80, 80));
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        sidebar.add(logoutBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(appName);
        sidebar.add(userLabel);
        sidebar.add(roleLabel);

        // ── Stats bar ─────────────────────────────────────────────
        ResultsDAO rDao = new ResultsDAO();

        JPanel statsBar = new JPanel(new GridLayout(1, 3, 12, 0));
        statsBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        statsBar.setBackground(new Color(245, 245, 255));

        addStatCard(statsBar, "Total Students",
                String.valueOf(rDao.getTotalStudents()),
                new Color(79, 70, 229));
        addStatCard(statsBar, "Total Subjects",
                String.valueOf(rDao.getTotalSubjects()),
                new Color(16, 130, 90));
        addStatCard(statsBar, "Attendance %",
                rDao.getOverallAttendancePct() + "%",
                new Color(200, 100, 0));

        // ── Main area ─────────────────────────────────────────────
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.add(statsBar, BorderLayout.NORTH);
        mainArea.add(tabs,     BorderLayout.CENTER);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(sidebar,  BorderLayout.WEST);
        getContentPane().add(mainArea, BorderLayout.CENTER);
    }

    private void addStatCard(JPanel parent, String label,
                             String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setForeground(Color.GRAY);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Arial", Font.BOLD, 26));
        val.setForeground(color);

        card.add(lbl, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        parent.add(card);
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(180, 36));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setBackground(new Color(55, 55, 85));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}