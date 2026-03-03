package com.snapspace.service;

import com.snapspace.dao.*;
import com.snapspace.model.*;
import com.snapspace.model.CommunityMember.Role;

import java.util.List;

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

    public Community createCommunity(String name, String description,
                                     Community.Visibility visibility, User creator) {
        Community community = new Community();
        community.setName(name.trim());
        community.setDescription(description == null ? "" : description.trim());
        community.setVisibility(visibility);
        community.setCreator(creator);
        communityDAO.save(community);

        CommunityMember membership = new CommunityMember();
        membership.setCommunity(community);
        membership.setUser(creator);
        membership.setRole(Role.ADMIN);
        memberDAO.save(membership);

        return community;
    }

    // ── Direct Upload ───────────────────────────────────────────────────────

    /**
     * Creates a brand new post and shares it to the community immediately.
     */
    public void uploadPost(Community community, User user, String title, String url, String publicId) {
        if (!isActiveMember(community, user)) return;

        ImagePost post = new ImagePost();
        post.setTitle(title);
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

    // ── Actions ─────────────────────────────────────────────────────────────

    public void join(Community community, User user) {
        if (memberDAO.find(community, user) != null) return;
        CommunityMember membership = new CommunityMember();
        membership.setCommunity(community);
        membership.setUser(user);
        membership.setRole(community.getVisibility() == Community.Visibility.PUBLIC ? Role.MEMBER : Role.PENDING);
        memberDAO.save(membership);
    }

    public void leave(Community community, User user) {
        CommunityMember m = memberDAO.find(community, user);
        if (m == null || community.getCreator().getId().equals(user.getId())) return;
        memberDAO.delete(m);
    }

    public void approveMember(Community community, Long targetUserId, User admin) {
        if (!isAdmin(community, admin)) return;
        List<CommunityMember> pending = memberDAO.findByRole(community, Role.PENDING);
        pending.stream()
                .filter(m -> m.getUser().getId().equals(targetUserId))
                .findFirst()
                .ifPresent(m -> memberDAO.updateRole(m, Role.MEMBER));
    }

    public void kickMember(Community community, Long targetUserId, User admin) {
        if (!isAdmin(community, admin)) return;
        memberDAO.findActiveMembers(community).stream()
                .filter(m -> m.getUser().getId().equals(targetUserId) && m.getRole() != Role.ADMIN)
                .findFirst()
                .ifPresent(memberDAO::delete);
    }

    public void promoteMember(Community community, Long targetUserId, User admin) {
        if (!isAdmin(community, admin)) return;
        memberDAO.findActiveMembers(community).stream()
                .filter(m -> m.getUser().getId().equals(targetUserId) && m.getRole() == Role.MEMBER)
                .findFirst()
                .ifPresent(m -> memberDAO.updateRole(m, Role.ADMIN));
    }

    public void sharePost(Community community, Long postId, User user) {
        if (!isActiveMember(community, user)) {
            System.out.println("LOG: Share failed - User " + user.getUsername() + " not member of " + community.getName());
            return;
        }

        ImagePost post = postDAO.findById(postId);
        if (post == null) {
            System.out.println("LOG: Share failed - Post ID " + postId + " not found.");
            return;
        }

        if (communityPostDAO.exists(community, post)) return;

        CommunityPost cp = new CommunityPost();
        cp.setCommunity(community);
        cp.setPost(post);
        cp.setSharedBy(user);
        communityPostDAO.save(cp);
        System.out.println("LOG: Post " + postId + " shared to " + community.getName());
    }

    public CommunityMessage sendMessage(Community community, String text, User user) {
        if (!isActiveMember(community, user) || text == null || text.isBlank()) return null;
        CommunityMessage msg = new CommunityMessage();
        msg.setCommunity(community);
        msg.setAuthor(user);
        msg.setText(text.trim());
        messageDAO.save(msg);
        return msg;
    }
}