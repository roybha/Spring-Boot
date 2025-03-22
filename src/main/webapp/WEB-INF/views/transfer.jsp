<%@ page import="java.util.List" %>
<%@ page import="com.example.SpringWeb.DTO.TransferRequest" %>
<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Переказ грошей</title>
    <link rel="stylesheet" type="text/css" href="/style.css">
</head>
<body>

<h2>Переказ грошей</h2>

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

<form onsubmit="event.preventDefault(); transfer();">
    <label for="fromAccountNumber">Номер рахунку:</label>
    <input type="text" id="fromAccountNumber" name="fromAccountNumber" required><br><br>

    <label for="toAccountNumber">Номер рахунку одержувача:</label>
    <input type="text" id="toAccountNumber" name="toAccountNumber" required><br><br>

    <label for="amount">Сума:</label>
    <input type="number" id="amount" name="amount" required min="0.01" step="0.01"><br><br>

    <label for="currency">Валюта:</label>
    <select id="currency" name="currency" required>
        <option value="USD">USD</option>
        <option value="EUR">EUR</option>
        <option value="UAH">UAH</option>
        <option value="CHF">CHF</option>
        <option value="GBP">GBP</option>
    </select><br><br>

    <button type="submit">Переказати гроші</button>
</form>
<script>
    var socket = new WebSocket('ws://localhost:9000/ws/accounts/transfer');

    socket.onopen = function () {
        console.log("WebSocket з'єднання встановлено");
    };

    socket.onmessage = function (event) {
        var response = JSON.parse(event.data);


        if (response.status === "success") {
            alert("✅ " + response.message);
        } else {
            alert("❌ Помилка: " + response.message);
        }
    };

    socket.onerror = function (error) {
        console.error("Помилка WebSocket:", error);
    };

    function transfer() {
        var fromAccount = document.getElementById("fromAccountNumber").value;
        var toAccount = document.getElementById("toAccountNumber").value;
        var amount = document.getElementById("amount").value;
        var currency = document.getElementById("currency").value;

        var transferRequest = {
            operation: "TRANSFER",
            fromAccount: fromAccount,
            toAccount: toAccount,
            amount: parseFloat(amount),
            currency: currency
        };

        socket.send(JSON.stringify(transferRequest));
    }
</script>

<a href="/accounts/operation">Повернутися на сторінку опцій</a>

</body>
</html>
