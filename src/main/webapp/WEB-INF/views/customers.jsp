<%@ page import="com.example.SpringWeb.DTO.CustomerResponse" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<section class="customers-info">
    <%
        List<CustomerResponse> customers = (List<CustomerResponse>) request.getAttribute("customers");
        Integer currentPage = (Integer) request.getAttribute("currentPage");
        Integer totalPages = (Integer) request.getAttribute("totalPages");
        Long totalCustomers = (Long) request.getAttribute("totalCustomers");
        Integer size = (Integer) request.getAttribute("size"); // Отримуємо size з моделі

        if (customers != null && !customers.isEmpty()) {
    %>
    <h2>Список клієнтів</h2>
    <table border="1">
        <tr>
            <th>ID</th>
            <th>Ім'я</th>
            <th>Прізвище</th>
            <th>Email</th>
            <th>Номер телефону</th>
        </tr>
        <%
            for (CustomerResponse customer : customers) {
        %>
        <tr>
            <td><%= customer.getId() %></td>
            <td><%= customer.getName() %></td>
            <td><%= customer.getSurname() %></td>
            <td><%= customer.getEmail() %></td>
            <td><%= customer.getPhoneNumber() %></td>
        </tr>
        <%
            }
        %>
    </table>

    <div class="pagination">
        <form action="/customers/all" method="GET">
            <!-- Задати size у вигляді прихованого поля -->
            <input type="hidden" name="size" value="<%= size != null ? size : 10 %>">

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
    <p>Немає доступних клієнтів.</p>
    <%
        }
    %>
</section>
