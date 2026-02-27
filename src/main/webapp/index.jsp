<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>SnapSpace</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Bebas+Neue&family=DM+Sans:wght@300;400;500&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/styles.css">
</head>
<body>

<!-- NAV -->
<nav>
    <a class="nav-logo" href="#">SNAP<span>SPACE</span></a>
    <div class="nav-links">
        <a href="#">Explore</a>
        <a href="#">Community</a>
        <a href="${pageContext.request.contextPath}/login">Login</a>
        <a href="${pageContext.request.contextPath}/register" class="btn-primary">Get Started</a>
    </div>
</nav>

<!-- HERO -->
<section class="hero">

    <!-- LEFT -->
    <div class="hero-left">
        <div class="hero-tag">Your shots. Your space.</div>

        <h1 class="hero-headline">
            FOR<br>
            EVERY<br>
            <span>MOMENT</span>
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

        <!-- Thumbnail strip — swap srcs for real Cloudinary URLs -->
        <div class="hero-strip">
            <div class="hero-strip-item"><div class="placeholder"></div></div>
            <div class="hero-strip-item"><div class="placeholder"></div></div>
            <div class="hero-strip-item"><div class="placeholder"></div></div>
            <div class="hero-strip-item"><div class="placeholder"></div></div>
        </div>
    </div>

    <!-- RIGHT -->
    <div class="hero-right">
        <div class="hero-right-grid">
            <!-- Tall left image -->
            <div class="hero-img tall">
                <div class="placeholder"></div>
            </div>
            <!-- Top right -->
            <div class="hero-img">
                <div class="placeholder"></div>
                <span class="label">#community</span>
            </div>
            <!-- Bottom right -->
            <div class="hero-img">
                <div class="placeholder"></div>
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