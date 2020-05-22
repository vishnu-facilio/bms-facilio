package com.facilio.bmsconsole.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.quotation.*;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
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
        if (quotation.getShippingCharges() != null) {
            quotationTotalCost += quotation.getShippingCharges();
        }
        if (quotation.getMiscellaneousCharges() != null) {
            quotationTotalCost += quotation.getMiscellaneousCharges();
        }
        if (quotation.getAdjustmentsCost() != null) {
            quotationTotalCost += quotation.getAdjustmentsCost();
        }
        if (quotation.getDiscountAmount() != null) {
            quotationTotalCost -= quotation.getDiscountAmount();
        }
        quotation.setTotalCost(quotationTotalCost);
    }

    public static void setLineItems(QuotationContext quotation) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String lineItemModuleName = FacilioConstants.ContextNames.QUOTATION_LINE_ITEMS;
        List<FacilioField> fields = modBean.getAllFields(lineItemModuleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<LookupField> fetchSupplementsList = Arrays.asList((LookupField) fieldsAsMap.get("itemType"),
                (LookupField) fieldsAsMap.get("toolType"), (LookupField) fieldsAsMap.get("tax"), (LookupField) fieldsAsMap.get("service"), (LookupField) fieldsAsMap.get("labour"));
        SelectRecordsBuilder<QuotationLineItemsContext> builder = new SelectRecordsBuilder<QuotationLineItemsContext>()
                .moduleName(lineItemModuleName)
                .select(fields)
                .beanClass(QuotationLineItemsContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("quotation"), String.valueOf(quotation.getId()), NumberOperators.EQUALS))
                .fetchSupplements(fetchSupplementsList);
        List<QuotationLineItemsContext> list = builder.get();
        quotation.setLineItems(list);
    }

    public static void setQuotationAssociatedTerms(QuotationContext quotation) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.QUOTATION_ASSOCIATED_TERMS);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<QuotationAssociatedTermsContext> builder = new SelectRecordsBuilder<QuotationAssociatedTermsContext>()
                .module(module)
                .beanClass(QuotationAssociatedTermsContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("quotation"), String.valueOf(quotation.getId()), NumberOperators.EQUALS))
                .fetchSupplement((LookupField) fieldsAsMap.get("terms"));
        List<QuotationAssociatedTermsContext> list = builder.get();
        quotation.setTermsAssociated(list);
    }

    public static List<TaxGroupContext> getTaxesForGroups(List<Long> parentTaxIds) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TAX_GROUPS);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TAX_GROUPS);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        SelectRecordsBuilder<TaxGroupContext> builder = new SelectRecordsBuilder<TaxGroupContext>()
                .module(module)
                .beanClass(TaxGroupContext.class)
                .select(fields)
                .fetchSupplement((LookupField) fieldsAsMap.get("childTax"));
        if (CollectionUtils.isNotEmpty(parentTaxIds)) {
            builder.andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("parentTax"), StringUtils.join(parentTaxIds, ","), NumberOperators.EQUALS));
        }
        List<TaxGroupContext> records = builder.get();

        return records;
    }

    public static void fillTaxDetails(List<TaxContext> taxList) throws Exception {
        List<TaxContext> taxGroupsList = taxList.stream().filter(tax -> tax.getType() == TaxContext.Type.GROUP.getIndex()).collect(Collectors.toList());
        List<Long> parentTaxIds = taxGroupsList.stream().map(TaxContext::getId).collect(Collectors.toList());
        List<TaxGroupContext> taxGroups = getTaxesForGroups(parentTaxIds);
        HashMap<Long,List<TaxContext>> parentTaxIdsVsChildTaxes = new HashMap<>();
        for (TaxGroupContext taxGroup: taxGroups) {
            if (taxGroup.getParentTax() != null) {
                if (parentTaxIdsVsChildTaxes.get(taxGroup.getParentTax().getId()) == null ) {
                    parentTaxIdsVsChildTaxes.put(taxGroup.getParentTax().getId(), new ArrayList<>());
                }
                parentTaxIdsVsChildTaxes.get(taxGroup.getParentTax().getId()).add(taxGroup.getChildTax());
            }
        }
        for (TaxContext taxGroup: taxGroupsList) {
            if (parentTaxIdsVsChildTaxes.get(taxGroup.getId()) != null) {
                taxGroup.setChildTaxes(parentTaxIdsVsChildTaxes.get(taxGroup.getId()));
            }
        }
    }

    public static void addLocation(QuotationContext quotation, LocationContext location ) throws Exception {

        FacilioContext context = new FacilioContext();
        if (location != null && location.getId() > 0) {
            context.put(FacilioConstants.ContextNames.RECORD, location);
            context.put(FacilioConstants.ContextNames.RECORD_ID, location.getId());
            context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(location.getId()));
            FacilioChain editLocation = FacilioChainFactory.updateLocationChain();
            editLocation.execute(context);
        }
        else {
            location.setName(quotation.getSubject() + "_location");
            context.put(FacilioConstants.ContextNames.RECORD, location);
            FacilioChain addLocation = FacilioChainFactory.addLocationChain();
            addLocation.execute(context);
            long locationId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
            location.setId(locationId);
        }
    }

}
