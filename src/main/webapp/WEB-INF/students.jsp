<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>All Students</title>
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

        table {
            width: 80%;
            margin: 0 auto;
            border-collapse: collapse;
            background-color: #fff;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
            border-radius: 10px;
            overflow: hidden;
        }

        th, td {
            padding: 12px 15px;
            text-align: left;
        }

        th {
            background-color: #4CAF50;
            color: white;
            text-transform: uppercase;
            font-size: 14px;
        }

        tr:nth-child(even) {
            background-color: #f4f6f8;
        }

        tr:hover {
            background-color: #e0f2f1;
        }

        td {
            font-size: 14px;
            color: #555;
        }
    </style>
</head>
<body>
<h1>All Students</h1>

<table>
    <tr>
        <th>Name</th>
        <th>Email</th>
        <th>School</th>
        <th>Age</th>
    </tr>

    <c:forEach items="${students}" var="s">
        <tr>
            <td>${s.name}</td>
            <td>${s.email}</td>
            <td>${s.school}</td>
            <td>${s.age}</td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
