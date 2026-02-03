<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Student Details</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f6f8;
            margin: 0;
            padding: 20px;
        }

        h1 {
            text-align: center;
            color: #333;
            margin-bottom: 30px;
        }

        .card {
            max-width: 500px;
            margin: 0 auto;
            background-color: #fff;
            padding: 25px 30px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            border-radius: 12px;
        }

        .card p {
            font-size: 16px;
            color: #555;
            margin: 10px 0;
        }

        .card p strong {
            color: #333;
        }

        .not-found {
            text-align: center;
            color: #c0392b;
            font-weight: bold;
            margin-top: 50px;
        }

        a {
            display: inline-block;
            margin-top: 20px;
            text-decoration: none;
            color: #fff;
            background-color: #4CAF50;
            padding: 10px 20px;
            border-radius: 6px;
            transition: background-color 0.3s ease;
        }

        a:hover {
            background-color: #45a049;
        }
    </style>
</head>
<body>
<h1>Student Details</h1>

<c:if test="${not empty student}">
    <div class="card">
        <p><strong>ID:</strong> ${student.id}</p>
        <p><strong>Name:</strong> ${student.name}</p>
        <p><strong>Email:</strong> ${student.email}</p>
        <p><strong>School:</strong> ${student.school}</p>
        <p><strong>Age:</strong> ${student.age}</p>
        <p><strong>DOB:</strong> ${student.dob}</p>
    </div>
</c:if>

<c:if test="${empty student}">
    <p class="not-found">Student not found.</p>
</c:if>

<div style="text-align: center;">
    <a href="${pageContext.request.contextPath}/students/view">Back to list</a>
</div>
</body>
</html>
