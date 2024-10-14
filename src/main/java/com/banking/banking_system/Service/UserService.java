package com.banking.banking_system.Service;

import com.banking.banking_system.DTO.UserDTO;
import com.banking.banking_system.DTO.UserMapper;
import com.banking.banking_system.Entity.User;
import com.banking.banking_system.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Autowired
    public UserService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }


    public boolean doesUserHaveBankCard() {
        String tc = getTcFromToken();

        Optional<User> optionalUser = userRepository.findByTc(tc);
        if (optionalUser.isEmpty()) {
            return false;
        }

        User user = optionalUser.get();

        return user.getBankCards().size() > 0;
    }

    public boolean doesUserHaveCreditCard() {
        String tc = getTcFromToken();

        Optional<User> optionalUser = userRepository.findByTc(tc);
        if (optionalUser.isEmpty()) {
            return false;
        }
        User user = optionalUser.get();

        return user.getCreditCards().size() > 0;
    }

    public UserDTO getUser() {
        String tc = getTcFromToken();

        Optional<User> optionalUser = userRepository.findByTc(tc);
        if (optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();

        return UserMapper.toDTO(user);
    }

    public boolean updateUserAge(int age) {
        String tc = getTcFromToken();

        Optional<User> optionalUser = userRepository.findByTc(tc);
        if (optionalUser.isEmpty()) {
            return false;
        }

        User user = optionalUser.get();
        user.setAge(age);

        userRepository.save(user);
        return true;
    }

    private static String getTcFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

}
