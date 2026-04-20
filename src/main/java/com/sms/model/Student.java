package com.sms.model;

public class Student {
    private int id;
    private String rollNumber, fullName, email, phone, studentClass, department, gender;

    // Constructors
    public Student() {}

    public Student(String rollNumber, String fullName, String email,
                   String phone, String studentClass, String department, String gender) {
        this.rollNumber = rollNumber;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.studentClass = studentClass;
        this.department = department;
        this.gender = gender;
    }

    // Getters and Setters (generate with Alt+Insert in IntelliJ)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String r) { this.rollNumber = r; }
    public String getFullName() { return fullName; }
    public void setFullName(String n) { this.fullName = n; }
    public String getEmail() { return email; }
    public void setEmail(String e) { this.email = e; }
    public String getPhone() { return phone; }
    public void setPhone(String p) { this.phone = p; }
    public String getStudentClass() { return studentClass; }
    public void setStudentClass(String c) { this.studentClass = c; }
    public String getDepartment() { return department; }
    public void setDepartment(String d) { this.department = d; }
    public String getGender() { return gender; }
    public void setGender(String g) { this.gender = g; }
}