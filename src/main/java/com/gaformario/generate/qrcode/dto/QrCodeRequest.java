package com.gaformario.generate.qrcode.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record QrCodeRequest(
        @NotBlank(message = "Texto não pode esta vazio")
        @URL(message = "Texto deve ser uma URL válida")
        String text
) {
}
