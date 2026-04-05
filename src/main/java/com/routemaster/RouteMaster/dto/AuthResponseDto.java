package com.routemaster.RouteMaster.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class AuthResponseDto {
    private String token;
    private String role;
}
