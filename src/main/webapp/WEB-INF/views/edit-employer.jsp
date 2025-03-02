<%@ page import="com.example.SpringWeb.DTO.EmployerResponse" %>
<%@ page import="com.example.SpringWeb.DTO.CustomerResponse" %>
<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Редагування компанії</title>
    <link rel="stylesheet" type="text/css" href="/style.css">
</head>
<body>

<h2>Редагування компанії</h2>

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
<%
    String success = request.getParameter("success");
    String error = request.getParameter("error");

    if (success != null && !success.isEmpty()) {
%>
<p style="color:green;"><%= success %></p>
<%
    }
    if (error != null && !error.isEmpty()) {
%>
<p style="color:red;"><%= error %></p>
<%
    }
%>

<form action="/employers/change" method="GET">
    <label for="employerName">Введіть назву компанії:</label>
    <input type="text" id="employerName" name="employerName" required>
    <button type="submit">Знайти</button>
</form>

<hr>

<%
    EmployerResponse employer = (EmployerResponse) request.getAttribute("employer");
    if (employer != null) {
%>
<form action="/employers/update/<%= employer.getId() %>" method="POST">
    <label for="name">Назва компанії:</label>
    <input type="text" id="name" name="name" value="<%= employer.getName() %>" required><br><br>

    <label for="address">Адреса:</label>
    <input type="text" id="address" name="address" value="<%= employer.getAddress() %>" required><br><br>


    <button type="submit">Зберегти зміни</button>
</form>

<h3>Клієнти компанії</h3>
<%
    List<CustomerResponse> customers = (List<CustomerResponse>) request.getAttribute("customers");
    if (customers != null && !customers.isEmpty()) {
%>
<table border="1">
    <tr>
        <th>Ім'я</th>
        <th>Прізвище</th>
        <th>Email</th>
        <th>Дії</th>
    </tr>
    <%
        for (CustomerResponse customer : customers) {
    %>
    <tr>
        <td><%= customer.getName() %></td>
        <td><%= customer.getSurname() %></td>
        <td><%= customer.getEmail() %></td>
        <td>
            <form action="/employers/deleteCustomer" method="POST" style="display:inline;">
                <input type="hidden" name="employerName" value="<%= employer.getName() %>">
                <input type="hidden" name="customerId" value="<%= customer.getId() %>">
                <button type="submit" onclick="return confirm('Ви впевнені, що хочете видалити цього клієнта?')">Видалити</button>
            </form>
        </td>
    </tr>
    <% } %>
</table>
<%
} else {
%>
<p>У компанії немає клієнтів.</p>
<%
    }
%>

<h3>Додати клієнта до компанії</h3>
<form action="/employers/addCustomer" method="POST">
    <input type="hidden" name="employerName" value="<%= employer.getName() %>">
    <label for="customerId">ID клієнта:</label>
    <input type="number" id="customerId" name="customerId" required min="1">
    <button type="submit">Додати</button>
</form>

<form action="/employers/deleteEmployer/<%= employer.getName() %>" method="POST"
      onsubmit="return confirm('Ви впевнені, що хочете видалити цю компанію?')">
    <button type="submit" style="color: red;">Видалити компанію</button>
</form>

<%
    }
%>

<a href="/menu">Повернутися в меню</a>

</body>
</html>
