<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<nav class="navbar">
    <a class="nav-logo" href="${pageContext.request.contextPath}/">SNAP<span>SPACE</span></a>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/feed">Feed</a>
        <a href="${pageContext.request.contextPath}/communities">Communities</a>
        <c:if test="${not empty sessionScope.user}">
            <a href="${pageContext.request.contextPath}/boards">Boards</a>
            <a href="${pageContext.request.contextPath}/upload" class="nav-cta">+ Upload</a>
        </c:if>
        <c:if test="${empty sessionScope.user}">
            <a href="${pageContext.request.contextPath}/login">Login</a>
            <a href="${pageContext.request.contextPath}/register" class="nav-cta">Get Started</a>
        </c:if>
    </div>
</nav>

