package com.banking.banking_system.ServiceTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.banking.banking_system.Entity.ATM;
import com.banking.banking_system.Entity.BankCard;
import com.banking.banking_system.Repository.ATMRepository;
import com.banking.banking_system.Repository.BankCardRepository;
import com.banking.banking_system.Repository.CreditCardRepository;
import com.banking.banking_system.Service.ATMService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

public class ATMServiceTest {

    @Mock
    private ATMRepository atmRepository;

    @Mock
    private BankCardRepository bankCardRepository;

    @Mock
    private CreditCardRepository creditCardRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ATMService atmService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test for getATM()
    @Test
    void testGetATM_Success() {
        ATM atm = new ATM();
        atm.setId(1L);
        when(atmRepository.findById(1L)).thenReturn(Optional.of(atm));

        ATM result = atmService.getATM(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(atmRepository, times(1)).findById(1L);
    }

    @Test
    void testGetATM_ATMNotFound() {
        when(atmRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> atmService.getATM(1L));
        assertEquals("ATM not found", exception.getMessage());
    }

    // Test for createATM()
    @Test
    void testCreateATM_Success() {
        boolean result = atmService.createATM("Location 1", 10000.0);

        assertTrue(result);
        verify(atmRepository, times(1)).save(any(ATM.class));
    }

    @Test
    void testCreateATM_Failure() {
        doThrow(new RuntimeException()).when(atmRepository).save(any(ATM.class));

        boolean result = atmService.createATM("Location 1", 10000.0);

        assertFalse(result);
        verify(atmRepository, times(1)).save(any(ATM.class));
    }

    // Test for deleteATM()
    @Test
    void testDeleteATM_Success() {
        boolean result = atmService.deleteATM(1L);

        assertTrue(result);
        verify(atmRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteATM_Failure() {
        doThrow(new RuntimeException()).when(atmRepository).deleteById(1L);

        boolean result = atmService.deleteATM(1L);

        assertFalse(result);
        verify(atmRepository, times(1)).deleteById(1L);
    }

    // Test for depositMoney()
    @Test
    void testDepositMoney_InactiveCard() {
        // Arrange
        BankCard bankCard = new BankCard();
        bankCard.setCardNumber("1234567890123456");
        bankCard.setBalance(5000.0);
        bankCard.setCardPassword("password");
        bankCard.setActive(false); // Card is inactive

        ATM atm = new ATM();
        atm.setId(1L);
        atm.setBalance(10000.0);

        when(bankCardRepository.findByCardNumber("1234567890123456")).thenReturn(Optional.of(bankCard));
        when(passwordEncoder.matches("password", bankCard.getCardPassword())).thenReturn(true);
        when(atmRepository.findById(1L)).thenReturn(Optional.of(atm));

        // Act
        boolean result = atmService.depositMoney(1L, "1234567890123456", "password", 2000.0);

        // Assert
        assertFalse(result); // Expecting failure since the card is inactive
        assertEquals(10000.0, atm.getBalance()); // ATM balance should remain the same
        assertEquals(5000.0, bankCard.getBalance()); // Bank card balance should remain the same
        verify(atmRepository, never()).save(any(ATM.class));
        verify(bankCardRepository, never()).save(any(BankCard.class));
    }

    @Test
    void testDepositMoney_AfterActivatingCard() {
        // Arrange
        BankCard bankCard = new BankCard();
        bankCard.setCardNumber("1234567890123456");
        bankCard.setBalance(5000.0);
        bankCard.setCardPassword("password");
        bankCard.setActive(true); // Card is now active

        ATM atm = new ATM();
        atm.setId(1L);
        atm.setBalance(10000.0);

        when(bankCardRepository.findByCardNumber("1234567890123456")).thenReturn(Optional.of(bankCard));
        when(passwordEncoder.matches("password", bankCard.getCardPassword())).thenReturn(true);
        when(atmRepository.findById(1L)).thenReturn(Optional.of(atm));

        // Act
        boolean result = atmService.depositMoney(1L, "1234567890123456", "password", 2000.0);

        // Assert
        assertTrue(result); // Expecting success since the card is now active
        assertEquals(12000.0, atm.getBalance()); // ATM balance should be updated
        assertEquals(3000.0, bankCard.getBalance()); // Bank card balance should be updated
        verify(atmRepository, times(1)).save(atm);
        verify(bankCardRepository, times(1)).save(bankCard);
    }


    @Test
    void testDepositMoney_CardNotFound() {
        when(bankCardRepository.findByCardNumber("1234567890123456")).thenReturn(Optional.empty());

        boolean result = atmService.depositMoney(1L, "1234567890123456", "password", 2000.0);

        assertFalse(result);
        verify(atmRepository, never()).save(any(ATM.class));
        verify(bankCardRepository, never()).save(any(BankCard.class));
    }

    // Test for withdrawMoney()
    @Test
    void testWithdrawMoney_Success_AfterActivatingCard() {
        // Arrange
        BankCard bankCard = new BankCard();
        bankCard.setCardNumber("1234567890123456");
        bankCard.setBalance(5000.0);
        bankCard.setCardPassword("encodedPassword");
        bankCard.setActive(true); // Ensure the card is active

        ATM atm = new ATM();
        atm.setId(1L);
        atm.setBalance(10000.0);

        when(bankCardRepository.findByCardNumber("1234567890123456")).thenReturn(Optional.of(bankCard));
        when(passwordEncoder.matches("password", bankCard.getCardPassword())).thenReturn(true);
        when(atmRepository.findById(1L)).thenReturn(Optional.of(atm));

        // Act
        boolean result = atmService.withdrawMoney(1L, "1234567890123456", "password", 2000.0);

        // Assert
        assertTrue(result); // Expecting the withdrawal to succeed
        assertEquals(8000.0, atm.getBalance()); // ATM balance should decrease by 2000
        assertEquals(7000.0, bankCard.getBalance()); // Bank card balance should increase by 2000
        verify(atmRepository, times(1)).save(atm);
        verify(bankCardRepository, times(1)).save(bankCard);
    }

    @Test
    void testWithdrawMoney_InsufficientFunds_ActiveCard() {
        BankCard bankCard = new BankCard();
        bankCard.setCardNumber("1234567890123456");
        bankCard.setBalance(1000.0); // Insufficient funds in the bank card
        bankCard.setCardPassword("encodedPassword");
        bankCard.setActive(true); // Ensure the card is active

        ATM atm = new ATM();
        atm.setId(1L);
        atm.setBalance(1000.0);

        when(bankCardRepository.findByCardNumber("1234567890123456")).thenReturn(Optional.of(bankCard));
        when(passwordEncoder.matches("password", bankCard.getCardPassword())).thenReturn(true);
        when(atmRepository.findById(1L)).thenReturn(Optional.of(atm));

        boolean result = atmService.withdrawMoney(1L, "1234567890123456", "password", 2000.0);

        assertFalse(result);
        assertEquals(1000.0, atm.getBalance());
        assertEquals(1000.0, bankCard.getBalance());
        verify(atmRepository, never()).save(atm);
        verify(bankCardRepository, never()).save(bankCard);
    }

    @Test
    void testWithdrawMoney_InactiveCard() {
        BankCard bankCard = new BankCard();
        bankCard.setCardNumber("1234567890123456");
        bankCard.setBalance(5000.0);
        bankCard.setCardPassword("encodedPassword");
        bankCard.setActive(false);

        ATM atm = new ATM();
        atm.setId(1L);
        atm.setBalance(10000.0);

        when(bankCardRepository.findByCardNumber("1234567890123456")).thenReturn(Optional.of(bankCard));
        when(passwordEncoder.matches("password", bankCard.getCardPassword())).thenReturn(true);
        when(atmRepository.findById(1L)).thenReturn(Optional.of(atm));

        boolean result = atmService.withdrawMoney(1L, "1234567890123456", "password", 2000.0);

        assertFalse(result); // Expecting failure since the card is inactive
        assertEquals(10000.0, atm.getBalance()); // ATM balance should remain unchanged
        assertEquals(5000.0, bankCard.getBalance()); // Bank card balance should remain unchanged
        verify(atmRepository, never()).save(any(ATM.class));
        verify(bankCardRepository, never()).save(any(BankCard.class));
    }

    @Test
    void testGetBalance_Success_ActiveCard() {
        BankCard bankCard = new BankCard();
        bankCard.setCardNumber("1234567890123456");
        bankCard.setBalance(5000.0);
        bankCard.setCardPassword("encodedPassword");
        bankCard.setIsActive(true);

        when(bankCardRepository.findByCardNumber("1234567890123456")).thenReturn(Optional.of(bankCard));
        when(passwordEncoder.matches("password", bankCard.getCardPassword())).thenReturn(true);

        double balance = atmService.getBalance("1234567890123456", "password");

        assertEquals(5000.0, balance);
    }

    @Test
    void testGetBalance_Fail_InActiveCard() {
        // Arrange
        BankCard bankCard = new BankCard();
        bankCard.setCardNumber("1234567890123456");
        bankCard.setBalance(5000.0);
        bankCard.setCardPassword("encodedPassword");
        bankCard.setActive(false); // Ensure the card is inactive

        when(bankCardRepository.findByCardNumber("1234567890123456")).thenReturn(Optional.of(bankCard));
        when(passwordEncoder.matches("password", bankCard.getCardPassword())).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            atmService.getBalance("1234567890123456", "password");
        });

        assertEquals("Card is not active or password is incorrect", exception.getMessage());
    }


    @Test
    void testGetBalance_CardNotFound() {
        when(bankCardRepository.findByCardNumber("1234567890123456")).thenReturn(Optional.empty());

        double balance = atmService.getBalance("1234567890123456", "password");

        assertEquals(-1, balance);
    }

    @Test
    void testGetBalance_IncorrectPassword() {
        BankCard bankCard = new BankCard();
        bankCard.setCardNumber("1234567890123456");
        bankCard.setBalance(5000.0);
        bankCard.setCardPassword("encodedPassword");

        when(bankCardRepository.findByCardNumber("1234567890123456")).thenReturn(Optional.of(bankCard));
        when(passwordEncoder.matches("wrongPassword", bankCard.getCardPassword())).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> atmService.getBalance("1234567890123456", "wrongPassword"));
        assertEquals("Card is not active or password is incorrect", exception.getMessage());
    }
}

