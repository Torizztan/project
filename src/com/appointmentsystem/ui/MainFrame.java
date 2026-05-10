package com.appointmentsystem.ui;

import com.appointmentsystem.util.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class MainFrame extends JFrame {
    static final Color PRIMARY = new Color(142, 81, 255);
    static final Color PRIMARY_DARK = new Color(110, 55, 220);
    static final Color BG_LIGHT = new Color(244, 244, 245);
    static final Color TEXT_DARK = new Color(9, 9, 11);
    static final Color TEXT_GRAY = new Color(113, 113, 123);
    static final Color CARD_BG = Color.WHITE;
    static final Color SUCCESS = new Color(0, 188, 125);
    static final Color ERROR = new Color(231, 0, 11);
    static final Color WARNING = new Color(254, 154, 0);
    static final Color BORDER = new Color(228, 228, 231);

    private CardLayout contentLayout;
    private JPanel contentPanel;
    private JPanel sidebarPanel;
    private String activeItem = "Dashboard";

    public MainFrame() {
        setTitle("Appointment and Schedule Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 650));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_LIGHT);

        sidebarPanel = createSidebar();
        mainPanel.add(sidebarPanel, BorderLayout.WEST);

        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(BG_LIGHT);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        String role = SessionManager.getRole();
        contentPanel.add(new DashboardPanel(), "Dashboard");
        contentPanel.add(new CalendarPanel(), "Calendar");
        contentPanel.add(new BookingPanel(), "Bookings");
        contentPanel.add(new NotificationPanel(), "Notifications");
        contentPanel.add(new SettingsPanel(), "Settings");

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY, 0, getHeight(), PRIMARY_DARK);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setLayout(new BorderLayout());
        sidebar.setOpaque(false);

        JPanel topSection = new JPanel();
        topSection.setOpaque(false);
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setBorder(BorderFactory.createEmptyBorder(20, 16, 10, 16));

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        logoPanel.setOpaque(false);
        logoPanel.setMaximumSize(new Dimension(230, 50));

        JLabel logoIcon = new JLabel("\u2630");
        logoIcon.setFont(new Font("SansSerif", Font.BOLD, 20));
        logoIcon.setForeground(Color.WHITE);
        logoPanel.add(logoIcon);

        JLabel appTitle = new JLabel("Schedule Sys.");
        appTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        appTitle.setForeground(Color.WHITE);
        logoPanel.add(appTitle);

        topSection.add(logoPanel);
        topSection.add(Box.createVerticalStrut(10));

        JPanel userInfoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
            }
        };
        userInfoPanel.setOpaque(false);
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        userInfoPanel.setMaximumSize(new Dimension(230, 80));

        JLabel userName = new JLabel(SessionManager.getCurrentUser().getName());
        userName.setFont(new Font("SansSerif", Font.BOLD, 14));
        userName.setForeground(Color.WHITE);
        userName.setAlignmentX(Component.LEFT_ALIGNMENT);
        userInfoPanel.add(userName);

        String roleText = SessionManager.getRole().substring(0, 1).toUpperCase() + SessionManager.getRole().substring(1);
        JLabel userRole = new JLabel(roleText + " \u2022 " + SessionManager.getCurrentUser().getEmail());
        userRole.setFont(new Font("SansSerif", Font.PLAIN, 11));
        userRole.setForeground(new Color(255, 255, 255, 180));
        userRole.setAlignmentX(Component.LEFT_ALIGNMENT);
        userInfoPanel.add(Box.createVerticalStrut(3));
        userInfoPanel.add(userRole);

        topSection.add(userInfoPanel);
        topSection.add(Box.createVerticalStrut(20));

        JLabel menuLabel = new JLabel("MENU");
        menuLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        menuLabel.setForeground(new Color(255, 255, 255, 120));
        menuLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        menuLabel.setBorder(BorderFactory.createEmptyBorder(0, 4, 8, 0));
        topSection.add(menuLabel);

        String[][] menuItems = {
            {"Dashboard", "\u25A3"},
            {"Calendar", "\uD83D\uDCC5"},
            {"Bookings", "\uD83D\uDCCB"},
            {"Notifications", "\uD83D\uDD14"},
            {"Settings", "\u2699"}
        };

        for (String[] item : menuItems) {
            JPanel menuItem = createSidebarItem(item[0], item[1]);
            topSection.add(menuItem);
            topSection.add(Box.createVerticalStrut(4));
        }

        sidebar.add(topSection, BorderLayout.NORTH);

        JPanel bottomSection = new JPanel();
        bottomSection.setOpaque(false);
        bottomSection.setLayout(new BoxLayout(bottomSection, BoxLayout.Y_AXIS));
        bottomSection.setBorder(BorderFactory.createEmptyBorder(10, 16, 20, 16));

        JPanel logoutItem = createSidebarItem("Logout", "\u2190");
        logoutItem.removeAll();

        JButton logoutBtn = new JButton("\u2190  Logout") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 30));
                } else {
                    g2.setColor(new Color(0, 0, 0, 0));
                }
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logoutBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        logoutBtn.setForeground(new Color(255, 200, 200));
        logoutBtn.setBorderPainted(false);
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.setHorizontalAlignment(SwingConstants.LEFT);
        logoutBtn.setMaximumSize(new Dimension(230, 40));
        logoutBtn.setPreferredSize(new Dimension(198, 40));
        logoutBtn.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?", "Logout",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                SessionManager.logout();
                dispose();
                new LoginFrame().setVisible(true);
            }
        });

        bottomSection.add(logoutBtn);
        sidebar.add(bottomSection, BorderLayout.SOUTH);

        return sidebar;
    }

    private JPanel createSidebarItem(String name, String icon) {
        boolean isActive = name.equals(activeItem);
        JPanel item = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (name.equals(activeItem)) {
                    g2.setColor(new Color(255, 255, 255, 40));
                } else if (getClientProperty("hover") != null && (boolean) getClientProperty("hover")) {
                    g2.setColor(new Color(255, 255, 255, 20));
                }
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
            }
        };
        item.setOpaque(false);
        item.setMaximumSize(new Dimension(230, 40));
        item.setPreferredSize(new Dimension(198, 40));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        item.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel label = new JLabel(icon + "  " + name);
        label.setFont(new Font("SansSerif", isActive ? Font.BOLD : Font.PLAIN, 13));
        label.setForeground(isActive ? Color.WHITE : new Color(255, 255, 255, 180));
        item.add(label, BorderLayout.CENTER);

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                activeItem = name;
                contentLayout.show(contentPanel, name);
                refreshSidebar();
                refreshActivePanel();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                item.putClientProperty("hover", true);
                item.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                item.putClientProperty("hover", false);
                item.repaint();
            }
        });

        return item;
    }

    private void refreshSidebar() {
        Container parent = sidebarPanel.getParent();
        parent.remove(sidebarPanel);
        sidebarPanel = createSidebar();
        parent.add(sidebarPanel, BorderLayout.WEST);
        parent.revalidate();
        parent.repaint();
    }

    private void refreshActivePanel() {
        for (Component comp : contentPanel.getComponents()) {
            if (comp.isVisible() && comp instanceof Refreshable) {
                ((Refreshable) comp).refresh();
            }
        }
    }

    interface Refreshable {
        void refresh();
    }
}
