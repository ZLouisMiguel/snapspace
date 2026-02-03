<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Upload Image - SnapSpace</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f6f8;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }

        form {
            background-color: #fff;
            padding: 30px 40px;
            border-radius: 12px;
            box-shadow: 0 6px 15px rgba(0,0,0,0.1);
            width: 400px;
            text-align: center;
        }

        h2 {
            margin-bottom: 20px;
            color: #333;
        }

        input[type="file"] {
            margin: 15px 0;
        }

        button {
            padding: 10px 20px;
            background-color: #4CAF50;
            border: none;
            border-radius: 6px;
            color: white;
            font-size: 16px;
            cursor: pointer;
            transition: background-color 0.3s;
        }

        button:hover {
            background-color: #45a049;
        }

        a {
            display: block;
            margin-top: 15px;
            text-decoration: none;
            color: #4CAF50;
        }
    </style>
</head>
<body>

<form action="${pageContext.request.contextPath}/upload" method="post" enctype="multipart/form-data">
    <h2>Upload Image</h2>

    <label>Select Image:</label>
    <input type="file" name="image" accept="image/*" required />

    <br/>
    <button type="submit">Upload</button>

    <a href="${pageContext.request.contextPath}/feed">Back to Feed</a>
</form>

</body>
</html>