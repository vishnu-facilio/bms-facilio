package com.facilio.bmsconsole.util;

import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.iam.accounts.context.SecurityPolicy;
import com.facilio.iam.accounts.util.IAMAccountConstants;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.iam.accounts.util.IAMUtil;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;

import java.util.*;
import java.util.stream.Collectors;

public class SecurityPolicyAPI {
    public static long createSecurityPolicy(SecurityPolicy securityPolicy) throws Exception {
        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder();
        List<FacilioField> securityPolicyFields = IAMAccountConstants.getSecurityPolicyFields();
        insertRecordBuilder
                .fields(securityPolicyFields)
                .table("SecurityPolicies");
        Map<String, Object> prop = FieldUtil.getAsProperties(securityPolicy);
        return insertRecordBuilder.insert(prop);
    }

    public static void updateSecurityPolicy(SecurityPolicy securityPolicy) throws Exception {
        List<FacilioField> securityPolicyFields = IAMAccountConstants.getSecurityPolicyFields();
        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder();
        updateRecordBuilder
                .fields(securityPolicyFields)
                .table("SecurityPolicies")
                .andCondition(CriteriaAPI.getCondition("SecurityPolicies.SECURITY_POLICY_ID", "securityPolicyId", securityPolicy.getId()+"", NumberOperators.EQUALS));

        updateRecordBuilder.update(FieldUtil.getAsProperties(securityPolicy));

        List<Long> userIds = new GenericSelectRecordBuilder()
                .select(IAMAccountConstants.getAccountsUserFields())
                .table(IAMAccountConstants.getAccountsUserModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("SECURITY_POLICY_ID", "securityPolicyId", securityPolicy.getId() + "", NumberOperators.EQUALS))
                .get()
                    .stream()
                    .map(i -> (long) i.get("uid"))
                    .collect(Collectors.toList());

        IAMUtil.dropUserSecurityPolicyCache(userIds);
    }

    public static SecurityPolicy fetchSecurityPolicy(long id) throws Exception {
        List<SecurityPolicy> securityPolicies = securityPolicyList(CriteriaAPI.getCondition("SecurityPolicies.SECURITY_POLICY_ID", "secPolId", id + "", NumberOperators.EQUALS));
        return securityPolicies.get(0);
    }

    private static List<SecurityPolicy> securityPolicyList(Condition con) throws Exception {
        return new GenericSelectRecordBuilder()
                    .select(IAMAccountConstants.getSecurityPolicyFields())
                    .table("SecurityPolicies")
                    .andCondition(con)
                    .get()
                        .stream()
                        .map(i -> FieldUtil.getAsBeanFromMap(i, SecurityPolicy.class))
                        .collect(Collectors.toList());
    }

    public static List<SecurityPolicy> fetchAllSecurityPolicies(long orgId) throws Exception {
        return securityPolicyList(CriteriaAPI.getCondition("SecurityPolicies.ORGID", "orgId", orgId+"", NumberOperators.EQUALS));
    }
}
