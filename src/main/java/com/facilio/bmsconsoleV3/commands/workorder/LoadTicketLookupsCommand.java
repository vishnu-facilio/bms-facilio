package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadTicketLookupsCommand extends FacilioCommand {

    private static final Logger LOGGER = LogManager.getLogger(LoadTicketLookupsCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {

        LOGGER.trace("Starting LoadTicketLookupsCommand");

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<LookupField> fetchLookupsList = new ArrayList<>();


        String[] supplementFieldNames = new String[]{"type", "category", "priority", "tenant",
                "vendor", "createdBy", "resource", "status","assignmentGroup","assignedTo","assignedBy","moduleState","approvalStatus","jobPlan"};
        for (String fieldName : supplementFieldNames) {
            fetchLookupsList.add((LookupField) fieldsAsMap.get(fieldName));
        }

        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CLIENT)) {
            fetchLookupsList.add((LookupField) fieldsAsMap.get("client"));
        }

        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SAFETY_PLAN)) {
            fetchLookupsList.add((LookupField) fieldsAsMap.get("safetyPlan"));
        }
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SERVICE_REQUEST)){
            fetchLookupsList.add((LookupField) fieldsAsMap.get("serviceRequest"));
        }
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.FAILURE_CODES)){
            fetchLookupsList.add((LookupField) fieldsAsMap.get("failureClass"));
        }

        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, fetchLookupsList);
        return false;
    }
}
