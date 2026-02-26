package com.snapspace.controller;

import com.snapspace.model.User;
import com.snapspace.service.ImageService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Servlet responsible for handling image uploads in the SnapSpace application.
 *
 * <p>
 * This servlet only handles HTTP concerns — reading the request and redirecting.
 * All upload logic (Cloudinary, database) is delegated to {@link ImageService}.
 * </p>
 *
 * <p>
 * Node.js equivalent: this is your Express route handler. It reads req, calls
 * a service, then sends a redirect. Nothing else.
 * </p>
 */
@WebServlet("/upload")
@MultipartConfig(fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 10 * 1024 * 1024
)
public class UploadImageServlet extends HttpServlet {

    /**
     * Service handling all image upload business logic.
     * This replaces the direct DAO reference — the servlet no longer
     * knows about the database or Cloudinary.
     */
    private final ImageService imageService = new ImageService();

    /**
     * Displays the image upload page.
     *
     * @param request  HTTP request
     * @param response HTTP response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/upload.jsp").forward(request, response);
    }

    /**
     * Handles image upload form submission.
     *
     * <p>
     * This method:
     * <ol>
     *     <li>Verifies the user is authenticated via session</li>
     *     <li>Extracts the file and original filename from the multipart request</li>
     *     <li>Passes the raw stream, title, and user to {@link ImageService#upload}</li>
     *     <li>Redirects to the feed on success</li>
     * </ol>
     * No file system access, no DAO, no Cloudinary imports — all of that lives
     * in the service layer.
     * </p>
     *
     * @param request  HTTP multipart request containing the uploaded image
     * @param response HTTP response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Part filePart = request.getPart("image");
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

        imageService.upload(filePart.getInputStream(), fileName, user);

        response.sendRedirect(request.getContextPath() + "/feed");
    }
}