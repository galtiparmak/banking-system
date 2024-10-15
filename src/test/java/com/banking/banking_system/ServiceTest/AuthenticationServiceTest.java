package com.banking.banking_system.ServiceTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.banking.banking_system.Authentication.AuthenticationRequest;
import com.banking.banking_system.Authentication.AuthenticationResponse;
import com.banking.banking_system.Authentication.RegisterUserRequest;
import com.banking.banking_system.Entity.*;
import com.banking.banking_system.Repository.AdminRepository;
import com.banking.banking_system.Repository.EmployeeRepository;
import com.banking.banking_system.Repository.UserRepository;
import com.banking.banking_system.Service.AuthenticationService;
import com.banking.banking_system.Service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test for registerUser() method
    @Test
    void testRegisterUser_Success() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setTc("12345678901");
        request.setName("John");
        request.setSurname("Doe");
        request.setEmail("john@example.com");
        request.setPassword("password");
        request.setAge(30);

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("jwtToken");

        AuthenticationResponse response = authenticationService.registerUser(request);

        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        verify(userRepository, times(1)).save(any(User.class));
    }

    // Test for createAdmin() method
    @Test
    void testCreateAdmin_Success() {
        String username = "admin";
        String password = "password";

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(jwtService.generateAccessToken(any(Admin.class))).thenReturn("jwtToken");

        AuthenticationResponse response = authenticationService.createAdmin(username, password);

        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        verify(adminRepository, times(1)).save(any(Admin.class));
    }

    // Test for authenticateAdmin() method
    @Test
    void testAuthenticateAdmin_Success() {
        String username = "admin";
        String password = "password";

        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword("encodedPassword");

        when(adminRepository.findByUsername(username)).thenReturn(Optional.of(admin));
        when(jwtService.generateAccessToken(admin)).thenReturn("jwtToken");

        AuthenticationResponse response = authenticationService.authenticateAdmin(username, password);

        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testAuthenticateAdmin_AdminNotFound() {
        String username = "admin";
        String password = "password";

        when(adminRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            authenticationService.authenticateAdmin(username, password);
        });
    }

    // Test for authenticateUser() method
    @Test
    void testAuthenticateUser_Success() {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setTc("12345678901");
        request.setPassword("password");

        User user = new User();
        user.setTc("12345678901");
        user.setPassword("encodedPassword");

        when(userRepository.findByTc("12345678901")).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("jwtToken");

        AuthenticationResponse response = authenticationService.authenticateUser(request);

        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testAuthenticateUser_UserNotFound() {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setTc("12345678901");
        request.setPassword("password");

        when(userRepository.findByTc("12345678901")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            authenticationService.authenticateUser(request);
        });
    }

    // Test for authenticateEmployee() method
    @Test
    void testAuthenticateEmployee_Success() {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setTc("98765432101");
        request.setPassword("password");

        Employee employee = new Employee();
        employee.setTc("98765432101");
        employee.setPassword("encodedPassword");

        when(employeeRepository.findByTc("98765432101")).thenReturn(Optional.of(employee));
        when(jwtService.generateAccessToken(employee)).thenReturn("jwtToken");

        AuthenticationResponse response = authenticationService.authenticateEmployee(request);

        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testAuthenticateEmployee_EmployeeNotFound() {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setTc("98765432101");
        request.setPassword("password");

        when(employeeRepository.findByTc("98765432101")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            authenticationService.authenticateEmployee(request);
        });
    }
}

