package com.banking.banking_system.DTO;

import com.banking.banking_system.Entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO {
    private String name;
    private String surname;
    private String email;
    private String position;
    private String tc;
    private int age;
    private String department;
    private String phoneNumber;
    private Role role;
}
