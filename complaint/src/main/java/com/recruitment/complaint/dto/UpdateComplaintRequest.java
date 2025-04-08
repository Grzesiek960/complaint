package com.recruitment.complaint.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class UpdateComplaintRequest {

    @NotBlank
    private String content;
}
