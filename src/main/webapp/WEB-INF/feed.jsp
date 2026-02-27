<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>SnapSpace — Feed</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/styles.css">
</head>
<body>

<nav>
    <a class="nav-logo" href="${pageContext.request.contextPath}/">SNAP<span>SPACE</span></a>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/feed">Feed</a>
        <c:if test="${not empty sessionScope.user}">
            <a href="${pageContext.request.contextPath}/upload" class="nav-cta">+ Upload</a>
        </c:if>
        <c:if test="${empty sessionScope.user}">
            <a href="${pageContext.request.contextPath}/login">Login</a>
            <a href="${pageContext.request.contextPath}/register" class="nav-cta">Get Started</a>
        </c:if>
    </div>
</nav>

<main class="feed-page">

    <c:choose>
        <c:when test="${empty posts}">
            <div class="feed-empty">
                <h2 class="feed-empty-headline">NOTHING HERE YET</h2>
                <p class="feed-empty-sub">Be the first to upload something.</p>
                <a href="${pageContext.request.contextPath}/upload" class="btn-main">Upload now →</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="feed-grid">
                <c:forEach var="post" items="${posts}">
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