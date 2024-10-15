package com.banking.banking_system.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "session_token")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String accessToken;
    private String refreshToken;
    private boolean loggedOut;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
