<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html lang="ru">
<head>
    <title>Meals</title>
    <style type="text/css">
        table {
            border: 1px solid #000;
        }

        td, th {
            padding: 7px;
            border: 1px solid #000;
        }
    </style>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>
<table>
    <thead>
    <tr>
        <th>Date</th>
        <th>Description</th>
        <th>Calories</th>
    </tr>
    </thead>
    <tbody>
    <%--@elvariable id="list" type="java.util.List"--%>
    <c:forEach var="list1" items="${list}">
        <tr style="color: ${list1.excess? 'firebrick': 'darkgreen'}">
            <fmt:parseDate value="${list1.dateTime}" pattern="yyyy-MM-d'T'HH:mm" var="parsedDateTime" type="both"/>
            <td><fmt:formatDate pattern="yyyy-MM-dd HH:mm" value="${parsedDateTime}"/></td>
            <td>${list1.description}</td>
            <td>${list1.calories}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>