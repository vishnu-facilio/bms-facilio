package com.facilio.bmsconsole.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.quotation.QuotationContext;
import com.facilio.bmsconsole.context.quotation.QuotationLineItemsContext;
import com.facilio.bmsconsole.context.quotation.TaxContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QuotationAPI {

    public static List<TaxContext> getTaxesForIdList(List<Long> taxIds) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TAX);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TAX);
        SelectRecordsBuilder<TaxContext> builder = new SelectRecordsBuilder<TaxContext>()
                .module(module)
                .beanClass(TaxContext.class)
                .select(fields);
        if (CollectionUtils.isNotEmpty(taxIds)) {
            builder.andCondition(CriteriaAPI.getIdCondition(taxIds, module));
        }
        List<TaxContext> records = builder.get();

        return records;
    }

    public static void calculateQuotationCost(QuotationContext quotation) throws Exception {
        /* Calculates the Total Cost and Tax Amount for Line Items
            Line item Cost = Unit Price * Qty
            Tax Amount = Total Cost * Tax Rate(%)
            Total Cost = LineItemCostTotal + Tax Amount + Shipping + Misc charges + Adjustments - Discount Amount
        */
        Double totalTaxAmount = 0.0;
        Double lineItemsSubtotal = 0.0;
        if (CollectionUtils.isNotEmpty(quotation.getLineItems())) {
            List<QuotationLineItemsContext> lineItems = quotation.getLineItems();
            List<Long> uniqueTaxIds = lineItems.stream().filter(lineItem -> lineItem.getTax() != null).map(lineItem -> lineItem.getTax().getId()).distinct().collect(Collectors.toList());
            List<TaxContext> taxList = getTaxesForIdList(uniqueTaxIds);
            Map<Long, Double> taxIdVsRateMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(taxList)) {
                taxList.forEach(tax -> taxIdVsRateMap.put(tax.getId(), tax.getRate()));
            }
            for (QuotationLineItemsContext lineItem : lineItems) {
                if (lineItem.getQuantity() < 0) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Quantity cannot be negative for Line Item");
                } else {
                    Double lineItemCost = lineItem.getQuantity() * lineItem.getUnitPrice();
                    lineItem.setCost(lineItemCost);
                    if (lineItem.getTax() != null) {
                        Double taxRate = taxIdVsRateMap.get(lineItem.getTax().getId());
                        Double taxAmount = taxRate * lineItem.getCost() / 100;
                        lineItem.setTaxAmount(taxAmount);
                        totalTaxAmount += taxAmount;
                        lineItemsSubtotal += lineItemCost;
                    }
                }
            }
        }
        quotation.setSubTotal(lineItemsSubtotal);
        quotation.setTotalTaxAmount(totalTaxAmount);
        Double quotationTotalCost = lineItemsSubtotal + totalTaxAmount;
        if (quotation.getShippingCharges() != -1) {
            quotationTotalCost += quotation.getShippingCharges();
        }
        if (quotation.getMiscellaneousCharges() != -1) {
            quotationTotalCost += quotation.getMiscellaneousCharges();
        }
        if (quotation.getAdjustmentsCost() != -1) {
            quotationTotalCost += quotation.getAdjustmentsCost();
        }
        if (quotation.getDiscountAmount() != -1) {
            quotationTotalCost -= quotation.getDiscountAmount();
        }
        quotation.setTotalCost(quotationTotalCost);
    }

}
