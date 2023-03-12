package com.facilio.bmsconsole.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.log4j.LogManager;

public class QRCodeGenerator {
	
/*	public static void main(String[] args) throws FileNotFoundException, NotFoundException, IOException {
		String image_name = "39_639";
		Map < EncodeHintType, ErrorCorrectionLevel > hintMap = new HashMap < EncodeHintType, ErrorCorrectionLevel > ();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        String filePath = System.getProperty("user.home") + "/"+image_name+".png";
        String charset = "UTF-8";
        String result = readQRCode(filePath, charset, hintMap);
        String[] resultString = result.split("_");
        for(String s : resultString){
        System.out.println(s);
        }
	}
*/
private static org.apache.log4j.Logger log = LogManager.getLogger(QRCodeGenerator.class.getName());
	public static void generate_qr(String image_name,String qrCodeData) {
        try {
            String filePath = System.getProperty("user.home") + "/"+image_name+".png";
            String charset = "UTF-8"; // or "ISO-8859-1"
            Map < EncodeHintType, ErrorCorrectionLevel > hintMap = new HashMap < EncodeHintType, ErrorCorrectionLevel > ();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            BitMatrix matrix = new MultiFormatWriter().encode(
                new String(qrCodeData.getBytes(charset), charset),
                BarcodeFormat.QR_CODE, 200, 200, hintMap);
            MatrixToImageWriter.writeToFile(matrix, filePath.substring(filePath
                .lastIndexOf('.') + 1), new File(filePath));
            System.out.println("QR code has been generated successfully");
        } catch (Exception e) {
            System.err.println(e);
        }
    }
	
	 public static String readQRCode(String filePath, String charset, Map hintMap)
			    throws FileNotFoundException, IOException, NotFoundException, com.google.zxing.NotFoundException {
	            try(FileInputStream fis = new FileInputStream(filePath)) {
                    BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                            new BufferedImageLuminanceSource(
                                    ImageIO.read(fis))));
                    com.google.zxing.Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap, hintMap);
                    return qrCodeResult.getText();
                }
			    }

    public static File generateQR(String imageName, String qrCodeData, int width, int height) {
        try {
            long startTime= System.currentTimeMillis();
            File tempFile = File.createTempFile(imageName,"");
            String charset = "UTF-8"; // or "ISO-8859-1"
            Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hintMap.put(EncodeHintType.MARGIN, 1);
            hintMap.put(EncodeHintType.CHARACTER_SET, charset);
            BitMatrix matrix = new MultiFormatWriter().encode(qrCodeData, BarcodeFormat.QR_CODE, width, height, hintMap);
            MatrixToImageWriter.writeToFile(matrix,  imageName.substring(imageName.lastIndexOf('.') + 1), tempFile);
            log.info("QR code has been generated successfully, time taken: "+(System.currentTimeMillis()-startTime));
            return tempFile;
        } catch (Exception e) {
            log.info(e);
        }
        return null;
    }
    public static BufferedImage generateQRCodeImage(String barcodeText) throws Exception {

        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 200, 200,getQRCodeHints());

        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    private static java.util.Map<EncodeHintType, Object> getQRCodeHints() {

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        return hints;
    }

}
