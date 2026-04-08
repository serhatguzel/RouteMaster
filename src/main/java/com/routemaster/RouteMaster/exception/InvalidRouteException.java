package com.routemaster.RouteMaster.exception;

import org.springframework.http.HttpStatus;

public class InvalidRouteException extends BusinessException {
    public InvalidRouteException(String message) {
        super("INVALID_ROUTE", message, HttpStatus.BAD_REQUEST);
    }
}
