<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>SnapSpace — Communities</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/styles.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/communities.css">
</head>
<body>

<jsp:include page="/WEB-INF/fragments/navbar.jsp" />

<main class="communities-page">

    <div class="communities-header">
        <div>
            <span class="communities-tag">Discover &amp; connect</span>
            <h1 class="communities-headline">COMMUNITIES</h1>
        </div>
        <c:if test="${not empty sessionScope.user}">
            <button class="btn-main" onclick="toggleCreateForm()">+ New Community</button>
        </c:if>
    </div>

    <!-- Create community form -->
    <c:if test="${not empty sessionScope.user}">
        <div class="create-community-form" id="createCommunityForm">
            <form action="${pageContext.request.contextPath}/communities" method="post"
                  class="create-community-inner">
                <input type="text" name="name" placeholder="Community name..." required
                       autocomplete="off" class="create-community-input" />
                <textarea name="description" placeholder="What's this community about? (optional)"
                          class="create-community-desc" rows="2"></textarea>
                <div class="create-community-footer">
                    <label class="visibility-label">
                        <input type="radio" name="visibility" value="PUBLIC" checked /> Public
                    </label>
                    <label class="visibility-label">
                        <input type="radio" name="visibility" value="PRIVATE" /> Private
                    </label>
                    <button type="submit" class="btn-main">Create →</button>
                    <button type="button" class="btn-ghost" onclick="toggleCreateForm()">Cancel</button>
                </div>
            </form>
        </div>
    </c:if>

    <!-- Communities grid -->
    <c:choose>
        <c:when test="${empty communities}">
            <div class="communities-empty">
                <h2>No communities yet</h2>
                <p>Be the first to create one.</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="communities-grid">
                <c:forEach var="community" items="${communities}">
                    <a href="${pageContext.request.contextPath}/community?id=${community.id}"
                       class="community-card">
                        <div class="community-card-cover">
                            <c:choose>
                                <c:when test="${not empty community.coverImageUrl}">
                                    <img src="${community.coverImageUrl}" alt="${community.name}" />
                                </c:when>
                                <c:otherwise>
                                    <div class="community-card-cover-placeholder">
                                        ${community.name.substring(0,1).toUpperCase()}
                                    </div>
                                </c:otherwise>
                            </c:choose>
                            <span class="community-visibility-badge ${community.visibility == 'PRIVATE' ? 'private' : 'public'}">
                                ${community.visibility == 'PRIVATE' ? '🔒 Private' : '🌐 Public'}
                            </span>
                        </div>
                        <div class="community-card-info">
                            <span class="community-card-name">${community.name}</span>
                            <c:if test="${not empty community.description}">
                                <span class="community-card-desc">${community.description}</span>
                            </c:if>
                        </div>
                    </a>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>

</main>

<script>
    function toggleCreateForm() {
        const form = document.getElementById('createCommunityForm');
        form.classList.toggle('visible');
        if (form.classList.contains('visible')) {
            form.querySelector('input[name="name"]').focus();
        }
    }
</script>

</body>
</html>