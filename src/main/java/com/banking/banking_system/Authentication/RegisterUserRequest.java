package com.banking.banking_system.Authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserRequest {
    private String TC;
    private String name;
    private String surname;
    private String email;
    private String password;
    private int age;
}
