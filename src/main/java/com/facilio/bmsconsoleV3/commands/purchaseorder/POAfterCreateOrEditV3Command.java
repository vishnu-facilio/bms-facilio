package com.facilio.bmsconsoleV3.commands.purchaseorder;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.CurrencyContext;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.PurchaseOrderAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsoleV3.context.V3TermsAndConditionContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PoAssociatedTermsContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderLineItemContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3ReceivableContext;
import com.facilio.bmsconsoleV3.util.QuotationAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.CurrencyUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class POAfterCreateOrEditV3Command extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3PurchaseOrderContext> purchaseOrderContexts = recordMap.get(moduleName);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASE_ORDER);
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        FacilioModule lineItemsModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);

        if (CollectionUtils.isNotEmpty(purchaseOrderContexts)) {
            for (V3PurchaseOrderContext purchaseOrderContext : purchaseOrderContexts) {
                if (purchaseOrderContext != null) {

                    if (purchaseOrderContext.getId() > 0) {
                        if (purchaseOrderContext.getLineItems() != null && purchaseOrderContext.getReceivableStatus() == V3PurchaseOrderContext.ReceivableStatus.YET_TO_RECEIVE.getIndex()) {
                            DeleteRecordBuilder<V3PurchaseOrderLineItemContext> deleteBuilder = new DeleteRecordBuilder<V3PurchaseOrderLineItemContext>()
                                    .module(lineItemsModule)
                                    .andCondition(CriteriaAPI.getCondition("PO_ID", "purchaseOrder", String.valueOf(purchaseOrderContext.getId()), NumberOperators.EQUALS));
                            deleteBuilder.delete();
                            V3PurchaseOrderContext poContext = new V3PurchaseOrderContext();
                            poContext.setId(purchaseOrderContext.getId());

                            CurrencyContext baseCurrency = Constants.getBaseCurrency(context);
                            Map<String, CurrencyContext> currencyCodeVsCurrency = Constants.getCurrencyMap(context);
                            String parentRecordCurrencyCode = purchaseOrderContext.getCurrencyCode();
                            Double parentRecordExchangeRate = purchaseOrderContext.getExchangeRate();

                            for (V3PurchaseOrderLineItemContext lineItemContext : purchaseOrderContext.getLineItems()) {
                                lineItemContext.setPurchaseOrder(poContext);
                                CurrencyUtil.setCurrencyCodeAndExchangeRateForWrite(lineItemContext, baseCurrency, currencyCodeVsCurrency, parentRecordCurrencyCode, parentRecordExchangeRate);
                            }
                            List<FacilioField> lineItemFields = modBean.getAllFields(lineItemsModule.getName());
                            lineItemFields.addAll(FieldFactory.getCurrencyPropsFields(lineItemsModule));

                            RecordAPI.addRecord(false, purchaseOrderContext.getLineItems(), lineItemsModule, lineItemFields);
                        }

                        if (receivablesNotCreated(purchaseOrderContext.getId())) {
                            FacilioModule receivableModule = modBean.getModule(FacilioConstants.ContextNames.RECEIVABLE);
                            V3ReceivableContext receivableContext = new V3ReceivableContext();
                            receivableContext.setPoId(purchaseOrderContext);
                            receivableContext.setStatus(V3ReceivableContext.Status.YET_TO_RECEIVE);
                            receivableContext.setRequiredTime(purchaseOrderContext.getRequiredTime());
                            if (QuotationAPI.lookupValueIsNotEmpty(purchaseOrderContext.getVendor())) {
                                receivableContext.setVendor(purchaseOrderContext.getVendor());
                            }
                            if (QuotationAPI.lookupValueIsNotEmpty(purchaseOrderContext.getStoreRoom())) {
                                receivableContext.setStoreRoom(purchaseOrderContext.getStoreRoom());
                            }
                            RecordAPI.addRecord(true, Collections.singletonList(receivableContext), receivableModule, modBean.getAllFields(receivableModule.getName()));

                            List<V3TermsAndConditionContext> terms = PurchaseOrderAPI.fetchPoDefaultTermsV3();
                            if (CollectionUtils.isNotEmpty(terms)) {
                                List<V3PoAssociatedTermsContext> poAssociatedTerms = new ArrayList<>();
                                for (V3TermsAndConditionContext term : terms) {
                                    V3PoAssociatedTermsContext associatedTerm = new V3PoAssociatedTermsContext();
                                    associatedTerm.setPurchaseOrder(purchaseOrderContext);
                                    associatedTerm.setTerms(term);
                                    poAssociatedTerms.add(associatedTerm);
                                }
                                PurchaseOrderAPI.updateTermsAssociatedV3(poAssociatedTerms);
                            }
                        }

                        List<FacilioField> updatedFields = new ArrayList<FacilioField>();
                        updatedFields.add(fieldsMap.get("totalQuantity"));

                        double totalQuantity = getTotalQuantity(purchaseOrderContext.getId());

                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("totalQuantity", totalQuantity);

                        //update total cost for purchase order
                        UpdateRecordBuilder<V3PurchaseOrderContext> updateBuilder = new UpdateRecordBuilder<V3PurchaseOrderContext>()
                                .module(module).fields(updatedFields)
                                .andCondition(CriteriaAPI.getIdCondition(purchaseOrderContext.getId(), module));
                        updateBuilder.updateViaMap(map);
                    }

                }
            }
        }
        return false;
    }

    private double getTotalQuantity(long id) throws Exception {
        if (id <= 0) {
            return 0d;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule lineModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);
        List<FacilioField> linefields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(linefields);

        List<FacilioField> field = new ArrayList<>();
        field.add(FieldFactory.getField("totalItems", "sum(QUANTITY)", FieldType.DECIMAL));

        SelectRecordsBuilder<V3PurchaseOrderLineItemContext> builder = new SelectRecordsBuilder<V3PurchaseOrderLineItemContext>()
                .select(field).moduleName(lineModule.getName())
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("purchaseOrder"), String.valueOf(id), NumberOperators.EQUALS))
                .setAggregation();

        List<Map<String, Object>> rs = builder.getAsProps();
        if (rs != null && rs.size() > 0) {
            if (rs.get(0).get("totalItems") != null) {
                return (double) rs.get(0).get("totalItems");
            }
            return 0d;
        }
        return 0d;
    }
    private boolean receivablesNotCreated(long poId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String receivablesModuleName = FacilioConstants.ContextNames.RECEIVABLE;
        FacilioModule receivablesModule = modBean.getModule(receivablesModuleName);
        List<FacilioField> receivablesFields = modBean.getAllFields(receivablesModuleName);
        SelectRecordsBuilder<V3ReceivableContext> selectRecordsBuilder = new SelectRecordsBuilder<V3ReceivableContext>()
                .module(receivablesModule)
                .beanClass(V3ReceivableContext.class)
                .select(receivablesFields)
                .andCondition(CriteriaAPI.getCondition("PO_ID", "poId", String.valueOf(poId), NumberOperators.EQUALS));
        List<V3ReceivableContext> receivables = selectRecordsBuilder.get();
        if(receivables.size() <= 0){
            return true;
        }
        return false;
    }

}
