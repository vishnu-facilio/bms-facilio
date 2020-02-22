package com.facilio.accounts.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.chargebee.internal.StringJoiner;
import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.User;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.modules.FieldUtil;

public class UserUtil {

	public static final String JWT_DELIMITER = "#";
	private static Logger log = LogManager.getLogger(UserUtil.class.getName());
	
	public static String createJWT(String id, String issuer, String subject, long ttlMillis) {
		 
		try {
		    Algorithm algorithm = Algorithm.HMAC256("secret");
		    
		    String key = subject + JWT_DELIMITER + System.currentTimeMillis();
		    JWTCreator.Builder builder = JWT.create().withSubject(key)
	        .withIssuer(issuer);
		    
		    return builder.sign(algorithm);
		} catch (UnsupportedEncodingException | JWTCreationException exception){
			log.info("exception occurred while creating JWT "+ exception.toString());
		    //UTF-8 encoding not supported
		}
		return null;
	}

	public static List<User> getAppUsers(List<IAMUser> iamUsers) throws Exception {
	
		List<User> appUsers = new ArrayList<User>();
		if(CollectionUtils.isNotEmpty(iamUsers)) {
			for(IAMUser iamUser : iamUsers) {
				appUsers.add(new User(iamUser));
			}
			return appUsers;
		}
		return null;
		
	}
	
	public static void setIAMUserPropsv3(List<Map<String, Object>> actualPropsList, long orgId, boolean shouldFetchDeleted, String appDomain) throws Exception {
		if(CollectionUtils.isNotEmpty(actualPropsList)) {
			List<Map<String, Object>> finalMap = new ArrayList<Map<String,Object>>();
			StringJoiner userIds = new StringJoiner(",");
			for(Map<String, Object> map : actualPropsList) {
				userIds.add(String.valueOf((long)map.get("iamOrgUserId")));
			}
			List<IAMUser> iamUsers = getIAMUserPropsv3(userIds.toString(), orgId, shouldFetchDeleted, appDomain);
			if (CollectionUtils.isNotEmpty(iamUsers)) {
				for(Map<String, Object> map : actualPropsList) {
					long iamOrgUserId = (long)map.get("iamOrgUserId");
					List<IAMUser> result = iamUsers.stream()  
	                .filter(user -> user.getIamOrgUserId() == iamOrgUserId)     
	                .collect(Collectors.toList());
					if(CollectionUtils.isNotEmpty(result)) {
						map.putAll(FieldUtil.getAsProperties(result.get(0)));
						finalMap.add(map);
					}
				}
			}
			actualPropsList.clear();
			actualPropsList.addAll(finalMap);
		}
	}
	
	public static Map<String, Object> getUserFromEmailOrPhone(String emailOrPhone, String appDomain) throws Exception {
		return IAMUserUtil.getUserForEmailOrPhone(emailOrPhone, appDomain, false, -1);
	}
	
	public static List<IAMUser> getIAMUserPropsv3(String userIds, long orgId, boolean shouldFetchDeleted, String appDomain) throws Exception {
		return IAMUserUtil.getUserDatav3(userIds, orgId, shouldFetchDeleted, appDomain);
	}
	
	public static Map<String, Object> getUserFromPhone(String phone, String appDomain) throws Exception {
		return IAMUserUtil.getUserForEmailOrPhone(phone, appDomain, true, -1);
	}
	
	public static Map<String, Object> getUserFromEmailOrPhoneForOrg(String emailOrPhone, String appDomain) throws Exception {
		return IAMUserUtil.getUserForEmailOrPhone(emailOrPhone, appDomain, true, AccountUtil.getCurrentOrg().getOrgId());
	}
	

}
