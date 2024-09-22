package com.banking.banking_system.ServiceTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.banking.banking_system.Authentication.AuthenticationResponse;
import com.banking.banking_system.Authentication.RegisterEmployeeRequest;
import com.banking.banking_system.DTO.EmployeeDTO;
import com.banking.banking_system.Entity.Admin;
import com.banking.banking_system.Entity.Employee;
import com.banking.banking_system.Repository.AdminRepository;
import com.banking.banking_system.Repository.EmployeeRepository;
import com.banking.banking_system.Service.AdminService;
import com.banking.banking_system.Service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

public class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test for getAdmin()
    @Test
    void testGetAdmin_Success() {
        Admin admin = new Admin();
        admin.setId(1L);
        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));

        Admin result = adminService.getAdmin(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(adminRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAdmin_AdminNotFound() {
        when(adminRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            adminService.getAdmin(1L);
        });

        assertEquals("Admin not found", exception.getMessage());
    }

    // Test for getAllAdmins()
    @Test
    void testGetAllAdmins() {
        List<Admin> admins = new ArrayList<>();
        admins.add(new Admin());
        when(adminRepository.findAll()).thenReturn(admins);

        List<Admin> result = adminService.getAllAdmins();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(adminRepository, times(1)).findAll();
    }

    // Test for createAdmin()
    @Test
    void testCreateAdmin_Success() {
        String username = "admin";
        String password = "password";
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        boolean result = adminService.createAdmin(username, password);

        assertTrue(result);
        verify(adminRepository, times(1)).save(any(Admin.class));
    }

    @Test
    void testCreateAdmin_Failure() {
        doThrow(new RuntimeException("Save failed")).when(adminRepository).save(any(Admin.class));

        boolean result = adminService.createAdmin("admin", "password");

        assertFalse(result);
        verify(adminRepository, times(1)).save(any(Admin.class));
    }

    // Test for deleteAdmin()
    @Test
    void testDeleteAdmin_Success() {
        boolean result = adminService.deleteAdmin(1L);

        assertTrue(result);
        verify(adminRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteAdmin_Failure() {
        doThrow(new RuntimeException("Delete failed")).when(adminRepository).deleteById(1L);

        boolean result = adminService.deleteAdmin(1L);

        assertFalse(result);
        verify(adminRepository, times(1)).deleteById(1L);
    }

    // Test for getAllEmployees()
    @Test
    void testGetAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee());
        when(employeeRepository.findAll()).thenReturn(employees);

        List<Employee> result = adminService.getAllEmployees();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(employeeRepository, times(1)).findAll();
    }

    // Test for getEmployee()
    @Test
    void testGetEmployee_Success() {
        Employee employee = new Employee();
        employee.setTc("12345678901");
        when(employeeRepository.findByTc("12345678901")).thenReturn(Optional.of(employee));

        EmployeeDTO result = adminService.getEmployee("12345678901");

        assertNotNull(result);
        verify(employeeRepository, times(1)).findByTc("12345678901");
    }

    @Test
    void testGetEmployee_EmployeeNotFound() {
        when(employeeRepository.findByTc("12345678901")).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            adminService.getEmployee("12345678901");
        });

        assertEquals("Employee not found", exception.getMessage());
    }

    // Test for createEmployee()
    @Test
    void testCreateEmployee_Success() {
        RegisterEmployeeRequest request = new RegisterEmployeeRequest();
        request.setTc("12345678901");
        request.setName("John");
        request.setSurname("Doe");
        request.setEmail("john@example.com");
        request.setPassword("password");
        request.setAge("30");
        request.setPosition("Developer");
        request.setDepartment("IT");
        request.setPhoneNumber("1234567890");

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(jwtService.generateToken(any(Employee.class))).thenReturn("jwtToken");

        AuthenticationResponse result = adminService.createEmployee(request);

        assertNotNull(result);
        assertEquals("jwtToken", result.getToken());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    // Test for deleteEmployee()
    @Test
    void testDeleteEmployee_Success() {
        boolean result = adminService.deleteEmployee(1L);

        assertTrue(result);
        verify(employeeRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteEmployee_Failure() {
        doThrow(new RuntimeException("Delete failed")).when(employeeRepository).deleteById(1L);

        boolean result = adminService.deleteEmployee(1L);

        assertFalse(result);
        verify(employeeRepository, times(1)).deleteById(1L);
    }
}

