<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Welcome to SnapSpace</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f6d365;
            margin: 0;
            height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .container {
            text-align: center;
            background-color: rgba(255, 255, 255, 0.6);
            padding: 50px 80px;
            border-radius: 15px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.2);
        }

        h1 {
            font-size: 48px;
            margin-bottom: 20px;
            color: #333;
        }

        p {
            font-size: 18px;
            margin-bottom: 40px;
            color: #555;
        }

        .btn {
            display: inline-block;
            padding: 12px 25px;
            margin: 0 10px;
            font-size: 16px;
            color: #fff;
            background-color: #4CAF50;
            border: none;
            border-radius: 8px;
            text-decoration: none;
            transition: background-color 0.3s;
        }

        .btn:hover {
            background-color: #45a049;
        }
    </style>
</head>
<body>

<div class="container">
    <h1>Welcome to SnapSpace</h1>
    <p>SnapSpace is your personal image-sharing space. Upload, explore, and enjoy amazing visuals from the community.</p>

    <a href="${pageContext.request.contextPath}/login" class="btn">Login</a>
    <a href="${pageContext.request.contextPath}/register" class="btn">Register</a>
</div>

</body>
</html>