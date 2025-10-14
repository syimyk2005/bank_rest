package com.example.bankcards.repository;

import com.example.bankcards.entity.CardForBlocking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardForBlockingRepository extends JpaRepository<CardForBlocking, Long> {

}
