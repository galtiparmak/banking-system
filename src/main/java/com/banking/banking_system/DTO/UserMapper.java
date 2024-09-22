package com.banking.banking_system.DTO;

import com.banking.banking_system.Entity.User;

public class UserMapper {
    public static UserDTO toDTO(User user) {
        return new UserDTO(
                user.getTc(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getAge(),
                user.getRole()
        );
    }

    public static User toUser(UserDTO userDTO) {
        return User.builder()
                .tc(userDTO.getTc())
                .name(userDTO.getName())
                .surname(userDTO.getSurname())
                .email(userDTO.getEmail())
                .age(userDTO.getAge())
                .role(userDTO.getRole())
                .build();
    }
}
