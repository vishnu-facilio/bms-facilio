package com.facilio.bmsconsoleV3.commands.vendor;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.LoadVendorLookUpCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadVendorLookupCommandV3 extends FacilioCommand {
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
        LookupField contactField = (LookupField) fieldsAsMap.get("registeredBy");
        LookupField moduleStateField = (LookupField) fieldsAsMap.get("moduleState");
        LookupField addressField = (LookupField) fieldsAsMap.get("address");
        additionaLookups.add(contactField);
        additionaLookups.add(addressField);
        additionaLookups.add(moduleStateField);
        for (FacilioField f : fields) {
            if (!f.isDefault() && f.getDataTypeEnum() == FieldType.LOOKUP) {
                additionaLookups.add((LookupField) f);
            }
        }
        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS,additionaLookups);
        return false;
    }
}
