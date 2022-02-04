package com.facilio.bmsconsoleV3.commands.purchaseorder;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.*;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadAssociatedTermsLookupCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        LookupFieldMeta termsField = new LookupFieldMeta((LookupField) fieldsAsMap.get("terms"));
        LargeTextField termsLongDescField = (LargeTextField) modBean.getField("longDesc", FacilioConstants.ContextNames.TERMS_AND_CONDITIONS);
        termsField.addChildSupplement(termsLongDescField);


        List<SupplementRecord> fetchLookupsList = new ArrayList<>();

        fetchLookupsList.add(termsField);

        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, fetchLookupsList);
        return false;
    }
}
