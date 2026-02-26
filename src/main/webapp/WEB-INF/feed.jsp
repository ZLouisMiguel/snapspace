<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Feed</title>
</head>
<body>
<h2>Feed</h2>

<c:if test="${not empty sessionScope.user}">
    <p>
        <a href="${pageContext.request.contextPath}/upload">Upload New Image</a>
    </p>
</c:if>

<c:forEach var="post" items="${posts}">
    <div style="margin-bottom: 20px; border: 1px solid #ccc; padding: 10px; width: 300px;">
        <h4>${post.title}</h4>
        <p>Uploaded by: ${post.owner.username}</p>
        <img src="${post.imageUrl}" style="width: 100%; height: auto;" />
    </div>
</c:forEach>

</body>
</html>