package com.appointmentsystem.ui;

import com.appointmentsystem.dao.AppointmentDAO;
import com.appointmentsystem.model.Appointment;
import com.appointmentsystem.util.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class CalendarPanel extends JPanel implements MainFrame.Refreshable {
    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private Calendar currentMonth;
    private JPanel calendarGrid;
    private JLabel monthLabel;
    private JPanel appointmentListPanel;
    private Date selectedDate;

    public CalendarPanel() {
        currentMonth = Calendar.getInstance();
        selectedDate = new Date(System.currentTimeMillis());
        setLayout(new BorderLayout());
        setBackground(MainFrame.BG_LIGHT);
        buildUI();
    }

    private void buildUI() {
        removeAll();

        JPanel mainContent = new JPanel(new BorderLayout(16, 0));
        mainContent.setBackground(MainFrame.BG_LIGHT);
        mainContent.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(480, 0));

        JLabel titleLabel = new JLabel("Calendar");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(MainFrame.TEXT_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Click on a date to view appointments");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitleLabel.setForeground(MainFrame.TEXT_GRAY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(subtitleLabel);
        leftPanel.add(Box.createVerticalStrut(16));

        JPanel calendarCard = createCalendarCard();
        leftPanel.add(calendarCard);

        mainContent.add(leftPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(380, 0));

        appointmentListPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
            }
        };
        appointmentListPanel.setOpaque(false);
        appointmentListPanel.setLayout(new BoxLayout(appointmentListPanel, BoxLayout.Y_AXIS));
        appointmentListPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        updateAppointmentList();

        JScrollPane scroll = new JScrollPane(appointmentListPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        rightPanel.add(scroll);

        mainContent.add(rightPanel, BorderLayout.EAST);

        add(mainContent, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel createCalendarCard() {
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
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setOpaque(false);
        navPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        navPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton prevBtn = createNavButton("\u25C0");
        prevBtn.addActionListener(e -> {
            currentMonth.add(Calendar.MONTH, -1);
            refreshCalendar();
        });

        SimpleDateFormat monthFmt = new SimpleDateFormat("MMMM yyyy");
        monthLabel = new JLabel(monthFmt.format(currentMonth.getTime()), SwingConstants.CENTER);
        monthLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        monthLabel.setForeground(MainFrame.TEXT_DARK);

        JButton nextBtn = createNavButton("\u25B6");
        nextBtn.addActionListener(e -> {
            currentMonth.add(Calendar.MONTH, 1);
            refreshCalendar();
        });

        navPanel.add(prevBtn, BorderLayout.WEST);
        navPanel.add(monthLabel, BorderLayout.CENTER);
        navPanel.add(nextBtn, BorderLayout.EAST);
        card.add(navPanel);
        card.add(Box.createVerticalStrut(12));

        JPanel dayHeaders = new JPanel(new GridLayout(1, 7, 4, 0));
        dayHeaders.setOpaque(false);
        dayHeaders.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        dayHeaders.setAlignmentX(Component.LEFT_ALIGNMENT);
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : days) {
            JLabel lbl = new JLabel(day, SwingConstants.CENTER);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
            lbl.setForeground(MainFrame.TEXT_GRAY);
            dayHeaders.add(lbl);
        }
        card.add(dayHeaders);
        card.add(Box.createVerticalStrut(8));

        calendarGrid = new JPanel(new GridLayout(6, 7, 4, 4));
        calendarGrid.setOpaque(false);
        calendarGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        fillCalendarGrid();
        card.add(calendarGrid);

        return card;
    }

    private void fillCalendarGrid() {
        calendarGrid.removeAll();

        Calendar cal = (Calendar) currentMonth.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;

        List<Date> datesWithAppts;
        String role = SessionManager.getRole();
        int userId = SessionManager.getCurrentUser().getId();
        if ("admin".equals(role)) {
            datesWithAppts = appointmentDAO.getDatesWithAppointments(year, month);
        } else {
            datesWithAppts = appointmentDAO.getDatesWithAppointmentsForUser(year, month, userId, role);
        }

        Calendar today = Calendar.getInstance();
        int todayDay = today.get(Calendar.DAY_OF_MONTH);
        int todayMonth = today.get(Calendar.MONTH);
        int todayYear = today.get(Calendar.YEAR);

        Calendar selectedCal = Calendar.getInstance();
        if (selectedDate != null) {
            selectedCal.setTime(selectedDate);
        }

        for (int i = 0; i < 42; i++) {
            int dayNum = i - firstDayOfWeek + 1;
            if (dayNum < 1 || dayNum > daysInMonth) {
                JLabel empty = new JLabel("");
                empty.setPreferredSize(new Dimension(50, 42));
                calendarGrid.add(empty);
            } else {
                final int day = dayNum;
                boolean isToday = (day == todayDay && cal.get(Calendar.MONTH) == todayMonth && year == todayYear);

                Calendar dayCal = Calendar.getInstance();
                dayCal.set(year, month - 1, day);
                Date dayDate = new Date(dayCal.getTimeInMillis());

                boolean hasAppt = datesWithAppts.stream().anyMatch(d -> {
                    Calendar dc = Calendar.getInstance();
                    dc.setTime(d);
                    return dc.get(Calendar.DAY_OF_MONTH) == day;
                });

                boolean isSelected = selectedDate != null && selectedCal.get(Calendar.DAY_OF_MONTH) == day
                        && selectedCal.get(Calendar.MONTH) == cal.get(Calendar.MONTH)
                        && selectedCal.get(Calendar.YEAR) == year;

                JButton dayBtn = new JButton(String.valueOf(day)) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        if (isSelected) {
                            g2.setColor(MainFrame.PRIMARY);
                            g2.fill(new RoundRectangle2D.Double(4, 2, getWidth() - 8, getHeight() - 4, 10, 10));
                        } else if (getModel().isRollover()) {
                            g2.setColor(new Color(245, 243, 255));
                            g2.fill(new RoundRectangle2D.Double(4, 2, getWidth() - 8, getHeight() - 4, 10, 10));
                        }

                        g2.dispose();
                        super.paintComponent(g);

                        if (hasAppt) {
                            Graphics2D g3 = (Graphics2D) g.create();
                            g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g3.setColor(isSelected ? Color.WHITE : MainFrame.PRIMARY);
                            g3.fillOval(getWidth() / 2 - 3, getHeight() - 10, 6, 6);
                            g3.dispose();
                        }
                    }
                };
                dayBtn.setFont(new Font("SansSerif", isToday ? Font.BOLD : Font.PLAIN, 13));
                dayBtn.setForeground(isSelected ? Color.WHITE : (isToday ? MainFrame.PRIMARY : MainFrame.TEXT_DARK));
                dayBtn.setBorderPainted(false);
                dayBtn.setContentAreaFilled(false);
                dayBtn.setFocusPainted(false);
                dayBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                dayBtn.setPreferredSize(new Dimension(50, 42));
                dayBtn.addActionListener(e -> {
                    selectedDate = dayDate;
                    refreshCalendar();
                    updateAppointmentList();
                });
                calendarGrid.add(dayBtn);
            }
        }
    }

    private void refreshCalendar() {
        SimpleDateFormat monthFmt = new SimpleDateFormat("MMMM yyyy");
        monthLabel.setText(monthFmt.format(currentMonth.getTime()));
        fillCalendarGrid();
        calendarGrid.revalidate();
        calendarGrid.repaint();
    }

    private void updateAppointmentList() {
        appointmentListPanel.removeAll();

        SimpleDateFormat dateFmt = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
        JLabel dateTitle = new JLabel(dateFmt.format(selectedDate));
        dateTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        dateTitle.setForeground(MainFrame.PRIMARY);
        dateTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        appointmentListPanel.add(dateTitle);
        appointmentListPanel.add(Box.createVerticalStrut(4));

        JLabel subtitle = new JLabel("Appointments for this day");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(MainFrame.TEXT_GRAY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        appointmentListPanel.add(subtitle);
        appointmentListPanel.add(Box.createVerticalStrut(16));

        String role = SessionManager.getRole();
        int userId = SessionManager.getCurrentUser().getId();
        List<Appointment> appts;
        if ("admin".equals(role)) {
            appts = appointmentDAO.getAppointmentsByDate(selectedDate);
        } else {
            appts = appointmentDAO.getAppointmentsByDateAndUser(selectedDate, userId, role);
        }

        if (appts.isEmpty()) {
            JLabel noAppts = new JLabel("No appointments on this day.");
            noAppts.setFont(new Font("SansSerif", Font.ITALIC, 13));
            noAppts.setForeground(MainFrame.TEXT_GRAY);
            noAppts.setAlignmentX(Component.LEFT_ALIGNMENT);
            appointmentListPanel.add(noAppts);
        } else {
            SimpleDateFormat timeFmt = new SimpleDateFormat("h:mm a");
            for (Appointment a : appts) {
                JPanel apptCard = createAppointmentCard(a, timeFmt, role);
                appointmentListPanel.add(apptCard);
                appointmentListPanel.add(Box.createVerticalStrut(8));
            }
        }

        appointmentListPanel.revalidate();
        appointmentListPanel.repaint();
    }

    private JPanel createAppointmentCard(Appointment a, SimpleDateFormat timeFmt, String role) {
        Color statusColor;
        switch (a.getStatus()) {
            case "approved": statusColor = MainFrame.SUCCESS; break;
            case "pending": statusColor = MainFrame.WARNING; break;
            case "rejected": statusColor = MainFrame.ERROR; break;
            case "completed": statusColor = new Color(0, 150, 137); break;
            default: statusColor = MainFrame.TEXT_GRAY;
        }

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(244, 244, 245));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(statusColor);
                g2.fillRect(0, 6, 4, getHeight() - 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 12));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        String personLabel;
        if ("student".equals(role)) {
            personLabel = "Teacher: " + (a.getTeacherName() != null ? a.getTeacherName() : "N/A");
        } else if ("teacher".equals(role)) {
            personLabel = "Student: " + (a.getStudentName() != null ? a.getStudentName() : "N/A");
        } else {
            personLabel = (a.getStudentName() != null ? a.getStudentName() : "?") + " \u2192 " + (a.getTeacherName() != null ? a.getTeacherName() : "?");
        }

        JLabel nameLabel = new JLabel(personLabel);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        nameLabel.setForeground(MainFrame.TEXT_DARK);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel timeLabel = new JLabel(timeFmt.format(a.getTime()) + "  \u2022  " + a.getStatus().toUpperCase());
        timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        timeLabel.setForeground(statusColor);
        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(nameLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(timeLabel);

        return card;
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btn.setForeground(MainFrame.TEXT_DARK);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(40, 36));
        return btn;
    }

    @Override
    public void refresh() {
        buildUI();
    }
}
