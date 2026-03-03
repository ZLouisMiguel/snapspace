package com.snapspace.controller;

import com.snapspace.dao.CommentDAO;
import com.snapspace.dao.ImagePostDAO;
import com.snapspace.model.Comment;
import com.snapspace.model.ImagePost;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Server-Sent Events (SSE) endpoint for live comment streaming.
 *
 * <p>
 * When a client opens GET /comments/stream?postId=X, this servlet holds
 * the connection open asynchronously and pushes new comments as they appear,
 * without the client needing to refresh or poll manually.
 * </p>
 *
 * <p>
 * How SSE works (Node.js comparison):
 * This is equivalent to Express's res.write() in a long-lived route —
 * the response never calls res.end(), so the browser keeps the connection
 * open and fires an 'onmessage' event every time the server writes a line
 * prefixed with "data: ".
 * </p>
 *
 * <p>
 * The async=true on @WebServlet is required — without it Tomcat closes
 * the response the moment doGet() returns, ending the stream immediately.
 * asyncSupported=true tells Tomcat to keep the response alive after the
 * servlet method exits.
 * </p>
 *
 * <p>
 * Polling interval: 2 seconds. Low enough to feel live, high enough
 * not to hammer the DB under load.
 * </p>
 */
@WebServlet(urlPatterns = "/comments/stream", asyncSupported = true)
public class CommentStreamServlet extends HttpServlet {

    private final ImagePostDAO postDAO = new ImagePostDAO();
    private final CommentDAO commentDAO = new CommentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Parse and validate the postId param
        String idParam = request.getParameter("postId");
        if (idParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "postId required");
            return;
        }

        Long postId;
        try {
            postId = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "invalid postId");
            return;
        }

        ImagePost post = postDAO.findById(postId);
        if (post == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "post not found");
            return;
        }

        // SSE requires this content type — the browser's EventSource API
        // will not treat the response as an event stream without it
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("X-Accel-Buffering", "no"); // Disable Nginx buffering if behind a proxy

        // Hand control to Tomcat's async mechanism so this thread is freed
        // while we hold the connection open. Without this, we'd block a
        // Tomcat worker thread for the entire duration of the stream.
        AsyncContext async = request.startAsync();
        async.setTimeout(0); // No timeout — connection lives until client disconnects

        // Spin up a background thread to do the polling
        // In a production app this would be a proper thread pool, but for
        // a learning project a single daemon thread per connection is fine
        Thread poller = new Thread(() -> {
            PrintWriter writer = null;
            try {
                writer = response.getWriter();

                // lastSeenId tracks the highest comment ID already sent.
                // Starts at 0, so the first poll returns all existing comments
                // for the post — giving the page its initial comment list
                // without a separate HTTP call.
                long lastSeenId = 0;

                while (!async.getRequest().isAsyncStarted() || isClientConnected(writer)) {

                    List<Comment> newComments = commentDAO.findSince(post, lastSeenId);

                    for (Comment c : newComments) {
                        // SSE format: each event is "data: <payload>\n\n"
                        // We send a simple pipe-delimited string: id|username|text
                        // The client splits on | to build the DOM element.
                        // JSON would be cleaner but adds a parsing dependency —
                        // this keeps the client-side JS minimal.
                        String payload = c.getId()
                                + "|" + escapeSSE(c.getUser().getUsername())
                                + "|" + escapeSSE(c.getText());

                        writer.write("data: " + payload + "\n\n");
                        writer.flush();

                        lastSeenId = c.getId();
                    }

                    // Poll every 2 seconds
                    Thread.sleep(2000);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                // Client disconnected — this is normal, not an error
            } finally {
                async.complete();
            }
        });

        poller.setDaemon(true); // Don't block JVM shutdown
        poller.start();
    }

    /**
     * Checks whether the client is still connected by attempting a flush.
     * If the client has navigated away, flush() will throw, ending the loop.
     *
     * @param writer the response writer
     * @return true if the connection appears open
     */
    private boolean isClientConnected(PrintWriter writer) {
        writer.flush();
        return !writer.checkError();
    }

    /**
     * Strips newlines from SSE payload values.
     *
     * <p>
     * SSE uses newlines as delimiters — a newline inside a payload would
     * be interpreted as the end of the event and corrupt the stream.
     * We replace them with a space, which is safe for display purposes.
     * </p>
     *
     * @param value the raw string value
     * @return the value with newlines replaced
     */
    private String escapeSSE(String value) {
        if (value == null) return "";
        return value.replace("\n", " ").replace("\r", " ");
    }
}