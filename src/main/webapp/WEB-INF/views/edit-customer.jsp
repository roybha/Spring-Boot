<%@ page import="com.example.SpringWeb.model.Customer" %>
<%@ page import="com.example.SpringWeb.model.Account" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.SpringWeb.model.Employer" %>
<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Редагування клієнта</title>
    <link rel="stylesheet" type="text/css" href="/style.css">
</head>
<body>

<h2>Редагування клієнта</h2>

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

<form action="/customers/change" method="GET">
    <label for="searchId">Введіть ID клієнта:</label>
    <input type="number" id="searchId" name="id" required min="1">
    <button type="submit">Знайти</button>
</form>

<hr>

<%
    Customer customer = (Customer) request.getAttribute("customer");
    if (customer != null) {
%>
<form action="/customers/update/<%= customer.getId() %>" method="POST">
    <label for="name">Ім'я:</label>
    <input type="text" id="name" name="name" value="<%= customer.getName() %>" required><br><br>

    <label for="surname">Прізвище:</label>
    <input type="text" id="surname" name="surname" value="<%= customer.getSurname() %>" required><br><br>

    <label for="email">Email:</label>
    <input type="email" id="email" name="email" value="<%= customer.getEmail() %>" required><br><br>

    <label for="age">Вік:</label>
    <input type="number" id="age" name="age" value="<%= customer.getAge() %>" min="18" required><br><br>

    <button type="submit">Зберегти зміни</button>
</form>

<%
    List<Account> accounts = (List<Account>) request.getAttribute("accounts");
    if (accounts != null && !accounts.isEmpty()) {
%>
<h3>Рахунки клієнта</h3>
<table border="1">
    <tr>
        <th>Номер рахунку</th>
        <th>Баланс</th>
        <th>Валюта</th>
        <th>Дії</th>
    </tr>
    <%
        for (Account account : accounts) {
    %>
    <tr>
        <td><%= account.getAccountNumber() %></td>
        <td><%= account.getBalance() %></td>
        <td><%= account.getCurrency() %></td>
        <td>
            <form action="/accounts/delete" method="POST" style="display:inline;">
                <input type="hidden" name="accountNumber" value="<%= account.getAccountNumber() %>">
                <button type="submit" onclick="return confirm('Ви впевнені, що хочете видалити цей рахунок?')">Видалити</button>
            </form>
        </td>
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

<h3>Створити новий рахунок</h3>
<form action="/accounts/create" method="POST">
    <input type="hidden" name="customerId" value="<%= customer.getId() %>">
    <label for="balance">Баланс:</label>
    <input type="number" id="balance" name="balance" required min="0" step="0.01"><br><br>

    <label for="currency">Валюта:</label>
    <select id="currency" name="currency" required>
        <option value="USD">USD</option>
        <option value="EUR">EUR</option>
        <option value="UAH">UAH</option>
        <option value="CHF">CHF</option>
        <option value="GBP">GBP</option>
    </select><br><br>

    <button type="submit">Створити рахунок</button>
</form>


<h3>Додати клієнта до компанії</h3>
<form action="/customers/add_to_employer" method="POST">
    <input type="hidden" name="customerId" value="<%= customer.getId() %>">
    <label for="employerName">Назва компанії:</label>
    <input type="number" id="employerName" name="employerName" required ><br><br>
    <button type="submit">Додати до компанії</button>
</form>
<h3>Компанії, де працює цей клієнт</h3>

<%
    List<Employer> employers = (List<Employer>) request.getAttribute("employers");
    if (employers != null && !employers.isEmpty()) {
%>
<table border="1">
    <tr>
        <th>ID</th>
        <th>Назва</th>
        <th>Адреса</th>
        <th>Дії</th>
    </tr>
    <%
        for (Employer employer : employers) {
    %>
    <tr>
        <td><%= employer.getId() %></td>
        <td><%= employer.getName() %></td>
        <td><%= employer.getAddress() %></td>
        <td>
            <form action="/customers/remove_from_employer/<%= customer.getId() %>?employerId=<%= employer.getId() %>" method="POST"
                  onsubmit="return confirm('Ви впевнені, що хочете видалити клієнта з цього роботодавця?')">
                <button type="submit" style="color: darkorange;">Видалити з цієї компанії</button>
            </form>
        </td>
    </tr>
    <% } %>
</table>
<%
} else {
%>
<p>Цей клієнт не має роботодавця.</p>
<%
    }
%>

<h3>Видалити клієнта</h3>
<form action="/customers/delete/<%= customer.getId() %>" method="POST"
      onsubmit="return confirm('Ви впевнені, що хочете видалити цього клієнта?')">
    <button type="submit" style="color: red;">Видалити клієнта</button>
</form>
<%
    }
%>

<a href="/menu">Повернутися в меню</a>

</body>
</html>

