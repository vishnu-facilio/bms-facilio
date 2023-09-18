package com.facilio.bmsconsoleV3.commands.transferRequest;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.StoreroomApi;
import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.bmsconsoleV3.context.inventory.*;
import com.facilio.bmsconsoleV3.enums.CostType;
import com.facilio.bmsconsoleV3.util.V3StoreroomApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class UpdateCurrentBalanceAfterTransferCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3TransferRequestContext> transferRequestContexts = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(transferRequestContexts)){
                if (transferRequestContexts.get(0).getIsCompleted()) {
                    Long storeroomId = transferRequestContexts.get(0).getTransferToStore().getId();
                    V3StoreRoomContext storeRoom = V3StoreroomApi.getStoreRoom(storeroomId);

                    if(!Objects.isNull(context.get(FacilioConstants.ContextNames.ITEM_TYPES))){
                        List<V3TransferRequestLineItemContext> itemTypesList = (List<V3TransferRequestLineItemContext>) context.get(FacilioConstants.ContextNames.ITEM_TYPES);
                        for(V3TransferRequestLineItemContext itemTypeLineItem : itemTypesList) {
                            double quantityTransferred = itemTypeLineItem.getQuantity();
                            V3ItemTypesContext itemType = itemTypeLineItem.getItemType();
                            Long itemTypeId = itemTypeLineItem.getItemType().getId();
                            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                            String itemModuleName = FacilioConstants.ContextNames.ITEM;
                            FacilioModule module = modBean.getModule(itemModuleName);
                            List<FacilioField> fields = modBean.getAllFields(itemModuleName);
                            SelectRecordsBuilder<V3ItemContext> selectRecordsBuilder = new SelectRecordsBuilder<V3ItemContext>()
                                    .module(module)
                                    .beanClass(V3ItemContext.class)
                                    .select(fields)
                                    .andCondition(CriteriaAPI.getCondition("ITEM_TYPES_ID", "itemType", String.valueOf(itemTypeId), NumberOperators.EQUALS))
                                    .andCondition(CriteriaAPI.getCondition("STORE_ROOM_ID", "storeRoom", String.valueOf(storeroomId), NumberOperators.EQUALS));
                            List<V3ItemContext> items = selectRecordsBuilder.get();
                            if (items.size() >= 1) {
                                Long itemId = items.get(0).getId();
                                double newQuantity = items.get(0).getQuantity() + quantityTransferred;
                                double newCurrentQuantity = items.get(0).getCurrentQuantity() + quantityTransferred;
                                List<FacilioField> updatedFields = new ArrayList<>();
                                Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
                                updatedFields.add(fieldsMap.get("quantity"));
                                updatedFields.add(fieldsMap.get("currentQuantity"));

                                Map<String, Object> map = new HashMap<>();
                                map.put("quantity", newQuantity);
                                map.put("currentQuantity", newCurrentQuantity);

                                //update total quantity in fromStore in Item Table
                                UpdateRecordBuilder<V3ItemContext> updateBuilder = new UpdateRecordBuilder<V3ItemContext>()
                                        .module(module).fields(updatedFields)
                                        .andCondition(CriteriaAPI.getIdCondition(itemId, module));
                                updateBuilder.updateViaMap(map);
                            } else {
                                V3ItemContext itemRecord = new V3ItemContext();
                                itemRecord.setItemType(itemType);
                                itemRecord.setStoreRoom(storeRoom);
                                itemRecord.setCostType(CostType.FIFO.getIndex());
                                itemRecord.setQuantity(quantityTransferred);
                                itemRecord.setCurrentQuantity(quantityTransferred);
                                InsertRecordBuilder<V3ItemContext> readingBuilder = new InsertRecordBuilder<V3ItemContext>()
                                        .module(module).fields(fields).addRecord(itemRecord);
                                readingBuilder.save();

                            }
                        }
                    }

                    if(!Objects.isNull(context.get(FacilioConstants.ContextNames.TOOL_TYPES))) {
                        List<V3TransferRequestLineItemContext> toolTypesList = (List<V3TransferRequestLineItemContext>) context.get(FacilioConstants.ContextNames.TOOL_TYPES);
                        for(V3TransferRequestLineItemContext toolTypeLineItem : toolTypesList) {
                        double quantityTransferred = toolTypeLineItem.getQuantity();
                        long toolTypeId = toolTypeLineItem.getToolType().getId();
                        V3ToolTypesContext toolType = toolTypeLineItem.getToolType();
                        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                        String toolModuleName = FacilioConstants.ContextNames.TOOL;
                        FacilioModule module = modBean.getModule(toolModuleName);
                        List<FacilioField> fields = modBean.getAllFields(toolModuleName);

                        SelectRecordsBuilder<V3ToolContext> selectRecordsBuilder = new SelectRecordsBuilder<V3ToolContext>()
                                .module(module)
                                .beanClass(V3ToolContext.class)
                                .select(fields)
                                .andCondition(CriteriaAPI.getCondition("TOOL_TYPE_ID", "toolType", String.valueOf(toolTypeId), NumberOperators.EQUALS))
                                .andCondition(CriteriaAPI.getCondition("STORE_ROOM_ID", "storeRoom", String.valueOf(storeroomId), NumberOperators.EQUALS));
                        List<V3ToolContext> tools = selectRecordsBuilder.get();
                        if (tools.size() >= 1) {
                            Long itemId = tools.get(0).getId();
                            double newQuantity = tools.get(0).getQuantity() + quantityTransferred;
                            double newCurrentQuantity = tools.get(0).getCurrentQuantity() + quantityTransferred;
                            List<FacilioField> updatedFields = new ArrayList<>();
                            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
                            updatedFields.add(fieldsMap.get("quantity"));
                            updatedFields.add(fieldsMap.get("currentQuantity"));
                            updatedFields.add(fieldsMap.get("lastPurchasedDate"));
                            Map<String, Object> map = new HashMap<>();
                            map.put("quantity", newQuantity);
                            map.put("currentQuantity", newCurrentQuantity);
                            map.put("lastPurchasedDate", System.currentTimeMillis());
                            //update total quantity in fromStore in Tool Table
                            UpdateRecordBuilder<V3ToolContext> updateBuilder = new UpdateRecordBuilder<V3ToolContext>()
                                    .module(module).fields(updatedFields)
                                    .andCondition(CriteriaAPI.getIdCondition(itemId, module));
                            updateBuilder.updateViaMap(map);
                        } else {
                            V3ToolContext toolRecord = new V3ToolContext();
                            toolRecord.setToolType(toolType);
                            toolRecord.setStoreRoom(storeRoom);
                            toolRecord.setQuantity(quantityTransferred);
                            toolRecord.setCostType(CostType.FIFO.getIndex());
                            toolRecord.setCurrentQuantity(quantityTransferred);
                            toolRecord.setLastPurchasedDate(System.currentTimeMillis());
                            InsertRecordBuilder<V3ToolContext> readingBuilder = new InsertRecordBuilder<V3ToolContext>()
                                    .module(module).fields(fields).addRecord(toolRecord);
                            readingBuilder.save();
                        }
                    }
                    }
                }
        }
        return false;
    }

}
