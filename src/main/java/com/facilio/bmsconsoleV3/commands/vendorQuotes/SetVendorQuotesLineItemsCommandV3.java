package com.facilio.bmsconsoleV3.commands.vendorQuotes;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationLineItemsContext;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesContext;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesLineItemsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SetVendorQuotesLineItemsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = Constants.getRecordIds(context).get(0);
        V3VendorQuotesContext vendorQuotes = (V3VendorQuotesContext) CommandUtil.getModuleData(context, FacilioConstants.ContextNames.VENDOR_QUOTES, id);

        //set pending line items
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String lineItemModuleName = FacilioConstants.ContextNames.VENDOR_QUOTES_LINE_ITEMS;
        List<FacilioField> fields = modBean.getAllFields(lineItemModuleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<V3VendorQuotesLineItemsContext> builder = new SelectRecordsBuilder<V3VendorQuotesLineItemsContext>()
                .moduleName(lineItemModuleName)
                .select(fields)
                .beanClass(V3VendorQuotesLineItemsContext.class)
                .andCondition(CriteriaAPI.getCondition("VENDOR_QUOTE_ID", "vendorQuotes", String.valueOf(id), NumberOperators.EQUALS))
                .fetchSupplements(Arrays.asList((LookupField) fieldsAsMap.get("itemType"), (LookupField) fieldsAsMap.get("toolType"), (LookupField) fieldsAsMap.get("service")));
        List<V3VendorQuotesLineItemsContext> list = builder.get();
        vendorQuotes.setVendorQuotesLineItems(list);

        return false;
    }
}
