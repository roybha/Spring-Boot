<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Поповнити рахунок</title>
    <link rel="stylesheet" type="text/css" href="/style.css">
</head>
<body>

<h2>Поповнити рахунок</h2>

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

<form action="/accounts/deposit" method="POST">
    <input type="hidden" name="operation" value="deposit">

    <label for="accountNumber">Номер рахунку:</label>
    <input type="text" id="accountNumber" name="accountNumber" required><br><br>

    <label for="balance">Сума:</label>
    <input type="number" id="balance" name="balance" required min="0.01" step="0.01"><br><br>

    <button type="submit">Поповнити рахунок</button>
</form>

<a href="/accounts/operation">Повернутися на сторінку опцій</a>

</body>
</html>
