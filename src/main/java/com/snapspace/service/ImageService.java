package com.snapspace.service;

import com.snapspace.dao.ImagePostDAO;
import com.snapspace.model.ImagePost;
import com.snapspace.model.User;
import com.snapspace.util.CloudinaryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Service layer responsible for all image post operations.
 *
 * <p>
 * This class sits between the servlet layer and the DAO layer.
 * Servlets hand it raw HTTP data. It handles the business logic —
 * uploading to Cloudinary via {@link CloudinaryUtil}, building the entity,
 * and persisting via {@link ImagePostDAO}. The DAO never needs to know
 * about Cloudinary, and the servlet never needs to know about either.
 * </p>
 *
 * <p>
 * Node.js equivalent: your service file that an Express route calls —
 * the route doesn't touch S3 or the DB directly, it just calls
 * {@code imageService.upload(...)}.
 * </p>
 */
public class ImageService {

    /**
     * DAO for persisting and retrieving image posts.
     */
    private final ImagePostDAO dao = new ImagePostDAO();

    /**
     * Uploads an image to Cloudinary and persists the post metadata to the database.
     *
     * <p>
     * Step by step:
     * <ol>
     *   <li>Reads the raw bytes from the servlet's input stream</li>
     *   <li>Delegates the upload to {@link CloudinaryUtil} — no Cloudinary SDK here</li>
     *   <li>Extracts the URL and public ID from the result</li>
     *   <li>Builds the {@link ImagePost} entity and saves it via the DAO</li>
     * </ol>
     * </p>
     *
     * @param imageStream raw bytes from the HTTP multipart request
     * @param title       display title for the post, typically the original filename
     * @param owner       the authenticated user making the upload
     * @throws IOException if the Cloudinary upload fails
     */
    public void upload(InputStream imageStream, String title, User owner) throws IOException {

        // All Cloudinary SDK knowledge lives in CloudinaryUtil — this service never imports it
        Map uploadResult = CloudinaryUtil.upload(
                imageStream.readAllBytes(),
                "snapspace/user_" + owner.getId()
        );

        // Cloudinary returns "secure_url" (the full HTTPS image link)
        // and "public_id" (the key needed to delete or transform it later)
        String imageUrl = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id");

        ImagePost post = new ImagePost();
        post.setTitle(title);
        post.setImageUrl(imageUrl);
        post.setCloudinaryPublicId(publicId);
        post.setOwner(owner);

        dao.save(post);
    }

    /**
     * Retrieves all image posts for the feed.
     *
     * @return list of all {@link ImagePost} entities from the database
     */
    public List<ImagePost> getFeed() {
        return dao.findAll();
    }
}