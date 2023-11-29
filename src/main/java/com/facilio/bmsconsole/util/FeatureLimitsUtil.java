package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class FeatureLimitsUtil {
    public static Long getFeatureLimitsForOrg(String featureName) throws Exception {

        Map<String,FacilioField> fieldMap= FieldFactory.getAsMap(FieldFactory.getFeatureLimitsFields());
        GenericSelectRecordBuilder builder=new GenericSelectRecordBuilder()
                .table(ModuleFactory.getFeatureLimitsModule().getTableName())
                .select(Collections.singletonList(fieldMap.get(FacilioConstants.ContextNames.LIMIT_COUNT)))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("featureName"),featureName, StringOperators.IS));
        Map<String,Object> countMap= builder.fetchFirst();

        return (Long) countMap.get(FacilioConstants.ContextNames.LIMIT_COUNT);

    }
    public static Map<String, Long>  getFeatureNameVsLimits(Long orgId)throws Exception{

        AccountUtil.setCurrentAccount(orgId);
        if(AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == orgId) {

            Map<String, Long> featureNameVsLimits = new HashMap<>();
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFeatureLimitsFields());
            List<FacilioField> selectedFields = new ArrayList<>();
            selectedFields.add(fieldMap.get("featureName"));
            selectedFields.add(fieldMap.get("limitCount"));

            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .select(selectedFields)
                    .table(ModuleFactory.getFeatureLimitsModule().getTableName());
            List<Map<String, Object>> props = builder.get();

            if (CollectionUtils.isNotEmpty(props)) {
                for (Map<String, Object> map : props) {
                    featureNameVsLimits.put((String) map.get(FacilioConstants.ContextNames.FEATURE_NAME), (Long) map.get(FacilioConstants.ContextNames.LIMIT_COUNT));
                }
                return featureNameVsLimits;
            }

        }

     return null;
    }

    public static void updateFeatureLimits(String featureName,Long featureLimit) throws Exception{

        Map<String,Object> updateProp=new HashMap<>();
        updateProp.put(FacilioConstants.ContextNames.LIMIT_COUNT,featureLimit);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFeatureLimitsFields());

        List<FacilioField> selectedFields = new ArrayList<>();
        selectedFields.add(fieldMap.get("featureName"));
        selectedFields.add(fieldMap.get("limitCount"));
        GenericUpdateRecordBuilder updateBuilder=new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getFeatureLimitsModule().getTableName())
                .fields(Collections.singletonList(fieldMap.get("limitCount")))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("featureName"),featureName,StringOperators.IS));
        updateBuilder.update(updateProp);
    }
}
