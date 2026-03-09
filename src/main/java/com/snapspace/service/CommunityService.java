package com.snapspace.service;

import com.snapspace.dao.*;
import com.snapspace.model.*;
import com.snapspace.model.CommunityMember.Role;

import java.util.List;

/**
 * Business logic for all community operations.
 *
 * <p>
 * Each method loads what it needs within its own DAO call (which opens/closes
 * its own Hibernate session). No entity is passed between sessions — DAOs now
 * accept bare IDs for queries, which eliminates the detached-entity bug that
 * caused messages and posts to silently fail.
 * </p>
 */
public class CommunityService {

    private final CommunityDAO communityDAO = new CommunityDAO();
    private final CommunityMemberDAO memberDAO = new CommunityMemberDAO();
    private final CommunityPostDAO communityPostDAO = new CommunityPostDAO();
    private final CommunityMessageDAO messageDAO = new CommunityMessageDAO();
    private final ImagePostDAO postDAO = new ImagePostDAO();

    // ── Read ────────────────────────────────────────────────────────────────

    public Community getCommunity(Long id) {
        return communityDAO.findById(id);
    }

    public List<Community> getVisibleCommunities(User user) {
        if (user == null) return communityDAO.findAllPublic();
        return communityDAO.findVisible(user.getId());
    }

    public CommunityMember getMembership(Community community, User user) {
        if (user == null) return null;
        return memberDAO.find(community, user);
    }

    public boolean isActiveMember(Community community, User user) {
        CommunityMember m = getMembership(community, user);
        return m != null && m.getRole() != Role.PENDING;
    }

    public boolean isAdmin(Community community, User user) {
        CommunityMember m = getMembership(community, user);
        return m != null && m.getRole() == Role.ADMIN;
    }

    /**
     * Returns all CommunityPost link-rows for this community, newest first.
     */
    public List<CommunityPost> getPosts(Community community) {
        return communityPostDAO.findByCommunity(community.getId());
    }

    /**
     * Returns the 50 most recent chat messages, oldest first.
     */
    public List<CommunityMessage> getRecentMessages(Community community) {
        return messageDAO.findRecent(community.getId(), 50);
    }

    public List<CommunityMember> getActiveMembers(Community community) {
        return memberDAO.findActiveMembers(community);
    }

    public List<CommunityMember> getPendingRequests(Community community) {
        return memberDAO.findByRole(community, Role.PENDING);
    }

    public long getMemberCount(Community community) {
        return memberDAO.countMembers(community);
    }

    // ── Create ──────────────────────────────────────────────────────────────

    public Community createCommunity(String name, String description,
                                     Community.Visibility visibility, User creator) {
        Community community = new Community();
        community.setName(name.trim());
        community.setDescription(description == null ? "" : description.trim());
        community.setVisibility(visibility);
        community.setCreator(creator);
        communityDAO.save(community);

        // Creator is automatically an ADMIN member
        CommunityMember membership = new CommunityMember();
        membership.setCommunity(community);
        membership.setUser(creator);
        membership.setRole(Role.ADMIN);
        memberDAO.save(membership);

        return community;
    }

    // ── Direct Upload (new ImagePost + share to community in one step) ──────

    /**
     * Creates a brand-new {@link ImagePost} and immediately links it to the
     * community via a {@link CommunityPost} row.
     */
    public void uploadPost(Community community, User user,
                           String title, String url, String publicId) {
        if (!isActiveMember(community, user)) return;

        ImagePost post = new ImagePost();
        post.setTitle(title == null || title.isBlank() ? "Untitled" : title.trim());
        post.setImageUrl(url);
        post.setCloudinaryPublicId(publicId);
        post.setOwner(user);
        postDAO.save(post);

        CommunityPost cp = new CommunityPost();
        cp.setCommunity(community);
        cp.setPost(post);
        cp.setSharedBy(user);
        communityPostDAO.save(cp);
    }

    // ── Membership actions ───────────────────────────────────────────────────

    public void join(Community community, User user) {
        if (memberDAO.find(community, user) != null) return;
        CommunityMember membership = new CommunityMember();
        membership.setCommunity(community);
        membership.setUser(user);
        membership.setRole(
                community.getVisibility() == Community.Visibility.PUBLIC
                        ? Role.MEMBER : Role.PENDING
        );
        memberDAO.save(membership);
    }

    public void leave(Community community, User user) {
        CommunityMember m = memberDAO.find(community, user);
        // Prevent the creator from leaving (they'd orphan the community)
        if (m == null || community.getCreator().getId().equals(user.getId())) return;
        memberDAO.delete(m);
    }

    public void approveMember(Community community, Long targetUserId, User admin) {
        if (!isAdmin(community, admin)) return;
        memberDAO.findByRole(community, Role.PENDING).stream()
                .filter(m -> m.getUser().getId().equals(targetUserId))
                .findFirst()
                .ifPresent(m -> memberDAO.updateRole(m, Role.MEMBER));
    }

    public void kickMember(Community community, Long targetUserId, User admin) {
        if (!isAdmin(community, admin)) return;
        memberDAO.findActiveMembers(community).stream()
                .filter(m -> m.getUser().getId().equals(targetUserId)
                        && m.getRole() != Role.ADMIN)
                .findFirst()
                .ifPresent(memberDAO::delete);
    }

    public void promoteMember(Community community, Long targetUserId, User admin) {
        if (!isAdmin(community, admin)) return;
        memberDAO.findActiveMembers(community).stream()
                .filter(m -> m.getUser().getId().equals(targetUserId)
                        && m.getRole() == Role.MEMBER)
                .findFirst()
                .ifPresent(m -> memberDAO.updateRole(m, Role.ADMIN));
    }

    /**
     * Shares an existing {@link ImagePost} into the community.
     * No-ops if the post is already shared (duplicate guard).
     */
    public void sharePost(Community community, Long postId, User user) {
        if (!isActiveMember(community, user)) return;
        ImagePost post = postDAO.findById(postId);
        if (post == null) return;
        if (communityPostDAO.exists(community.getId(), postId)) return;

        CommunityPost cp = new CommunityPost();
        cp.setCommunity(community);
        cp.setPost(post);
        cp.setSharedBy(user);
        communityPostDAO.save(cp);
    }

    /**
     * Persists a new chat message and returns the saved entity (with its
     * generated ID) so the SSE stream picks it up on the very next poll.
     *
     * @return the saved {@link CommunityMessage}, or {@code null} if the user
     * isn't a member or the text is blank
     */
    public CommunityMessage sendMessage(Community community, String text, User user) {
        if (!isActiveMember(community, user)) return null;
        if (text == null || text.isBlank()) return null;

        CommunityMessage msg = new CommunityMessage();
        msg.setCommunity(community);
        msg.setAuthor(user);
        msg.setText(text.trim());
        messageDAO.save(msg);
        return msg;
    }
}