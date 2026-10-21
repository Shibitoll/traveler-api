package com.example.traveler_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Schema(name = "CreateLocationRequest")
public class CreateLocationRequest {

    @JsonProperty("parent_version")
    @Schema(example = "1")
    private Integer parentVersion;

    @Schema(example = "Eiffel Tower")
    private String name;

    @Schema(example = "Champ de Mars, 5 Av. Anatole France, 75007 Paris")
    private String address;

    @Schema(example = "48.8584")
    private Double latitude;

    @Schema(example = "2.2945")
    private Double longitude;

    @JsonProperty("arrival_date")
    private OffsetDateTime arrivalDate;

    @JsonProperty("departure_date")
    private OffsetDateTime departureDate;

    @Schema(example = "25.00")
    private BigDecimal budget;

    @Schema(example = "Visit at sunset")
    private String notes;

    public CreateLocationRequest() {
    }

    public Integer getParentVersion() { return parentVersion; }
    public void setParentVersion(Integer parentVersion) { this.parentVersion = parentVersion; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public OffsetDateTime getArrivalDate() { return arrivalDate; }
    public void setArrivalDate(OffsetDateTime arrivalDate) { this.arrivalDate = arrivalDate; }

    public OffsetDateTime getDepartureDate() { return departureDate; }
    public void setDepartureDate(OffsetDateTime departureDate) { this.departureDate = departureDate; }

    public BigDecimal getBudget() { return budget; }
    public void setBudget(BigDecimal budget) { this.budget = budget; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}