package com.care4elders.paramedicalcare.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.time.LocalDateTime;

@Document(collection = "paramedical_professionals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParamedicalProfessional {
    @Id
    private String id;
    private String name;
    private String specialty;
    private String contactInfo;
    private GeoJsonPoint location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
