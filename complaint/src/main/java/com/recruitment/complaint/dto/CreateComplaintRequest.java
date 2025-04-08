package com.recruitment.complaint.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class CreateComplaintRequest {

    @NotBlank
    private String productId;

    @NotBlank
    private String reporter;

    @NotBlank
    private String content;
}
