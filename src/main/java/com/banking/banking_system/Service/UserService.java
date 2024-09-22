package com.banking.banking_system.Service;

import com.banking.banking_system.DTO.UserDTO;
import com.banking.banking_system.DTO.UserMapper;
import com.banking.banking_system.Entity.User;
import com.banking.banking_system.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public boolean doesUserHaveBankCard(String tc) {
        Optional<User> optionalUser = userRepository.findByTc(tc);
        if (optionalUser.isEmpty()) {
            return false;
        }

        User user = optionalUser.get();

        return user.getBankCards().size() > 0;
    }

    public boolean doesUserHaveCreditCard(String tc) {
        Optional<User> optionalUser = userRepository.findByTc(tc);
        if (optionalUser.isEmpty()) {
            return false;
        }

        User user = optionalUser.get();

        return user.getCreditCards().size() > 0;
    }

    public UserDTO getUser(String tc) {
        Optional<User> optionalUser = userRepository.findByTc(tc);
        if (optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();

        return UserMapper.toDTO(user);
    }

    public boolean updateUserAge(String tc, int age) {
        Optional<User> optionalUser = userRepository.findByTc(tc);
        if (optionalUser.isEmpty()) {
            return false;
        }

        User user = optionalUser.get();
        user.setAge(age);

        userRepository.save(user);
        return true;
    }

}
