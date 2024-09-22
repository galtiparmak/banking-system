package com.banking.banking_system.Configuration;

import com.banking.banking_system.Entity.Admin;
import com.banking.banking_system.Entity.Employee;
import com.banking.banking_system.Entity.User;
import com.banking.banking_system.Repository.AdminRepository;
import com.banking.banking_system.Repository.EmployeeRepository;
import com.banking.banking_system.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final AdminRepository adminRepository;


    @Bean
    public UserDetailsService userDetailsService() {
        return identifier -> {
            Optional<User> optionalUser = userRepository.findByTc(identifier);
            if (optionalUser.isPresent()) {
                return optionalUser.get();
            }

            Optional<Employee> optionalEmployee = employeeRepository.findByTc(identifier);
            if (optionalEmployee.isPresent()) {
                return optionalEmployee.get();
            }

            Optional<Admin> optionalAdmin = adminRepository.findByUsername(identifier);
            if (optionalAdmin.isPresent()) {
                return optionalAdmin.get();
            }

            // If none found, throw exception
            throw new UsernameNotFoundException("User, Employee, or Admin not found");
        };
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
