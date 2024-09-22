package com.banking.banking_system.Controller;

import com.banking.banking_system.Entity.ATM;
import com.banking.banking_system.Service.ATMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/atm/employee")
@PreAuthorize("hasRole('EMPLOYEE')")
public class ATMEmployeeController {

    private final ATMService atmService;
    @Autowired
    public ATMEmployeeController(ATMService atmService) {
        this.atmService = atmService;
    }
    @GetMapping("/getATM")
    public ResponseEntity<ATM> getATM(@RequestParam Long id) {
        return ResponseEntity.ok(atmService.getATM(id));
    } // http://localhost:8080/atm/employee/getATM?id=...

    @GetMapping("/getATMs")
    public ResponseEntity<List<ATM>> getATMs() {
        return ResponseEntity.ok(atmService.getATMs());
    } // http://localhost:8080/atm/employee/getATMs

    @PostMapping("/createATM")
    public ResponseEntity<String> createATM(@RequestParam String location, @RequestParam double balance) {
        if (atmService.createATM(location, balance)) {
            return ResponseEntity.ok("ATM created successfully");
        }
        return ResponseEntity.badRequest().body("ATM creation failed");
    } // http://localhost:8080/atm/employee/createATM?location=...

    @DeleteMapping("/deleteATM")
    public ResponseEntity<String> deleteATM(@RequestParam Long id) {
        if (atmService.deleteATM(id)) {
            return ResponseEntity.ok("ATM deleted successfully");
        }
        return ResponseEntity.badRequest().body("ATM deletion failed");
    } // http://localhost:8080/atm/employee/deleteATM?id=...

}
