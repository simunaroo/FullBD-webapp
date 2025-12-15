package com.fullbd.fullbdwebsite.repository;

import com.fullbd.fullbdwebsite.model.QuoteRequest;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuoteRequestRepository extends JpaRepository<QuoteRequest, Long> {
    List<QuoteRequest> findAllByCreatedAtAfter(LocalDateTime date);
}