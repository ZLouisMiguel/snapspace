<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>SnapSpace — ${board.name}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/styles.css">
</head>
<body>

<nav>
    <a class="nav-logo" href="${pageContext.request.contextPath}/">SNAP<span>SPACE</span></a>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/boards">← Boards</a>
        <a href="${pageContext.request.contextPath}/upload" class="nav-cta">+ Upload</a>
    </div>
</nav>

<main class="boards-page">

    <div class="boards-header">
        <div class="boards-header-left">
            <span class="boards-tag">
                ${fn:length(board.posts)} shots
                <c:if test="${board.defaultBoard}"> · default board</c:if>
            </span>
            <h1 class="boards-headline">${board.name}</h1>
        </div>
    </div>

    <c:choose>
        <c:when test="${empty board.posts}">
            <div class="feed-empty">
                <h2 class="feed-empty-headline">EMPTY BOARD</h2>
                <p class="feed-empty-sub">Start saving shots from the feed.</p>
                <a href="${pageContext.request.contextPath}/feed" class="btn-main">Browse feed →</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="feed-grid">
                <c:forEach var="post" items="${board.posts}">
                    <a href="${pageContext.request.contextPath}/post?id=${post.id}" class="feed-card">
                        <div class="feed-card-img">
                            <img src="${post.imageUrl}" alt="${post.title}" />
                            <div class="feed-card-overlay">
                                <span class="feed-save-btn">View</span>
                            </div>
                        </div>
                        <div class="feed-card-info">
                            <span class="feed-card-title">${post.title}</span>
                            <span class="feed-card-user">@${post.owner.username}</span>
                        </div>
                    </a>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>

</main>

</body>
</html>