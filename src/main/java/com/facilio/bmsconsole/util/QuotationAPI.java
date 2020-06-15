package com.facilio.bmsconsole.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.TermsAndConditionContext;
import com.facilio.bmsconsoleV3.context.quotation.*;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
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

    public static TaxContext getTaxDetails(Long taxId) throws Exception {
        List<TaxContext> taxList = getTaxesForIdList(Collections.singletonList(taxId));
        if (CollectionUtils.isNotEmpty(taxList)) {
            return taxList.get(0);
        }
        return null;
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
                    Double taxRate = 0d;
                    Double taxAmount = 0d;
                    lineItem.setCost(lineItemCost);
                    if (lineItem.getTax() != null) {
                        taxRate = taxIdVsRateMap.get(lineItem.getTax().getId());
                        taxAmount = taxRate * lineItem.getCost() / 100;
                    }
                    lineItem.setTaxAmount(taxAmount);
                    totalTaxAmount += taxAmount;
                    lineItemsSubtotal += lineItemCost;
                }
            }
        }
        quotation.setSubTotal(lineItemsSubtotal);
        quotation.setTotalTaxAmount(totalTaxAmount);
        Double quotationTotalCost = lineItemsSubtotal + totalTaxAmount;
        if (quotation.getDiscountPercentage() != null) {
            Double discountAmount = (quotationTotalCost * quotation.getDiscountPercentage() / 100);
            quotation.setDiscountAmount(discountAmount);
            quotationTotalCost = quotationTotalCost - discountAmount;
        }
        else if (quotation.getDiscountAmount() != null) {
            quotationTotalCost -= quotation.getDiscountAmount();
        }
        if (quotation.getShippingCharges() != null) {
            quotationTotalCost += quotation.getShippingCharges();
        }
        if (quotation.getMiscellaneousCharges() != null) {
            quotationTotalCost += quotation.getMiscellaneousCharges();
        }
        if (quotation.getAdjustmentsCost() != null) {
            quotationTotalCost += quotation.getAdjustmentsCost();
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

    public static List<TaxGroupContext> getTaxGroupsForChildTax(List<Long> childTaxIds) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TAX_GROUPS);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TAX_GROUPS);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        SelectRecordsBuilder<TaxGroupContext> builder = new SelectRecordsBuilder<TaxGroupContext>()
                .module(module)
                .beanClass(TaxGroupContext.class)
                .select(fields)
                .fetchSupplement((LookupField) fieldsAsMap.get("childTax"));
        if (CollectionUtils.isNotEmpty(childTaxIds)) {
            builder.andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("childTax"), StringUtils.join(childTaxIds, ","), NumberOperators.EQUALS));
        }
        List<TaxGroupContext> records = builder.get();

        return records;
    }

    public static void fillTaxDetails(List<TaxContext> taxList) throws Exception {
        List<TaxContext> taxGroupsList = taxList.stream().filter(tax -> tax.getType() == TaxContext.Type.GROUP.getIndex()).collect(Collectors.toList());
        List<Long> parentTaxIds = taxGroupsList.stream().map(TaxContext::getId).collect(Collectors.toList());
        List<TaxGroupContext> taxGroups = getTaxesForGroups(parentTaxIds);
        HashMap<Long, List<TaxContext>> parentTaxIdsVsChildTaxes = new HashMap<>();
        for (TaxGroupContext taxGroup : taxGroups) {
            if (taxGroup.getParentTax() != null) {
                if (parentTaxIdsVsChildTaxes.get(taxGroup.getParentTax().getId()) == null) {
                    parentTaxIdsVsChildTaxes.put(taxGroup.getParentTax().getId(), new ArrayList<>());
                }
                parentTaxIdsVsChildTaxes.get(taxGroup.getParentTax().getId()).add(taxGroup.getChildTax());
            }
        }
        for (TaxContext taxGroup : taxGroupsList) {
            if (parentTaxIdsVsChildTaxes.get(taxGroup.getId()) != null) {
                taxGroup.setChildTaxes(parentTaxIdsVsChildTaxes.get(taxGroup.getId()));
            }
        }
    }

    public static void addLocation(QuotationContext quotation, LocationContext location) throws Exception {

        if (location != null) {
            if (location.getId() > 0) {
                FacilioChain chain = FacilioChainFactory.updateLocationChain();
                chain.getContext().put(FacilioConstants.ContextNames.RECORD, location);
                chain.getContext().put(FacilioConstants.ContextNames.RECORD_ID, location.getId());
                chain.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(location.getId()));
                chain.execute();
            } else {
                location.setName(quotation.getSubject() + "_location");
                FacilioChain addLocation = FacilioChainFactory.addLocationChain();
                addLocation.getContext().put(FacilioConstants.ContextNames.RECORD, location);
                addLocation.execute();
                long locationId = (long) addLocation.getContext().get(FacilioConstants.ContextNames.RECORD_ID);
                location.setId(locationId);
            }
        }
    }

    public static void addQuotationTerms(Long id, List<QuotationAssociatedTermsContext> termsAssociated) throws Exception {
        if (CollectionUtils.isNotEmpty(termsAssociated)) {
            QuotationContext quotation = new QuotationContext();
            quotation.setId(id);
            for (QuotationAssociatedTermsContext term : termsAssociated) {
                term.setQuotation(quotation);
            }
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.QUOTATION_ASSOCIATED_TERMS);
            List<FacilioField> fields = modBean.getAllFields(module.getName());
            RecordAPI.addRecord(false, termsAssociated, module, fields);
        }
    }

    public static void setTaxAsInactive(TaxContext tax) throws Exception {

        TaxContext updateTaxContext = new TaxContext();
        updateTaxContext.setId(tax.getId());
        updateTaxContext.setIsActive(false);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TAX);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        RecordAPI.updateRecord(updateTaxContext, module, fields);
    }

    public static void updateTaxGroupsOnChildUpdate(TaxContext tax, Long oldTaxId) throws Exception {

        if (tax.getType() == TaxContext.Type.INDIVIDUAL.getIndex()) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule taxModule = modBean.getModule(FacilioConstants.ContextNames.TAX);
            List<FacilioField> taxFields = modBean.getAllFields(taxModule.getName());
            List<TaxGroupContext> taxGroupsForChildTax = getTaxGroupsForChildTax(Collections.singletonList(oldTaxId));
            for (TaxGroupContext childTaxGroup : taxGroupsForChildTax) {
                TaxContext taxGroupsParentTax = getTaxDetails(childTaxGroup.getParentTax().getId());
                setTaxAsInactive(taxGroupsParentTax);
                Long oldTaxGroupId = taxGroupsParentTax.getId();
                List<TaxGroupContext> taxGroupsForParentTax = getTaxesForGroups(Collections.singletonList(oldTaxGroupId));
                taxGroupsParentTax.setId(-1);

                Double rate = 0d;
                for (TaxGroupContext tg : taxGroupsForParentTax) {
                    if (tg.getChildTax().getId() == oldTaxId) {
                        tg.setChildTax(tax);
                        rate += tax.getRate();
                    } else {
                        rate += tg.getChildTax().getRate();
                    }
                }
                taxGroupsParentTax.setRate(rate);

                RecordAPI.addRecord(false, Collections.singletonList(taxGroupsParentTax), taxModule, taxFields);

                List<TaxGroupContext> taxGroups = new ArrayList<>();
                FacilioModule taxGroupsModule = modBean.getModule(FacilioConstants.ContextNames.TAX_GROUPS);
                List<FacilioField> taxGroupFields = modBean.getAllFields(FacilioConstants.ContextNames.TAX_GROUPS);
                if (CollectionUtils.isNotEmpty(taxGroupsForParentTax)) {
                    for (TaxGroupContext childTaxG2 : taxGroupsForParentTax) {
                        TaxGroupContext taxGroup = new TaxGroupContext();
                        taxGroup.setParentTax(taxGroupsParentTax);
                        if (childTaxG2.getChildTax().getId() == oldTaxId) {
                            taxGroup.setChildTax(tax);
                        } else {
                            taxGroup.setChildTax(childTaxG2.getChildTax());
                        }
                        taxGroups.add(taxGroup);
                    }
                }
                RecordAPI.addRecord(false, taxGroups, taxGroupsModule, taxGroupFields);
            }
        }
    }

    public static void setTaxSplitUp(QuotationContext quotation) throws Exception {
        Map<Long, TaxSplitUpContext> taxSplitUp = new HashMap<>();
        if (CollectionUtils.isNotEmpty(quotation.getLineItems())) {
            for (QuotationLineItemsContext lineItem : quotation.getLineItems()) {
                if (lineItem.getTax() != null) {
                    if (lineItem.getTax().getType() == TaxContext.Type.INDIVIDUAL.getIndex()) {
                        Double taxAmount = getTaxAmount(lineItem, lineItem.getTax().getRate());
                        setTaxAmountInMap(taxSplitUp, lineItem.getTax(), taxAmount);
                    } else if (lineItem.getTax().getType() == TaxContext.Type.GROUP.getIndex()) {
                        List<TaxGroupContext> taxGroups = getTaxesForGroups(Collections.singletonList(lineItem.getTax().getId()));
                        for (TaxGroupContext taxGroup : taxGroups) {
                            if (taxGroup.getChildTax() != null) {
                                Double taxAmount = getTaxAmount(lineItem, taxGroup.getChildTax().getRate());
                                setTaxAmountInMap(taxSplitUp, taxGroup.getChildTax(), taxAmount);
                            }
                        }
                    }
                }
            }
        }
        if (MapUtils.isNotEmpty(taxSplitUp)) {
            List<TaxSplitUpContext> taxSplitUps = new ArrayList<>(taxSplitUp.values());
            if (CollectionUtils.isNotEmpty(taxSplitUps)) {
                quotation.setTaxSplitUp(taxSplitUps);
            }
        }
    }

    private static Double getTaxAmount(QuotationLineItemsContext lineItem, Double rate) {
        Double taxAmount = lineItem.getCost() * rate / 100;
        taxAmount = Math.round(taxAmount * 100.0) / 100.0;
        return taxAmount;
    }

    private static void setTaxAmountInMap(Map<Long, TaxSplitUpContext> taxSplitUp, TaxContext tax, Double taxAmount) {

        if (taxSplitUp.containsKey(tax.getId())) {
            TaxSplitUpContext prevContext = taxSplitUp.get(tax.getId());
            Double prevAmount = prevContext.getTaxAmount();
            taxAmount += prevAmount;
            prevContext.setTaxAmount(taxAmount);
            taxSplitUp.put(tax.getId(), prevContext);
        } else {
            TaxSplitUpContext newContext = new TaxSplitUpContext();
            newContext.setTaxAmount(taxAmount);
            newContext.setTax(tax);
            taxSplitUp.put(tax.getId(), newContext);
        }
    }

    public static String formatDecimal(Double val) {
        DecimalFormat df =new DecimalFormat(".00");
        return df.format(val);
    }

    public static void validateForWorkorder(QuotationContext quotation) throws Exception {
        // Considering one Quotation for a Workorder.
        if (quotation.getWorkorder() != null && quotation.getWorkorder().getId() > 0) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.QUOTATION);
            List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.QUOTATION);
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
            SelectRecordsBuilder<QuotationContext> builder = new SelectRecordsBuilder<QuotationContext>()
                    .module(module)
                    .beanClass(QuotationContext.class)
                    .select(fields)
                    .andCondition(CriteriaAPI.getCondition(fieldsMap.get("workorder"), String.valueOf(quotation.getWorkorder().getId()), PickListOperators.IS))
                    .andCondition(CriteriaAPI.getCondition(fieldsMap.get("isQuotationRevised"), String.valueOf(false) ,BooleanOperators.IS));
            List<QuotationContext> records = builder.get();
            if (CollectionUtils.isNotEmpty(records)) {
                QuotationContext firstRecord = records.get(0);
                if (quotation.getId() < 0 || (/* To allow update */ firstRecord.getId() != quotation.getId())) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Quotation Already exist for this Workorder Quotation Id " + firstRecord.getId());
                }
            }
        }
    }
    public static List<TermsAndConditionContext> getTermsAndConditionsForIdList(List<Long> idsList) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS);
        SelectRecordsBuilder<TermsAndConditionContext> builder = new SelectRecordsBuilder<TermsAndConditionContext>()
                .module(module)
                .beanClass(TermsAndConditionContext.class)
                .select(fields);
        if (CollectionUtils.isNotEmpty(idsList)) {
            builder.andCondition(CriteriaAPI.getIdCondition(idsList, module));
        }
        List<TermsAndConditionContext> records = builder.get();

        return records;
    }
}
