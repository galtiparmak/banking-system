package com.banking.banking_system.DTO;

import com.banking.banking_system.Entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String tc;
    private String name;
    private String surname;
    private String email;
    private int age;
    private Role role;
}
