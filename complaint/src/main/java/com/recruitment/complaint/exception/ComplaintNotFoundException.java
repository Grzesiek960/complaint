package com.recruitment.complaint.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ComplaintNotFoundException extends RuntimeException {

    private static final String MESSAGE_TEMPLATE = "Complaint with id=%d not found";

    public ComplaintNotFoundException(Long id) {
        super(String.format(MESSAGE_TEMPLATE, id));
    }
}
