package com.recruitment.complaint.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.Instant;

@Data
public class ComplaintResponse {
    private Long id;
    private String productId;
    private String reporter;
    private String content;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss", timezone = "UTC")
    private Instant createdAt;
    private String country;
    private int reportCount;
}
