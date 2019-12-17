package com.facilio.bmsconsole.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

public class FileJWTUtil {
	static String issuer = "auth0";
	private static final Logger LOGGER = LogManager.getLogger(FileJWTUtil.class.getName());
	
	public static String generateFileJWT(Map<String, String> claims) {
		try {
		    Algorithm algorithm = Algorithm.HMAC256("vgMfGXPpw4SOk4E9mHzOpZrgQGVkvQKf");
		    
		    JWTCreator.Builder builder = JWT.create();
		    for(Map.Entry<String, String> claim : claims.entrySet()) {
		    	builder.withClaim(claim.getKey(), claim.getValue());
		    }
		    builder.withIssuer(issuer);
		    
		    return builder.sign(algorithm);
		} catch (UnsupportedEncodingException | JWTCreationException exception){
			LOGGER.error("exception occurred while creating JWT "+ exception.toString());
		    //UTF-8 encoding not supported
		}
		return null;
	}
	
	public static Map<String, String> validateJWT(String token) {
		try {
			Map<String, String> claims = new HashMap<>();
			Algorithm algorithm = Algorithm.HMAC256("vgMfGXPpw4SOk4E9mHzOpZrgQGVkvQKf");
			JWTVerifier verifier = JWT.require(algorithm).withIssuer(issuer).build(); // Reusable verifier instance

			DecodedJWT jwt = verifier.verify(token);
			System.out.println("\ndecoded " + jwt.getClaims());
			Map<String, Claim> jwtClaims = jwt.getClaims();
			if(jwtClaims!=null && !jwtClaims.isEmpty()) {
				for(Map.Entry<String, Claim> claim : jwtClaims.entrySet()) {
					claims.put(claim.getKey(), claim.getValue().asString());
				}
			}
			return claims;
		} catch (UnsupportedEncodingException | JWTVerificationException exception) {
			LOGGER.error("exception occurred while decoding JWT "+ exception.toString());
			// UTF-8 encoding not supported
			return null;

		}
	}
}
