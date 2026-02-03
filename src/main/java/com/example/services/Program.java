package com.example.services;

import java.time.LocalDate;
import java.util.List;

import com.example.model.Student;
import com.example.util.HibernateUtil;

public class Program {

    public static void main(String[] args) {

        // Initialize Hibernate SessionFactory
        HibernateUtil.getSessionFactory();

        // Access the service (Singleton)
        StudentServices service = StudentServices.getInstance();

        // ---------- ADD STUDENTS ----------
        System.out.println("Adding students...");

        // Now using LocalDate for dob instead of age
        // Assuming they're around 18-20 years old (born around 2004-2006)
        service.addStudent(new Student("Alice", "alice@gmail.com", "RCA",
                LocalDate.of(2005, 3, 15)));  // Born March 15, 2005
        service.addStudent(new Student("Bob", "bob@gmail.com", "RCA",
                LocalDate.of(2004, 7, 22)));  // Born July 22, 2004
        service.addStudent(new Student("Chris", "chris@gmail.com", "KICS",
                LocalDate.of(2003, 11, 5)));  // Born November 5, 2003

        System.out.println("Students added successfully.");

        // ---------- GET ALL STUDENTS ----------
        System.out.println("\nFetching all students (HQL)...");
        List<Student> allStudents = service.getAll();
        allStudents.forEach(System.out::println);

        // ---------- GET BY ID ----------
        System.out.println("\nFetching student with ID = 1...");
        Student s1 = service.getStudentById(1);
        System.out.println(s1 != null ? s1 : "Student not found");

        // ---------- USING Criteria API ----------
        System.out.println("\nFetching all students using Criteria API...");
        List<Student> critStudents = service.findAllStudents();
        critStudents.forEach(System.out::println);

        // ---------- Fetch using custom method ----------
        System.out.println("\nListStudents()...");
        List<Student> ls = service.listStudents();
        ls.forEach(System.out::println);

        // Test age calculation
        System.out.println("\n--- Age Calculation Test ---");
        allStudents.forEach(student -> {
            System.out.println(student.getName() +
                    ": Born on " + student.getDob() +
                    " | Age: " + student.getAge() + " years");
        });

        // Close Hibernate
        HibernateUtil.getSessionFactory().close();

        System.out.println("\nProgram finished.");
    }
}