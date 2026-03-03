<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>SnapSpace — Verify Code</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/styles.css">
</head>
<body>

<div class="auth-page">

    <div class="auth-left">
        <a class="nav-logo" href="${pageContext.request.contextPath}/">SNAP<span>SPACE</span></a>
        <p class="auth-left-quote">Check your email for a 6-digit code.</p>
    </div>

    <div class="auth-right">
        <div class="auth-card">

            <div class="auth-header">
                <span class="auth-tag">Second step</span>
                <h1 class="auth-headline">Verify it’s you</h1>
                <p class="auth-sub">Enter the code we emailed to you.</p>
            </div>

            <c:if test="${param.error == 'invalid'}">
                <div class="auth-error">
                    That code doesn’t look right. Try again.
                </div>
            </c:if>

            <c:if test="${param.error == 'otp_expired'}">
                <div class="auth-error">
                    Your code has expired. Please sign in again.
                </div>
            </c:if>

            <form class="auth-form" action="${pageContext.request.contextPath}/verify-otp" method="post">
                <div class="form-group">
                    <label for="otp">Verification code</label>
                    <input type="text" id="otp" name="otp" maxlength="6" pattern="\d{6}" placeholder="123456" required />
                </div>

                <button type="submit" class="btn-main auth-submit">Verify →</button>
            </form>

            <div class="auth-footer">
                Entered the wrong email?
                <a href="${pageContext.request.contextPath}/login">Start over</a>
            </div>

        </div>
    </div>

</div>

</body>
</html>

