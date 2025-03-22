<%@ page import="com.example.SpringWeb.DTO.CustomerRequest" %>
<%@ page import="com.example.SpringWeb.DTO.CustomerResponse" %>
<%@ page import="com.example.SpringWeb.DTO.AccountResponse" %>
<%@ page import="com.example.SpringWeb.DTO.EmployerResponse" %>
<%@ page import="java.util.List" %>
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
    CustomerResponse customer = (CustomerResponse) request.getAttribute("customer");
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
<h3>Рахунки клієнта</h3>
<table border="1" id="accountsTable">
    <tr>
        <th>Номер рахунку</th>
        <th>Баланс</th>
        <th>Валюта</th>
        <th>Дії</th>
    </tr>
    <tbody id="accountsBody">
    <%
        List<AccountResponse> accounts = (List<AccountResponse>) request.getAttribute("accounts");
        if (accounts != null && !accounts.isEmpty()) {
            for (AccountResponse account : accounts) {
    %>
    <tr id="row-<%= account.getAccountNumber() %>">
        <td><%= account.getAccountNumber() %></td>
        <td><%= account.getBalance() %></td>
        <td><%= account.getCurrency() %></td>
        <td>
            <button onclick="deleteAccount('<%= account.getAccountNumber() %>')">
                Видалити
            </button>
        </td>
    </tr>
    <%
        }
    } else {
    %>
    <tr id="noAccountsRow">
        <td colspan="4">У цього клієнта немає рахунків.</td>
    </tr>
    <% } %>
    </tbody>
</table>

<script>
    var socket = new WebSocket('ws://localhost:9000/ws/accounts/delete');

    socket.onopen = function() {
        console.log("WebSocket підключено для оновлення рахунків");
    };

    socket.onmessage = function (event) {
        var response = JSON.parse(event.data);
        if (response.status === "success") {
            alert("✅ " + response.message);
        } else {
            alert("❌ Помилка: " + response.message);
        }
    };

    socket.onerror = function(event) {
        console.error("Помилка WebSocket: ", event);
    };

    socket.onclose = function(event) {
        console.log("WebSocket з'єднання закрите");
    };

    function deleteAccount(accountNumber) {
        if (!confirm('Ви впевнені, що хочете видалити цей рахунок?')) {
            return;
        }

        var message = {
            "operation": "DELETE",
            "accountNumber": accountNumber
        };

        socket.send(JSON.stringify(message));
    }
</script>


<h3>Створити новий рахунок</h3>
<form onsubmit="createAccount(); return false;">
    <input type="hidden" id="customerId" value="<%= customer.getId() %>">

    <label for="balance">Баланс:</label>
    <input type="number" id="balance" required min="0" step="0.01"><br><br>

    <label for="currency">Валюта:</label>
    <select id="currency" required>
        <option value="USD">USD</option>
        <option value="EUR">EUR</option>
        <option value="UAH">UAH</option>
        <option value="CHF">CHF</option>
        <option value="GBP">GBP</option>
    </select><br><br>

    <button type="submit">Створити рахунок</button>
</form>
<script>
    var socket = new WebSocket('ws://localhost:9000/ws/accounts/create');

    socket.onopen = function() {
        console.log("WebSocket підключено для створення рахунку");
    };

    socket.onmessage = function (event) {
        var response = JSON.parse(event.data);
        if (response.status === "success") {
            alert("✅ " + response.message);
        } else {
            alert("❌ Помилка: " + response.message);
        }
    };

    socket.onerror = function(event) {
        console.error("Помилка WebSocket: ", event);
    };

    socket.onclose = function(event) {
        console.log("WebSocket з'єднання закрите");
    };

    function createAccount() {
        var customerId = document.getElementById("customerId").value;
        var balance = document.getElementById("balance").value;
        var currency = document.getElementById("currency").value;

        var message = {
            "operation": "CREATE",
            "customerId": customerId,
            "balance": balance,
            "currency": currency
        };

        socket.send(JSON.stringify(message));
    }
</script>


<h3>Додати клієнта до компанії</h3>
<form action="/customers/add_to_employer" method="POST">
    <input type="hidden" name="customerId" value="<%= customer.getId() %>">
    <label for="employerName">Назва компанії:</label>
    <input type="text" id="employerName" name="employerName" required ><br><br>
    <button type="submit">Додати до компанії</button>
</form>
<h3>Компанії, де працює цей клієнт</h3>

<%
    List<EmployerResponse> employers = (List<EmployerResponse>) request.getAttribute("employers");
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
        for (EmployerResponse employer : employers) {
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

