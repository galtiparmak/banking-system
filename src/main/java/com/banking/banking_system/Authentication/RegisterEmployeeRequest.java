package com.banking.banking_system.Authentication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterEmployeeRequest {
    private String TC;
    private String name;
    private String surname;
    private String email;
    private String password;
    private int age;
    private String role;
    private String position;
    private String department;
    private String phoneNumber;
}
