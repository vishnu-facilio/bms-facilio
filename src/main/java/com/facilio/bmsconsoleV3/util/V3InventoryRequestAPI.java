package com.facilio.bmsconsoleV3.util;

import com.facilio.accounts.dto.User;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.bmsconsole.context.WorkorderToolsContext;
import com.facilio.bmsconsoleV3.context.V3WorkorderToolsContext;
import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestLineItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolTypesContext;
import com.facilio.bmsconsoleV3.context.reservation.InventoryReservationContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class V3InventoryRequestAPI {
    public static boolean checkQuantityForWoItemNeedingApproval(ItemTypesContext itemType, V3InventoryRequestLineItemContext lineItem, double woItemQuantity) throws Exception {
        if (lineItem != null) {
            lineItem = getLineItem(lineItem.getId());
            if (woItemQuantity <= (lineItem.getIssuedQuantity())) {
                updateRequestUsedQuantity(lineItem, woItemQuantity);
                return true;
            }
            return false;
        }
        throw new IllegalArgumentException("Please request approval for the item before using it");
    }
    public static boolean checkQuantityForWoItemNeedingApprovalV3(V3ItemTypesContext itemType, V3InventoryRequestLineItemContext lineItem, double woItemQuantity) throws Exception {
        if (lineItem != null) {
            lineItem = getLineItem(lineItem.getId());
            if (woItemQuantity <= (lineItem.getIssuedQuantity())) {
                updateRequestUsedQuantity(lineItem, woItemQuantity);
                return true;
            }
            return false;
        }
        throw new IllegalArgumentException("Please request approval for the item before using it");
    }
    public static boolean checkQuantityForWoToolNeedingApprovalV3(V3ToolTypesContext toolType, V3InventoryRequestLineItemContext lineItem, V3WorkorderToolsContext woTool) throws Exception {
        if(lineItem != null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);
            List<FacilioField> fields = modBean.getAllFields(module.getName());

            SelectRecordsBuilder<V3InventoryRequestLineItemContext> builder = new SelectRecordsBuilder<V3InventoryRequestLineItemContext>()
                    .module(module)
                    .beanClass(FacilioConstants.ContextNames.getClassFromModule(module))
                    .select(fields)
                    .andCondition(CriteriaAPI.getIdCondition(lineItem.getId(), module))

                    ;
            List<V3InventoryRequestLineItemContext> lineItems = builder.get();
            if(CollectionUtils.isNotEmpty(lineItems)) {
                if(!toolType.isRotating() && woTool.getQuantity() <= (lineItems.get(0).getIssuedQuantity())) {
                    return true;
                }
                else if(toolType.isRotating() && checkRotatingToolCountForWorkOrder(woTool.getTool().getId(), woTool.getParentId()) <= lineItems.get(0).getIssuedQuantity()) {
                    return true;
                }
                return false;
            }
            return false;
        }
        throw new IllegalArgumentException("Please request approval for the tool before using it");
    }
    public static boolean checkQuantityForWoToolNeedingApproval(ToolTypesContext toolType, V3InventoryRequestLineItemContext lineItem, WorkorderToolsContext woTool) throws Exception {
        if(lineItem != null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);
            List<FacilioField> fields = modBean.getAllFields(module.getName());

            SelectRecordsBuilder<V3InventoryRequestLineItemContext> builder = new SelectRecordsBuilder<V3InventoryRequestLineItemContext>()
                    .module(module)
                    .beanClass(FacilioConstants.ContextNames.getClassFromModule(module))
                    .select(fields)
                    .andCondition(CriteriaAPI.getIdCondition(lineItem.getId(), module))

                    ;
            List<V3InventoryRequestLineItemContext> lineItems = builder.get();
            if(CollectionUtils.isNotEmpty(lineItems)) {
                if(!toolType.isRotating() && woTool.getQuantity() <= (lineItems.get(0).getIssuedQuantity())) {
                    return true;
                }
                else if(toolType.isRotating() && checkRotatingToolCountForWorkOrder(woTool.getTool().getId(), woTool.getParentId()) <= lineItems.get(0).getIssuedQuantity()) {
                    return true;
                }
                return false;
            }
            return false;
        }
        throw new IllegalArgumentException("Please request approval for the tool before using it");
    }
    public static double checkRotatingToolCountForWorkOrder(long toolId, long parentId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule workorderToolsModule = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_TOOLS);
        List<FacilioField> workorderToolsFields = modBean.getAllFields(workorderToolsModule.getName());

        SelectRecordsBuilder<WorkorderToolsContext> selectBuilder = new SelectRecordsBuilder<WorkorderToolsContext>()
                .select(workorderToolsFields).table(workorderToolsModule.getTableName())
                .moduleName(workorderToolsModule.getName()).beanClass(WorkorderToolsContext.class)
                .andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId",String.valueOf(parentId) , NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("TOOL", "tool",String.valueOf(toolId) , NumberOperators.EQUALS))
                ;

        List<WorkorderToolsContext> workorderTools = selectBuilder.get();
        return workorderTools.size();


    }
    public static V3InventoryRequestLineItemContext getLineItem(long lineItemId) throws Exception {
      V3InventoryRequestLineItemContext lineItems =  V3RecordAPI.getRecord(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS, lineItemId);
        if (lineItems!= null) {
            return lineItems;
        }
        throw new IllegalArgumentException("No appropriate lineitem found");

    }

    public static void updateRequestUsedQuantity(V3InventoryRequestLineItemContext lineItem, double usedQuantity) throws Exception {

        if (lineItem != null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule lineItemModule = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);
            FacilioField usedQuantityField = modBean.getField("usedQuantity", lineItemModule.getName());
            lineItem.setUsedQuantity(usedQuantity);
            List<FacilioField> updatedfields = new ArrayList<FacilioField>();
            updatedfields.add(usedQuantityField);

            V3RecordAPI.updateRecord(lineItem,lineItemModule,updatedfields);

        }
    }

    public static User getUserToIssueFromReservation(InventoryReservationContext inventoryReservation) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule inventoryRequestModule = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST);
        FacilioModule inventoryReservationModule = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_RESERVATION);
        final String requestedForFieldName = "requestedFor";

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getIdField(inventoryRequestModule));
        fields.add(modBean.getField(requestedForFieldName,FacilioConstants.ContextNames.INVENTORY_REQUEST));
        FacilioField inventoryRequestField = modBean.getField("inventoryRequest", FacilioConstants.ContextNames.INVENTORY_RESERVATION);
        fields.add(inventoryRequestField);

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(inventoryReservationModule.getTableName())
                .innerJoin(inventoryRequestModule.getTableName())
                .on(inventoryReservationModule.getTableName() + "."+ inventoryRequestField.getColumnName() +" = " + inventoryRequestModule.getTableName() + ".ID");

        selectBuilder.andCondition(CriteriaAPI.getIdCondition(inventoryReservation.getId(),inventoryReservationModule));
        List<Map<String, Object>> props = selectBuilder.get();
        if(CollectionUtils.isEmpty(props)){
            return null;
        }
        Long requestedForId = (Long) props.get(0).get(requestedForFieldName);
        if(requestedForId == null){
            return null;
        }
        User requestedFor = new User();
        requestedFor.setId(requestedForId);
        return requestedFor;
    }
}
