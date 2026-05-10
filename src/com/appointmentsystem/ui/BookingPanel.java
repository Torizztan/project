package com.appointmentsystem.ui;

import com.appointmentsystem.dao.AppointmentDAO;
import com.appointmentsystem.dao.NotificationDAO;
import com.appointmentsystem.dao.UserDAO;
import com.appointmentsystem.model.Appointment;
import com.appointmentsystem.model.User;
import com.appointmentsystem.util.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class BookingPanel extends JPanel implements MainFrame.Refreshable {
    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private UserDAO userDAO = new UserDAO();
    private NotificationDAO notificationDAO = new NotificationDAO();

    public BookingPanel() {
        setLayout(new BorderLayout());
        setBackground(MainFrame.BG_LIGHT);
        buildUI();
    }

    private void buildUI() {
        removeAll();

        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(MainFrame.BG_LIGHT);
        mainContent.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel titleLabel = new JLabel("Bookings");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(MainFrame.TEXT_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainContent.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Manage your appointments");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitleLabel.setForeground(MainFrame.TEXT_GRAY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainContent.add(subtitleLabel);
        mainContent.add(Box.createVerticalStrut(16));

        String role = SessionManager.getRole();

        if ("student".equals(role)) {
            JButton bookBtn = createPrimaryButton("+ Book New Appointment");
            bookBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
            bookBtn.setMaximumSize(new Dimension(240, 40));
            bookBtn.addActionListener(e -> showBookingDialog());
            mainContent.add(bookBtn);
            mainContent.add(Box.createVerticalStrut(16));
        }

        JPanel card = createCard("All Bookings");

        List<Appointment> appointments;
        String[] columns;

        if ("admin".equals(role)) {
            appointments = appointmentDAO.getAllAppointments();
            columns = new String[]{"Student", "Teacher", "Date", "Time", "Status", "Notes"};
        } else if ("teacher".equals(role)) {
            int teacherId = SessionManager.getCurrentUser().getId();
            appointments = appointmentDAO.getTeacherUpcomingAppointments(teacherId);
            List<Appointment> past = appointmentDAO.getTeacherPastAppointments(teacherId);
            appointments.addAll(past);
            columns = new String[]{"Student", "Date", "Time", "Status", "Notes"};
        } else {
            appointments = appointmentDAO.getStudentAppointments(SessionManager.getCurrentUser().getId());
            columns = new String[]{"Teacher", "Date", "Time", "Status", "Notes", "Action"};
        }

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return "student".equals(SessionManager.getRole()) && col == columns.length - 1;
            }
        };

        SimpleDateFormat dateFmt = new SimpleDateFormat("MMM dd, yyyy");
        SimpleDateFormat timeFmt = new SimpleDateFormat("h:mm a");

        for (Appointment a : appointments) {
            if ("admin".equals(role)) {
                model.addRow(new Object[]{
                        a.getStudentName(), a.getTeacherName(),
                        dateFmt.format(a.getDate()), timeFmt.format(a.getTime()),
                        a.getStatus(), a.getNotes() != null ? a.getNotes() : ""
                });
            } else if ("teacher".equals(role)) {
                model.addRow(new Object[]{
                        a.getStudentName(),
                        dateFmt.format(a.getDate()), timeFmt.format(a.getTime()),
                        a.getStatus(), a.getNotes() != null ? a.getNotes() : ""
                });
            } else {
                String action = "pending".equals(a.getStatus()) ? "Cancel" : "";
                model.addRow(new Object[]{
                        a.getTeacherName(),
                        dateFmt.format(a.getDate()), timeFmt.format(a.getTime()),
                        a.getStatus(), a.getNotes() != null ? a.getNotes() : "",
                        action
                });
            }
        }

        JTable table = createStyledTable(model);
        if ("student".equals(role)) {
            table.getColumn("Action").setCellRenderer(new ButtonRenderer());
            table.getColumn("Action").setCellEditor(new CancelEditor(new JCheckBox(), appointments));
        }

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(null);
        tableScroll.setPreferredSize(new Dimension(0, 400));
        tableScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(tableScroll);
        mainContent.add(card);

        JScrollPane scroll = new JScrollPane(mainContent);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private void showBookingDialog() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
                "Book New Appointment", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(400, 380);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        panel.setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(MainFrame.PRIMARY);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.setLayout(new BorderLayout());
        JLabel headerLabel = new JLabel("New Appointment");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel, BorderLayout.CENTER);
        panel.add(headerPanel);
        panel.add(Box.createVerticalStrut(20));

        List<User> teachers = userDAO.getTeachers();
        JComboBox<User> teacherBox = new JComboBox<>();
        teacherBox.addItem(null);
        for (User t : teachers) {
            teacherBox.addItem(t);
        }
        teacherBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "-- Choose Teacher --" : ((User) value).getName());
                return this;
            }
        });
        teacherBox.setFont(new Font("SansSerif", Font.PLAIN, 13));
        teacherBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        teacherBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel teacherLabel = new JLabel("Teacher");
        teacherLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        teacherLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(teacherLabel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(teacherBox);
        panel.add(Box.createVerticalStrut(12));

        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD)");
        dateLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(dateLabel);
        panel.add(Box.createVerticalStrut(4));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        JTextField dateField = new JTextField(sdf.format(new java.util.Date()));
        dateField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        dateField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        dateField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(dateField);
        panel.add(Box.createVerticalStrut(12));

        JLabel timeLabel = new JLabel("Time (HH:MM, 24-hour format)");
        timeLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(timeLabel);
        panel.add(Box.createVerticalStrut(4));

        JTextField timeField = new JTextField("09:00");
        timeField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        timeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        timeField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(timeField);
        panel.add(Box.createVerticalStrut(16));

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        errorLabel.setForeground(MainFrame.ERROR);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(errorLabel);
        panel.add(Box.createVerticalStrut(4));

        JButton confirmBtn = createPrimaryButton("Confirm Booking");
        confirmBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        confirmBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmBtn.addActionListener(e -> {
            User selectedTeacher = (User) teacherBox.getSelectedItem();
            if (selectedTeacher == null) {
                errorLabel.setText("Please select a teacher.");
                return;
            }
            try {
                Date date = Date.valueOf(dateField.getText().trim());
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                Calendar today = Calendar.getInstance();
                today.set(Calendar.HOUR_OF_DAY, 0);
                today.set(Calendar.MINUTE, 0);
                today.set(Calendar.SECOND, 0);
                today.set(Calendar.MILLISECOND, 0);
                if (cal.before(today)) {
                    errorLabel.setText("Cannot book past dates.");
                    return;
                }

                String timeStr = timeField.getText().trim();
                if (!timeStr.matches("\\d{2}:\\d{2}")) {
                    errorLabel.setText("Invalid time format. Use HH:MM.");
                    return;
                }
                Time time = Time.valueOf(timeStr + ":00");

                int studentId = SessionManager.getCurrentUser().getId();
                if (appointmentDAO.createAppointment(studentId, selectedTeacher.getId(), date, time)) {
                    notificationDAO.addNotification(selectedTeacher.getId(),
                            "New appointment request from " + SessionManager.getCurrentUser().getName()
                                    + " on " + dateField.getText() + " at " + timeStr);
                    JOptionPane.showMessageDialog(dialog, "Appointment booked successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refresh();
                } else {
                    errorLabel.setText("Time slot conflict! Choose another time.");
                }
            } catch (IllegalArgumentException ex) {
                errorLabel.setText("Invalid date format. Use YYYY-MM-DD.");
            }
        });
        panel.add(confirmBtn);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private JPanel createCard(String title) {
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

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setGridColor(new Color(240, 240, 240));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(245, 243, 255));
        table.setSelectionForeground(MainFrame.TEXT_DARK);

        javax.swing.table.JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setBackground(Color.WHITE);
        header.setForeground(MainFrame.TEXT_GRAY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, MainFrame.PRIMARY));
        header.setPreferredSize(new Dimension(0, 36));

        return table;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(MainFrame.PRIMARY.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(MainFrame.PRIMARY.brighter());
                } else {
                    g2.setColor(MainFrame.PRIMARY);
                }
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    @Override
    public void refresh() {
        buildUI();
    }

    private class CancelEditor extends DefaultCellEditor {
        private JButton button;
        private List<Appointment> appointments;
        private int clickedRow;

        public CancelEditor(JCheckBox checkBox, List<Appointment> appointments) {
            super(checkBox);
            this.appointments = appointments;
            button = new JButton("Cancel");
            button.setFont(new Font("SansSerif", Font.BOLD, 11));
            button.setForeground(MainFrame.ERROR);
            button.setBorderPainted(false);
            button.addActionListener(e -> {
                fireEditingStopped();
                if (clickedRow >= 0 && clickedRow < appointments.size()) {
                    Appointment a = appointments.get(clickedRow);
                    if (!"pending".equals(a.getStatus())) return;
                    int confirm = JOptionPane.showConfirmDialog(BookingPanel.this,
                            "Cancel this appointment?", "Confirm", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        appointmentDAO.cancelAppointment(a.getId(), SessionManager.getCurrentUser().getId());
                        refresh();
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

    private static class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setFont(new Font("SansSerif", Font.BOLD, 11));
            setBorderPainted(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            String text = value != null ? value.toString() : "";
            setText(text);
            setForeground(MainFrame.ERROR);
            setBackground(Color.WHITE);
            return this;
        }
    }
}
