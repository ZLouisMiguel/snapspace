package com.snapspace.controller;

import com.snapspace.dao.CommunityMessageDAO;
import com.snapspace.model.Community;
import com.snapspace.model.CommunityMessage;
import com.snapspace.model.User;
import com.snapspace.service.CommunityService;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * SSE endpoint for live community chat.
 *
 * <p>
 * Identical in structure to {@link CommentStreamServlet} but scoped
 * to a community's message stream rather than a post's comments.
 * Only active members of the community can connect.
 * </p>
 *
 * GET /community/chat?id=X
 */
@WebServlet(urlPatterns = "/community/chat", asyncSupported = true)
public class CommunityChatStreamServlet extends HttpServlet {

    private final CommunityService    communityService = new CommunityService();
    private final CommunityMessageDAO messageDAO       = new CommunityMessageDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        Long communityId = parseLong(request.getParameter("id"));
        if (communityId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Community community = communityService.getCommunity(communityId);
        if (community == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Private communities require active membership to stream chat
        if (community.getVisibility() == Community.Visibility.PRIVATE
                && !communityService.isActiveMember(community, user)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("X-Accel-Buffering", "no");

        AsyncContext async = request.startAsync();
        async.setTimeout(0);

        Thread poller = new Thread(() -> {
            PrintWriter writer = null;
            try {
                writer = response.getWriter();
                long lastSeenId = 0;

                while (isClientConnected(writer)) {
                    List<CommunityMessage> newMessages = messageDAO.findSince(community, lastSeenId);

                    for (CommunityMessage msg : newMessages) {
                        // Format: id|username|text  — same pipe-delimited SSE as comments
                        String payload = msg.getId()
                                + "|" + escapeSSE(msg.getAuthor().getUsername())
                                + "|" + escapeSSE(msg.getText());
                        writer.write("data: " + payload + "\n\n");
                        writer.flush();
                        lastSeenId = msg.getId();
                    }

                    Thread.sleep(2000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                // Client disconnected — normal
            } finally {
                async.complete();
            }
        });

        poller.setDaemon(true);
        poller.start();
    }

    private boolean isClientConnected(PrintWriter writer) {
        writer.flush();
        return !writer.checkError();
    }

    private String escapeSSE(String value) {
        if (value == null) return "";
        return value.replace("\n", " ").replace("\r", " ");
    }

    private Long parseLong(String param) {
        if (param == null || param.isBlank()) return null;
        try { return Long.parseLong(param); }
        catch (NumberFormatException e) { return null; }
    }
}