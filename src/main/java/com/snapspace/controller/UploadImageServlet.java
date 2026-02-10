package com.snapspace.controller;

import com.snapspace.dao.ImagePostDAO;
import com.snapspace.model.ImagePost;
import com.snapspace.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Servlet responsible for handling image uploads in the SnapSpace application.
 *
 * <p>
 * This servlet:
 * <ul>
 *     <li>Displays the image upload form (GET)</li>
 *     <li>Processes multipart image uploads (POST)</li>
 *     <li>Saves uploaded images to the server filesystem</li>
 *     <li>Persists {@link ImagePost} metadata to the database</li>
 * </ul>
 * </p>
 */
@WebServlet("/upload")
@MultipartConfig(fileSizeThreshold = 1024 * 1024,   // 1MB
        maxFileSize = 5 * 1024 * 1024,     // 5MB
        maxRequestSize = 10 * 1024 * 1024  // 10MB
)
public class UploadImageServlet extends HttpServlet {

    /**
     * Base directory name where uploaded images are stored.
     */
    private static final String UPLOAD_DIR = "uploads";

    /**
     * DAO used to persist {@link ImagePost} entities.
     */
    private final ImagePostDAO imageDAO = new ImagePostDAO();

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
     * Handles image upload submission.
     *
     * <p>
     * This method:
     * <ol>
     *     <li>Ensures the user is authenticated</li>
     *     <li>Extracts the uploaded image</li>
     *     <li>Stores the image in a user-specific directory</li>
     *     <li>Creates and saves an {@link ImagePost} entity</li>
     * </ol>
     * </p>
     *
     * @param request  HTTP request containing multipart data
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

        String appPath = request.getServletContext().getRealPath("");
        String userDirPath = appPath + File.separator + UPLOAD_DIR + File.separator + "user_" + user.getId();

        File userDir = new File(userDirPath);
        if (!userDir.exists()) {
            userDir.mkdirs();
        }

        String fullPath = userDirPath + File.separator + fileName;
        filePart.write(fullPath);

        ImagePost imagePost = new ImagePost();
        imagePost.setTitle(fileName);
        imagePost.setImagePath("user_" + user.getId() + "/" + fileName);
        imagePost.setOwner(user);

        imageDAO.save(imagePost);

        response.sendRedirect(request.getContextPath() + "/feed");
    }
}
