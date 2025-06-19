package com.gaformario.generate.qrcode.controller;

import com.gaformario.generate.qrcode.dto.QrCodeRequest;
import com.gaformario.generate.qrcode.dto.QrCodeResponse;
import com.gaformario.generate.qrcode.service.QrCodeGeneratorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/qr-code")
@Tag(name = "QR Code", description = "Endpoint para criação de um QR Code dado uma url")
public class QrCodeController {

    private static final Logger log = LoggerFactory.getLogger(QrCodeController.class);
    private final QrCodeGeneratorService qrCodeGeneratorService;

    public QrCodeController(QrCodeGeneratorService qrCodeService) {
        this.qrCodeGeneratorService = qrCodeService;
    }

    @PostMapping
    public ResponseEntity<QrCodeResponse> generate(@RequestBody @Valid QrCodeRequest request) {
        try {
            QrCodeResponse response = this.qrCodeGeneratorService.generateUploadQrCode(request.text());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("error: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
