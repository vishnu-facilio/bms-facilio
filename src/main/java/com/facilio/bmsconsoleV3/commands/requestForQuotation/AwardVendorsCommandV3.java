package com.facilio.bmsconsoleV3.commands.requestForQuotation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationLineItemsContext;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesLineItemsContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AwardVendorsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        V3RequestForQuotationContext requestForQuotation = (V3RequestForQuotationContext) context.get(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION);
        List<V3RequestForQuotationLineItemsContext> requestForQuotationLineItems = (List<V3RequestForQuotationLineItemsContext>) context.get(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION_LINE_ITEMS);

        if(CollectionUtils.isNotEmpty(requestForQuotationLineItems)) {
            for (V3RequestForQuotationLineItemsContext lineItem : requestForQuotationLineItems) {
                if (lineItem.getAwardedPrice() == null) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Vendor has not quoted unit price");
                }
                Double totalCost = lineItem.getQuantity() * lineItem.getAwardedPrice();

                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                String moduleName = FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION_LINE_ITEMS;
                FacilioModule module = modBean.getModule(moduleName);
                List<FacilioField> fields = modBean.getAllFields(moduleName);

                List<FacilioField> updatedFields = new ArrayList<>();
                Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
                updatedFields.add(fieldsMap.get("totalCost"));

                Map<String, Object> map = new HashMap<>();
                map.put("totalCost", totalCost);

                UpdateRecordBuilder<V3RequestForQuotationLineItemsContext> updateBuilder = new UpdateRecordBuilder<V3RequestForQuotationLineItemsContext>()
                        .module(module).fields(updatedFields)
                        .andCondition(CriteriaAPI.getIdCondition(lineItem.getId(), module));
                updateBuilder.updateViaMap(map);

                // to update awarded status in vendorQuoteLineItem
                Long vendorQuoteLineItemId = null;
                if (requestForQuotation != null && lineItem != null && lineItem.getAwardedTo() != null) {
                    vendorQuoteLineItemId = getVendorQuoteLineItem(requestForQuotation.getId(), lineItem.getId(), lineItem.getAwardedTo().getId());
                }

                if (vendorQuoteLineItemId != null) {
                    awardVendorQuoteLineItem(vendorQuoteLineItemId);
                }

                // History handling
                JSONObject info = new JSONObject();
                info.put(FacilioConstants.ContextNames.USER, requestForQuotation.getSysModifiedBy().getName());
                CommonCommandUtil.addActivityToContext(requestForQuotation.getId(), -1, RequestForQuotationActivityType.QUOTE_AWARDED, info, (FacilioContext) context.get(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION_CONTEXT));

            }
        }
        return false;
    }
    private Long getVendorQuoteLineItem(Long rfqId, Long rfqLineItemId, Long vendorId) throws Exception{

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String lineItemModuleName = FacilioConstants.ContextNames.VENDOR_QUOTES_LINE_ITEMS;
        List<FacilioField> lineItemFields = modBean.getAllFields(FacilioConstants.ContextNames.VENDOR_QUOTES_LINE_ITEMS);

        SelectRecordsBuilder<V3VendorQuotesLineItemsContext> recordsBuilder = new SelectRecordsBuilder<V3VendorQuotesLineItemsContext>()
                .moduleName(lineItemModuleName)
                .select(lineItemFields)
                .beanClass(V3VendorQuotesLineItemsContext.class)
                .innerJoin("Vendor_Quotes")
                .on("Vendor_Quotes.ID = Vendor_Quotes_LineItems.VENDOR_QUOTE_ID")
                .andCondition(CriteriaAPI.getCondition("Vendor_Quotes.RFQ_ID", "requestForQuotation", String.valueOf(rfqId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("Vendor_Quotes_LineItems.RFQ_LINE_ITEM_ID", "requestForQuotationLineItem", String.valueOf(rfqLineItemId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("Vendor_Quotes.VENDOR", "vendor", String.valueOf(vendorId), NumberOperators.EQUALS));

       V3VendorQuotesLineItemsContext vendorQuotesLineItem = recordsBuilder.fetchFirst();
       if(vendorQuotesLineItem!=null){
           return vendorQuotesLineItem.getId();
       }
      return null;
    }
    private void awardVendorQuoteLineItem(Long vendorQuoteLineItemId) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VENDOR_QUOTES_LINE_ITEMS);
        List<FacilioField> lineItemFields = modBean.getAllFields(FacilioConstants.ContextNames.VENDOR_QUOTES_LINE_ITEMS);
        List<FacilioField> updatedFields = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(lineItemFields);
        updatedFields.add(fieldsMap.get("isLineItemAwarded"));

        Map<String, Object> map = new HashMap<>();
        map.put("isLineItemAwarded", true);
        UpdateRecordBuilder<V3VendorQuotesLineItemsContext> updateBuilder = new UpdateRecordBuilder<V3VendorQuotesLineItemsContext>()
                .module(module).fields(updatedFields)
                .andCondition(CriteriaAPI.getIdCondition(vendorQuoteLineItemId, module));
        updateBuilder.updateViaMap(map);
    }
}
