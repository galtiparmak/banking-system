package com.banking.banking_system.Service;

import com.banking.banking_system.Authentication.AuthenticationResponse;
import com.banking.banking_system.Authentication.RegisterEmployeeRequest;
import com.banking.banking_system.DTO.EmployeeDTO;
import com.banking.banking_system.DTO.EmployeeMapper;
import com.banking.banking_system.Entity.Admin;
import com.banking.banking_system.Entity.Employee;
import com.banking.banking_system.Entity.Role;
import com.banking.banking_system.Repository.AdminRepository;
import com.banking.banking_system.Repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    private final AdminRepository adminRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    @Autowired
    public AdminService(AdminRepository adminRepository, EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.adminRepository = adminRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public Admin getAdmin(Long id) {
        Optional<Admin> optionalAdmin = adminRepository.findById(id);
        if (optionalAdmin.isEmpty()) {
            throw new RuntimeException("Admin not found");
        }
        return optionalAdmin.get();
    }

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    public boolean createAdmin(String username, String password) {
        try {
            Admin admin = Admin.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .role(Role.ADMIN)
                    .build();
            adminRepository.save(admin);
            return true;
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean deleteAdmin(Long id) {
        try {
            adminRepository.deleteById(id);
            return true;
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public EmployeeDTO getEmployee(String tc) {
        Optional<Employee> optionalEmployee = employeeRepository.findByTc(tc);
        if (optionalEmployee.isEmpty()) {
            throw new RuntimeException("Employee not found");
        }
        Employee employee = optionalEmployee.get();

        return EmployeeMapper.toDTO(employee);
    }

    public AuthenticationResponse createEmployee(RegisterEmployeeRequest request) {
        var employee = Employee
                .builder()
                .tc(request.getTc())
                .name(request.getName())
                .surname(request.getSurname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .age(Integer.parseInt(request.getAge()))
                .role(Role.EMPLOYEE)
                .position(request.getPosition())
                .department(request.getDepartment())
                .phoneNumber(request.getPhoneNumber())
                .createdAt(new java.util.Date())
                .build();

        employeeRepository.save(employee);

        var token = jwtService.generateToken(employee);

        return AuthenticationResponse
                .builder()
                .token(token)
                .build();
    }

    public boolean deleteEmployee(Long id) {
        try {
            employeeRepository.deleteById(id);
            return true;
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

}
