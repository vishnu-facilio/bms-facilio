package com.facilio.bmsconsole.commands.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.survey.SurveyResponseContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FetchSurveyCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception{

        String moduleName = (String) context.getOrDefault("moduleName",null);
        FacilioUtil.throwIllegalArgumentException(moduleName == null ,"Module Name should not be empty.");

        long recordId = (long) context.getOrDefault("recordId",-1L);
        FacilioUtil.throwIllegalArgumentException( recordId<=0 ,"Record Id should be greater than zero.");

        ModuleBean bean = Constants.getModBean();
        FacilioModule module = bean.getModule(FacilioConstants.Survey.SURVEY_RESPONSE);
        List<FacilioField> fields  = bean.getAllFields(module.getName());

        boolean superAdmin = AccountUtil.getCurrentUser().isSuperAdmin();

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<SurveyResponseContext> builder = new SelectRecordsBuilder<SurveyResponseContext>()
                .select(fields)
                .moduleName(module.getName())
                .beanClass(SurveyResponseContext.class);
        if(!superAdmin){
            builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("assignedTo"),String.valueOf(AccountUtil.getCurrentUser().getPeopleId()), NumberOperators.EQUALS));
        }
        if(moduleName.equals(FacilioConstants.ContextNames.WORK_ORDER)){
            builder.andCondition(CriteriaAPI.getCondition(module.getTableName()+".WORKORDER_ID",module.getTableName()+".workOrderId",String.valueOf(recordId), StringOperators.IS));

        } else if(moduleName.equals(FacilioConstants.ContextNames.SERVICE_REQUEST)){
            builder.andCondition(CriteriaAPI.getCondition(module.getTableName()+".SERVICE_REQUEST_ID",module.getTableName()+".serviceRequestId",String.valueOf(recordId), StringOperators.IS));
        }

        List<SurveyResponseContext> response = builder.get();

        if(CollectionUtils.isNotEmpty(response)){

            long assignedTo = response.get(0).getAssignedTo().getId();
            long currentUser = Objects.requireNonNull(AccountUtil.getCurrentUser()).getPeopleId();

            context.put("isSurveyAvailable", superAdmin || (assignedTo == currentUser));
            context.put("isSuperAdmin", superAdmin);
            context.put("isViewAllSurvey",  response.size() > 1);
            context.put("response", response);
        }

        return false;
    }
}
