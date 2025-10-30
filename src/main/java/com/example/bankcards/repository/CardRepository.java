
package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    Page<Card> findByUserIdAndCardNumberContaining(Long userId, String cardNumber, Pageable pageable);

    Page<Card> findByUserId(Long userId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Card c WHERE c.cardNumber IN :cardNumbers")
    List<Card> findAllByCardNumberInForUpdate(List<String> cardNumbers);

    Optional<Card> findByCardNumber(String cardNumber);

    boolean existsByCardNumber(String cardNumber);
}
