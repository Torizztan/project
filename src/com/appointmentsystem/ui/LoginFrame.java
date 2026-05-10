package com.appointmentsystem.ui;

import com.appointmentsystem.dao.UserDAO;
import com.appointmentsystem.model.User;
import com.appointmentsystem.util.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class LoginFrame extends JFrame {
    private static final Color PRIMARY = new Color(142, 81, 255);
    private static final Color PRIMARY_DARK = new Color(110, 55, 220);
    private static final Color BG_LIGHT = new Color(244, 244, 245);
    private static final Color TEXT_DARK = new Color(9, 9, 11);
    private static final Color TEXT_GRAY = new Color(113, 113, 123);

    private CardLayout cardLayout;
    private JPanel formCards;
    private UserDAO userDAO;

    public LoginFrame() {
        userDAO = new UserDAO();
        setTitle("Appointment and Schedule Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_LIGHT);

        JPanel contentPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 24, 24));
                g2.dispose();
            }
        };
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JPanel sidePanel = createSidePanel();
        contentPanel.add(sidePanel, BorderLayout.WEST);

        cardLayout = new CardLayout();
        formCards = new JPanel(cardLayout);
        formCards.setOpaque(false);
        formCards.add(createLoginPanel(), "login");
        formCards.add(createRegisterPanel(), "register");

        contentPanel.add(formCards, BorderLayout.CENTER);

        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private JPanel createSidePanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY, 0, getHeight(), PRIMARY_DARK);
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 24, 24));
                g2.dispose();
            }
        };
        panel.setPreferredSize(new Dimension(340, 0));
        panel.setLayout(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(5, 30, 5, 30);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel icon = new JLabel("\uD83D\uDCC5");
        icon.setFont(new Font("SansSerif", Font.PLAIN, 48));
        gbc.gridy = 0;
        panel.add(icon, gbc);

        JLabel title = new JLabel("Schedule System");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 30, 5, 30);
        panel.add(title, gbc);

        JLabel subtitle = new JLabel("<html><center>Appointment and Schedule<br>Management System</center></html>");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(new Color(255, 255, 255, 200));
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 30, 20, 30);
        panel.add(subtitle, gbc);

        JLabel desc = new JLabel("<html><center>Book appointments,<br>manage schedules,<br>and stay organized.</center></html>");
        desc.setFont(new Font("SansSerif", Font.PLAIN, 12));
        desc.setForeground(new Color(255, 255, 255, 160));
        desc.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 30, 5, 30);
        panel.add(desc, gbc);

        return panel;
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 60, 5, 60);

        JLabel titleLabel = new JLabel("Sign In");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        titleLabel.setForeground(PRIMARY);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 60, 5, 60);
        panel.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Welcome back! Please sign in to continue.");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitleLabel.setForeground(TEXT_GRAY);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 60, 20, 60);
        panel.add(subtitleLabel, gbc);

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        emailLabel.setForeground(TEXT_DARK);
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 60, 2, 60);
        panel.add(emailLabel, gbc);

        JTextField emailField = createTextField("Enter your email");
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 60, 10, 60);
        panel.add(emailField, gbc);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        passLabel.setForeground(TEXT_DARK);
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 60, 2, 60);
        panel.add(passLabel, gbc);

        JPasswordField passField = createPasswordField("Enter your password");
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 60, 15, 60);
        panel.add(passField, gbc);

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        errorLabel.setForeground(new Color(231, 0, 11));
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 60, 5, 60);
        panel.add(errorLabel, gbc);

        JButton loginBtn = createButton("Sign In", PRIMARY);
        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passField.getPassword());
            if (email.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Please fill in all fields.");
                return;
            }
            User user = userDAO.login(email, password);
            if (user != null) {
                SessionManager.setCurrentUser(user);
                dispose();
                new MainFrame().setVisible(true);
            } else {
                errorLabel.setText("Invalid email or password.");
            }
        });
        gbc.gridy = 7;
        gbc.insets = new Insets(5, 60, 10, 60);
        panel.add(loginBtn, gbc);

        JButton switchBtn = new JButton("Don't have an account? Create one");
        switchBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        switchBtn.setForeground(PRIMARY);
        switchBtn.setBorderPainted(false);
        switchBtn.setContentAreaFilled(false);
        switchBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        switchBtn.addActionListener(e -> cardLayout.show(formCards, "register"));
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 60, 0, 60);
        panel.add(switchBtn, gbc);

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 60, 3, 60);

        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        titleLabel.setForeground(PRIMARY);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 60, 3, 60);
        panel.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Register to get started.");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitleLabel.setForeground(TEXT_GRAY);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 60, 12, 60);
        panel.add(subtitleLabel, gbc);

        JLabel nameLabel = new JLabel("Full Name");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        nameLabel.setForeground(TEXT_DARK);
        gbc.gridy = 2;
        gbc.insets = new Insets(3, 60, 1, 60);
        panel.add(nameLabel, gbc);

        JTextField nameField = createTextField("Enter your full name");
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 60, 6, 60);
        panel.add(nameField, gbc);

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        emailLabel.setForeground(TEXT_DARK);
        gbc.gridy = 4;
        gbc.insets = new Insets(3, 60, 1, 60);
        panel.add(emailLabel, gbc);

        JTextField emailField = createTextField("Enter your email");
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 60, 6, 60);
        panel.add(emailField, gbc);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        passLabel.setForeground(TEXT_DARK);
        gbc.gridy = 6;
        gbc.insets = new Insets(3, 60, 1, 60);
        panel.add(passLabel, gbc);

        JPasswordField passField = createPasswordField("Create a password");
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 60, 6, 60);
        panel.add(passField, gbc);

        JLabel roleLabel = new JLabel("Role");
        roleLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        roleLabel.setForeground(TEXT_DARK);
        gbc.gridy = 8;
        gbc.insets = new Insets(3, 60, 1, 60);
        panel.add(roleLabel, gbc);

        JComboBox<String> roleBox = new JComboBox<>(new String[]{"student", "teacher"});
        roleBox.setFont(new Font("SansSerif", Font.PLAIN, 13));
        roleBox.setPreferredSize(new Dimension(0, 38));
        gbc.gridy = 9;
        gbc.insets = new Insets(0, 60, 8, 60);
        panel.add(roleBox, gbc);

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        errorLabel.setForeground(new Color(231, 0, 11));
        gbc.gridy = 10;
        gbc.insets = new Insets(0, 60, 3, 60);
        panel.add(errorLabel, gbc);

        JButton registerBtn = createButton("Sign Up", PRIMARY);
        registerBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passField.getPassword());
            String role = (String) roleBox.getSelectedItem();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Please fill in all fields.");
                return;
            }
            if (userDAO.register(name, email, password, role)) {
                errorLabel.setForeground(new Color(0, 188, 125));
                errorLabel.setText("Registered successfully! You can now login.");
                nameField.setText("");
                emailField.setText("");
                passField.setText("");
            } else {
                errorLabel.setForeground(new Color(231, 0, 11));
                errorLabel.setText("Email already exists.");
            }
        });
        gbc.gridy = 11;
        gbc.insets = new Insets(3, 60, 6, 60);
        panel.add(registerBtn, gbc);

        JButton switchBtn = new JButton("Already have an account? Sign in");
        switchBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        switchBtn.setForeground(PRIMARY);
        switchBtn.setBorderPainted(false);
        switchBtn.setContentAreaFilled(false);
        switchBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        switchBtn.addActionListener(e -> cardLayout.show(formCards, "login"));
        gbc.gridy = 12;
        gbc.insets = new Insets(0, 60, 0, 60);
        panel.add(switchBtn, gbc);

        return panel;
    }

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(new Color(160, 160, 170));
                    g2.setFont(getFont());
                    g2.drawString(placeholder, getInsets().left + 5, getHeight() / 2 + getFont().getSize() / 2 - 2);
                    g2.dispose();
                }
            }
        };
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(0, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(228, 228, 231), 1),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) { field.repaint(); }
            @Override
            public void focusLost(FocusEvent e) { field.repaint(); }
        });
        return field;
    }

    private JPasswordField createPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getPassword().length == 0 && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(new Color(160, 160, 170));
                    g2.setFont(getFont());
                    g2.drawString(placeholder, getInsets().left + 5, getHeight() / 2 + getFont().getSize() / 2 - 2);
                    g2.dispose();
                }
            }
        };
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(0, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(228, 228, 231), 1),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) { field.repaint(); }
            @Override
            public void focusLost(FocusEvent e) { field.repaint(); }
        });
        return field;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(0, 42));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }
}
