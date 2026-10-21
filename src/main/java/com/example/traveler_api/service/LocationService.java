package com.example.traveler_api.service;

import com.example.traveler_api.entity.Location;
import com.example.traveler_api.entity.TravelPlan;
import com.example.traveler_api.exception.OptimisticLockingConflictException;
import com.example.traveler_api.exception.ResourceNotFoundException;
import com.example.traveler_api.repository.LocationRepository;
import com.example.traveler_api.repository.TravelPlanRepository;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final TravelPlanRepository travelPlanRepository;

    public LocationService(LocationRepository locationRepository, TravelPlanRepository travelPlanRepository) {
        this.locationRepository = locationRepository;
        this.travelPlanRepository = travelPlanRepository;
    }

    @Transactional
    public Location addLocationToPlan(UUID planId, Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location body cannot be null");
        }

        validateLocationFields(location);

        TravelPlan plan = travelPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Travel plan not found with id: " + planId));

        if (location.getParentVersion() != null && !location.getParentVersion().equals(plan.getVersion())) {
            throw new OptimisticLockingConflictException(
                    "Conflict: Parent plan version mismatch. Expected version: " + plan.getVersion(),
                    plan.getVersion()
            );
        }

        Integer maxOrder = locationRepository.findMaxOrderByTravelPlanId(planId);
        if (maxOrder == null) {
            maxOrder = 0;
        }

        if (location.getLocationOrder() == null) {
            location.setLocationOrder(maxOrder + 1);
        }

        location.setTravelPlan(plan);

        plan.setVersion(plan.getVersion() + 1);

        try {
            travelPlanRepository.save(plan);
            return locationRepository.save(location);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new OptimisticLockingConflictException(
                    "Conflict: Parent plan was modified concurrently by another user",
                    plan.getVersion()
            );
        }
    }

    @Transactional
    public Location updateLocation(UUID locationId, Location updatedData) {
        if (updatedData == null) {
            throw new IllegalArgumentException("Location body cannot be null");
        }

        validateLocationFields(updatedData);

        Location existingLocation = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + locationId));

        TravelPlan parentPlan = existingLocation.getTravelPlan();

        if (updatedData.getParentVersion() != null && !updatedData.getParentVersion().equals(parentPlan.getVersion())) {
            throw new OptimisticLockingConflictException(
                    "Conflict: Parent plan version mismatch. Expected version: " + parentPlan.getVersion(),
                    parentPlan.getVersion()
            );
        }

        if (updatedData.getName() != null) existingLocation.setName(updatedData.getName());
        if (updatedData.getAddress() != null) existingLocation.setAddress(updatedData.getAddress());
        if (updatedData.getLatitude() != null) existingLocation.setLatitude(updatedData.getLatitude());
        if (updatedData.getLongitude() != null) existingLocation.setLongitude(updatedData.getLongitude());
        if (updatedData.getArrivalDate() != null) existingLocation.setArrivalDate(updatedData.getArrivalDate());
        if (updatedData.getDepartureDate() != null) existingLocation.setDepartureDate(updatedData.getDepartureDate());
        if (updatedData.getNotes() != null) existingLocation.setNotes(updatedData.getNotes());
        if (updatedData.getEstimatedCost() != null) existingLocation.setEstimatedCost(updatedData.getEstimatedCost());
        if (updatedData.getLocationOrder() != null) existingLocation.setLocationOrder(updatedData.getLocationOrder());

        parentPlan.setVersion(parentPlan.getVersion() + 1);

        try {
            travelPlanRepository.save(parentPlan);
            return locationRepository.save(existingLocation);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new OptimisticLockingConflictException(
                    "Conflict: Parent plan was modified concurrently by another user",
                    parentPlan.getVersion()
            );
        }
    }

    @Transactional
    public void deleteLocation(UUID locationId, Integer parentVersion) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + locationId));

        TravelPlan parentPlan = location.getTravelPlan();

        if (parentVersion != null && !parentVersion.equals(parentPlan.getVersion())) {
            throw new OptimisticLockingConflictException(
                    "Conflict: Parent plan version mismatch. Expected version: " + parentPlan.getVersion(),
                    parentPlan.getVersion()
            );
        }

        parentPlan.setVersion(parentPlan.getVersion() + 1);

        try {
            travelPlanRepository.save(parentPlan);
            locationRepository.delete(location);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new OptimisticLockingConflictException(
                    "Conflict: Parent plan was modified concurrently by another user",
                    parentPlan.getVersion()
            );
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
    }
}