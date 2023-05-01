package com.facilio.bmsconsoleV3.commands.item;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
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

public class LoadItemTransactionsLookupCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if (fields == null) {
            fields = modBean.getAllFields(moduleName);
        }

        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<LookupField> additionalLookup = new ArrayList<LookupField>();
        additionalLookup.add((LookupField) fieldsAsMap.get("storeRoom"));
        additionalLookup.add((LookupField) fieldsAsMap.get("itemType"));
        additionalLookup.add((LookupField) fieldsAsMap.get("item"));
        additionalLookup.add((LookupField) fieldsAsMap.get("issuedTo"));
        additionalLookup.add((LookupField) fieldsAsMap.get("workorder"));

        for (FacilioField field : fields) {
            if (!field.isDefault() && field.getDataTypeEnum() == FieldType.LOOKUP) {
                additionalLookup.add((LookupField) field);
            }
        }

        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS,additionalLookup);
        return false;
    }
}
