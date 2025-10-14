package com.example.bankcards.repository;

import com.example.bankcards.entity.CardForBlocking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardForBlockingRepository extends JpaRepository<CardForBlocking, Long> {
    void deleteByCardNumber(String cardNumber);

    Optional<CardForBlocking> findByCardNumber(String cardNumber);
}
