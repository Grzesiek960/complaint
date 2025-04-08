package com.recruitment.complaint.mapper;

import com.recruitment.complaint.domain.Complaint;
import com.recruitment.complaint.dto.ComplaintResponse;
import com.recruitment.complaint.dto.CreateComplaintRequest;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-08T21:33:48+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.13 (Amazon.com Inc.)"
)
@Component
public class ComplaintMapperImpl implements ComplaintMapper {

    @Override
    public ComplaintResponse toResponse(Complaint complaint) {
        if ( complaint == null ) {
            return null;
        }

        ComplaintResponse complaintResponse = new ComplaintResponse();

        complaintResponse.setId( complaint.getId() );
        complaintResponse.setProductId( complaint.getProductId() );
        complaintResponse.setReporter( complaint.getReporter() );
        complaintResponse.setContent( complaint.getContent() );
        complaintResponse.setCreatedAt( complaint.getCreatedAt() );
        complaintResponse.setCountry( complaint.getCountry() );
        complaintResponse.setReportCount( complaint.getReportCount() );

        return complaintResponse;
    }

    @Override
    public Complaint toEntity(CreateComplaintRequest request) {
        if ( request == null ) {
            return null;
        }

        Complaint.ComplaintBuilder complaint = Complaint.builder();

        complaint.productId( request.getProductId() );
        complaint.reporter( request.getReporter() );
        complaint.content( request.getContent() );

        return complaint.build();
    }
}
