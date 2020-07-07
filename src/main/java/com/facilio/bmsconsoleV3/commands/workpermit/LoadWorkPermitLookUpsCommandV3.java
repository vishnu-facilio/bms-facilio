package com.facilio.bmsconsoleV3.commands.workpermit;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadWorkPermitLookUpsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        if (fields == null) {
            fields = modBean.getAllFields(moduleName);
        }
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<LookupField> additionaLookups = new ArrayList<LookupField>();
        LookupField vendorField = (LookupField) fieldsAsMap.get("vendor");
        LookupField vendorContactField = (LookupField) fieldsAsMap.get("vendorContact");
        LookupField ticketField = (LookupField) fieldsAsMap.get("ticket");
        LookupField moduleStateField = (LookupField)fieldsAsMap.get("moduleState");
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.TENANTS)) {
            LookupField tenantField = (LookupField)fieldsAsMap.get("tenant");
            additionaLookups.add(tenantField);
        }
        LookupField requestedByField = (LookupField)fieldsAsMap.get("requestedBy");
        LookupField issuedToUserField = (LookupField)fieldsAsMap.get("issuedToUser");
        LookupField baseSpaceField = (LookupField)fieldsAsMap.get("space");
        LookupField workPermitTypeField = (LookupField)fieldsAsMap.get("workPermitType");
        LookupField peopleField = (LookupField)fieldsAsMap.get("people");

        additionaLookups.add(vendorField);
        additionaLookups.add(ticketField);
        additionaLookups.add(vendorContactField);
        additionaLookups.add(moduleStateField);
        additionaLookups.add(requestedByField);
        additionaLookups.add(issuedToUserField);
        additionaLookups.add(baseSpaceField);
        additionaLookups.add(workPermitTypeField);
        additionaLookups.add(peopleField);
        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS,additionaLookups);

        return false;
    }
}
