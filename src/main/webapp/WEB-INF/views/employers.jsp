<%@ page import="java.util.List" %>
<%@ page import="com.example.SpringWeb.DTO.EmployerResponse" %>
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
        List<EmployerResponse> employers = (List<EmployerResponse>) request.getAttribute("employers");
        Integer currentPage = (Integer) request.getAttribute("currentPage");
        Integer totalPages = (Integer) request.getAttribute("totalPages");
        Long totalEmployers = (Long) request.getAttribute("totalEmployers");
        Integer size = (Integer) request.getAttribute("size");

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
            for (EmployerResponse employer : employers) {
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

    <!-- Пагінація -->
    <div class="pagination">
        <form action="/employers/all" method="GET">
            <input type="hidden" name="size" value="<%= size %>">

            <% if (currentPage > 1) { %>
            <button type="submit" name="page" value="1">Перша</button>
            <button type="submit" name="page" value="<%= currentPage - 1 %>">Попередня</button>
            <% } %>

            <% if (currentPage < totalPages) { %>
            <button type="submit" name="page" value="<%= currentPage + 1 %>">Наступна</button>
            <button type="submit" name="page" value="<%= totalPages %>">Остання</button>
            <% } %>
        </form>
    </div>

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
