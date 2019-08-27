package com.facilio.accounts.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.User;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.iam.accounts.impl.IAMUserBeanImpl;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.iam.accounts.util.IAMUtil;

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
	
	public static void setIAMUserProps(List<Map<String, Object>> actualPropsList, long orgId) throws Exception {
		if(CollectionUtils.isNotEmpty(actualPropsList)) {
			List<Map<String, Object>> finalMap = new ArrayList<Map<String,Object>>();
			List<Long> userIds = new ArrayList<Long>();
			for(Map<String, Object> map : actualPropsList) {
				userIds.add((long)map.get("uid"));
			}
			Map<Long, Map<String, Object>> iamUserMap = getIAMUserProps(userIds, orgId);
			for(Map<String, Object> map : actualPropsList) {
				long uId = (long)map.get("uid");
				if(iamUserMap.containsKey(uId)) {
					map.putAll(iamUserMap.get(uId));
					finalMap.add(map);
				}
			}
			actualPropsList.clear();
			actualPropsList.addAll(finalMap);
		}
	}
	
	public static Map<String, Object> getUserFromEmailOrPhone(String emailOrPhone, String portalDomain) throws Exception {
		
		Criteria userEmailCriteria = new Criteria();
		userEmailCriteria.addAndCondition(CriteriaAPI.getCondition("Account_Users.EMAIL", "email", emailOrPhone, StringOperators.IS));
		userEmailCriteria.addOrCondition(CriteriaAPI.getCondition("Account_Users.MOBILE", "mobile", emailOrPhone, StringOperators.IS));
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("DOMAIN_NAME", "domainName", portalDomain, StringOperators.IS));
		
		Criteria finalCriteira = new Criteria();
		finalCriteira.andCriteria(userEmailCriteria);
		
		Map<Long, Map<String, Object>> userMap = IAMUserUtil.getIAMOrgUserData(finalCriteira, AccountUtil.getCurrentOrg().getOrgId());
		if(userMap != null) {
			return userMap.values().stream().findFirst().get();
		}
		return null;
		
	}
	public static Map<Long, Map<String, Object>> getIAMUserProps(List<Long> userIds, long orgId) throws Exception {
		Map<Long, Map<String, Object>> iamUserMap = IAMUserUtil.getUserData(userIds, orgId);
		return iamUserMap;
	}
	
	public static Map<Long, Map<String, Object>> getIAMUsersMapForCurrentOrg() throws Exception {
		return IAMUserUtil.getIAMOrgUserData(null, AccountUtil.getCurrentOrg().getOrgId());
		
	}
	
	public static Map<String, Object> getUserFromPhone(String phone) throws Exception {
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(AccountUtil.getCurrentOrg().getId()), NumberOperators.EQUALS));
		criteria.addAndCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", String.valueOf(-1), NumberOperators.EQUALS));
		criteria.addAndCondition(CriteriaAPI.getCondition("USER_STATUS", "userStatus", "1", NumberOperators.EQUALS));
		
		Criteria criteria2 = new Criteria();
		criteria2.addAndCondition(CriteriaAPI.getCondition("PHONE", "phone", phone, StringOperators.IS));
		criteria2.addOrCondition(CriteriaAPI.getCondition("MOBILE", "mobile", phone, StringOperators.IS));
		
		Criteria finalCriteria = new Criteria();
		finalCriteria.andCriteria(criteria);
		finalCriteria.andCriteria(criteria2);
		Map<Long, Map<String, Object>> userMap = IAMUtil.getUserBean().getUserData(finalCriteria, AccountUtil.getCurrentOrg().getOrgId());
		if(userMap != null) {
			return userMap.values().stream().findFirst().get();
		}
		return null;
	}
	
	public static Map<String, Object> getUserFromEmailOrPhoneForOrg(String emailOrPhone) throws Exception {
		Criteria finalCriteira = new Criteria();
		Criteria userEmailCriteria = new Criteria();
		userEmailCriteria.addAndCondition(CriteriaAPI.getCondition("Account_Users.EMAIL", "email", emailOrPhone, StringOperators.IS));
		userEmailCriteria.addOrCondition(CriteriaAPI.getCondition("Account_Users.MOBILE", "mobile", emailOrPhone, StringOperators.IS));
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("USER_STATUS", "userStatus", "1", NumberOperators.EQUALS));
		finalCriteira.andCriteria(userEmailCriteria);
		finalCriteira.andCriteria(criteria);
		
		Map<Long, Map<String, Object>> userMap = IAMUserUtil.getIAMOrgUserData(finalCriteira, AccountUtil.getCurrentOrg().getOrgId());
		if(userMap != null) {
			return userMap.values().stream().findFirst().get();
		}
		return null;
	}
	

}
