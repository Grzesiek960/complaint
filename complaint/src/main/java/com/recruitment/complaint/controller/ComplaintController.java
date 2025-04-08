package com.recruitment.complaint.controller;

import com.recruitment.complaint.dto.ComplaintResponse;
import com.recruitment.complaint.dto.CreateComplaintRequest;
import com.recruitment.complaint.dto.UpdateComplaintRequest;
import com.recruitment.complaint.service.ComplaintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping
    public ResponseEntity<ComplaintResponse> createComplaint(
            @Valid @RequestBody CreateComplaintRequest request,
            HttpServletRequest httpRequest
    ) {
        String forwardedIp = httpRequest.getHeader("X-Forwarded-For");
        String clientIp = (forwardedIp == null || forwardedIp.isBlank())
                ? httpRequest.getRemoteAddr()
                : forwardedIp;

        ComplaintResponse created = complaintService.createComplaint(request, clientIp);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComplaintResponse> updateComplaint(
            @PathVariable Long id,
            @Valid @RequestBody UpdateComplaintRequest updateRequest
    ) {
        ComplaintResponse updated = complaintService.updateComplaintContent(id, updateRequest);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComplaintResponse> getComplaintById(@PathVariable Long id) {
        ComplaintResponse response = complaintService.getComplaint(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ComplaintResponse>> getAllComplaints(Pageable pageable) {
        Page<ComplaintResponse> page = complaintService.getAllComplaints(pageable);
        return ResponseEntity.ok(page);
    }
}
