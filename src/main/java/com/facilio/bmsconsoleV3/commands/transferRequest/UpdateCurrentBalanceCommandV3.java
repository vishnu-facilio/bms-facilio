package com.facilio.bmsconsoleV3.commands.transferRequest;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.inventory.*;
import com.facilio.bmsconsoleV3.util.V3ItemsApi;
import com.facilio.bmsconsoleV3.util.V3ToolsApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
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
                if (transferRequestContexts.get(0).getIsStaged()) {
                    List<V3TransferRequestLineItemContext> itemTypesList = new ArrayList<>();
                    List<V3TransferRequestLineItemContext> toolTypesList = new ArrayList<>();
                    Long storeroomId = transferRequestContexts.get(0).getTransferFromStore().getId();
                    context.put(FacilioConstants.ContextNames.STORE_ROOM_ID,storeroomId);
                    List<V3TransferRequestLineItemContext> transferrequestlineitems = transferRequestContexts.get(0).getTransferrequestlineitems();
                   for(V3TransferRequestLineItemContext lineItem : transferrequestlineitems){
                           Integer inventoryType = lineItem.getInventoryType();
                           quantityTransferred = lineItem.getQuantity();
                           double quantity = 0;
                           double currentQuantity =0;
                           if(inventoryType.equals(V3TransferRequestLineItemContext.InventoryType.ITEM.getIndex())){
                               V3ItemTypesContext itemType=lineItem.getItemType();
                               itemTypesList.add(lineItem);
                               if(itemType!=null) {
                                  itemTypeId = itemType.getId();
                                   V3ItemContext item = V3ItemsApi.getItemsForTypeAndStore(storeroomId, itemTypeId);
                                   itemId = item.getId();
                                   quantity = item.getQuantity();
                                   currentQuantity = item.getCurrentQuantity();
                                   if(!transferRequestContexts.get(0).getIsCompleted() && !transferRequestContexts.get(0).getIsShipped()){
                                      if(quantity<quantityTransferred){
                                          throw new IllegalArgumentException("Quantity transferred is more than available quantity");
                                      }
                                      else{
                                          ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                                          String itemModuleName = FacilioConstants.ContextNames.ITEM;
                                          FacilioModule module = modBean.getModule(itemModuleName);
                                          List<FacilioField> fields = modBean.getAllFields(itemModuleName);
                                          List<FacilioField> updatedFields = new ArrayList<>();
                                          Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
                                          updatedFields.add(fieldsMap.get("quantity"));
                                          updatedFields.add(fieldsMap.get("currentQuantity"));
                                          double newQuantity =quantity-quantityTransferred;
                                          double newCurrentQuantity = currentQuantity - quantityTransferred;

                                          Map<String, Object> map = new HashMap<>();
                                          map.put("quantity", newQuantity);
                                          map.put("currentQuantity", newCurrentQuantity);
                                          //update total quantity in fromStore in Item Table
                                          UpdateRecordBuilder<V3ItemContext> updateBuilder = new UpdateRecordBuilder<V3ItemContext>()
                                                  .module(module).fields(updatedFields)
                                                  .andCondition(CriteriaAPI.getIdCondition(itemId, module));
                                          updateBuilder.updateViaMap(map);

                                      }
                                  }
                                   context.put(FacilioConstants.ContextNames.ITEM_TYPES,itemTypesList);
                               }
                           }
                           else if(inventoryType.equals(V3TransferRequestLineItemContext.InventoryType.TOOL.getIndex())){
                               V3ToolTypesContext toolType=lineItem.getToolType();
                               toolTypesList.add(lineItem);
                               if(toolType!=null){
                                   toolTypeId = toolType.getId();
                                   V3ToolContext tool = V3ToolsApi.getToolsForTypeAndStore(storeroomId, toolTypeId);
                                   toolId = tool.getId();
                                   currentQuantity = tool.getCurrentQuantity();
                                   if(!transferRequestContexts.get(0).getIsCompleted() && !transferRequestContexts.get(0).getIsShipped()) {
                                       if (currentQuantity < quantityTransferred) {
                                           throw new IllegalArgumentException("Quantity transferred is more than available quantity");
                                       } else {
                                           ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                                           String toolModuleName = FacilioConstants.ContextNames.TOOL;
                                           FacilioModule module = modBean.getModule(toolModuleName);
                                           List<FacilioField> fields = modBean.getAllFields(toolModuleName);
                                           List<FacilioField> updatedFields = new ArrayList<>();
                                           Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
                                           updatedFields.add(fieldsMap.get("currentQuantity"));

                                           double newQuantity = currentQuantity - quantityTransferred;

                                           Map<String, Object> map = new HashMap<>();
                                           map.put("currentQuantity", newQuantity);

                                           //update total quantity in fromStore in Tool Table
                                           UpdateRecordBuilder<V3ToolContext> updateBuilder = new UpdateRecordBuilder<V3ToolContext>()
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
        return false;
    }
}
