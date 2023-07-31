package com.facilio.bmsconsoleV3.commands.quotation;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.activity.ActivityContext;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.QuotationActivityType;
import com.facilio.bmsconsole.context.CurrencyContext;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsoleV3.context.quotation.QuotationAssociatedTermsContext;
import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.bmsconsoleV3.context.quotation.QuotationLineItemsContext;
import com.facilio.bmsconsoleV3.util.QuotationAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.CurrencyUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InsertQuotationLineItemsAndActivitiesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        // For Setting Quotation Id in Line Items post save
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<QuotationContext> list = recordMap.get(moduleName);
        Long quotationId = (Long) context.get(Constants.RECORD_ID);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if (CollectionUtils.isNotEmpty(list)) {
            for (QuotationContext quotation : list) {
                if (CollectionUtils.isNotEmpty(quotation.getLineItems())) {
                    FacilioModule lineItemModule = modBean.getModule(FacilioConstants.ContextNames.QUOTE_LINE_ITEMS);
                    DeleteRecordBuilder<QuotationLineItemsContext> deleteBuilder = new DeleteRecordBuilder<QuotationLineItemsContext>()
                            .module(lineItemModule)
                            .andCondition(CriteriaAPI.getCondition("QUOTATION_ID", "quotation", String.valueOf(quotation.getId()), NumberOperators.EQUALS));
                    deleteBuilder.delete();
                    QuotationContext quotationContext = new QuotationContext();
                    quotationContext.setId(quotation.getId());
                    List<FacilioField> lineItemFields = modBean.getAllFields(lineItemModule.getName());

                    if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.MULTI_CURRENCY)) {
                        CurrencyContext baseCurrency = Constants.getBaseCurrency(context);
                        Map<String, CurrencyContext> currencyCodeVsCurrency = Constants.getCurrencyMap(context);
                        String parentRecordCurrencyCode = quotation.getCurrencyCode();
                        Double parentRecordExchangeRate = quotation.getExchangeRate();
                        lineItemFields.addAll(FieldFactory.getCurrencyPropsFields(lineItemModule));
                        for (QuotationLineItemsContext lineItem : quotation.getLineItems()) {
                            CurrencyUtil.setCurrencyCodeAndExchangeRateForWrite(lineItem, baseCurrency, currencyCodeVsCurrency, parentRecordCurrencyCode, parentRecordExchangeRate);
                        }
                    }

                    for (QuotationLineItemsContext lineItem : quotation.getLineItems()) {
                        lineItem.setQuote(quotationContext);
                    }

                    V3RecordAPI.addRecord(false, quotation.getLineItems(), lineItemModule, lineItemFields);
                }

                Map<String, Object> bodyParams = Constants.getBodyParams(context);
                if (MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("revise")) {
                    if (CollectionUtils.isNotEmpty(quotation.getTermsAssociated())) {
                        List<QuotationAssociatedTermsContext> terms = quotation.getTermsAssociated();
                        QuotationAPI.addQuotationTerms(quotation.getId(), terms);
                    }
                    Long oldRecordId = (Long) context.get(FacilioConstants.ContextNames.OLD_RECORD_ID);
                    FacilioChain activitiesChain = ReadOnlyChainFactory.getActivitiesChain();
                    activitiesChain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.QUOTE_ACTIVITY);
                    activitiesChain.getContext().put(FacilioConstants.ContextNames.PARENT_ID, oldRecordId);
                    activitiesChain.execute();
                    List<ActivityContext> activities = (List<ActivityContext>) activitiesChain.getContext().get(FacilioConstants.ContextNames.RECORD_LIST);
                    for (ActivityContext activity : activities) {
                        activity.setId(-1);
                        activity.setParentId(quotation.getId());
                    }
                    List<ActivityContext> activitiesList = (List<ActivityContext>) context.get(FacilioConstants.ContextNames.ACTIVITY_LIST);
                    if (activitiesList == null) {
                        activitiesList = new ArrayList<>();
                    }
                    activitiesList.addAll(activities);
                    context.put(FacilioConstants.ContextNames.ACTIVITY_LIST, activitiesList);
                    JSONObject info = new JSONObject();
                    info.put("quotationId", oldRecordId);
                    CommonCommandUtil.addActivityToContext(quotation.getId(), -1, QuotationActivityType.REVISE_QUOTATION, info, (FacilioContext) context);
                } else if (quotationId != null && quotationId > 0) {
                    List<UpdateChangeSet> updatedSet = Constants.getRecordChangeSets(context, quotationId);
                    if (CollectionUtils.isNotEmpty(updatedSet)) {
                        FacilioField moduleStateField = modBean.getField(FacilioConstants.ContextNames.MODULE_STATE, moduleName);
                        FacilioField approvalStateField = modBean.getField(FacilioConstants.ContextNames.APPROVAL_STATUS, moduleName);
                        FacilioField totalCostField = modBean.getField(FacilioConstants.ContextNames.TOTAL_COST, moduleName);
                        List<Long> updatedFieldIdsList = updatedSet.stream().map(change -> change.getFieldId()).collect(Collectors.toList());
                        if (!((updatedFieldIdsList.contains(moduleStateField.getFieldId()) || updatedFieldIdsList.contains(approvalStateField.getFieldId())) && updatedFieldIdsList.size() == 1)) {
                            if (updatedFieldIdsList.contains(totalCostField.getFieldId())) {
                                JSONObject info = new JSONObject();
                                info.put(FacilioConstants.ContextNames.TOTAL_COST, quotation.getTotalCost());
                                CommonCommandUtil.addActivityToContext(quotation.getId(), -1, QuotationActivityType.UPDATE, info, (FacilioContext) context);
                            } else {
                                JSONObject info = new JSONObject();
                                CommonCommandUtil.addActivityToContext(quotation.getId(), -1, QuotationActivityType.UPDATE, info, (FacilioContext) context);
                            }
                        }
                    }
                } else {
                    JSONObject info = new JSONObject();
                    info.put(FacilioConstants.ContextNames.TOTAL_COST, quotation.getTotalCost());
                    CommonCommandUtil.addActivityToContext(quotation.getId(), -1, QuotationActivityType.ADD, info, (FacilioContext) context);

                }
            }
        }
        return false;
    }
}
