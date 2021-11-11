package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
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
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateCurrentBalanceCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
       String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3TransferRequestContext> transferRequestContexts = recordMap.get(moduleName);
        Long itemId = null;
        Long toolId = null;
        Long itemTypeId= null;
        Long toolTypeId=null;
        double quantityTransferred=0;
        if (CollectionUtils.isNotEmpty(transferRequestContexts)){
            for (V3TransferRequestContext transferRequestContext : transferRequestContexts) {
                if (transferRequestContext != null && transferRequestContext.getData().get("isStaged").equals(true)) {
                    List<V3TransferRequestLineItemContext> itemTypesList = new ArrayList<>();
                    List<V3TransferRequestLineItemContext> toolTypesList = new ArrayList<>();
                    Long storeroomId = transferRequestContext.getTransferFromStore().getId();
                    context.put(FacilioConstants.ContextNames.STORE_ROOM_ID,storeroomId);
                    List<V3TransferRequestLineItemContext> transferrequestlineitems = transferRequestContext.getLineItems();
                   for(V3TransferRequestLineItemContext lineItem : transferrequestlineitems){
                           Integer inventoryType = lineItem.getInventoryType();
                           quantityTransferred = lineItem.getQuantityTransferred();
                           double currentQuantity = 0;

                           if(inventoryType.equals(V3TransferRequestLineItemContext.InventoryType.ITEM.getIndex())){
                               ItemTypesContext itemType=lineItem.getItemType();
                               itemTypesList.add(lineItem);
                               if(itemType!=null) {
                                  itemTypeId = itemType.getId();
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
                                      for(ItemContext item : items){
                                          itemId = item.getId();
                                          currentQuantity = item.getQuantity();
                                      }
                                   if(transferRequestContext.getData().get("isCompleted").equals(false) && transferRequestContext.getData().get("isShipped").equals(false)){
                                      if(currentQuantity<quantityTransferred){
                                          throw new IllegalArgumentException("Quantity transferred is more than available quantity");
                                      }
                                      else{
                                          List<FacilioField> updatedFields = new ArrayList<>();
                                          Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
                                          updatedFields.add(fieldsMap.get("quantity"));

                                          double newQuantity =currentQuantity-quantityTransferred;

                                          Map<String, Object> map = new HashMap<>();
                                          map.put("quantity", newQuantity);

                                          //update total quantity in fromStore in Item Table
                                          UpdateRecordBuilder<ItemContext> updateBuilder = new UpdateRecordBuilder<ItemContext>()
                                                  .module(module).fields(updatedFields)
                                                  .andCondition(CriteriaAPI.getIdCondition(itemId, module));
                                          updateBuilder.updateViaMap(map);

                                      }
                                  }

                                   context.put(FacilioConstants.ContextNames.ITEM_TYPES,itemTypesList);

                               }
                           }
                           else if(inventoryType.equals(V3TransferRequestLineItemContext.InventoryType.TOOL.getIndex())){
                               ToolTypesContext toolType=lineItem.getToolType();
                               toolTypesList.add(lineItem);
                               if(toolType!=null){
                                   toolTypeId = toolType.getId();
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
                                   for(ToolContext tool : tools){
                                       toolId = tool.getId();
                                       currentQuantity = tool.getCurrentQuantity();
                                   }
                                   if(transferRequestContext.getData().get("isCompleted").equals(false) && transferRequestContext.getData().get("isShipped").equals(false)) {
                                       if (currentQuantity < quantityTransferred) {
                                           throw new IllegalArgumentException("Quantity transferred is more than available quantity");
                                       } else {
                                           List<FacilioField> updatedFields = new ArrayList<>();
                                           Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
                                           updatedFields.add(fieldsMap.get("currentQuantity"));

                                           double newQuantity = currentQuantity - quantityTransferred;

                                           Map<String, Object> map = new HashMap<>();
                                           map.put("currentQuantity", newQuantity);

                                           //update total quantity in fromStore in Tool Table
                                           UpdateRecordBuilder<ToolContext> updateBuilder = new UpdateRecordBuilder<ToolContext>()
                                                   .module(module).fields(updatedFields)
                                                   .andCondition(CriteriaAPI.getIdCondition(toolId, module));
                                           updateBuilder.updateViaMap(map);

                                       }
                                   }

                                   context.put(FacilioConstants.ContextNames.TOOL_TYPES,toolTypesList);

                               }
                           }



                   }
                }

            }

        }

        return false;
    }

}
