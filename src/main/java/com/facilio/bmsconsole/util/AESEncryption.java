package com.facilio.bmsconsole.util;

import org.apache.log4j.LogManager;

import java.security.InvalidKeyException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESEncryption {
    private static org.apache.log4j.Logger log = LogManager.getLogger(AESEncryption.class.getName());
    private Cipher cipher;
    private SecretKey secretKey;
    private byte[] privateKey = "oB9jtDA0z1fkRJSC98KrgZccWvHAq38I".getBytes();
    
    public AESEncryption () {
        try {
            this.cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        } catch (Exception e) {
            log.info("Exception occurred ", e);
        }

        this.secretKey = new SecretKeySpec(this.privateKey, "AES");
    }

    public String encrypt(String plainText) {
        byte[] plainTextByte = plainText.getBytes();
        try {
            this.cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
        } catch (InvalidKeyException e) {
            log.info("Exception occurred ", e);
        }

        byte[] encryptedByte = new byte[0];
        try {
            encryptedByte = cipher.doFinal(plainTextByte);
        } catch (Exception e) {
            log.info("Exception occurred ", e);
        }

        Base64.Encoder encoder = Base64.getEncoder();
        String encryptedText = encoder.encodeToString(encryptedByte);
        return encryptedText;
    }

    public String decrypt(String encryptedText) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] encryptedTextByte = decoder.decode(encryptedText);
        try {
            this.cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
        } catch (InvalidKeyException e) {
            log.info("Exception occurred ", e);
        }
        byte[] decryptedByte = new byte[0];
        try {
            decryptedByte = cipher.doFinal(encryptedTextByte);
        } catch (Exception e) {
            log.info("Exception occurred ", e);
        }
        return new String(decryptedByte);
    }
}