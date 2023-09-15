package com.facilio.bmsconsoleV3.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.Preference;
import com.facilio.bmsconsole.context.TermsAndConditionContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.PreferenceAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsoleV3.context.BaseLineItemContext;
import com.facilio.bmsconsoleV3.context.BaseLineItemsParentModuleContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.quotation.*;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LargeTextField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.LookupFieldMeta;
import com.facilio.util.CurrencyUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.xpath.operations.Bool;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public static Double getDefaultorStoredMarkupValue(Context context, Double lineItemMarkup) throws Exception {
        QuotationSettingContext quotationSetting = (QuotationSettingContext) context.get(FacilioConstants.ContextNames.QUOTATIONSETTING);
        Double markupValue = 0.0;
        if(quotationSetting != null && quotationSetting.getMarkupdisplayMode() == 1) {
            markupValue = quotationSetting.getGlobalMarkupValue();
        }
        else if(quotationSetting != null && quotationSetting.getMarkupdisplayMode() == 2 && lineItemMarkup != null) {
            return lineItemMarkup;
        }
        else if(quotationSetting != null && quotationSetting.getMarkupDefaultValue() != null && quotationSetting.getCanShowMarkupDefaultValue()) {
            markupValue = quotationSetting.getMarkupDefaultValue().doubleValue();
        }

        return markupValue;
    }

    public static  Double getMarkupValue(BaseLineItemContext lineItem, QuotationContext quotation, Boolean isGlobalMarkup, Context context) throws Exception {
        Double globalMarkupValue = getDefaultorStoredMarkupValue(context, null);

        Double markup =  getDefaultorStoredMarkupValue(context, lineItem.getMarkup());


        if(isGlobalMarkup) {
             markup = globalMarkupValue;
        }
        Double unitPrice = lineItem.getUnitPrice();
//        Double quantity = lineItem.getQuantity();
        Double percentageValue = 0.0;
        if(markup != null && unitPrice != null) {
            Double perValue = ((markup / 100) * unitPrice);
            BigDecimal bd = new BigDecimal(perValue).setScale(2, RoundingMode.HALF_UP);
            percentageValue = bd.doubleValue();
        }
        return  percentageValue;
    }

    public static  Double getGlobalMarkupValue(BaseLineItemContext lineItem, Double markup) throws Exception {
        Double markupValue = markup != null ? markup : 0.0;

        Double unitPrice = lineItem.getUnitPrice();
        Double quantity = lineItem.getQuantity();

        Double percentageValue = 0.0;
        if(unitPrice != null) {
            Double perValue = ((markupValue / 100) * unitPrice) * quantity;
            BigDecimal bd = new BigDecimal(perValue).setScale(2, RoundingMode.HALF_UP);
            percentageValue = bd.doubleValue();
        }
        return  percentageValue;
    }

    public static  Boolean showMarkupValue(Context context,QuotationContext quotation) throws Exception {
        /*
        this menthod is used to control the markup value calcuation;
        */

        ApplicationContext app = AccountUtil.getCurrentApp();
        QuotationSettingContext quotationSetting = (QuotationSettingContext) context.get(FacilioConstants.ContextNames.QUOTATIONSETTING);
        if( app != null && quotationSetting != null) {
            if ( (app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP) || app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP))) {
                if(quotation != null && quotation.getCustomerType() != null && quotation.getCustomerType() == 4) {
                    return quotationSetting.getVendorquote();
                }
                else if(quotation != null && quotation.getCustomerType() != null && (quotation.getCustomerType() == 1 || quotation.getCustomerType() == 2)) {
                    return quotationSetting.getEnduserquote();
                }
                else if(quotationSetting.getVendorquote() || quotationSetting.getEnduserquote()) {
                    return true;
                }
            }
            else if(((app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP) || (app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP)) && quotationSetting.getAllowUserSeeMarkup()))) {
                return true;
            }
            else if(((app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP)) && quotationSetting.getVendorquote())) {
                return true;
            }
        }
        return false;
    }

    public static  Boolean getGlobalMarkupValue(Context context) throws Exception {
        /*
        this menthod is used to control global or lineitem level markupvalue
        */
        QuotationSettingContext quotationSetting = (QuotationSettingContext) context.get(FacilioConstants.ContextNames.QUOTATIONSETTING);
        if(quotationSetting != null && quotationSetting.getMarkupdisplayMode() == 1) {
            return true;
        }
        return false;
    }



    public static void lineItemsCostCalculationsForQuotation(QuotationContext record, List lineItems, Context context) throws Exception {
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
        Boolean showMarkup = showMarkupValue(context, record);
        Long taxMode = getTaxMode();
        Long discountMode = getDiscountMode();
        Boolean isGlobalMarkup = getGlobalMarkupValue(context);
        Double totalMarkup = 0.0;


        if (CollectionUtils.isNotEmpty(lineItems)) {
            List<Long> uniqueTaxIds = ((List<BaseLineItemContext>)lineItems).stream().filter(lineItem -> lookupValueIsNotEmpty(lineItem.getTax())).map(lineItem -> lineItem.getTax().getId()).distinct().collect(Collectors.toList());
            List<TaxContext> taxList = getTaxesForIdList(uniqueTaxIds);
            Map<Long, Double> taxIdVsRateMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(taxList)) {
                taxList.forEach(tax -> taxIdVsRateMap.put(tax.getId(), tax.getRate()));
            }

            for (BaseLineItemContext lineItem : (List<BaseLineItemContext>) lineItems) {
                if (lineItem.getQuantity() == null || lineItem.getQuantity() < 0) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Quantity cannot be negative or empty for Line Item");
                } else if (lineItem.getUnitPrice() == null || lineItem.getUnitPrice() < 0) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Unit Price cannot be negative or empty for Line Item");
                } else {
                    Double taxRate = 0d;
                    Double taxAmount = 0d;
                    Double lineItemCost = lineItem.getQuantity() * lineItem.getUnitPrice();
                    Double markupValue = getMarkupValue(lineItem, record, isGlobalMarkup, context);

                    if (showMarkup && markupValue != null && !isGlobalMarkup) {
                        lineItemCost = lineItem.getQuantity() * (lineItem.getUnitPrice() + markupValue);
                    }
                    else if(showMarkup && markupValue != null) {
                        /* This method used to calculate the totalmarkup value*/
                        totalMarkup += (markupValue * lineItem.getQuantity());
                    }
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
                if (lookupValueIsNotEmpty(record.getTax())) {
                    TaxContext tax = getTaxDetails(record.getTax().getId());
                    totalTaxAmount = (lineItemsSubtotal * tax.getRate()) / 100;
                    record.setTotalTaxAmount(totalTaxAmount);
                }
            }
            // set totalMarkupValueToSubTotal
            if(showMarkup && isGlobalMarkup) {
                lineItemsSubtotal += totalMarkup;
            }
            record.setSubTotal(lineItemsSubtotal);
            quotationTotalCost = lineItemsSubtotal + totalTaxAmount;
            if (record.getDiscountPercentage() != null) {
                Double discountAmount = (quotationTotalCost * record.getDiscountPercentage() / 100);
                record.setDiscountAmount(discountAmount);
                quotationTotalCost = quotationTotalCost - discountAmount;
            } else if (record.getDiscountAmount() != null) {
                quotationTotalCost -= record.getDiscountAmount();
            }

            if (Objects.equals(discountMode, 1l) && Objects.equals(taxMode, 2l)) {
                if (lookupValueIsNotEmpty(record.getTax())) {
                    TaxContext tax = getTaxDetails(record.getTax().getId());
                    totalTaxAmount = (quotationTotalCost * tax.getRate()) / 100;
                    record.setTotalTaxAmount(totalTaxAmount);
                    quotationTotalCost += totalTaxAmount;
                }
            }
            if (Objects.equals(discountMode, 1l) && Objects.equals(taxMode, 1l)) {
                for (BaseLineItemContext lineItem : (List<BaseLineItemContext>)lineItems) {
                    Double taxRate;
                    Double taxAmount = 0d;
                    if (lookupValueIsNotEmpty(lineItem.getTax())) {
                        Double relativeLineItemCost = relativeLineItemCost(lineItem.getUnitPrice(), lineItem.getQuantity(), lineItemsSubtotal, record.getDiscountAmount());
                        taxRate = taxIdVsRateMap.get(lineItem.getTax().getId());
                        taxAmount = Math.round(((taxRate * relativeLineItemCost) / 100) * 100.0) / 100.0;
                    }
                    lineItem.setTaxAmount(taxAmount);
                    totalTaxAmount += taxAmount;
                }
                quotationTotalCost += totalTaxAmount;
            }
            record.setTotalTaxAmount(totalTaxAmount);
            if(showMarkup && isGlobalMarkup) {
                record.setTotalMarkup(totalMarkup);
//                quotationTotalCost += totalMarkup;
            }

            record.setTotalCost(quotationTotalCost);

        }
    }


    public static void lineItemsCostCalculations(BaseLineItemsParentModuleContext record, List lineItems) throws Exception {
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
        if (CollectionUtils.isNotEmpty(lineItems)) {
            List<Long> uniqueTaxIds = ((List<BaseLineItemContext>)lineItems).stream().filter(lineItem -> lookupValueIsNotEmpty(lineItem.getTax())).map(lineItem -> lineItem.getTax().getId()).distinct().collect(Collectors.toList());
            List<TaxContext> taxList = getTaxesForIdList(uniqueTaxIds);
            Map<Long, Double> taxIdVsRateMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(taxList)) {
                taxList.forEach(tax -> taxIdVsRateMap.put(tax.getId(), tax.getRate()));
            }

            for (BaseLineItemContext lineItem : (List<BaseLineItemContext>) lineItems) {
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
                if (lookupValueIsNotEmpty(record.getTax())) {
                    TaxContext tax = getTaxDetails(record.getTax().getId());
                    totalTaxAmount = (lineItemsSubtotal * tax.getRate()) / 100;
                    record.setTotalTaxAmount(totalTaxAmount);
                }
            }

            record.setSubTotal(lineItemsSubtotal);
            quotationTotalCost = lineItemsSubtotal + totalTaxAmount;
            if (record.getDiscountPercentage() != null) {
                Double discountAmount = (quotationTotalCost * record.getDiscountPercentage() / 100);
                record.setDiscountAmount(discountAmount);
                quotationTotalCost = quotationTotalCost - discountAmount;
            } else if (record.getDiscountAmount() != null) {
                quotationTotalCost -= record.getDiscountAmount();
            }

            if (Objects.equals(discountMode, 1l) && Objects.equals(taxMode, 2l)) {
                if (lookupValueIsNotEmpty(record.getTax())) {
                    TaxContext tax = getTaxDetails(record.getTax().getId());
                    totalTaxAmount = (quotationTotalCost * tax.getRate()) / 100;
                    record.setTotalTaxAmount(totalTaxAmount);
                    quotationTotalCost += totalTaxAmount;
                }
            }
            if (Objects.equals(discountMode, 1l) && Objects.equals(taxMode, 1l)) {
                for (BaseLineItemContext lineItem : (List<BaseLineItemContext>)lineItems) {
                    Double taxRate;
                    Double taxAmount = 0d;
                    if (lookupValueIsNotEmpty(lineItem.getTax())) {
                        Double relativeLineItemCost = relativeLineItemCost(lineItem.getUnitPrice(), lineItem.getQuantity(), lineItemsSubtotal, record.getDiscountAmount());
                        taxRate = taxIdVsRateMap.get(lineItem.getTax().getId());
                        taxAmount = Math.round(((taxRate * relativeLineItemCost) / 100) * 100.0) / 100.0;
                    }
                    lineItem.setTaxAmount(taxAmount);
                    totalTaxAmount += taxAmount;
                }
                quotationTotalCost += totalTaxAmount;
            }
            record.setTotalTaxAmount(totalTaxAmount);
            record.setTotalCost(quotationTotalCost);
        }
    }

    public static Double relativeLineItemCost(Double unitPrice, Double quantity, Double subTotal, Double discountAmount) throws Exception {
        if (unitPrice != null && quantity != null) {
            Double lineItemCost = (unitPrice * quantity);
            if (Objects.equals(getDiscountMode(), 1l) && subTotal != null) {
                Double subTotalAfterDiscount = subTotal;
                if (discountAmount != null) {
                    subTotalAfterDiscount -= discountAmount;
                }
                return subTotalAfterDiscount * (lineItemCost / subTotal);
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

        FacilioModule lineItemModule = modBean.getModule(lineItemModuleName);
        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.MULTI_CURRENCY) && CurrencyUtil.isMultiCurrencyEnabledModule(lineItemModule)) {
            fields.addAll(FieldFactory.getCurrencyPropsFields(lineItemModule));
        }

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
            Map<String, Object> currencyInfo = CurrencyUtil.getCurrencyInfo();
            for(QuotationLineItemsContext lineItem : list){
                CurrencyUtil.checkAndFillBaseCurrencyToRecord(lineItem, currencyInfo);
            }
        }
    }

    public static void setQuotationAssociatedTerms(QuotationContext quotation) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.QUOTE_ASSOCIATED_TERMS);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        LookupFieldMeta termsField = new LookupFieldMeta((LookupField) fieldsAsMap.get("terms"));
        LargeTextField termsLongDescField = (LargeTextField) modBean.getField("longDesc", FacilioConstants.ContextNames.TERMS_AND_CONDITIONS);
        termsField.addChildSupplement(termsLongDescField);

        SelectRecordsBuilder<QuotationAssociatedTermsContext> builder = new SelectRecordsBuilder<QuotationAssociatedTermsContext>()
                .module(module)
                .beanClass(QuotationAssociatedTermsContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("quote"), String.valueOf(quotation.getId()), NumberOperators.EQUALS))
                .fetchSupplement(termsField);
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

    public static void setTaxSplitUp(BaseLineItemsParentModuleContext record, List lineItems) throws Exception {
        Map<Long, TaxSplitUpContext> taxSplitUp = new HashMap<>();
        Long taxMode = getTaxMode();
        if (taxMode  != null && taxMode == 1 && CollectionUtils.isNotEmpty(lineItems)) {
            for (BaseLineItemContext lineItem : (List<BaseLineItemContext>) lineItems) {
                Double relativeLineItemCost = relativeLineItemCost(lineItem.getUnitPrice(), lineItem.getQuantity(), record.getSubTotal(), record.getDiscountAmount());
                if (lookupValueIsNotEmpty(lineItem.getTax()) && relativeLineItemCost != 0d) {
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
        } else if (taxMode != null && taxMode == 2 && lookupValueIsNotEmpty(record.getTax())) {
            if (record.getTax().getType() != null && record.getTax().getType() == TaxContext.Type.INDIVIDUAL.getIndex()) {
                setTaxAmountInMap(taxSplitUp, record.getTax(), record.getTotalTaxAmount());
            } else if (record.getTax().getType() != null && record.getTax().getType() == TaxContext.Type.GROUP.getIndex()) {
                List<TaxGroupContext> taxGroups = getTaxesForGroups(Collections.singletonList(record.getTax().getId()));
                Double total = record.getSubTotal();
                if (Objects.equals(getDiscountMode(), 1l) && record.getDiscountAmount() != null) {
                    total -= record.getDiscountAmount();
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
                record.setTaxSplitUp(taxSplitUps);
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
        DecimalFormat df = new DecimalFormat(".00");
        return df.format(val);
    }


    public static long getQuoteCount(long workorderID) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.QUOTE);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.QUOTE);
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        SelectRecordsBuilder<QuotationContext> builder = new SelectRecordsBuilder<QuotationContext>()
                .module(module)
                .beanClass(QuotationContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("workorder"), String.valueOf(workorderID), PickListOperators.IS));
        List<QuotationContext> records = builder.get();
        return records.size();
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

    public static List<TermsAndConditionContext> fetchQuotationDefaultTerms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS);
        List<FacilioField> fields = modBean.getAllFields(module.getName());

        SelectRecordsBuilder<TermsAndConditionContext> builder = new SelectRecordsBuilder<TermsAndConditionContext>()
                .module(module)
                .beanClass(TermsAndConditionContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("DEFAULT_ON_QUOTATION", "defaultOnQuotation", String.valueOf(1), NumberOperators.EQUALS))
                ;
        ;
        List<TermsAndConditionContext> list = builder.get();


        return list;

    }

    public static void updateTermsAssociated(List<QuotationAssociatedTermsContext> associatedTerms) throws Exception {
        if(CollectionUtils.isNotEmpty(associatedTerms)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.QUOTE_ASSOCIATED_TERMS);
            List<FacilioField> fields = modBean.getAllFields(module.getName());
            RecordAPI.addRecord(false, associatedTerms, module, fields);
        }
    }

    public static QuotationSettingContext fetchQuotationSetting() throws Exception {

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
        .select(FieldFactory.getQuoteSettingFields())
        .table(ModuleFactory.getQuoteSettingModule().getTableName())
        .limit(1);


        List<Map<String, Object>> props = selectBuilder.get();
        if (props != null && !props.isEmpty()) {
            QuotationSettingContext quotationSettingDATA = FieldUtil.getAsBeanFromMap(props.get(0), QuotationSettingContext.class);

            return quotationSettingDATA;
        }
        return null;
    }
}
