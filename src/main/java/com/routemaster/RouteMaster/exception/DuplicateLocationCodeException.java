package com.routemaster.RouteMaster.exception;

import org.springframework.http.HttpStatus;

public class DuplicateLocationCodeException extends BusinessException {
    public DuplicateLocationCodeException(String locationCode) {
        super("DUPLICATE_LOCATION_CODE",
                "Location code already exists: " + locationCode,
                HttpStatus.CONFLICT);
    }
}
