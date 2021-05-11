package com.facilio.bmsconsole.util;

import com.facilio.accounts.dto.IAMUser;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.iam.accounts.context.SecurityPolicy;
import com.facilio.iam.accounts.util.IAMAccountConstants;
import com.facilio.iam.accounts.util.IAMUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import lombok.var;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class SecurityPolicyAPI {
    public static long createSecurityPolicy(SecurityPolicy securityPolicy) throws Exception {
        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder();
        List<FacilioField> securityPolicyFields = IAMAccountConstants.getSecurityPolicyFields();
        insertRecordBuilder
                .fields(securityPolicyFields)
                .table("SecurityPolicies");
        Map<String, Object> prop = FieldUtil.getAsProperties(securityPolicy);
        long secPolId = insertRecordBuilder.insert(prop);

        var userIds = securityPolicy.getUsers();
        if (CollectionUtils.isEmpty(userIds)) {
            securityPolicy.setId(secPolId);
            return secPolId;
        }

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(IAMAccountConstants.getAccountsUserFields());
        List<FacilioField> fieldsToBeUpdated = new ArrayList<>();
        fieldsToBeUpdated.add(fieldMap.get("securityPolicyId"));

        for (long userId : userIds) {
            IAMUser userToBeUpdated = new IAMUser();
            userToBeUpdated.setUid(userId);
            userToBeUpdated.setSecurityPolicyId(secPolId);

            IAMUtil.getUserBean().updateUserv2(userToBeUpdated, fieldsToBeUpdated);
        }

        securityPolicy.setId(secPolId);
        return secPolId;
    }

    public static void updateSecurityPolicy(SecurityPolicy securityPolicy) throws Exception {
        List<FacilioField> securityPolicyFields = IAMAccountConstants.getSecurityPolicyFields();
        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder();
        updateRecordBuilder
                .fields(securityPolicyFields)
                .table("SecurityPolicies")
                .andCondition(CriteriaAPI.getCondition("SecurityPolicies.SECURITY_POLICY_ID", "securityPolicyId", securityPolicy.getId()+"", NumberOperators.EQUALS));

        updateRecordBuilder.update(FieldUtil.getAsProperties(securityPolicy));

        List<Long> users = securityPolicy.getUsers();

        if (CollectionUtils.isEmpty(users)) {
            return;
        }

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(IAMAccountConstants.getAccountsUserFields());

        GenericUpdateRecordBuilder userUpdateRecordBuilder = new GenericUpdateRecordBuilder();
        userUpdateRecordBuilder
                .fields(Arrays.asList(fieldMap.get("securityPolicyId")))
                .table("Account_Users")
                .andCondition(CriteriaAPI.getCondition("Account_Users.SECURITY_POLICY_ID", "securityPolicyId", securityPolicy.getId()+"", NumberOperators.EQUALS));

        Map<String, Object> prop = new HashMap<>();
        prop.put("securityPolicyId", -99L);

        userUpdateRecordBuilder.update(FieldUtil.getAsProperties(prop));
        List<FacilioField> fieldsToBeUpdated = new ArrayList<>();
        fieldsToBeUpdated.add(fieldMap.get("securityPolicyId"));

        for (long userId: users) {
            IAMUser userToBeUpdated = new IAMUser();
            userToBeUpdated.setUid(userId);
            userToBeUpdated.setSecurityPolicyId(securityPolicy.getId());

            IAMUtil.getUserBean().updateUserv2(userToBeUpdated, fieldsToBeUpdated);
        }
    }

    public static SecurityPolicy fetchSecurityPolicy(long id) throws Exception {
        List<SecurityPolicy> securityPolicies = securityPolicyList(CriteriaAPI.getCondition("SecurityPolicies.SECURITY_POLICY_ID", "secPolId", id + "", NumberOperators.EQUALS));
        return securityPolicies.get(0);
    }

    private static List<SecurityPolicy> securityPolicyList(Condition con) throws Exception {
        List<FacilioField> securityPolicyFields = IAMAccountConstants.getSecurityPolicyFields();
        Map<String, FacilioField> accountUserFieldMap = FieldFactory.getAsMap(IAMAccountConstants.getAccountsUserFields());

        List<FacilioField> fields = new ArrayList<>();
        fields.addAll(securityPolicyFields);
        fields.add(accountUserFieldMap.get("uid"));

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table("SecurityPolicies")
                .leftJoin("Account_Users")
                .on("SecurityPolicies.SECURITY_POLICY_ID = Account_Users.SECURITY_POLICY_ID")
                .andCondition(con);

        Map<Long, SecurityPolicy> securityPolicyMap = new HashMap<>();

        List<Map<String, Object>> props = selectBuilder.get();
        for (Map<String, Object> prop: props) {
            SecurityPolicy secPol = FieldUtil.getAsBeanFromMap(prop, SecurityPolicy.class);
            if (securityPolicyMap.get(secPol.getId()) == null) {
                securityPolicyMap.put(secPol.getId(), secPol);
            }

            Long uid = (Long) prop.get("uid");
            if (uid != null) {
                if (secPol.getUsers() == null) {
                    secPol.setUsers(new ArrayList<>());
                }

                secPol.getUsers().add(uid);
            }
        }

        return new ArrayList<>(securityPolicyMap.values());
    }

    public static List<SecurityPolicy> fetchAllSecurityPolicies(long orgId) throws Exception {
        return securityPolicyList(
                CriteriaAPI.getCondition("SecurityPolicies.ORGID", "orgId", orgId+"", NumberOperators.EQUALS));
    }
}
