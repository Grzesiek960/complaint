package com.recruitment.complaint.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruitment.complaint.dto.CreateComplaintRequest;
import com.recruitment.complaint.dto.UpdateComplaintRequest;
import com.recruitment.complaint.repository.ComplaintRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class ComplaintControllerIntegrationTest {

    @Autowired
    private ComplaintRepository complaintRepository;

    @AfterEach
    void cleanup() {
        complaintRepository.deleteAll();
    }

    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15.3")
            .withDatabaseName("complaintdb")
            .withUsername("appuser")
            .withPassword("secret");

    static {
        postgresContainer.start();
    }

    @DynamicPropertySource
    static void setDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.liquibase.change-log", () -> "classpath:db/migration/db.changelog-master.xml");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateComplaint() throws Exception {
        CreateComplaintRequest request = new CreateComplaintRequest();
        request.setProductId("prod-456");
        request.setReporter("jane@example.com");
        request.setContent("Szczegóły problemu z produktem");

        mockMvc.perform(post("/api/complaints")
                        .header("X-Forwarded-For", "127.0.0.1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value("prod-456"))
                .andExpect(jsonPath("$.reportCount").value(1));
    }

    @Test
    void testUpdateComplaint() throws Exception {
        CreateComplaintRequest createRequest = new CreateComplaintRequest();
        createRequest.setProductId("prod-789");
        createRequest.setReporter("alice@example.com");
        createRequest.setContent("Początkowa treść reklamacji");

        String postResponse = mockMvc.perform(post("/api/complaints")
                        .header("X-Forwarded-For", "127.0.0.1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(postResponse).get("id").asLong();

        UpdateComplaintRequest updateRequest = new UpdateComplaintRequest();
        updateRequest.setContent("Zaktualizowana treść reklamacji");

        mockMvc.perform(put("/api/complaints/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Zaktualizowana treść reklamacji"));
    }

    @Test
    void testGetComplaintById_NotFound() throws Exception {
        mockMvc.perform(get("/api/complaints/{id}", 9999))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllComplaints() throws Exception {
        CreateComplaintRequest request1 = new CreateComplaintRequest();
        request1.setProductId("prod-1");
        request1.setReporter("reporter1@example.com");
        request1.setContent("Treść 1");

        CreateComplaintRequest request2 = new CreateComplaintRequest();
        request2.setProductId("prod-2");
        request2.setReporter("reporter2@example.com");
        request2.setContent("Treść 2");

        mockMvc.perform(post("/api/complaints")
                        .header("X-Forwarded-For", "127.0.0.1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/complaints")
                        .header("X-Forwarded-For", "127.0.0.1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/complaints")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.number").value(0));
    }
}
