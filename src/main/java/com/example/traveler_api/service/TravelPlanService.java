package com.example.traveler_api.service;

import com.example.traveler_api.dto.TravelPlanSummary;
import com.example.traveler_api.entity.Location;
import com.example.traveler_api.entity.TravelPlan;
import com.example.traveler_api.exception.OptimisticLockingConflictException;
import com.example.traveler_api.exception.ResourceNotFoundException;
import com.example.traveler_api.repository.TravelPlanRepository;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TravelPlanService {

    private final TravelPlanRepository travelPlanRepository;

    public TravelPlanService(TravelPlanRepository travelPlanRepository) {
        this.travelPlanRepository = travelPlanRepository;
    }

    public List<TravelPlanSummary> getAllPlans() {
        return travelPlanRepository.findAll().stream()
                .map(plan -> new TravelPlanSummary(
                        plan.getId(),
                        plan.getTitle(),
                        plan.getDescription(),
                        plan.getStartDate(),
                        plan.getEndDate(),
                        plan.getBudget(),
                        plan.getCurrency() != null ? plan.getCurrency() : "EUR",
                        plan.getIsPublic() != null ? plan.getIsPublic() : true,
                        plan.getVersion() != null ? plan.getVersion() : 1,
                        plan.getCreatedAt(),
                        plan.getUpdatedAt(),
                        plan.getLocations() != null ? plan.getLocations().size() : 0
                ))
                .collect(Collectors.toList());
    }

    public TravelPlan getPlanById(UUID id) {
        TravelPlan plan = travelPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Travel plan not found with id: " + id));

        if (plan.getLocations() != null) {
            plan.getLocations().sort((l1, l2) -> {
                if (l1.getLocationOrder() == null) return 1;
                if (l2.getLocationOrder() == null) return -1;
                return l1.getLocationOrder().compareTo(l2.getLocationOrder());
            });
        }
        return plan;
    }

    @Transactional
    public TravelPlan createPlan(TravelPlan plan) {
        validateTravelPlan(plan);

        if (plan.getVersion() == null) {
            plan.setVersion(1);
        }
        if (plan.getCurrency() == null) {
            plan.setCurrency("EUR");
        }
        if (plan.getIsPublic() == null) {
            plan.setIsPublic(true);
        }
        if (plan.getLocations() != null) {
            for (Location location : plan.getLocations()) {
                location.setTravelPlan(plan);
            }
        } else {
            plan.setLocations(new ArrayList<>());
        }
        return travelPlanRepository.save(plan);
    }

    @Transactional
    public TravelPlan updatePlan(UUID id, TravelPlan updatedPlan) {
        TravelPlan existingPlan = getPlanById(id);

        if (updatedPlan.getId() != null && !updatedPlan.getId().equals(id)) {
            throw new IllegalArgumentException("Plan ID in URL path does not match ID in request body");
        }

        validateTravelPlanUpdate(existingPlan, updatedPlan);

        if (!updatedPlan.getVersion().equals(existingPlan.getVersion())) {
            throw new OptimisticLockingConflictException("Conflict: Plan was modified by another user", existingPlan.getVersion());
        }

        if (updatedPlan.getTitle() != null) existingPlan.setTitle(updatedPlan.getTitle());
        if (updatedPlan.getDescription() != null) existingPlan.setDescription(updatedPlan.getDescription());
        if (updatedPlan.getStartDate() != null) existingPlan.setStartDate(updatedPlan.getStartDate());
        if (updatedPlan.getEndDate() != null) existingPlan.setEndDate(updatedPlan.getEndDate());
        if (updatedPlan.getBudget() != null) existingPlan.setBudget(updatedPlan.getBudget());
        if (updatedPlan.getCurrency() != null) existingPlan.setCurrency(updatedPlan.getCurrency());
        if (updatedPlan.getIsPublic() != null) existingPlan.setIsPublic(updatedPlan.getIsPublic());

        if (updatedPlan.getLocations() != null) {
            if (existingPlan.getLocations() == null) {
                existingPlan.setLocations(new ArrayList<>());
            } else {
                existingPlan.getLocations().clear();
            }
            for (Location loc : updatedPlan.getLocations()) {
                loc.setTravelPlan(existingPlan);
                existingPlan.getLocations().add(loc);
            }
        }

        try {
            return travelPlanRepository.save(existingPlan);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new OptimisticLockingConflictException("Conflict: Plan was modified concurrently", existingPlan.getVersion());
        }
    }

    @Transactional
    public void deletePlan(UUID id, Integer version) {
        TravelPlan existingPlan = travelPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Travel plan not found with id: " + id));

        if (version != null && !version.equals(existingPlan.getVersion())) {
            throw new OptimisticLockingConflictException("Conflict: Travel plan was modified by another user", existingPlan.getVersion());
        }

        travelPlanRepository.delete(existingPlan);
    }

    private void validateTravelPlan(TravelPlan plan) {
        if (plan == null) {
            throw new IllegalArgumentException("Plan body cannot be null");
        }
        if (plan.getTitle() == null || plan.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required and cannot be blank");
        }
        if (plan.getTitle().length() > 255) {
            throw new IllegalArgumentException("Title length cannot exceed 255 characters");
        }
        if (plan.getDescription() != null && plan.getDescription().length() > 255) {
            throw new IllegalArgumentException("Description length cannot exceed 255 characters");
        }
        if (plan.getCurrency() != null) {
            String curr = plan.getCurrency().trim();
            if (curr.isEmpty() || curr.length() != 3 || !curr.matches("^[A-Z]{3}$")) {
                throw new IllegalArgumentException("Currency must be a valid 3-letter ISO uppercase code");
            }
        }
        if (plan.getBudget() != null) {
            if (plan.getBudget().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Budget must be positive or zero");
            }
            if (plan.getBudget().stripTrailingZeros().scale() > 2) {
                throw new IllegalArgumentException("Budget cannot have more than 2 decimal places");
            }
        }
        if (plan.getStartDate() == null && plan.getEndDate() != null) {
            throw new IllegalArgumentException("Cannot set end_date without start_date");
        }
        if (plan.getStartDate() != null && plan.getEndDate() != null && plan.getEndDate().isBefore(plan.getStartDate())) {
            throw new IllegalArgumentException("End date must be after or equal to start date");
        }
        validateLocationsList(plan.getLocations());
    }

    private void validateTravelPlanUpdate(TravelPlan existingPlan, TravelPlan updatedPlan) {
        if (updatedPlan == null) {
            throw new IllegalArgumentException("Update body cannot be null");
        }
        if (updatedPlan.getVersion() == null || updatedPlan.getVersion() < 1) {
            throw new IllegalArgumentException("Version is required for PUT update and must be >= 1");
        }
        if (updatedPlan.getTitle() == null || updatedPlan.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required and cannot be blank");
        }
        if (updatedPlan.getTitle().length() > 255) {
            throw new IllegalArgumentException("Title length cannot exceed 255 characters");
        }
        if (updatedPlan.getDescription() != null && updatedPlan.getDescription().length() > 255) {
            throw new IllegalArgumentException("Description length cannot exceed 255 characters");
        }
        if (updatedPlan.getCurrency() != null) {
            String curr = updatedPlan.getCurrency().trim();
            if (curr.isEmpty() || curr.length() != 3 || !curr.matches("^[A-Z]{3}$")) {
                throw new IllegalArgumentException("Currency must be a valid 3-letter ISO uppercase code");
            }
        }
        if (updatedPlan.getBudget() != null) {
            if (updatedPlan.getBudget().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Budget must be positive or zero");
            }
            if (updatedPlan.getBudget().stripTrailingZeros().scale() > 2) {
                throw new IllegalArgumentException("Budget cannot have more than 2 decimal places");
            }
        }

        LocalDate finalStart = updatedPlan.getStartDate() != null ? updatedPlan.getStartDate() : existingPlan.getStartDate();
        LocalDate finalEnd = updatedPlan.getEndDate() != null ? updatedPlan.getEndDate() : existingPlan.getEndDate();

        if (finalStart == null && finalEnd != null) {
            throw new IllegalArgumentException("Cannot have end_date without start_date");
        }
        if (finalStart != null && finalEnd != null && finalEnd.isBefore(finalStart)) {
            throw new IllegalArgumentException("End date must be after or equal to start date");
        }

        if (updatedPlan.getLocations() != null) {
            validateLocationsList(updatedPlan.getLocations());
        }
    }

    private void validateLocationsList(List<Location> locations) {
        if (locations != null && !locations.isEmpty()) {
            Set<Integer> orders = new HashSet<>();
            for (Location loc : locations) {
                if (loc == null) {
                    throw new IllegalArgumentException("Location item cannot be null");
                }
                validateLocationFields(loc);

                if (loc.getLocationOrder() == null) {
                    throw new IllegalArgumentException("Location visit_order is required");
                }
                if (loc.getLocationOrder() < 1) {
                    throw new IllegalArgumentException("Location visit_order must be >= 1");
                }
                if (!orders.add(loc.getLocationOrder())) {
                    throw new IllegalArgumentException("Duplicate visit_order in locations list");
                }
            }

            for (int i = 1; i <= locations.size(); i++) {
                if (!orders.contains(i)) {
                    throw new IllegalArgumentException("Location visit_orders must be contiguous starting from 1");
                }
            }
        }
    }

    private void validateLocationFields(Location loc) {
        if (loc.getName() == null || loc.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Location name is required and cannot be blank");
        }
        if (loc.getName().length() > 255) {
            throw new IllegalArgumentException("Location name length cannot exceed 255 characters");
        }
        if (loc.getNotes() != null && loc.getNotes().length() > 255) {
            throw new IllegalArgumentException("Location notes length cannot exceed 255 characters");
        }
        if (loc.getEstimatedCost() != null) {
            if (loc.getEstimatedCost().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Location cost must be positive or zero");
            }
            if (loc.getEstimatedCost().stripTrailingZeros().scale() > 2) {
                throw new IllegalArgumentException("Location cost cannot have more than 2 decimal places");
            }
        }
        if (loc.getLatitude() != null && (loc.getLatitude() < -90.0 || loc.getLatitude() > 90.0)) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }
        if (loc.getLongitude() != null && (loc.getLongitude() < -180.0 || loc.getLongitude() > 180.0)) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }
        if ((loc.getLatitude() != null && loc.getLongitude() == null) || (loc.getLatitude() == null && loc.getLongitude() != null)) {
            throw new IllegalArgumentException("Latitude and longitude must be provided together");
        }
        if (loc.getDepartureDate() != null && loc.getArrivalDate() == null) {
            throw new IllegalArgumentException("Arrival date is required if departure date is provided");
        }
        if (loc.getArrivalDate() != null && loc.getDepartureDate() != null) {
            if (loc.getDepartureDate().isBefore(loc.getArrivalDate())) {
                throw new IllegalArgumentException("Departure date must be after or equal to arrival date");
            }
        }
        if (loc.getLocationOrder() != null && loc.getLocationOrder() < 1) {
            throw new IllegalArgumentException("Location visit_order must be >= 1");
        }
    }
}