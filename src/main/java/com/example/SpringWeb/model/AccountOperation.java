package com.example.SpringWeb.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AccountOperation {
    NO_OP("NO_OP"),
    TRANSFER("TRANSFER"),
    DEPOSIT("DEPOSIT"),
    WITHDRAW("WITHDRAW"),
    CREATE("CREATE"),
    DELETE("DELETE");
    private final String name;
    public static AccountOperation getFromName(String name) {
        return switch (name){
            case "TRANSFER" -> TRANSFER;
            case "DEPOSIT" -> DEPOSIT;
            case "WITHDRAW" -> WITHDRAW;
            case "CREATE" -> CREATE;
            case "DELETE" -> DELETE;
            default -> NO_OP;
        };
    }
}
