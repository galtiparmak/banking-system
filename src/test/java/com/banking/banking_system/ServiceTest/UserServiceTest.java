package com.banking.banking_system.ServiceTest;

import com.banking.banking_system.Entity.BankCard;
import com.banking.banking_system.Entity.CreditCard;
import com.banking.banking_system.Entity.User;
import com.banking.banking_system.Repository.UserRepository;
import com.banking.banking_system.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUserHasBankCard() {
        // Mock User with a bank card
        User user = new User();
        List<BankCard> bankCards = new ArrayList<>();
        bankCards.add(new BankCard());
        user.setBankCards(bankCards);

        when(userRepository.findByTc("12345678901")).thenReturn(Optional.of(user));

        boolean result = userService.doesUserHaveBankCard();
        assertTrue(result, "User should have a bank card");
    }

    @Test
    void testUserHasNoBankCard() {
        User user = new User();
        user.setBankCards(new ArrayList<>());

        when(userRepository.findByTc("12345678902")).thenReturn(Optional.of(user));

        boolean result = userService.doesUserHaveBankCard();
        assertFalse(result, "User should not have a bank card");
    }

    @Test
    void testUserNotFound() {
        when(userRepository.findByTc("12345678903")).thenReturn(Optional.empty());

        boolean result = userService.doesUserHaveBankCard();
        assertFalse(result, "User should not be found");
    }

    @Test
    void testUserHasCreditCard() {
        User user = new User();
        List<CreditCard> creditCards = new ArrayList<>();
        creditCards.add(new CreditCard());
        user.setCreditCards(creditCards);

        when(userRepository.findByTc("12345678901")).thenReturn(Optional.of(user));

        boolean result = userService.doesUserHaveCreditCard();
        assertTrue(result, "User should have a credit card");
    }

    @Test
    void testUserHasNoCreditCard() {
        User user = new User();
        user.setCreditCards(new ArrayList<>());

        when(userRepository.findByTc("12345678902")).thenReturn(Optional.of(user));

        boolean result = userService.doesUserHaveCreditCard();
        assertFalse(result, "User should not have a credit card");
    }

    @Test
    void testGetUser() {
        User user = new User();
        user.setTc("12345678901");

        when(userRepository.findByTc("12345678901")).thenReturn(Optional.of(user));

        boolean result = userService.getUser() != null;

        assertTrue(result, "User should be found");
    }

    @Test
    void testUpdateUserAge() {
        User user = new User();
        user.setTc("12345678901");
        user.setAge(20);

        when(userRepository.findByTc("12345678901")).thenReturn(Optional.of(user));

        boolean result = userService.updateUserAge(21);

        assertTrue(result, "User should be updated");
    }

    @Test
    void testUpdateUserAgeUserNotFound() {
        when(userRepository.findByTc("12345678901")).thenReturn(Optional.empty());

        boolean result = userService.updateUserAge(21);

        assertFalse(result, "User should not be found");
    }

}
