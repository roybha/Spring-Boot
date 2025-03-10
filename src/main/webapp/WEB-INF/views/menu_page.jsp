<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Головне меню</title>
    <link rel="stylesheet" type="text/css" href="/style.css">
</head>
<body>

<h2>Головне меню</h2>

<ul>
    <li><a href="/customers/all">Переглянути список клієнтів</a></li>
    <li><a href="/customers/create">Додати нового клієнта</a></li>
    <li><a href="/customers/find">Знайти певного користувача за ID</a></li>
    <li><a href="/customers/change">Редагувати клієнта</a></li>
    <li><a href="/accounts/operation">Провести операції із акаунтами </a></li>
    <li><a href="/employers/create">Додати нову компанію</a></li>
    <li><a href="/employers/change">Редагувати компанію</a></li>
    <li><a href="/employers/find">Знайти певну компанію за назвою</a></li>
    <li><a href="/employers/all">Переглянути список компаній</a></li>
</ul>
<form action="${pageContext.request.contextPath}/logout" method="post">
    <button type="submit">Вийти</button>
</form>
</body>
</html>
