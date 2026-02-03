<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title> Register </title>
    <style>
        /* Reset some defaults */
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f6f8;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }

        h1 {
            text-align: center;
            color: #333;
        }

        form {
            background-color: #fff;
            padding: 30px 40px;
            border-radius: 12px;
            box-shadow: 0 6px 15px rgba(0,0,0,0.1);
            width: 350px;
        }

        label {
            display: block;
            margin-bottom: 15px;
            color: #555;
        }

        input[type="text"],
        input[type="email"],
        input[type="date"] {
            width: 100%;
            padding: 8px 12px;
            margin-top: 5px;
            border: 1px solid #ccc;
            border-radius: 6px;
            font-size: 14px;
            box-sizing: border-box;
        }

        button {
            width: 100%;
            padding: 10px;
            background-color: #4CAF50;
            border: none;
            border-radius: 6px;
            color: white;
            font-size: 16px;
            cursor: pointer;
            transition: 0.3s;
        }

        button:hover {
            background-color: #45a049;
        }
    </style>
</head>
<body>

<c:out value = "Hello World"/>
<form action="/register" method="post">
    <h1>${name}</h1>
    <label> Name:
        <input type="text" name="name" required>
    </label>

    <label> Email:
        <input type="email" name="email" required>
    </label>

    <label> School:
        <input type="text" name="school" required>
    </label>

    <label> DOB:
        <input type="date" name="dob" required>
    </label>

    <button type="submit">Submit</button>
</form>
</body>
</html>
