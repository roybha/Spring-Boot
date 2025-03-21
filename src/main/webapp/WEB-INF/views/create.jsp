<%@ page import="com.example.SpringWeb.DTO.CustomerRequest" %>
<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Створити нового клієнта</title>
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
<h2>Створення нового клієнта</h2>

<form action="/customers/add" method="POST">
    <label for="name">Ім'я:</label>
    <input type="text" id="name" name="name"  required><br><br>

    <label for="surname">Прізвище:</label>
    <input type="text" id="surname" name="surname" required><br><br>

    <label for="email">Email:</label>
    <input type="email" id="email" name="email"  required><br><br>

    <label for="age">Вік:</label>
    <input type="number" id="age" name="age" min="18" required><br><br>

    <label for="phoneNumber">Номер телефону:</label>
    <input type="tel" id="phoneNumber" name="phoneNumber"  pattern="^\+?[0-9]*$" title="Тільки цифри та символ '+' дозволені" required><br><br>

    <label for="password">Пароль:</label>
    <input type="password" id="password" name="password"  minlength="8" required><br><br>

    <button type="submit">Створити</button>
</form>

<a href="/menu">Повернутися на головне меню</a>
</body>
</html>
