package com.winnerx0.pika.taxact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TaxActRepository extends JpaRepository<TaxAct, UUID> {
}
