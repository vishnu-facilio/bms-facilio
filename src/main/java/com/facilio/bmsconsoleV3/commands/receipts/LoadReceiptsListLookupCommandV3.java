package com.facilio.bmsconsoleV3.commands.receipts;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadReceiptsListLookupCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        if (fields == null) {
            fields = modBean.getAllFields(moduleName);
        }

        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<LookupField> additionaLookups = new ArrayList<>();
        additionaLookups.add((LookupField) fieldsAsMap.get("sysCreatedBy"));
        additionaLookups.add((LookupField) fieldsAsMap.get("sysModifiedBy"));

        LookupFieldMeta lineItemField = new LookupFieldMeta((LookupField) fieldsAsMap.get("lineItem"));
        LookupField itemType = (LookupField) modBean.getField("itemType", FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);
        LookupField toolType=(LookupField) modBean.getField("toolType",FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);
        lineItemField.addChildSupplement(itemType);
        lineItemField.addChildSupplement(toolType);
        additionaLookups.add(lineItemField);

        for (FacilioField field : fields) {
            if (!field.isDefault() && field.getDataTypeEnum() == FieldType.LOOKUP) {
                additionaLookups.add((LookupField) field);
            }
        }

        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS,additionaLookups);
        return false;
    }
}
