package com.banking.banking_system.ServiceTest;

import com.banking.banking_system.Entity.*;
import com.banking.banking_system.Repository.*;
import com.banking.banking_system.Service.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MailServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserPasswordResetTokenRepository userPasswordResetTokenRepository;
    @Mock
    private CreditCardRepository creditCardRepository;
    @Mock
    private BankCardRepository bankCardRepository;
    @Mock
    private CardPasswordResetTokenRepository cardPasswordResetTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private MailService mailService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePasswordResetTokenForUser() {
        User user = new User();
        String token = "token";
        Date expiryDate = mailService.calculateExpiryDate(24);

        UserPasswordResetToken expectedToken = new UserPasswordResetToken(token, user, expiryDate);

        when(userPasswordResetTokenRepository.save(expectedToken)).thenReturn(expectedToken);

        mailService.createPasswordResetTokenForUser(user, token);

        UserPasswordResetToken actualToken = new UserPasswordResetToken(token, user, expiryDate);
        when(userPasswordResetTokenRepository.save(actualToken)).thenReturn(expectedToken);

        assertNotNull(actualToken, "The created token should not be null");
        assertEquals(expectedToken.getToken(), actualToken.getToken(), "The token should match");
        assertEquals(expectedToken.getUser(), actualToken.getUser(), "The user should match");
        assertEquals(expectedToken.getExpiryDate(), actualToken.getExpiryDate(), "The expiry date should match");
    }

    @Test
    void testCreatePasswordResetTokenForCreditCard() {
        CreditCard creditCard = new CreditCard(); // Mock or create a CreditCard object
        String token = "testToken123";
        Date expectedExpiryDate = mailService.calculateExpiryDate(24); // Calculate expiry date

        CardPasswordResetToken expectedToken = new CardPasswordResetToken(token, creditCard, expectedExpiryDate);

        when(cardPasswordResetTokenRepository.save(expectedToken)).thenReturn(expectedToken);

        mailService.createPasswordResetTokenForCard(creditCard, null, token);

        CardPasswordResetToken actualToken = new CardPasswordResetToken(token, creditCard, expectedExpiryDate);
        when(cardPasswordResetTokenRepository.save(actualToken)).thenReturn(expectedToken);

        assertNotNull(actualToken, "The created token should not be null");
        assertEquals(expectedToken.getToken(), actualToken.getToken(), "The token should match");
        assertEquals(expectedToken.getCreditCard(), actualToken.getCreditCard(), "The CreditCard should match");
        assertEquals(expectedToken.getExpiryDate(), actualToken.getExpiryDate(), "The expiry date should match");
    }

    @Test
    void testCreatePasswordResetTokenForBankCard() {
        BankCard bankCard = new BankCard(); // Mock or create a BankCard object
        String token = "testToken456";
        Date expectedExpiryDate = mailService.calculateExpiryDate(24); // Calculate expiry date

        CardPasswordResetToken expectedToken = new CardPasswordResetToken(token, bankCard, expectedExpiryDate);

        when(cardPasswordResetTokenRepository.save(expectedToken)).thenReturn(expectedToken);

        mailService.createPasswordResetTokenForCard(null, bankCard, token);

        CardPasswordResetToken actualToken = new CardPasswordResetToken(token, bankCard, expectedExpiryDate);
        when(cardPasswordResetTokenRepository.save(actualToken)).thenReturn(expectedToken);

        assertNotNull(actualToken, "The created token should not be null");
        assertEquals(expectedToken.getToken(), actualToken.getToken(), "The token should match");
        assertEquals(expectedToken.getBankCard(), actualToken.getBankCard(), "The BankCard should match");
        assertEquals(expectedToken.getExpiryDate(), actualToken.getExpiryDate(), "The expiry date should match");
    }

    @Test
    void testResetPasswordRequest_UserNotFound() {
        // Mock user not found by TC
        when(userRepository.findByTc("12345678901")).thenReturn(Optional.empty());

        // Act
        String result = mailService.resetPasswordRequest("12345678901");

        // Assert
        assertEquals("User not found", result);
    }

    @Test
    void testResetPasswordRequest_UserFound() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        when(userRepository.findByTc("12345678901")).thenReturn(Optional.of(user));

        // Act
        String result = mailService.resetPasswordRequest("12345678901");

        // Assert
        assertEquals("Email sent", result);
        verify(userPasswordResetTokenRepository, times(1)).save(any(UserPasswordResetToken.class));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testResetCardPasswordRequest_CardNotFound() {
        // Mock no card found by card number
        when(creditCardRepository.findByCardNumber("1111222233334444")).thenReturn(Optional.empty());
        when(bankCardRepository.findByCardNumber("1111222233334444")).thenReturn(Optional.empty());

        // Act
        boolean result = mailService.resetCardPasswordRequest("1111222233334444");

        // Assert
        assertFalse(result);
    }

    @Test
    void testResetCardPasswordRequest_CreditCardFound() {
        // Arrange
        CreditCard creditCard = new CreditCard();
        User user = new User();
        user.setEmail("test@example.com");
        creditCard.setUser(user);
        when(creditCardRepository.findByCardNumber("1111222233334444")).thenReturn(Optional.of(creditCard));

        // Act
        boolean result = mailService.resetCardPasswordRequest("1111222233334444");

        // Assert
        assertTrue(result);
        verify(cardPasswordResetTokenRepository, times(1)).save(any(CardPasswordResetToken.class));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testResetCardPasswordRequest_BankCardFound() {
        // Arrange
        BankCard bankCard = new BankCard();
        User user = new User();
        user.setEmail("test@example.com");
        bankCard.setUser(user);
        when(bankCardRepository.findByCardNumber("5555666677778888")).thenReturn(Optional.of(bankCard));

        // Act
        boolean result = mailService.resetCardPasswordRequest("5555666677778888");

        // Assert
        assertTrue(result);
        verify(cardPasswordResetTokenRepository, times(1)).save(any(CardPasswordResetToken.class));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testResetPassword_InvalidToken() {
        // Mock invalid token
        when(userPasswordResetTokenRepository.findByToken("invalidToken")).thenReturn(Optional.empty());

        // Act
        String result = mailService.resetPassword("invalidToken", "newPassword");

        // Assert
        assertEquals("Invalid token", result);
    }

    @Test
    void testResetPassword_TokenExpired() {
        // Arrange
        UserPasswordResetToken token = new UserPasswordResetToken();
        token.setExpiryDate(new Date(System.currentTimeMillis() - 1000)); // expired
        when(userPasswordResetTokenRepository.findByToken("expiredToken")).thenReturn(Optional.of(token));

        // Act
        String result = mailService.resetPassword("expiredToken", "newPassword");

        // Assert
        assertEquals("Token expired", result);
        verify(userPasswordResetTokenRepository, times(1)).delete(token);
    }

    @Test
    void testResetPassword_ValidToken() {
        // Arrange
        User user = new User();
        UserPasswordResetToken token = new UserPasswordResetToken();
        token.setExpiryDate(new Date(System.currentTimeMillis() + 10000)); // not expired
        token.setUser(user);
        when(userPasswordResetTokenRepository.findByToken("validToken")).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");

        // Act
        String result = mailService.resetPassword("validToken", "newPassword");

        // Assert
        assertEquals("Password reset", result);
        assertEquals("encodedPassword", user.getPassword());
        verify(userRepository, times(1)).save(user);
        verify(userPasswordResetTokenRepository, times(1)).delete(token);
    }

    @Test
    void testResetCardPassword_InvalidToken() {
        // Mock invalid token
        when(cardPasswordResetTokenRepository.findByToken("invalidToken")).thenReturn(Optional.empty());

        // Act
        String result = mailService.resetCardPassword("invalidToken", "newPassword");

        // Assert
        assertEquals("Invalid token", result);
    }

    @Test
    void testResetCardPassword_TokenExpired() {
        // Arrange
        CardPasswordResetToken token = new CardPasswordResetToken();
        token.setExpiryDate(new Date(System.currentTimeMillis() - 1000)); // expired
        when(cardPasswordResetTokenRepository.findByToken("expiredToken")).thenReturn(Optional.of(token));

        // Act
        String result = mailService.resetCardPassword("expiredToken", "newPassword");

        // Assert
        assertEquals("Token expired", result);
        verify(cardPasswordResetTokenRepository, times(1)).delete(token);
    }

    @Test
    void testResetCardPassword_ValidToken_CreditCard() {
        // Arrange
        CreditCard creditCard = new CreditCard();
        CardPasswordResetToken token = new CardPasswordResetToken();
        token.setExpiryDate(new Date(System.currentTimeMillis() + 10000)); // not expired
        token.setCreditCard(creditCard);
        when(cardPasswordResetTokenRepository.findByToken("validToken")).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");

        // Act
        String result = mailService.resetCardPassword("validToken", "newPassword");

        // Assert
        assertEquals("Card password has been successfully reset", result);
        assertEquals("encodedPassword", creditCard.getPassword());
        verify(creditCardRepository, times(1)).save(creditCard);
        verify(cardPasswordResetTokenRepository, times(1)).delete(token);
    }

    @Test
    void testResetCardPassword_ValidToken_BankCard() {
        // Arrange
        BankCard bankCard = new BankCard();
        CardPasswordResetToken token = new CardPasswordResetToken();
        token.setExpiryDate(new Date(System.currentTimeMillis() + 10000)); // not expired
        token.setBankCard(bankCard);
        when(cardPasswordResetTokenRepository.findByToken("validToken")).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");

        // Act
        String result = mailService.resetCardPassword("validToken", "newPassword");

        // Assert
        assertEquals("Card password has been successfully reset", result);
        assertEquals("encodedPassword", bankCard.getPassword());
        verify(bankCardRepository, times(1)).save(bankCard);
        verify(cardPasswordResetTokenRepository, times(1)).delete(token);
    }

}
