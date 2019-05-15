package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryRequestContext;
import com.facilio.bmsconsole.context.InventoryRequestLineItemContext;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.DeleteRecordBuilder;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.ItemsApi;
import com.facilio.bmsconsole.util.ToolsApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddOrUpdateInventoryRequestCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		InventoryRequestContext inventoryRequestContext = (InventoryRequestContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (inventoryRequestContext != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			
			FacilioModule lineModule = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);
			
			if (CollectionUtils.isEmpty(inventoryRequestContext.getLineItems())) {
				throw new Exception("Line items cannot be empty");
			}
			// setting current user to requestedBy
			if(inventoryRequestContext.getRequestedBy() == null) {
		 	  inventoryRequestContext.setRequestedBy(AccountUtil.getCurrentUser());
			}
			if (inventoryRequestContext.getId() > 0) {
				updateRecord(inventoryRequestContext, module, fields);
				
				DeleteRecordBuilder<InventoryRequestLineItemContext> deleteBuilder = new DeleteRecordBuilder<InventoryRequestLineItemContext>()
						.module(lineModule)
						.andCondition(CriteriaAPI.getCondition("INVENTORY_REQUEST_ID", "inventoryRequestId", String.valueOf(inventoryRequestContext.getId()), NumberOperators.EQUALS));
				deleteBuilder.delete();
			} else {
				if(inventoryRequestContext.getRequestedTime() == -1) {
					inventoryRequestContext.setRequestedTime(System.currentTimeMillis());
				}
				
				inventoryRequestContext.setStatus(InventoryRequestContext.Status.REQUESTED);
				addRecord(true, Collections.singletonList(inventoryRequestContext), module, fields);
			}
			
			updateLineItems(inventoryRequestContext);
			addRecord(false, inventoryRequestContext.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
			
			context.put(FacilioConstants.ContextNames.RECORD, inventoryRequestContext);
		}
		return false;
	}
	
	
	private void updateLineItems(InventoryRequestContext inventoryRequestContext) throws Exception{
		for (InventoryRequestLineItemContext lineItemContext : inventoryRequestContext.getLineItems()) {
			lineItemContext.setInventoryRequestId(inventoryRequestContext.getId());
			if(inventoryRequestContext.getParentId() > 0) {
				lineItemContext.setParentId(inventoryRequestContext.getParentId());
			}
			if(inventoryRequestContext.getStoreRoom() != null &&  inventoryRequestContext.getStoreRoom().getId() > 0) {
				lineItemContext.setStoreRoomId(inventoryRequestContext.getStoreRoom().getId());
			}
			if(lineItemContext.getAssetIds() != null && lineItemContext.getAssetIds().size() > 0) {
				lineItemContext.setAsset(lineItemContext.getAssetIds().get(0));
				for(int i=1; i<=lineItemContext.getAssetIds().size(); i++) {
					 InventoryRequestLineItemContext lineItem = new InventoryRequestLineItemContext();
					 lineItem.setAsset(lineItemContext.getAssetIds().get(i));
					 lineItem.setInventoryType(lineItemContext.getInventoryType());
					 if(lineItemContext.getInventoryType() == InventoryType.ITEM.getValue()) {
						 lineItem.setItemType(lineItemContext.getItemType());
					 }
					 else if(lineItemContext.getInventoryType() == InventoryType.TOOL.getValue()) {
						 lineItem.setToolType(lineItemContext.getToolType());
					 }
					 
					 lineItem.setQuantity(lineItemContext.getQuantity());
					 inventoryRequestContext.getLineItems().add(lineItem); 
				}
			}
			
		}
	}
	
	private void addRecord(boolean isLocalIdNeeded, List<? extends ModuleBaseWithCustomFields> list, FacilioModule module, List<FacilioField> fields) throws Exception {
		InsertRecordBuilder insertRecordBuilder = new InsertRecordBuilder<>()
				.module(module)
				.fields(fields);
		if(isLocalIdNeeded) {
			insertRecordBuilder.withLocalId();
		}
		insertRecordBuilder.addRecords(list);
		insertRecordBuilder.save();
	}
	
	public void updateRecord(ModuleBaseWithCustomFields data, FacilioModule module, List<FacilioField> fields) throws Exception {
		UpdateRecordBuilder updateRecordBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
				.module(module)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(data.getId(), module));
		updateRecordBuilder.update(data);
	}
	
	
}
