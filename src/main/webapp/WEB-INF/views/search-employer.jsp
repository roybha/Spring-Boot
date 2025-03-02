<%@ page import="com.example.SpringWeb.DTO.EmployerResponse" %>
<%@ page import="com.example.SpringWeb.DTO.CustomerResponse" %>
<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Інформація про компанію</title>
    <link rel="stylesheet" type="text/css" href="/style.css">
</head>
<body>

<c:if test="${not empty error}">
    <p style="color:red;">${error}</p>
</c:if>

<h2>Пошук компанії за ID</h2>

<form action="/employers/find" method="GET">
    <label for="employerName">Введіть назву компанії:</label>
    <input type="text" id="employerName" name="employerName" required >
    <button type="submit">Знайти</button>
</form>

<c:if test="${not empty employer}">
    <h3>Інформація про компанію</h3>
    <p><strong>Назва:</strong> ${employer.name}</p>
    <p><strong>Адреса:</strong> ${employer.address}</p>
    <%
        List<CustomerResponse> customers = (List<CustomerResponse>) request.getAttribute("customers");
        if (customers != null && !customers.isEmpty()) {
    %>
    <h3>Список клієнтів компанії</h3>
    <table border="1">
        <tr>
            <th>ID клієнта</th>
            <th>Ім'я</th>
            <th>Прізвище</th>
            <th>Email</th>
            <th>Вік</th>
        </tr>
        <%
            for (CustomerResponse customer : customers) {
        %>
        <tr>
            <td><%=customer.getId()%></td>
            <td><%= customer.getName() %></td>
            <td><%= customer.getSurname() %></td>
            <td><%= customer.getEmail() %></td>
            <td><%= customer.getAge() %></td>
        </tr>
        <% } %>
    </table>
    <%
    } else {
    %>
    <p>У цій компанії немає клієнтів.</p>
    <%
        }
    %>
</c:if>



<form action="/menu" method="GET">
    <button type="submit">Повернутися в меню</button>
</form>

</body>
</html>
