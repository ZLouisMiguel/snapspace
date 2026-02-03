package com.example.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name="abanyeshuri")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "fName", nullable = false, length = 100)
    private String name;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    private String school;

    @Transient
    private int age;

    private LocalDate dob;

    public Student() {}

    public Student(int id, String name, String email, String school, LocalDate dob) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.school = school;
        this.dob = dob;
    }

    public Student(String name, String email, String school, LocalDate dob) {
        this.name = name;
        this.email = email;
        this.school = school;
        this.dob = dob;
    }

    // Getters and Setters for all fields
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getSchool() {
        return school;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    // Transient property
    public int getAge() {
        if (dob == null) return 0;
        return Period.between(dob, LocalDate.now()).getYears();
    }

    @Override
    public String toString() {
        return "Student [id=" + id + ", name=" + name + ", email=" + email + ", school=" + school + ", age=" + getAge() + "]";
    }
}
