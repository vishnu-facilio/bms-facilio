package com.facilio.bmsconsoleV3.commands.tooltypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class LoadToolTypesLookUpCommandV3 extends FacilioCommand {
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
        
        LookupField statusField = (LookupField) fieldsAsMap.get("status");
        LookupField categoryField = (LookupField) fieldsAsMap.get("category");
        LookupField unitField = (LookupField) fieldsAsMap.get("unit");

        additionaLookups.add(statusField);
        additionaLookups.add(categoryField);
        additionaLookups.add(unitField);

        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS,additionaLookups);
        return false;
    }

}
