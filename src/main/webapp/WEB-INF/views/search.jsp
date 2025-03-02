<%@ page import="com.example.SpringWeb.DTO.EmployerResponse" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.SpringWeb.DTO.AccountResponse" %>
<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Пошук клієнта</title>
    <link rel="stylesheet" type="text/css" href="/style.css">
</head>
<body>

<c:if test="${not empty error}">
    <p style="color:red;">${error}</p>
</c:if>

<h2>Пошук клієнта за ID</h2>
<form action="/customers/find" method="GET">
    <label for="id">Введіть ID клієнта:</label>
    <input type="number" id="id" name="id" required min="1">
    <button type="submit">Знайти</button>
</form>

<c:if test="${not empty customer}">
    <h3>Результати пошуку</h3>
    <p><strong>ID:</strong> ${customer.id}</p>
    <p><strong>Ім'я:</strong> ${customer.name}</p>
    <p><strong>Прізвище:</strong> ${customer.surname}</p>
    <p><strong>Email:</strong> ${customer.email}</p>
    <p><strong>Вік:</strong> ${customer.age}</p>
</c:if>

<%
    List<EmployerResponse> employers = (List<EmployerResponse>) request.getAttribute("employers");
    if (employers != null && !employers.isEmpty()) {
%>
<h3>Компанії, на які працює клієнт</h3>
<table border="1">
    <tr>
        <th>ID</th>
        <th>Назва компанії</th>
        <th>Адреса</th>
    </tr>
    <%
        for (EmployerResponse employer : employers) {
    %>
    <tr>
        <td><%= employer.getId() %></td>
        <td><%= employer.getName() %></td>
        <td><%= employer.getAddress() %></td>
    </tr>
    <% } %>
</table>
<%
} else {
%>
<p>Клієнт не працює в жодній компанії.</p>
<%
    }
%>

<%
    List<AccountResponse> accounts = (List<AccountResponse>) request.getAttribute("accounts");
    if (accounts != null && !accounts.isEmpty()) {
%>
<h3>Рахунки клієнта</h3>
<table border="1">
    <tr>
        <th>Номер рахунку</th>
        <th>Баланс</th>
        <th>Валюта</th>
    </tr>
    <%
        for (AccountResponse account : accounts) {
    %>
    <tr>
        <td><%= account.getAccountNumber() %></td>
        <td><%= account.getBalance() %></td>
        <td><%= account.getCurrency() %></td>
    </tr>
    <% } %>
</table>
<%
} else {
%>
<p>У цього клієнта немає рахунків.</p>
<%
    }
%>

<form action="/menu" method="GET">
    <button type="submit">Повернутися в меню</button>
</form>

</body>
</html>
