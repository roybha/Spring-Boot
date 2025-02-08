<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Помилка</title>
</head>
<body>

<div class="error-container">
    <h1>Сталася помилка!</h1>
    <p><%= request.getParameter("message") != null ? request.getParameter("message") : "На жаль, виникла проблема під час обробки вашого запиту." %></p>
    <a href="/menu">Повернутися на головну</a>
</div>

</body>
</html>