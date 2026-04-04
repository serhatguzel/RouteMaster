package com.routemaster.RouteMaster.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDto {
    @NotBlank(message = "Username bos olamaz")
    private String username;
    @NotBlank(message = "Password bos olamaz")
    private String password;
}