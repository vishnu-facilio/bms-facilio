package com.facilio.services;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.services.impls.aws.AwsUtil;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CryptoUtils {

    public static byte[] HmacSHA256(String data, byte[] key) throws Exception
    {
        String algorithm = "HmacSHA256";
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    public static String hash256(String data) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(data.getBytes());
        return bytesToHex(md.digest());
    }

    public static String bytesToHex(byte[] bytes)
    {
        StringBuilder result = new StringBuilder();
        for (byte byt : bytes) result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        return result.toString();
    }

    public static String getSignature(String payload, String xAmzDate, String path) throws Exception
    {
        String secretKey = FacilioProperties.getConfig("secretKeyId");
        String dateStamp = new SimpleDateFormat("yyyyMMdd").format(new Date()); 	//"20170525";
        String regionName = FacilioProperties.getConfig("region");		//"us-west-2";

        byte[] kSecret = ("AWS4" + secretKey).getBytes(StandardCharsets.UTF_8);
        byte[] kDate = HmacSHA256(dateStamp, kSecret);
        byte[] kRegion = HmacSHA256(regionName, kDate);
       // byte[] kService = HmacSHA256(AwsUtil.AWS_IOT_SERVICE_NAME, kRegion);
       // byte[] kSigning = HmacSHA256("aws4_request", kService);

        String stringToSign = getStringToSign(payload, xAmzDate, path);
      //  return bytesToHex(HmacSHA256(stringToSign, kSigning));
        return null;
    }
    public static String getStringToSign(String payload, String xAmzDate, String path) throws NoSuchAlgorithmException
    {
        String canonicalHeader = "content-type:application/json\nhost:" + FacilioProperties.getConfig("host") + "\nx-amz-date:"+xAmzDate+"\n";
        String signedHeader = "content-type;host;x-amz-date";
        String canonicalRequest = "POST" + "\n" + path + "\n" + "" + "\n" + canonicalHeader + "\n" + signedHeader + "\n" + hash256(payload).toLowerCase();

      //  String scope = new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" + FacilioProperties.getConfig("region") + "/"+ AwsUtil.AWS_IOT_SERVICE_NAME + "/aws4_request";
       // return "AWS4-HMAC-SHA256" + "\n" + xAmzDate + "\n" + scope + "\n" + hash256(canonicalRequest).toLowerCase();
        return null;
    }

    public static Map<String, String> getAuthHeaders(String signature, String xAmzDate)
    {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        headers.put("Host", FacilioProperties.getConfig("host"));
        headers.put("X-Amz-Date", xAmzDate);
     //   headers.put("Authorization", "AWS4-HMAC-SHA256 Credential=" + FacilioProperties.getConfig("accessKeyId") + "/" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" + FacilioProperties.getConfig("region") + "/" + AwsUtil.AWS_IOT_SERVICE_NAME + "/aws4_request, SignedHeaders=content-type;host;x-amz-date, Signature=" + signature);
        return headers;
    }

    public static String encodeToBase64(String data) {
        Base64.Encoder encoder = Base64.getEncoder();
        String encodedData = encoder.encodeToString(data.getBytes());
        return encodedData;
    }

    public static String decodeFromBase64(String encodedData) {
        Base64.Decoder decoder = Base64.getDecoder();
        String decodedData = new String(decoder.decode(encodedData));
        return decodedData;
    }
}
