package com.recruitment.complaint.service;

import com.recruitment.complaint.domain.Complaint;
import com.recruitment.complaint.dto.ComplaintResponse;
import com.recruitment.complaint.dto.CreateComplaintRequest;
import com.recruitment.complaint.dto.UpdateComplaintRequest;
import com.recruitment.complaint.exception.ComplaintNotFoundException;
import com.recruitment.complaint.mapper.ComplaintMapper;
import com.recruitment.complaint.repository.ComplaintRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ComplaintService {

    private final ComplaintRepository repository;
    private final ComplaintMapper mapper;
    private final GeoLocationService geoLocationService;

    public ComplaintService(ComplaintRepository repository,
                            ComplaintMapper mapper,
                            GeoLocationService geoLocationService) {
        this.repository = repository;
        this.mapper = mapper;
        this.geoLocationService = geoLocationService;
    }

    @Transactional
    public ComplaintResponse createComplaint(CreateComplaintRequest request, String clientIp) {
        log.info("Rozpoczęto tworzenie reklamacji dla produktu {} przez użytkownika {}",
                request.getProductId(), request.getReporter());

        Optional<Complaint> existing = repository.findByProductIdAndReporter(
                request.getProductId(), request.getReporter());

        Complaint complaint;
        if (existing.isPresent()) {
            complaint = existing.get();
            complaint.setReportCount(complaint.getReportCount() + 1);
            log.info("Znaleziono duplikat, zwiększono licznik zgłoszeń do {}", complaint.getReportCount());
        } else {
            complaint = mapper.toEntity(request);
            String country = geoLocationService.getCountryForIp(clientIp);
            complaint.setCountry(country);
            repository.save(complaint);
            log.info("Utworzono nową reklamację dla produktu {}, IP={}, country={}",
                    complaint.getProductId(), clientIp, country);
        }

        Complaint saved = repository.save(complaint);
        log.debug("Zapisano reklamację w bazie, id={}", saved.getId());
        return mapper.toResponse(saved);
    }

    @Transactional
    public ComplaintResponse updateComplaintContent(Long complaintId, UpdateComplaintRequest updateRequest) {
        log.info("Aktualizacja treści reklamacji o id={}", complaintId);
        Complaint complaint = repository.findById(complaintId)
                .orElseThrow(() -> new ComplaintNotFoundException(complaintId));
        complaint.setContent(updateRequest.getContent());
        Complaint updated = repository.save(complaint);
        log.debug("Zaktualizowano treść reklamacji o id={}", complaintId);
        return mapper.toResponse(updated);
    }

    public ComplaintResponse getComplaint(Long id) {
        Complaint complaint = repository.findById(id)
                .orElseThrow(() -> new ComplaintNotFoundException(id));
        return mapper.toResponse(complaint);
    }

    public List<ComplaintResponse> getAllComplaints() {
        List<Complaint> all = repository.findAll();
        return all.stream().map(mapper::toResponse).toList();
    }
}
