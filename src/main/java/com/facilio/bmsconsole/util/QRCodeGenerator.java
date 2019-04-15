package com.facilio.bmsconsole.util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
			        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
			            new BufferedImageLuminanceSource(
			                ImageIO.read(new FileInputStream(filePath)))));
			        com.google.zxing.Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap, hintMap);
			        return qrCodeResult.getText();
			    }
}
