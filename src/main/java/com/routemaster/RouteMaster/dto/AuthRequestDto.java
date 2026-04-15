package com.routemaster.RouteMaster.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthRequestDto (
    @NotBlank(message = "Username bos olamaz")
    String username,
    @NotBlank(message = "Password bos olamaz")
    String password

    ) {}