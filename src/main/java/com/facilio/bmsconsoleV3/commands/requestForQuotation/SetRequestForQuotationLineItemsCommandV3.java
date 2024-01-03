package com.facilio.bmsconsoleV3.commands.requestForQuotation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.quotation.TaxContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationLineItemsContext;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesLineItemsContext;
import com.facilio.bmsconsoleV3.util.QuotationAPI;
import com.facilio.bmsconsoleV3.util.V3InventoryUtil;
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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        setName(list);
        setTaxAmount(list);
        requestForQuotation.setRequestForQuotationLineItems(list);

        return false;
    }

    private static void setTaxAmount(List<V3RequestForQuotationLineItemsContext> lineItems) throws Exception {
        List<Long> uniqueTaxIds = lineItems.stream().filter(lineItem -> V3InventoryUtil.lookupValueIsNotEmpty(lineItem.getTax())).map(lineItem -> lineItem.getTax().getId()).distinct().collect(Collectors.toList());
        List<TaxContext> taxList = QuotationAPI.getTaxesForIdList(uniqueTaxIds);
        Map<Long, Double> taxIdVsRateMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(taxList)) {
            taxList.forEach(tax -> taxIdVsRateMap.put(tax.getId(), tax.getRate()));
        }
        if(CollectionUtils.isNotEmpty(lineItems)){
            for (V3RequestForQuotationLineItemsContext lineItem: lineItems) {
                if(!ObjectUtils.allNotNull(lineItem,lineItem.getTax(),lineItem.getQuantity(),lineItem.getAwardedPrice(),taxIdVsRateMap)){
                    continue;
                }
                Double taxRate = taxIdVsRateMap.get(lineItem.getTax().getId());
                if(taxRate == null) {
                    continue;
                }
                Double taxAmount = taxRate * (lineItem.getQuantity() * lineItem.getAwardedPrice())/100;
                lineItem.setTaxAmount(taxAmount);
            }
        }
    }

    private static void setName(List<V3RequestForQuotationLineItemsContext> lineItems) {
        if(CollectionUtils.isEmpty(lineItems)){
            return;
        }
        for (V3RequestForQuotationLineItemsContext lineItem: lineItems) {
            if(lineItem.getInventoryTypeEnum() == null){
                continue;
            }
            if(lineItem.getInventoryTypeEnum() == V3RequestForQuotationLineItemsContext.InventoryTypeRfq.ITEM){
                if(lineItem.getItemType() == null){
                    continue;
                }
                lineItem.setName(lineItem.getItemType().getName());
            }
            if(lineItem.getInventoryTypeEnum() == V3RequestForQuotationLineItemsContext.InventoryTypeRfq.TOOL){
                if(lineItem.getToolType() == null){
                    continue;
                }
                lineItem.setName(lineItem.getToolType().getName());
            }
            if(lineItem.getInventoryTypeEnum() == V3RequestForQuotationLineItemsContext.InventoryTypeRfq.SERVICE){
                if(lineItem.getService() == null){
                    continue;
                }
                lineItem.setName(lineItem.getService().getName());
            }
        }
    }
}
