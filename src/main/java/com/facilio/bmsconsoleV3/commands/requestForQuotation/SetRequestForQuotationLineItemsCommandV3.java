package com.facilio.bmsconsoleV3.commands.requestForQuotation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationLineItemsContext;
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

public class SetRequestForQuotationLineItemsCommandV3  extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = Constants.getRecordIds(context).get(0);
        V3RequestForQuotationContext requestForQuotation = (V3RequestForQuotationContext) CommandUtil.getModuleData(context, FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION, id);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String lineItemModuleName = FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION_LINE_ITEMS;
        List<FacilioField> fields = modBean.getAllFields(lineItemModuleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<V3RequestForQuotationLineItemsContext> builder = new SelectRecordsBuilder<V3RequestForQuotationLineItemsContext>()
                .moduleName(lineItemModuleName)
                .select(fields)
                .beanClass(V3RequestForQuotationLineItemsContext.class)
                .andCondition(CriteriaAPI.getCondition("RFQ_ID", "requestForQuotation", String.valueOf(id), NumberOperators.EQUALS))
                .fetchSupplements(Arrays.asList((LookupField) fieldsAsMap.get("itemType"), (LookupField) fieldsAsMap.get("toolType"), (LookupField) fieldsAsMap.get("service"), (LookupField) fieldsAsMap.get("awardedTo")));
        List<V3RequestForQuotationLineItemsContext> list = builder.get();
        requestForQuotation.setRequestForQuotationLineItems(list);

        return false;
    }
}
