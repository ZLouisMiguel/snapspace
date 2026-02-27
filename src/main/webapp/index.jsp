<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>SnapSpace</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/styles.css">
</head>
<body>

<nav>
    <a class="nav-logo" href="#">SNAP<span>SPACE</span></a>
    <div class="nav-links">
        <a href="#">Explore</a>
        <a href="#">Community</a>
        <a href="${pageContext.request.contextPath}/login">Login</a>
        <a href="${pageContext.request.contextPath}/register" class="nav-cta">Get Started</a>
    </div>
</nav>

<section class="hero">

    <div class="hero-left">
        <div class="hero-tag">Your shots. Your space.</div>

        <h1 class="hero-headline">
            FOR<br>
            EVERY<br>
            <span class="accent">MOMENT</span>
        </h1>

        <p class="hero-sub">
            Upload your shots, see what the community is posting,
            and share the moments that actually matter —
            no algorithm, no ads, just you and your tastes
        </p>

        <div class="hero-actions">
            <a href="${pageContext.request.contextPath}/register" class="btn-main">
                Start Uploading →
            </a>
            <a href="${pageContext.request.contextPath}/login" class="btn-ghost">
                Sign in
            </a>
        </div>

        <div class="hero-strip">
            <div class="hero-strip-item">
                <img src="https://res.cloudinary.com/dxbxiqgzb/image/upload/v1772093589/snapspace/user_1/rvcqklb6cwpkue93uhqz.jpg" alt="moment" />
            </div>
            <div class="hero-strip-item">
                <img src="https://res.cloudinary.com/dxbxiqgzb/image/upload/v1772131946/snapspace/user_1/danz7sl0ug7fcdty4ano.jpg" alt="moment" />
            </div>
            <div class="hero-strip-item">
                <img src="https://res.cloudinary.com/dxbxiqgzb/image/upload/v1772164225/snapspace/user_1/glz1duovg0af9cvcfiyg.png" alt="moment" />
            </div>
            <div class="hero-strip-item">
                <img src="https://res.cloudinary.com/dxbxiqgzb/image/upload/v1772164164/snapspace/user_1/qkenrmxpvjusrar4zcfz.jpg" alt="moment" />
            </div>
        </div>
    </div>

    <div class="hero-right">
        <div class="hero-right-grid">
            <!-- Tall left -->
            <div class="hero-img tall">
                <img src="https://res.cloudinary.com/dxbxiqgzb/image/upload/v1772164092/snapspace/user_1/l37191lwb9gajgxumgy6.jpg" alt="moi" />
            </div>
            <!-- Top right -->
            <div class="hero-img">
                <img src="https://res.cloudinary.com/dxbxiqgzb/image/upload/v1772164116/snapspace/user_1/wyuokpw48fkqbs2tnsbo.jpg" alt="moment" />
                <span class="label">#community</span>
            </div>
            <!-- Bottom right -->
            <div class="hero-img">
                <img src="https://res.cloudinary.com/dxbxiqgzb/image/upload/v1772164164/snapspace/user_1/qkenrmxpvjusrar4zcfz.jpg" alt="moment" />
                <span class="label">#moments</span>
            </div>
        </div>

        <a href="${pageContext.request.contextPath}/register" class="circle-btn">
            JOIN<br>NOW
        </a>
    </div>

</section>

</body>
</html>