<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>SnapSpace — Create Account</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/styles.css">
</head>
<body>

<div class="auth-page">

    <div class="auth-left">
        <a class="nav-logo" href="${pageContext.request.contextPath}/">SNAP<span>SPACE</span></a>
        <div class="auth-img-grid">
            <div class="auth-img">
                <img src="https://res.cloudinary.com/dxbxiqgzb/image/upload/v1772164092/snapspace/user_1/l37191lwb9gajgxumgy6.jpg" alt="moment" />
            </div>
            <div class="auth-img">
                <img src="https://res.cloudinary.com/dxbxiqgzb/image/upload/v1772164116/snapspace/user_1/wyuokpw48fkqbs2tnsbo.jpg" alt="moment" />
            </div>
            <div class="auth-img">
                <img src="https://res.cloudinary.com/dxbxiqgzb/image/upload/v1772164164/snapspace/user_1/qkenrmxpvjusrar4zcfz.jpg" alt="moment" />
            </div>
            <div class="auth-img">
                <img src="https://res.cloudinary.com/dxbxiqgzb/image/upload/v1772164225/snapspace/user_1/glz1duovg0af9cvcfiyg.png" alt="moment" />
            </div>
        </div>
        <p class="auth-left-quote">Your space starts here.</p>
    </div>

    <div class="auth-right">
        <div class="auth-card">

            <div class="auth-header">
                <span class="auth-tag">New here?</span>
                <h1 class="auth-headline">Create account</h1>
                <p class="auth-sub">Join the community. It's free.</p>
            </div>

            <c:if test="${param.error == 'email_taken'}">
                <div class="auth-error">
                    An account with that email already exists.
                </div>
            </c:if>

            <c:if test="${param.error == 'invalid_input'}">
                <div class="auth-error">
                    All fields are required.
                </div>
            </c:if>

            <form class="auth-form" action="${pageContext.request.contextPath}/register" method="post">

                <div class="form-group">
                    <label for="email">Email</label>
                    <input type="email" id="email" name="email" placeholder="you@example.com" required />
                </div>

                <div class="form-group">
                    <label for="username">Username</label>
                    <input type="text" id="username" name="username" placeholder="how the world sees you" required />
                </div>

                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" placeholder="••••••••" required />
                </div>

                <button type="submit" class="btn-main auth-submit">Create account →</button>

            </form>

            <div class="auth-footer">
                Already have an account?
                <a href="${pageContext.request.contextPath}/login">Sign in</a>
            </div>

        </div>
    </div>

</div>

</body>
</html>