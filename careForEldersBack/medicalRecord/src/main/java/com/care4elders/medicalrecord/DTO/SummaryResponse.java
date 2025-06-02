package com.care4elders.medicalrecord.DTO;

import lombok.Data;

@Data
public class SummaryResponse {
    private String summary;

    public SummaryResponse(String summary) {
        this.summary = summary;
    }
}
