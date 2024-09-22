package com.banking.banking_system.Controller;

import com.banking.banking_system.Authentication.AuthenticationResponse;
import com.banking.banking_system.Authentication.RegisterEmployeeRequest;
import com.banking.banking_system.DTO.EmployeeDTO;
import com.banking.banking_system.Entity.Admin;
import com.banking.banking_system.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }


    @GetMapping("/admin")
    public ResponseEntity<Admin> getAdmin(@RequestParam Long id) {
        return ResponseEntity.ok(adminService.getAdmin(id));
    } // http://localhost:8080/api/admin/admin?id=...

    @GetMapping("/admins")
    public ResponseEntity<List<Admin>> getAllAdmins() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    } // http://localhost:8080/api/admin/admins

    @PostMapping("/create-admin")
    public ResponseEntity<String> createAdmin(@RequestParam String username, @RequestParam String password) {
        if (adminService.createAdmin(username, password)) {
            return ResponseEntity.ok("Admin created successfully");
        } else {
            return ResponseEntity.badRequest().body("Admin could not be created");
        }
    } // http://localhost:8080/api/admin/create-admin?username=...&password=...

    @DeleteMapping("/delete-admin")
    public ResponseEntity<String> deleteAdmin(@RequestParam Long id) {
        if (adminService.deleteAdmin(id)) {
            return ResponseEntity.ok("Admin deleted successfully");
        } else {
            return ResponseEntity.badRequest().body("Admin could not be deleted");
        }
    } // http://localhost:8080/api/admin/delete-admin?id=...

    @GetMapping("/employee")
    public ResponseEntity<EmployeeDTO> getEmployee(@RequestParam String TC) {
        return ResponseEntity.ok(adminService.getEmployee(TC));
    } // http://localhost:8080/api/admin/employee?TC=...

    @PostMapping("/create-employee")
    public ResponseEntity<AuthenticationResponse> registerEmployee(@RequestBody RegisterEmployeeRequest registerEmployeeRequest) {
        return ResponseEntity.ok(adminService.createEmployee(registerEmployeeRequest));
    } // http://localhost:8080/api/admin/create-employee

    @DeleteMapping("/delete-employee")
    public ResponseEntity<String> deleteEmployee(@RequestParam Long id) {
        if (adminService.deleteEmployee(id)) {
            return ResponseEntity.ok("Employee deleted successfully");
        } else {
            return ResponseEntity.badRequest().body("Employee could not be deleted");
        }
    } // http://localhost:8080/api/admin/delete-employee?id=...

}
