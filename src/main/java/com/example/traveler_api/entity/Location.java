package com.example.traveler_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_plan_id", nullable = false)
    @JsonIgnore
    private TravelPlan travelPlan;

    @Column(nullable = false)
    private String name;

    private String address;
    private Double latitude;
    private Double longitude;

    @Column(length = 255)
    private String notes;

    @JsonProperty("arrival_date")
    @Column(name = "arrival_date")
    private OffsetDateTime arrivalDate;

    @JsonProperty("departure_date")
    @Column(name = "departure_date")
    private OffsetDateTime departureDate;

    @JsonProperty("visit_order")
    @Column(name = "location_order", nullable = false)
    private Integer locationOrder;

    @JsonProperty("budget")
    @Column(name = "estimated_cost")
    private BigDecimal estimatedCost;

    @JsonProperty("created_at")
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @JsonProperty("updated_at")
    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Transient
    @JsonProperty("parent_version")
    private Integer parentVersion;

    @Transient
    private UUID bodyTravelPlanId;

    public Location() {
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public TravelPlan getTravelPlan() { return travelPlan; }
    public void setTravelPlan(TravelPlan travelPlan) { this.travelPlan = travelPlan; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public OffsetDateTime getArrivalDate() { return arrivalDate; }
    public void setArrivalDate(OffsetDateTime arrivalDate) { this.arrivalDate = arrivalDate; }

    public OffsetDateTime getDepartureDate() { return departureDate; }
    public void setDepartureDate(OffsetDateTime departureDate) { this.departureDate = departureDate; }

    public Integer getLocationOrder() { return locationOrder; }
    public void setLocationOrder(Integer locationOrder) { this.locationOrder = locationOrder; }

    @JsonProperty("budget")
    public BigDecimal getEstimatedCost() { return estimatedCost; }

    @JsonProperty("budget")
    public void setEstimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Integer getParentVersion() { return parentVersion; }
    public void setParentVersion(Integer parentVersion) { this.parentVersion = parentVersion; }

    @JsonProperty("travel_plan_id")
    public UUID getBodyTravelPlanId() {
        if (travelPlan != null && travelPlan.getId() != null) {
            return travelPlan.getId();
        }
        return bodyTravelPlanId;
    }

    @JsonProperty("travel_plan_id")
    public void setBodyTravelPlanId(UUID bodyTravelPlanId) {
        this.bodyTravelPlanId = bodyTravelPlanId;
    }
}