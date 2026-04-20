# 🎓 Student Management System

<p align="center">
  <img src="https://img.shields.io/badge/Java-17%2B-orange?style=for-the-badge&logo=java" />
  <img src="https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql" />
  <img src="https://img.shields.io/badge/Maven-Build-red?style=for-the-badge&logo=apache-maven" />
  <img src="https://img.shields.io/badge/Swing-Desktop%20UI-purple?style=for-the-badge" />
  <img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge" />
</p>

<p align="center">
  A full-featured <strong>desktop application</strong> built with Java Swing + MySQL for managing students, attendance, and academic results in an educational institution.
</p>

---

>
> 🔗 **GitHub Repository:** [https://github.com/shivangi-mtxqw/StudentManagementSystemNew](https://github.com/shivangi-mtxqw/StudentManagementSystemNew)

---

| Login | Dashboard |
|---|---|
| Dark themed login with status feedback | Sidebar nav + 3 live stat cards |

---

## ✨ Features

### 🔐 Authentication
- Role-based login (Admin / Teacher / Student)
- Username + password authentication via MySQL `users` table
- Session-aware dashboard with user name and role displayed

### 📊 Dashboard
- **Live stat cards** showing:
  - Total Students enrolled
  - Total Subjects
  - Overall Attendance percentage
- Sidebar navigation between modules
- Logout button

### 👨‍🎓 Student Management
| Action | Description |
|---|---|
| Add Student | Register new student with roll no., name, email, phone, class, department, gender |
| Edit Student | Update any student's details |
| Delete Student | Remove a student record |
| Search | Search by name or roll number (live filter) |
| View All | Paginated table of all enrolled students |

### 📅 Attendance Management
| Action | Description |
|---|---|
| Mark Attendance | Select student + subject + date + status (Present/Absent) |
| View Log | Full attendance history sorted by date (latest first) |
| Subject-wise | Attendance linked to `subjects` table |

### 📝 Results Management
| Action | Description |
|---|---|
| Add Result | Enter internal marks, external marks, max marks, semester, and exam year |
| Auto Grade | Grade calculated automatically (O / A+ / A / B+ / B / C / F) |
| CGPA | Auto-computes CGPA per student using 10-point grading scale |
| View All | Filterable results table for admin |
| Student View | Individual student's result card by semester |

---

## 🗂️ Project Structure

```
StudentManagementSystem2/
├── pom.xml                          # Maven build config
├── src/
│   └── main/java/com/sms/
│       ├── Main.java                # Entry point (Nimbus L&F + LoginFrame)
│       ├── db/
│       │   └── DBConnection.java    # MySQL JDBC singleton connection
│       ├── model/
│       │   └── Student.java         # Student POJO (id, roll, name, email, etc.)
│       ├── dao/
│       │   ├── UserDAO.java         # Login / role authentication
│       │   ├── StudentDAO.java      # CRUD + search for students
│       │   ├── AttendanceDAO.java   # Mark & fetch attendance records
│       │   └── ResultsDAO.java      # Results, CGPA, grade calculation, stats
│       └── ui/
│           ├── login/
│           │   └── LoginFrame.java  # Login screen (dark themed)
│           ├── dashboard/
│           │   └── DashboardFrame.java  # Main window with sidebar + tabs
│           ├── student/
│           │   └── StudentPanel.java    # Student management tab
│           ├── attendance/
│           │   └── AttendancePanel.java # Attendance tab
│           └── result/
│               └── ResultsPanel.java   # Results tab
```

---

## 🗄️ Database Schema

Connect to MySQL and run the following SQL to set up the database:

```sql
CREATE DATABASE IF NOT EXISTS student_management;
USE student_management;

-- Users (login)
CREATE TABLE IF NOT EXISTS users (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(100) NOT NULL,
    role       VARCHAR(20)  NOT NULL,   -- 'admin', 'teacher', 'student'
    full_name  VARCHAR(100)
);

-- Students
CREATE TABLE IF NOT EXISTS students (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    roll_number  VARCHAR(20)  NOT NULL UNIQUE,
    full_name    VARCHAR(100) NOT NULL,
    email        VARCHAR(100),
    phone        VARCHAR(15),
    class        VARCHAR(50),
    department   VARCHAR(100),
    gender       VARCHAR(10)
);

-- Subjects
CREATE TABLE IF NOT EXISTS subjects (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    subject_name  VARCHAR(100) NOT NULL
);

-- Attendance
CREATE TABLE IF NOT EXISTS attendance (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    student_id       INT NOT NULL,
    subject_id       INT NOT NULL,
    attendance_date  DATE NOT NULL,
    status           VARCHAR(10) NOT NULL,    -- 'Present' or 'Absent'
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (subject_id) REFERENCES subjects(id)
);

-- Results
CREATE TABLE IF NOT EXISTS results (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    student_id      INT NOT NULL,
    subject_id      INT NOT NULL,
    semester        INT,
    internal_marks  DOUBLE,
    external_marks  DOUBLE,
    total_marks     DOUBLE GENERATED ALWAYS AS (internal_marks + external_marks) STORED,
    max_marks       DOUBLE,
    grade           VARCHAR(5),
    exam_year       VARCHAR(10),
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (subject_id) REFERENCES subjects(id)
);

-- Default admin user
INSERT INTO users (username, password, role, full_name)
VALUES ('admin', 'admin123', 'admin', 'Administrator');
```

---

## 🚀 Getting Started

### Prerequisites

| Requirement | Version |
|---|---|
| Java JDK | 17 or higher |
| MySQL Server | 8.0+ |
| Apache Maven | 3.6+ |

### 1. Clone the Repository

```bash
git clone https://github.com/shivangi-mtxqw/StudentManagementSystemNew.git
cd StudentManagementSystemNew
```

### 2. Configure Database Connection

Edit `src/main/java/com/sms/db/DBConnection.java`:

```java
private static final String URL      = "jdbc:mysql://localhost:3306/student_management";
private static final String USER     = "root";
private static final String PASSWORD = "your_mysql_password";
```

### 3. Set Up the Database

Open MySQL Workbench (or terminal) and run the SQL from the [Database Schema](#-database-schema) section above.

### 4. Build & Run

```bash
# Compile
mvn compile

# Run
mvn exec:java -Dexec.mainClass="com.sms.Main"
```

Or if Maven is not installed, use the pre-compiled classes:

```bash
# Download dependency JARs first, then:
java -cp "target/classes;libs/mysql-connector-j-8.0.33.jar;libs/jbcrypt-0.4.jar" com.sms.Main
```

---

## 🔑 Default Login

| Username | Password | Role |
|---|---|---|
| `admin` | `admin123` | admin |

> You can add more users directly in the `users` table in MySQL.

---

## 🎨 UI Design

- **Look & Feel:** Nimbus (Java built-in)
- **Color Scheme:** Dark sidebar (`#1E1E32`) + light content area
- **Login:** Dark themed card with indigo button (`#4F46E5`)
- **Dashboard:** 3-column stat cards with color-coded borders

---

## 📦 Dependencies

Managed via Maven (`pom.xml`):

| Dependency | Version | Purpose |
|---|---|---|
| `mysql-connector-j` | 8.0.33 | MySQL JDBC driver |
| `jbcrypt` | 0.4 | BCrypt password hashing (included) |

---

## 🧱 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| UI Framework | Java Swing (Nimbus L&F) |
| Database | MySQL 8.0 |
| Build Tool | Apache Maven |
| DB Driver | MySQL Connector/J 8.0.33 |
| Architecture | DAO Pattern (MVC-like) |

---

## 🤝 Contributing

1. Fork this repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -m "feat: add my feature"`
4. Push to GitHub: `git push origin feature/my-feature`
5. Open a Pull Request

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

<p align="center">
  Made with ❤️ using Java Swing &amp; MySQL
</p>
