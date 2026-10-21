package com.example.traveler_api.repository;

import com.example.traveler_api.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface LocationRepository extends JpaRepository<Location, UUID> {

    @Query("SELECT COALESCE(MAX(l.locationOrder), 0) FROM Location l WHERE l.travelPlan.id = :planId")
    Integer findMaxOrderByTravelPlanId(@Param("planId") UUID planId);
}