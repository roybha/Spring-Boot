<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Поповнити рахунок</title>
    <link rel="stylesheet" type="text/css" href="/style.css">
</head>
<body>

<h2>Поповнити рахунок</h2>

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

<form onsubmit="deposit(); return false;">
    <label for="accountNumber">Номер рахунку:</label>
    <input type="text" id="accountNumber" name="accountNumber" required><br><br>

    <label for="balance">Сума:</label>
    <input type="number" id="balance" name="balance" required min="0.01" step="0.01"><br><br>

    <button type="submit">Поповнити рахунок</button>
</form>

<script type="text/javascript">
    var socket = new WebSocket('ws://localhost:9000/ws/accounts/deposit');

    socket.onopen = function() {
        console.log("WebSocket підключено");
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

    function deposit() {
        var accountNumber = document.getElementById("accountNumber").value;
        var amount = document.getElementById("balance").value;

        var message = {
            "operation": "DEPOSIT",
            "accountNumber": accountNumber,
            "amount": amount
        };

        socket.send(JSON.stringify(message));
    }
</script>

<a href="/accounts/operation">Повернутися на сторінку опцій</a>

</body>
</html>
