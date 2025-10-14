package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Integer> {

    Page<Card> findByUserIdAndCardNumberContaining(Long userId, String cardNumber, Pageable pageable);

    Page<Card> findByUserId(Long userId, Pageable pageable);

    Optional<Card> findById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Card c where c.cardNumber = :cardNumber")
    Optional<Card> findByCardNumberForUpdate(String cardNumber);

}
