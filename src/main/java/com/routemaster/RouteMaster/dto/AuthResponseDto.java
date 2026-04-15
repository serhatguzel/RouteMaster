package com.routemaster.RouteMaster.dto;

public record AuthResponseDto(String accessToken, String refreshToken, String role) { }
