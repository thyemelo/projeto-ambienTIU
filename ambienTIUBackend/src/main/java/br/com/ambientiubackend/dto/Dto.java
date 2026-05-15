package br.com.ambientiubackend.dto;

import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record Dto(
        @NotBlank
        String temperature,

        @NotBlank
        String humidity,

        @NotBlank
        String ilumination,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime time
) {
}

