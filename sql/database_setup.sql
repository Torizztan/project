-- ============================================================
-- Appointment and Schedule Management System
-- Database Setup Script
-- Run this in phpMyAdmin (XAMPP) or MySQL command line
-- ============================================================

CREATE DATABASE IF NOT EXISTS appointment_db;
USE appointment_db;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('student', 'teacher', 'admin') NOT NULL DEFAULT 'student',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Appointments table
CREATE TABLE IF NOT EXISTS appointments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    teacher_id INT NOT NULL,
    date DATE NOT NULL,
    time TIME NOT NULL,
    status ENUM('pending', 'approved', 'rejected', 'cancelled', 'completed') NOT NULL DEFAULT 'pending',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Notifications table
CREATE TABLE IF NOT EXISTS notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    message TEXT NOT NULL,
    is_read TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Insert default admin account (password: admin123)
INSERT INTO users (name, email, password, role) VALUES
('Admin', 'admin@system.com', 'admin123', 'admin');

-- Insert sample teacher account (password: teacher123)
INSERT INTO users (name, email, password, role) VALUES
('Dr. Smith', 'smith@school.com', 'teacher123', 'teacher');

-- Insert sample student account (password: student123)
INSERT INTO users (name, email, password, role) VALUES
('John Doe', 'john@student.com', 'student123', 'student');
