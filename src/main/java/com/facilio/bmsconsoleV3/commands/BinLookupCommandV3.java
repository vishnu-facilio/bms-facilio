package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.*;
import org.apache.commons.chain.Context;

import java.util.*;


public class BinLookupCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        if (fields == null) {
            fields = modBean.getAllFields(moduleName);
        }
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<SupplementRecord> additionalLookups = new ArrayList<SupplementRecord>();
        LookupField itemField = (LookupField) fieldsAsMap.get("item");
        LookupField toolField = (LookupField) fieldsAsMap.get("tool");

        additionalLookups.add(itemField);
        additionalLookups.add(toolField);


        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS,additionalLookups);
        return false;
    }
}