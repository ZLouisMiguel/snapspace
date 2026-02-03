package com.example.controller;

import com.example.model.Student;
import com.example.services.StudentServices;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/students/view")
public class StudentViewController extends HttpServlet {

    private final StudentServices services = StudentServices.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idParam = req.getParameter("id");

        if (idParam != null) {
            try {
                int id = Integer.parseInt(idParam);
                Student student = services.getStudentById(id);
                if (student != null) {
                    req.setAttribute("student", student);
                    req.getRequestDispatcher("/WEB-INF/student-detail.jsp").forward(req, resp);
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Student not found");
                }

            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid student ID");
            }

        } else {

            List<Student> students = services.getAll();
            req.setAttribute("students", students);
            req.getRequestDispatcher("/WEB-INF/students.jsp")
                    .forward(req, resp);
        }
    }
}
