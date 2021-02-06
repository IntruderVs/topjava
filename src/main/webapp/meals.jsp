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

        tr.excess {
            color: firebrick;
        }

        tr {
            color: darkgreen
        }
    </style>
</head>
<body>
<br>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>
<a href="meals?action=add">Add meal</a>
<br>
<table>
    <thead>
    <tr>
        <th>Date</th>
        <th>Description</th>
        <th>Calories</th>
        <th></th>
        <th></th>
    </tr>
    </thead>
    <tbody>
    <%--@elvariable id="listMealTo" type="java.util.List"--%>
    <c:forEach var="mealTo" items="${listMealTo}">
        <tr ${mealTo.excess? "class='excess'": ""}>
            <fmt:parseDate value="${mealTo.dateTime}" pattern="yyyy-MM-d'T'HH:mm" var="parsedDateTime" type="both"/>
            <td><fmt:formatDate pattern="yyyy-MM-dd HH:mm" value="${parsedDateTime}"/></td>
            <td>${mealTo.description}</td>
            <td>${mealTo.calories}</td>
            <td><a href="meals?action=edit&mealId=${mealTo.id}">Update</a></td>
            <td><a href="meals?action=delete&mealId=${mealTo.id}">Delete</a></td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>