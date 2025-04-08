package com.recruitment.complaint.service;

import com.recruitment.complaint.domain.Complaint;
import com.recruitment.complaint.dto.ComplaintResponse;
import com.recruitment.complaint.dto.CreateComplaintRequest;
import com.recruitment.complaint.dto.UpdateComplaintRequest;
import com.recruitment.complaint.exception.ComplaintNotFoundException;
import com.recruitment.complaint.mapper.ComplaintMapper;
import com.recruitment.complaint.repository.ComplaintRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ComplaintServiceTest {

    @Mock
    private ComplaintRepository repository;

    @Mock
    private ComplaintMapper mapper;

    @Mock
    private GeoLocationService geoLocationService;

    @InjectMocks
    private ComplaintService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateComplaint_whenNewComplaint_thenReturnCreatedComplaint() {
        // Przygotowanie danych wejściowych
        CreateComplaintRequest request = new CreateComplaintRequest();
        request.setProductId("prod-123");
        request.setReporter("john@example.com");
        request.setContent("Problem z produktem");

        String clientIp = "127.0.0.1";
        Complaint complaint = new Complaint();
        complaint.setProductId("prod-123");
        complaint.setReporter("john@example.com");
        complaint.setContent("Problem z produktem");

        // Brak duplikatu – zgłoszenie nowe
        when(repository.findByProductIdAndReporter("prod-123", "john@example.com"))
                .thenReturn(Optional.empty());
        when(geoLocationService.getCountryForIp(clientIp))
                .thenReturn("Polska");
        when(mapper.toEntity(request)).thenReturn(complaint);

        // Symulujemy zapis nowej reklamacji
        Complaint savedComplaint = new Complaint();
        savedComplaint.setId(1L);
        savedComplaint.setProductId("prod-123");
        savedComplaint.setReporter("john@example.com");
        savedComplaint.setContent("Problem z produktem");
        savedComplaint.setCountry("Polska");
        savedComplaint.setReportCount(1);
        when(repository.save(any(Complaint.class))).thenReturn(savedComplaint);

        ComplaintResponse expectedResponse = new ComplaintResponse();
        expectedResponse.setId(1L);
        expectedResponse.setProductId("prod-123");
        expectedResponse.setReporter("john@example.com");
        expectedResponse.setContent("Problem z produktem");
        expectedResponse.setCountry("Polska");
        expectedResponse.setReportCount(1);
        when(mapper.toResponse(savedComplaint)).thenReturn(expectedResponse);

        // Wywołanie testowanej metody
        ComplaintResponse result = service.createComplaint(request, clientIp);

        // Weryfikacja
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Polska", result.getCountry());
    }

    @Test
    void testCreateComplaint_whenExistingComplaint_thenIncrementReportCount() {
        // Przygotowanie danych wejściowych
        CreateComplaintRequest request = new CreateComplaintRequest();
        request.setProductId("prod-123");
        request.setReporter("john@example.com");
        request.setContent("Problem z produktem");

        String clientIp = "127.0.0.1";
        Complaint existingComplaint = new Complaint();
        existingComplaint.setId(1L);
        existingComplaint.setProductId("prod-123");
        existingComplaint.setReporter("john@example.com");
        existingComplaint.setContent("Problem z produktem");
        existingComplaint.setReportCount(1);

        // Zgłoszenie już istnieje
        when(repository.findByProductIdAndReporter("prod-123", "john@example.com"))
                .thenReturn(Optional.of(existingComplaint));

        // Aktualizacja zgłoszenia
        Complaint updatedComplaint = new Complaint();
        updatedComplaint.setId(1L);
        updatedComplaint.setProductId("prod-123");
        updatedComplaint.setReporter("john@example.com");
        updatedComplaint.setContent("Problem z produktem");
        updatedComplaint.setReportCount(2);
        when(repository.save(existingComplaint)).thenReturn(updatedComplaint);

        ComplaintResponse expectedResponse = new ComplaintResponse();
        expectedResponse.setId(1L);
        expectedResponse.setProductId("prod-123");
        expectedResponse.setReporter("john@example.com");
        expectedResponse.setContent("Problem z produktem");
        expectedResponse.setReportCount(2);
        when(mapper.toResponse(updatedComplaint)).thenReturn(expectedResponse);

        // Wywołanie metody
        ComplaintResponse result = service.createComplaint(request, clientIp);

        // Weryfikacja
        assertNotNull(result);
        assertEquals(2, result.getReportCount());
    }

    @Test
    void testUpdateComplaintContent_whenComplaintExists_thenUpdateContent() {
        // Przygotowanie danych wejściowych
        Long complaintId = 1L;
        UpdateComplaintRequest updateRequest = new UpdateComplaintRequest();
        updateRequest.setContent("Zaktualizowana treść reklamacji");

        Complaint complaint = new Complaint();
        complaint.setId(complaintId);
        complaint.setContent("Stara treść");

        when(repository.findById(complaintId)).thenReturn(Optional.of(complaint));

        Complaint updatedComplaint = new Complaint();
        updatedComplaint.setId(complaintId);
        updatedComplaint.setContent("Zaktualizowana treść reklamacji");
        when(repository.save(complaint)).thenReturn(updatedComplaint);

        ComplaintResponse expectedResponse = new ComplaintResponse();
        expectedResponse.setId(complaintId);
        expectedResponse.setContent("Zaktualizowana treść reklamacji");
        when(mapper.toResponse(updatedComplaint)).thenReturn(expectedResponse);

        // Wywołanie metody
        ComplaintResponse result = service.updateComplaintContent(complaintId, updateRequest);

        // Weryfikacja
        assertNotNull(result);
        assertEquals("Zaktualizowana treść reklamacji", result.getContent());
    }

    @Test
    void testUpdateComplaintContent_whenComplaintNotFound_thenThrowException() {
        // Przygotowanie danych wejściowych
        Long complaintId = 1L;
        UpdateComplaintRequest updateRequest = new UpdateComplaintRequest();
        updateRequest.setContent("Zaktualizowana treść reklamacji");

        when(repository.findById(complaintId)).thenReturn(Optional.empty());

        // Weryfikacja – spodziewamy się wyjątku
        assertThrows(ComplaintNotFoundException.class, () -> {
            service.updateComplaintContent(complaintId, updateRequest);
        });
    }

    @Test
    void testGetComplaint_whenComplaintExists_thenReturnComplaintResponse() {
        Long complaintId = 1L;
        Complaint complaint = new Complaint();
        complaint.setId(complaintId);
        complaint.setContent("Treść reklamacji");
        when(repository.findById(complaintId)).thenReturn(Optional.of(complaint));

        ComplaintResponse expectedResponse = new ComplaintResponse();
        expectedResponse.setId(complaintId);
        expectedResponse.setContent("Treść reklamacji");
        when(mapper.toResponse(complaint)).thenReturn(expectedResponse);

        // Wywołanie metody
        ComplaintResponse result = service.getComplaint(complaintId);

        // Weryfikacja
        assertNotNull(result);
        assertEquals(complaintId, result.getId());
    }

    @Test
    void testGetComplaint_whenComplaintNotFound_thenThrowException() {
        Long complaintId = 1L;
        when(repository.findById(complaintId)).thenReturn(Optional.empty());

        assertThrows(ComplaintNotFoundException.class, () -> {
            service.getComplaint(complaintId);
        });
    }

    @Test
    void testGetAllComplaints() {
        Complaint complaint1 = new Complaint();
        complaint1.setId(1L);
        Complaint complaint2 = new Complaint();
        complaint2.setId(2L);

        when(repository.findAll()).thenReturn(Arrays.asList(complaint1, complaint2));
        ComplaintResponse response1 = new ComplaintResponse();
        response1.setId(1L);
        ComplaintResponse response2 = new ComplaintResponse();
        response2.setId(2L);
        when(mapper.toResponse(complaint1)).thenReturn(response1);
        when(mapper.toResponse(complaint2)).thenReturn(response2);

        List<ComplaintResponse> responses = service.getAllComplaints();

        assertNotNull(responses);
        assertEquals(2, responses.size());
    }
}
