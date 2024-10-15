package com.banking.banking_system.Repository;

import com.banking.banking_system.Entity.SessionToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SessionTokenRepository extends JpaRepository<SessionToken, Long> {
    @Query("""
        select t
        from SessionToken t inner join User u
        on t.user.id = u.id
        where t.user.id = :userId and t.loggedOut = false
""")
    List<SessionToken> findAllByUser(Long userId);

    Optional<SessionToken> findByAccessToken(String accessToken);

    Optional<SessionToken> findByRefreshToken(String refreshToken);
}
