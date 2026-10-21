package com.example.traveler_api.config;

import com.example.traveler_api.dto.*;
import com.example.traveler_api.entity.Location;
import com.example.traveler_api.entity.TravelPlan;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Travel Planner API")
                        .version("1.0.0")
                        .description("REST API documentation for Traveler API project"));
    }

    @Bean
    public OpenApiCustomizer schemaCustomizer() {
        return openApi -> {
            Class<?>[] classes = new Class<?>[] {
                TravelPlan.class,
                TravelPlanSummary.class,
                TravelPlanDetails.class,
                CreateTravelPlanRequest.class,
                UpdateTravelPlanRequest.class,
                Location.class,
                CreateLocationRequest.class,
                UpdateLocationRequest.class,
                HealthCheck.class,
                ErrorResponse.class,
                ConflictResponse.class
            };

            for (Class<?> clazz : classes) {
                Map<String, Schema> schemas = ModelConverters.getInstance().read(clazz);
                schemas.forEach((key, schema) -> openApi.getComponents().addSchemas(key, schema));
            }
        };
    }
}