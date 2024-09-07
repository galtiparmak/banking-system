package com.banking.banking_system.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_password_reset_tokens")
public class UserPasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Column(nullable = false, unique = true)
    private String token;
    @JoinColumn(nullable = false, name = "user_id")
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    private User user;
    @Column(nullable = false)
    private Date expiryDate;

    public UserPasswordResetToken(String token, User user, Date date) {
        this.token = token;
        this.user = user;
        this.expiryDate = date;
    }
}
