# Driving School Web Application

## Overview
The **Driving School Web Application** is a comprehensive system designed to support driving schools and students preparing for driving exams. The application combines theoretical learning, practical driving schedules, and administrative functionalities to optimize the learning process and improve communication between students, instructors, and school administrators.

## Features
### 1. Interactive Driving Lesson Schedule
- Allows instructors and students to schedule and track driving lessons.
- Includes a calendar with filtering options for available slots.
- Enables cancellation and rescheduling of bookings.

### 2. Educational Materials, Tests & Exams
- A dedicated section for theoretical learning with tests, and exams.
- Includes educational materials covering traffic regulations, road signs, emergency procedures, and more.
- Categorized test questions and lectures for structured learning.
- Tracks test and exam statistics and displays them on the dashboard.

### 3. Student Progress Tracking & Statistics
- Allows both students and instructors to track learning progress.
- Displays completed driving hours, test scores, exam performance, and instructor feedback.
- Dashboard for students with category progress and overall statistics.
- Instructors can view all their students and expand their statistics.
- Instructors can add comments, driving hours, and test scores for students.
- General statistics available for users and administrators to analyze performance and trends.

### 4. Communication & Notifications
- Internal messaging system for communication between students and instructors.
- Automatic notifications about upcoming lessons, test results, and instructor feedback.
- Notifications displayed in the dashboard.

### 5. Administrative Panel for Driving Schools
- Manages instructor schedules, student progress, reports, and payments.
- Admins can monitor all meetings, manage students, and generate reports.
- Payment management: Admins handle payments, and students can view their payment status.
- Admins have full control over permissions and access rights.

### 6. User-Friendly Interface
- Intuitive and easy-to-use interface for seamless navigation.

### 7. Data Security & Privacy
- Ensures a high level of data security in compliance with privacy regulations.

### 8. Mobile Accessibility
- Fully responsive design for a seamless experience on mobile devices.

## Technologies Used
- **Backend**: Java, Spring Boot
- **Frontend**: Thymeleaf, SCSS
- **Database**: MySQL
- **Development Tools**: IntelliJ IDEA, Visual Studio Code, Figma, XAMPP, Maven, JUnit
- **Architecture**: Model-View-Controller (MVC)

## Installation & Setup
1. **Clone the repository**:
   ```bash
   git clone https://github.com/mat3usq/DrivingSchool.git
   ```
2. **Navigate to the project directory**:
   ```bash
   cd DrivingSchool
   ```
3. **Set up the database**:
   - Ensure MySQL is installed and running.
   - Create a database using the provided schema.
4. **Configure application properties**:
   - Modify `application.properties` with your database credentials.
5. **Build and run the application**:
   ```bash
   mvn spring-boot:run
   ```
6. **Access the application**:
   - Open `http://localhost:8080` in your browser.

## Authors
- **Mateusz Mogielnicki**
- **Tomasz Marchela**

## License
This project is licensed under the MIT License.
