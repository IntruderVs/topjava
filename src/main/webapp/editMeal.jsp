<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html lang="ru">
<head>
    <%--@elvariable id="mealForEdit" type="model.Meal"--%>
    <title>${mealForEdit.id != null ? "Edit meal" : "New meal"}</title>
    <style type="text/css">
        div.field {
            padding-bottom: 5px;
        }

        div.field label {
            display: block;
            float: left;
            width: 120px;
            height: 15px;
        }
    </style>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>${mealForEdit.id != null ? "Edit meal" : "New meal"}</h2>

<form method="POST" action='meals' name="frmAddMeal">
    <jsp:useBean id="mealForEdit" type="ru.javawebinar.topjava.model.MealTo"/>//for dot autocorrect
    <div class="field">
        <label for="dateTime">Date time</label>
        <input type="datetime-local" name="dateTime" id="dateTime" value="${mealForEdit.dateTime}" required/> <br/>
    </div>
    <div class="field">
        <label for="description">Description</label>
        <input type="text" name="description" id="description" value="${mealForEdit.description}" required/>
        <br/>
    </div>
    <div class="field">
        <label for="calories">Calories</label>
        <input type="number" name="calories" id="calories" value="${mealForEdit.calories}" required/> <br/>
    </div>
    <button type="submit" value="${mealForEdit.id}" name="mealId">Save</button>
    <button type="button" onclick="window.location='meals';return false;">Cancel</button>
</form>
</body>
</html>