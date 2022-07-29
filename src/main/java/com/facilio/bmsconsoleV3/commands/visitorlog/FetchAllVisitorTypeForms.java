package com.facilio.bmsconsoleV3.commands.visitorlog;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.V3VisitorTypeContext;
import com.facilio.bmsconsole.context.VisitorTypeFormsContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.facilio.bmsconsoleV3.util.V3VisitorManagementAPI.getVisitorTypeFormForType;

public class FetchAllVisitorTypeForms extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long currentAppId= AccountUtil.getCurrentApp().getId();
        String moduleName= FacilioConstants.ContextNames.VISITOR_TYPE;
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3VisitorTypeContext> vTypeList = recordMap.get(moduleName);
        List<V3VisitorTypeContext> newVisitorTypeList = new ArrayList<>();
        Map<String, List<Object>> queryParams = (Map<String, List<Object>>)context.get(Constants.QUERY_PARAMS);
        if(queryParams.containsKey("parentModuleName") && queryParams.get("parentModuleName") != null && !queryParams.get("parentModuleName").isEmpty()) {
            String parentModule = (String) queryParams.get("parentModuleName").get(0);
            Boolean showAll = false;
            if (queryParams.containsKey("showAll") && queryParams.get("showAll") != null && !queryParams.get("showAll").isEmpty()) {
                showAll = Boolean.parseBoolean((String) queryParams.get("showAll").get(0));
            }
            if (queryParams.containsKey("appId") && queryParams.get("appId") != null && !queryParams.get("appId").isEmpty()) {
                currentAppId = Long.parseLong((String) queryParams.get("appId").get(0));
            }
            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder();
            selectRecordBuilder.table(ModuleFactory.getVisitorTypeFormsModule().getTableName()).
                    select(FieldFactory.getVisitorTypeFormsFields())
                    .andCondition(CriteriaAPI.getCondition("APP_ID", "appId", currentAppId + "", NumberOperators.EQUALS));
            List<Map<String, Object>> visitorTypeFormList = selectRecordBuilder.get();
            for (V3VisitorTypeContext visitorType : vTypeList) {
                VisitorTypeFormsContext visitorTypeFormContext = getVisitorTypeFormForType(visitorTypeFormList, visitorType.getId());
                if (parentModule.equals(FacilioConstants.ContextNames.VISITOR_LOG)) {
                    visitorType.setVisitorFormId(visitorTypeFormContext.getVisitorLogFormId());
                    if (visitorType.getVisitorFormId() > 0) {
                        FacilioForm visitorLogForm = FormsAPI.getFormFromDB(visitorType.getVisitorFormId());
                        visitorType.setForm(visitorLogForm);
                        visitorType.setFormEnabled(visitorTypeFormContext.isVisitorLogEnabled());
                    }
                    if (showAll) {
                        newVisitorTypeList.add(visitorType);
                    } else if (visitorType.getEnabled() && visitorType.getFormEnabled()) {
                        newVisitorTypeList.add(visitorType);
                    }
                } else if (parentModule.equals(FacilioConstants.ContextNames.INVITE_VISITOR)) {
                    visitorType.setVisitorFormId(visitorTypeFormContext.getVisitorInviteFormId());
                    if (visitorType.getVisitorFormId() > 0) {
                        FacilioForm visitorLogForm = FormsAPI.getFormFromDB(visitorType.getVisitorFormId());
                        visitorType.setForm(visitorLogForm);
                        visitorType.setFormEnabled(visitorTypeFormContext.isInviteEnabled());
                    }
                    if (showAll) {
                        newVisitorTypeList.add(visitorType);
                    } else if (visitorType.getEnabled() && visitorType.getFormEnabled()) {
                        newVisitorTypeList.add(visitorType);
                    }
                }

            }
            recordMap.put(moduleName, newVisitorTypeList);
            context.put(Constants.RECORD_MAP, recordMap);
        }

        return false;
    }
}
