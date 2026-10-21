package com.example.traveler_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(name = "UpdateTravelPlanRequest")
public class UpdateTravelPlanRequest {

    @Schema(example = "1")
    private Integer version;

    @Schema(example = "Updated Trip Title")
    private String title;

    @Schema(example = "Updated Description")
    private String description;

    @JsonProperty("start_date")
    @Schema(example = "2026-10-16")
    private LocalDate startDate;

    @JsonProperty("end_date")
    @Schema(example = "2026-10-25")
    private LocalDate endDate;

    @Schema(example = "600.00")
    private BigDecimal budget;

    @Schema(example = "EUR")
    private String currency;

    @JsonProperty("is_public")
    @Schema(example = "true")
    private Boolean isPublic;

    public UpdateTravelPlanRequest() {
    }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public BigDecimal getBudget() { return budget; }
    public void setBudget(BigDecimal budget) { this.budget = budget; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
}