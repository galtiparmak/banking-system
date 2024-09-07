package com.banking.banking_system.DTO;

import com.banking.banking_system.Entity.User;

public class UserMapper {
    public static UserDTO toDTO(User user) {
        return new UserDTO(
                user.getTC(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getPassword(),
                user.getAge(),
                user.getRole()
        );
    }

    public static User toUser(UserDTO userDTO) {
        return User.builder()
                .TC(userDTO.getTC())
                .name(userDTO.getName())
                .surname(userDTO.getSurname())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .age(userDTO.getAge())
                .role(userDTO.getRole())
                .build();
    }
}
