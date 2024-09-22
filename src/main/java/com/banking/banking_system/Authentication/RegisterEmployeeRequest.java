package com.banking.banking_system.Authentication;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterEmployeeRequest {
    @JsonProperty("tc")
    private String tc;
    private String name;
    private String surname;
    private String email;
    private String password;
    private String age;
    private String role;
    private String position;
    private String department;
    private String phoneNumber;
}
