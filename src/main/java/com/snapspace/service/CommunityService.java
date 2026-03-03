package com.snapspace.service;

import com.snapspace.dao.*;
import com.snapspace.model.*;
import com.snapspace.model.CommunityMember.Role;

import java.util.List;

/**
 * Service layer for all community operations.
 *
 * <p>
 * Enforces all business rules — membership checks, role guards,
 * privacy controls, and duplicate-post prevention. Servlets never
 * import a DAO directly; everything goes through here.
 * </p>
 */
public class CommunityService {

    private final CommunityDAO        communityDAO    = new CommunityDAO();
    private final CommunityMemberDAO  memberDAO       = new CommunityMemberDAO();
    private final CommunityPostDAO    communityPostDAO = new CommunityPostDAO();
    private final CommunityMessageDAO messageDAO      = new CommunityMessageDAO();
    private final ImagePostDAO        postDAO         = new ImagePostDAO();

    // ── Read ────────────────────────────────────────────────────────────────

    public Community getCommunity(Long id) {
        return communityDAO.findById(id);
    }

    public List<Community> getVisibleCommunities(User user) {
        if (user == null) return communityDAO.findAllPublic();
        return communityDAO.findVisible(user.getId());
    }

    /** Returns the membership record for a user in a community, or null. */
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

    public List<CommunityPost> getPosts(Community community) {
        return communityPostDAO.findByCommunity(community);
    }

    public List<CommunityMessage> getRecentMessages(Community community) {
        return messageDAO.findRecent(community, 50);
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

    /**
     * Creates a new community and automatically makes the creator an ADMIN member.
     */
    public Community createCommunity(String name, String description,
                                     Community.Visibility visibility, User creator) {
        Community community = new Community();
        community.setName(name.trim());
        community.setDescription(description == null ? "" : description.trim());
        community.setVisibility(visibility);
        community.setCreator(creator);
        communityDAO.save(community);

        // Creator is always the first ADMIN
        CommunityMember membership = new CommunityMember();
        membership.setCommunity(community);
        membership.setUser(creator);
        membership.setRole(Role.ADMIN);
        memberDAO.save(membership);

        return community;
    }

    // ── Join / Leave ─────────────────────────────────────────────────────────

    /**
     * Handles a user joining or requesting to join a community.
     *
     * <p>
     * PUBLIC  → user is immediately a MEMBER.
     * PRIVATE → user is set to PENDING until an admin approves.
     * </p>
     * Does nothing if the user is already a member or has a pending request.
     */
    public void join(Community community, User user) {
        if (memberDAO.find(community, user) != null) return; // Already involved

        CommunityMember membership = new CommunityMember();
        membership.setCommunity(community);
        membership.setUser(user);
        membership.setRole(
                community.getVisibility() == Community.Visibility.PUBLIC
                        ? Role.MEMBER
                        : Role.PENDING
        );
        memberDAO.save(membership);
    }

    public void leave(Community community, User user) {
        CommunityMember m = memberDAO.find(community, user);
        if (m == null) return;
        // Creator cannot leave — they must delete or transfer first
        if (community.getCreator().getId().equals(user.getId())) return;
        memberDAO.delete(m);
    }

    // ── Admin actions ────────────────────────────────────────────────────────

    /**
     * Approves a pending join request. Caller must verify they are an admin.
     */
    public void approveMember(Community community, Long targetUserId, User admin) {
        if (!isAdmin(community, admin)) return;
        // Find the pending member by community + userId
        List<CommunityMember> pending = memberDAO.findByRole(community, Role.PENDING);
        pending.stream()
                .filter(m -> m.getUser().getId().equals(targetUserId))
                .findFirst()
                .ifPresent(m -> memberDAO.updateRole(m, Role.MEMBER));
    }

    /**
     * Removes a member from the community. Admins cannot kick other admins.
     */
    public void kickMember(Community community, Long targetUserId, User admin) {
        if (!isAdmin(community, admin)) return;
        List<CommunityMember> members = memberDAO.findActiveMembers(community);
        members.stream()
                .filter(m -> m.getUser().getId().equals(targetUserId)
                        && m.getRole() != Role.ADMIN)
                .findFirst()
                .ifPresent(memberDAO::delete);
    }

    /**
     * Promotes a MEMBER to ADMIN.
     */
    public void promoteMember(Community community, Long targetUserId, User admin) {
        if (!isAdmin(community, admin)) return;
        List<CommunityMember> members = memberDAO.findActiveMembers(community);
        members.stream()
                .filter(m -> m.getUser().getId().equals(targetUserId)
                        && m.getRole() == Role.MEMBER)
                .findFirst()
                .ifPresent(m -> memberDAO.updateRole(m, Role.ADMIN));
    }

    // ── Posts & Messages ──────────────────────────────────────────────────────

    /**
     * Shares a post into a community.
     * Only active members can share. Duplicate shares are silently ignored.
     */
    public void sharePost(Community community, Long postId, User user) {
        if (!isActiveMember(community, user)) return;

        ImagePost post = postDAO.findById(postId);
        if (post == null) return;
        if (communityPostDAO.exists(community, post)) return; // Already shared

        CommunityPost cp = new CommunityPost();
        cp.setCommunity(community);
        cp.setPost(post);
        cp.setSharedBy(user);
        communityPostDAO.save(cp);
    }

    /**
     * Sends a chat message in a community.
     * Only active members can message.
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