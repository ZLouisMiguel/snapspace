<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>SnapSpace — My Boards</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/styles.css">
</head>
<body>

<nav>
    <a class="nav-logo" href="${pageContext.request.contextPath}/">SNAP<span>SPACE</span></a>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/feed">Feed</a>
        <a href="${pageContext.request.contextPath}/upload" class="nav-cta">+ Upload</a>
    </div>
</nav>

<main class="boards-page">

    <div class="boards-header">
        <div class="boards-header-left">
            <span class="boards-tag">Your collection</span>
            <h1 class="boards-headline">MY BOARDS</h1>
        </div>
        <button class="btn-main" onclick="toggleCreateForm()">+ New Board</button>
    </div>

    <!-- Create board form — hidden by default -->
    <div class="create-board-form" id="createBoardForm">
        <form action="${pageContext.request.contextPath}/boards" method="post"
              class="create-board-inner">
            <input type="hidden" name="action" value="create" />
            <input
                type="text"
                name="name"
                placeholder="Name your board..."
                required
                autocomplete="off"
                class="create-board-input"
            />
            <button type="submit" class="btn-main">Create →</button>
            <button type="button" class="btn-ghost" onclick="toggleCreateForm()">Cancel</button>
        </form>
    </div>

    <!-- Boards grid -->
    <div class="boards-grid">
        <c:forEach var="board" items="${boards}">
            <div class="board-card">
                <a href="${pageContext.request.contextPath}/boards?id=${board.id}"
                   class="board-card-preview">
                    <c:choose>
                        <c:when test="${not empty board.posts}">
                            <!-- Show up to 4 preview images -->
                            <c:forEach var="p" items="${board.posts}" begin="0" end="3">
                                <div class="board-preview-img">
                                    <img src="${p.imageUrl}" alt="${p.title}" />
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div class="board-preview-empty">
                                <span>No shots yet</span>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </a>
                <div class="board-card-info">
                    <div class="board-card-meta">
                        <span class="board-card-name">
                            ${board.name}
                            <c:if test="${board.defaultBoard}">
                                <span class="board-default-badge">default</span>
                            </c:if>
                        </span>
                        <span class="board-card-count">${fn:length(board.posts)} shots</span>
                    </div>
                    <c:if test="${!board.defaultBoard}">
                        <form action="${pageContext.request.contextPath}/boards" method="post"
                              class="board-delete-form">
                            <input type="hidden" name="action"  value="delete" />
                            <input type="hidden" name="boardId" value="${board.id}" />
                            <button type="submit" class="board-delete-btn"
                                    onclick="return confirm('Delete this board?')">
                                Delete
                            </button>
                        </form>
                    </c:if>
                </div>
            </div>
        </c:forEach>
    </div>

</main>

<script>
    function toggleCreateForm() {
        const form = document.getElementById('createBoardForm');
        form.classList.toggle('visible');
        if (form.classList.contains('visible')) {
            form.querySelector('input[name="name"]').focus();
        }
    }
</script>

</body>
</html>