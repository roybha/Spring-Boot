<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Зняти гроші з рахунку</title>
    <link rel="stylesheet" type="text/css" href="/style.css">
</head>
<body>

<h2>Зняти гроші з рахунку</h2>

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

<form onsubmit="event.preventDefault(); withdraw();">
    <label for="accountNumber">Номер рахунку:</label>
    <input type="text" id="accountNumber" name="accountNumber" required><br><br>

    <label for="amount">Сума:</label>
    <input type="number" id="amount" name="amount" required min="0.01" step="0.01"><br><br>

    <button type="submit">Зняти гроші</button>
</form>
<script>
    var socket = new WebSocket('ws://localhost:9000/ws/accounts/withdraw');

    socket.onopen = function () {
        console.log("WebSocket з'єднання встановлено");
    };

    socket.onmessage = function (event) {
        var response = JSON.parse(event.data);

        if (response.status === "success") {
            alert("✅ Зняття грошей успішно виконано!");
        } else {
            alert("❌ Помилка: " + response.message);
        }
    };

    socket.onerror = function (error) {
        console.error("Помилка WebSocket:", error);
    };


    function withdraw() {
        var accountNumber = document.getElementById("accountNumber").value;
        var amount = document.getElementById("amount").value;


        var withdrawRequest = {
            operation: "WITHDRAW",
            accountNumber: accountNumber,
            amount: parseFloat(amount)
        };

        socket.send(JSON.stringify(withdrawRequest));
    }
</script>
<a href="/accounts/operation">Повернутися на сторінку опцій</a>
</body>
</html>
