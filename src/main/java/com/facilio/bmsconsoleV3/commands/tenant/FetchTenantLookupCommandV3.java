package com.facilio.bmsconsoleV3.commands.tenant;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class FetchTenantLookupCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> tenantFields = modBean.getAllFields(FacilioConstants.ContextNames.TENANT);
        List<LookupField> lookupFields = new ArrayList<>();
        for (FacilioField f : tenantFields) {
            if (f instanceof LookupField) {
                lookupFields.add((LookupField) f);
            }
        }
        if(CollectionUtils.isEmpty(lookupFields)) {
            context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, lookupFields);
        }

        return false;
    }
}
