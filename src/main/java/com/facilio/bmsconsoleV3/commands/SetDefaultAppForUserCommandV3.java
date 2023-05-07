package com.facilio.bmsconsoleV3.commands;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetDefaultAppForUserCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<Long> ouIds = (List<Long>) context.get(FacilioConstants.ContextNames.ORG_USER_ID);
        Long appId = (Long) context.get(FacilioConstants.ContextNames.APPLICATION_ID);
        boolean isMobile = (boolean) context.getOrDefault(FacilioConstants.ContextNames.IS_MOBILE,false);
        boolean isWeb = (boolean) context.getOrDefault(FacilioConstants.ContextNames.IS_WEB,false);

        if(CollectionUtils.isEmpty(ouIds)){
            throw new IllegalArgumentException("User(s) cannot be empty");
        }
        if(appId == null){
            throw new IllegalArgumentException("Invalid app id");
        }

        for(Long ouId : ouIds){

            GenericUpdateRecordBuilder trueBuilder =  getUpdateBuilder(ouId,appId,NumberOperators.EQUALS);
            Map<String, Object> props = new HashMap<>();
            if(isMobile) {
                props.put("isDefaultMobileApp", true);
            } else if(isWeb){
                props.put("isDefaultApp", true);
            } else {
                props.put("isDefaultMobileApp", true);
                props.put("isDefaultApp", true);
            }
            trueBuilder.update(props);

            GenericUpdateRecordBuilder falseBuilder =  getUpdateBuilder(ouId,appId,NumberOperators.NOT_EQUALS);
            props = new HashMap<>();
            if(isMobile) {
                props.put("isDefaultMobileApp", false);
            } else if(isWeb){
                props.put("isDefaultApp", false);
            } else {
                props.put("isDefaultMobileApp", false);
                props.put("isDefaultApp", false);
            }
            falseBuilder.update(props);
        }
        return false;
    }

    public GenericUpdateRecordBuilder getUpdateBuilder(Long ouId, Long appId, NumberOperators applicationOperator) throws Exception {
        ApplicationContext app = ApplicationApi.getApplicationForId(appId);
        List<FacilioField> fields = AccountConstants.getOrgUserAppsFields();
        fields.addAll(FieldFactory.getApplicationFields());
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("APPLICATION_ID","applicationId",String.valueOf(appId), applicationOperator));
        criteria.addAndCondition(CriteriaAPI.getCondition("ORG_USERID","ouid",String.valueOf(ouId), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition("DOMAIN_TYPE","domainType",String.valueOf(app.getDomainType()), NumberOperators.EQUALS));
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .fields(AccountConstants.getOrgUserAppsFields())
                .table("ORG_User_Apps")
                .innerJoin("Application")
                .on("Application.ID = ORG_User_Apps.APPLICATION_ID")
                .andCriteria(criteria);
        return builder;
    }
}
