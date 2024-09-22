package com.banking.banking_system.ServiceTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import com.banking.banking_system.DTO.EmployeeDTO;
import com.banking.banking_system.DTO.UserDTO;
import com.banking.banking_system.Entity.*;
import com.banking.banking_system.Repository.*;
import com.banking.banking_system.Service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CardRequestRepository cardRequestRepository;

    @Mock
    private CardPasswordResetTokenRepository cardPasswordResetTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CreditCardRepository creditCardRepository;

    @Mock
    private BankCardRepository bankCardRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetEmployee_EmployeeExists() {
        Employee employee = new Employee();
        employee.setTc("12345678901");
        when(employeeRepository.findByTc("12345678901")).thenReturn(Optional.of(employee));

        EmployeeDTO result = employeeService.getEmployee("12345678901");

        assertNotNull(result);
        verify(employeeRepository, times(1)).findByTc("12345678901");
    }

    @Test
    void testGetEmployee_EmployeeNotFound() {
        when(employeeRepository.findByTc("12345678901")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            employeeService.getEmployee("12345678901");
        });

        assertEquals("Employee not found", exception.getMessage());
    }

    @Test
    void testUpdateEmployeePosition_EmployeeExists() {
        Employee employee = new Employee();
        employee.setTc("12345678901");
        employee.setPosition("Developer");

        when(employeeRepository.findByTc("12345678901")).thenReturn(Optional.of(employee));

        employeeService.updateEmployeePosition("12345678901", "Manager");

        verify(employeeRepository, times(1)).save(employee);
        assertEquals("Manager", employee.getPosition());
    }

    @Test
    void testUpdateEmployeePosition_EmployeeNotFound() {
        when(employeeRepository.findByTc("12345678901")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            employeeService.updateEmployeePosition("12345678901", "Manager");
        });

        assertEquals("Employee not found", exception.getMessage());
    }

    @Test
    void testApproveCreditCardRequest_Success() {
        CardRequest cardRequest = new CardRequest();
        cardRequest.setUserTc("12345678901");
        User user = new User();

        when(cardRequestRepository.findById(1L)).thenReturn(Optional.of(cardRequest));
        when(userRepository.findByTc("12345678901")).thenReturn(Optional.of(user));

        boolean result = employeeService.approveCreditCardRequest(1L, 5000);

        assertTrue(result);
        verify(creditCardRepository, times(1)).save(any(CreditCard.class));
    }

    @Test
    void testApproveCreditCardRequest_Failure_CreditLimitNegative() {
        boolean result = employeeService.approveCreditCardRequest(1L, -5000);

        assertFalse(result);
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void testApproveBankCardRequest_Success() {
        CardRequest cardRequest = new CardRequest();
        cardRequest.setUserTc("12345678901");
        User user = new User();

        when(cardRequestRepository.findById(1L)).thenReturn(Optional.of(cardRequest));
        when(userRepository.findByTc("12345678901")).thenReturn(Optional.of(user));

        boolean result = employeeService.approveBankCardRequest(1L);

        assertTrue(result);
        verify(bankCardRepository, times(1)).save(any(BankCard.class));
    }

    @Test
    void testApproveBankCardRequest_Failure_CardRequestNotFound() {
        when(cardRequestRepository.findById(1L)).thenReturn(Optional.empty());

        boolean result = employeeService.approveBankCardRequest(1L);

        assertFalse(result);
        verify(bankCardRepository, never()).save(any(BankCard.class));
    }

    @Test
    void testGetAllUsers() {
        List<User> users = new ArrayList<>();
        users.add(new User());

        when(userRepository.findAll()).thenReturn(users);

        List<UserDTO> result = employeeService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetAllCardRequests() {
        List<CardRequest> cardRequests = Arrays.asList(new CardRequest());

        when(cardRequestRepository.findAll()).thenReturn(cardRequests);

        List<CardRequest> result = employeeService.getAllCardRequests();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(cardRequestRepository, times(1)).findAll();
    }
}

