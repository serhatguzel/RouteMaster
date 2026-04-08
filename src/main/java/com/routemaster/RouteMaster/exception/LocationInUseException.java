package com.routemaster.RouteMaster.exception;

import org.springframework.http.HttpStatus;

public class LocationInUseException extends BusinessException {
    public LocationInUseException(String message) {
        super("LOCATION_IN_USE", message, HttpStatus.CONFLICT);
    }
}
