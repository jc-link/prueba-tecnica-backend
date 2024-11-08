package com.project.client_ms.repositories;

import com.project.client_ms.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    @Query("""
        select t from Token t inner join AppUser u
        on t.appUser.id = u.id
        where u.id = :userId and (t.expired = false or t.revoked = false)
        """)
    List<Token> findAllValidTokenByAppUserId(Long userId);

    Optional<Token> findByToken(String token);
}
