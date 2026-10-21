package com.example.traveler_api.repository;

import com.example.traveler_api.entity.TravelPlan;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface TravelPlanRepository extends JpaRepository<TravelPlan, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM TravelPlan p WHERE p.id = :id")
    Optional<TravelPlan> findByIdWithLock(@Param("id") UUID id);
}