package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.Preference;
import com.facilio.bmsconsole.context.TermsAndConditionContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.PreferenceAPI;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
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
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
        /*
        Tax Modes ==>    1 = Line Item Level
                         2 = Transaction Level
        Discount Modes => 1 = Before Tax
                          2 = After Tax
        Based on these these two preferences there will be 4 different types of calculations
       
        */
        Double totalTaxAmount = 0.0;
        Double lineItemsSubtotal = 0.0;
        Double quotationTotalCost = 0.0;
        Long taxMode = getTaxMode();
        Long discountMode = getDiscountMode();
        if (CollectionUtils.isNotEmpty(quotation.getLineItems())) {
            List<QuotationLineItemsContext> lineItems = quotation.getLineItems();
            List<Long> uniqueTaxIds = lineItems.stream().filter(lineItem -> lookupValueIsNotEmpty(lineItem.getTax())).map(lineItem -> lineItem.getTax().getId()).distinct().collect(Collectors.toList());
            List<TaxContext> taxList = getTaxesForIdList(uniqueTaxIds);
            Map<Long, Double> taxIdVsRateMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(taxList)) {
                taxList.forEach(tax -> taxIdVsRateMap.put(tax.getId(), tax.getRate()));
            }

            for (QuotationLineItemsContext lineItem : lineItems) {
                if (lineItem.getQuantity() == null || lineItem.getQuantity() < 0) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Quantity cannot be negative or empty for Line Item");
                } else if (lineItem.getUnitPrice() == null || lineItem.getUnitPrice() < 0) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Unit Price cannot be negative or empty for Line Item");
                } else {
                    Double taxRate = 0d;
                    Double taxAmount = 0d;
                    Double lineItemCost = lineItem.getQuantity() * lineItem.getUnitPrice();
                    lineItem.setCost(lineItemCost);
                    if (Objects.equals(taxMode, 1l) && Objects.equals(discountMode, 2l) && lookupValueIsNotEmpty(lineItem.getTax())) {
                        taxRate = taxIdVsRateMap.get(lineItem.getTax().getId());
                        taxAmount = taxRate * lineItem.getCost() / 100;
                    }
                    lineItem.setTaxAmount(taxAmount);
                    totalTaxAmount += taxAmount;
                    lineItemsSubtotal += lineItemCost;
                }
            }
            if (Objects.equals(discountMode, 2l) && Objects.equals(taxMode, 2l)) {
                if (lookupValueIsNotEmpty(quotation.getTax())) {
                    TaxContext tax = getTaxDetails(quotation.getTax().getId());
                    totalTaxAmount = (lineItemsSubtotal * tax.getRate()) / 100;
                    quotation.setTotalTaxAmount(totalTaxAmount);
                }
            }

            quotation.setSubTotal(lineItemsSubtotal);
            quotationTotalCost = lineItemsSubtotal + totalTaxAmount;
            if (quotation.getDiscountPercentage() != null) {
                Double discountAmount = (quotationTotalCost * quotation.getDiscountPercentage() / 100);
                quotation.setDiscountAmount(discountAmount);
                quotationTotalCost = quotationTotalCost - discountAmount;
            } else if (quotation.getDiscountAmount() != null) {
                quotationTotalCost -= quotation.getDiscountAmount();
            }

            if (Objects.equals(discountMode, 1l) && Objects.equals(taxMode, 2l)) {
                if (lookupValueIsNotEmpty(quotation.getTax())) {
                    TaxContext tax = getTaxDetails(quotation.getTax().getId());
                    totalTaxAmount = (quotationTotalCost * tax.getRate()) / 100;
                    quotation.setTotalTaxAmount(totalTaxAmount);
                    quotationTotalCost += totalTaxAmount;
                }
            }
            if (Objects.equals(discountMode, 1l) && Objects.equals(taxMode, 1l)) {
                for (QuotationLineItemsContext lineItem : quotation.getLineItems()) {
                    Double taxRate;
                    Double taxAmount = 0d;
                    if (lookupValueIsNotEmpty(lineItem.getTax())) {
                        Double relativeLineItemCost = relativeLineItemCost(lineItem, lineItemsSubtotal, quotation.getDiscountAmount());
                        taxRate = taxIdVsRateMap.get(lineItem.getTax().getId());
                        taxAmount = Math.round(((taxRate * relativeLineItemCost) / 100) * 100.0) / 100.0;
                    }
                    lineItem.setTaxAmount(taxAmount);
                    totalTaxAmount += taxAmount;
                }
                quotationTotalCost += totalTaxAmount;
            }
            quotation.setTotalTaxAmount(totalTaxAmount);
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

    private static Double relativeLineItemCost(QuotationLineItemsContext lineItem, Double subTotal,Double discountAmount) throws Exception {
        if (lineItem.getUnitPrice() != null && lineItem.getQuantity() != null) {
            Double lineItemCost = (lineItem.getUnitPrice() * lineItem.getQuantity());
            if (Objects.equals(getDiscountMode(), 1l) && subTotal != null && discountAmount != null) {
                Double subTotalAfterDiscount = subTotal - discountAmount;
                return subTotalAfterDiscount *(lineItemCost/subTotal);
            } else if (Objects.equals(getDiscountMode(), 2l)) {
                return lineItemCost;
            }
        }
        return 0d;
    }

    public static void setLineItems(QuotationContext quotation) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String lineItemModuleName = FacilioConstants.ContextNames.QUOTE_LINE_ITEMS;
        List<FacilioField> fields = modBean.getAllFields(lineItemModuleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<LookupField> fetchSupplementsList = Arrays.asList((LookupField) fieldsAsMap.get("itemType"),
                (LookupField) fieldsAsMap.get("toolType"), (LookupField) fieldsAsMap.get("tax"), (LookupField) fieldsAsMap.get("service"), (LookupField) fieldsAsMap.get("labour"));
        SelectRecordsBuilder<QuotationLineItemsContext> builder = new SelectRecordsBuilder<QuotationLineItemsContext>()
                .moduleName(lineItemModuleName)
                .select(fields)
                .beanClass(QuotationLineItemsContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("quote"), String.valueOf(quotation.getId()), NumberOperators.EQUALS))
                .fetchSupplements(fetchSupplementsList);
        List<QuotationLineItemsContext> list = builder.get();
        if (CollectionUtils.isNotEmpty(list)) {
            quotation.setLineItems(list);
        }
    }

    public static void setQuotationAssociatedTerms(QuotationContext quotation) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.QUOTE_ASSOCIATED_TERMS);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<QuotationAssociatedTermsContext> builder = new SelectRecordsBuilder<QuotationAssociatedTermsContext>()
                .module(module)
                .beanClass(QuotationAssociatedTermsContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("quote"), String.valueOf(quotation.getId()), NumberOperators.EQUALS))
                .fetchSupplement((LookupField) fieldsAsMap.get("terms"));
        List<QuotationAssociatedTermsContext> list = builder.get();
        if (CollectionUtils.isNotEmpty(list)) {
            quotation.setTermsAssociated(list);
        }
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
        List<TaxContext> taxGroupsList = taxList.stream().filter(tax -> tax.getType() != null && tax.getType() == TaxContext.Type.GROUP.getIndex()).collect(Collectors.toList());
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
                if (lookupValueIsNotEmpty(quotation.getTenant())) {
                    V3TenantContext tenant = V3TenantsAPI.getTenant(quotation.getTenant().getId());
                    location.setName(tenant.getName());
                }  else {
                    location.setName(quotation.getSubject() + "_location");
                }
                FacilioChain chain = FacilioChainFactory.updateLocationChain();
                chain.getContext().put(FacilioConstants.ContextNames.RECORD, location);
                chain.getContext().put(FacilioConstants.ContextNames.RECORD_ID, location.getId());
                chain.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(location.getId()));
                chain.execute();
            } else {
                if (lookupValueIsNotEmpty(quotation.getTenant())) {
                    V3TenantContext tenant = V3TenantsAPI.getTenant(quotation.getTenant().getId());
                    location.setName(tenant.getName());
                }  else {
                    location.setName(quotation.getSubject() + "_location");
                }
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
                term.setQuote(quotation);
            }
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.QUOTE_ASSOCIATED_TERMS);
            List<FacilioField> fields = modBean.getAllFields(module.getName());
            V3RecordAPI.addRecord(false, termsAssociated, module, fields);
        }
    }

    public static void setTaxAsInactive(TaxContext tax) throws Exception {

        TaxContext updateTaxContext = new TaxContext();
        updateTaxContext.setId(tax.getId());
        updateTaxContext.setIsActive(false);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TAX);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        V3RecordAPI.updateRecord(updateTaxContext, module, Arrays.asList(fieldsMap.get("isActive")));
    }

    public static void updateTaxGroupsOnChildUpdate(TaxContext tax, Long oldTaxId) throws Exception {

        if (tax.getType() != null && tax.getType() == TaxContext.Type.INDIVIDUAL.getIndex()) {
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

                V3RecordAPI.addRecord(false, Collections.singletonList(taxGroupsParentTax), taxModule, taxFields);

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
                V3RecordAPI.addRecord(false, taxGroups, taxGroupsModule, taxGroupFields);
            }
        }
    }

    public static void setTaxSplitUp(QuotationContext quotation) throws Exception {
        Map<Long, TaxSplitUpContext> taxSplitUp = new HashMap<>();
        Long taxMode = getTaxMode();
        if (taxMode  != null && taxMode == 1 && CollectionUtils.isNotEmpty(quotation.getLineItems())) {
            for (QuotationLineItemsContext lineItem : quotation.getLineItems()) {
                Double relativeLineItemCost = relativeLineItemCost(lineItem, quotation.getSubTotal(), quotation.getDiscountAmount());
                if (lookupValueIsNotEmpty(lineItem.getTax())) {
                    if (lineItem.getTax().getType() != null && lineItem.getTax().getType() == TaxContext.Type.INDIVIDUAL.getIndex()) {
                        Double taxAmount = getTaxAmount(relativeLineItemCost, lineItem.getTax().getRate());
                        setTaxAmountInMap(taxSplitUp, lineItem.getTax(), taxAmount);
                    } else if (lineItem.getTax().getType() != null && lineItem.getTax().getType() == TaxContext.Type.GROUP.getIndex()) {
                        List<TaxGroupContext> taxGroups = getTaxesForGroups(Collections.singletonList(lineItem.getTax().getId()));
                        for (TaxGroupContext taxGroup : taxGroups) {
                            if (lookupValueIsNotEmpty(taxGroup.getChildTax())) {
                                Double taxAmount = getTaxAmount(relativeLineItemCost, taxGroup.getChildTax().getRate());
                                setTaxAmountInMap(taxSplitUp, taxGroup.getChildTax(), taxAmount);
                            }
                        }
                    }
                }
            }
        } else if (taxMode != null && taxMode == 2 && lookupValueIsNotEmpty(quotation.getTax())) {
            if (quotation.getTax().getType() != null && quotation.getTax().getType() == TaxContext.Type.INDIVIDUAL.getIndex()) {
                setTaxAmountInMap(taxSplitUp, quotation.getTax(), quotation.getTotalTaxAmount());
            } else if (quotation.getTax().getType() != null && quotation.getTax().getType() == TaxContext.Type.GROUP.getIndex()) {
                List<TaxGroupContext> taxGroups = getTaxesForGroups(Collections.singletonList(quotation.getTax().getId()));
                Double total = quotation.getSubTotal();
                if (Objects.equals(getDiscountMode(), 1l) && quotation.getDiscountAmount() != null) {
                    total -= quotation.getDiscountAmount();
                }
                for (TaxGroupContext taxGroup : taxGroups) {
                    if (lookupValueIsNotEmpty(taxGroup.getChildTax())) {
                        Double taxAmount = getTaxAmount(total, taxGroup.getChildTax().getRate());
                        setTaxAmountInMap(taxSplitUp, taxGroup.getChildTax(), taxAmount);
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

    private static Double getTaxAmount(Double cost, Double rate) {
        if (cost != null && rate != null) {
            Double taxAmount = cost * rate / 100;
            taxAmount = Math.round(taxAmount * 100.0) / 100.0;
            return taxAmount;
        }
        return 0d;
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
        if (lookupValueIsNotEmpty(quotation.getWorkorder())) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.QUOTE);
            List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.QUOTE);
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
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Quotation Already exist for this Workorder Quotation Id " + firstRecord.getParentId());
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

    public static boolean lookupValueIsNotEmpty(ModuleBaseWithCustomFields context) {
        return context != null && context.getId() > 0;
    }

    public static Preference getTaxPref() {
        //taxApplication == 1(line item level tax), 2(transaction level tax)
        FacilioForm form = new FacilioForm();
        List<FormSection> sections = new ArrayList<FormSection>();
        FormSection formSection = new FormSection();
        formSection.setName("Tax Preference");
        List<FormField> fields = new ArrayList<FormField>();
        fields.add(new FormField("taxApplication", FacilioField.FieldDisplayType.SELECTBOX, "Apply Tax At", FormField.Required.REQUIRED, 1, 1));

        formSection.setFields(fields);
        sections.add(formSection);
        form.setSections(sections);
        form.setFields(fields);
        form.setLabelPosition(FacilioForm.LabelPosition.TOP);
        return new Preference("taxApplication", "Tax Application Preference", form, "Choose how the tax rates are to be applied.") {
            @Override
            public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
            }

            @Override
            public void disable(Long recordId, Long moduleId) throws Exception {
            }

        };

    }

    public static Preference getDiscountPref() {
        //discountApplication == 1(before tax), 2(after tax)

        FacilioForm form = new FacilioForm();
        List<FormSection> sections = new ArrayList<FormSection>();
        FormSection formSection = new FormSection();
        formSection.setName("Discount Preference");
        List<FormField> fields = new ArrayList<FormField>();
        fields.add(new FormField("discountApplication", FacilioField.FieldDisplayType.SELECTBOX, "Apply Discount Before Tax/After Tax", FormField.Required.REQUIRED, 1, 1));

        formSection.setFields(fields);
        sections.add(formSection);
        form.setSections(sections);
        form.setFields(fields);
        form.setLabelPosition(FacilioForm.LabelPosition.TOP);
        return new Preference("discountApplication", "Discount Application Preference", form, "Choose how the discounts are to be applied.") {
            @Override
            public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
            }

            @Override
            public void disable(Long recordId, Long moduleId) throws Exception {
            }

        };

    }

    public static Long getTaxMode () throws Exception {
        JSONObject orgPreference = PreferenceAPI.getEnabledOrgPreferences();
        Long taxMode = 0l;
        if (orgPreference != null && orgPreference.containsKey("taxApplication")) {
            Map<String, Object> prefMeta = (Map<String, Object>) orgPreference.get("taxApplication");
            String formDataString = (String) prefMeta.get("formData");
            JSONObject prefFormData = new JSONObject();
            if (StringUtils.isNotEmpty(formDataString)) {
                JSONParser parser = new JSONParser();
                prefFormData = (JSONObject)parser.parse(formDataString);
            }
            if (prefFormData.containsKey("taxApplication") && prefFormData.get("taxApplication") != null) {
                taxMode = (Long) prefFormData.get("taxApplication");
            }
        }
        return taxMode;
    }

    public static Long getDiscountMode () throws Exception {
        JSONObject orgPreference = PreferenceAPI.getEnabledOrgPreferences();
        Long discountMode = 0l;
        if (orgPreference != null && orgPreference.containsKey("discountApplication")) {
            Map<String, Object> prefMeta = (Map<String, Object>) orgPreference.get("discountApplication");
            String formDataString = (String) prefMeta.get("formData");
            JSONObject prefFormData = new JSONObject();
            if (StringUtils.isNotEmpty(formDataString)) {
                JSONParser parser = new JSONParser();
                prefFormData = (JSONObject)parser.parse(formDataString);
            }
            if (prefFormData.containsKey("discountApplication") && prefFormData.get("discountApplication") != null) {
                discountMode = (Long) prefFormData.get("discountApplication");
            }
        }
        return discountMode;
    }
}
