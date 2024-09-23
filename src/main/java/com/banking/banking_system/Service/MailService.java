package com.banking.banking_system.Service;

import com.banking.banking_system.Entity.*;
import com.banking.banking_system.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class MailService {

    private final UserRepository userRepository;
    private final UserPasswordResetTokenRepository userPasswordResetTokenRepository;
    private final CreditCardRepository creditCardRepository;
    private final BankCardRepository bankCardRepository;
    private final CardPasswordResetTokenRepository cardPasswordResetTokenRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MailService(
            UserRepository userRepository,
            UserPasswordResetTokenRepository userPasswordResetTokenRepository,
            CreditCardRepository creditCardRepository,
            BankCardRepository bankCardRepository,
            CardPasswordResetTokenRepository cardPasswordResetTokenRepository,
            JavaMailSender mailSender,
            PasswordEncoder passwordEncoder)
    {
        this.userRepository = userRepository;
        this.userPasswordResetTokenRepository = userPasswordResetTokenRepository;
        this.creditCardRepository = creditCardRepository;
        this.bankCardRepository = bankCardRepository;
        this.cardPasswordResetTokenRepository = cardPasswordResetTokenRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }
    public void createPasswordResetTokenForUser(User user, String token) {
        UserPasswordResetToken myToken = new UserPasswordResetToken(token, user, calculateExpiryDate(24));
        userPasswordResetTokenRepository.save(myToken);
    }

    public void createPasswordResetTokenForCard(CreditCard creditCard, BankCard bankCard, String token) {
        CardPasswordResetToken myToken;

        if (creditCard != null) {
            myToken = new CardPasswordResetToken(token, creditCard, calculateExpiryDate(24));
        } else {
            myToken = new CardPasswordResetToken(token, bankCard, calculateExpiryDate(24));
        }

        cardPasswordResetTokenRepository.save(myToken);
    }

    public Date calculateExpiryDate(int expiryTimeInHours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.HOUR, expiryTimeInHours);
        return new Date(calendar.getTime().getTime());
    }

    public void sendPasswordResetEmail(String email, String resetToken, String subject, String resetUrlPath) {
        String resetUrl = "http://localhost:8080" + resetUrlPath + "?token=" + resetToken;
        String message = "To reset your password, copy the link below:\n" + resetUrl;

        SimpleMailMessage emailMessage = new SimpleMailMessage();
        emailMessage.setTo(email);
        emailMessage.setFrom("****");
        emailMessage.setSubject(subject);
        emailMessage.setText(message);

        mailSender.send(emailMessage);
    }

    public void sendAccountPasswordResetEmail(String email, String resetToken) {
        String subject = "Account Password Reset Request";
        String resetUrlPath = "/api/user/reset-account-password";
        sendPasswordResetEmail(email, resetToken, subject, resetUrlPath);
    }

    public void sendCardPasswordResetEmail(String email, String resetToken) {
        String subject = "Card Password Reset Request";
        String resetUrlPath = "/api/user/reset-card-password";
        sendPasswordResetEmail(email, resetToken, subject, resetUrlPath);
    }


    public String resetPasswordRequest(String tc) {
        Optional<User> optionalUser = userRepository.findByTc(tc);
        if (optionalUser.isEmpty()) {
            return "User not found";
        }

        User user = optionalUser.get();
        String email = user.getEmail();

        String token = UUID.randomUUID().toString();
        createPasswordResetTokenForUser(user, token);
        sendAccountPasswordResetEmail(email, token);

        return "Email sent";
    }

    public boolean resetCardPasswordRequest(String cardNumber) {
        Optional<CreditCard> optionalCreditCard = creditCardRepository.findByCardNumber(cardNumber);
        Optional<BankCard> optionalBankCard = bankCardRepository.findByCardNumber(cardNumber);

        if (optionalCreditCard.isEmpty() && optionalBankCard.isEmpty()) {
            return false;
        }

        String email;
        CreditCard creditCard = optionalCreditCard.orElse(null);
        BankCard bankCard = optionalBankCard.orElse(null);

        if (creditCard != null) {
            email = creditCard.getUser().getEmail();
        } else {
            email = bankCard.getUser().getEmail();
        }

        String token = UUID.randomUUID().toString();
        createPasswordResetTokenForCard(creditCard, bankCard, token);
        sendCardPasswordResetEmail(email, token);

        return true;
    }

    public String resetPassword(String token, String password) {
        Optional<UserPasswordResetToken> optionalToken = userPasswordResetTokenRepository.findByToken(token);
        if (optionalToken.isEmpty()) {
            return "Invalid token";
        }

        UserPasswordResetToken myToken = optionalToken.get();

        User user = myToken.getUser();
        Date date = myToken.getExpiryDate();

        if (date.before(new Date())) {
            userPasswordResetTokenRepository.delete(myToken);
            return "Token expired";
        }

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        userPasswordResetTokenRepository.delete(myToken);

        return "Password reset";
    }

    public String resetCardPassword(String token, String newPassword) {
        Optional<CardPasswordResetToken> optionalToken = cardPasswordResetTokenRepository.findByToken(token);
        if (optionalToken.isEmpty()) {
            return "Invalid token";
        }

        CardPasswordResetToken myToken = optionalToken.get();
        Date expiryDate = myToken.getExpiryDate();

        if (expiryDate.before(new Date())) {
            cardPasswordResetTokenRepository.delete(myToken);
            return "Token expired";
        }

        CreditCard creditCard = myToken.getCreditCard();
        BankCard bankCard = myToken.getBankCard();

        if (creditCard != null) {
            creditCard.setPassword(passwordEncoder.encode(newPassword));
            creditCard.setActive(true);
            creditCardRepository.save(creditCard);
        } else if (bankCard != null) {
            bankCard.setPassword(passwordEncoder.encode(newPassword));
            bankCard.setActive(true);
            bankCardRepository.save(bankCard);
        }

        cardPasswordResetTokenRepository.delete(myToken);
        return "Card password has been successfully reset";
    }
}
