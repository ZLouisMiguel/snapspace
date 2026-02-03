package com.example.controller;

import com.example.model.Student;
import com.example.services.StudentServices;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/students")
public class StudentController extends HttpServlet {

    private final StudentServices services = StudentServices.getInstance();
    private final ObjectMapper mapper = new ObjectMapper();

    // -------- READ --------
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        String id = req.getParameter("id");

        if (id != null) {
            Student student = services.getStudentById(Integer.parseInt(id));
            mapper.writeValue(resp.getOutputStream(), student);
        } else {
            List<Student> students = services.getAll();
            mapper.writeValue(resp.getOutputStream(), students);
        }
    }

    // -------- CREATE --------
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String school = req.getParameter("school");
        LocalDate dob = LocalDate.parse(req.getParameter("dob"));

        Student student = new Student(name, email, school, dob);
        services.addStudent(student);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        mapper.writeValue(resp.getOutputStream(), student);
    }

    // -------- DELETE --------
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");

        if (id == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Student id required");
            return;
        }

        services.deleteStudent(Integer.parseInt(id));
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
