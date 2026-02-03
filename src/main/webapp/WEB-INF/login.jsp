<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Login</title>
</head>
<body>

<h2>Login</h2>

<c:if test="${param.error == 'true'}">
    <p style="color:red;">Invalid credentials</p>
</c:if>

<form action="${pageContext.request.contextPath}/login" method="post">
    <label>Email</label>
    <input type="email" name="email" required />

    <label>Password</label>
    <input type="password" name="password" required />

    <button type="submit">Login</button>
</form>

</body>
</html>