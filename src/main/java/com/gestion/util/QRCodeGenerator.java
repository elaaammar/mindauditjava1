package com.gestion.util;

import com.gestion.entity.Entreprise;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.image.BufferedImage;
import java.io.File;

public class QRCodeGenerator {
    public static BufferedImage generateImage(Entreprise e) throws Exception {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode("Entreprise: " + e.getNom(), BarcodeFormat.QR_CODE, 300, 300);
        return MatrixToImageWriter.toBufferedImage(matrix);
    }
    public static void saveToFile(BufferedImage bi, String path) throws Exception {
        javax.imageio.ImageIO.write(bi, "PNG", new File(path));
    }
}
