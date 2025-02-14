<%@ page import="com.example.SpringWeb.model.Employer" %>
<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Список роботодавців</title>
    <link rel="stylesheet" type="text/css" href="/style.css">
</head>
<body>
<section class="employers-info">
    <%
        // Отримуємо список роботодавців з атрибуту моделі
        List<Employer> employers = (List<Employer>) request.getAttribute("employers");
        if (employers != null && !employers.isEmpty()) {
    %>
    <h2>Список роботодавців</h2>
    <table border="1">
        <tr>
            <th>ID</th>
            <th>Назва компанії</th>
            <th>Адреса</th>
        </tr>
        <%
            for (Employer employer : employers) {
        %>
        <tr>
            <td><%= employer.getId() %></td>
            <td><%= employer.getName() %></td>
            <td><%= employer.getAddress() %></td>
        </tr>
        <%
            }
        %>
    </table>
    <%
    } else {
    %>
    <p>Немає доступних роботодавців.</p>
    <%
        }
    %>
</section>

<a href="/menu">Повернутися на головне меню</a>
</body>
</html>
