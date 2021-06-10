package com.facilio.auth.actions;

import com.facilio.aws.util.FacilioProperties;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Log4j
public class PasswordHashUtil {

    private static MessageDigest md;

    public static String cryptWithMD5(String pass) {
        if (StringUtils.isNotEmpty(pass)) {
            try {
                md = MessageDigest.getInstance(FacilioProperties.getPasswordHasingFunction());
                byte[] passBytes = pass.getBytes();
                md.reset();
                byte[] digested = md.digest(passBytes);
                StringBuilder sb = new StringBuilder();
                for (byte aDigested : digested) {
                    sb.append(Integer.toHexString(0xff & aDigested));
                }
                return sb.toString();
            } catch (NoSuchAlgorithmException ex) {
                LOGGER.info("Exception ", ex);
            }
        }

        return null;
    }
}


