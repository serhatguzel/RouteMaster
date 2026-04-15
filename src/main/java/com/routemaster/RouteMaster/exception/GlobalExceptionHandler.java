package com.routemaster.RouteMaster.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String BASE_URI = "https://routemaster.com/errors/";

    // Tüm iş kuralı exception'ları (BusinessException ve alt sınıfları)
    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusinessException(BusinessException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(ex.getHttpStatus(), ex.getMessage());
        pd.setTitle(toTitle(ex.getErrorCode()));
        pd.setType(URI.create(BASE_URI + ex.getErrorCode().toLowerCase().replace('_', '-')));
        pd.setProperty("errorCode", ex.getErrorCode());
        return pd;
    }

    // JPA kaydı bulunamadığında
    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFoundException(EntityNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Resource Not Found");
        pd.setType(URI.create(BASE_URI + "not-found"));
        pd.setProperty("errorCode", "NOT_FOUND");
        return pd;
    }

    // Spring Security - Yanlış kullanıcı adı/şifre
    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ProblemDetail handleBadCredentialsException(
            org.springframework.security.authentication.BadCredentialsException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        pd.setTitle("Authentication Failed");
        pd.setType(URI.create(BASE_URI + "bad-credentials"));
        pd.setProperty("errorCode", "BAD_CREDENTIALS");
        return pd;
    }

    // DTO Validasyon (Geçersiz paremetreler, regex hataları vs.)
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Girdi doğrulama hatası (Validation Failed)");
        pd.setTitle("Validation Error");
        pd.setType(URI.create(BASE_URI + "validation-error"));
        pd.setProperty("errorCode", "VALIDATION_ERROR");

        // Hangi alanlarda ne hata olduğunu bir harita(Map) içinde topluyoruz
        java.util.Map<String, String> errors = new java.util.HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        // Hatalı paramtreleri front-end'in okuyabileceği şekilde JSON objesinin içine atıyoruz
        pd.setProperty("invalidParams", errors);
        return pd;
    }

    // Beklenmeyen tüm hatalar
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(Exception ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        pd.setTitle("Internal Server Error");
        pd.setType(URI.create(BASE_URI + "internal-error"));
        pd.setProperty("errorCode", "INTERNAL_ERROR");
        return pd;
    }

    // "INVALID_ROUTE" → "Invalid Route"
    private String toTitle(String errorCode) {
        return java.util.Arrays.stream(errorCode.split("_"))
                .map(w -> w.charAt(0) + w.substring(1).toLowerCase())
                .collect(java.util.stream.Collectors.joining(" "));
    }
}
