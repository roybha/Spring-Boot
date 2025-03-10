<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Реєстрація</title>
</head>
<body>
<h2>Реєстрація</h2>

<% if (request.getAttribute("error") != null) { %>
<p style="color: red;"><%= request.getAttribute("error") %></p>
<% } %>
<form:form action="${pageContext.request.contextPath}/register" method="POST" modelAttribute="admin">
    <label for="username">Ім'я користувача:</label>
    <form:input path="username" id="username"/>
    <br>

    <label for="password">Пароль:</label>
    <form:password path="password" id="password"/>
    <br>

    <input type="submit" value="Зареєструватися"/>
</form:form>

</body>
</html>

