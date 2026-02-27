<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>SnapSpace — ${post.title}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/styles.css">
</head>
<body>

<nav>
    <a class="nav-logo" href="${pageContext.request.contextPath}/">SNAP<span>SPACE</span></a>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/feed">← Feed</a>
        <c:if test="${not empty sessionScope.user}">
            <a href="${pageContext.request.contextPath}/upload" class="nav-cta">+ Upload</a>
        </c:if>
    </div>
</nav>

<main class="post-page">

    <!-- ===================== MAIN VIEW ===================== -->
    <div class="post-main" id="postMain">

        <!-- Image panel -->
        <div class="post-image-panel">
            <img src="${post.imageUrl}" alt="${post.title}" class="post-image" />
        </div>

        <!-- Info panel -->
        <div class="post-info-panel" id="infoPanel">

            <div class="post-meta">
                <span class="post-tag">#snapshot</span>
                <h1 class="post-title">${post.title}</h1>
                <p class="post-owner">@${post.owner.username}</p>
            </div>

            <!-- Like button -->
            <div class="post-likes">
                <form action="${pageContext.request.contextPath}/post?id=${post.id}" method="post">
                    <input type="hidden" name="action" value="like" />
                    <button type="submit" class="like-btn ${hasLiked ? 'liked' : ''}">
                        <span class="like-icon">${hasLiked ? '♥' : '♡'}</span>
                        <span class="like-count">${likeCount}</span>
                    </button>
                </form>
            </div>

            <!-- Comments drawer tab -->
            <button class="comments-tab" id="commentsTab" onclick="toggleDrawer()">
                <span>💬 Comments</span>
                <span class="comments-count">${fn:length(comments)}</span>
                <span class="drawer-arrow" id="drawerArrow">→</span>
            </button>

        </div>

        <!-- Comments drawer — slides in over info panel -->
        <div class="comments-drawer" id="commentsDrawer">

            <div class="drawer-header">
                <h3 class="drawer-title">Comments</h3>
                <button class="drawer-close" onclick="toggleDrawer()">✕</button>
            </div>

            <div class="comments-list" id="commentsList">
                <c:choose>
                    <c:when test="${empty comments}">
                        <div class="comments-empty">
                            No comments yet. Be the first.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="comment" items="${comments}">
                            <div class="comment-item">
                                <span class="comment-user">@${comment.user.username}</span>
                                <p class="comment-text">${comment.text}</p>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Comment form — only shown when logged in -->
            <c:if test="${not empty sessionScope.user}">
                <form class="comment-form"
                      action="${pageContext.request.contextPath}/post?id=${post.id}"
                      method="post">
                    <input type="hidden" name="action" value="comment" />
                    <div class="comment-input-row">
                        <input
                            type="text"
                            name="text"
                            placeholder="Add a comment..."
                            required
                            autocomplete="off"
                        />
                        <button type="submit" class="comment-submit">↑</button>
                    </div>
                </form>
            </c:if>

            <c:if test="${empty sessionScope.user}">
                <div class="comment-login-prompt">
                    <a href="${pageContext.request.contextPath}/login">Sign in to comment</a>
                </div>
            </c:if>

        </div>

    </div>

    <!-- ===================== MORE FROM COMMUNITY ===================== -->
    <c:if test="${not empty more}">
        <section class="post-more">
            <div class="post-more-header">
                <span class="post-more-tag">More from the community</span>
            </div>
            <div class="post-more-grid">
                <c:forEach var="related" items="${more}">
                    <a href="${pageContext.request.contextPath}/post?id=${related.id}"
                       class="post-more-card">
                        <div class="post-more-img">
                            <img src="${related.imageUrl}" alt="${related.title}" />
                        </div>
                        <div class="post-more-info">
                            <span class="post-more-title">${related.title}</span>
                            <span class="post-more-user">@${related.owner.username}</span>
                        </div>
                    </a>
                </c:forEach>
            </div>
        </section>
    </c:if>

</main>

<script>
    function toggleDrawer() {
        const drawer = document.getElementById('commentsDrawer');
        const arrow  = document.getElementById('drawerArrow');
        const info   = document.getElementById('infoPanel');
        const isOpen = drawer.classList.contains('open');

        if (isOpen) {
            drawer.classList.remove('open');
            info.classList.remove('hidden');
            arrow.textContent = '→';
        } else {
            drawer.classList.add('open');
            info.classList.add('hidden');
            arrow.textContent = '←';
            // Scroll comments to bottom so latest is visible
            const list = document.getElementById('commentsList');
            list.scrollTop = list.scrollHeight;
        }
    }
</script>

</body>
</html>