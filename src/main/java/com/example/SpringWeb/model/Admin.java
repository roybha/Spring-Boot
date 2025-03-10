package com.example.SpringWeb.model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.stereotype.Component;

@Component
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "admins")
public class Admin extends AbstractEntity{
    @Column(unique=true,nullable = false,length = 100)
    private String username;

    @Column(nullable = false,length = 100)
    private String password;
}
