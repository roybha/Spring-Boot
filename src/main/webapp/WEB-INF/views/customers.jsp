<%@ page import="com.example.SpringWeb.model.Customer" %>
<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Список клієнтів</title>
    <link rel="stylesheet" type="text/css" href="/style.css">
</head>
<body>
<section class="customers-info">
    <%
        // Отримуємо список клієнтів з атрибуту моделі
        List<Customer> customers = (List<Customer>) request.getAttribute("customers");
        if (customers != null && !customers.isEmpty()) {
    %>
    <h2>Список клієнтів</h2>
    <table border="1">
        <tr>
            <th>ID</th>
            <th>Ім'я</th>
            <th>Прізвище</th>
            <th>Email</th>
        </tr>
        <%
            for (Customer customer : customers) {
        %>
        <tr>
            <td><%= customer.getId() %></td>
            <td><%= customer.getName() %></td>
            <td><%= customer.getSurname() %></td>
            <td><%= customer.getEmail() %></td>
        </tr>
        <%
            }
        %>
    </table>
    <%
    } else {
    %>
    <p>Немає доступних клієнтів.</p>
    <%
        }
    %>
</section>

<a href="/menu">Повернутися на головне меню</a>
</body>
</html>
