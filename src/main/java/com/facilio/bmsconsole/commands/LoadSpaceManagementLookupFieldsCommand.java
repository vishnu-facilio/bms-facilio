package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadSpaceManagementLookupFieldsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        List<FacilioField>fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        if (fields == null) {
            fields = modBean.getAllFields(moduleName);
        }
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<LookupField> additionalLookups = new ArrayList<LookupField>();
        LookupField locationField = (LookupField) fieldsAsMap.get("location");
        LookupField failureClassField = (LookupField) fieldsAsMap.get("failureClass");
        if (locationField != null) {
            additionalLookups.add(locationField);
        }
        if(failureClassField != null){
            additionalLookups.add(failureClassField);
        }

        context.put(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST,additionalLookups);
        return false;
    }

}
