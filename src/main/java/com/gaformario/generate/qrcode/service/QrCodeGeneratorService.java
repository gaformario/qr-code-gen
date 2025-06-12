package com.gaformario.generate.qrcode.service;

import com.gaformario.generate.qrcode.dto.QrCodeResponse;
import com.gaformario.generate.qrcode.ports.StoragePort;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class QrCodeGeneratorService {
    private final StoragePort storage;

    public QrCodeGeneratorService(StoragePort storage) {
        this.storage = storage;
    }

    public QrCodeResponse generateUploadQrCode(String text) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 1);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 300, 300, hints);
        MatrixToImageConfig config = new MatrixToImageConfig(0xFF1A237E, 0xFFFFFFFF);

        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix, config);

        InputStream logoStream = getClass().getResourceAsStream("/image.png");
        if (logoStream == null) {
            throw new FileNotFoundException("Logotipo não encontrado em");
        }
        BufferedImage logo = ImageIO.read(logoStream);

        int logoSize = qrImage.getWidth() / 4;

        BufferedImage logoWithBackground = new BufferedImage(logoSize, logoSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D logoGraphics = logoWithBackground.createGraphics();
        logoGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        logoGraphics.setColor(Color.WHITE);
        logoGraphics.fillOval(0, 0, logoSize, logoSize);

        int logoPadding = Math.max(logoSize / 40, 2);
        int actualLogoSize = logoSize - (logoPadding * 2);
        Image scaledLogo = logo.getScaledInstance(actualLogoSize, actualLogoSize, Image.SCALE_SMOOTH);

        BufferedImage circularLogo = new BufferedImage(actualLogoSize, actualLogoSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = circularLogo.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, actualLogoSize, actualLogoSize));
        g2.drawImage(scaledLogo, 0, 0, null);
        g2.dispose();

        logoGraphics.drawImage(circularLogo, logoPadding, logoPadding, null);

        logoGraphics.setColor(Color.BLACK);
        logoGraphics.setStroke(new BasicStroke(2));
        logoGraphics.drawOval(logoPadding, logoPadding, actualLogoSize, actualLogoSize);

        logoGraphics.dispose();

        int centerX = (qrImage.getWidth() - logoSize) / 2;
        int centerY = (qrImage.getHeight() - logoSize) / 2;

        Graphics2D qrGraphics = qrImage.createGraphics();
        qrGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        qrGraphics.setColor(Color.WHITE);
        qrGraphics.fillOval(centerX - 2, centerY - 2, logoSize + 4, logoSize + 4);

        qrGraphics.drawImage(logoWithBackground, centerX, centerY, null);
        qrGraphics.dispose();
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", pngOutputStream);
        byte[] pngQrCodeData = pngOutputStream.toByteArray();
        String url = storage.uploadFile(pngQrCodeData, UUID.randomUUID().toString(), "image/png");

        return new QrCodeResponse(url);
    }
}
