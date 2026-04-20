package com.sms.ui.login;

import com.sms.dao.UserDAO;
import com.sms.ui.dashboard.DashboardFrame;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;

    public LoginFrame() {
        setTitle("Student Management System — Login");
        setSize(420, 340);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(30, 30, 50));

        // Header
        JLabel title = new JLabel("Student Management System", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(24, 0, 8, 0));

        JLabel subtitle = new JLabel("Sign in to continue", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(150, 150, 180));
        subtitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 30, 50));
        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.CENTER);

        // Form panel
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(42, 42, 68));
        form.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(6, 0, 6, 0);

        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(300, 36));
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 160)), "Username"));

        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(300, 36));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 160)), "Password"));

        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(79, 70, 229));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.setPreferredSize(new Dimension(300, 40));

        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(220, 80, 80));

        gc.gridx = 0; gc.gridy = 0; form.add(usernameField, gc);
        gc.gridy = 1; form.add(passwordField, gc);
        gc.gridy = 2; form.add(loginBtn, gc);
        gc.gridy = 3; form.add(statusLabel, gc);

        loginBtn.addActionListener(e -> doLogin());
        passwordField.addActionListener(e -> doLogin());

        main.add(header, BorderLayout.NORTH);
        main.add(form, BorderLayout.CENTER);
        setContentPane(main);
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter username and password.");
            return;
        }

        String[] result = new UserDAO().login(username, password);
        if (result != null) {
            dispose();
            new DashboardFrame(result[0], result[1]).setVisible(true);
        } else {
            statusLabel.setText("Invalid credentials. Try again.");
            passwordField.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}