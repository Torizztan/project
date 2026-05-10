# Appointment and Schedule Management System

A Java Swing desktop application for managing appointments and schedules between students and teachers. Built with **Java**, **MySQL (XAMPP)**, and **FlatLaf** for a modern UI.

---

## Features

- **Login & Registration** - Sign in / create account with role selection (Student, Teacher, Admin)
- **Dashboard** - Role-specific dashboard with stats and appointment tables
- **Calendar** - Interactive clickable calendar showing appointments per day
- **Bookings** - Book new appointments (students), manage requests (teachers), view all (admin)
- **Notifications** - Real-time notifications for appointment status changes
- **Settings** - Update profile name and password
- **Admin Panel** - User management, delete appointments, view all stats

## Roles

| Role    | Capabilities                                                    |
|---------|----------------------------------------------------------------|
| Student | Book appointments, cancel pending, view own appointments       |
| Teacher | Approve/reject/complete student requests, add remarks          |
| Admin   | View all appointments & users, delete records, view stats      |

## Default Accounts

| Role    | Email              | Password    |
|---------|--------------------|-------------|
| Admin   | admin@system.com   | admin123    |
| Teacher | smith@school.com   | teacher123  |
| Student | john@student.com   | student123  |

---

## Step-by-Step Setup Guide

### PART 1: Setting Up the Database (XAMPP)

1. **Open XAMPP Control Panel**
   - Launch XAMPP from your Start Menu or Desktop
   - Click **Start** next to **Apache**
   - Click **Start** next to **MySQL**
   - Both should turn green (running)

2. **Open phpMyAdmin**
   - Click the **Admin** button next to MySQL, OR
   - Open your browser and go to: `http://localhost/phpmyadmin`

3. **Create the Database**
   - In phpMyAdmin, click the **SQL** tab at the top
   - Copy and paste the ENTIRE contents of the file `sql/database_setup.sql` into the SQL text box
   - Click **Go** to execute
   - You should see a success message
   - On the left sidebar, you should now see a database called `appointment_db` with 3 tables:
     - `users`
     - `appointments`
     - `notifications`

4. **Verify the Database**
   - Click on `appointment_db` in the left sidebar
   - Click on the `users` table
   - You should see 3 default accounts (Admin, Dr. Smith, John Doe)

---

### PART 2: Setting Up the Project in NetBeans

1. **Create a New Project**
   - Open NetBeans IDE
   - Go to **File** > **New Project...**
   - Select **Java with Ant** > **Java Application**
   - Click **Next**
   - Project Name: `AppointmentSystem`
   - Uncheck "Create Main Class" (we already have one)
   - Click **Finish**

2. **Copy Source Files**
   - In your file explorer, navigate to the `src` folder from this repository
   - Copy the entire `com` folder
   - Paste it into your NetBeans project's `src` folder
     - The path should be: `NetBeansProjects/AppointmentSystem/src/com/appointmentsystem/...`
   - Your project structure should look like:
     ```
     AppointmentSystem/
     └── src/
         └── com/
             └── appointmentsystem/
                 ├── Main.java
                 ├── db/
                 │   └── DatabaseConnection.java
                 ├── model/
                 │   ├── User.java
                 │   ├── Appointment.java
                 │   └── Notification.java
                 ├── dao/
                 │   ├── UserDAO.java
                 │   ├── AppointmentDAO.java
                 │   └── NotificationDAO.java
                 ├── ui/
                 │   ├── LoginFrame.java
                 │   ├── MainFrame.java
                 │   ├── DashboardPanel.java
                 │   ├── CalendarPanel.java
                 │   ├── BookingPanel.java
                 │   ├── NotificationPanel.java
                 │   └── SettingsPanel.java
                 └── util/
                     └── SessionManager.java
     ```

3. **Add Libraries (FlatLaf & MySQL Connector)**

   You mentioned you already have these JARs. If not, download them:
   - **FlatLaf**: https://repo1.maven.org/maven2/com/formdev/flatlaf/3.4.1/flatlaf-3.4.1.jar
   - **MySQL Connector/J**: https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.3.0/mysql-connector-j-8.3.0.jar

   To add them in NetBeans:
   - Right-click your project name (`AppointmentSystem`) in the Projects panel
   - Select **Properties**
   - In the left menu, click **Libraries**
   - Click **Add JAR/Folder**
   - Navigate to and select `flatlaf-3.4.1.jar` (or your version)
   - Click **Open**
   - Click **Add JAR/Folder** again
   - Navigate to and select `mysql-connector-j-8.3.0.jar` (or your version, e.g., `mysql-connector-java-8.x.x.jar`)
   - Click **Open**
   - Click **OK** to close the Properties window

4. **Set the Main Class**
   - Right-click the project > **Properties**
   - Click **Run** in the left menu
   - In **Main Class**, type: `com.appointmentsystem.Main`
   - Click **OK**

5. **Run the Application**
   - Make sure XAMPP MySQL is running (green)
   - Press the green **Run** button (or press `F6`)
   - The Login window should appear
   - Try logging in with: `admin@system.com` / `admin123`

---

### PART 3: How to Use the Application

#### As a Student:
1. Login with a student account (or register a new one)
2. Click **Bookings** in the sidebar > **"+ Book New Appointment"**
3. Select a teacher, pick a date and time, click **Confirm**
4. View your appointments on the **Dashboard** or **Bookings** page
5. Cancel pending appointments by clicking **Cancel**
6. Check **Calendar** to see your appointments on specific dates
7. Check **Notifications** for status updates from teachers

#### As a Teacher:
1. Login with a teacher account
2. View **Dashboard** to see upcoming student requests
3. Click **Manage** on an appointment to approve/reject/complete it
4. Add remarks when updating status
5. View past appointments in the Dashboard
6. Check **Calendar** to see your schedule

#### As an Admin:
1. Login with admin account
2. **Dashboard** shows all system stats (users, pending, approved, etc.)
3. View and delete any appointment
4. Manage users (view/delete)
5. **Calendar** shows all appointments across the system

---

### Troubleshooting

| Problem | Solution |
|---------|----------|
| "MySQL JDBC Driver not found" | Make sure `mysql-connector-j-x.x.x.jar` is added to Libraries |
| "Connection failed" | Make sure XAMPP MySQL is running (green). Check port 3306 |
| "Access denied" | The app connects as `root` with no password (XAMPP default). If you set a MySQL password, update `DatabaseConnection.java` |
| "Unknown database" | Run the `sql/database_setup.sql` script in phpMyAdmin first |
| Blank/white screen | Check if FlatLaf JAR is properly added to Libraries |
| Table is empty | Make sure you ran the SQL script to create tables and sample data |

---

## Project Structure

```
src/com/appointmentsystem/
├── Main.java                 - Application entry point (sets up FlatLaf theme)
├── db/
│   └── DatabaseConnection.java - MySQL connection handler
├── model/
│   ├── User.java             - User data model
│   ├── Appointment.java      - Appointment data model
│   └── Notification.java     - Notification data model
├── dao/
│   ├── UserDAO.java          - User database operations
│   ├── AppointmentDAO.java   - Appointment database operations
│   └── NotificationDAO.java  - Notification database operations
├── ui/
│   ├── LoginFrame.java       - Login & Registration window
│   ├── MainFrame.java        - Main window with sidebar navigation
│   ├── DashboardPanel.java   - Role-specific dashboard
│   ├── CalendarPanel.java    - Interactive clickable calendar
│   ├── BookingPanel.java     - Booking management
│   ├── NotificationPanel.java - Notifications list
│   └── SettingsPanel.java    - Account settings
└── util/
    └── SessionManager.java   - Session/login state management
```

## Database Tables

- **users** - Stores user accounts (id, name, email, password, role)
- **appointments** - Stores bookings (id, student_id, teacher_id, date, time, status, notes)
- **notifications** - Stores notifications (id, user_id, message, is_read)

## Tech Stack

- **Language**: Java (JDK 8+)
- **UI Framework**: Java Swing + FlatLaf
- **Database**: MySQL (via XAMPP)
- **JDBC Driver**: MySQL Connector/J
- **IDE**: NetBeans
