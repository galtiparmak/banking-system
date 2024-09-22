package com.banking.banking_system.DTO;

import com.banking.banking_system.Entity.Employee;

public class EmployeeMapper {
    public static EmployeeDTO toDTO(Employee employee) {
        return new EmployeeDTO(
                employee.getName(),
                employee.getSurname(),
                employee.getEmail(),
                employee.getPosition(),
                employee.getTc(),
                employee.getAge(),
                employee.getDepartment(),
                employee.getPhoneNumber(),
                employee.getRole()
        );
    }

    public static Employee toEmployee(EmployeeDTO employeeDTO) {
        return Employee.builder()
                .name(employeeDTO.getName())
                .surname(employeeDTO.getSurname())
                .email(employeeDTO.getEmail())
                .position(employeeDTO.getPosition())
                .tc(employeeDTO.getTc())
                .age(employeeDTO.getAge())
                .department(employeeDTO.getDepartment())
                .phoneNumber(employeeDTO.getPhoneNumber())
                .role(employeeDTO.getRole())
                .build();
    }
}
