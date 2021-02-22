package com.facilio.bmsconsoleV3.commands.purchaseorder;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.context.PurchaseOrderLineItemContext;
import com.facilio.bmsconsole.util.PurchaseOrderAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsoleV3.context.V3TermsAndConditionContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PoAssociatedTermsContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderLineItemContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3ReceivableContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestContext;
import com.facilio.bmsconsoleV3.util.QuotationAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

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
                            DeleteRecordBuilder<PurchaseOrderLineItemContext> deleteBuilder = new DeleteRecordBuilder<PurchaseOrderLineItemContext>()
                              .module(lineItemsModule)
                                    .andCondition(CriteriaAPI.getCondition("PO_ID", "purchaseOrder", String.valueOf(purchaseOrderContext.getId()), NumberOperators.EQUALS));
                            deleteBuilder.delete();
                            V3PurchaseOrderContext poContext = new V3PurchaseOrderContext();
                            poContext.setId(purchaseOrderContext.getId());
                            for (V3PurchaseOrderLineItemContext lineItemContext : purchaseOrderContext.getLineItems()) {
                                lineItemContext.setPurchaseOrder(poContext);
                            }
                            RecordAPI.addRecord(false, purchaseOrderContext.getLineItems(), lineItemsModule, modBean.getAllFields(lineItemsModule.getName()));
                        }
                        Long poId = (Long) context.get(Constants.RECORD_ID);
                        if (poId == null || poId <= 0) {

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
                        Map<String, Object> bodyParams = Constants.getBodyParams(context);
                        if (MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey(FacilioConstants.ContextNames.PR_IDS)) {
                            List<Long> prIds = (List<Long>) bodyParams.get(FacilioConstants.ContextNames.PR_IDS);
                            if (CollectionUtils.isNotEmpty(prIds)) {
                                FacilioModule purchaseRequestModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST);
                                PurchaseOrderContext purchaseOrder = (PurchaseOrderContext) context.get(FacilioConstants.ContextNames.RECORD);
                                Map<String, Object> updateMap = new HashMap<>();
                                FacilioField statusField = modBean.getField("status", purchaseRequestModule.getName());
                                FacilioField poField = modBean.getField("purchaseOrder", purchaseRequestModule.getName());
                                updateMap.put("status", V3PurchaseRequestContext.Status.COMPLETED.ordinal() + 1);
                                updateMap.put("purchaseOrder", FieldUtil.getAsProperties(purchaseOrder));
                                List<FacilioField> updatedfields = new ArrayList<FacilioField>();
                                updatedfields.add(poField);
                                updatedfields.add(statusField);
                                UpdateRecordBuilder<V3PurchaseRequestContext> updateBuilder2 = new UpdateRecordBuilder<V3PurchaseRequestContext>()
                                        .module(purchaseRequestModule)
                                        .fields(updatedfields)
                                        .andCondition(CriteriaAPI.getIdCondition(prIds, purchaseRequestModule));
                                updateBuilder2.updateViaMap(updateMap);
                            }
                        }
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

}
