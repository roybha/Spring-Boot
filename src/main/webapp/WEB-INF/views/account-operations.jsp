<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Операції з рахунком</title>
    <link rel="stylesheet" type="text/css" href="/style.css">
</head>
<body>

<h2>Операції з рахунком</h2>

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
<br>
<!-- Посилання на відповідні сторінки для кожної операції -->
<a href="/accounts/deposit">Поповнити рахунок</a><br>
<a href="/accounts/withdraw">Зняти гроші</a><br>
<a href="/accounts/transfer">Переказати гроші</a>
<br>

<a href="/menu">Повернутися до головного меню</a>

</body>
</html>
