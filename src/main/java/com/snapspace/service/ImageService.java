package com.snapspace.service;

import com.snapspace.model.ImagePost;
import com.snapspace.dao.ImagePostDAO;

import java.util.List;

/**
 * Service layer responsible for image post operations.
 *
 * <p>
 * This class encapsulates business logic related to image uploads
 * and retrieving the image feed. It delegates persistence logic
 * to {@link ImagePostDAO}.
 * </p>
 */
public class ImageService {

    /**
     * Data Access Object for image post persistence and retrieval.
     */
    private final ImagePostDAO dao = new ImagePostDAO();

    /**
     * Uploads a new image post.
     *
     * <p>
     * This method persists an {@link ImagePost} entity to the database.
     * The image itself is expected to be hosted externally (e.g., Cloudinary),
     * with only metadata and URLs stored in the database.
     * </p>
     *
     * @param post the image post to be uploaded
     */
    public void upload(ImagePost post) {
        dao.save(post);
    }

    /**
     * Retrieves the image feed.
     *
     * <p>
     * This typically returns all image posts ordered by creation time,
     * depending on DAO implementation.
     * </p>
     *
     * @return a list of {@link ImagePost} objects representing the feed
     */
    public List<ImagePost> feed() {
        return dao.findAll();
    }
}
