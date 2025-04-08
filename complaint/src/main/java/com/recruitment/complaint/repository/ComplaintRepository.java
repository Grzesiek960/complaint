package com.recruitment.complaint.repository;

import com.recruitment.complaint.domain.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    Optional<Complaint> findByProductIdAndReporter(String productId, String reporter);
}
