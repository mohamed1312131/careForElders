package com.care4elders.paramedicalcare.mapper;

import com.care4elders.paramedicalcare.dto.CreateProfessionalRequest;
import com.care4elders.paramedicalcare.dto.ParamedicalProfessionalDTO;
import com.care4elders.paramedicalcare.dto.UpdateProfessionalRequest;
import com.care4elders.paramedicalcare.entity.ParamedicalProfessional;
import org.springframework.stereotype.Component;

@Component
public class ProfessionalMapper {

    public ParamedicalProfessionalDTO toDTO(ParamedicalProfessional entity) {
        if (entity == null) {
            throw new IllegalArgumentException("ParamedicalProfessional cannot be null");
        }

        return ParamedicalProfessionalDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .specialty(entity.getSpecialty())
                .contactInfo(entity.getContactInfo())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateEntityFromRequest(ParamedicalProfessional entity, UpdateProfessionalRequest request) {
        if (request == null || entity == null) return;

        if (request.hasName()) {
            entity.setName(request.getName());
        }

        if (request.hasSpecialty()) {
            entity.setSpecialty(request.getSpecialty());
        }

        if (request.hasContactInfo()) {
            entity.setContactInfo(request.getContactInfo());
        }
    }

    public ParamedicalProfessional toEntity(CreateProfessionalRequest dto) {
        return ParamedicalProfessional.builder()
                .name(dto.getName())
                .specialty(dto.getSpecialty())
                .contactInfo(dto.getContactInfo())
                .build();
    }
}
