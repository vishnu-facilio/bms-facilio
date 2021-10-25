package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.ItemsApi;
import com.facilio.bmsconsole.util.StoreroomApi;
import com.facilio.bmsconsole.util.ToolsApi;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolTypesContext;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestContext;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestLineItemContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.SubFormContext;
import com.facilio.v3.context.V3Context;
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
            for (V3TransferRequestContext transferRequestContext : transferRequestContexts) {
                if (transferRequestContext != null && transferRequestContext.getData().get("isCompleted").equals(true)) {
                    Long storeroomId = transferRequestContext.getTransferToStore().getId();
                    StoreRoomContext storeRoom = StoreroomApi.getStoreRoom(storeroomId);
                    double quantityTransferred=(double)context.get(FacilioConstants.ContextNames.TOTAL_QUANTITY);
                    if(!Objects.isNull(context.get(FacilioConstants.ContextNames.ITEM_TYPES_ID))){
                        long itemTypeId = (long) context.get(FacilioConstants.ContextNames.ITEM_TYPES_ID);
                        ItemTypesContext itemType = ItemsApi.getItemTypes(itemTypeId);
                        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                        String itemModuleName = FacilioConstants.ContextNames.ITEM;
                        FacilioModule module = modBean.getModule(itemModuleName);
                        List<FacilioField> fields = modBean.getAllFields(itemModuleName);

                        SelectRecordsBuilder<ItemContext> selectRecordsBuilder = new SelectRecordsBuilder<ItemContext>()
                                .module(module)
                                .beanClass(ItemContext.class)
                                .select(fields)
                                .andCondition(CriteriaAPI.getCondition("ITEM_TYPES_ID", "itemType", String.valueOf(itemTypeId), NumberOperators.EQUALS))
                                .andCondition(CriteriaAPI.getCondition("STORE_ROOM_ID", "storeRoom", String.valueOf(storeroomId), NumberOperators.EQUALS));
                        List<ItemContext> items = selectRecordsBuilder.get();
                        if(items.size()>=1){
                            Long itemId = items.get(0).getId();
                            double newQuantity =items.get(0).getQuantity()+quantityTransferred;

                            List<FacilioField> updatedFields = new ArrayList<>();
                            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
                            updatedFields.add(fieldsMap.get("quantity"));

                            Map<String, Object> map = new HashMap<>();
                            map.put("quantity", newQuantity);

                            //update total quantity in fromStore in Item Table
                            UpdateRecordBuilder<ItemContext> updateBuilder = new UpdateRecordBuilder<ItemContext>()
                                    .module(module).fields(updatedFields)
                                    .andCondition(CriteriaAPI.getIdCondition(itemId, module));
                            updateBuilder.updateViaMap(map);
                        }
                        else{
                            ItemContext itemRecord = new ItemContext();
                            itemRecord.setItemType(itemType);
                            itemRecord.setStoreRoom(storeRoom);
                            itemRecord.setCostType(ItemContext.CostType.FIFO);
                            itemRecord.setQuantity(quantityTransferred);
                            InsertRecordBuilder<ItemContext> readingBuilder = new InsertRecordBuilder<ItemContext>()
                                    .module(module).fields(fields).addRecord(itemRecord);
                            readingBuilder.save();

                        }
                    }

                    if(!Objects.isNull(context.get(FacilioConstants.ContextNames.TOOL_TYPES_ID))) {
                        long toolTypeId = (long) context.get(FacilioConstants.ContextNames.TOOL_TYPES_ID);
                        ToolTypesContext toolType = ToolsApi.getToolTypes(toolTypeId);
                        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                        String toolModuleName = FacilioConstants.ContextNames.TOOL;
                        FacilioModule module = modBean.getModule(toolModuleName);
                        List<FacilioField> fields = modBean.getAllFields(toolModuleName);

                        SelectRecordsBuilder<ToolContext> selectRecordsBuilder = new SelectRecordsBuilder<ToolContext>()
                                .module(module)
                                .beanClass(ToolContext.class)
                                .select(fields)
                                .andCondition(CriteriaAPI.getCondition("TOOL_TYPE_ID", "toolType", String.valueOf(toolTypeId), NumberOperators.EQUALS))
                                .andCondition(CriteriaAPI.getCondition("STORE_ROOM_ID", "storeRoom", String.valueOf(storeroomId), NumberOperators.EQUALS));
                        List<ToolContext> tools = selectRecordsBuilder.get();
                        if(tools.size()>=1){
                            Long itemId = tools.get(0).getId();
                            double newQuantity =tools.get(0).getQuantity()+quantityTransferred;
                            double newCurrentQuantity =tools.get(0).getCurrentQuantity()+quantityTransferred;
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
                            UpdateRecordBuilder<ToolContext> updateBuilder = new UpdateRecordBuilder<ToolContext>()
                                    .module(module).fields(updatedFields)
                                    .andCondition(CriteriaAPI.getIdCondition(itemId, module));
                            updateBuilder.updateViaMap(map);
                        }
                        else{
                            ToolContext toolRecord = new ToolContext();
                            toolRecord.setToolType(toolType);
                            toolRecord.setStoreRoom(storeRoom);
                            toolRecord.setQuantity(quantityTransferred);
                            toolRecord.setCurrentQuantity(quantityTransferred);
                            toolRecord.setRate(0);
                            toolRecord.setLastPurchasedDate(System.currentTimeMillis());
                            InsertRecordBuilder<ToolContext> readingBuilder = new InsertRecordBuilder<ToolContext>()
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
