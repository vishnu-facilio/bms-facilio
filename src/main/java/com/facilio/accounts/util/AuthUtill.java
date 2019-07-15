package com.facilio.accounts.util;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.apache.log4j.LogManager;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.fw.auth.CognitoUtil;
import com.facilio.fw.auth.CognitoUtil.CognitoUser;

public class AuthUtill {
	private static org.apache.log4j.Logger logger = LogManager.getLogger(AuthUtill.class.getName());
	public static final String JWT_DELIMITER = "#";
	
	public static CognitoUser verifiyFacilioToken(String idToken, boolean isPortalUser, boolean overrideSessionCheck) {
		System.out.println("verifiyFacilioToken() :idToken :"+idToken);
		try {
			DecodedJWT decodedjwt = validateJWT(idToken, "auth0");
			CognitoUser faciliouser = new CognitoUser();
			if(decodedjwt != null) {
				String email = null;
				if (decodedjwt.getSubject().contains(JWT_DELIMITER)) {
					email = decodedjwt.getSubject().split(JWT_DELIMITER)[0];
				}
				else {
					email = decodedjwt.getSubject().split("_")[0];
				}
				faciliouser.setEmail(email);
				faciliouser.setFacilioauth(true);
				faciliouser.setPortaluser(decodedjwt.getClaim("portaluser").asBoolean());

				if (!isPortalUser) {
					if (overrideSessionCheck || AccountUtil.getUserBean().verifyUserSessionv2(faciliouser.getEmail(), idToken)) {
						return faciliouser;
					} else {
						// invalid session
						return null;
					}
				}
			}
			return faciliouser;
		} catch (Exception e) {
			logger.info("Exception occurred ", e);
			return null;
		}
	}
	
	public static DecodedJWT validateJWT(String token ,String issuer) {
		try {
		    Algorithm algorithm = Algorithm.HMAC256("secret");
		    JWTVerifier verifier = JWT.require(algorithm)
		        .withIssuer(issuer)
		        .build(); //Reusable verifier instance

		    DecodedJWT jwt = verifier.verify(token);
		    System.out.println("\ndecoded "+jwt.getSubject());
		    System.out.println("\ndecoded "+jwt.getClaims());
		    
		    return jwt;
		} catch (UnsupportedEncodingException | JWTVerificationException exception){
			logger.info("exception occurred while decoding JWT ", exception);
			//UTF-8 encoding not supported
			return null;

		}
	}
	
	public static Boolean verifyPasswordv2(String emailaddress, String password) {
        boolean passwordValid = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs=null;
        try {
            conn = FacilioConnectionPool.INSTANCE.getConnection();
            pstmt = conn.prepareStatement("SELECT Users.password,Users.EMAIL FROM Users WHERE Users.EMAIL = ? and USER_VERIFIED=1");
            pstmt.setString(1, emailaddress);
            rs = pstmt.executeQuery();
            if(rs.next()) {
                String storedPass = rs.getString("password");
                String emailindb = rs.getString(2);
                logger.info("Stored : "+storedPass);
                logger.info("UserGiv: "+password);
                if(storedPass.equals(password)) {
                    passwordValid = true;
                }
            } else {
            	logger.info("No records found for  "+emailaddress);
                return null;
            }

        } catch(SQLException | RuntimeException e) {
            logger.info("Exception while verifying password, ", e);
        } finally {
            DBUtil.closeAll(conn, pstmt,rs);
        }

        return passwordValid;
    }
    

}
