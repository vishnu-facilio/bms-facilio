package com.facilio.bmsconsoleV3.commands.vendorQuotes;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.LookupFieldMeta;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadVendorQuoteLineItemsCommandV3  extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if (fields == null) {
            fields = modBean.getAllFields(moduleName);
        }

        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        LookupFieldMeta vendorQuotesLookup = new LookupFieldMeta((LookupField) fieldsAsMap.get("vendorQuotes"));
        LookupField vendorLookup =(LookupField) modBean.getField("vendor", FacilioConstants.ContextNames.VENDOR_QUOTES);
        vendorQuotesLookup.addChildSupplement(vendorLookup);

        List<LookupField> additionaLookups = new ArrayList<>();
        additionaLookups.add((LookupField) fieldsAsMap.get("itemType"));
        additionaLookups.add((LookupField) fieldsAsMap.get("toolType"));
        additionaLookups.add((LookupField) fieldsAsMap.get("service"));
        additionaLookups.add((LookupField) vendorQuotesLookup);
        for (FacilioField field : fields) {
            if (!field.isDefault() && field.getDataTypeEnum() == FieldType.LOOKUP) {
                additionaLookups.add((LookupField) field);
            }
        }

        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, additionaLookups);
        return false;
    }
}
