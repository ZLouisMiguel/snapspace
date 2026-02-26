<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Register</title>
</head>
<body>

<h2>Create Account</h2>

<c:if test="${param.error == 'email_taken'}">
    <p style="color:red;">An account with that email already exists.</p>
</c:if>

<c:if test="${param.error == 'invalid_input'}">
    <p style="color:red;">All fields are required.</p>
</c:if>

<form action="${pageContext.request.contextPath}/register" method="post">
    <label>Email</label>
    <input type="email" name="email" required />

    <label>Username</label>
    <input type="text" name="username" required />

    <label>Password</label>
    <input type="password" name="password" required />

    <button type="submit">Register</button>
</form>

<p>
    Already have an account?
    <a href="${pageContext.request.contextPath}/login">Login</a>
</p>

</body>
</html>