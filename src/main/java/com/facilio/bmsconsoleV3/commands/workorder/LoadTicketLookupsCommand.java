package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadTicketLookupsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<LookupField> fetchLookupsList = new ArrayList<>();
        LookupField tenant = (LookupField) fieldsAsMap.get("tenant");
        fetchLookupsList.add(tenant);

        LookupField vendor = (LookupField) fieldsAsMap.get("vendor");
        fetchLookupsList.add(vendor);

        LookupField createdByField = (LookupField) fieldsAsMap.get("createdBy");
        fetchLookupsList.add(createdByField);

        LookupField type = (LookupField) fieldsAsMap.get("type");
        fetchLookupsList.add(type);

        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CLIENT)) {
            LookupField client = (LookupField) fieldsAsMap.get("client");
            fetchLookupsList.add(client);
        }

        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SAFETY_PLAN)) {
            LookupField safetyPlan = (LookupField) fieldsAsMap.get("safetyPlan");
            fetchLookupsList.add(safetyPlan);
        }

        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, fetchLookupsList);
        return false;
    }
}
