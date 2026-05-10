package com.appointmentsystem.ui;

import com.appointmentsystem.dao.AppointmentDAO;
import com.appointmentsystem.dao.UserDAO;
import com.appointmentsystem.model.Appointment;
import com.appointmentsystem.model.User;
import com.appointmentsystem.util.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.List;

public class DashboardPanel extends JPanel implements MainFrame.Refreshable {
    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private UserDAO userDAO = new UserDAO();
    private JPanel contentArea;

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(MainFrame.BG_LIGHT);
        buildUI();
    }

    private void buildUI() {
        if (contentArea != null) remove(contentArea);

        contentArea = new JPanel();
        contentArea.setLayout(new BoxLayout(contentArea, BoxLayout.Y_AXIS));
        contentArea.setBackground(MainFrame.BG_LIGHT);
        contentArea.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        String role = SessionManager.getRole();
        User user = SessionManager.getCurrentUser();

        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(MainFrame.TEXT_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentArea.add(titleLabel);

        JLabel welcomeLabel = new JLabel("Welcome back, " + user.getName() + "!");
        welcomeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        welcomeLabel.setForeground(MainFrame.TEXT_GRAY);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentArea.add(welcomeLabel);
        contentArea.add(Box.createVerticalStrut(20));

        if ("admin".equals(role)) {
            buildAdminDashboard();
        } else if ("teacher".equals(role)) {
            buildTeacherDashboard();
        } else {
            buildStudentDashboard();
        }

        JScrollPane scroll = new JScrollPane(contentArea);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBackground(MainFrame.BG_LIGHT);
        add(scroll, BorderLayout.CENTER);
    }

    private void buildAdminDashboard() {
        JPanel statsRow = new JPanel(new GridLayout(1, 6, 12, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        statsRow.add(createStatCard("Total Users", String.valueOf(userDAO.getUserCount()), MainFrame.PRIMARY));
        statsRow.add(createStatCard("Pending", String.valueOf(appointmentDAO.getCountByStatus("pending")), MainFrame.WARNING));
        statsRow.add(createStatCard("Approved", String.valueOf(appointmentDAO.getCountByStatus("approved")), MainFrame.SUCCESS));
        statsRow.add(createStatCard("Completed", String.valueOf(appointmentDAO.getCountByStatus("completed")), new Color(0, 150, 137)));
        statsRow.add(createStatCard("Rejected", String.valueOf(appointmentDAO.getCountByStatus("rejected")), MainFrame.ERROR));
        statsRow.add(createStatCard("Cancelled", String.valueOf(appointmentDAO.getCountByStatus("cancelled")), MainFrame.TEXT_GRAY));

        contentArea.add(statsRow);
        contentArea.add(Box.createVerticalStrut(20));

        JPanel bookingsCard = createCard("All Bookings");
        List<Appointment> appointments = appointmentDAO.getAllAppointments();
        String[] columns = {"Student", "Teacher", "Date", "Time", "Status", "Action"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return col == 5; }
        };
        SimpleDateFormat dateFmt = new SimpleDateFormat("MMM dd, yyyy");
        SimpleDateFormat timeFmt = new SimpleDateFormat("h:mm a");
        for (Appointment a : appointments) {
            model.addRow(new Object[]{
                    a.getStudentName(), a.getTeacherName(),
                    dateFmt.format(a.getDate()), timeFmt.format(a.getTime()),
                    a.getStatus(), "Delete"
            });
        }
        JTable table = createStyledTable(model);
        table.getColumn("Action").setCellRenderer(new ButtonRenderer());
        table.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), appointments, table, this));
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(null);
        tableScroll.setPreferredSize(new Dimension(0, 250));
        bookingsCard.add(tableScroll);
        contentArea.add(bookingsCard);
        contentArea.add(Box.createVerticalStrut(20));

        JPanel usersCard = createCard("User Management");
        List<User> users = userDAO.getAllUsers();
        String[] userCols = {"Name", "Email", "Role", "Action"};
        DefaultTableModel userModel = new DefaultTableModel(userCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return col == 3; }
        };
        for (User u : users) {
            String action = u.getId() == SessionManager.getCurrentUser().getId() ? "(You)" : "Delete";
            userModel.addRow(new Object[]{u.getName(), u.getEmail(), u.getRole().substring(0, 1).toUpperCase() + u.getRole().substring(1), action});
        }
        JTable userTable = createStyledTable(userModel);
        userTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        userTable.getColumn("Action").setCellEditor(new UserButtonEditor(new JCheckBox(), users, userTable, this));
        JScrollPane userScroll = new JScrollPane(userTable);
        userScroll.setBorder(null);
        userScroll.setPreferredSize(new Dimension(0, 200));
        usersCard.add(userScroll);
        contentArea.add(usersCard);
    }

    private void buildTeacherDashboard() {
        int teacherId = SessionManager.getCurrentUser().getId();

        JPanel statsRow = new JPanel(new GridLayout(1, 3, 12, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        List<Appointment> upcoming = appointmentDAO.getTeacherUpcomingAppointments(teacherId);
        List<Appointment> past = appointmentDAO.getTeacherPastAppointments(teacherId);
        long pendingCount = upcoming.stream().filter(a -> "pending".equals(a.getStatus())).count();

        statsRow.add(createStatCard("Upcoming", String.valueOf(upcoming.size()), MainFrame.PRIMARY));
        statsRow.add(createStatCard("Pending", String.valueOf(pendingCount), MainFrame.WARNING));
        statsRow.add(createStatCard("Past", String.valueOf(past.size()), MainFrame.TEXT_GRAY));

        contentArea.add(statsRow);
        contentArea.add(Box.createVerticalStrut(20));

        JPanel upcomingCard = createCard("Upcoming Student Requests");
        String[] columns = {"Student", "Date", "Time", "Status", "Action"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return col == 4; }
        };
        SimpleDateFormat dateFmt = new SimpleDateFormat("MMM dd");
        SimpleDateFormat timeFmt = new SimpleDateFormat("h:mm a");
        for (Appointment a : upcoming) {
            model.addRow(new Object[]{
                    a.getStudentName(),
                    dateFmt.format(a.getDate()),
                    timeFmt.format(a.getTime()),
                    a.getStatus(), "Manage"
            });
        }
        JTable table = createStyledTable(model);
        table.getColumn("Action").setCellRenderer(new ButtonRenderer());
        table.getColumn("Action").setCellEditor(new TeacherActionEditor(new JCheckBox(), upcoming, table, this));
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(null);
        tableScroll.setPreferredSize(new Dimension(0, 220));
        upcomingCard.add(tableScroll);
        contentArea.add(upcomingCard);
        contentArea.add(Box.createVerticalStrut(20));

        JPanel pastCard = createCard("Past Appointments");
        String[] pastCols = {"Student", "Date", "Time", "Status", "Remarks"};
        DefaultTableModel pastModel = new DefaultTableModel(pastCols, 0);
        for (Appointment a : past) {
            pastModel.addRow(new Object[]{
                    a.getStudentName(),
                    dateFmt.format(a.getDate()),
                    timeFmt.format(a.getTime()),
                    a.getStatus(),
                    a.getNotes() != null ? a.getNotes() : "No remarks"
            });
        }
        JTable pastTable = createStyledTable(pastModel);
        pastTable.setEnabled(false);
        JScrollPane pastScroll = new JScrollPane(pastTable);
        pastScroll.setBorder(null);
        pastScroll.setPreferredSize(new Dimension(0, 180));
        pastCard.add(pastScroll);
        contentArea.add(pastCard);
    }

    private void buildStudentDashboard() {
        int studentId = SessionManager.getCurrentUser().getId();

        List<Appointment> appointments = appointmentDAO.getStudentAppointments(studentId);
        long pendingCount = appointments.stream().filter(a -> "pending".equals(a.getStatus())).count();
        long approvedCount = appointments.stream().filter(a -> "approved".equals(a.getStatus())).count();

        JPanel statsRow = new JPanel(new GridLayout(1, 3, 12, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        statsRow.add(createStatCard("Total", String.valueOf(appointments.size()), MainFrame.PRIMARY));
        statsRow.add(createStatCard("Pending", String.valueOf(pendingCount), MainFrame.WARNING));
        statsRow.add(createStatCard("Approved", String.valueOf(approvedCount), MainFrame.SUCCESS));

        contentArea.add(statsRow);
        contentArea.add(Box.createVerticalStrut(20));

        JPanel card = createCard("My Appointments");
        String[] columns = {"Teacher", "Date", "Time", "Status", "Remarks", "Action"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return col == 5; }
        };
        SimpleDateFormat dateFmt = new SimpleDateFormat("MMM dd, yyyy");
        SimpleDateFormat timeFmt = new SimpleDateFormat("h:mm a");
        for (Appointment a : appointments) {
            String action = "pending".equals(a.getStatus()) ? "Cancel" : "";
            model.addRow(new Object[]{
                    a.getTeacherName(),
                    dateFmt.format(a.getDate()),
                    timeFmt.format(a.getTime()),
                    a.getStatus(),
                    a.getNotes() != null ? a.getNotes() : "None",
                    action
            });
        }
        JTable table = createStyledTable(model);
        table.getColumn("Action").setCellRenderer(new ButtonRenderer());
        table.getColumn("Action").setCellEditor(new StudentCancelEditor(new JCheckBox(), appointments, table, this));
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(null);
        tableScroll.setPreferredSize(new Dimension(0, 300));
        card.add(tableScroll);
        contentArea.add(card);
    }

    private JPanel createStatCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(accentColor);
                g2.fillRect(0, 10, 4, getHeight() - 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 16));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        titleLbl.setForeground(MainFrame.TEXT_GRAY);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("SansSerif", Font.BOLD, 28));
        valueLbl.setForeground(MainFrame.TEXT_DARK);
        valueLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(valueLbl);

        return card;
    }

    JPanel createCard(String title) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLbl.setForeground(MainFrame.PRIMARY);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titleLbl);
        card.add(Box.createVerticalStrut(12));

        return card;
    }

    JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setGridColor(new Color(240, 240, 240));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(245, 243, 255));
        table.setSelectionForeground(MainFrame.TEXT_DARK);
        table.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setBackground(Color.WHITE);
        header.setForeground(MainFrame.TEXT_GRAY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, MainFrame.PRIMARY));
        header.setPreferredSize(new Dimension(0, 36));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.LEFT);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(new StatusCellRenderer());
        }

        return table;
    }

    @Override
    public void refresh() {
        removeAll();
        buildUI();
        revalidate();
        repaint();
    }

    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof String) {
                String text = (String) value;
                switch (text.toLowerCase()) {
                    case "pending": setForeground(new Color(133, 100, 4)); break;
                    case "approved": setForeground(new Color(21, 87, 36)); break;
                    case "rejected": setForeground(new Color(114, 28, 36)); break;
                    case "cancelled": setForeground(new Color(108, 117, 125)); break;
                    case "completed": setForeground(new Color(12, 84, 96)); break;
                    default: setForeground(MainFrame.TEXT_DARK);
                }
            }
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
            return c;
        }
    }

    private static class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setFont(new Font("SansSerif", Font.BOLD, 11));
            setBorderPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            String text = value != null ? value.toString() : "";
            setText(text);
            if ("Delete".equals(text) || "Cancel".equals(text)) {
                setForeground(MainFrame.ERROR);
                setBackground(Color.WHITE);
            } else if ("Manage".equals(text)) {
                setForeground(MainFrame.PRIMARY);
                setBackground(Color.WHITE);
            } else {
                setForeground(MainFrame.TEXT_GRAY);
                setBackground(Color.WHITE);
            }
            return this;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private List<Appointment> appointments;
        private JTable table;
        private DashboardPanel panel;
        private int clickedRow;

        public ButtonEditor(JCheckBox checkBox, List<Appointment> appointments, JTable table, DashboardPanel panel) {
            super(checkBox);
            this.appointments = appointments;
            this.table = table;
            this.panel = panel;
            button = new JButton();
            button.setFont(new Font("SansSerif", Font.BOLD, 11));
            button.setBorderPainted(false);
            button.addActionListener(e -> {
                fireEditingStopped();
                if (clickedRow >= 0 && clickedRow < appointments.size()) {
                    Appointment a = appointments.get(clickedRow);
                    int confirm = JOptionPane.showConfirmDialog(panel,
                            "Delete this appointment?", "Confirm",
                            JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        appointmentDAO.deleteAppointment(a.getId());
                        panel.refresh();
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            clickedRow = row;
            button.setText(value != null ? value.toString() : "");
            button.setForeground(MainFrame.ERROR);
            button.setBackground(Color.WHITE);
            return button;
        }

        @Override
        public Object getCellEditorValue() { return button.getText(); }
    }

    private class UserButtonEditor extends DefaultCellEditor {
        private JButton button;
        private List<User> users;
        private JTable table;
        private DashboardPanel panel;
        private int clickedRow;

        public UserButtonEditor(JCheckBox checkBox, List<User> users, JTable table, DashboardPanel panel) {
            super(checkBox);
            this.users = users;
            this.table = table;
            this.panel = panel;
            button = new JButton();
            button.setFont(new Font("SansSerif", Font.BOLD, 11));
            button.setBorderPainted(false);
            button.addActionListener(e -> {
                fireEditingStopped();
                if (clickedRow >= 0 && clickedRow < users.size()) {
                    User u = users.get(clickedRow);
                    if (u.getId() == SessionManager.getCurrentUser().getId()) return;
                    int confirm = JOptionPane.showConfirmDialog(panel,
                            "Delete user '" + u.getName() + "'?", "Confirm",
                            JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        userDAO.deleteUser(u.getId());
                        panel.refresh();
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            clickedRow = row;
            String text = value != null ? value.toString() : "";
            button.setText(text);
            button.setForeground("(You)".equals(text) ? MainFrame.TEXT_GRAY : MainFrame.ERROR);
            button.setBackground(Color.WHITE);
            return button;
        }

        @Override
        public Object getCellEditorValue() { return button.getText(); }
    }

    private class TeacherActionEditor extends DefaultCellEditor {
        private JButton button;
        private List<Appointment> appointments;
        private JTable table;
        private DashboardPanel panel;
        private int clickedRow;
        private com.appointmentsystem.dao.NotificationDAO notificationDAO = new com.appointmentsystem.dao.NotificationDAO();

        public TeacherActionEditor(JCheckBox checkBox, List<Appointment> appointments, JTable table, DashboardPanel panel) {
            super(checkBox);
            this.appointments = appointments;
            this.table = table;
            this.panel = panel;
            button = new JButton("Manage");
            button.setFont(new Font("SansSerif", Font.BOLD, 11));
            button.setBorderPainted(false);
            button.setForeground(MainFrame.PRIMARY);
            button.addActionListener(e -> {
                fireEditingStopped();
                if (clickedRow >= 0 && clickedRow < appointments.size()) {
                    Appointment a = appointments.get(clickedRow);
                    showTeacherActionDialog(a);
                }
            });
        }

        private void showTeacherActionDialog(Appointment a) {
            JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(panel), "Manage Appointment", Dialog.ModalityType.APPLICATION_MODAL);
            dialog.setSize(380, 260);
            dialog.setLocationRelativeTo(panel);

            JPanel dialogPanel = new JPanel();
            dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
            dialogPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
            dialogPanel.setBackground(Color.WHITE);

            JLabel titleLbl = new JLabel("Update: " + a.getStudentName());
            titleLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
            titleLbl.setForeground(MainFrame.PRIMARY);
            titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            dialogPanel.add(titleLbl);
            dialogPanel.add(Box.createVerticalStrut(15));

            JComboBox<String> statusBox = new JComboBox<>(new String[]{"approved", "rejected", "completed"});
            statusBox.setFont(new Font("SansSerif", Font.PLAIN, 13));
            statusBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            statusBox.setAlignmentX(Component.LEFT_ALIGNMENT);
            dialogPanel.add(new JLabel("Status:"));
            dialogPanel.add(statusBox);
            dialogPanel.add(Box.createVerticalStrut(10));

            JTextField notesField = new JTextField();
            notesField.setFont(new Font("SansSerif", Font.PLAIN, 13));
            notesField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            notesField.setAlignmentX(Component.LEFT_ALIGNMENT);
            dialogPanel.add(new JLabel("Remarks:"));
            dialogPanel.add(notesField);
            dialogPanel.add(Box.createVerticalStrut(15));

            JButton saveBtn = new JButton("Update");
            saveBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
            saveBtn.setForeground(Color.WHITE);
            saveBtn.setBackground(MainFrame.PRIMARY);
            saveBtn.setBorderPainted(false);
            saveBtn.setFocusPainted(false);
            saveBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
            saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            saveBtn.addActionListener(ev -> {
                String newStatus = (String) statusBox.getSelectedItem();
                String notes = notesField.getText().trim();
                appointmentDAO.updateStatus(a.getId(), newStatus, notes);
                String notifMsg = "Your appointment on " + a.getDate() + " has been " + newStatus
                        + (notes.isEmpty() ? "" : ". Remarks: " + notes);
                notificationDAO.addNotification(a.getStudentId(), notifMsg);
                dialog.dispose();
                panel.refresh();
            });
            dialogPanel.add(saveBtn);

            dialog.setContentPane(dialogPanel);
            dialog.setVisible(true);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            clickedRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() { return "Manage"; }
    }

    private class StudentCancelEditor extends DefaultCellEditor {
        private JButton button;
        private List<Appointment> appointments;
        private JTable table;
        private DashboardPanel panel;
        private int clickedRow;

        public StudentCancelEditor(JCheckBox checkBox, List<Appointment> appointments, JTable table, DashboardPanel panel) {
            super(checkBox);
            this.appointments = appointments;
            this.table = table;
            this.panel = panel;
            button = new JButton("Cancel");
            button.setFont(new Font("SansSerif", Font.BOLD, 11));
            button.setBorderPainted(false);
            button.setForeground(MainFrame.ERROR);
            button.addActionListener(e -> {
                fireEditingStopped();
                if (clickedRow >= 0 && clickedRow < appointments.size()) {
                    Appointment a = appointments.get(clickedRow);
                    if (!"pending".equals(a.getStatus())) return;
                    int confirm = JOptionPane.showConfirmDialog(panel,
                            "Cancel this appointment?", "Confirm",
                            JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        appointmentDAO.cancelAppointment(a.getId(), SessionManager.getCurrentUser().getId());
                        panel.refresh();
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            clickedRow = row;
            String text = value != null ? value.toString() : "";
            button.setText(text);
            button.setEnabled(!text.isEmpty());
            return button;
        }

        @Override
        public Object getCellEditorValue() { return button.getText(); }
    }
}
