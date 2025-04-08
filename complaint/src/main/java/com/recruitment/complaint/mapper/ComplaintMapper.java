package com.recruitment.complaint.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.recruitment.complaint.domain.Complaint;
import com.recruitment.complaint.dto.ComplaintResponse;
import com.recruitment.complaint.dto.CreateComplaintRequest;

@Mapper(componentModel = "spring")
public interface ComplaintMapper {

    ComplaintResponse toResponse(Complaint complaint);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "reportCount", ignore = true)
    Complaint toEntity(CreateComplaintRequest request);
}
