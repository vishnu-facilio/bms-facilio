package com.facilio.aws.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.facilio.services.FacilioHttpUtils;
import com.nimbusds.jose.jwk.RSAKey;

public class FederatedIdentityUtil {

	private static final String GOOGLE_JWK_ENDPOINT = "https://www.googleapis.com/oauth2/v3/certs";
	
	private static Map<String, String> jwkStore = new HashMap<>();
	
	private static Algorithm getGoogleAlgorithm(String idToken) throws Exception {
		String jwkResp = null;
		if (jwkStore.containsKey(GOOGLE_JWK_ENDPOINT)) {
			jwkResp = jwkStore.get(GOOGLE_JWK_ENDPOINT);
		}
		else {
			jwkResp = FacilioHttpUtils.doHttpGet(GOOGLE_JWK_ENDPOINT, null, null);
		}
		if (jwkResp != null) {
			jwkStore.put(GOOGLE_JWK_ENDPOINT, jwkResp);
			JSONObject jobj = (JSONObject) new JSONParser().parse(jwkResp);
			
			JSONArray keys = (JSONArray) jobj.get("keys");
			for (Object key : keys) {
				JSONObject keyJson = (JSONObject) key;
				if (JWT.decode(idToken).getKeyId().equals(keyJson.get("kid").toString())) {
					
					RSAKey rsaKey = com.nimbusds.jose.jwk.RSAKey.parse(keyJson.toJSONString());
					
					return Algorithm.RSA256(rsaKey.toRSAPublicKey(), rsaKey.toRSAPrivateKey());
				}
			}
		}
		return null;
	}
	
	public static JSONObject verifyGooogeIdToken(String idToken) throws Exception {
		
		if (!"true".equalsIgnoreCase(FacilioProperties.getConfig("google.auth"))) {
			return null;
		}
		
		Algorithm algorithm = getGoogleAlgorithm(idToken);
		
		if (algorithm != null) {
			
			JWTVerifier verifier = JWT.require(algorithm).build();
	
			DecodedJWT jwt = verifier.verify(idToken);
			
			if (jwt != null) {
				if (jwt.getIssuer().indexOf("accounts.google.com") >= 0 && jwt.getAudience().get(0).equals(FacilioProperties.getConfig("google.auth.clientid"))) {
					Map<String, Claim> claims = jwt.getClaims();
					Iterator<String> itr = claims.keySet().iterator();
					
					JSONObject authData = new JSONObject();
					while (itr.hasNext()) {
						String key = itr.next();
						
						authData.put(key, claims.get(key).asString());
					}
					return authData;
				}
			}
		}
		return null;
	}
}
