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
import java.util.Set;

/**
 * Servlet responsible for handling image uploads in the SnapSpace application.
 *
 * <p>
 * This servlet handles HTTP concerns only — reading the multipart request,
 * validating the file, and redirecting. All upload logic (Cloudinary, database)
 * is delegated to {@link ImageService}.
 * </p>
 *
 * <p>
 * Node.js equivalent: your Express route handler. It reads req, validates the
 * file, calls a service, then sends a redirect. Nothing else.
 * </p>
 */
@WebServlet("/upload")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 10 * 1024 * 1024
)
public class UploadImageServlet extends HttpServlet {

    /**
     * MIME types accepted for upload.
     * Anything else is rejected before it reaches Cloudinary.
     */
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );

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
     * Steps:
     * <ol>
     *   <li>Reads the authenticated user from the session</li>
     *   <li>Checks the file part is non-empty and has an allowed MIME type</li>
     *   <li>Passes the stream, filename, and user to {@link ImageService#upload}</li>
     *   <li>Redirects to the feed on success, or back to upload with an error code</li>
     * </ol>
     * </p>
     *
     * @param request  HTTP multipart request containing the uploaded image
     * @param response HTTP response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        // AuthFilter already guards this route, but defensive check keeps the
        // method self-contained if the filter mapping ever changes.
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Part filePart = request.getPart("image");

        // Reject empty submissions (user clicked upload without choosing a file)
        if (filePart == null || filePart.getSize() == 0) {
            response.sendRedirect(request.getContextPath() + "/upload?error=no_file");
            return;
        }

        // Reject disallowed MIME types — checked server-side, not just in the browser
        String contentType = filePart.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            response.sendRedirect(request.getContextPath() + "/upload?error=invalid_type");
            return;
        }

        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

        imageService.upload(filePart.getInputStream(), fileName, user);

        response.sendRedirect(request.getContextPath() + "/feed");
    }
}
