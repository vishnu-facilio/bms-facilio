package com.facilio.sandbox.utils;

import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.Organization;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.db.util.DBConf;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SandboxUtil {
    public static JSONObject getSandboxSignUpDataParser(Map<String, Object> dataMap) {
        JSONObject signupData = new JSONObject();
        signupData.put("name", dataMap.get("name"));
        signupData.put("email", dataMap.get("email"));
        signupData.put("cognitoId", dataMap.get("cognitoId"));
        signupData.put("domainname", dataMap.get("domainname"));
        signupData.put("isFacilioAuth", dataMap.get("isFacilioAuth"));
        signupData.put("companyname", dataMap.get("companyname"));
        signupData.put("phone", dataMap.get("phone"));
        signupData.put("timezone", dataMap.get("timezone"));
        signupData.put("language", dataMap.get("language"));
        signupData.put("orgType", Organization.OrgType.SANDBOX);
        signupData.put("locale", DBConf.getInstance().getCurrentLocale());
        signupData.put("productionOrgId", ((Number)dataMap.get("productionOrgId")).longValue());
        return signupData;
    }
    public static Organization constructSandboxOrganizationFromMap(Map<String, Object> sandboxOrgMap){
        Organization sandboxOrganization = new Organization();
        sandboxOrganization.setId( ((Number)sandboxOrgMap.get("id")).longValue());
        sandboxOrganization.setOrgId( ((Number)sandboxOrgMap.get("orgId")).longValue());
        sandboxOrganization.setName(String.valueOf(sandboxOrgMap.get("name")));
        sandboxOrganization.setProductionOrgId( ((Number)sandboxOrgMap.get("productionOrgId")).longValue());
        sandboxOrganization.setDbName( String.valueOf(sandboxOrgMap.get("dbName")));
        sandboxOrganization.setLanguage(String.valueOf(sandboxOrgMap.get("language")));
        sandboxOrganization.setDomain(String.valueOf(sandboxOrgMap.get("domain")));
        sandboxOrganization.setCountry( String.valueOf(sandboxOrgMap.get("country")));
        sandboxOrganization.setOrgType(((Number)sandboxOrgMap.get("orgType")).intValue());
        sandboxOrganization.setTimezone( String.valueOf(sandboxOrgMap.get("timezone")));
        sandboxOrganization.setCreatedTime( ((Number)sandboxOrgMap.get("createdTime")).longValue());
        return sandboxOrganization;
    }
    public static IAMUser constructSandboxIAMUserFromMap(Map<String, Object> userDataMap) {
        IAMUser iamUser = new IAMUser();
        iamUser.setId(((Number)userDataMap.get("id")).longValue());
        iamUser.setName(String.valueOf(userDataMap.get("name")));
        iamUser.setUid(((Number)userDataMap.get("uid")).longValue());
        iamUser.setOrgId(((Number)userDataMap.get("orgId")).longValue());
        iamUser.setEmail(String.valueOf(userDataMap.get("email")));
        iamUser.setUserName(String.valueOf(userDataMap.get("userName")));
        iamUser.setIamOrgUserId(((Number)userDataMap.get("iamOrgUserId")).longValue());
        iamUser.setUserVerified((Boolean)userDataMap.get("userVerified"));
        iamUser.setUserStatus((Boolean)userDataMap.get("userStatus"));
        iamUser.setLanguage(String.valueOf(userDataMap.get("language")));
        iamUser.setCountry(String.valueOf(userDataMap.get("country")));
        iamUser.setIdentifier(String.valueOf(userDataMap.get("identifier")));
        iamUser.setTimezone(String.valueOf(userDataMap.get("timezone")));
        return iamUser;
    }
    public static Map<Long, String> getComponentTypeMap()  {
        List<ComponentType> componentTypeList = ComponentType.ORDERED_COMPONENT_TYPE_LIST;
        Map<Long, String> componentTypeMap = new  TreeMap<>();

        for (ComponentType componentType : componentTypeList) {
            long order = componentType.ordinal();
            String value = componentType.getValue();
            componentTypeMap.put(order, value);
        }
        return componentTypeMap;
    }
}