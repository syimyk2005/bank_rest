package com.example.bankcards.repository;

import com.example.bankcards.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query("""
                Select t from Token t inner join User u
                on t.user.id = u.id
                where t.user.id = :userId and t.isRevoked = false
            """)
    List<Token> findAllTokenByUser(Long userId);

    Optional<Token> findByAccessToken(String accessToken);

}
