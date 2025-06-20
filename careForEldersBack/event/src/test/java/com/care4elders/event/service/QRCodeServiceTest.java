package com.care4elders.event.service;

import com.google.zxing.WriterException;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;


class QRCodeServiceTest {

    private final QRCodeService qrCodeService = new QRCodeService();

    @Test
    void testGenerateQRCodeImageReturnsNonEmptyByteArray() throws WriterException, IOException {
        String text = "https://care4elders.com";
        int width = 200;
        int height = 200;

        byte[] qrCode = qrCodeService.generateQRCodeImage(text, width, height);

        assertNotNull(qrCode, "QR code byte array should not be null");
        assertTrue(qrCode.length > 0, "QR code byte array should not be empty");
    }

    @Test
    void testGenerateQRCodeImageWithEmptyText() {
        String text = "";
        int width = 100;
        int height = 100;

        assertThrows(IllegalArgumentException.class, () -> {
            qrCodeService.generateQRCodeImage(text, width, height);
        }, "Should throw IllegalArgumentException for empty text");
    }
}
