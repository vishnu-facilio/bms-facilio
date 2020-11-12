package com.facilio.bmsconsoleV3.commands.purchaseorder;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.InventoryType;
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
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class POAfterCreateorEditV3Command extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3PurchaseOrderContext> purchaseOrderContexts = recordMap.get(moduleName);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if(CollectionUtils.isNotEmpty(purchaseOrderContexts)){
            for (V3PurchaseOrderContext purchaseOrderContext : purchaseOrderContexts) {
                if (purchaseOrderContext != null) {

                    FacilioModule lineItemsModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);
                    if (purchaseOrderContext.getId() > 0) {
                        if (purchaseOrderContext.getLineItems() != null) {
                            DeleteRecordBuilder<PurchaseOrderLineItemContext> deleteBuilder = new DeleteRecordBuilder<PurchaseOrderLineItemContext>()
                                    .module(lineItemsModule)
                                    .andCondition(CriteriaAPI.getCondition("PO_ID", "poid", String.valueOf(purchaseOrderContext.getId()), NumberOperators.EQUALS));
                            deleteBuilder.delete();
                            updateLineItems(purchaseOrderContext);
                            RecordAPI.addRecord(false, purchaseOrderContext.getLineItems(), lineItemsModule, modBean.getAllFields(lineItemsModule.getName()));
                        }
                        FacilioModule receivableModule = modBean.getModule(FacilioConstants.ContextNames.RECEIVABLE);
                        V3ReceivableContext receivableContext = new V3ReceivableContext();
                        receivableContext.setPoId(purchaseOrderContext);
                        receivableContext.setStatus(V3ReceivableContext.Status.YET_TO_RECEIVE);
                        receivableContext.setRequiredTime(purchaseOrderContext.getRequiredTime());
                        RecordAPI.addRecord(true, Collections.singletonList(receivableContext), receivableModule, modBean.getAllFields(receivableModule.getName()));

                        List<V3TermsAndConditionContext> terms = PurchaseOrderAPI.fetchPoDefaultTermsV3();
                        if(CollectionUtils.isNotEmpty(terms)) {
                            List<V3PoAssociatedTermsContext> poAssociatedTerms = new ArrayList<>();
                            for(V3TermsAndConditionContext term : terms) {
                                V3PoAssociatedTermsContext associatedTerm = new V3PoAssociatedTermsContext();
                                associatedTerm.setPoId(purchaseOrderContext.getId());
                                associatedTerm.setTerms(term);
                                poAssociatedTerms.add(associatedTerm);
                            }
                            PurchaseOrderAPI.updateTermsAssociatedV3(purchaseOrderContext.getId(), poAssociatedTerms );
                        }

                        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER);
                        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASE_ORDER);
                        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
                        List<FacilioField> updatedFields = new ArrayList<FacilioField>();
                        updatedFields.add(fieldsMap.get("totalCost"));
                        updatedFields.add(fieldsMap.get("totalQuantity"));

                        double totalCost = getTotalCost(purchaseOrderContext.getId());
                        double totalQuantity = getTotalQuantity(purchaseOrderContext.getId());

                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("totalCost", totalCost);
                        map.put("totalQuantity", totalQuantity);

                        //update total cost for purchase order
                        UpdateRecordBuilder<V3PurchaseOrderContext> updateBuilder = new UpdateRecordBuilder<V3PurchaseOrderContext>()
                                .module(module).fields(updatedFields)
                                .andCondition(CriteriaAPI.getIdCondition(purchaseOrderContext.getId(), module));
                        updateBuilder.updateViaMap(map);

                        if(context.get(FacilioConstants.ContextNames.PR_IDS) != null) {
                            List<Long> purchaseRequestsIds = (List<Long>) context.get(FacilioConstants.ContextNames.PR_IDS);
                            FacilioModule purchaseRequestModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST);
                            PurchaseOrderContext purchaseOrder = (PurchaseOrderContext) context.get(FacilioConstants.ContextNames.RECORD);
                            Map<String, Object> updateMap = new HashMap<>();
                            FacilioField statusField = modBean.getField("status", purchaseRequestModule.getName());
                            FacilioField poField = modBean.getField("purchaseOrder", purchaseRequestModule.getName());
                            updateMap.put("status", V3PurchaseRequestContext.Status.COMPLETED.ordinal()+1);
                            updateMap.put("purchaseOrder", FieldUtil.getAsProperties(purchaseOrder));
                            List<FacilioField> updatedfields = new ArrayList<FacilioField>();
                            updatedfields.add(poField);
                            updatedfields.add(statusField);
                            UpdateRecordBuilder<V3PurchaseRequestContext> updateBuilder2 = new UpdateRecordBuilder<V3PurchaseRequestContext>()
                                    .module(purchaseRequestModule)
                                    .fields(updatedfields)
                                    .andCondition(CriteriaAPI.getIdCondition(purchaseRequestsIds,purchaseRequestModule));
                            updateBuilder2.updateViaMap(updateMap);
                        }
                    }

                }
            }
        }
        return false;
    }


    private void updateLineItems(V3PurchaseOrderContext purchaseOrderContext) {
        for (V3PurchaseOrderLineItemContext lineItemContext : purchaseOrderContext.getLineItems()) {
            lineItemContext.setPoId(purchaseOrderContext.getId());
            updateLineItemCost(lineItemContext);
        }
    }


    private void updateLineItemCost(V3PurchaseOrderLineItemContext lineItemContext) {
        if (lineItemContext.getUnitPrice() > 0) {
            lineItemContext.setCost(lineItemContext.getUnitPrice() * lineItemContext.getQuantity());
        } else {
            lineItemContext.setCost(0.0);
        }
    }

    private void checkForStoreRoom(V3PurchaseOrderContext po, List<V3PurchaseOrderLineItemContext> lineItems) throws RESTException {
        if (CollectionUtils.isNotEmpty(lineItems)) {
            for (V3PurchaseOrderLineItemContext lineItem : lineItems) {
                if ((lineItem.getInventoryType() == InventoryType.ITEM.getValue() || lineItem.getInventoryType() == InventoryType.TOOL.getValue()) && po.getStoreRoom() == null) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Receivable are invalid");
                }
            }
        }
    }


    private Double getTotalCost(long id) throws Exception {
        if (id <= 0) {
            return 0d;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule lineModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);
        List<FacilioField> linefields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(linefields);

        List<FacilioField> field = new ArrayList<>();
        field.add(FieldFactory.getField("totalItemsCost", "sum(COST)", FieldType.DECIMAL));

        SelectRecordsBuilder<V3PurchaseOrderLineItemContext> builder = new SelectRecordsBuilder<V3PurchaseOrderLineItemContext>()
                .select(field).moduleName(lineModule.getName())
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("poId"), String.valueOf(id), NumberOperators.EQUALS))
                .setAggregation()
                ;

        List<Map<String, Object>> rs = builder.getAsProps();
        if (rs != null && rs.size() > 0) {
            if (rs.get(0).get("totalItemsCost") != null) {
                return (double) rs.get(0).get("totalItemsCost");
            }
            return 0d;
        }
        return 0d;
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
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("poId"), String.valueOf(id), NumberOperators.EQUALS))
                .setAggregation()
                ;

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
