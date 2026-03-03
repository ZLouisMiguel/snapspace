package com.snapspace.service;

import com.snapspace.dao.CommentDAO;
import com.snapspace.dao.ImagePostDAO;
import com.snapspace.dao.LikeDAO;
import com.snapspace.model.Comment;
import com.snapspace.model.ImagePost;
import com.snapspace.model.Like;
import com.snapspace.model.User;

import java.util.List;

/**
 * Service layer for all single-post operations: fetching post data,
 * submitting comments, and toggling likes.
 *
 * <p>
 * Like toggling also triggers a Sparks sync via {@link BoardService},
 * so that the user's Sparks board always reflects their current liked posts.
 * </p>
 */
public class PostService {

    private final ImagePostDAO postDAO = new ImagePostDAO();
    private final CommentDAO commentDAO = new CommentDAO();
    private final LikeDAO likeDAO = new LikeDAO();
    private final BoardService boardService = new BoardService();

    /**
     * Fetches a post by ID.
     *
     * @param postId the post's ID
     * @return the matching {@link ImagePost}, or {@code null} if not found
     */
    public ImagePost getPost(Long postId) {
        return postDAO.findById(postId);
    }

    /**
     * Fetches all comments for a post, ordered oldest first.
     *
     * @param post the post to fetch comments for
     * @return ordered list of {@link Comment} entities
     */
    public List<Comment> getComments(ImagePost post) {
        return commentDAO.findByPost(post);
    }

    /**
     * Returns the total like count for a post.
     *
     * @param post the post to count likes for
     * @return number of likes
     */
    public long getLikeCount(ImagePost post) {
        return likeDAO.countByPost(post);
    }

    /**
     * Checks whether a user has liked a given post.
     *
     * @param post the post to check
     * @param user the user to check
     * @return true if the user has already liked this post
     */
    public boolean hasLiked(ImagePost post, User user) {
        return likeDAO.hasLiked(post, user);
    }

    /**
     * Retrieves recent posts for the "more from the community" section,
     * excluding the currently viewed post.
     *
     * @param excludeId the ID of the post currently being viewed
     * @param limit     maximum number of posts to return
     * @return list of recent {@link ImagePost} entities
     */
    public List<ImagePost> getMorePosts(Long excludeId, int limit) {
        return postDAO.findRecent(excludeId, limit);
    }

    /**
     * Saves a new comment on a post if the text is non-blank.
     *
     * @param text the comment body
     * @param user the authenticated user submitting the comment
     * @param post the post being commented on
     */
    public void addComment(String text, User user, ImagePost post) {
        if (text == null || text.isBlank()) return;

        Comment comment = new Comment();
        comment.setText(text.trim());
        comment.setUser(user);
        comment.setImage(post);
        commentDAO.save(comment);
    }

    /**
     * Toggles a like on a post for the given user, then syncs Sparks.
     *
     * <p>
     * The flow:
     * <ol>
     *   <li>Determine the new liked state (opposite of current)</li>
     *   <li>Persist the like or delete it</li>
     *   <li>Call {@link BoardService#syncSparks} to add/remove from Sparks board</li>
     * </ol>
     * Sparks is always a reflection of likes — this is the single place
     * that enforces that invariant.
     * </p>
     *
     * @param post the post to like or unlike
     * @param user the authenticated user
     */
    public void toggleLike(ImagePost post, User user) {
        boolean currentlyLiked = likeDAO.hasLiked(post, user);

        if (currentlyLiked) {
            likeDAO.delete(post, user);
        } else {
            Like like = new Like();
            like.setUser(user);
            like.setImage(post);
            likeDAO.save(like);
        }

        boolean nowLiked = !currentlyLiked;
        boardService.syncSparks(user, post, nowLiked);
    }
}
