<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Створити нову компанію</title>
    <link rel="stylesheet" type="text/css" href="/style.css">
</head>
<body>

<%
    String successMessage = (String) request.getAttribute("success");
    String errorMessage = (String) request.getAttribute("error");

    if (successMessage != null) {
%>
<p style="color:green;"><%= successMessage %></p>
<%
    }

    if (errorMessage != null) {
%>
<p style="color:red;"><%= errorMessage %></p>
<%
    }
%>

<h2>Створення нової компанії</h2>

<!-- Форма для створення нової компанії -->
<form action="/employers/addEmployer" method="POST">
    <label for="name">Назва компанії:</label>
    <input type="text" id="name" name="name" required><br><br>

    <label for="address">Адреса:</label>
    <input type="text" id="address" name="address" required><br><br>

    <button type="submit">Створити</button>
</form>

<a href="/menu">Повернутися на головне меню</a>

</body>
</html>
